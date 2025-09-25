package com.hsmy.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.entity.ExchangeRecord;
import com.hsmy.entity.MeritRecord;
import com.hsmy.entity.UserPeriodStats;
import com.hsmy.entity.UserStats;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.ExchangeRecordMapper;
import com.hsmy.mapper.MeritRecordMapper;
import com.hsmy.enums.PeriodType;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.MeritService;
import com.hsmy.service.UserPeriodStatsService;
import com.hsmy.utils.IdGenerator;
import com.hsmy.utils.UserLockManager;
import com.hsmy.vo.ExchangeVO;
import com.hsmy.vo.KnockVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

/**
 * 功德Service实现类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Service
@RequiredArgsConstructor
public class MeritServiceImpl implements MeritService {
    
    private final MeritRecordMapper meritRecordMapper;
    private final UserStatsMapper userStatsMapper;
    private final ExchangeRecordMapper exchangeRecordMapper;
    private final UserPeriodStatsService userPeriodStatsService;
    private final UserLockManager userLockManager;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer manualKnock(KnockVO knockVO) {
        // 使用用户锁确保同一用户的功德累计操作串行化
        return userLockManager.executeWithUserLock(knockVO.getUserId(), () -> {
            return executeKnockOperation(knockVO);
        });
    }

    /**
     * 执行敲击操作的核心逻辑（已被锁保护）
     */
    private Integer executeKnockOperation(KnockVO knockVO) {
        Date knockTime = knockVO.getKnockTime() != null ? knockVO.getKnockTime() : new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(knockTime);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date hourStart = cal.getTime();

        cal.add(Calendar.HOUR_OF_DAY, 1);
        cal.add(Calendar.MILLISECOND, -1);
        Date hourEnd = cal.getTime();

        int knockCount = knockVO.getKnockCount() != null && knockVO.getKnockCount() > 0 ? knockVO.getKnockCount() : 1;
        int perKnockMerit = knockVO.getMeritValue() != null && knockVO.getMeritValue() > 0 ? knockVO.getMeritValue() : 1;
        int baseMerit = knockCount * perKnockMerit;

        Double multiplier = knockVO.getMultiplier() != null && knockVO.getMultiplier() > 0 ? knockVO.getMultiplier() : 1.0;
        BigDecimal bonusRate = BigDecimal.valueOf(multiplier);
        int totalMerit = bonusRate.multiply(BigDecimal.valueOf(baseMerit)).setScale(0, RoundingMode.FLOOR).intValue();

        String knockMode = knockVO.getKnockMode() != null ? knockVO.getKnockMode().toUpperCase(Locale.ROOT) : "MANUAL";
        String knockType = knockMode.startsWith("AUTO") ? "auto" : "manual";
        String sessionId = knockVO.getSessionId() != null ? knockVO.getSessionId() : IdUtil.simpleUUID();

        java.time.LocalDate statLocalDate = knockTime.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        Date statDate = java.sql.Date.valueOf(statLocalDate);

        long knockCountLong = knockCount;
        long totalMeritLong = totalMerit;

        if ("manual".equals(knockType)) {
            LambdaQueryWrapper<MeritRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MeritRecord::getUserId, knockVO.getUserId())
                   .eq(MeritRecord::getKnockType, knockType)
                   .eq(MeritRecord::getSource, "knock")
                   .ge(MeritRecord::getCreateTime, hourStart)
                   .le(MeritRecord::getCreateTime, hourEnd);

            MeritRecord existingRecord = meritRecordMapper.selectOne(wrapper);
            if (existingRecord != null) {
                int newTotalMerit = existingRecord.getMeritGained() + totalMerit;
                int existingBase = existingRecord.getBaseMerit() != null ? existingRecord.getBaseMerit() : 0;

                existingRecord.setMeritGained(newTotalMerit);
                existingRecord.setBaseMerit(existingBase + baseMerit);
                existingRecord.setBonusRate(bonusRate);
                existingRecord.setPropSnapshot(knockVO.getPropSnapshot());
                existingRecord.setKnockMode(knockMode);
                existingRecord.setStatDate(statDate);
                existingRecord.setDescription("手动敲击获得功德(本小时累计功德: " + newTotalMerit + ")");

                meritRecordMapper.updateById(existingRecord);

                userStatsMapper.updateKnockStats(knockVO.getUserId(), knockCountLong, totalMeritLong);
                userPeriodStatsService.recordKnock(knockVO.getUserId(), knockCountLong, totalMeritLong,
                        knockVO.getComboCount() != null ? knockVO.getComboCount() : 0, knockTime);
                return totalMerit;
            }
        }

        MeritRecord record = new MeritRecord();
        record.setId(IdGenerator.nextId());
        record.setUserId(knockVO.getUserId());
        record.setMeritGained(totalMerit);
        record.setBaseMerit(baseMerit);
        record.setKnockType(knockType);
        record.setKnockMode(knockMode);
        record.setSource("knock");
        record.setSessionId(sessionId);
        record.setComboCount(knockVO.getComboCount() != null ? knockVO.getComboCount() : 0);
        record.setBonusRate(bonusRate);
        record.setPropSnapshot(knockVO.getPropSnapshot());
        record.setStatDate(statDate);
        record.setDescription(("auto".equals(knockType) ? "自动" : "手动") + "敲击获得功德");
        record.setCreateTime("manual".equals(knockType) ? hourStart : knockTime);

        meritRecordMapper.insert(record);

        userStatsMapper.updateKnockStats(knockVO.getUserId(), knockCountLong, totalMeritLong);
        userPeriodStatsService.recordKnock(knockVO.getUserId(), knockCountLong, totalMeritLong,
                        knockVO.getComboCount() != null ? knockVO.getComboCount() : 0, knockTime);

        return totalMerit;
    }
    
    @Override
    public String startAutoKnock(Long userId, Integer duration) {
        String sessionId = IdUtil.simpleUUID();
        
        // TODO: 实现自动敲击逻辑，可以使用定时任务或异步任务
        // 这里简化处理，仅返回会话ID
        
        return sessionId;
    }
    
    @Override
    public Boolean stopAutoKnock(Long userId, String sessionId) {
        // TODO: 停止自动敲击任务
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> exchangeMerit(ExchangeVO exchangeVO) {
        // 使用用户锁确保兑换操作的原子性
        return userLockManager.executeWithUserLock(exchangeVO.getUserId(), () -> {
            return executeExchangeOperation(exchangeVO);
        });
    }

    /**
     * 执行兑换操作的核心逻辑（已被锁保护）
     */
    private Map<String, Object> executeExchangeOperation(ExchangeVO exchangeVO) {
        Map<String, Object> result = new HashMap<>();

        // 查询用户统计信息
        UserStats userStats = userStatsMapper.selectByUserId(exchangeVO.getUserId());
        if (userStats == null) {
            throw new BusinessException("用户统计信息不存在");
        }

        // 检查功德值是否足够
        if (userStats.getTotalMerit() < exchangeVO.getMeritAmount()) {
            throw new BusinessException("功德值不足");
        }

        // 计算兑换功德币（比例1000:1）
        Integer exchangeRate = 1000;
        Integer meritCoins = exchangeVO.getMeritAmount().intValue() / exchangeRate;

        // 创建兑换记录
        ExchangeRecord record = new ExchangeRecord();
        record.setId(IdGenerator.nextId());
        record.setUserId(exchangeVO.getUserId());
        record.setMeritUsed(exchangeVO.getMeritAmount());
        record.setMeritCoinsGained(meritCoins);
        record.setExchangeRate(exchangeRate);
        record.setExchangeTime(new Date());
        exchangeRecordMapper.insert(record);

        // 更新用户统计
        userStats.setTotalMerit(userStats.getTotalMerit() - exchangeVO.getMeritAmount());
        userStats.setMeritCoins(userStats.getMeritCoins() + meritCoins);
        userStatsMapper.updateById(userStats);

        result.put("meritUsed", exchangeVO.getMeritAmount());
        result.put("meritCoinsGained", meritCoins);
        result.put("remainingMerit", userStats.getTotalMerit());
        result.put("totalMeritCoins", userStats.getMeritCoins());

        return result;
    }
    
    @Override
    public Map<String, Object> getMeritStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        UserStats userStats = userStatsMapper.selectByUserId(userId);
        if (userStats != null) {
            stats.put("totalMerit", userStats.getTotalMerit());
            stats.put("totalKnocks", userStats.getTotalKnocks());
            stats.put("maxCombo", userStats.getMaxCombo());
            stats.put("meritCoins", userStats.getMeritCoins());
            stats.put("currentLevel", userStats.getCurrentLevel());
        } else {
            stats.put("totalMerit", 0L);
            stats.put("totalKnocks", 0L);
            stats.put("maxCombo", 0);
            stats.put("meritCoins", 0L);
            stats.put("currentLevel", 1);
        }

        Map<PeriodType, UserPeriodStats> periodStats = userPeriodStatsService.loadCurrentPeriods(userId, new Date());
        UserPeriodStats dayStats = periodStats.get(PeriodType.DAY);
        stats.put("todayMerit", dayStats != null ? dayStats.getMeritGained() : 0L);
        stats.put("todayKnocks", dayStats != null ? dayStats.getKnockCount() : 0L);
        UserPeriodStats weekStats = periodStats.get(PeriodType.WEEK);
        stats.put("weeklyMerit", weekStats != null ? weekStats.getMeritGained() : 0L);
        UserPeriodStats monthStats = periodStats.get(PeriodType.MONTH);
        stats.put("monthlyMerit", monthStats != null ? monthStats.getMeritGained() : 0L);
        
        return stats;
    }
    
    @Override
    public Page<MeritRecord> getMeritRecords(Long userId, Date startDate, Date endDate, 
                                            Integer pageNum, Integer pageSize) {
        Page<MeritRecord> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<MeritRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeritRecord::getUserId, userId);
        if (startDate != null) {
            wrapper.ge(MeritRecord::getCreateTime, startDate);
        }
        if (endDate != null) {
            wrapper.le(MeritRecord::getCreateTime, endDate);
        }
        wrapper.orderByDesc(MeritRecord::getCreateTime);
        
        return meritRecordMapper.selectPage(page, wrapper);
    }
    
    @Override
    public Long getTodayMerit(Long userId) {
        return meritRecordMapper.sumTodayMerit(userId);
    }
    
    @Override
    public Long getWeeklyMerit(Long userId) {
        return meritRecordMapper.sumWeeklyMerit(userId);
    }
    
    @Override
    public Long getMonthlyMerit(Long userId) {
        return meritRecordMapper.sumMonthlyMerit(userId);
    }

    @Override
    public Long getMeritByStatDate(Long userId, Date statDate) {
        if (statDate == null) {
            return getTodayMerit(userId);
        }
        return meritRecordMapper.sumMeritByStatDate(userId, statDate);
    }

    @Override
    public Map<String, Object> getBalance(Long userId) {
        Map<String, Object> balance = new HashMap<>();

        // 获取总功德值
        Long totalMerit = getTotalMerit(userId);
        balance.put("totalMerit", totalMerit != null ? totalMerit : 0L);
        
        // 获取功德币数量
        Integer meritCoins = getMeritCoins(userId);
        balance.put("meritCoins", meritCoins != null ? meritCoins : 0);
        
        // 获取今日功德
        Long todayMerit = getTodayMerit(userId);
        balance.put("todayMerit", todayMerit != null ? todayMerit : 0L);
        
        // 获取本周功德
        Long weeklyMerit = getWeeklyMerit(userId);
        balance.put("weeklyMerit", weeklyMerit != null ? weeklyMerit : 0L);
        
        // 获取本月功德
        Long monthlyMerit = getMonthlyMerit(userId);
        balance.put("monthlyMerit", monthlyMerit != null ? monthlyMerit : 0L);
        
        // 获取用户统计信息
        UserStats userStats = userStatsMapper.selectByUserId(userId);
        if (userStats != null) {
            balance.put("userLevel", userStats.getCurrentLevel());
            balance.put("totalKnocks", userStats.getTotalKnocks());
            balance.put("continuousSignDays", userStats.getContinuousSignDays());
        } else {
            balance.put("userLevel", 1);
            balance.put("totalKnocks", 0);
            balance.put("continuousSignDays", 0);
        }
        
        // 兑换比例信息
        balance.put("exchangeRatio", "1000:1"); // 1000功德兑换1功德币
        balance.put("minExchangeMerit", 1000); // 最小兑换功德值
        
        return balance;
    }
    
    @Override
    public Long getTotalMerit(Long userId) {
        // 计算用户总功德值
        return meritRecordMapper.sumTotalMerit(userId);
    }
    
    @Override
    public Integer getMeritCoins(Long userId) {
        // 从用户统计表获取功德币数量
        UserStats userStats = userStatsMapper.selectByUserId(userId);
        return userStats != null ? userStats.getMeritCoins().intValue() : 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addMeritRecord(Long userId, Integer merit, String source, String description) {
        // 使用用户锁确保功德记录添加的原子性
        return userLockManager.executeWithUserLock(userId, () -> {
            MeritRecord record = new MeritRecord();
            record.setId(IdGenerator.nextId());
            record.setUserId(userId);
            record.setMeritGained(merit);
            record.setSource(source);
            record.setDescription(description);
            record.setBonusRate(BigDecimal.ONE);
            meritRecordMapper.insert(record);

            // 更新用户统计
            userStatsMapper.addMerit(userId, merit.longValue());

            return true;
        });
    }
}
