package com.hsmy.service.impl;

import com.hsmy.entity.UserScriptureProgress;
import com.hsmy.mapper.UserScriptureProgressMapper;
import com.hsmy.service.UserScriptureProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserScriptureProgressServiceImpl implements UserScriptureProgressService {

    private final UserScriptureProgressMapper userScriptureProgressMapper;

    @Override
    public UserScriptureProgress getByUserAndSection(Long userId, Long sectionId) {
        return userScriptureProgressMapper.selectByUserAndSection(userId, sectionId);
    }

    @Override
    public UserScriptureProgress getLatestByUserAndScripture(Long userId, Long scriptureId) {
        return userScriptureProgressMapper.selectLatestByUserAndScripture(userId, scriptureId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserScriptureProgress saveSectionProgress(Long userId,
                                                     Long scriptureId,
                                                     Long sectionId,
                                                     Double sectionProgress,
                                                     Integer lastPosition,
                                                     Integer spendSeconds,
                                                     boolean completed) {
        UserScriptureProgress progress = userScriptureProgressMapper.selectByUserAndSection(userId, sectionId);
        Date now = new Date();
        BigDecimal progressValue = sectionProgress == null ? BigDecimal.ZERO : BigDecimal.valueOf(sectionProgress);
        Integer finalSpendSeconds = spendSeconds == null ? 0 : spendSeconds;
        Integer completeFlag = completed || (sectionProgress != null && sectionProgress >= 100D) ? 1 : 0;

        if (progress == null) {
            progress = new UserScriptureProgress();
            progress.setUserId(userId);
            progress.setScriptureId(scriptureId);
            progress.setSectionId(sectionId);
            progress.setReadingProgress(progressValue);
            progress.setLastPosition(lastPosition);
            progress.setLastReadTime(now);
            progress.setSpendSeconds(finalSpendSeconds);
            progress.setIsCompleted(completeFlag);
            progress.setIsDeleted(0);
            userScriptureProgressMapper.insert(progress);
        } else {
            progress.setReadingProgress(progressValue);
            progress.setLastPosition(lastPosition);
            progress.setLastReadTime(now);
            progress.setSpendSeconds(progress.getSpendSeconds() == null ? finalSpendSeconds : progress.getSpendSeconds() + finalSpendSeconds);
            progress.setIsCompleted(completeFlag);
            userScriptureProgressMapper.updateById(progress);
        }
        return progress;
    }

    @Override
    public Integer countCompletedSections(Long userId, Long scriptureId) {
        return userScriptureProgressMapper.countCompletedByScripture(userId, scriptureId);
    }
}
