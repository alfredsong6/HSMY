package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 活动定义实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_activity")
public class Activity extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 活动名称
     */
    private String activityName;
    
    /**
     * 活动类型：festival-节日活动，special-特殊活动，regular-常规活动
     */
    private String activityType;
    
    /**
     * 活动描述
     */
    @TableField("`description`")
    private String description;
    
    /**
     * 活动横幅URL
     */
    private String bannerUrl;
    
    /**
     * 开始时间
     */
    private Date startTime;
    
    /**
     * 结束时间
     */
    private Date endTime;
    
    /**
     * 功德加成倍率
     */
    private BigDecimal meritBonusRate;
    
    /**
     * 活动规则
     */
    @TableField("`rules`")
    private String rules;
    
    /**
     * 状态：0-未开始，1-进行中，2-已结束
     */
    private Integer status;
    
    /**
     * 排序序号
     */
    private Integer sortOrder;
}