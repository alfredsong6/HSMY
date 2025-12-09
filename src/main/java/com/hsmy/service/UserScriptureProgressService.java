package com.hsmy.service;

import com.hsmy.entity.UserScriptureProgress;

public interface UserScriptureProgressService {

    /**
     * 获取用户在指定分段的进度
     */
    UserScriptureProgress getByUserAndSection(Long userId, Long sectionId);

    /**
     * 获取用户最近阅读的分段
     */
    UserScriptureProgress getLatestByUserAndScripture(Long userId, Long scriptureId);

    /**
     * 保存或更新分段进度
     */
    UserScriptureProgress saveSectionProgress(Long userId,
                                              Long scriptureId,
                                              Long sectionId,
                                              Double sectionProgress,
                                              Integer lastPosition,
                                              Integer spendSeconds,
                                              boolean completed);

    /**
     * 统计已完成分段数量
     */
    Integer countCompletedSections(Long userId, Long scriptureId);
}
