package com.hsmy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.entity.Scripture;
import com.hsmy.vo.ScriptureQueryVO;

import java.util.List;

/**
 * 典籍Service接口
 *
 * @author HSMY
 * @date 2025/09/25
 */
public interface ScriptureService {

    /**
     * 根据典籍类型获取典籍列表
     *
     * @param scriptureType 典籍类型
     * @return 典籍列表
     */
    List<Scripture> getScripturesByType(String scriptureType);

    /**
     * 获取所有上架的典籍
     *
     * @return 典籍列表
     */
    List<Scripture> getAllActiveScriptures();

    /**
     * 获取热门典籍列表
     *
     * @return 热门典籍列表
     */
    List<Scripture> getHotScriptures();

    /**
     * 根据价格范围获取典籍列表
     *
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 典籍列表
     */
    List<Scripture> getScripturesByPriceRange(Integer minPrice, Integer maxPrice);

    /**
     * 根据难度等级获取典籍列表
     *
     * @param difficultyLevel 难度等级
     * @return 典籍列表
     */
    List<Scripture> getScripturesByDifficultyLevel(Integer difficultyLevel);

    /**
     * 根据ID获取典籍详情
     *
     * @param scriptureId 典籍ID
     * @return 典籍详情
     */
    Scripture getScriptureById(Long scriptureId);

    /**
     * 根据关键词搜索典籍
     *
     * @param keyword 关键词
     * @return 典籍列表
     */
    List<Scripture> searchScriptures(String keyword);

    /**
     * 分页查询典籍列表
     *
     * @param queryVO 查询条件
     * @return 分页结果
     */
    Page<Scripture> pageScriptures(ScriptureQueryVO queryVO);

    /**
     * 根据标签获取典籍列表
     *
     * @param tag 标签
     * @return 典籍列表
     */
    List<Scripture> getScripturesByTag(String tag);

    /**
     * 获取用户可用的典籍列表（免费或已付费有效）
     *
     * @param userId 用户ID
     * @return 典籍列表
     */
    List<Scripture> getUsableScriptures(Long userId);

    /**
     * 增加典籍阅读次数
     *
     * @param scriptureId 典籍ID
     * @return 是否成功
     */
    Boolean increaseReadCount(Long scriptureId);

    /**
     * 增加典籍购买次数
     *
     * @param scriptureId 典籍ID
     * @return 是否成功
     */
    Boolean increasePurchaseCount(Long scriptureId);

    /**
     * 检查典籍是否可购买
     *
     * @param scriptureId 典籍ID
     * @return 是否可购买
     */
    Boolean checkScriptureAvailable(Long scriptureId);

    /**
     * 创建典籍
     *
     * @param scripture 典籍信息
     * @return 是否成功
     */
    Boolean createScripture(Scripture scripture);

    /**
     * 更新典籍信息
     *
     * @param scripture 典籍信息
     * @return 是否成功
     */
    Boolean updateScripture(Scripture scripture);

    /**
     * 删除典籍
     *
     * @param scriptureId 典籍ID
     * @return 是否成功
     */
    Boolean deleteScripture(Long scriptureId);
}
