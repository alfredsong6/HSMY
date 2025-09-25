package com.hsmy.service;

import com.hsmy.vo.AutoKnockHeartbeatVO;
import com.hsmy.vo.AutoKnockStartVO;
import com.hsmy.vo.AutoKnockStopVO;
import com.hsmy.vo.KnockVO;

import java.time.LocalDate;
import java.util.Map;

/**
 * 敲击Service接口
 *
 * @author HSMY
 * @date 2025/09/19
 */
public interface KnockService {

    /**
     * 手动敲击
     *
     * @param knockVO 敲击参数
     * @return 敲击结果
     */
    Map<String, Object> manualKnock(KnockVO knockVO);

    /**
     * 开始自动敲击（新版本）
     *
     * @param userId 用户ID
     * @param startVO 自动敲击启动参数
     * @return 操作结果
     */
    Map<String, Object> startAutoKnock(Long userId, AutoKnockStartVO startVO);

    /**
     * 停止自动敲击（新版本）
     *
     * @param userId 用户ID
     * @param stopVO 自动敲击停止参数
     * @return 操作结果
     */
    Map<String, Object> stopAutoKnock(Long userId, AutoKnockStopVO stopVO);

    /**
     * 自动敲击心跳
     *
     * @param userId 用户ID
     * @param heartbeatVO 心跳参数
     * @return 心跳响应
     */
    Map<String, Object> heartbeat(Long userId, AutoKnockHeartbeatVO heartbeatVO);

    /**
     * 获取指定日期所在日/周/月/年的敲击统计
     *
     * @param userId 用户ID
     * @param referenceDate 参考日期（null 则默认为今天）
     * @return 周期统计数据
     */
    Map<String, Object> getKnockPeriodStats(Long userId, LocalDate referenceDate);

    /**
     * 获取敲击统计
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    Map<String, Object> getKnockStats(Long userId);

    /**
     * 获取当前自动敲击状态
     *
     * @param userId 用户ID
     * @return 自动敲击状态
     */
    Map<String, Object> getAutoKnockStatus(Long userId);

    /**
     * 检查并结算超时的会话（定时任务调用）
     * 超过1分钟没有心跳的会话将被结算
     */
    void cleanupTimeoutSessions();

    /**
     * 旧版兼容方法（将被废弃）
     */
    @Deprecated
    Map<String, Object> startAutoKnock(Long userId, Integer duration, Integer knocksPerSecond);

    @Deprecated
    Map<String, Object> stopAutoKnock(Long userId, String sessionId);

    @Deprecated
    void checkAndSettleExpiredSessions();
}