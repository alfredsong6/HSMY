package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.UserAchievement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户成就Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Mapper
public interface UserAchievementMapper extends BaseMapper<UserAchievement> {
    
    /**
     * 根据用户ID查询用户成就
     * 
     * @param userId 用户ID
     * @return 用户成就列表
     */
    List<UserAchievement> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和成就ID查询
     * 
     * @param userId 用户ID
     * @param achievementId 成就ID
     * @return 用户成就信息
     */
    UserAchievement selectByUserAndAchievement(@Param("userId") Long userId, @Param("achievementId") Long achievementId);
    
    /**
     * 更新用户成就进度
     * 
     * @param userId 用户ID
     * @param achievementId 成就ID
     * @param progress 进度
     * @return 影响行数
     */
    int updateProgress(@Param("userId") Long userId, @Param("achievementId") Long achievementId, @Param("progress") Long progress);
    
    /**
     * 完成成就
     * 
     * @param userId 用户ID
     * @param achievementId 成就ID
     * @return 影响行数
     */
    int completeAchievement(@Param("userId") Long userId, @Param("achievementId") Long achievementId);
    
    /**
     * 查询用户已完成的成就
     * 
     * @param userId 用户ID
     * @return 已完成成就列表
     */
    List<UserAchievement> selectCompletedByUserId(@Param("userId") Long userId);
}