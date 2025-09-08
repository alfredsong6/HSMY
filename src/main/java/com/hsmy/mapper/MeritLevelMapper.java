package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.MeritLevel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功德等级Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Mapper
public interface MeritLevelMapper extends BaseMapper<MeritLevel> {
    
    /**
     * 根据功德值查询对应等级
     * 
     * @param meritValue 功德值
     * @return 功德等级信息
     */
    MeritLevel selectByMeritValue(@Param("meritValue") Long meritValue);
    
    /**
     * 查询所有启用的等级配置
     * 
     * @return 等级列表
     */
    List<MeritLevel> selectAllActive();
    
    /**
     * 根据等级查询
     * 
     * @param level 等级
     * @return 等级信息
     */
    MeritLevel selectByLevel(@Param("level") Integer level);
}