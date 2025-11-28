package com.hsmy.websocket;

import com.hsmy.dto.UserSessionContext;
import com.hsmy.service.KnockService;
import com.hsmy.service.SessionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class KnockWebSocketHandlerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private KnockService knockService;

    @Test
    void websocketShouldHandlePingAndKnock() throws Exception {
        String token = "test-token";
        UserSessionContext sessionContext = new UserSessionContext();
        sessionContext.setUserId(123L);
        sessionContext.setStatus(1);
        when(sessionService.getUserSessionContext(token)).thenReturn(sessionContext);
        when(knockService.manualKnock(ArgumentMatchers.any())).thenReturn(Collections.singletonMap("ok", true));

        CountDownLatch latch = new CountDownLatch(3);
        List<String> received = new ArrayList<>();
        AtomicReference<WebSocketSession> sessionRef = new AtomicReference<>();

        WebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        client.doHandshake(new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                sessionRef.set(session);
                session.sendMessage(new TextMessage("{\"event\":\"ping\"}"));
                session.sendMessage(new TextMessage("{\"event\":\"knock\",\"payload\":{\"knockCount\":1}}"));
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                received.add(message.getPayload());
                latch.countDown();
            }
        }, headers, new URI("ws://localhost:" + port + "/ws/knock")).get();

        assertTrue(latch.await(5, TimeUnit.SECONDS), "did not receive expected websocket messages");
        assertTrue(received.stream().anyMatch(m -> m.contains("\"pong\"")), "missing pong ack");
        assertTrue(received.stream().anyMatch(m -> m.contains("\"knockAck\"")), "missing knock ack");

        verify(knockService).manualKnock(ArgumentMatchers.any());

        WebSocketSession session = sessionRef.get();
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}
