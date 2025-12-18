package com.hsmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.entity.Scripture;
import com.hsmy.mapper.ScriptureMapper;
import com.hsmy.service.ScriptureService;
import com.hsmy.vo.ScriptureQueryVO;
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
    public Page<Scripture> pageScriptures(ScriptureQueryVO queryVO) {
        long pageNum = queryVO != null && queryVO.getPageNum() != null && queryVO.getPageNum() > 0 ? queryVO.getPageNum() : 1;
        long pageSize = queryVO != null && queryVO.getPageSize() != null && queryVO.getPageSize() > 0 ? queryVO.getPageSize() : 10;
        pageSize = Math.min(100, pageSize);

        LambdaQueryWrapper<Scripture> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Scripture::getStatus, 1)
                .eq(Scripture::getIsDeleted, 0);

        if (queryVO != null) {
            if (org.springframework.util.StringUtils.hasText(queryVO.getKeyword())) {
                String keyword = queryVO.getKeyword().trim();
                wrapper.and(w -> w.like(Scripture::getScriptureName, keyword)
                        .or().like(Scripture::getAuthor, keyword)
                        .or().like(Scripture::getDescription, keyword)
                        .or().like(Scripture::getCategoryTags, keyword));
            }
            if (org.springframework.util.StringUtils.hasText(queryVO.getScriptureType())) {
                wrapper.eq(Scripture::getScriptureType, queryVO.getScriptureType().trim());
            }
            if (queryVO.getIsHot() != null) {
                wrapper.eq(Scripture::getIsHot, queryVO.getIsHot());
            }
            if (queryVO.getMinPrice() != null) {
                wrapper.ge(Scripture::getPrice, queryVO.getMinPrice());
            }
            if (queryVO.getMaxPrice() != null) {
                wrapper.le(Scripture::getPrice, queryVO.getMaxPrice());
            }
            if (queryVO.getDifficultyLevel() != null) {
                wrapper.eq(Scripture::getDifficultyLevel, queryVO.getDifficultyLevel());
            }
            if (org.springframework.util.StringUtils.hasText(queryVO.getTag())) {
                wrapper.like(Scripture::getCategoryTags, queryVO.getTag().trim());
            }

            boolean asc = !"desc".equalsIgnoreCase(queryVO.getSortOrder());
            String sortField = queryVO.getSortField();
            boolean sorted = false;
            if (org.springframework.util.StringUtils.hasText(sortField)) {
                switch (sortField.trim().toLowerCase()) {
                    case "read_count":
                        wrapper.orderBy(true, asc, Scripture::getReadCount);
                        sorted = true;
                        break;
                    case "purchase_count":
                        wrapper.orderBy(true, asc, Scripture::getPurchaseCount);
                        sorted = true;
                        break;
                    case "create_time":
                        wrapper.orderBy(true, asc, Scripture::getCreateTime);
                        sorted = true;
                        break;
                    case "price":
                        wrapper.orderBy(true, asc, Scripture::getPrice);
                        sorted = true;
                        break;
                    default:
                        break;
                }
            }
            if (!sorted) {
                wrapper.orderByAsc(Scripture::getSortOrder)
                        .orderByDesc(Scripture::getCreateTime);
            }
        } else {
            wrapper.orderByAsc(Scripture::getSortOrder)
                    .orderByDesc(Scripture::getCreateTime);
        }

        Page<Scripture> page = new Page<>(pageNum, pageSize);
        return scriptureMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Scripture> getScripturesByTag(String tag) {
        return scriptureMapper.selectByTag(tag);
    }

    @Override
    public List<Scripture> getUsableScriptures(Long userId) {
        return scriptureMapper.selectUsableByUser(userId);
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
