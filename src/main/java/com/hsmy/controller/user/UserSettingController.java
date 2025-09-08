package com.hsmy.controller.user;

import com.hsmy.common.Result;
import com.hsmy.entity.UserSetting;
import com.hsmy.service.UserSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户设置Controller
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@RestController
@RequestMapping("/user/settings")
@RequiredArgsConstructor
public class UserSettingController {
    
    private final UserSettingService userSettingService;
    
    /**
     * 获取用户设置
     * 
     * @param request HTTP请求
     * @return 用户设置信息
     */
    @GetMapping
    public Result<UserSetting> getUserSettings(HttpServletRequest request) {
        try {
            // TODO: 从token获取用户ID
            Long userId = 1L; // 临时硬编码
            
            UserSetting userSetting = userSettingService.getUserSettingByUserId(userId);
            if (userSetting == null) {
                // 如果没有设置记录，初始化默认设置
                userSettingService.initUserDefaultSetting(userId);
                userSetting = userSettingService.getUserSettingByUserId(userId);
            }
            
            return Result.success(userSetting);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户设置
     * 
     * @param userSetting 用户设置信息
     * @param request HTTP请求
     * @return 更新结果
     */
    @PutMapping
    public Result<Map<String, Object>> updateUserSettings(@Validated @RequestBody UserSetting userSetting,
                                                          HttpServletRequest request) {
        try {
            // TODO: 从token获取用户ID
            Long userId = 1L; // 临时硬编码
            userSetting.setUserId(userId);
            
            boolean success = userSettingService.updateUserSetting(userSetting);
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "设置更新成功");
                return Result.success("更新成功", result);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新音效设置
     * 
     * @param soundEnabled 音效开关
     * @param soundVolume 音量大小
     * @param request HTTP请求
     * @return 更新结果
     */
    @PutMapping("/sound")
    public Result<Map<String, Object>> updateSoundSettings(@RequestParam Integer soundEnabled,
                                                           @RequestParam Integer soundVolume,
                                                           HttpServletRequest request) {
        try {
            // TODO: 从token获取用户ID
            Long userId = 1L; // 临时硬编码
            
            boolean success = userSettingService.updateSoundSetting(userId, soundEnabled, soundVolume);
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("soundEnabled", soundEnabled);
                result.put("soundVolume", soundVolume);
                result.put("message", "音效设置更新成功");
                return Result.success("更新成功", result);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新提醒设置
     * 
     * @param dailyReminder 每日提醒开关
     * @param reminderTime 提醒时间
     * @param request HTTP请求
     * @return 更新结果
     */
    @PutMapping("/reminder")
    public Result<Map<String, Object>> updateReminderSettings(@RequestParam Integer dailyReminder,
                                                              @RequestParam(required = false) String reminderTime,
                                                              HttpServletRequest request) {
        try {
            // TODO: 从token获取用户ID
            Long userId = 1L; // 临时硬编码
            
            boolean success = userSettingService.updateReminderSetting(userId, dailyReminder, reminderTime);
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("dailyReminder", dailyReminder);
                result.put("reminderTime", reminderTime != null ? reminderTime : "");
                result.put("message", "提醒设置更新成功");
                return Result.success("更新成功", result);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新隐私设置
     * 
     * @param privacyMode 隐私模式
     * @param request HTTP请求
     * @return 更新结果
     */
    @PutMapping("/privacy")
    public Result<Map<String, Object>> updatePrivacySettings(@RequestParam Integer privacyMode,
                                                             HttpServletRequest request) {
        try {
            // TODO: 从token获取用户ID
            Long userId = 1L; // 临时硬编码
            
            boolean success = userSettingService.updatePrivacySetting(userId, privacyMode);
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("privacyMode", privacyMode);
                result.put("message", "隐私设置更新成功");
                return Result.success("更新成功", result);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 重置为默认设置
     * 
     * @param request HTTP请求
     * @return 重置结果
     */
    @PostMapping("/reset")
    public Result<Map<String, Object>> resetToDefaultSettings(HttpServletRequest request) {
        try {
            // TODO: 从token获取用户ID
            Long userId = 1L; // 临时硬编码
            
            boolean success = userSettingService.initUserDefaultSetting(userId);
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "设置已重置为默认值");
                return Result.success("重置成功", result);
            } else {
                return Result.error("重置失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}