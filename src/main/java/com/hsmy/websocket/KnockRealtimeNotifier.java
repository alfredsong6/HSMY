package com.hsmy.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 将敲击业务事件推送到实时 WebSocket 通道。
 */
@Component
@RequiredArgsConstructor
public class KnockRealtimeNotifier {

    private final KnockWebSocketSessionManager sessionManager;

    public void notifyManualKnock(Long userId, Map<String, Object> payload) {
        sessionManager.sendToUser(userId, "manualKnock", enrich(payload));
    }

    public void notifyAutoStart(Long userId, Map<String, Object> payload) {
        sessionManager.sendToUser(userId, "autoKnockStarted", enrich(payload));
    }

    public void notifyAutoStop(Long userId, Map<String, Object> payload) {
        sessionManager.sendToUser(userId, "autoKnockStopped", enrich(payload));
    }

    public void notifyAutoTimeout(Long userId, Map<String, Object> payload) {
        sessionManager.sendToUser(userId, "autoKnockTimeout", enrich(payload));
    }

    public void notifyHeartbeat(Long userId, Map<String, Object> payload) {
        sessionManager.sendToUser(userId, "autoKnockHeartbeat", enrich(payload));
    }

    private Map<String, Object> enrich(Map<String, Object> payload) {
        Map<String, Object> data = new HashMap<>(payload != null ? payload : new HashMap<>());
        data.put("timestamp", System.currentTimeMillis());
        return data;
    }
}
