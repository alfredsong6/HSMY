package com.hsmy.service.impl;

import com.hsmy.entity.MeritLevel;
import com.hsmy.entity.UserStats;
import com.hsmy.mapper.MeritLevelMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.MeritLevelService;
import com.hsmy.vo.MeritLevelProgressVO;
import com.hsmy.vo.MeritLevelStatusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ���µȼ�Serviceʵ����.
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Service
@RequiredArgsConstructor
public class MeritLevelServiceImpl implements MeritLevelService {
    
    private final MeritLevelMapper meritLevelMapper;
    private final UserStatsMapper userStatsMapper;
    
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
            return -1L;
        }
        
        return Math.max(0L, nextLevel.getMinMerit() - currentMerit);
    }

    @Override
    public MeritLevelStatusVO getUserCurrentLevel(Long userId) {
        MeritLevelProgressVO progress = getUserLevelProgress(userId);
        return progress.getCurrentLevelDetail();
    }

    @Override
    public MeritLevelProgressVO getUserLevelProgress(Long userId) {
        long totalMerit = resolveTotalMerit(userId);
        List<MeritLevel> levels = getAllActiveLevels();
        MeritLevel currentLevel = getMeritLevelByValue(totalMerit);
        Integer currentLevelValue = currentLevel != null ? currentLevel.getLevel() : null;

        MeritLevel nextLevel = findNextLevel(levels, currentLevelValue);
        Long remainingToNext = null;
        if (currentLevelValue != null) {
            if (nextLevel != null) {
                remainingToNext = Math.max(0L, nextLevel.getMinMerit() - totalMerit);
            } else {
                remainingToNext = 0L;
            }
        }

        List<MeritLevelStatusVO> levelStatuses = new ArrayList<>();
        MeritLevelStatusVO currentLevelVO = null;
        for (MeritLevel level : levels) {
            MeritLevelStatusVO statusVO = toStatusVO(level);
            if (currentLevelValue != null && Objects.equals(level.getLevel(), currentLevelValue)) {
                statusVO.setStatus("IN_PROGRESS");
                statusVO.setRemainingMerit(remainingToNext);
                statusVO.setCurrent(true);
                currentLevelVO = statusVO;
            } else if (currentLevelValue != null && level.getLevel() < currentLevelValue) {
                statusVO.setStatus("COMPLETED");
                statusVO.setRemainingMerit(0L);
                statusVO.setCurrent(false);
            } else {
                statusVO.setStatus("NOT_STARTED");
                statusVO.setRemainingMerit(null);
                statusVO.setCurrent(false);
            }
            levelStatuses.add(statusVO);
        }

        MeritLevelProgressVO progress = new MeritLevelProgressVO();
        progress.setUserId(userId);
        progress.setTotalMerit(totalMerit);
        progress.setCurrentLevel(currentLevelValue);
        progress.setRemainingMeritToNextLevel(remainingToNext);
        progress.setCurrentLevelDetail(currentLevelVO);
        progress.setLevels(levelStatuses);
        return progress;
    }

    private MeritLevel findNextLevel(List<MeritLevel> levels, Integer currentLevel) {
        if (currentLevel == null) {
            return null;
        }
        return levels.stream()
                .filter(level -> level.getLevel() != null && level.getLevel() > currentLevel)
                .findFirst()
                .orElse(null);
    }

    private MeritLevelStatusVO toStatusVO(MeritLevel level) {
        if (level == null) {
            return null;
        }
        MeritLevelStatusVO vo = new MeritLevelStatusVO();
        vo.setLevel(level.getLevel());
        vo.setLevelName(level.getLevelName());
        vo.setMinMerit(level.getMinMerit());
        vo.setMaxMerit(level.getMaxMerit());
        vo.setLevelBenefits(level.getLevelBenefits());
        vo.setIconUrl(level.getIconUrl());
        vo.setBonusRate(level.getBonusRate());
        vo.setDailyExchangeLimit(level.getDailyExchangeLimit());
        return vo;
    }

    private long resolveTotalMerit(Long userId) {
        if (userId == null) {
            return 0L;
        }
        UserStats stats = userStatsMapper.selectByUserId(userId);
        if (stats == null || stats.getTotalMerit() == null) {
            return 0L;
        }
        return stats.getTotalMerit();
    }
}
