package com.hsmy.websocket;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理敲击功能的实时 WebSocket 会话。
 */
@Slf4j
@Component
public class KnockWebSocketSessionManager {

    private final Map<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    public void register(Long userId, WebSocketSession session) {
        userSessions.computeIfAbsent(userId, id -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                .add(session);
        log.debug("Registered websocket session {} for user {}", session.getId(), userId);
    }

    public void remove(Long userId, WebSocketSession session) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
        log.debug("Removed websocket session {} for user {}", session.getId(), userId);
    }

    public void sendToUser(Long userId, String event, Object payload) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        String message = buildMessage(event, payload);
        sessions.forEach(session -> send(session, message));
    }

    public void broadcast(String event, Object payload) {
        String message = buildMessage(event, payload);
        userSessions.values().forEach(sessions -> sessions.forEach(session -> send(session, message)));
    }

    private String buildMessage(String event, Object payload) {
        return JSON.toJSONString(new WebSocketEnvelope(event, payload));
    }

    private void send(WebSocketSession session, String message) {
        if (!session.isOpen()) {
            return;
        }
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException ex) {
            log.warn("Failed to send websocket message, session {}", session.getId(), ex);
        }
    }

    private static class WebSocketEnvelope {
        private final String event;
        private final Object data;

        private WebSocketEnvelope(String event, Object data) {
            this.event = event;
            this.data = data;
        }

        public String getEvent() {
            return event;
        }

        public Object getData() {
            return data;
        }
    }
}
