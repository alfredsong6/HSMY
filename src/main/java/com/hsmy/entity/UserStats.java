package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户统计实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_stats")
public class UserStats extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 总功德值
     */
    private Long totalMerit;
    
    /**
     * 功德币余额
     */
    private Long meritCoins;
    
    /**
     * 总敲击次数
     */
    private Long totalKnocks;
    
    /**
     * 今日功德值
     */
    private Long todayMerit;
    
    /**
     * 今日敲击次数
     */
    private Long todayKnocks;
    
    /**
     * 本周功德值
     */
    private Long weeklyMerit;
    
    /**
     * 本月功德值
     */
    private Long monthlyMerit;
    
    /**
     * 连续登录天数
     */
    private Integer consecutiveDays;
    
    /**
     * 总登录天数
     */
    private Integer totalLoginDays;
    
    /**
     * 当前等级
     */
    private Integer currentLevel;
    
    /**
     * 最高连击数
     */
    private Integer maxCombo;
    
    /**
     * 最后敲击时间
     */
    private Date lastKnockTime;
    
    /**
     * 最后登录日期
     */
    private Date lastLoginDate;
}