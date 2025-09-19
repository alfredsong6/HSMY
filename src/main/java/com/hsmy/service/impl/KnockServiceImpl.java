package com.hsmy.service.impl;

import cn.hutool.core.util.IdUtil;
import com.hsmy.entity.AutoKnockSession;
import com.hsmy.entity.UserStats;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.AchievementService;
import com.hsmy.service.KnockService;
import com.hsmy.service.MeritService;
import com.hsmy.service.TaskService;
import com.hsmy.vo.AutoKnockHeartbeatVO;
import com.hsmy.vo.AutoKnockStartVO;
import com.hsmy.vo.AutoKnockStopVO;
import com.hsmy.vo.KnockVO;
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

    // 用于存储用户最后敲击时间，实现简单的频率限制
    private static final Map<Long, LocalDateTime> lastKnockTimeMap = new ConcurrentHashMap<>();
    // 最小敲击间隔（毫秒）
    private static final long MIN_KNOCK_INTERVAL_MS = 100;

    // 存储自动敲击会话
    private static final Map<String, AutoKnockSession> autoKnockSessions = new ConcurrentHashMap<>();
    // 存储用户的活跃会话ID
    private static final Map<Long, String> userActiveSession = new ConcurrentHashMap<>();

    // 允许的自动敲击时长（秒）
    private static final Set<Integer> ALLOWED_DURATIONS = new HashSet<>(Arrays.asList(-1, 10, 30, 60, 300, 600));
    // 允许的每秒敲击次数范围
    private static final int MIN_KNOCKS_PER_SECOND = 1;
    private static final int MAX_KNOCKS_PER_SECOND = 10;
    // 每次敲击基础功德值
    private static final int BASE_MERIT_PER_KNOCK = 1;

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

        // 2. 计算功德值收益
        Integer meritGained = meritService.manualKnock(knockVO);

        // 3. 记录敲击日志
        log.info("用户 {} 手动敲击，获得功德值：{}, 连击数：{}",
                userId, meritGained, knockVO.getComboCount());

        // 4. 异步检查成就和任务进度
        checkAchievementAndTaskProgress(userId, knockVO.getKnockCount(), meritGained);

        // 5. 获取更新后的用户统计
        UserStats userStats = userStatsMapper.selectByUserId(userId);

        // 6. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("meritGained", meritGained);
        result.put("totalMerit", userStats.getTotalMerit());
        result.put("todayMerit", userStats.getTodayMerit());
        result.put("totalKnocks", userStats.getTotalKnocks());
        result.put("todayKnocks", userStats.getTodayKnocks());
        result.put("comboCount", knockVO.getComboCount() != null ? knockVO.getComboCount() : 0);
        result.put("maxCombo", userStats.getMaxCombo());

        return result;
    }

    @Override
    public Map<String, Object> startAutoKnock(Long userId, Integer duration, Integer knocksPerSecond) {
        // 1. 校验时长参数（支持-1表示无限时长）
        if (!ALLOWED_DURATIONS.contains(duration)) {
            throw new BusinessException("不支持的时长，请选择：-1(无限)、10秒、30秒、1分钟、5分钟或10分钟");
        }

        // 2. 校验每秒敲击次数参数
        if (knocksPerSecond == null || knocksPerSecond < MIN_KNOCKS_PER_SECOND || knocksPerSecond > MAX_KNOCKS_PER_SECOND) {
            throw new BusinessException("每秒敲击次数必须在" + MIN_KNOCKS_PER_SECOND + "到" + MAX_KNOCKS_PER_SECOND + "之间");
        }

        // 3. 检查是否已有活跃会话
        String existingSessionId = userActiveSession.get(userId);
        if (existingSessionId != null) {
            AutoKnockSession existingSession = autoKnockSessions.get(existingSessionId);
            if (existingSession != null && "active".equals(existingSession.getStatus())) {
                throw new BusinessException("您已有正在进行的自动敲击，请先停止后再开始新的");
            }
        }

        // 4. 创建自动敲击会话
        String sessionId = IdUtil.simpleUUID();
        AutoKnockSession session = new AutoKnockSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setStartTime(LocalDateTime.now());

        // 处理无限时长的情况
        if (duration == -1) {
            session.setEndTime(null); // 无限时长不设置结束时间
            session.setExpectedKnocks(-1); // 无限时长没有预期敲击次数
            session.setExpectedMerit(-1); // 无限时长没有预期功德值
        } else {
            session.setEndTime(LocalDateTime.now().plusSeconds(duration));
            session.setExpectedKnocks(duration * knocksPerSecond);
            session.setExpectedMerit(duration * knocksPerSecond * BASE_MERIT_PER_KNOCK);
        }

        session.setDuration(duration);
        session.setKnockPerSecond(knocksPerSecond);
        session.setBaseMeritPerKnock(BASE_MERIT_PER_KNOCK);
        session.setActualKnocks(0);
        session.setActualMerit(0);
        session.setStatus("active");
        session.setSettled(false);
        session.setLastHeartbeatTime(LocalDateTime.now()); // 初始化心跳时间
        session.setClientKnockCount(0); // 初始化客户端敲击数

        // 5. 存储会话
        autoKnockSessions.put(sessionId, session);
        userActiveSession.put(userId, sessionId);

        log.info("用户 {} 开始自动敲击，会话ID：{}，时长：{}秒，每秒敲击：{}次",
                userId, sessionId, duration, knocksPerSecond);

        // 6. 返回会话ID和预期收益
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("startTime", session.getStartTime());
        result.put("endTime", session.getEndTime());
        result.put("duration", duration);
        result.put("expectedKnocks", session.getExpectedKnocks());
        result.put("expectedMerit", session.getExpectedMerit());
        result.put("knockPerSecond", knocksPerSecond);

        return result;
    }

    @Override
    public Map<String, Object> stopAutoKnock(Long userId, String sessionId) {
        // 1. 校验会话ID
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

        // 2. 计算实际收益（使用会话中保存的每秒敲击次数）
        LocalDateTime now = LocalDateTime.now();
        long actualSeconds = ChronoUnit.SECONDS.between(session.getStartTime(), now);
        actualSeconds = Math.min(actualSeconds, session.getDuration()); // 不超过预定时长

        int actualKnocks = (int) (actualSeconds * session.getKnockPerSecond());
        int actualMerit = actualKnocks * BASE_MERIT_PER_KNOCK;

        session.setActualKnocks(actualKnocks);
        session.setActualMerit(actualMerit);
        session.setStatus("stopped");

        // 3. 更新用户统计
        if (actualMerit > 0 && !session.getSettled()) {
            // 创建敲击记录
            KnockVO knockVO = new KnockVO();
            knockVO.setUserId(userId);
            knockVO.setKnockCount(actualKnocks);
            knockVO.setSessionId(sessionId);

            // 调用service更新统计
            Integer totalMeritGained = meritService.manualKnock(knockVO);
            session.setActualMerit(totalMeritGained);
            session.setSettled(true);

            // 更新成就和任务进度
            checkAchievementAndTaskProgress(userId, actualKnocks, totalMeritGained);

            log.info("用户 {} 停止自动敲击，会话ID：{}，实际敲击：{}次，获得功德：{}",
                    userId, sessionId, actualKnocks, totalMeritGained);
        }

        // 4. 清理会话
        userActiveSession.remove(userId);

        // 5. 获取更新后的用户统计
        UserStats userStats = userStatsMapper.selectByUserId(userId);

        // 6. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("duration", actualSeconds);
        result.put("actualKnocks", actualKnocks);
        result.put("actualMerit", session.getActualMerit());
        result.put("totalMerit", userStats.getTotalMerit());
        result.put("todayMerit", userStats.getTodayMerit());

        return result;
    }

    @Override
    public Map<String, Object> getKnockStats(Long userId) {
        // 1. 查询用户统计
        UserStats userStats = userStatsMapper.selectByUserId(userId);
        if (userStats == null) {
            throw new BusinessException("用户统计信息不存在");
        }

        // 2. 构建统计数据
        Map<String, Object> stats = new HashMap<>();

        // 今日统计
        Map<String, Object> todayStats = new HashMap<>();
        todayStats.put("todayMerit", userStats.getTodayMerit());
        todayStats.put("todayKnocks", userStats.getTodayKnocks());
        stats.put("today", todayStats);

        // 历史统计
        Map<String, Object> historyStats = new HashMap<>();
        historyStats.put("totalMerit", userStats.getTotalMerit());
        historyStats.put("totalKnocks", userStats.getTotalKnocks());
        historyStats.put("weeklyMerit", userStats.getWeeklyMerit());
        historyStats.put("monthlyMerit", userStats.getMonthlyMerit());
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
                autoKnockInfo.put("endTime", activeSession.getEndTime());

                // 计算进度
                LocalDateTime now = LocalDateTime.now();
                long elapsedSeconds = ChronoUnit.SECONDS.between(activeSession.getStartTime(), now);
                long totalSeconds = activeSession.getDuration();
                double progress = Math.min(100.0, (elapsedSeconds * 100.0) / totalSeconds);
                autoKnockInfo.put("progress", progress);

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
        status.put("endTime", session.getEndTime());
        status.put("duration", session.getDuration());

        // 计算进度
        LocalDateTime now = LocalDateTime.now();
        long elapsedSeconds = ChronoUnit.SECONDS.between(session.getStartTime(), now);
        long totalSeconds = session.getDuration();

        // 计算当前预计收益（使用会话中保存的每秒敲击次数）
        int currentKnocks = (int) Math.min(elapsedSeconds * session.getKnockPerSecond(), session.getExpectedKnocks());
        int currentMerit = currentKnocks * BASE_MERIT_PER_KNOCK;

        status.put("elapsedSeconds", elapsedSeconds);
        status.put("remainingSeconds", Math.max(0, totalSeconds - elapsedSeconds));
        status.put("progress", Math.min(100.0, (elapsedSeconds * 100.0) / totalSeconds));
        status.put("currentKnocks", currentKnocks);
        status.put("currentMerit", currentMerit);
        status.put("expectedKnocks", session.getExpectedKnocks());
        status.put("expectedMerit", session.getExpectedMerit());
        status.put("knockPerSecond", session.getKnockPerSecond());

        // 判断是否即将完成
        if (now.isAfter(session.getEndTime())) {
            status.put("isCompleting", true);
            status.put("message", "自动敲击即将完成");
        } else {
            status.put("isCompleting", false);
            status.put("message", "自动敲击进行中");
        }

        return status;
    }

    @Override
    public void cleanupTimeoutSessions() {
        LocalDateTime now = LocalDateTime.now();

        autoKnockSessions.values().stream()
                .filter(session -> "active".equals(session.getStatus()))
                .filter(session -> {
                    // 统一以心跳超时1分钟为依据清理会话
                    LocalDateTime lastHeartbeat = session.getLastHeartbeatTime();
                    if (lastHeartbeat != null) {
                        return ChronoUnit.MINUTES.between(lastHeartbeat, now) >= 1;
                    }
                    // 如果没有心跳记录，检查是否创建超过1分钟
                    return ChronoUnit.MINUTES.between(session.getStartTime(), now) >= 1;
                })
                .forEach(session -> {
                    try {
                        // 自动结算 - 所有类型的敲击数量都以客户端传递的为依据
                        Integer actualKnocks = session.getClientKnockCount() != null ? session.getClientKnockCount() : 0;
                        Integer actualMerit = actualKnocks * session.getBaseMeritPerKnock();

                        session.setActualKnocks(actualKnocks);
                        session.setActualMerit(actualMerit);
                        session.setStatus("completed");

                        if (!session.getSettled()) {
                            // 创建敲击记录
                            KnockVO knockVO = new KnockVO();
                            knockVO.setUserId(session.getUserId());
                            knockVO.setKnockCount(actualKnocks);
                            knockVO.setSessionId(session.getSessionId());

                            // 更新统计
                            Integer totalMeritGained = meritService.manualKnock(knockVO);
                            session.setActualMerit(totalMeritGained);
                            session.setSettled(true);

                            // 更新成就和任务进度
                            checkAchievementAndTaskProgress(session.getUserId(),
                                    actualKnocks, totalMeritGained);

                            log.info("自动结算会话 {}，用户 {}，敲击 {} 次，获得功德 {}",
                                    session.getSessionId(), session.getUserId(),
                                    actualKnocks, totalMeritGained);
                        }

                        // 清理用户活跃会话
                        userActiveSession.remove(session.getUserId());
                    } catch (Exception e) {
                        log.error("自动结算会话失败：" + session.getSessionId(), e);
                    }
                });

        // 清理已完成超过1小时的会话
        autoKnockSessions.entrySet().removeIf(entry -> {
            AutoKnockSession session = entry.getValue();
            return !"active".equals(session.getStatus()) &&
                    now.isAfter(session.getEndTime().plusHours(1));
        });
    }

    @Override
    public Map<String, Object> startAutoKnock(Long userId, AutoKnockStartVO startVO) {
        // 调用旧版本方法保持兼容性
        return startAutoKnock(userId, startVO.getDuration(), startVO.getKnocksPerSecond());
    }

    @Override
    public Map<String, Object> stopAutoKnock(Long userId, AutoKnockStopVO stopVO) {
        String sessionId = stopVO.getSessionId();
        Integer clientKnockCount = stopVO.getKnockCount();

        // 1. 校验会话ID
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

        // 2. 使用客户端传递的敲击数据
        Integer actualKnocks = clientKnockCount != null ? clientKnockCount : 0;
        Integer actualMerit = actualKnocks * BASE_MERIT_PER_KNOCK;

        session.setActualKnocks(actualKnocks);
        session.setActualMerit(actualMerit);
        session.setClientKnockCount(actualKnocks);
        session.setStatus("stopped");

        // 3. 更新用户统计
        if (actualMerit > 0 && !session.getSettled()) {
            // 创建敲击记录
            KnockVO knockVO = new KnockVO();
            knockVO.setUserId(userId);
            knockVO.setKnockCount(actualKnocks);
            knockVO.setSessionId(sessionId);

            // 调用service更新统计
            Integer totalMeritGained = meritService.manualKnock(knockVO);
            session.setActualMerit(totalMeritGained);
            session.setSettled(true);

            // 更新成就和任务进度
            checkAchievementAndTaskProgress(userId, actualKnocks, totalMeritGained);

            log.info("用户 {} 停止自动敲击，会话ID：{}，客户端敲击：{}次，获得功德：{}",
                    userId, sessionId, actualKnocks, totalMeritGained);
        }

        // 4. 清理用户活跃会话记录
        userActiveSession.remove(userId);

        // 5. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("status", session.getStatus());
        result.put("actualKnocks", actualKnocks);
        result.put("actualMerit", session.getActualMerit());
        result.put("duration", stopVO.getActualDuration());

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

        // 处理剩余时间：如果是无限时长（duration为-1），则剩余时间为-1
        if (session.getDuration() == -1 || session.getEndTime() == null) {
            result.put("remainingTime", -1);
        } else {
            long remainingSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), session.getEndTime());
            result.put("remainingTime", Math.max(0, remainingSeconds));
        }

        result.put("expectedKnocks", session.getExpectedKnocks());
        result.put("currentKnocks", heartbeatVO.getCurrentKnockCount());

        return result;
    }

    @Override
    @Deprecated
    public void checkAndSettleExpiredSessions() {
        cleanupTimeoutSessions();
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