package com.hsmy.controller.wechat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsmy.config.WechatMiniProperties;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wechat")
public class WechatUrlSchemeController {

    private static final long ALLOW_TIME_DIFF_MILLIS = 5000L;

    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/stable_token";

    private static final String GENERATE_SCHEME_URL =
            "https://api.weixin.qq.com/wxa/generatescheme?access_token={accessToken}";


    private final WechatMiniProperties wechatProperties;

    /**
     * 是否生成带失效时间的 scheme
     */
    @Value("${wechat.mini-app.scheme-expire-seconds:86400}")
    private long schemeExpireSeconds;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WechatUrlSchemeController(WechatMiniProperties wechatProperties) {
        this.wechatProperties = wechatProperties;
    }

    @PostMapping(
            value = "/get-scheme",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public SchemeResponse getScheme(@RequestBody WechatSchemeRequest req) {
        try {
            // 1. 基础参数校验
            String validateMsg = validateRequest(req);
            if (validateMsg != null) {
                return SchemeResponse.fail(1000, validateMsg);
            }

            // 2. 校验时间戳，不能超过 5 秒
            long now = System.currentTimeMillis();
            if (Math.abs(now - req.getCurrentTime()) > ALLOW_TIME_DIFF_MILLIS) {
                return SchemeResponse.fail(1001, "time expire");
            }

            // 3. 校验 originId
            if (!wechatProperties.getAppId().equals(req.getOriginId())) {
                return SchemeResponse.fail(1002, "origin error");
            }

            // 4. 校验 sign
            String signRaw = "currentTime=" + req.getCurrentTime() + "&originId=" + req.getOriginId();
            String localSign = DigestUtils.md5Hex(signRaw.toLowerCase());
            if (!localSign.equalsIgnoreCase(req.getSign())) {
                return SchemeResponse.fail(1003, "sign error");
            }

            // 5. 获取 access_token
            String accessToken = getAccessToken();
            if (!StringUtils.hasText(accessToken)) {
                return SchemeResponse.fail(1004, "get access_token failed");
            }

            // 6. 调用微信 generatescheme
            String scheme = generateScheme(accessToken, req.getPath(), req.getQuery());
            if (!StringUtils.hasText(scheme)) {
                return SchemeResponse.fail(1005, "generate scheme failed");
            }

            // 7. 返回平台要求格式
            SchemeResponse.Result result = new SchemeResponse.Result();
            result.setScheme(scheme);

            SchemeResponse resp = new SchemeResponse();
            resp.setCode(0);
            resp.setMsg("success");
            resp.setResult(result);
            return resp;

        } catch (Exception e) {
            return SchemeResponse.fail(9999, "system error: " + e.getMessage());
        }
    }

    private String validateRequest(WechatSchemeRequest req) {
        if (req == null) {
            return "request body is null";
        }
        if (!StringUtils.hasText(req.getOriginId())) {
            return "originId is required";
        }
        if (req.getCurrentTime() == null) {
            return "currentTime is required";
        }
        if (!StringUtils.hasText(req.getSign())) {
            return "sign is required";
        }
        if (!StringUtils.hasText(req.getPath())) {
            return "path is required";
        }
        // 按你上传的平台文档，query 也是必传；如果没有参数，传空字符串即可
        if (req.getQuery() == null) {
            return "query is required";
        }
        return null;
    }

    private String getAccessToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("grant_type", "client_credential");
        body.put("appid", wechatProperties.getAppId());
        body.put("secret", wechatProperties.getSecret());
        body.put("force_refresh", false);

        ResponseEntity<String> response = restTemplate.postForEntity(
                ACCESS_TOKEN_URL,
                new HttpEntity<Map<String, Object>>(body, headers),
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return null;
        }

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        if (jsonNode.has("access_token")) {
            return jsonNode.get("access_token").asText();
        }
        return null;
    }

    private String generateScheme(String accessToken, String path, String query) throws Exception {
        Map<String, Object> body = new HashMap<String, Object>();

        Map<String, Object> jumpWxa = new HashMap<String, Object>();
        jumpWxa.put("path", path);
        jumpWxa.put("query", query);
        // 可选：develop / trial / release
        jumpWxa.put("env_version", "release");

        body.put("jump_wxa", jumpWxa);

        // 兼容常见生成方式：带过期时间
        body.put("is_expire", true);
        body.put("expire_time", (System.currentTimeMillis() / 1000L) + schemeExpireSeconds);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                GENERATE_SCHEME_URL,
                HttpMethod.POST,
                entity,
                String.class,
                accessToken
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return null;
        }

        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        // 微信成功时一般 errcode = 0，且返回 openlink
        if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() == 0 && jsonNode.has("openlink")) {
            return jsonNode.get("openlink").asText();
        }

        return null;
    }

    // ==================== DTO ====================

    public static class WechatSchemeRequest {
        private String path;
        private String query;
        private String originId;
        private Long currentTime;
        private String sign;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getOriginId() {
            return originId;
        }

        public void setOriginId(String originId) {
            this.originId = originId;
        }

        public Long getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(Long currentTime) {
            this.currentTime = currentTime;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SchemeResponse {
        private Integer code;
        private String msg;
        private Result result;

        public static SchemeResponse fail(Integer code, String msg) {
            SchemeResponse resp = new SchemeResponse();
            resp.setCode(code);
            resp.setMsg(msg);
            return resp;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public static class Result {
            private String scheme;

            public String getScheme() {
                return scheme;
            }

            public void setScheme(String scheme) {
                this.scheme = scheme;
            }
        }
    }
}
