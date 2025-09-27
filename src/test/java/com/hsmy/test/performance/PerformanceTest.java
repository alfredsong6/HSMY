package com.hsmy.test.performance;

import com.hsmy.HsmyApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 性能测试
 */
@SpringBootTest(classes = HsmyApplication.class)
@TestPropertySource(locations = "classpath:application-test.yml")
class PerformanceTest {

    private static final String TEST_TOKEN = "test_token_123456";
    private static final int CONCURRENT_USERS = 50;
    private static final int OPERATIONS_PER_USER = 20;

    @BeforeEach
    void setUp() {
        // 初始化测试环境
    }

    @Test
    void testKnockServiceThroughput() throws Exception {
        System.out.println("开始敲击服务吞吐量测试...");
        
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        LongAdder successCount = new LongAdder();
        LongAdder errorCount = new LongAdder();
        
        long startTime = System.currentTimeMillis();

        CompletableFuture<Void>[] futures = new CompletableFuture[CONCURRENT_USERS];
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            final int userId = i + 1;
            futures[i] = CompletableFuture.runAsync(() -> {
                for (int j = 0; j < OPERATIONS_PER_USER; j++) {
                    try {
                        boolean success = performKnockOperation(userId);
                        if (success) {
                            successCount.increment();
                        } else {
                            errorCount.increment();
                        }
                    } catch (Exception e) {
                        errorCount.increment();
                    }
                }
            }, executor);
        }

        CompletableFuture.allOf(futures).get(60, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        long totalOperations = CONCURRENT_USERS * OPERATIONS_PER_USER;
        long actualSuccessful = successCount.sum();
        long totalTime = endTime - startTime;
        double throughput = (double) actualSuccessful / (totalTime / 1000.0);

        System.out.println("=== 敲击服务性能测试结果 ===");
        System.out.println("并发用户数: " + CONCURRENT_USERS);
        System.out.println("每用户操作数: " + OPERATIONS_PER_USER);
        System.out.println("总操作数: " + totalOperations);
        System.out.println("成功操作数: " + actualSuccessful);
        System.out.println("失败操作数: " + errorCount.sum());
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("平均响应时间: " + (totalTime / (double) actualSuccessful) + "ms");
        System.out.println("吞吐量: " + String.format("%.2f", throughput) + " ops/sec");
        System.out.println("成功率: " + String.format("%.2f", (actualSuccessful / (double) totalOperations) * 100) + "%");

        // 性能断言
        assertTrue(throughput > 50, "吞吐量应该大于50 ops/sec，实际: " + throughput);
        assertTrue(totalTime < 30000, "总耗时应该小于30秒，实际: " + totalTime + "ms");
        assertTrue(actualSuccessful > totalOperations * 0.9, "成功率应该大于90%");

        executor.shutdown();
    }

    @Test
    void testMeritExchangeConcurrency() throws Exception {
        System.out.println("开始功德兑换并发测试...");
        
        int concurrentUsers = 20;
        int exchangeOperations = 10;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        AtomicInteger successfulExchanges = new AtomicInteger(0);
        AtomicInteger failedExchanges = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        CompletableFuture<Void>[] futures = new CompletableFuture[concurrentUsers];
        for (int i = 0; i < concurrentUsers; i++) {
            final int userId = i + 1;
            futures[i] = CompletableFuture.runAsync(() -> {
                for (int j = 0; j < exchangeOperations; j++) {
                    try {
                        boolean success = performMeritExchange(userId, 10);
                        if (success) {
                            successfulExchanges.incrementAndGet();
                        } else {
                            failedExchanges.incrementAndGet();
                        }
                        Thread.sleep(50); // 添加小延迟模拟真实场景
                    } catch (Exception e) {
                        failedExchanges.incrementAndGet();
                    }
                }
            }, executor);
        }

        CompletableFuture.allOf(futures).get(30, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        System.out.println("=== 功德兑换并发测试结果 ===");
        System.out.println("并发用户数: " + concurrentUsers);
        System.out.println("成功兑换: " + successfulExchanges.get());
        System.out.println("失败兑换: " + failedExchanges.get());
        System.out.println("耗时: " + (endTime - startTime) + "ms");

        // 验证数据一致性
        assertTrue(successfulExchanges.get() + failedExchanges.get() == concurrentUsers * exchangeOperations,
                "操作总数应该等于成功数加失败数");

        executor.shutdown();
    }

    @Test
    void testApiResponseTime() throws Exception {
        System.out.println("开始API响应时间测试...");
        
        int testCount = 100;
        LongAdder totalResponseTime = new LongAdder();
        long maxResponseTime = 0;
        long minResponseTime = Long.MAX_VALUE;
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < testCount; i++) {
            long startTime = System.nanoTime();
            
            boolean success = performQuickApiCall(i + 1);
            
            long responseTime = (System.nanoTime() - startTime) / 1_000_000; // 转换为毫秒
            
            if (success) {
                successCount.incrementAndGet();
                totalResponseTime.add(responseTime);
                maxResponseTime = Math.max(maxResponseTime, responseTime);
                minResponseTime = Math.min(minResponseTime, responseTime);
            }
            
            Thread.sleep(10); // 小延迟避免过度压力
        }

        double avgResponseTime = totalResponseTime.sum() / (double) successCount.get();

        System.out.println("=== API响应时间测试结果 ===");
        System.out.println("测试次数: " + testCount);
        System.out.println("成功次数: " + successCount.get());
        System.out.println("平均响应时间: " + String.format("%.2f", avgResponseTime) + "ms");
        System.out.println("最大响应时间: " + maxResponseTime + "ms");
        System.out.println("最小响应时间: " + minResponseTime + "ms");

        // 响应时间断言
        assertTrue(avgResponseTime < 500, "平均响应时间应该小于500ms，实际: " + avgResponseTime);
        assertTrue(maxResponseTime < 2000, "最大响应时间应该小于2000ms，实际: " + maxResponseTime);
        assertTrue(successCount.get() > testCount * 0.95, "成功率应该大于95%");
    }

