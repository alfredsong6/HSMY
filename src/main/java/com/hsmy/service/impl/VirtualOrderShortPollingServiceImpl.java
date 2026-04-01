package com.hsmy.service.impl;

import com.hsmy.service.PaymentService;
import com.hsmy.service.VirtualOrderShortPollingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class VirtualOrderShortPollingServiceImpl implements VirtualOrderShortPollingService {

    private static final long POLL_INTERVAL_MILLIS = 1500L;
    private static final int MAX_ATTEMPTS = 15;
    private static final long SHUTDOWN_WAIT_MILLIS = 5000L;
    private static final long SHUTDOWN_SLEEP_MILLIS = 100L;

    private final PaymentService paymentService;
    private final ConcurrentMap<String, PollingTask> tasks = new ConcurrentHashMap<>();
    private final AtomicBoolean cycleRunning = new AtomicBoolean(false);
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    public VirtualOrderShortPollingServiceImpl(@Lazy PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void ensurePolling(String orderNo) {
        if (!StringUtils.hasText(orderNo)) {
            return;
        }
        if (shuttingDown.get()) {
            log.info("服务正在关闭，忽略虚拟支付短轮询注册，orderNo={}", orderNo);
            return;
        }
        tasks.computeIfAbsent(orderNo, PollingTask::new);
    }

    @Override
    public boolean isPolling(String orderNo) {
        return StringUtils.hasText(orderNo) && tasks.containsKey(orderNo);
    }

    @Scheduled(fixedDelay = POLL_INTERVAL_MILLIS)
    public void runPollingCycle() {
        if (tasks.isEmpty()) {
            return;
        }
        if (!cycleRunning.compareAndSet(false, true)) {
            return;
        }
        try {
            List<PollingTask> snapshot = new ArrayList<>(tasks.values());
            for (PollingTask task : snapshot) {
                pollOrder(task);
            }
        } finally {
            cycleRunning.set(false);
        }
    }

    @PreDestroy
    public void shutdown() {
        shuttingDown.set(true);
        long deadline = System.currentTimeMillis() + SHUTDOWN_WAIT_MILLIS;
        while (cycleRunning.get() && System.currentTimeMillis() < deadline) {
            try {
                Thread.sleep(SHUTDOWN_SLEEP_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (!tasks.isEmpty()) {
            log.info("虚拟支付短轮询服务关闭时仍有{}个活跃任务", tasks.size());
        }
    }

    private void pollOrder(PollingTask task) {
        int attempt = task.nextAttempt();
        String orderNo = task.getOrderNo();
        try {
            boolean terminal = paymentService.syncWechatOrder(orderNo);
            if (terminal) {
                tasks.remove(orderNo, task);
                return;
            }
            if (attempt >= MAX_ATTEMPTS) {
                log.info("虚拟支付短轮询达到最大尝试次数，停止轮询，orderNo={}, attempts={}", orderNo, attempt);
                tasks.remove(orderNo, task);
            }
        } catch (Exception e) {
            log.error("虚拟支付短轮询执行失败，orderNo={}, attempt={}", orderNo, attempt, e);
            if (attempt >= MAX_ATTEMPTS) {
                tasks.remove(orderNo, task);
            }
        }
    }

    private static final class PollingTask {

        private final String orderNo;
        private final AtomicInteger attempts = new AtomicInteger(0);

        private PollingTask(String orderNo) {
            this.orderNo = orderNo;
        }

        private String getOrderNo() {
            return orderNo;
        }

        private int nextAttempt() {
            return attempts.incrementAndGet();
        }
    }
}
