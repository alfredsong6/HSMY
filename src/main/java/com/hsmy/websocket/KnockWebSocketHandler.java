package com.hsmy.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hsmy.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理敲击功能的 WebSocket 消息，提供心跳与实时通知能力。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KnockWebSocketHandler extends TextWebSocketHandler {

    private final KnockWebSocketSessionManager sessionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = resolveUserId(session);
        if (userId == null) {
            closeSession(session, "unauthorized");
            return;
        }
        sessionManager.register(userId, session);
        send(session, JSON.toJSONString(buildAck("connected")));
        log.debug("Websocket connection established, userId={}, sessionId={}", userId, session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JSONObject payload = JSON.parseObject(message.getPayload());
            String event = payload.getString("event");
            if ("ping".equalsIgnoreCase(event)) {
                send(session, JSON.toJSONString(buildAck("pong")));
                return;
            }
            if ("clientUpdate".equalsIgnoreCase(event)) {
                log.debug("Received client update from session {}: {}", session.getId(), message.getPayload());
                send(session, JSON.toJSONString(buildAck("ack")));
                return;
            }
            log.warn("Unknown websocket event: {} from session {}", event, session.getId());
            send(session, JSON.toJSONString(buildError("unknown_event")));
        } catch (Exception ex) {
            log.warn("Failed to process websocket message, sessionId={}, payload={}", session.getId(), message.getPayload(), ex);
            send(session, JSON.toJSONString(buildError("invalid_payload")));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        Long userId = resolveUserId(session);
        if (userId != null) {
            sessionManager.remove(userId, session);
        }
        log.debug("Websocket connection closed, userId={}, sessionId={}, status={}", userId, session.getId(), status);
    }

    private Long resolveUserId(WebSocketSession session) {
        Object userId = session.getAttributes().get(LoginInterceptor.USER_ID_ATTRIBUTE);
        if (userId instanceof Long) {
            return (Long) userId;
        }
        if (userId != null) {
            try {
                return Long.valueOf(userId.toString());
            } catch (NumberFormatException ignored) {
                log.warn("Failed to parse websocket userId from attributes: {}", userId);
            }
        }
        return null;
    }

    private Map<String, Object> buildAck(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("event", "system");
        map.put("message", message);
        map.put("timestamp", System.currentTimeMillis());
        return map;
    }

    private Map<String, Object> buildError(String reason) {
        Map<String, Object> map = new HashMap<>();
        map.put("event", "error");
        map.put("reason", reason);
        map.put("timestamp", System.currentTimeMillis());
        return map;
    }

    private void send(WebSocketSession session, String payload) {
        if (!session.isOpen()) {
            return;
        }
        try {
            session.sendMessage(new TextMessage(payload));
        } catch (IOException ex) {
            log.warn("Failed to send websocket message, session {}", session.getId(), ex);
        }
    }

    private void closeSession(WebSocketSession session, String reason) {
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException ex) {
            log.warn("Failed to close websocket session {}", session.getId(), ex);
        }
        log.warn("Closed websocket session {} due to {}", session.getId(), reason);
    }
}
