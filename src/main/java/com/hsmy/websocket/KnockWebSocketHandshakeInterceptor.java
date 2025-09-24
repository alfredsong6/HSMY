package com.hsmy.websocket;

import com.hsmy.dto.UserSessionContext;
import com.hsmy.interceptor.LoginInterceptor;
import com.hsmy.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * WebSocket 握手拦截器，复用登录校验逻辑，确保只有登录用户可以建立实时连接。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KnockWebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final SessionService sessionService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest)) {
            log.warn("Unsupported websocket request type: {}", request.getClass());
            return false;
        }
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String token = extractToken(servletRequest);
        if (!StringUtils.hasText(token)) {
            log.warn("Websocket handshake missing token, uri={}", servletRequest.getRequestURI());
            return false;
        }
        UserSessionContext sessionContext = sessionService.getUserSessionContext(token);
        if (sessionContext == null || sessionContext.getStatus() == null || sessionContext.getStatus() != 1) {
            log.warn("Websocket handshake token invalid, uri={}, token={}", servletRequest.getRequestURI(), token);
            return false;
        }
        attributes.put(LoginInterceptor.USER_ID_ATTRIBUTE, sessionContext.getUserId());
        attributes.put(LoginInterceptor.USER_SESSION_CONTEXT_ATTRIBUTE, sessionContext);
        log.debug("Websocket handshake success, userId={}, uri={}", sessionContext.getUserId(), servletRequest.getRequestURI());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }

    private String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization)) {
            if (authorization.startsWith("Bearer ")) {
                return authorization.substring(7);
            }
            return authorization;
        }
        String sessionId = request.getHeader("X-Session-Id");
        if (!StringUtils.hasText(sessionId)) {
            sessionId = request.getParameter("sessionId");
        }
        if (!StringUtils.hasText(sessionId)) {
            sessionId = request.getParameter("token");
        }
        return sessionId;
    }
}
