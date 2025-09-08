package com.hsmy.service.impl;

import com.hsmy.entity.MeritLevel;
import com.hsmy.mapper.MeritLevelMapper;
import com.hsmy.service.MeritLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 功德等级Service实现类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Service
@RequiredArgsConstructor
public class MeritLevelServiceImpl implements MeritLevelService {
    
    private final MeritLevelMapper meritLevelMapper;
    
    @Override
    public MeritLevel getMeritLevelByValue(Long meritValue) {
        return meritLevelMapper.selectByMeritValue(meritValue);
    }
    
    @Override
    public List<MeritLevel> getAllActiveLevels() {
        return meritLevelMapper.selectAllActive();
    }
    
    @Override
    public MeritLevel getMeritLevelByLevel(Integer level) {
        return meritLevelMapper.selectByLevel(level);
    }
    
    @Override
    public MeritLevel checkLevelUp(Integer currentLevel, Long meritValue) {
        MeritLevel newLevel = getMeritLevelByValue(meritValue);
        if (newLevel != null && newLevel.getLevel() > currentLevel) {
            return newLevel;
        }
        return null;
    }
    
    @Override
    public MeritLevel getNextLevel(Integer currentLevel) {
        List<MeritLevel> levels = getAllActiveLevels();
        return levels.stream()
                .filter(level -> level.getLevel() == currentLevel + 1)
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public Long getRequiredMeritForNextLevel(Integer currentLevel, Long currentMerit) {
        MeritLevel nextLevel = getNextLevel(currentLevel);
        if (nextLevel == null) {
            return -1L; // 已是最高等级
        }
        
        return Math.max(0L, nextLevel.getMinMerit() - currentMerit);
    }
}