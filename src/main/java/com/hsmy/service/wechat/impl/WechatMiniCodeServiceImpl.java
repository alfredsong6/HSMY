package com.hsmy.service.wechat.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsmy.exception.BusinessException;
import com.hsmy.service.wechat.WechatMiniAuthService;
import com.hsmy.service.wechat.WechatMiniCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序码服务实现.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatMiniCodeServiceImpl implements WechatMiniCodeService {

    private final WechatMiniAuthService wechatMiniAuthService;
    private final RestTemplateBuilder restTemplateBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] getUnlimitedCode(String page, String scene, Integer width) {
        if (!StringUtils.hasText(page)) {
            throw new BusinessException("小程序 page 不能为空");
        }
        if (!StringUtils.hasText(scene)) {
            throw new BusinessException("小程序 scene 不能为空");
        }
        String accessToken = wechatMiniAuthService.getAccessToken();
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.weixin.qq.com/wxa/getwxacodeunlimit")
                .queryParam("access_token", accessToken)
                .toUriString();

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("page", page);
        bodyMap.put("scene", scene);
        if (width != null && width > 0) {
            bodyMap.put("width", String.valueOf(width));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(bodyMap, headers);

        ResponseEntity<byte[]> response = getRestTemplate().exchange(url, HttpMethod.POST, entity, byte[].class);
        MediaType contentType = response.getHeaders().getContentType();
        byte[] data = response.getBody();

        if (data == null || data.length == 0) {
            throw new BusinessException("微信返回空的二维码数据");
        }

        // 如果返回 JSON，可能是错误信息
        if (contentType != null && contentType.includes(MediaType.APPLICATION_JSON)) {
            handleJsonError(data);
        }

        // 某些情况下 Content-Type 可能缺失，尝试解析 JSON 判断
        handlePossibleJsonError(data);

        return data;
    }

    private void handleJsonError(byte[] data) {
        try {
            JsonNode node = objectMapper.readTree(data);
            int errCode = node.path("errcode").asInt(0);
            if (errCode != 0) {
                String errMsg = node.path("errmsg").asText("unknown error");
                throw new BusinessException("获取小程序码失败: " + errMsg);
            }
        } catch (Exception ignore) {
            // ignore parsing failures here
        }
    }

    private void handlePossibleJsonError(byte[] data) {
        try {
            String text = new String(data, StandardCharsets.UTF_8);
            if (text.startsWith("{")) {
                JsonNode node = objectMapper.readTree(text);
                int errCode = node.path("errcode").asInt(0);
                if (errCode != 0) {
                    String errMsg = node.path("errmsg").asText("unknown error");
                    throw new BusinessException("获取小程序码失败: " + errMsg);
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception ignore) {
            // treat as binary
        }
    }

    private RestTemplate getRestTemplate() {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }
}
