package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户任务进度实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_task")
public class UserTask extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 任务日期
     */
    private Date taskDate;
    
    /**
     * 当前进度
     */
    private Integer progress;
    
    /**
     * 是否完成：0-未完成，1-已完成
     */
    private Integer isCompleted;
    
    /**
     * 完成时间
     */
    private Date completeTime;
    
    /**
     * 是否已领取奖励：0-未领取，1-已领取
     */
    private Integer isClaimed;
    
    /**
     * 领取时间
     */
    private Date claimTime;
    
    /**
     * 关联的任务信息（用于联表查询）
     */
    @TableField(exist = false)
    private Task task;
}