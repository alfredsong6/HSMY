package com.hsmy.service.wechat.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsmy.config.WechatMiniProperties;
import com.hsmy.exception.BusinessException;
import com.hsmy.service.wechat.WechatMiniAuthService;
import com.hsmy.service.wechat.dto.WechatPhoneInfo;
import com.hsmy.service.wechat.dto.WechatSessionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 微信小程序认证实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatMiniAuthServiceImpl implements WechatMiniAuthService {

    private static final String ACCESS_TOKEN_CACHE_KEY_PREFIX = "hsmy:wechat:mini:access_token:";

    private final WechatMiniProperties properties;
    private final StringRedisTemplate stringRedisTemplate;
    private final RestTemplateBuilder restTemplateBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WechatSessionInfo code2Session(String appId, String jsCode) {
        String resolvedAppId = StringUtils.hasText(appId) ? appId : properties.getAppId();
        if (!StringUtils.hasText(resolvedAppId) || !StringUtils.hasText(properties.getSecret())) {
            throw new BusinessException("微信小程序配置缺失");
        }

        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.weixin.qq.com/sns/jscode2session")
                .queryParam("appid", resolvedAppId)
                .queryParam("secret", properties.getSecret())
                .queryParam("js_code", jsCode)
                .queryParam("grant_type", "authorization_code")
                .toUriString();

        String body = getRestTemplate().getForObject(url, String.class);
        JsonNode resp = parseJson(body, "jscode2session");
        checkWechatError(resp, "jscode2session");

        WechatSessionInfo info = new WechatSessionInfo();
        info.setOpenId(resp.path("openid").asText(null));
        info.setUnionId(resp.path("unionid").asText(null));
        info.setSessionKey(resp.path("session_key").asText(null));

        if (!StringUtils.hasText(info.getOpenId()) || !StringUtils.hasText(info.getSessionKey())) {
            throw new BusinessException("获取微信登录态失败");
        }
        return info;
    }

    @Override
    public WechatPhoneInfo getPhoneNumber(String phoneCode, String sessionKey) {
        if (!StringUtils.hasText(sessionKey)) {
            throw new BusinessException("sessionKey 缺失，请先调用 code2Session");
        }
        if (!StringUtils.hasText(phoneCode)) {
            throw new BusinessException("phoneCode 不能为空");
        }

        String accessToken = getAccessToken();
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.weixin.qq.com/wxa/business/getuserphonenumber")
                .queryParam("access_token", accessToken)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"code\":\"" + phoneCode + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> respEntity = getRestTemplate().postForEntity(url, entity, String.class);
        JsonNode resp = parseJson(respEntity.getBody(), "getuserphonenumber");
        checkWechatError(resp, "getuserphonenumber");

        JsonNode phoneInfoNode = resp.path("phone_info");
        WechatPhoneInfo info = new WechatPhoneInfo();
        info.setPhoneNumber(phoneInfoNode.path("phoneNumber").asText(null));
        info.setPurePhoneNumber(phoneInfoNode.path("purePhoneNumber").asText(null));

        if (!StringUtils.hasText(info.getPhoneNumber()) && !StringUtils.hasText(info.getPurePhoneNumber())) {
            throw new BusinessException("获取手机号失败");
        }
        return info;
    }

    @Override
    public String getDefaultAppId() {
        return properties.getAppId();
    }

    @Override
    public String getAccessToken() {
        String appId = properties.getAppId();
        String secret = properties.getSecret();
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(secret)) {
            throw new BusinessException("微信小程序配置缺失");
        }

        String cacheKey = ACCESS_TOKEN_CACHE_KEY_PREFIX + appId;
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(cached)) {
            return cached;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("grant_type", "client_credential");
        requestBody.put("appid", appId);
        requestBody.put("secret", secret);
        requestBody.put("force_refresh", false);

        ResponseEntity<String> response = getRestTemplate().postForEntity(
                "https://api.weixin.qq.com/cgi-bin/stable_token",
                new HttpEntity<>(requestBody, headers),
                String.class
        );
        JsonNode resp = parseJson(response.getBody(), "getStableAccessToken");
        checkWechatError(resp, "getStableAccessToken");

        String token = resp.path("access_token").asText(null);
        int expiresIn = resp.path("expires_in").asInt(7000);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("获取微信 access_token 失败");
        }

        long cacheSeconds = Math.max(expiresIn - 200L, 60L);
        stringRedisTemplate.opsForValue().set(cacheKey, token, cacheSeconds, TimeUnit.SECONDS);
        return token;
    }

    private void checkWechatError(JsonNode resp, String api) {
        if (resp == null) {
            throw new BusinessException("微信接口异常: " + api);
        }

        int errCode = resp.path("errcode").asInt(0);
        if (errCode != 0) {
            String errMsg = resp.path("errmsg").asText("unknown error");
            log.warn("微信接口返回错误 api={}, errCode={}, errMsg={}", api, errCode, errMsg);
            throw new BusinessException("微信接口错误: " + errMsg);
        }
    }

    private RestTemplate getRestTemplate() {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    private JsonNode parseJson(String body, String api) {
        try {
            return objectMapper.readTree(body);
        } catch (Exception e) {
            log.error("解析微信响应失败, api={}, body={}", api, body, e);
            throw new BusinessException("微信接口响应解析失败: " + api);
        }
    }
}
