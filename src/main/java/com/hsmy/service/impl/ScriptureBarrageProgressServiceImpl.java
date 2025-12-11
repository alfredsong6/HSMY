package com.hsmy.service.impl;

import com.hsmy.entity.ScriptureBarrageProgress;
import com.hsmy.mapper.ScriptureBarrageProgressMapper;
import com.hsmy.service.ScriptureBarrageProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ScriptureBarrageProgressServiceImpl implements ScriptureBarrageProgressService {

    private final ScriptureBarrageProgressMapper scriptureBarrageProgressMapper;

    @Override
    public ScriptureBarrageProgress getByUserAndScripture(Long userId, Long scriptureId) {
        if (userId == null || scriptureId == null) {
            return null;
        }
        return scriptureBarrageProgressMapper.selectByUserAndScripture(userId, scriptureId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScriptureBarrageProgress saveProgress(Long userId,
                                                 Long scriptureId,
                                                 Long sectionId,
                                                 Integer sectionOffset,
                                                 Integer lastOffset,
                                                 Integer lastFetchLimit,
                                                 Boolean dailyReset) {
        if (userId == null || scriptureId == null) {
            return null;
        }
        ScriptureBarrageProgress progress = scriptureBarrageProgressMapper.selectByUserAndScripture(userId, scriptureId);
        Date now = new Date();

        int safeOffset = lastOffset == null || lastOffset < 0 ? 0 : lastOffset;
        int safeSectionOffset = sectionOffset == null || sectionOffset < 0 ? 0 : sectionOffset;
        int safeLimit = lastFetchLimit == null || lastFetchLimit < 0 ? 0 : lastFetchLimit;

        if (progress == null) {
            progress = new ScriptureBarrageProgress();
            progress.setUserId(userId);
            progress.setScriptureId(scriptureId);
            // 初始化游标
            progress.setLastOffset(safeOffset);
            progress.setLastSectionId(sectionId);
            progress.setSectionOffset(safeSectionOffset);
            progress.setLastFetchLimit(safeLimit);
            progress.setLastFetchTime(now);
            progress.setIsDailyReset(Boolean.TRUE.equals(dailyReset) ? 1 : 0);
            progress.setIsDeleted(0);
            scriptureBarrageProgressMapper.insert(progress);
        } else {
            // 更新游标与配置
            progress.setLastOffset(safeOffset);
            progress.setLastSectionId(sectionId);
            progress.setSectionOffset(safeSectionOffset);
            progress.setLastFetchLimit(safeLimit);
            progress.setLastFetchTime(now);
            if (dailyReset != null) {
                progress.setIsDailyReset(Boolean.TRUE.equals(dailyReset) ? 1 : 0);
            }
            scriptureBarrageProgressMapper.updateById(progress);
        }
        return progress;
    }
}
