package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.MeritRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 功德记录Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Mapper
public interface MeritRecordMapper extends BaseMapper<MeritRecord> {
    
    /**
     * 查询用户功德记录
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 功德记录列表
     */
    List<MeritRecord> selectByUserIdAndDate(@Param("userId") Long userId,
                                           @Param("startDate") Date startDate,
                                           @Param("endDate") Date endDate);
    
    /**
     * 统计用户今日功德
     * 
     * @param userId 用户ID
     * @return 今日功德总数
     */
    Long sumTodayMerit(@Param("userId") Long userId);
    
    /**
     * 统计用户本周功德
     * 
     * @param userId 用户ID
     * @return 本周功德总数
     */
    Long sumWeeklyMerit(@Param("userId") Long userId);
    
    /**
     * 统计用户本月功德
     * 
     * @param userId 用户ID
     * @return 本月功德总数
     */
    Long sumMonthlyMerit(@Param("userId") Long userId);
    
    /**
     * 统计用户总功德
     * 
     * @param userId 用户ID
     * @return 总功德数
     */
    Long sumTotalMerit(@Param("userId") Long userId);
    
    /**
     * 查询会话内的功德记录
     * 
     * @param sessionId 会话ID
     * @return 功德记录列表
     */
    List<MeritRecord> selectBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 获取最大连击数
     * 
     * @param userId 用户ID
     * @return 最大连击数
     */
    Integer getMaxCombo(@Param("userId") Long userId);
}