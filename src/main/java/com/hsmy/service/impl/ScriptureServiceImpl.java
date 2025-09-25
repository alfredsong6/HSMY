package com.hsmy.service.impl;

import com.hsmy.entity.Scripture;
import com.hsmy.mapper.ScriptureMapper;
import com.hsmy.service.ScriptureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 典籍Service实现类
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Service
@RequiredArgsConstructor
public class ScriptureServiceImpl implements ScriptureService {

    private final ScriptureMapper scriptureMapper;

    @Override
    public List<Scripture> getScripturesByType(String scriptureType) {
        return scriptureMapper.selectByType(scriptureType);
    }

    @Override
    public List<Scripture> getAllActiveScriptures() {
        return scriptureMapper.selectAllActive();
    }

    @Override
    public List<Scripture> getHotScriptures() {
        return scriptureMapper.selectHotScriptures();
    }

    @Override
    public List<Scripture> getScripturesByPriceRange(Integer minPrice, Integer maxPrice) {
        return scriptureMapper.selectByPriceRange(minPrice, maxPrice);
    }

    @Override
    public List<Scripture> getScripturesByDifficultyLevel(Integer difficultyLevel) {
        return scriptureMapper.selectByDifficultyLevel(difficultyLevel);
    }

    @Override
    public Scripture getScriptureById(Long scriptureId) {
        return scriptureMapper.selectById(scriptureId);
    }

    @Override
    public List<Scripture> searchScriptures(String keyword) {
        return scriptureMapper.searchByKeyword(keyword);
    }

    @Override
    public List<Scripture> getScripturesByTag(String tag) {
        return scriptureMapper.selectByTag(tag);
    }

    @Override
    @Transactional
    public Boolean increaseReadCount(Long scriptureId) {
        int result = scriptureMapper.increaseReadCount(scriptureId);
        return result > 0;
    }

    @Override
    @Transactional
    public Boolean increasePurchaseCount(Long scriptureId) {
        int result = scriptureMapper.increasePurchaseCount(scriptureId);
        return result > 0;
    }

    @Override
    public Boolean checkScriptureAvailable(Long scriptureId) {
        Scripture scripture = scriptureMapper.selectById(scriptureId);
        return scripture != null && scripture.getStatus() != null && scripture.getStatus() == 1;
    }

    @Override
    @Transactional
    public Boolean createScripture(Scripture scripture) {
        int result = scriptureMapper.insert(scripture);
        return result > 0;
    }

    @Override
    @Transactional
    public Boolean updateScripture(Scripture scripture) {
        int result = scriptureMapper.updateById(scripture);
        return result > 0;
    }

    @Override
    @Transactional
    public Boolean deleteScripture(Long scriptureId) {
        int result = scriptureMapper.deleteById(scriptureId);
        return result > 0;
    }
}