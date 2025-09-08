package com.hsmy.service.impl;

import com.hsmy.entity.Achievement;
import com.hsmy.entity.UserAchievement;
import com.hsmy.mapper.AchievementMapper;
import com.hsmy.mapper.UserAchievementMapper;
import com.hsmy.service.AchievementService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 成就Service实现类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {
    
    private final AchievementMapper achievementMapper;
    private final UserAchievementMapper userAchievementMapper;
    
    @Override
    public List<Achievement> getAchievementsByType(String achievementType) {
        return achievementMapper.selectByType(achievementType);
    }
    
    @Override
    public List<Achievement> getAllActiveAchievements() {
        return achievementMapper.selectAllActive();
    }
    
    @Override
    public List<Achievement> getAchievementsByLevel(Integer achievementLevel) {
        return achievementMapper.selectByLevel(achievementLevel);
    }
    
    @Override
    public List<UserAchievement> getUserAchievements(Long userId) {
        return userAchievementMapper.selectByUserId(userId);
    }
    
    @Override
    public List<UserAchievement> getCompletedAchievements(Long userId) {
        return userAchievementMapper.selectCompletedByUserId(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAchievementProgress(Long userId, String achievementType, Long progress) {
        // 获取该类型的所有成就
        List<Achievement> achievements = getAchievementsByType(achievementType);
        boolean hasNewCompletion = false;
        
        for (Achievement achievement : achievements) {
            // 检查用户是否已有该成就记录
            UserAchievement userAchievement = userAchievementMapper.selectByUserAndAchievement(userId, achievement.getId());
            
            if (userAchievement == null) {
                // 创建新的成就记录
                userAchievement = new UserAchievement();
                userAchievement.setId(IdGenerator.nextId());
                userAchievement.setUserId(userId);
                userAchievement.setAchievementId(achievement.getId());
                userAchievement.setProgress(progress);
                userAchievement.setIsCompleted(0);
                userAchievement.setIsClaimed(0);
                userAchievementMapper.insert(userAchievement);
            } else if (userAchievement.getIsCompleted() == 0) {
                // 更新进度
                userAchievementMapper.updateProgress(userId, achievement.getId(), progress);
            }
            
            // 检查是否完成成就
            if (userAchievement.getIsCompleted() == 0 && progress >= achievement.getConditionValue()) {
                userAchievementMapper.completeAchievement(userId, achievement.getId());
                hasNewCompletion = true;
            }
        }
        
        return hasNewCompletion;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean checkAndCompleteAchievement(Long userId, Long achievementId, Long currentValue) {
        Achievement achievement = achievementMapper.selectById(achievementId);
        if (achievement == null) {
            return false;
        }
        
        UserAchievement userAchievement = userAchievementMapper.selectByUserAndAchievement(userId, achievementId);
        if (userAchievement == null) {
            // 创建新的成就记录
            userAchievement = new UserAchievement();
            userAchievement.setId(IdGenerator.nextId());
            userAchievement.setUserId(userId);
            userAchievement.setAchievementId(achievementId);
            userAchievement.setProgress(currentValue);
            userAchievement.setIsCompleted(0);
            userAchievement.setIsClaimed(0);
            userAchievementMapper.insert(userAchievement);
        } else {
            // 更新进度
            userAchievementMapper.updateProgress(userId, achievementId, currentValue);
        }
        
        // 检查是否完成成就
        if (userAchievement.getIsCompleted() == 0 && currentValue >= achievement.getConditionValue()) {
            userAchievementMapper.completeAchievement(userId, achievementId);
            return true;
        }
        
        return false;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean claimAchievementReward(Long userId, Long achievementId) {
        UserAchievement userAchievement = userAchievementMapper.selectByUserAndAchievement(userId, achievementId);
        if (userAchievement == null || userAchievement.getIsCompleted() == 0) {
            throw new RuntimeException("成就未完成，无法领取奖励");
        }
        
        if (userAchievement.getIsClaimed() == 1) {
            throw new RuntimeException("奖励已领取");
        }
        
        // 标记奖励已领取
        userAchievement.setIsClaimed(1);
        userAchievement.setClaimTime(new Date());
        
        // TODO: 实现具体的奖励发放逻辑
        
        return userAchievementMapper.updateById(userAchievement) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean initUserAchievements(Long userId) {
        // 获取所有启用的成就
        List<Achievement> achievements = getAllActiveAchievements();
        
        for (Achievement achievement : achievements) {
            // 检查用户是否已有该成就记录
            UserAchievement existingAchievement = userAchievementMapper.selectByUserAndAchievement(userId, achievement.getId());
            if (existingAchievement == null) {
                // 创建新的成就记录
                UserAchievement userAchievement = new UserAchievement();
                userAchievement.setId(IdGenerator.nextId());
                userAchievement.setUserId(userId);
                userAchievement.setAchievementId(achievement.getId());
                userAchievement.setProgress(0L);
                userAchievement.setIsCompleted(0);
                userAchievement.setIsClaimed(0);
                userAchievementMapper.insert(userAchievement);
            }
        }
        
        return true;
    }
}