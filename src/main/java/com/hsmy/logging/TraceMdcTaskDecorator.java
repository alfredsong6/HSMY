package com.hsmy.logging;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.HashMap;
import java.util.Map;

public class TraceMdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                if (contextMap != null && !contextMap.isEmpty()) {
                    MDC.setContextMap(new HashMap<>(contextMap));
                } else {
                    MDC.clear();
                }
                if (MDC.get(TraceIdUtils.TRACE_ID_KEY) == null) {
                    MDC.put(TraceIdUtils.TRACE_ID_KEY, TraceIdUtils.newTraceId());
                }
                runnable.run();
            } finally {
                if (previous != null && !previous.isEmpty()) {
                    MDC.setContextMap(previous);
                } else {
                    MDC.clear();
                }
            }
        };
    }
}
