package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * VIP套餐实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_vip_package")
public class VipPackage extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 套餐名称
     */
    private String packageName;
    
    /**
     * 套餐类型：1-月卡，2-季卡，3-年卡，4-永久
     */
    private Integer packageType;
    
    /**
     * 价格（元）
     */
    private BigDecimal price;
    
    /**
     * 原价（元）
     */
    private BigDecimal originalPrice;
    
    /**
     * 时长（天），-1表示永久
     */
    private Integer durationDays;
    
    /**
     * 功德加成倍率
     */
    private BigDecimal meritBonusRate;
    
    /**
     * 每日额外功德值
     */
    private Integer dailyMeritBonus;
    
    /**
     * 套餐描述
     */
    private String description;
    
    /**
     * 套餐权益，JSON格式
     */
    private String benefits;
    
    /**
     * 排序序号
     */
    private Integer sortOrder;
    
    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer isActive;
}