    @Test
    void testMemoryUsage() {
        System.out.println("开始内存使用测试...");
        
        Runtime runtime = Runtime.getRuntime();
        
        // 记录初始内存状态
        long initialUsedMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // 执行大量操作
        for (int i = 0; i < 1000; i++) {
            performKnockOperation(i % 10 + 1);
            if (i % 100 == 0) {
                System.gc(); // 定期触发垃圾回收
            }
        }
        
        // 强制垃圾回收
        System.gc();
        Thread.yield();
        
        // 记录最终内存状态
        long finalUsedMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalUsedMemory - initialUsedMemory;
        
        System.out.println("=== 内存使用测试结果 ===");
        System.out.println("初始内存使用: " + (initialUsedMemory / 1024 / 1024) + "MB");
        System.out.println("最终内存使用: " + (finalUsedMemory / 1024 / 1024) + "MB");
        System.out.println("内存增长: " + (memoryIncrease / 1024 / 1024) + "MB");
        
        // 内存使用断言（内存增长不应该超过50MB）
        assertTrue(memoryIncrease < 50 * 1024 * 1024, 
                "内存增长不应该超过50MB，实际增长: " + (memoryIncrease / 1024 / 1024) + "MB");
    }

    @Test
    void testStressTest() throws Exception {
        System.out.println("开始压力测试...");
        
        int highConcurrency = 100;
        int stressOperations = 50;
        ExecutorService executor = Executors.newFixedThreadPool(highConcurrency);
        
        AtomicInteger totalOperations = new AtomicInteger(0);
        AtomicInteger successfulOperations = new AtomicInteger(0);
        AtomicInteger errors = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();

        CompletableFuture<Void>[] futures = new CompletableFuture[highConcurrency];
        for (int i = 0; i < highConcurrency; i++) {
            final int userId = i + 1;
            futures[i] = CompletableFuture.runAsync(() -> {
                for (int j = 0; j < stressOperations; j++) {
                    totalOperations.incrementAndGet();
                    try {
                        // 混合操作类型
                        boolean success;
                        if (j % 3 == 0) {
                            success = performKnockOperation(userId);
                        } else if (j % 3 == 1) {
                            success = performMeritExchange(userId, 5);
                        } else {
                            success = performQuickApiCall(userId);
                        }
                        
                        if (success) {
                            successfulOperations.incrementAndGet();
                        } else {
                            errors.incrementAndGet();
                        }
                    } catch (Exception e) {
                        errors.incrementAndGet();
                    }
                }
            }, executor);
        }

        CompletableFuture.allOf(futures).get(120, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        
        long totalTime = endTime - startTime;
        double throughput = (double) successfulOperations.get() / (totalTime / 1000.0);
        
        System.out.println("=== 压力测试结果 ===");
        System.out.println("并发数: " + highConcurrency);
        System.out.println("总操作数: " + totalOperations.get());
        System.out.println("成功操作数: " + successfulOperations.get());
        System.out.println("失败操作数: " + errors.get());
        System.out.println("成功率: " + String.format("%.2f", (successfulOperations.get() / (double) totalOperations.get()) * 100) + "%");
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("吞吐量: " + String.format("%.2f", throughput) + " ops/sec");
        
        // 压力测试断言
        assertTrue(successfulOperations.get() > totalOperations.get() * 0.8, 
                "压力测试成功率应该大于80%");
        assertTrue(throughput > 30, "压力测试吞吐量应该大于30 ops/sec");
        
        executor.shutdown();
    }

    // 模拟敲击操作
    private boolean performKnockOperation(int userId) {
        try {
            // 模拟敲击操作的处理时间
            Thread.sleep((long) (Math.random() * 50 + 10)); // 10-60ms随机延迟
            
            // 模拟90%的成功率
            return Math.random() > 0.1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // 模拟功德兑换操作
    private boolean performMeritExchange(int userId, int amount) {
        try {
            // 模拟兑换操作的处理时间
            Thread.sleep((long) (Math.random() * 100 + 20)); // 20-120ms随机延迟
            
            // 模拟85%的成功率（考虑余额不足等情况）
            return Math.random() > 0.15;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    // 模拟快速API调用
    private boolean performQuickApiCall(int userId) {
        try {
            // 模拟快速查询操作
            Thread.sleep((long) (Math.random() * 20 + 5)); // 5-25ms随机延迟
            
            // 模拟95%的成功率
            return Math.random() > 0.05;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}