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
}