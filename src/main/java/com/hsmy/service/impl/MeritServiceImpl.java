package com.hsmy.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.entity.ExchangeRecord;
import com.hsmy.entity.MeritRecord;
import com.hsmy.entity.UserStats;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.ExchangeRecordMapper;
import com.hsmy.mapper.MeritRecordMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.MeritService;
import com.hsmy.utils.IdGenerator;
import com.hsmy.vo.ExchangeVO;
import com.hsmy.vo.KnockVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer manualKnock(KnockVO knockVO) {
        // 计算功德值
        Integer baseMerit = knockVO.getKnockCount();
        BigDecimal bonusRate = BigDecimal.ONE;
        
        // 连击加成
        if (knockVO.getComboCount() != null && knockVO.getComboCount() > 5) {
            bonusRate = bonusRate.add(new BigDecimal("0.1"));
        }
        
        Integer totalMerit = bonusRate.multiply(new BigDecimal(baseMerit)).intValue();
        
        // 创建功德记录
        MeritRecord record = new MeritRecord();
        record.setId(IdGenerator.nextId());
        record.setUserId(knockVO.getUserId());
        record.setMeritGained(totalMerit);
        record.setKnockType("manual");
        record.setSource("knock");
        record.setSessionId(knockVO.getSessionId() != null ? knockVO.getSessionId() : IdUtil.simpleUUID());
        record.setComboCount(knockVO.getComboCount() != null ? knockVO.getComboCount() : 0);
        record.setBonusRate(bonusRate);
        record.setDescription("手动敲击获得功德");
        meritRecordMapper.insert(record);
        
        // 更新用户统计
        userStatsMapper.updateKnockStats(knockVO.getUserId(), 
                                        knockVO.getKnockCount().longValue(), 
                                        totalMerit.longValue());
        
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
            stats.put("todayMerit", userStats.getTodayMerit());
            stats.put("weeklyMerit", userStats.getWeeklyMerit());
            stats.put("monthlyMerit", userStats.getMonthlyMerit());
            stats.put("totalKnocks", userStats.getTotalKnocks());
            stats.put("todayKnocks", userStats.getTodayKnocks());
            stats.put("maxCombo", userStats.getMaxCombo());
            stats.put("meritCoins", userStats.getMeritCoins());
            stats.put("currentLevel", userStats.getCurrentLevel());
        }
        
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
    }
}