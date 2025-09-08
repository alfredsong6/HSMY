package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.Achievement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成就Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Mapper
public interface AchievementMapper extends BaseMapper<Achievement> {
    
    /**
     * 根据成就类型查询成就列表
     * 
     * @param achievementType 成就类型
     * @return 成就列表
     */
    List<Achievement> selectByType(@Param("achievementType") String achievementType);
    
    /**
     * 查询所有启用的成就
     * 
     * @return 成就列表
     */
    List<Achievement> selectAllActive();
    
    /**
     * 根据成就等级查询成就
     * 
     * @param achievementLevel 成就等级
     * @return 成就列表
     */
    List<Achievement> selectByLevel(@Param("achievementLevel") Integer achievementLevel);
}