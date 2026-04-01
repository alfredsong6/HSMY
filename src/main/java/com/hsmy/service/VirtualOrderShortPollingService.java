package com.hsmy.service;

/**
 * Short-lived in-memory polling for virtual payment orders.
 */
public interface VirtualOrderShortPollingService {

    void ensurePolling(String orderNo);

    boolean isPolling(String orderNo);
}
