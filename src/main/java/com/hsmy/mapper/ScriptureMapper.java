package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.Scripture;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 典籍Mapper接口
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Mapper
public interface ScriptureMapper extends BaseMapper<Scripture> {

    /**
     * 根据典籍类型查询典籍列表
     *
     * @param scriptureType 典籍类型
     * @return 典籍列表
     */
    List<Scripture> selectByType(@Param("scriptureType") String scriptureType);

    /**
     * 查询所有上架的典籍
     *
     * @return 典籍列表
     */
    List<Scripture> selectAllActive();

    /**
     * 查询热门典籍
     *
     * @return 热门典籍列表
     */
    List<Scripture> selectHotScriptures();

    /**
     * 根据价格范围查询典籍
     *
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 典籍列表
     */
    List<Scripture> selectByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);

    /**
     * 根据难度等级查询典籍
     *
     * @param difficultyLevel 难度等级
     * @return 典籍列表
     */
    List<Scripture> selectByDifficultyLevel(@Param("difficultyLevel") Integer difficultyLevel);

    /**
     * 更新典籍购买次数
     *
     * @param scriptureId 典籍ID
     * @return 影响行数
     */
    int increasePurchaseCount(@Param("scriptureId") Long scriptureId);

    /**
     * 更新典籍阅读次数
     *
     * @param scriptureId 典籍ID
     * @return 影响行数
     */
    int increaseReadCount(@Param("scriptureId") Long scriptureId);

    /**
     * 根据关键词搜索典籍
     *
     * @param keyword 关键词
     * @return 典籍列表
     */
    List<Scripture> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 根据标签查询典籍
     *
     * @param tag 标签
     * @return 典籍列表
     */
    List<Scripture> selectByTag(@Param("tag") String tag);
}