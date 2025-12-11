package com.hsmy.service;

import com.hsmy.entity.UserSetting;

/**
 * 用户设置Service接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
public interface UserSettingService {
    
    /**
     * 根据用户ID获取用户设置
     * 
     * @param userId 用户ID
     * @return 用户设置信息
     */
    UserSetting getUserSettingByUserId(Long userId);
    
    /**
     * 初始化用户默认设置
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean initUserDefaultSetting(Long userId);
    
    /**
     * 更新用户设置
     * 
     * @param userSetting 用户设置信息
     * @return 是否成功
     */
    Boolean updateUserSetting(UserSetting userSetting);
    
    /**
     * 更新音效设置
     * 
     * @param userId 用户ID
     * @param soundEnabled 音效开关
     * @param soundVolume 音量大小
     * @return 是否成功
     */
    Boolean updateSoundSetting(Long userId, Integer soundEnabled, Integer soundVolume);
    
    /**
     * 更新提醒设置
     * 
     * @param userId 用户ID
     * @param dailyReminder 每日提醒
     * @param reminderTime 提醒时间
     * @return 是否成功
     */
    Boolean updateReminderSetting(Long userId, Integer dailyReminder, String reminderTime);
    
    /**
     * 更新隐私设置
     * 
     * @param userId 用户ID
     * @param privacyMode 隐私模式
     * @return 是否成功
     */
    Boolean updatePrivacySetting(Long userId, Integer privacyMode);

    /**
     * 更新弹幕设置
     *
     * @param userId 用户ID
     * @param bulletScreen 弹幕设置
     * @param scriptureId 典籍ID（bullet_screen=3时必填）
     * @return 是否成功
     */
    Boolean updateBulletScreenSetting(Long userId, Integer bulletScreen, Long scriptureId);
}
