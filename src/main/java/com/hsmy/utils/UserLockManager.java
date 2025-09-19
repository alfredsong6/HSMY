package com.hsmy.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用户锁管理器
 * 为每个用户ID提供独立的锁，确保同一用户的操作串行化
 *
 * @author HSMY
 * @date 2025/09/19
 */
@Slf4j
@Component
public class UserLockManager {

    /**
     * 存储用户锁的映射，key为userId，value为对应的锁
     */
    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    /**
     * 获取指定用户的锁
     *
     * @param userId 用户ID
     * @return 该用户对应的锁对象
     */
    public ReentrantLock getUserLock(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId不能为null");
        }

        // 使用computeIfAbsent确保线程安全地创建锁
        return userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
    }

    /**
     * 执行需要用户锁保护的操作
     *
     * @param userId 用户ID
     * @param action 需要执行的操作
     * @param <T> 返回值类型
     * @return 操作结果
     */
    public <T> T executeWithUserLock(Long userId, UserLockAction<T> action) {
        ReentrantLock lock = getUserLock(userId);
        lock.lock();
        try {
            log.debug("获取用户{}的锁成功", userId);
            return action.execute();
        } finally {
            lock.unlock();
            log.debug("释放用户{}的锁成功", userId);
        }
    }

    /**
     * 执行需要用户锁保护的操作（无返回值）
     *
     * @param userId 用户ID
     * @param action 需要执行的操作
     */
    public void executeWithUserLock(Long userId, UserLockVoidAction action) {
        ReentrantLock lock = getUserLock(userId);
        lock.lock();
        try {
            log.debug("获取用户{}的锁成功", userId);
            action.execute();
        } finally {
            lock.unlock();
            log.debug("释放用户{}的锁成功", userId);
        }
    }

    /**
     * 清理无用的锁（可选的优化方法，在用户数量很大时使用）
     * 注意：这个方法需要谨慎使用，确保没有正在使用的锁被清理
     */
    public void cleanupUnusedLocks() {
        userLocks.entrySet().removeIf(entry -> {
            ReentrantLock lock = entry.getValue();
            // 只清理没有被任何线程持有的锁
            if (!lock.isLocked() && lock.getQueueLength() == 0) {
                log.debug("清理用户{}的无用锁", entry.getKey());
                return true;
            }
            return false;
        });
    }

    /**
     * 获取当前锁的数量（用于监控）
     *
     * @return 当前锁的数量
     */
    public int getLockCount() {
        return userLocks.size();
    }

    /**
     * 用户锁操作接口（有返回值）
     *
     * @param <T> 返回值类型
     */
    @FunctionalInterface
    public interface UserLockAction<T> {
        T execute();
    }

    /**
     * 用户锁操作接口（无返回值）
     */
    @FunctionalInterface
    public interface UserLockVoidAction {
        void execute();
    }
}