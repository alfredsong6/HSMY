package com.hsmy.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hsmy.entity.UserStats;
import com.hsmy.entity.meditation.MeditationDailyStats;
import com.hsmy.entity.meditation.MeditationSession;
import com.hsmy.entity.meditation.MeditationSubscription;
import com.hsmy.entity.meditation.MeditationUserPref;
import com.hsmy.entity.meditation.MeritCoinTransaction;
import com.hsmy.entity.MeritRecord;
import com.hsmy.enums.MeditationSessionStatusEnum;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.mapper.meditation.MeditationDailyStatsMapper;
import com.hsmy.mapper.meditation.MeditationSessionMapper;
import com.hsmy.mapper.meditation.MeditationSubscriptionMapper;
import com.hsmy.mapper.meditation.MeditationUserPrefMapper;
import com.hsmy.mapper.meditation.MeritCoinTransactionMapper;
import com.hsmy.mapper.MeritRecordMapper;
import com.hsmy.service.UserPeriodStatsService;
import com.hsmy.service.MeditationService;
import com.hsmy.utils.UserLockManager;
import com.hsmy.vo.meditation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeditationServiceImpl implements MeditationService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final MeditationSessionMapper meditationSessionMapper;
    private final MeditationDailyStatsMapper meditationDailyStatsMapper;
    private final MeditationSubscriptionMapper meditationSubscriptionMapper;
    private final MeditationUserPrefMapper meditationUserPrefMapper;
    private final MeritCoinTransactionMapper meritCoinTransactionMapper;
    private final MeritRecordMapper meritRecordMapper;
    private final UserPeriodStatsService userPeriodStatsService;
    private final UserStatsMapper userStatsMapper;
    private final UserLockManager userLockManager;

    private static final Map<String, SubscriptionPlan> PLAN_MAP = new HashMap<>();

    static {
        PLAN_MAP.put("DAY", new SubscriptionPlan("DAY", 1, 1));
        PLAN_MAP.put("WEEK", new SubscriptionPlan("WEEK", 7, 5));
        PLAN_MAP.put("MONTH", new SubscriptionPlan("MONTH", 30, 15));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MeditationSubscriptionStatusVO purchaseSubscription(Long userId, MeditationSubscriptionPurchaseVO purchaseVO) {
        return userLockManager.executeWithUserLock(userId, () -> purchaseSubscriptionInternal(userId, purchaseVO));
    }

    private MeditationSubscriptionStatusVO purchaseSubscriptionInternal(Long userId, MeditationSubscriptionPurchaseVO purchaseVO) {
        String planKey = normalizePlanType(purchaseVO.getPlanType());
        SubscriptionPlan plan = PLAN_MAP.get(planKey);
        if (plan == null) {
            throw new BusinessException("不支持的订阅类型");
        }

        Date now = new Date();
        meditationSubscriptionMapper.expireSubscriptions(now);

        MeditationSubscription active = meditationSubscriptionMapper.selectActiveByUser(userId, now);
        LocalDateTime baseStart = LocalDateTime.ofInstant(now.toInstant(), ZoneId.systemDefault());
        if (active != null && active.getEndTime() != null) {
            LocalDateTime activeEnd = LocalDateTime.ofInstant(active.getEndTime().toInstant(), ZoneId.systemDefault());
            if (activeEnd.isAfter(baseStart)) {
                baseStart = activeEnd;
            }
        }
        LocalDateTime newEnd = baseStart.plusDays(plan.getDays());

        // 扣功德币
        deductCoins(userId, plan.getCoins());
        long remainingCoins = queryRemainingCoins(userId);

        MeditationSubscription subscription = new MeditationSubscription();
        subscription.setUserId(userId);
        subscription.setPlanType(plan.getPlanType());
        subscription.setStartTime(toDate(baseStart));
        subscription.setEndTime(toDate(newEnd));
        subscription.setStatus("CURRENT");
        subscription.setCoinCost(plan.getCoins());
        meditationSubscriptionMapper.insert(subscription);

        MeritCoinTransaction tx = new MeritCoinTransaction();
        tx.setUserId(userId);
        tx.setBizType("MEDITATION_SUBSCRIBE");
        tx.setBizId(subscription.getId());
        tx.setChangeAmount(-plan.getCoins());
        tx.setBalanceAfter((int) remainingCoins);
        tx.setRemark(String.format("购买冥想%s订阅", plan.getPlanType()));
        meritCoinTransactionMapper.insert(tx);

        subscription.setOrderId(tx.getId());
        meditationSubscriptionMapper.updateById(subscription);

        MeditationSubscriptionStatusVO statusVO = buildSubscriptionStatusVO(subscription, remainingCoins);
        log.info("用户 {} 购买冥想订阅：{}，到期时间 {}", userId, planKey, subscription.getEndTime());
        return statusVO;
    }

    @Override
    public MeditationSubscriptionStatusVO getSubscriptionStatus(Long userId) {
        Date now = new Date();
        meditationSubscriptionMapper.expireSubscriptions(now);
        MeditationSubscription active = meditationSubscriptionMapper.selectActiveByUser(userId, now);
        long remainingCoins = queryRemainingCoins(userId);
        if (active != null) {
            return buildSubscriptionStatusVO(active, remainingCoins);
        }
        MeditationSubscription latest = meditationSubscriptionMapper.selectLatestByUser(userId);
        if (latest == null) {
            MeditationSubscriptionStatusVO empty = new MeditationSubscriptionStatusVO();
            empty.setActive(false);
            empty.setRemainingCoins((int) remainingCoins);
            return empty;
        }
        MeditationSubscriptionStatusVO status = buildSubscriptionStatusVO(latest, remainingCoins);
        status.setActive(false);
        status.setRemainingSeconds(0);
        return status;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MeditationSessionStartResponse startSession(Long userId, MeditationSessionStartVO startVO) {
        return userLockManager.executeWithUserLock(userId, () -> startSessionInternal(userId, startVO));
    }

    private MeditationSessionStartResponse startSessionInternal(Long userId, MeditationSessionStartVO startVO) {
        //validatePlanActive(userId);

        int withKnock = startVO.getWithKnock() != null && startVO.getWithKnock() == 1 ? 1 : 0;
        Integer knockFrequency = startVO.getKnockFrequency();
        if (withKnock == 1) {
            if (knockFrequency == null || knockFrequency < 60 || knockFrequency > 100) {
                throw new BusinessException("敲击频率需在60-100之间");
            }
        } else {
            knockFrequency = null;
        }

        MeditationSession session = new MeditationSession();
        session.setSessionId(IdUtil.simpleUUID());
        session.setUserId(userId);
        session.setPlannedDuration(startVO.getPlannedDuration());
        session.setActualDuration(null);
        session.setStartTime(startVO.getStartTime());
        session.setStatus(MeditationSessionStatusEnum.STARTED.name());
        session.setLastHeartbeatTime(new Date());
        session.setWithKnock(withKnock);
        session.setKnockFrequency(knockFrequency);
        session.setSaveFlag(1);
        session.setCoinCost(0);
        session.setCoinRefunded(0);
        session.setPaymentStatus("SETTLED");
        //session.setConfigSnapshot(buildConfigSnapshot(startVO));
        meditationSessionMapper.insert(session);

        // 更新默认配置
        //updatePreferenceInternal(userId, startVO.getPlannedDuration(), withKnock, knockFrequency);

        MeditationSessionStartResponse response = new MeditationSessionStartResponse();
        response.setSessionId(session.getSessionId());
        response.setStartTime(session.getStartTime());
        response.setPlannedDuration(startVO.getPlannedDuration());
        response.setWithKnock(withKnock);
        response.setKnockFrequency(knockFrequency);
        response.setRemainingCoins((int) queryRemainingCoins(userId));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MeditationSessionFinishResponse finishSession(Long userId, MeditationSessionFinishVO finishVO) {
        return userLockManager.executeWithUserLock(userId, () -> finishSessionInternal(userId, finishVO));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pingSession(Long userId, MeditationSessionPingVO pingVO) {
        userLockManager.executeWithUserLock(userId, () -> {
            MeditationSession session = findSessionBySessionId(userId, pingVO.getSessionId());
            if (session.getEndTime() != null) {
                throw new BusinessException("会话已结束");
            }
            session.setStatus(MeditationSessionStatusEnum.STARTED.name());
            session.setLastHeartbeatTime(new Date());
            meditationSessionMapper.updateById(session);
            return null;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer settleAbnormalSessions(Long userId) {
        return userLockManager.executeWithUserLock(userId, () -> settleAbnormalSessionsInternal(userId));
    }

    private Integer settleAbnormalSessionsInternal(Long userId) {
        Date threshold = new Date(System.currentTimeMillis() - 60_000L);
        LambdaQueryWrapper<MeditationSession> query = new LambdaQueryWrapper<>();
        query.eq(MeditationSession::getUserId, userId)
                .eq(MeditationSession::getIsDeleted, 0)
                .eq(MeditationSession::getStatus, MeditationSessionStatusEnum.STARTED.name())
                .lt(MeditationSession::getLastHeartbeatTime, threshold)
                .isNotNull(MeditationSession::getLastHeartbeatTime);
        List<MeditationSession> staleSessions = meditationSessionMapper.selectList(query);
        if (staleSessions.isEmpty()) {
            return 0;
        }
        int settled = 0;
        for (MeditationSession session : staleSessions) {
            Date lastHeartbeat = session.getLastHeartbeatTime();
            Date startTime = session.getStartTime();
            if (lastHeartbeat == null || startTime == null) {
                continue;
            }
            long seconds = Math.max(1L, (lastHeartbeat.getTime() - startTime.getTime()) / 1000);
            session.setActualDuration((int) seconds);
            session.setEndTime(lastHeartbeat);
            session.setStatus(MeditationSessionStatusEnum.INTERRUPTED.name());
            meditationSessionMapper.updateById(session);
            settled++;
        }
        return settled;
    }

    private MeditationSessionFinishResponse finishSessionInternal(Long userId, MeditationSessionFinishVO finishVO) {
        MeditationSession session = findSessionBySessionId(userId, finishVO.getSessionId());
        if (session.getEndTime() != null) {
            throw new BusinessException("会话已结束");
        }

        MeditationSessionStatusEnum finishStatus = resolveFinishStatus(finishVO);
        Date now = new Date();
        session.setActualDuration(finishVO.getActualDuration());
        session.setEndTime(now);
        session.setStatus(finishStatus.name());
        meditationSessionMapper.updateById(session);

        MeditationDailyStats todayStats = null;
        Integer meritGained = null;
        if (finishStatus == MeditationSessionStatusEnum.COMPLETED) {
            todayStats = upsertDailyStats(userId, now, null, null, finishVO.getActualDuration());
            meritGained = settleMeditationMerit(userId, session, finishVO.getActualDuration(), now);
        }

        MeditationSessionFinishResponse response = new MeditationSessionFinishResponse();
        response.setSessionId(session.getSessionId());
        response.setActualDuration(session.getActualDuration());
        response.setMeritGained(meritGained);
        response.setEndTime(now);
        if (todayStats != null) {
            response.setTodaySessionCount(todayStats.getSessionCount());
            response.setTodayTotalMinutes(todayStats.getTotalMinutes());
        }
//        response.setTotalSessionCount(meditationSessionMapper.countTotalSessions(userId));
//        response.setTotalMinutes(secondsToMinutes(meditationSessionMapper.sumTotalDuration(userId)));
        response.setRemainingCoins((int) queryRemainingCoins(userId));
        return response;
    }

    private int settleMeditationMerit(Long userId, MeditationSession session, Integer actualDuration, Date endTime) {
        int minutes = secondsToMinutes(actualDuration);
        if (minutes <= 0) {
            return 0;
        }
        MeritRecord record = new MeritRecord();
        record.setUserId(userId);
        record.setMeritGained(minutes);
        record.setBaseMerit(minutes);
        record.setSource("meditation");
        record.setSessionId(session.getSessionId());
        record.setStatDate(java.sql.Date.valueOf(toLocalDate(endTime)));
        record.setDescription("冥想完成");
        meritRecordMapper.insert(record);

        //userPeriodStatsService.recordKnock(userId, 0L, minutes, 0, endTime);
        userStatsMapper.addMerit(userId, (long) minutes);
        return minutes;
    }

    private MeditationSessionStatusEnum resolveFinishStatus(MeditationSessionFinishVO finishVO) {
        try {
            MeditationSessionStatusEnum parsed = MeditationSessionStatusEnum.fromValue(finishVO.getStatus());
            if (parsed == null || parsed == MeditationSessionStatusEnum.STARTED) {
                return MeditationSessionStatusEnum.COMPLETED;
            }
            return parsed;
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("会话状态非法");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void discardSession(Long userId, MeditationSessionDiscardVO discardVO) {
        userLockManager.executeWithUserLock(userId, () -> {
            MeditationSession session = findSessionBySessionId(userId, discardVO.getSessionId());
            session.setSaveFlag(0);
            session.setStatus(MeditationSessionStatusEnum.INTERRUPTED.name());
            session.setEndTime(new Date());
            meditationSessionMapper.updateById(session);
            return null;
        });
    }

    @Override
    public MeditationStatsSummaryVO getStatsSummary(Long userId) {
        Date now = new Date();
        Date start = toDate(LocalDate.now().atStartOfDay());
        Date end = toDate(LocalDate.now().atTime(LocalTime.MAX));
        int todaySessions = Optional.ofNullable(meditationSessionMapper.countByUserAndDate(userId, start, end)).orElse(0);
        int todayDuration = secondsToMinutes(meditationSessionMapper.sumDurationByUserAndDate(userId, start, end));
        int totalSessions = Optional.ofNullable(meditationSessionMapper.countTotalSessions(userId)).orElse(0);
        int totalDuration = secondsToMinutes(meditationSessionMapper.sumTotalDuration(userId));

        MeditationStatsSummaryVO summaryVO = new MeditationStatsSummaryVO();
        summaryVO.setTodayCount(todaySessions);
        summaryVO.setTodayMinutes(todayDuration);
        summaryVO.setTotalCount(totalSessions);
        summaryVO.setTotalMinutes(totalDuration);
        return summaryVO;
    }

    @Override
    public List<MeditationMonthViewVO> getMonthStats(Long userId, String month) {
        LocalDate targetMonth = StringUtils.hasText(month) ? LocalDate.parse(month, MONTH_FORMATTER) : LocalDate.now();
        LocalDate startDate = targetMonth.withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        Date start = toDate(startDate.atStartOfDay());
        Date end = toDate(endDate.atTime(LocalTime.MAX));

        List<MeditationMonthStatDTO> rows = meditationSessionMapper.selectMonthStats(userId, start, end);
        List<MeditationMonthViewVO> result = new ArrayList<>();
        for (MeditationMonthStatDTO row : rows) {
            MeditationMonthViewVO vo = new MeditationMonthViewVO();
            vo.setStatDate(row.getStatDate());
            vo.setSessionCount(Optional.ofNullable(row.getSessionCount()).orElse(0));
            vo.setTotalMinutes(Optional.ofNullable(row.getTotalMinutes()).orElse(0));
            vo.setMood(row.getLastMood());
            vo.setInsight(row.getLastInsight());
            result.add(vo);
        }
        return result;
    }

    @Override
    public MeditationUserPrefVO getUserPreference(Long userId) {
        MeditationUserPref pref = meditationUserPrefMapper.selectById(userId);
        if (pref == null) {
            MeditationUserPrefVO vo = new MeditationUserPrefVO();
            vo.setDefaultDuration(600);
            vo.setDefaultWithKnock(0);
            vo.setDefaultKnockFrequency(60);
            return vo;
        }
        MeditationUserPrefVO vo = new MeditationUserPrefVO();
        vo.setDefaultDuration(pref.getDefaultDuration());
        vo.setDefaultWithKnock(pref.getDefaultWithKnock());
        vo.setDefaultKnockFrequency(pref.getDefaultKnockFrequency());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MeditationUserPrefVO updateUserPreference(Long userId, MeditationUserPrefVO prefVO) {
        updatePreferenceInternal(userId,
                prefVO.getDefaultDuration(),
                prefVO.getDefaultWithKnock(),
                prefVO.getDefaultKnockFrequency());
        return getUserPreference(userId);
    }

    private MeditationSubscriptionStatusVO buildSubscriptionStatusVO(MeditationSubscription subscription, long remainingCoins) {
        MeditationSubscriptionStatusVO vo = new MeditationSubscriptionStatusVO();
        vo.setActive("CURRENT".equalsIgnoreCase(subscription.getStatus())
                && subscription.getEndTime() != null
                && subscription.getEndTime().after(new Date()));
        vo.setPlanType(subscription.getPlanType());
        vo.setStartTime(subscription.getStartTime());
        vo.setEndTime(subscription.getEndTime());
        vo.setRemainingCoins((int) remainingCoins);
        if (subscription.getEndTime() != null) {
            long seconds = Math.max(0, (subscription.getEndTime().getTime() - System.currentTimeMillis()) / 1000);
            vo.setRemainingSeconds(seconds);
        }
        return vo;
    }

    private void validatePlanActive(Long userId) {
        Date now = new Date();
        meditationSubscriptionMapper.expireSubscriptions(now);
        MeditationSubscription active = meditationSubscriptionMapper.selectActiveByUser(userId, now);
        if (active == null || active.getEndTime() == null || active.getEndTime().before(new Date())) {
            throw new BusinessException("冥想订阅已失效，请先购买订阅");
        }
    }

    private MeditationSession findSessionBySessionId(Long userId, String sessionId) {
        LambdaQueryWrapper<MeditationSession> query = new LambdaQueryWrapper<>();
        query.eq(MeditationSession::getSessionId, sessionId)
                .eq(MeditationSession::getUserId, userId)
                .eq(MeditationSession::getIsDeleted, 0)
                .last("LIMIT 1");
        MeditationSession session = meditationSessionMapper.selectOne(query);
        if (session == null) {
            throw new BusinessException("会话不存在");
        }
        return session;
    }

    private MeditationDailyStats upsertDailyStats(Long userId, Date now, String mood, String insight, Integer actualDurationSeconds) {
        LocalDate statDate = toLocalDate(now);
        MeditationDailyStats stats = meditationDailyStatsMapper.selectByUserAndDate(userId, java.sql.Date.valueOf(statDate));
        int minutes = secondsToMinutes(actualDurationSeconds);
        if (stats == null) {
            stats = new MeditationDailyStats();
            stats.setUserId(userId);
            stats.setStatDate(java.sql.Date.valueOf(statDate));
            stats.setSessionCount(1);
            stats.setTotalMinutes(minutes);
            stats.setLastMood(mood);
            stats.setLastInsight(insight);
            meditationDailyStatsMapper.insert(stats);
        } else {
            stats.setSessionCount(stats.getSessionCount() + 1);
            stats.setTotalMinutes(stats.getTotalMinutes() + minutes);
            stats.setLastMood(mood);
            stats.setLastInsight(insight);
            meditationDailyStatsMapper.updateById(stats);
        }
        return stats;
    }

    private void updatePreferenceInternal(Long userId, Integer duration, Integer withKnock, Integer frequency) {
        MeditationUserPref pref = meditationUserPrefMapper.selectById(userId);
        boolean insert = false;
        if (pref == null) {
            pref = new MeditationUserPref();
            pref.setUserId(userId);
            insert = true;
        }
        if (duration != null) {
            pref.setDefaultDuration(duration);
        }
        if (withKnock != null) {
            pref.setDefaultWithKnock(withKnock);
        }
        if (withKnock != null && withKnock == 1 && frequency != null) {
            pref.setDefaultKnockFrequency(frequency);
        } else if (withKnock != null && withKnock == 0) {
            pref.setDefaultKnockFrequency(null);
        }
        pref.setLastUpdateTime(new Date());
        if (insert) {
            meditationUserPrefMapper.insert(pref);
        } else {
            meditationUserPrefMapper.updateById(pref);
        }
    }

    private String buildConfigSnapshot(MeditationSessionStartVO startVO) {
        Map<String, Object> map = new HashMap<>();
        map.put("plannedDuration", startVO.getPlannedDuration());
        map.put("withKnock", startVO.getWithKnock());
        map.put("knockFrequency", startVO.getKnockFrequency());
        return com.alibaba.fastjson.JSON.toJSONString(map);
    }

    private String buildConfigSnapshot(Integer duration, Integer withKnock, Integer frequency) {
        Map<String, Object> map = new HashMap<>();
        map.put("plannedDuration", duration);
        map.put("withKnock", withKnock);
        map.put("knockFrequency", frequency);
        return com.alibaba.fastjson.JSON.toJSONString(map);
    }

    private int secondsToMinutes(Integer seconds) {
        if (seconds == null || seconds <= 0) {
            return 0;
        }
        return BigDecimal.valueOf(seconds)
                .divide(BigDecimal.valueOf(60), 0, RoundingMode.UP)
                .intValue();
    }

    private LocalDate toLocalDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    private Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private long queryRemainingCoins(Long userId) {
        UserStats stats = userStatsMapper.selectByUserId(userId);
        return stats != null && stats.getMeritCoins() != null ? stats.getMeritCoins() : 0L;
    }

    private void deductCoins(Long userId, int amount) {
        if (amount <= 0) {
            return;
        }
        int updated = userStatsMapper.reduceMeritCoins(userId, (long) amount);
        if (updated <= 0) {
            throw new BusinessException("功德币余额不足");
        }
    }

    private String normalizePlanType(String planType) {
        return planType == null ? null : planType.trim().toUpperCase(Locale.ROOT);
    }

    private static class SubscriptionPlan {
        private final String planType;
        private final int days;
        private final int coins;

        SubscriptionPlan(String planType, int days, int coins) {
            this.planType = planType;
            this.days = days;
            this.coins = coins;
        }

        public String getPlanType() {
            return planType;
        }

        public int getDays() {
            return days;
        }

        public int getCoins() {
            return coins;
        }
    }
}
