package com.hsmy.service.impl;

import com.hsmy.entity.ScriptureSection;
import com.hsmy.mapper.ScriptureSectionMapper;
import com.hsmy.service.ScriptureSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScriptureSectionServiceImpl implements ScriptureSectionService {

    private final ScriptureSectionMapper scriptureSectionMapper;

    @Override
    public List<ScriptureSection> listByScriptureId(Long scriptureId) {
        return scriptureSectionMapper.selectByScriptureId(scriptureId);
    }

    @Override
    public ScriptureSection getFirstSection(Long scriptureId) {
        return scriptureSectionMapper.selectFirstSection(scriptureId);
    }

    @Override
    public ScriptureSection getById(Long sectionId) {
        return scriptureSectionMapper.selectById(sectionId);
    }

    @Override
    public ScriptureSection getByScriptureAndNo(Long scriptureId, Integer sectionNo) {
        return scriptureSectionMapper.selectByScriptureAndNo(scriptureId, sectionNo);
    }
}
