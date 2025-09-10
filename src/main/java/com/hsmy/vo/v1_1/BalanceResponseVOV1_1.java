package com.hsmy.vo.v1_1;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 功德余额查询响应VO V1.1版本
 * 
 * V1.1版本相比v1.0新增更详细的统计信息
 * 
 * @author HSMY
 * @date 2025/09/10
 */
@Data
public class BalanceResponseVOV1_1 implements Serializable {
    
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
     * 功德币数量
     */
    private Integer meritCoins;
    
    /**
     * 今日功德
     */
    private Long todayMerit;
    
    /**
     * 本周功德
     */
    private Long weeklyMerit;
    
    /**
     * 本月功德
     */
    private Long monthlyMerit;
    
    /**
     * 用户等级
     */
    private Integer userLevel;
    
    /**
     * 升级还需功德值
     */
    private Long needMeritForNextLevel;
    
    /**
     * 功德兑换比例 (功德:功德币)
     */
    private String exchangeRatio = "1000:1";
    
    /**
     * 账户状态 1:正常 2:受限 3:冻结
     */
    private Integer accountStatus = 1;
    
    /**
     * 最后敲击时间
     */
    private Long lastKnockTime;
    
    /**
     * 连续签到天数
     */
    private Integer continuousSignDays = 0;
    
    /**
     * API版本
     */
    private String apiVersion = "v1.1";
}