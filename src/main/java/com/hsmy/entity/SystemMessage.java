package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 系统消息实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_system_message")
public class SystemMessage extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息标题
     */
    private String title;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型：system-系统通知，activity-活动通知，reward-奖励通知
     */
    private String messageType;
    
    /**
     * 目标类型：all-全体用户，user-指定用户，level-指定等级
     */
    private String targetType;
    
    /**
     * 目标值：用户ID列表或等级范围
     */
    private String targetValue;
    
    /**
     * 跳转链接
     */
    private String linkUrl;
    
    /**
     * 发布时间
     */
    private Date publishTime;
    
    /**
     * 过期时间
     */
    private Date expireTime;
    
    /**
     * 状态：0-草稿，1-已发布，2-已过期
     */
    private Integer status;
}