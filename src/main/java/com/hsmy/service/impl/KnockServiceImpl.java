package com.hsmy.service.impl;

import cn.hutool.core.util.IdUtil;
import com.hsmy.entity.AutoKnockSession;
import com.hsmy.entity.UserPeriodStats;
import com.hsmy.entity.UserStats;
import com.hsmy.exception.BusinessException;
import com.hsmy.enums.PeriodType;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.AchievementService;
import com.hsmy.service.KnockService;
import com.hsmy.service.MeritService;
import com.hsmy.service.UserPeriodStatsService;
import com.hsmy.service.TaskService;
import com.hsmy.vo.AutoKnockHeartbeatVO;
import com.hsmy.vo.AutoKnockStartVO;
import com.hsmy.vo.AutoKnockStopVO;
import com.hsmy.vo.KnockVO;
import com.hsmy.websocket.KnockRealtimeNotifier;
import com.hsmy.utils.UserLockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 敲击Service实现类
 *
 * @author HSMY
 * @date 2025/09/19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnockServiceImpl implements KnockService {

    private final MeritService meritService;
    private final TaskService taskService;
    private final AchievementService achievementService;
    private final UserStatsMapper userStatsMapper;
    private final UserPeriodStatsService userPeriodStatsService;
    private final KnockRealtimeNotifier knockRealtimeNotifier;
    private final UserLockManager userLockManager;

    // 用于存储用户最后敲击时间，实现简单的频率限制
    private static final Map<Long, LocalDateTime> lastKnockTimeMap = new ConcurrentHashMap<>();
    // 最小敲击间隔（毫秒）
    private static final long MIN_KNOCK_INTERVAL_MS = 100;

    // 存储自动敲击会话
    private static final Map<String, AutoKnockSession> autoKnockSessions = new ConcurrentHashMap<>();
    // 存储用户的活跃会话ID
    private static final Map<Long, String> userActiveSession = new ConcurrentHashMap<>();

    // 支持的自动敲击模式
    private static final Set<String> SUPPORTED_SESSION_MODES = new HashSet<>(Arrays.asList("AUTO_AUTOEND", "AUTO_TIMED"));
    // 支持的限制类型
    private static final Set<String> SUPPORTED_LIMIT_TYPES = new HashSet<>(Arrays.asList("DURATION", "COUNT"));
    // 允许的每秒敲击次数范围
    private static final int MIN_KNOCKS_PER_SECOND = 1;
    private static final int MAX_KNOCKS_PER_SECOND = 10;
    // 每次敲击基础功德值
    private static final int BASE_MERIT_PER_KNOCK = 1;
    // 自动敲击功德币计价配置
    private static final int COIN_COST_PER_MINUTE = 1;
    private static final int COIN_COST_PER_HUNDRED_KNOCKS = 1;
    private static final int MIN_AUTO_COIN_COST = 1;
    private static final String PAYMENT_STATUS_RESERVED = "RESERVED";
    private static final String PAYMENT_STATUS_SETTLED = "SETTLED";
    private static final String PAYMENT_STATUS_REFUNDED = "REFUNDED";

    @Override
    public Map<String, Object> manualKnock(KnockVO knockVO) {
        Long userId = knockVO.getUserId();

        // 1. 校验敲击频率限制
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastKnockTime = lastKnockTimeMap.get(userId);
        if (lastKnockTime != null) {
            long intervalMs = ChronoUnit.MILLIS.between(lastKnockTime, now);
            if (intervalMs < MIN_KNOCK_INTERVAL_MS) {
                throw new BusinessException("敲击太快了，请稍后再试");
            }
        }
        lastKnockTimeMap.put(userId, now);

        // 2. 补全敲击上下文
        if (knockVO.getMultiplier() == null || knockVO.getMultiplier() <= 0) {
            knockVO.setMultiplier(1.0);
        }
        if (knockVO.getKnockMode() == null || knockVO.getKnockMode().trim().isEmpty()) {
            knockVO.setKnockMode("MANUAL");
        } else {
            knockVO.setKnockMode(knockVO.getKnockMode().toUpperCase(Locale.ROOT));
        }
        if (knockVO.getLimitType() != null) {
            knockVO.setLimitType(knockVO.getLimitType().toUpperCase(Locale.ROOT));
        }
        if (knockVO.getKnockCount() == null || knockVO.getKnockCount() <= 0) {
            knockVO.setKnockCount(1);
        }
        if (knockVO.getMeritValue() == null || knockVO.getMeritValue() <= 0) {
            knockVO.setMeritValue(BASE_MERIT_PER_KNOCK);
        }

        // 2. 计算功德值收益
        Integer meritGained = meritService.manualKnock(knockVO);

        // 3. 记录敲击日志
        log.info("用户 {} 手动敲击，获得功德值：{}, 连击数：{}",
                userId, meritGained, knockVO.getComboCount());

        // 4. 异步检查成就和任务进度
        checkAchievementAndTaskProgress(userId, knockVO.getKnockCount(), meritGained);

        // 5. 获取更新后的用户统计
        UserStats userStats = userStatsMapper.selectByUserId(userId);
        Map<PeriodType, UserPeriodStats> periodStats = userPeriodStatsService.loadCurrentPeriods(userId, new Date());
        UserPeriodStats dayStats = periodStats.get(PeriodType.DAY);

        // 6. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("meritGained", meritGained);
        result.put("totalMerit", userStats.getTotalMerit());
        result.put("todayMerit", dayStats != null ? dayStats.getMeritGained() : 0L);
        result.put("totalKnocks", userStats.getTotalKnocks());
        result.put("todayKnocks", dayStats != null ? dayStats.getKnockCount() : 0L);
        result.put("comboCount", knockVO.getComboCount() != null ? knockVO.getComboCount() : 0);
        result.put("maxCombo", userStats.getMaxCombo());
        result.put("multiplier", knockVO.getMultiplier());
        result.put("propSnapshot", knockVO.getPropSnapshot());
        result.put("knockMode", knockVO.getKnockMode());

        knockRealtimeNotifier.notifyManualKnock(userId, result);
        return result;
    }

    @Override
    public Map<String, Object> startAutoKnock(Long userId, Integer duration, Integer knocksPerSecond) {
        AutoKnockStartVO legacyVO = new AutoKnockStartVO();
        legacyVO.setMode("AUTO_TIMED");
        legacyVO.setLimitType("DURATION");
        legacyVO.setLimitValue(duration != null ? duration : 60);
        legacyVO.setKnocksPerSecond(knocksPerSecond != null ? knocksPerSecond : 3);
        legacyVO.setMultiplier(1.0);
        legacyVO.setSource(1);
        return startAutoKnock(userId, legacyVO);
    }

    @Override
    public Map<String, Object> stopAutoKnock(Long userId, String sessionId) {
        AutoKnockSession session = autoKnockSessions.get(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }

        AutoKnockStopVO stopVO = new AutoKnockStopVO();
        stopVO.setSessionId(sessionId);
        stopVO.setKnockCount(session.getClientKnockCount() != null ? session.getClientKnockCount() : 0);
        if (session.getDuration() != null && session.getDuration() > 0) {
            stopVO.setActualDuration(session.getDuration());
        }
        return stopAutoKnock(userId, stopVO);
    }

    @Override
    public Map<String, Object> getKnockStats(Long userId) {
        // 1. 查询用户统计
        UserStats userStats = userStatsMapper.selectByUserId(userId);
        if (userStats == null) {
            throw new BusinessException("用户统计信息不存在");
        }

        Map<PeriodType, UserPeriodStats> periodStats = userPeriodStatsService.loadCurrentPeriods(userId, new Date());
        UserPeriodStats dayStats = periodStats.get(PeriodType.DAY);
        UserPeriodStats weekStats = periodStats.get(PeriodType.WEEK);
        UserPeriodStats monthStats = periodStats.get(PeriodType.MONTH);

        // 2. 构建统计数据
        Map<String, Object> stats = new HashMap<>();

        // 今日统计
        Map<String, Object> todayStats = new HashMap<>();
        todayStats.put("todayMerit", dayStats != null ? dayStats.getMeritGained() : 0L);
        todayStats.put("todayKnocks", dayStats != null ? dayStats.getKnockCount() : 0L);
        stats.put("today", todayStats);

        // 历史统计
        Map<String, Object> historyStats = new HashMap<>();
        historyStats.put("totalMerit", userStats.getTotalMerit());
        historyStats.put("totalKnocks", userStats.getTotalKnocks());
        historyStats.put("weeklyMerit", weekStats != null ? weekStats.getMeritGained() : 0L);
        historyStats.put("monthlyMerit", monthStats != null ? monthStats.getMeritGained() : 0L);
        historyStats.put("totalLoginDays", userStats.getTotalLoginDays());
        historyStats.put("consecutiveDays", userStats.getConsecutiveDays());
        stats.put("history", historyStats);

        // 连击记录
        Map<String, Object> comboStats = new HashMap<>();
        comboStats.put("maxCombo", userStats.getMaxCombo());
        stats.put("combo", comboStats);

        // 等级和功德币
        Map<String, Object> levelStats = new HashMap<>();
        levelStats.put("currentLevel", userStats.getCurrentLevel());
        levelStats.put("meritCoins", userStats.getMeritCoins());
        stats.put("level", levelStats);

        // 最后敲击时间
        if (userStats.getLastKnockTime() != null) {
            stats.put("lastKnockTime", userStats.getLastKnockTime());
        }

        // 检查是否有活跃的自动敲击会话
        String activeSessionId = userActiveSession.get(userId);
        if (activeSessionId != null) {
            AutoKnockSession activeSession = autoKnockSessions.get(activeSessionId);
            if (activeSession != null && "active".equals(activeSession.getStatus())) {
                Map<String, Object> autoKnockInfo = new HashMap<>();
                autoKnockInfo.put("sessionId", activeSessionId);
                autoKnockInfo.put("startTime", activeSession.getStartTime());
                autoKnockInfo.put("expectedEndTime", activeSession.getEndTime());
                autoKnockInfo.put("sessionMode", activeSession.getSessionMode());
                autoKnockInfo.put("limitType", activeSession.getLimitType());
                autoKnockInfo.put("limitValue", activeSession.getLimitValue());
                autoKnockInfo.put("multiplier", activeSession.getMultiplier());
                autoKnockInfo.put("propSnapshot", activeSession.getPropSnapshot());
                autoKnockInfo.put("coinCost", activeSession.getCoinCost());
                autoKnockInfo.put("coinRefunded", activeSession.getCoinRefunded());
                autoKnockInfo.put("paymentStatus", activeSession.getPaymentStatus());
                if (userStats != null && userStats.getMeritCoins() != null) {
                    autoKnockInfo.put("remainingCoins", userStats.getMeritCoins());
                }

                LocalDateTime now = LocalDateTime.now();
                long elapsedSeconds = ChronoUnit.SECONDS.between(activeSession.getStartTime(), now);
                Long totalSeconds = activeSession.getDuration() != null && activeSession.getDuration() > 0
                        ? activeSession.getDuration().longValue()
                        : null;
                if (totalSeconds != null && totalSeconds > 0) {
                    autoKnockInfo.put("progress", Math.min(100.0, (elapsedSeconds * 100.0) / totalSeconds));
                    autoKnockInfo.put("remainingSeconds", Math.max(0, totalSeconds - elapsedSeconds));
                } else {
                    autoKnockInfo.put("progress", -1);
                    autoKnockInfo.put("remainingSeconds", -1);
                }

                stats.put("activeAutoKnock", autoKnockInfo);
            }
        }

        return stats;
    }

    @Override
    public Map<String, Object> getAutoKnockStatus(Long userId) {
        // 1. 查询当前活跃的自动敲击会话
        String sessionId = userActiveSession.get(userId);

        Map<String, Object> status = new HashMap<>();

        if (sessionId == null) {
            status.put("hasActiveSession", false);
            status.put("message", "当前没有进行中的自动敲击");
            return status;
        }

        AutoKnockSession session = autoKnockSessions.get(sessionId);
        if (session == null || !"active".equals(session.getStatus())) {
            // 清理无效的映射
            userActiveSession.remove(userId);
            status.put("hasActiveSession", false);
            status.put("message", "当前没有进行中的自动敲击");
            return status;
        }

        // 2. 返回会话信息和进度
        status.put("hasActiveSession", true);
        status.put("sessionId", sessionId);
        status.put("startTime", session.getStartTime());
        status.put("expectedEndTime", session.getEndTime());
        status.put("duration", session.getDuration());
        status.put("sessionMode", session.getSessionMode());
        status.put("limitType", session.getLimitType());
        status.put("limitValue", session.getLimitValue());
        status.put("multiplier", session.getMultiplier());
        status.put("propSnapshot", session.getPropSnapshot());
        status.put("coinCost", session.getCoinCost());
        status.put("coinRefunded", session.getCoinRefunded());
        status.put("paymentStatus", session.getPaymentStatus());

        LocalDateTime now = LocalDateTime.now();
        long elapsedSeconds = ChronoUnit.SECONDS.between(session.getStartTime(), now);
        Long totalSeconds = session.getDuration() != null && session.getDuration() > 0 ? session.getDuration().longValue() : null;

        int expectedKnocks = session.getExpectedKnocks() != null ? session.getExpectedKnocks() : -1;
        double multiplier = session.getMultiplier() != null ? session.getMultiplier() : 1.0;

        int computedKnocks;
        if (expectedKnocks > 0) {
            computedKnocks = (int) Math.min(elapsedSeconds * session.getKnockPerSecond(), expectedKnocks);
        } else {
            // 使用客户端心跳上报的数值作为当前敲击次数
            computedKnocks = session.getClientKnockCount() != null
                    ? session.getClientKnockCount()
                    : (int) (elapsedSeconds * session.getKnockPerSecond());
        }

        int currentMerit = (int) Math.floor(computedKnocks * BASE_MERIT_PER_KNOCK * multiplier);

        status.put("elapsedSeconds", elapsedSeconds);
        if (totalSeconds != null && totalSeconds > 0) {
            status.put("remainingSeconds", Math.max(0, totalSeconds - elapsedSeconds));
            status.put("progress", Math.min(100.0, (elapsedSeconds * 100.0) / totalSeconds));
        } else {
            status.put("remainingSeconds", -1);
            status.put("progress", -1);
        }
        status.put("currentKnocks", computedKnocks);
        status.put("currentMerit", currentMerit);
        status.put("expectedKnocks", expectedKnocks);
        status.put("expectedMerit", session.getExpectedMerit());
        status.put("knockPerSecond", session.getKnockPerSecond());
        status.put("coinCost", session.getCoinCost());
        status.put("coinRefunded", session.getCoinRefunded());
        status.put("paymentStatus", session.getPaymentStatus());

        LocalDateTime expectedEnd = session.getEndTime();
        if (expectedEnd != null && now.isAfter(expectedEnd)) {
            status.put("isCompleting", true);
            status.put("message", "自动敲击即将完成");
        } else {
            status.put("isCompleting", false);
            status.put("message", "自动敲击进行中");
        }

        status.put("remainingCoins", queryRemainingCoins(userId));

        return status;
    }

    @Override
    public void cleanupTimeoutSessions() {
        LocalDateTime now = LocalDateTime.now();

        autoKnockSessions.values().stream()
                .filter(session -> "active".equals(session.getStatus()))
                .filter(session -> {
                    LocalDateTime lastHeartbeat = session.getLastHeartbeatTime();
                    if (lastHeartbeat != null) {
                        return ChronoUnit.MINUTES.between(lastHeartbeat, now) >= 1;
                    }
                    return ChronoUnit.MINUTES.between(session.getStartTime(), now) >= 1;
                })
                .forEach(session -> userLockManager.executeWithUserLock(session.getUserId(), () -> {
                    try {
                        if (!"active".equals(session.getStatus())) {
                            return;
                        }

                        Integer actualKnocks = session.getClientKnockCount() != null ? session.getClientKnockCount() : 0;
                        double sessionMultiplier = session.getMultiplier() != null ? session.getMultiplier() : 1.0;
                        Integer actualMerit = (int) Math.floor(actualKnocks * session.getBaseMeritPerKnock() * sessionMultiplier);

                        int actualDurationSeconds = (int) ChronoUnit.SECONDS.between(session.getStartTime(), now);

                        session.setActualKnocks(actualKnocks);
                        session.setActualMerit(actualMerit);
                        session.setStatus("timeout");
                        session.setEndReason("timeout");
                        session.setActualEndTime(now);

                        if (!session.getSettled() && actualMerit > 0) {
                            KnockVO knockVO = new KnockVO();
                            knockVO.setUserId(session.getUserId());
                            knockVO.setKnockCount(actualKnocks);
                            knockVO.setSessionId(session.getSessionId());
                            knockVO.setKnockMode(session.getSessionMode());
                            knockVO.setMultiplier(sessionMultiplier);
                            knockVO.setPropSnapshot(session.getPropSnapshot());
                            knockVO.setLimitType(session.getLimitType());
                            knockVO.setLimitValue(session.getLimitValue());
                            knockVO.setKnockType(2);

                            Integer totalMeritGained = meritService.manualKnock(knockVO);
                            session.setActualMerit(totalMeritGained);
                            session.setSettled(true);

                            checkAchievementAndTaskProgress(session.getUserId(), actualKnocks, totalMeritGained);

                            log.info("自动结算会话 {}，用户 {}，敲击 {} 次，获得功德 {}",
                                    session.getSessionId(), session.getUserId(),
                                    actualKnocks, totalMeritGained);
                        }

                        userActiveSession.remove(session.getUserId());

                        int coinCost = session.getCoinCost() != null ? session.getCoinCost() : 0;
                        int refundAmount = calculateRefundCoins(session, actualKnocks, actualDurationSeconds, false);
                        if (refundAmount > 0) {
                            refundCoins(session.getUserId(), refundAmount);
                            session.setCoinRefunded((session.getCoinRefunded() != null ? session.getCoinRefunded() : 0) + refundAmount);
                        }

                        if (coinCost > 0) {
                            if (session.getCoinRefunded() != null && session.getCoinRefunded() >= coinCost) {
                                session.setPaymentStatus(PAYMENT_STATUS_REFUNDED);
                            } else {
                                session.setPaymentStatus(PAYMENT_STATUS_SETTLED);
                            }
                        } else {
                            session.setPaymentStatus(PAYMENT_STATUS_SETTLED);
                        }

                        long remainingCoins = queryRemainingCoins(session.getUserId());

                        Map<String, Object> payload = new HashMap<>();
                        payload.put("sessionId", session.getSessionId());
                        payload.put("status", session.getStatus());
                        payload.put("actualKnocks", session.getActualKnocks());
                        payload.put("actualMerit", session.getActualMerit());
                        payload.put("endReason", session.getEndReason());
                        payload.put("coinCost", coinCost);
                        payload.put("coinRefunded", session.getCoinRefunded());
                        payload.put("paymentStatus", session.getPaymentStatus());
                        payload.put("remainingCoins", remainingCoins);
                        payload.put("duration", actualDurationSeconds);

                        knockRealtimeNotifier.notifyAutoTimeout(session.getUserId(), payload);
                    } catch (Exception e) {
                        log.error("自动结算会话失败：" + session.getSessionId(), e);
                    }
                }));

        // 清理已完成超过1小时的会话
        autoKnockSessions.entrySet().removeIf(entry -> {
            AutoKnockSession session = entry.getValue();
            if ("active".equals(session.getStatus())) {
                return false;
            }
            if (session.getEndTime() == null) {
                return session.getActualEndTime() != null && now.isAfter(session.getActualEndTime().plusHours(1));
            }
            return now.isAfter(session.getEndTime().plusHours(1));
        });
    }

    @Override
    public Map<String, Object> startAutoKnock(Long userId, AutoKnockStartVO startVO) {
        return userLockManager.executeWithUserLock(userId, () -> startAutoKnockInternal(userId, startVO));
    }

    private Map<String, Object> startAutoKnockInternal(Long userId, AutoKnockStartVO startVO) {
        String mode = startVO.getMode() != null ? startVO.getMode().toUpperCase(Locale.ROOT) : null;
        if (mode == null || !SUPPORTED_SESSION_MODES.contains(mode)) {
            throw new BusinessException("不支持的会话模式，请选择 AUTO_AUTOEND 或 AUTO_TIMED");
        }

        String limitType = startVO.getLimitType() != null ? startVO.getLimitType().toUpperCase(Locale.ROOT) : null;
        if (limitType == null || !SUPPORTED_LIMIT_TYPES.contains(limitType)) {
            throw new BusinessException("限制类型仅支持 DURATION 或 COUNT");
        }

        Integer limitValue = startVO.getLimitValue();
        if (limitValue == null || limitValue <= 0) {
            throw new BusinessException("自动敲击需要正整数的限制值");
        }

        Integer knocksPerSecond = startVO.getKnocksPerSecond() != null
                ? startVO.getKnocksPerSecond()
                : 3;
        if (knocksPerSecond < MIN_KNOCKS_PER_SECOND || knocksPerSecond > MAX_KNOCKS_PER_SECOND) {
            throw new BusinessException("每秒敲击次数必须在" + MIN_KNOCKS_PER_SECOND + "到" + MAX_KNOCKS_PER_SECOND + "之间");
        }

        Double multiplier = startVO.getMultiplier() != null && startVO.getMultiplier() > 0
                ? startVO.getMultiplier()
                : 1.0;

        String existingSessionId = userActiveSession.get(userId);
        if (existingSessionId != null) {
            AutoKnockSession existingSession = autoKnockSessions.get(existingSessionId);
            if (existingSession != null && "active".equals(existingSession.getStatus())) {
                throw new BusinessException("您已有正在进行的自动敲击，请先停止后再开始新的");
            }
        }

        String sessionId = IdUtil.simpleUUID();
        LocalDateTime now = LocalDateTime.now();

        AutoKnockSession session = new AutoKnockSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setStartTime(now);
        session.setSessionMode(mode);
        session.setLimitType(limitType);
        session.setLimitValue(limitValue);
        session.setKnockPerSecond(knocksPerSecond);
        session.setBaseMeritPerKnock(BASE_MERIT_PER_KNOCK);
        session.setMultiplier(multiplier);
        session.setPropSnapshot(startVO.getPropSnapshot());
        session.setSource(startVO.getSource());
        session.setStatus("active");
        session.setSettled(false);
        session.setActualKnocks(0);
        session.setActualMerit(0);
        session.setClientKnockCount(0);
        session.setLastHeartbeatTime(now);
        session.setEndReason(null);
        session.setActualEndTime(null);

        Integer expectedKnocks;
        Integer expectedMerit;
        Integer sessionDuration;
        LocalDateTime expectedEnd;

        if ("DURATION".equals(limitType)) {
            sessionDuration = limitValue;
            expectedKnocks = limitValue * knocksPerSecond;
            expectedEnd = now.plusSeconds(sessionDuration.longValue());
        } else {
            sessionDuration = (int) Math.ceil(limitValue / (double) knocksPerSecond);
            expectedKnocks = limitValue;
            expectedEnd = now.plusSeconds(sessionDuration.longValue());
        }

        double expectedMeritRaw = expectedKnocks * BASE_MERIT_PER_KNOCK * multiplier;
        expectedMerit = (int) Math.floor(expectedMeritRaw);

        session.setDuration(sessionDuration);
        session.setExpectedKnocks(expectedKnocks);
        session.setExpectedMerit(expectedMerit);
        session.setEndTime(expectedEnd);

        int coinCost = calculateCoinCost(limitType, sessionDuration, expectedKnocks);
        boolean coinsDeducted = false;
        try {
            if (coinCost > 0) {
                deductCoins(userId, coinCost);
                coinsDeducted = true;
            }

            session.setCoinCost(coinCost);
            session.setCoinRefunded(0);
            session.setPaymentStatus(PAYMENT_STATUS_RESERVED);
            session.setWalletTxnId(null);

            autoKnockSessions.put(sessionId, session);
            userActiveSession.put(userId, sessionId);

            log.info("用户 {} 开始自动敲击，会话ID：{}，模式：{}，限制 {} {}，倍率 {}，每秒敲击 {}，功德币成本 {}",
                    userId, sessionId, mode, limitType, limitValue, multiplier, knocksPerSecond, coinCost);

            long remainingCoins = queryRemainingCoins(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("startTime", now);
            result.put("expectedEndTime", expectedEnd);
            result.put("sessionMode", mode);
            result.put("limitType", limitType);
            result.put("limitValue", limitValue);
            result.put("knockPerSecond", knocksPerSecond);
            result.put("expectedKnocks", expectedKnocks);
            result.put("expectedMerit", expectedMerit);
            result.put("multiplier", multiplier);
            result.put("propSnapshot", startVO.getPropSnapshot());
            result.put("coinCost", coinCost);
            result.put("paymentStatus", session.getPaymentStatus());
            result.put("remainingCoins", remainingCoins);

            knockRealtimeNotifier.notifyAutoStart(userId, result);
            return result;
        } catch (RuntimeException ex) {
            if (coinsDeducted) {
                refundCoins(userId, coinCost);
            }
            throw ex;
        }
    }

    @Override
    public Map<String, Object> stopAutoKnock(Long userId, AutoKnockStopVO stopVO) {
        return userLockManager.executeWithUserLock(userId, () -> stopAutoKnockInternal(userId, stopVO));
    }

    private Map<String, Object> stopAutoKnockInternal(Long userId, AutoKnockStopVO stopVO) {
        String sessionId = stopVO.getSessionId();
        Integer clientKnockCount = stopVO.getKnockCount();

        AutoKnockSession session = autoKnockSessions.get(sessionId);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }

        if (!session.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此会话");
        }

        if (!"active".equals(session.getStatus())) {
            throw new BusinessException("会话已结束");
        }

        Integer actualKnocks = clientKnockCount != null ? clientKnockCount : 0;
        double sessionMultiplier = session.getMultiplier() != null ? session.getMultiplier() : 1.0;
        int baseMerit = actualKnocks * BASE_MERIT_PER_KNOCK;
        int estimatedMerit = (int) Math.floor(baseMerit * sessionMultiplier);

        LocalDateTime now = LocalDateTime.now();
        session.setActualKnocks(actualKnocks);
        session.setActualMerit(estimatedMerit);
        session.setClientKnockCount(actualKnocks);
        session.setActualEndTime(now);

        int actualDurationSeconds = stopVO.getActualDuration() != null ? stopVO.getActualDuration() :
                (int) ChronoUnit.SECONDS.between(session.getStartTime(), now);

        boolean reachLimit = false;
        if ("COUNT".equalsIgnoreCase(session.getLimitType()) && session.getLimitValue() != null && session.getLimitValue() > 0) {
            reachLimit = actualKnocks >= session.getLimitValue();
        } else if ("DURATION".equalsIgnoreCase(session.getLimitType()) && session.getLimitValue() != null && session.getLimitValue() > 0) {
            reachLimit = actualDurationSeconds >= session.getLimitValue();
        }

        session.setStatus(reachLimit ? "completed" : "stopped");
        session.setEndReason(reachLimit ? "limit_reached" : "manual_stop");

        Integer totalMeritGained = 0;
        if (estimatedMerit > 0 && !session.getSettled()) {
            KnockVO knockVO = new KnockVO();
            knockVO.setUserId(userId);
            knockVO.setKnockCount(actualKnocks);
            knockVO.setMeritValue(BASE_MERIT_PER_KNOCK);
            knockVO.setSessionId(sessionId);
            knockVO.setKnockMode(session.getSessionMode());
            knockVO.setMultiplier(sessionMultiplier);
            knockVO.setPropSnapshot(session.getPropSnapshot());
            knockVO.setLimitType(session.getLimitType());
            knockVO.setLimitValue(session.getLimitValue());
            knockVO.setKnockType(2);

            totalMeritGained = meritService.manualKnock(knockVO);
            session.setActualMerit(totalMeritGained);
            session.setSettled(true);

            checkAchievementAndTaskProgress(userId, actualKnocks, totalMeritGained);

            log.info("用户 {} 停止自动敲击，会话ID：{}，敲击 {} 次，获得功德 {}，倍率 {}",
                    userId, sessionId, actualKnocks, totalMeritGained, sessionMultiplier);
        }

        userActiveSession.remove(userId);

        int coinCost = session.getCoinCost() != null ? session.getCoinCost() : 0;
        int refundAmount = calculateRefundCoins(session, actualKnocks, actualDurationSeconds, reachLimit);
        if (refundAmount > 0) {
            refundCoins(userId, refundAmount);
            session.setCoinRefunded((session.getCoinRefunded() != null ? session.getCoinRefunded() : 0) + refundAmount);
        }

        if (coinCost > 0) {
            if (session.getCoinRefunded() != null && session.getCoinRefunded() >= coinCost) {
                session.setPaymentStatus(PAYMENT_STATUS_REFUNDED);
            } else {
                session.setPaymentStatus(PAYMENT_STATUS_SETTLED);
            }
        } else {
            session.setPaymentStatus(PAYMENT_STATUS_SETTLED);
        }

        long remainingCoins = queryRemainingCoins(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("status", session.getStatus());
        result.put("actualKnocks", actualKnocks);
        result.put("actualMerit", session.getActualMerit());
        result.put("multiplier", sessionMultiplier);
        result.put("endReason", session.getEndReason());
        result.put("propSnapshot", session.getPropSnapshot());
        result.put("duration", actualDurationSeconds);
        result.put("totalMeritGained", totalMeritGained);
        result.put("coinCost", coinCost);
        result.put("coinRefunded", session.getCoinRefunded());
        result.put("paymentStatus", session.getPaymentStatus());
        result.put("remainingCoins", remainingCoins);

        knockRealtimeNotifier.notifyAutoStop(userId, result);
        return result;
    }

    @Override
    public Map<String, Object> heartbeat(Long userId, AutoKnockHeartbeatVO heartbeatVO) {
        String sessionId = heartbeatVO.getSessionId();
        AutoKnockSession session = autoKnockSessions.get(sessionId);

        if (session == null) {
            throw new BusinessException("会话不存在");
        }

        if (!userId.equals(session.getUserId())) {
            throw new BusinessException("无权限访问此会话");
        }

        if (!"active".equals(session.getStatus())) {
            throw new BusinessException("会话已结束");
        }

        // 更新心跳时间和客户端敲击数
        session.setLastHeartbeatTime(LocalDateTime.now());
        session.setClientKnockCount(heartbeatVO.getCurrentKnockCount());

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("status", session.getStatus());
        result.put("sessionMode", session.getSessionMode());
        result.put("multiplier", session.getMultiplier());
        result.put("propSnapshot", session.getPropSnapshot());
        result.put("coinCost", session.getCoinCost());
        result.put("coinRefunded", session.getCoinRefunded());
        result.put("paymentStatus", session.getPaymentStatus());

        // 处理剩余时间：如果是无限时长（duration为-1），则剩余时间为-1
        if (session.getDuration() == -1 || session.getEndTime() == null) {
            result.put("remainingTime", -1);
        } else {
            long remainingSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), session.getEndTime());
            result.put("remainingTime", Math.max(0, remainingSeconds));
        }

        result.put("expectedKnocks", session.getExpectedKnocks());
        result.put("currentKnocks", heartbeatVO.getCurrentKnockCount());

        knockRealtimeNotifier.notifyHeartbeat(userId, result);
        return result;
    }

    @Override
    @Deprecated
    public void checkAndSettleExpiredSessions() {
        cleanupTimeoutSessions();
    }

    /**
     * 计算自动敲击所需功德币成本
     */
    private int calculateCoinCost(String limitType, Integer sessionDuration, Integer expectedKnocks) {
        if (sessionDuration == null || expectedKnocks == null) {
            return MIN_AUTO_COIN_COST;
        }
        int cost;
        if ("DURATION".equalsIgnoreCase(limitType)) {
            int minutes = (int) Math.ceil(sessionDuration / 60.0);
            cost = minutes * COIN_COST_PER_MINUTE;
        } else if ("COUNT".equalsIgnoreCase(limitType)) {
            int units = (int) Math.ceil(expectedKnocks / 100.0);
            cost = units * COIN_COST_PER_HUNDRED_KNOCKS;
        } else {
            cost = MIN_AUTO_COIN_COST;
        }
        return Math.max(MIN_AUTO_COIN_COST, cost);
    }

    /**
     * 计算应退还的功德币
     */
    private int calculateRefundCoins(AutoKnockSession session, int actualKnocks, Integer actualDurationSeconds, boolean reachLimit) {
        int coinCost = session.getCoinCost() != null ? session.getCoinCost() : 0;
        if (coinCost <= 0 || reachLimit) {
            return 0;
        }

        double ratio;
        if ("COUNT".equalsIgnoreCase(session.getLimitType()) && session.getLimitValue() != null && session.getLimitValue() > 0) {
            ratio = Math.min(1.0, actualKnocks / (double) session.getLimitValue());
        } else if ("DURATION".equalsIgnoreCase(session.getLimitType()) && session.getLimitValue() != null && session.getLimitValue() > 0) {
            int durationUsed = actualDurationSeconds != null ? Math.max(0, actualDurationSeconds) : 0;
            ratio = Math.min(1.0, durationUsed / (double) session.getLimitValue());
        } else {
            ratio = 1.0;
        }

        int consumedCoins = (int) Math.ceil(coinCost * ratio);
        consumedCoins = Math.min(coinCost, Math.max(0, consumedCoins));
        int refund = coinCost - consumedCoins;
        return Math.max(0, refund);
    }

    private void deductCoins(Long userId, int coins) {
        if (coins <= 0) {
            return;
        }
        int updated = userStatsMapper.reduceMeritCoins(userId, (long) coins);
        if (updated <= 0) {
            throw new BusinessException("功德币不足，请先充值或调整自动敲击设置");
        }
    }

    private void refundCoins(Long userId, int coins) {
        if (coins <= 0) {
            return;
        }
        userStatsMapper.addMeritCoins(userId, (long) coins);
    }

    private long queryRemainingCoins(Long userId) {
        UserStats stats = userStatsMapper.selectByUserId(userId);
        return stats != null && stats.getMeritCoins() != null ? stats.getMeritCoins() : 0L;
    }

    /**
     * 异步检查成就和任务进度
     */
    private void checkAchievementAndTaskProgress(Long userId, Integer knockCount, Integer meritGained) {
        try {
            // 更新敲击类型的任务进度
            taskService.updateTaskProgress(userId, "knock", knockCount);

            // 更新功德类型的任务进度
            taskService.updateTaskProgress(userId, "merit", meritGained);

            // 更新敲击类型的成就进度
            achievementService.updateAchievementProgress(userId, "knock", knockCount.longValue());

            // 更新功德类型的成就进度
            achievementService.updateAchievementProgress(userId, "merit", meritGained.longValue());
        } catch (Exception e) {
            log.error("更新成就和任务进度失败", e);
        }
    }
}
