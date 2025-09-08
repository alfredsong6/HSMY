package com.hsmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hsmy.entity.UserSetting;
import com.hsmy.mapper.UserSettingMapper;
import com.hsmy.service.UserSettingService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

/**
 * 用户设置Service实现类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Service
@RequiredArgsConstructor
public class UserSettingServiceImpl implements UserSettingService {
    
    private final UserSettingMapper userSettingMapper;
    
    @Override
    public UserSetting getUserSettingByUserId(Long userId) {
        return userSettingMapper.selectByUserId(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean initUserDefaultSetting(Long userId) {
        // 检查是否已存在设置
        UserSetting existingSetting = userSettingMapper.selectByUserId(userId);
        if (existingSetting != null) {
            return true;
        }
        
        // 创建默认设置
        UserSetting userSetting = new UserSetting();
        userSetting.setId(IdGenerator.nextId());
        userSetting.setUserId(userId);
        userSetting.setSoundEnabled(1);
        userSetting.setSoundVolume(80);
        userSetting.setVibrationEnabled(1);
        userSetting.setDailyReminder(1);
        userSetting.setReminderTime(LocalTime.of(9, 0));
        userSetting.setPrivacyMode(0);
        userSetting.setAutoKnockSpeed(2);
        
        return userSettingMapper.insert(userSetting) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserSetting(UserSetting userSetting) {
        if (userSetting.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        return userSettingMapper.updateByUserId(userSetting) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSoundSetting(Long userId, Integer soundEnabled, Integer soundVolume) {
        UserSetting userSetting = new UserSetting();
        userSetting.setUserId(userId);
        userSetting.setSoundEnabled(soundEnabled);
        userSetting.setSoundVolume(soundVolume);
        return userSettingMapper.updateByUserId(userSetting) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateReminderSetting(Long userId, Integer dailyReminder, String reminderTime) {
        UserSetting userSetting = new UserSetting();
        userSetting.setUserId(userId);
        userSetting.setDailyReminder(dailyReminder);
        if (reminderTime != null) {
            userSetting.setReminderTime(LocalTime.parse(reminderTime));
        }
        return userSettingMapper.updateByUserId(userSetting) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePrivacySetting(Long userId, Integer privacyMode) {
        UserSetting userSetting = new UserSetting();
        userSetting.setUserId(userId);
        userSetting.setPrivacyMode(privacyMode);
        return userSettingMapper.updateByUserId(userSetting) > 0;
    }
}