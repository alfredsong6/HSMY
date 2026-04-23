package com.hsmy.logging;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hsmy.dto.UserSessionContext;
import com.hsmy.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestTraceLoggingFilter extends OncePerRequestFilter {

    private static final int PARAM_DIGEST_MAX_LENGTH = 512;
    private static final List<String> SENSITIVE_KEYS = Arrays.asList(
            "authorization", "token", "password", "sessionid", "session_id", "code", "captcha", "phone"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest requestToUse = shouldCacheBody(request)
                ? new CachedBodyHttpServletRequest(request)
                : request;
        long startTime = System.currentTimeMillis();
        String traceId = TraceIdUtils.getOrCreateTraceId(request.getHeader(TraceIdUtils.TRACE_ID_HEADER));
        MDC.put(TraceIdUtils.TRACE_ID_KEY, traceId);
        response.setHeader(TraceIdUtils.TRACE_ID_HEADER, traceId);

        try {
            filterChain.doFilter(requestToUse, response);
        } finally {
            log.info("request summary method={} uri={} clientIp={} status={} durationMs={} userId={} username={} paramDigest={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    resolveClientIp(request),
                    response.getStatus(),
                    System.currentTimeMillis() - startTime,
                    resolveUserId(request),
                    resolveUsername(request),
                    buildParamDigest(requestToUse));
            MDC.remove(TraceIdUtils.TRACE_ID_KEY);
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (hasText(forwardedFor)) {
            int separatorIndex = forwardedFor.indexOf(',');
            return separatorIndex >= 0 ? forwardedFor.substring(0, separatorIndex).trim() : forwardedFor.trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (hasText(realIp)) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    private Object resolveUserId(HttpServletRequest request) {
        Object userId = request.getAttribute(LoginInterceptor.USER_ID_ATTRIBUTE);
        return userId != null ? userId : "anonymous";
    }

    private String resolveUsername(HttpServletRequest request) {
        Object context = request.getAttribute(LoginInterceptor.USER_SESSION_CONTEXT_ATTRIBUTE);
        if (context instanceof UserSessionContext) {
            String username = ((UserSessionContext) context).getUsername();
            if (hasText(username)) {
                return username;
            }
        }
        return "anonymous";
    }

    private String buildParamDigest(HttpServletRequest request) {
        List<String> parts = new ArrayList<>();
        appendParameterMap(parts, request);
        if (request instanceof CachedBodyHttpServletRequest) {
            appendBody(parts, (CachedBodyHttpServletRequest) request);
        }
        if (parts.isEmpty()) {
            return "<empty>";
        }
        return truncate(String.join("; ", parts));
    }

    private void appendParameterMap(List<String> parts, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap == null || parameterMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values == null || values.length == 0) {
                parts.add(key + "=");
                continue;
            }
            for (String value : values) {
                parts.add(key + "=" + maskIfNecessary(key, value));
            }
        }
    }

    private void appendBody(List<String> parts, CachedBodyHttpServletRequest request) {
        byte[] body = request.getCachedBody();
        if (body == null || body.length == 0 || !isTextRequest(request)) {
            return;
        }

        Charset charset = StandardCharsets.UTF_8;
        if (hasText(request.getCharacterEncoding())) {
            charset = Charset.forName(request.getCharacterEncoding());
        }
        String bodyText = new String(body, charset).trim();
        if (!hasText(bodyText)) {
            return;
        }

        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase(Locale.ENGLISH).contains("application/json")) {
            try {
                Object parsed = JSON.parse(bodyText);
                if (parsed instanceof JSONObject) {
                    Map<String, Object> flattened = new LinkedHashMap<>(((JSONObject) parsed).getInnerMap());
                    for (Map.Entry<String, Object> entry : flattened.entrySet()) {
                        parts.add(entry.getKey() + "=" + maskIfNecessary(entry.getKey(), stringify(entry.getValue())));
                    }
                    return;
                }
            } catch (Exception ignored) {
                // keep raw body fallback
            }
        }

        parts.add("body=" + truncate(bodyText));
    }

    private boolean isTextRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (!hasText(contentType)) {
            return false;
        }
        String normalized = contentType.toLowerCase(Locale.ENGLISH);
        return normalized.contains("json")
                || normalized.contains("xml")
                || normalized.contains("text")
                || normalized.contains("form");
    }

    private boolean shouldCacheBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (!hasText(contentType)) {
            return false;
        }
        String normalized = contentType.toLowerCase(Locale.ENGLISH);
        return !normalized.contains("multipart/")
                && !normalized.contains("octet-stream")
                && isTextRequest(request);
    }

    private String maskIfNecessary(String key, String value) {
        if (value == null) {
            return "";
        }
        String normalizedKey = key == null ? "" : key.toLowerCase(Locale.ENGLISH);
        for (String sensitiveKey : SENSITIVE_KEYS) {
            if (normalizedKey.contains(sensitiveKey)) {
                return "***";
            }
        }
        return truncate(value);
    }

    private String stringify(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Iterable || value.getClass().isArray()) {
            return JSON.toJSONString(value);
        }
        return String.valueOf(value);
    }

    private String truncate(String value) {
        if (value == null) {
            return "";
        }
        if (value.length() <= PARAM_DIGEST_MAX_LENGTH) {
            return value;
        }
        return value.substring(0, PARAM_DIGEST_MAX_LENGTH) + "...";
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
