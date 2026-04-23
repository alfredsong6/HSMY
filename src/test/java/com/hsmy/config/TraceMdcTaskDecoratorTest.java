package com.hsmy.config;

import com.hsmy.logging.TraceIdUtils;
import com.hsmy.logging.TraceMdcTaskDecorator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TraceMdcTaskDecoratorTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void decorate_propagatesParentTraceId() {
        TaskDecorator decorator = new TraceMdcTaskDecorator();
        AtomicReference<String> traceIdInTask = new AtomicReference<>();

        MDC.put(TraceIdUtils.TRACE_ID_KEY, "trace-parent");
        Runnable decorated = decorator.decorate(() -> traceIdInTask.set(MDC.get(TraceIdUtils.TRACE_ID_KEY)));
        MDC.clear();

        decorated.run();

        assertEquals("trace-parent", traceIdInTask.get());
        assertNull(MDC.get(TraceIdUtils.TRACE_ID_KEY));
    }

    @Test
    void decorate_generatesTraceIdWhenParentContextMissing() {
        TaskDecorator decorator = new TraceMdcTaskDecorator();
        AtomicReference<String> traceIdInTask = new AtomicReference<>();

        Runnable decorated = decorator.decorate(() -> traceIdInTask.set(MDC.get(TraceIdUtils.TRACE_ID_KEY)));
        decorated.run();

        assertNotNull(traceIdInTask.get());
        assertFalse(traceIdInTask.get().isEmpty());
        assertNull(MDC.get(TraceIdUtils.TRACE_ID_KEY));
    }
}
