package com.hsmy.service;

import com.hsmy.entity.MeritLevel;

import java.util.List;

/**
 * 功德等级Service接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
public interface MeritLevelService {
    
    /**
     * 根据功德值获取对应等级
     * 
     * @param meritValue 功德值
     * @return 功德等级信息
     */
    MeritLevel getMeritLevelByValue(Long meritValue);
    
    /**
     * 获取所有启用的等级配置
     * 
     * @return 等级列表
     */
    List<MeritLevel> getAllActiveLevels();
    
    /**
     * 根据等级获取等级信息
     * 
     * @param level 等级
     * @return 等级信息
     */
    MeritLevel getMeritLevelByLevel(Integer level);
    
    /**
     * 检查是否升级
     * 
     * @param currentLevel 当前等级
     * @param meritValue 功德值
     * @return 新等级，如果没有升级返回null
     */
    MeritLevel checkLevelUp(Integer currentLevel, Long meritValue);
    
    /**
     * 获取下一等级信息
     * 
     * @param currentLevel 当前等级
     * @return 下一等级信息，如果已是最高等级返回null
     */
    MeritLevel getNextLevel(Integer currentLevel);
    
    /**
     * 计算升级所需功德值
     * 
     * @param currentLevel 当前等级
     * @param currentMerit 当前功德值
     * @return 升级所需功德值，如果已是最高等级返回-1
     */
    Long getRequiredMeritForNextLevel(Integer currentLevel, Long currentMerit);
}