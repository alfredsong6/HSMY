package com.hsmy.service.impl;

import com.hsmy.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirtualOrderShortPollingServiceTest {

    @Mock
    private PaymentService paymentService;

    private VirtualOrderShortPollingServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new VirtualOrderShortPollingServiceImpl(paymentService);
    }

    @Test
    void ensurePolling_deduplicatesSameOrder() {
        when(paymentService.syncWechatOrder("COIN400")).thenReturn(false);

        service.ensurePolling("COIN400");
        service.ensurePolling("COIN400");
        service.runPollingCycle();

        assertTrue(service.isPolling("COIN400"));
        verify(paymentService, times(1)).syncWechatOrder("COIN400");
    }

    @Test
    void runPollingCycle_removesOrder_whenSyncReturnsTerminal() {
        when(paymentService.syncWechatOrder("COIN401")).thenReturn(true);

        service.ensurePolling("COIN401");
        service.runPollingCycle();

        assertFalse(service.isPolling("COIN401"));
    }

    @Test
    void runPollingCycle_removesOrder_whenMaxAttemptsReached() {
        when(paymentService.syncWechatOrder("COIN402")).thenReturn(false);

        service.ensurePolling("COIN402");
        for (int i = 0; i < 15; i++) {
            service.runPollingCycle();
        }

        assertFalse(service.isPolling("COIN402"));
        verify(paymentService, times(15)).syncWechatOrder("COIN402");
    }

    @Test
    void shutdown_rejectsNewPollingTasks() {
        service.shutdown();
        service.ensurePolling("COIN403");

        assertFalse(service.isPolling("COIN403"));
    }
}
