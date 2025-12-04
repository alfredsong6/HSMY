package com.hsmy.config;

import com.hsmy.websocket.KnockWebSocketHandler;
import com.hsmy.websocket.KnockWebSocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置，注册敲击实时通道。
 */
//@Configuration
//@EnableWebSocket
@RequiredArgsConstructor
public class KnockWebSocketConfig implements WebSocketConfigurer {

    private final KnockWebSocketHandler knockWebSocketHandler;
    private final KnockWebSocketHandshakeInterceptor handshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(knockWebSocketHandler, "/ws/knock")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
