package com.hsmy.service.impl;

import com.hsmy.dto.MeritGainRequest;
import com.hsmy.dto.MeritGainResult;
import com.hsmy.entity.MeritLevel;
import com.hsmy.entity.UserStats;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.MeritGainService;
import com.hsmy.service.MeritLevelService;
import com.hsmy.utils.UserLockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 统一处理功德增长与等级升级的实现。
 */
@Service
@RequiredArgsConstructor
public class MeritGainServiceImpl implements MeritGainService {

    private final UserStatsMapper userStatsMapper;
    private final MeritLevelService meritLevelService;
    private final UserLockManager userLockManager;

    @Override
    public MeritGainResult gainMeritWithLock(MeritGainRequest request) {
        return userLockManager.executeWithUserLock(request.getUserId(), () -> gainMeritInternal(request));
    }

    @Override
    public MeritGainResult gainMerit(MeritGainRequest request) {
        return gainMeritInternal(request);
    }

    private MeritGainResult gainMeritInternal(MeritGainRequest request) {
        if (request == null || request.getUserId() == null || request.getMeritDelta() == null) {
            throw new BusinessException("功德参数缺失");
        }
        if (request.getMeritDelta() <= 0) {
            throw new BusinessException("功德增量必须为正数");
        }

        UserStats stats = userStatsMapper.selectByUserId(request.getUserId());
        if (stats == null) {
            throw new BusinessException("用户统计信息不存在");
        }

        long currentMerit = stats.getTotalMerit() != null ? stats.getTotalMerit() : 0L;
        long newTotalMerit = currentMerit + request.getMeritDelta();
        int currentLevel = stats.getCurrentLevel() != null ? stats.getCurrentLevel() : 1;
        MeritLevel levelAfter = meritLevelService.checkLevelUp(currentLevel, newTotalMerit);
        int targetLevel = levelAfter != null ? levelAfter.getLevel() : currentLevel;

        int updated;
        if (request.getKnockDelta() != null && request.getKnockDelta() > 0) {
            updated = userStatsMapper.updateKnockStatsAndLevel(
                    request.getUserId(),
                    request.getKnockDelta(),
                    request.getMeritDelta(),
                    targetLevel,
                    request.isUpdateLastKnockTime() ? new Date() : null
            );
        } else {
            updated = userStatsMapper.updateMeritAndLevel(
                    request.getUserId(),
                    request.getMeritDelta(),
                    targetLevel
            );
        }
        if (updated <= 0) {
            throw new BusinessException("更新用户功德失败");
        }

        return MeritGainResult.builder()
                .totalMeritAfter(newTotalMerit)
                .levelAfter(targetLevel)
                .levelUp(levelAfter != null && targetLevel > currentLevel)
                .build();
    }
}
