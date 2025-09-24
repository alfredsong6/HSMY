package com.hsmy.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 自动敲击会话实体
 * 
 * @author HSMY
 * @date 2025/09/11
 */
@Data
public class AutoKnockSession {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 预计结束时间
     */
    private LocalDateTime endTime;

    /**
     * 实际结束时间
     */
    private LocalDateTime actualEndTime;

    /**
     * 持续时长（秒）
     */
    private Integer duration;

    /**
     * 每秒敲击次数
     */
    private Integer knockPerSecond;
    
    /**
     * 每次敲击基础功德值
     */
    private Integer baseMeritPerKnock;
    
    /**
     * 预计总敲击次数
     */
    private Integer expectedKnocks;
    
    /**
     * 预计总功德值
     */
    private Integer expectedMerit;
    
    /**
     * 实际敲击次数
     */
    private Integer actualKnocks;
    
    /**
     * 实际功德值
     */
    private Integer actualMerit;
    
    /**
     * 会话状态：active-活跃，completed-已完成，stopped-已停止
     */
    private String status;
    
    /**
     * 是否已结算
     */
    private Boolean settled;

    /**
     * 倍率值（功德加成）
     */
    private Double multiplier;

    /**
     * 会话模式：MANUAL、AUTO_AUTOEND、AUTO_TIMED
     */
    private String sessionMode;

    /**
     * 限制类型：DURATION、COUNT
     */
    private String limitType;

    /**
     * 限制值（秒或次数）
     */
    private Integer limitValue;

    /**
     * 道具快照JSON
     */
    private String propSnapshot;

    /**
     * 最后心跳时间
     */
    private LocalDateTime lastHeartbeatTime;

    /**
     * 客户端报告的累计敲击次数
     */
    private Integer clientKnockCount;

    /**
     * 来源类型
     */
    private Integer source;

    /**
     * 会话结束原因
     */
    private String endReason;

    /**
     * 预扣功德币
     */
    private Integer coinCost;

    /**
     * 已退还功德币
     */
    private Integer coinRefunded;

    /**
     * 支付状态：RESERVED、SETTLED、REFUNDED
     */
    private String paymentStatus;

    /**
     * 钱包流水ID
     */
    private String walletTxnId;
}
