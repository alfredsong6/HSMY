package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 功德等级配置实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_merit_level")
public class MeritLevel extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 等级
     */
    private Integer level;
    
    /**
     * 等级名称
     */
    private String levelName;
    
    /**
     * 最低功德值
     */
    private Long minMerit;
    
    /**
     * 最高功德值，NULL表示无上限
     */
    private Long maxMerit;
    
    /**
     * 等级特权描述
     */
    private String levelBenefits;
    
    /**
     * 等级图标URL
     */
    private String iconUrl;
    
    /**
     * 功德加成倍率
     */
    private BigDecimal bonusRate;
    
    /**
     * 每日兑换限额（功德币）
     */
    private Integer dailyExchangeLimit;
}