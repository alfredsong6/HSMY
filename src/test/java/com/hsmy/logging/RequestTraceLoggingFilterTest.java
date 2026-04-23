package com.hsmy.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.hsmy.dto.UserSessionContext;
import com.hsmy.interceptor.LoginInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestTraceLoggingFilterTest {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(RequestTraceLoggingFilter.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
        MDC.clear();
    }

    @Test
    void doFilterInternal_generatesTraceIdAndLogsRequestSummary() throws Exception {
        RequestTraceLoggingFilter filter = new RequestTraceLoggingFilter();
        MockHttpServletRequest request = buildJsonRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> traceIdSeenInChain = new AtomicReference<>();

        FilterChain chain = (req, res) -> {
            traceIdSeenInChain.set(MDC.get(TraceIdUtils.TRACE_ID_KEY));
            consumeBody((HttpServletRequest) req);

            UserSessionContext sessionContext = new UserSessionContext();
            sessionContext.setUsername("tester");
            req.setAttribute(LoginInterceptor.USER_ID_ATTRIBUTE, 123L);
            req.setAttribute(LoginInterceptor.USER_SESSION_CONTEXT_ATTRIBUTE, sessionContext);
            ((MockHttpServletResponse) res).setStatus(201);
        };

        filter.doFilter(request, response, chain);

        String traceId = response.getHeader(TRACE_ID_HEADER);
        assertNotNull(traceId);
        assertFalse(traceId.isEmpty());
        assertEquals(traceId, traceIdSeenInChain.get());
        assertNull(MDC.get(TraceIdUtils.TRACE_ID_KEY));

        assertEquals(1, listAppender.list.size());
        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(Level.INFO, event.getLevel());
        assertEquals(traceId, event.getMDCPropertyMap().get(TraceIdUtils.TRACE_ID_KEY));
        assertTrue(event.getFormattedMessage().contains("request summary"));
        assertTrue(event.getFormattedMessage().contains("method=POST"));
        assertTrue(event.getFormattedMessage().contains("uri=/api/demo"));
        assertTrue(event.getFormattedMessage().contains("clientIp=10.0.0.8"));
        assertTrue(event.getFormattedMessage().contains("status=201"));
        assertTrue(event.getFormattedMessage().contains("userId=123"));
        assertTrue(event.getFormattedMessage().contains("username=tester"));
        assertTrue(event.getFormattedMessage().contains("alpha=1"));
        assertTrue(event.getFormattedMessage().contains("note=hello"));
    }

    @Test
    void doFilterInternal_masksSensitiveFieldsInParamDigest() throws Exception {
        RequestTraceLoggingFilter filter = new RequestTraceLoggingFilter();
        MockHttpServletRequest request = buildJsonRequest();
        request.addHeader(TRACE_ID_HEADER, "trace-from-client");
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> consumeBody((HttpServletRequest) req);

        filter.doFilter(request, response, chain);

        assertEquals("trace-from-client", response.getHeader(TRACE_ID_HEADER));
        assertEquals(1, listAppender.list.size());
        String message = listAppender.list.get(0).getFormattedMessage();
        assertTrue(message.contains("token=***"));
        assertTrue(message.contains("password=***"));
        assertTrue(message.contains("phone=***"));
        assertFalse(message.contains("secret-token"));
        assertFalse(message.contains("123456"));
        assertFalse(message.contains("13800138000"));
    }

    private MockHttpServletRequest buildJsonRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/demo");
        request.setContentType("application/json");
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setRemoteAddr("10.0.0.8");
        request.setContent("{\"note\":\"hello\",\"password\":\"123456\",\"phone\":\"13800138000\"}"
                .getBytes(StandardCharsets.UTF_8));
        request.addParameter("alpha", "1");
        request.addParameter("token", "secret-token");
        return request;
    }

    private void consumeBody(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        byte[] buffer = new byte[256];
        while (inputStream.read(buffer) != -1) {
            // consume
        }
    }
}
