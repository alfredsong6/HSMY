package com.hsmy.controller.knock;

import cn.hutool.core.util.IdUtil;
import com.hsmy.common.Result;
import com.hsmy.entity.AutoKnockSession;
import com.hsmy.entity.UserStats;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.AchievementService;
import com.hsmy.service.MeritService;
import com.hsmy.service.TaskService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.KnockVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 敲击功能Controller
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Slf4j
@RestController
@RequestMapping("/knock")
@RequiredArgsConstructor
public class KnockController {
    
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
    private static final Set<Integer> ALLOWED_DURATIONS = new HashSet<>(Arrays.asList(10, 30, 60, 300, 600));
    // 每秒敲击次数
    private static final int KNOCKS_PER_SECOND = 3;
    // 每次敲击基础功德值
    private static final int BASE_MERIT_PER_KNOCK = 1;
    
    /**
     * 手动敲击
     * 
     * @param knockVO 敲击参数
     * @param request HTTP请求
     * @return 敲击结果
     */
    @PostMapping("/manual")
    public Result<Map<String, Object>> manualKnock(@RequestBody KnockVO knockVO, HttpServletRequest request) {
        // 1. 获取用户ID
        Long userId = UserContextUtil.requireCurrentUserId();
        knockVO.setUserId(userId);
        
        // 2. 校验敲击频率限制
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastKnockTime = lastKnockTimeMap.get(userId);
        if (lastKnockTime != null) {
            long intervalMs = ChronoUnit.MILLIS.between(lastKnockTime, now);
            if (intervalMs < MIN_KNOCK_INTERVAL_MS) {
                throw new BusinessException("敲击太快了，请稍后再试");
            }
        }
        lastKnockTimeMap.put(userId, now);
        
        // 3. 计算功德值收益（基础+连击加成）
        Integer meritGained = meritService.manualKnock(knockVO);
        
        // 4. 更新用户统计数据（已在meritService中处理）
        
        // 5. 记录敲击日志
        log.info("用户 {} 手动敲击，获得功德值：{}, 连击数：{}", 
                userId, meritGained, knockVO.getComboCount());
        
        // 6. 异步检查成就和任务进度
        checkAchievementAndTaskProgress(userId, knockVO.getKnockCount(), meritGained);
        
        // 获取更新后的用户统计
        UserStats userStats = userStatsMapper.selectByUserId(userId);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("meritGained", meritGained);
        result.put("totalMerit", userStats.getTotalMerit());
        result.put("todayMerit", userStats.getTodayMerit());
        result.put("totalKnocks", userStats.getTotalKnocks());
        result.put("todayKnocks", userStats.getTodayKnocks());
        result.put("comboCount", knockVO.getComboCount() != null ? knockVO.getComboCount() : 0);
        result.put("maxCombo", userStats.getMaxCombo());
        
        return Result.success("敲击成功", result);
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
    
    /**
     * 开始自动敲击
     * 
     * @param duration 敲击时长（秒）
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/auto/start")
    public Result<Map<String, Object>> startAutoKnock(@RequestParam Integer duration, HttpServletRequest request) {
        try {
            // 1. 获取用户ID
            Long userId = UserContextUtil.requireCurrentUserId();
            
            // 2. 校验时长参数（10秒、30秒、1分钟、5分钟、10分钟）
            if (!ALLOWED_DURATIONS.contains(duration)) {
                return Result.error("不支持的时长，请选择：10秒、30秒、1分钟、5分钟或10分钟");
            }
            
            // 检查是否已有活跃会话
            String existingSessionId = userActiveSession.get(userId);
            if (existingSessionId != null) {
                AutoKnockSession existingSession = autoKnockSessions.get(existingSessionId);
                if (existingSession != null && "active".equals(existingSession.getStatus())) {
                    return Result.error("您已有正在进行的自动敲击，请先停止后再开始新的");
                }
            }
            
            // 3. 创建自动敲击会话
            String sessionId = IdUtil.simpleUUID();
            AutoKnockSession session = new AutoKnockSession();
            session.setSessionId(sessionId);
            session.setUserId(userId);
            session.setStartTime(LocalDateTime.now());
            session.setEndTime(LocalDateTime.now().plusSeconds(duration));
            session.setDuration(duration);
            session.setKnockPerSecond(KNOCKS_PER_SECOND);
            session.setBaseMeritPerKnock(BASE_MERIT_PER_KNOCK);
            session.setExpectedKnocks(duration * KNOCKS_PER_SECOND);
            session.setExpectedMerit(duration * KNOCKS_PER_SECOND * BASE_MERIT_PER_KNOCK);
            session.setActualKnocks(0);
            session.setActualMerit(0);
            session.setStatus("active");
            session.setSettled(false);
            
            // 存储会话
            autoKnockSessions.put(sessionId, session);
            userActiveSession.put(userId, sessionId);
            
            log.info("用户 {} 开始自动敲击，会话ID：{}，时长：{}秒", userId, sessionId, duration);
            
            // 4. 返回会话ID和预期收益
            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("startTime", session.getStartTime());
            result.put("endTime", session.getEndTime());
            result.put("duration", duration);
            result.put("expectedKnocks", session.getExpectedKnocks());
            result.put("expectedMerit", session.getExpectedMerit());
            result.put("knockPerSecond", KNOCKS_PER_SECOND);
            
            return Result.success("自动敲击已开始", result);
        } catch (Exception e) {
            log.error("开始自动敲击失败", e);
            return Result.error("开始自动敲击失败：" + e.getMessage());
        }
    }
    
    /**
     * 停止自动敲击
     * 
     * @param sessionId 会话ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/auto/stop")
    public Result<Map<String, Object>> stopAutoKnock(@RequestParam String sessionId, HttpServletRequest request) {
        try {
            // 1. 获取用户ID
            Long userId = UserContextUtil.requireCurrentUserId();
            
            // 2. 校验会话ID
            AutoKnockSession session = autoKnockSessions.get(sessionId);
            if (session == null) {
                return Result.error("会话不存在");
            }
            
            if (!session.getUserId().equals(userId)) {
                return Result.error("无权操作此会话");
            }
            
            if (!"active".equals(session.getStatus())) {
                return Result.error("会话已结束");
            }
            
            // 3. 计算实际收益
            LocalDateTime now = LocalDateTime.now();
            long actualSeconds = ChronoUnit.SECONDS.between(session.getStartTime(), now);
            actualSeconds = Math.min(actualSeconds, session.getDuration()); // 不超过预定时长
            
            int actualKnocks = (int) (actualSeconds * KNOCKS_PER_SECOND);
            int actualMerit = actualKnocks * BASE_MERIT_PER_KNOCK;
            
            session.setActualKnocks(actualKnocks);
            session.setActualMerit(actualMerit);
            session.setStatus("stopped");
            
            // 4. 更新用户统计
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
            
            // 5. 清理会话
            userActiveSession.remove(userId);
            
            // 获取更新后的用户统计
            UserStats userStats = userStatsMapper.selectByUserId(userId);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("duration", actualSeconds);
            result.put("actualKnocks", actualKnocks);
            result.put("actualMerit", session.getActualMerit());
            result.put("totalMerit", userStats.getTotalMerit());
            result.put("todayMerit", userStats.getTodayMerit());
            
            return Result.success("自动敲击已停止", result);
        } catch (Exception e) {
            log.error("停止自动敲击失败", e);
            return Result.error("停止自动敲击失败：" + e.getMessage());
        }
    }
    
    /**
     * 定时检查并自动结算超时的会话
     */
    @Scheduled(fixedDelay = 5000) // 每5秒检查一次
    public void checkAndSettleExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        
        autoKnockSessions.values().stream()
            .filter(session -> "active".equals(session.getStatus()))
            .filter(session -> now.isAfter(session.getEndTime()))
            .forEach(session -> {
                try {
                    // 自动结算
                    session.setActualKnocks(session.getExpectedKnocks());
                    session.setActualMerit(session.getExpectedMerit());
                    session.setStatus("completed");
                    
                    if (!session.getSettled()) {
                        // 创建敲击记录
                        KnockVO knockVO = new KnockVO();
                        knockVO.setUserId(session.getUserId());
                        knockVO.setKnockCount(session.getExpectedKnocks());
                        knockVO.setSessionId(session.getSessionId());
                        
                        // 更新统计
                        Integer totalMeritGained = meritService.manualKnock(knockVO);
                        session.setActualMerit(totalMeritGained);
                        session.setSettled(true);
                        
                        // 更新成就和任务进度
                        checkAchievementAndTaskProgress(session.getUserId(), 
                                session.getExpectedKnocks(), totalMeritGained);
                        
                        log.info("自动结算会话 {}，用户 {}，敲击 {} 次，获得功德 {}", 
                                session.getSessionId(), session.getUserId(), 
                                session.getExpectedKnocks(), totalMeritGained);
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
    
    /**
     * 获取敲击统计
     * 
     * @param request HTTP请求
     * @return 统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getKnockStats(HttpServletRequest request) {
        try {
            // 1. 获取用户ID
            Long userId = UserContextUtil.requireCurrentUserId();
            
            // 2. 查询用户统计
            UserStats userStats = userStatsMapper.selectByUserId(userId);
            if (userStats == null) {
                return Result.error("用户统计信息不存在");
            }
            
            // 3. 构建统计数据
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
            
            return Result.success("获取统计成功", stats);
        } catch (Exception e) {
            log.error("获取敲击统计失败", e);
            return Result.error("获取统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取当前自动敲击状态
     * 
     * @param request HTTP请求
     * @return 自动敲击状态
     */
    @GetMapping("/auto/status")
    public Result<Map<String, Object>> getAutoKnockStatus(HttpServletRequest request) {
        try {
            // 1. 获取用户ID
            Long userId = UserContextUtil.requireCurrentUserId();
            
            // 2. 查询当前活跃的自动敲击会话
            String sessionId = userActiveSession.get(userId);
            
            Map<String, Object> status = new HashMap<>();
            
            if (sessionId == null) {
                status.put("hasActiveSession", false);
                status.put("message", "当前没有进行中的自动敲击");
                return Result.success("获取状态成功", status);
            }
            
            AutoKnockSession session = autoKnockSessions.get(sessionId);
            if (session == null || !"active".equals(session.getStatus())) {
                // 清理无效的映射
                userActiveSession.remove(userId);
                status.put("hasActiveSession", false);
                status.put("message", "当前没有进行中的自动敲击");
                return Result.success("获取状态成功", status);
            }
            
            // 3. 返回会话信息和进度
            status.put("hasActiveSession", true);
            status.put("sessionId", sessionId);
            status.put("startTime", session.getStartTime());
            status.put("endTime", session.getEndTime());
            status.put("duration", session.getDuration());
            
            // 计算进度
            LocalDateTime now = LocalDateTime.now();
            long elapsedSeconds = ChronoUnit.SECONDS.between(session.getStartTime(), now);
            long totalSeconds = session.getDuration();
            
            // 计算当前预计收益
            int currentKnocks = (int) Math.min(elapsedSeconds * KNOCKS_PER_SECOND, session.getExpectedKnocks());
            int currentMerit = currentKnocks * BASE_MERIT_PER_KNOCK;
            
            status.put("elapsedSeconds", elapsedSeconds);
            status.put("remainingSeconds", Math.max(0, totalSeconds - elapsedSeconds));
            status.put("progress", Math.min(100.0, (elapsedSeconds * 100.0) / totalSeconds));
            status.put("currentKnocks", currentKnocks);
            status.put("currentMerit", currentMerit);
            status.put("expectedKnocks", session.getExpectedKnocks());
            status.put("expectedMerit", session.getExpectedMerit());
            status.put("knockPerSecond", KNOCKS_PER_SECOND);
            
            // 判断是否即将完成
            if (now.isAfter(session.getEndTime())) {
                status.put("isCompleting", true);
                status.put("message", "自动敲击即将完成");
            } else {
                status.put("isCompleting", false);
                status.put("message", "自动敲击进行中");
            }
            
            return Result.success("获取状态成功", status);
        } catch (Exception e) {
            log.error("获取自动敲击状态失败", e);
            return Result.error("获取状态失败：" + e.getMessage());
        }
    }
}