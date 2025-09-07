package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务定义实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_task")
public class Task extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 任务类型：daily-每日任务，weekly-每周任务，achievement-成就任务，activity-活动任务
     */
    private String taskType;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 任务图标URL
     */
    private String iconUrl;
    
    /**
     * 目标类型：knock-敲击，login-登录，share-分享，donate-捐赠
     */
    private String targetType;
    
    /**
     * 目标值
     */
    private Integer targetValue;
    
    /**
     * 奖励功德值
     */
    private Integer rewardMerit;
    
    /**
     * 奖励功德币
     */
    private Integer rewardCoins;
    
    /**
     * 奖励道具ID
     */
    private Long rewardItemId;
    
    /**
     * 刷新类型：daily-每日刷新，weekly-每周刷新
     */
    private String refreshType;
    
    /**
     * 排序序号
     */
    private Integer sortOrder;
    
    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer isActive;
}