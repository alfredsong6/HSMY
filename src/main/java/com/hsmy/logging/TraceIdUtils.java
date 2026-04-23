package com.hsmy.logging;

import org.slf4j.MDC;

import java.util.UUID;

public final class TraceIdUtils {

    public static final String TRACE_ID_KEY = "traceId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    private TraceIdUtils() {
    }

    public static String currentTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    public static String getOrCreateTraceId(String incomingTraceId) {
        if (hasText(incomingTraceId)) {
            return incomingTraceId.trim();
        }
        String existingTraceId = currentTraceId();
        if (hasText(existingTraceId)) {
            return existingTraceId;
        }
        return newTraceId();
    }

    public static String ensureTraceId() {
        String traceId = getOrCreateTraceId(null);
        MDC.put(TRACE_ID_KEY, traceId);
        return traceId;
    }

    public static String newTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
