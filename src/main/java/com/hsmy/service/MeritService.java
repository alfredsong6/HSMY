package com.hsmy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.entity.MeritRecord;
import com.hsmy.vo.ExchangeVO;
import com.hsmy.vo.KnockVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 功德Service接口
 * 
 * @author HSMY
 * @date 2025/09/07
 */
public interface MeritService {
    
    /**
     * 手动敲击
     * 
     * @param knockVO 敲击信息
     * @return 获得功德值
     */
    Integer manualKnock(KnockVO knockVO);
    
    /**
     * 自动敲击开始
     * 
     * @param userId 用户ID
     * @param duration 持续时间（秒）
     * @return 会话ID
     */
    String startAutoKnock(Long userId, Integer duration);
    
    /**
     * 自动敲击停止
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 是否成功
     */
    Boolean stopAutoKnock(Long userId, String sessionId);
    
    /**
     * 功德兑换功德币
     * 
     * @param exchangeVO 兑换信息
     * @return 兑换结果
     */
    Map<String, Object> exchangeMerit(ExchangeVO exchangeVO);
    
    /**
     * 获取用户功德统计
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getMeritStats(Long userId);
    
    /**
     * 获取功德记录
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 功德记录分页
     */
    Page<MeritRecord> getMeritRecords(Long userId, Date startDate, Date endDate, 
                                      Integer pageNum, Integer pageSize);
    
    /**
     * 获取今日功德
     * 
     * @param userId 用户ID
     * @return 今日功德值
     */
    Long getTodayMerit(Long userId);
    
    /**
     * 获取本周功德
     * 
     * @param userId 用户ID
     * @return 本周功德值
     */
    Long getWeeklyMerit(Long userId);
    
    /**
     * 获取本月功德
     * 
     * @param userId 用户ID
     * @return 本月功德值
     */
    Long getMonthlyMerit(Long userId);
    
    /**
     * 添加功德记录
     * 
     * @param userId 用户ID
     * @param merit 功德值
     * @param source 来源
     * @param description 描述
     * @return 是否成功
     */
    Boolean addMeritRecord(Long userId, Integer merit, String source, String description);
}