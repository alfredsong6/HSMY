package com.hsmy.service.impl;

import com.hsmy.utils.UserLockManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MeritService并发安全测试
 * 这是一个独立的测试类，可以通过main方法运行
 *
 * @author HSMY
 * @date 2025/09/19
 */
public class MeritServiceConcurrencyTest {

    public static void main(String[] args) {
        MeritServiceConcurrencyTest test = new MeritServiceConcurrencyTest();

        System.out.println("开始测试用户锁管理器的并发安全性...");

        try {
            test.testUserLockManagerConcurrency();
            System.out.println("✓ 单用户并发测试通过");

            test.testMultipleUsersNoInterference();
            System.out.println("✓ 多用户并发测试通过");

            System.out.println("\n所有测试通过！用户锁机制工作正常。");
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void testUserLockManagerConcurrency() throws InterruptedException {
        UserLockManager lockManager = new UserLockManager();
        AtomicInteger counter = new AtomicInteger(0);
        int threadCount = 100;
        int incrementsPerThread = 10;
        Long userId = 1L;

        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // 启动多个线程同时对同一用户执行操作
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        lockManager.executeWithUserLock(userId, () -> {
                            // 模拟功德值累加操作
                            int current = counter.get();
                            // 添加一些延迟，增加竞态条件发生的概率
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            counter.set(current + 1);
                        });
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有线程完成
        latch.await();
        executor.shutdown();

        // 验证结果：应该等于 threadCount * incrementsPerThread
        int expected = threadCount * incrementsPerThread;
        int actual = counter.get();

        System.out.println("单用户测试 - Expected: " + expected + ", Actual: " + actual);

        if (expected != actual) {
            throw new AssertionError("并发操作结果不正确，期望: " + expected + ", 实际: " + actual);
        }
    }

    public void testMultipleUsersNoInterference() throws InterruptedException {
        UserLockManager lockManager = new UserLockManager();
        AtomicInteger user1Counter = new AtomicInteger(0);
        AtomicInteger user2Counter = new AtomicInteger(0);
        int threadCount = 50;
        int incrementsPerThread = 10;

        CountDownLatch latch = new CountDownLatch(threadCount * 2);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount * 2);

        // 用户1的操作
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        lockManager.executeWithUserLock(1L, () -> {
                            int current = user1Counter.get();
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            user1Counter.set(current + 1);
                        });
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 用户2的操作
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < incrementsPerThread; j++) {
                        lockManager.executeWithUserLock(2L, () -> {
                            int current = user2Counter.get();
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            user2Counter.set(current + 1);
                        });
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有线程完成
        latch.await();
        executor.shutdown();

        // 验证结果：每个用户的操作都应该是正确的
        int expected = threadCount * incrementsPerThread;

        System.out.println("多用户测试 - User1 Expected: " + expected + ", Actual: " + user1Counter.get());
        System.out.println("多用户测试 - User2 Expected: " + expected + ", Actual: " + user2Counter.get());

        if (expected != user1Counter.get()) {
            throw new AssertionError("用户1的并发操作结果不正确，期望: " + expected + ", 实际: " + user1Counter.get());
        }

        if (expected != user2Counter.get()) {
            throw new AssertionError("用户2的并发操作结果不正确，期望: " + expected + ", 实际: " + user2Counter.get());
        }
    }
}