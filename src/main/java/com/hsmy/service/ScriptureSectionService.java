package com.hsmy.service;

import com.hsmy.entity.ScriptureSection;

import java.util.List;

public interface ScriptureSectionService {

    /**
     * 查询典籍的分段列表
     */
    List<ScriptureSection> listByScriptureId(Long scriptureId);

    /**
     * 获取典籍的首段
     */
    ScriptureSection getFirstSection(Long scriptureId);

    /**
     * 根据ID获取分段
     */
    ScriptureSection getById(Long sectionId);

    /**
     * 根据典籍与序号获取分段
     */
    ScriptureSection getByScriptureAndNo(Long scriptureId, Integer sectionNo);
}
