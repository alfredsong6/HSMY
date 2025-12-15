package com.hsmy.service;

import com.hsmy.entity.ScriptureBarrageProgress;

public interface ScriptureBarrageProgressService {

    /**
     * 查询用户在典籍的弹幕进度
     */
    ScriptureBarrageProgress getByUserAndScripture(Long userId, Long scriptureId);

    /**
     * 保存或更新弹幕进度
     */
    ScriptureBarrageProgress saveProgress(Long userId,
                                          Long scriptureId,
                                          Long sectionId,
                                          Integer sectionOffset,
                                          Integer lastOffset,
                                          Integer lastFetchLimit,
                                          Boolean dailyReset);

    /**
     * 重置阅读/弹幕进度到开头
     */
    ScriptureBarrageProgress resetProgress(Long userId,
                                           Long scriptureId,
                                           Long firstSectionId,
                                           Boolean dailyReset);
}
