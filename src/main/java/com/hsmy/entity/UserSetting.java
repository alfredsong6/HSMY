package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

/**
 * 用户设置实体类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_setting")
public class UserSetting extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 音效开关（0-关闭，1-开启）
     */
    private Integer soundEnabled;
    
    /**
     * 音量大小（0-100）
     */
    private Integer soundVolume;
    
    /**
     * 震动反馈（0-关闭，1-开启）
     */
    private Integer vibrationEnabled;
    
    /**
     * 每日提醒（0-关闭，1-开启）
     */
    private Integer dailyReminder;
    
    /**
     * 提醒时间
     */
    private LocalTime reminderTime;
    
    /**
     * 隐私模式（0-公开，1-仅好友可见，2-完全隐私）
     */
    private Integer privacyMode;
    
    /**
     * 自动敲击速度（1-慢速，2-中速，3-快速）
     */
    private Integer autoKnockSpeed;
    
    /**
     * 当前主题ID
     */
    private Long themeId;
    
    /**
     * 当前皮肤ID
     */
    private Long skinId;
    
    /**
     * 当前音效ID
     */
    private Long soundId;
}