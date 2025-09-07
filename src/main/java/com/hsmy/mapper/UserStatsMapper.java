package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.UserStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户统计Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Mapper
public interface UserStatsMapper extends BaseMapper<UserStats> {
    
    /**
     * 根据用户ID查询统计信息
     * 
     * @param userId 用户ID
     * @return 用户统计信息
     */
    UserStats selectByUserId(@Param("userId") Long userId);
    
    /**
     * 增加功德值
     * 
     * @param userId 用户ID
     * @param merit 功德值
     * @return 影响行数
     */
    int addMerit(@Param("userId") Long userId, @Param("merit") Long merit);
    
    /**
     * 增加功德币
     * 
     * @param userId 用户ID
     * @param coins 功德币
     * @return 影响行数
     */
    int addMeritCoins(@Param("userId") Long userId, @Param("coins") Long coins);
    
    /**
     * 扣减功德币
     * 
     * @param userId 用户ID
     * @param coins 功德币
     * @return 影响行数
     */
    int reduceMeritCoins(@Param("userId") Long userId, @Param("coins") Long coins);
    
    /**
     * 更新敲击统计
     * 
     * @param userId 用户ID
     * @param knockCount 敲击次数
     * @param merit 功德值
     * @return 影响行数
     */
    int updateKnockStats(@Param("userId") Long userId, 
                        @Param("knockCount") Long knockCount,
                        @Param("merit") Long merit);
    
    /**
     * 重置每日统计
     * 
     * @return 影响行数
     */
    int resetDailyStats();
    
    /**
     * 重置每周统计
     * 
     * @return 影响行数
     */
    int resetWeeklyStats();
    
    /**
     * 重置每月统计
     * 
     * @return 影响行数
     */
    int resetMonthlyStats();
}