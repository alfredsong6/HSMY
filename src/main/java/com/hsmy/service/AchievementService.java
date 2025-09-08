package com.hsmy.service;

import com.hsmy.entity.Achievement;
import com.hsmy.entity.UserAchievement;

import java.util.List;

/**
 * 成就Service接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
public interface AchievementService {
    
    /**
     * 根据成就类型获取成就列表
     * 
     * @param achievementType 成就类型
     * @return 成就列表
     */
    List<Achievement> getAchievementsByType(String achievementType);
    
    /**
     * 获取所有启用的成就
     * 
     * @return 成就列表
     */
    List<Achievement> getAllActiveAchievements();
    
    /**
     * 根据成就等级获取成就列表
     * 
     * @param achievementLevel 成就等级
     * @return 成就列表
     */
    List<Achievement> getAchievementsByLevel(Integer achievementLevel);
    
    /**
     * 根据用户ID获取用户成就
     * 
     * @param userId 用户ID
     * @return 用户成就列表
     */
    List<UserAchievement> getUserAchievements(Long userId);
    
    /**
     * 获取用户已完成的成就
     * 
     * @param userId 用户ID
     * @return 已完成成就列表
     */
    List<UserAchievement> getCompletedAchievements(Long userId);
    
    /**
     * 更新用户成就进度
     * 
     * @param userId 用户ID
     * @param achievementType 成就类型
     * @param progress 进度值
     * @return 是否有新成就完成
     */
    Boolean updateAchievementProgress(Long userId, String achievementType, Long progress);
    
    /**
     * 检查并完成成就
     * 
     * @param userId 用户ID
     * @param achievementId 成就ID
     * @param currentValue 当前值
     * @return 是否完成成就
     */
    Boolean checkAndCompleteAchievement(Long userId, Long achievementId, Long currentValue);
    
    /**
     * 领取成就奖励
     * 
     * @param userId 用户ID
     * @param achievementId 成就ID
     * @return 是否成功
     */
    Boolean claimAchievementReward(Long userId, Long achievementId);
    
    /**
     * 初始化用户成就
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean initUserAchievements(Long userId);
}