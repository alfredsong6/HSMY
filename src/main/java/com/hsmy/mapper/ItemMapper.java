package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 道具Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Mapper
public interface ItemMapper extends BaseMapper<Item> {
    
    /**
     * 根据道具类型查询道具列表
     * 
     * @param itemType 道具类型
     * @return 道具列表
     */
    List<Item> selectByType(@Param("itemType") String itemType);
    
    /**
     * 查询所有上架的道具
     * 
     * @return 道具列表
     */
    List<Item> selectAllActive();
    
    /**
     * 根据分类查询道具
     * 
     * @param category 道具分类
     * @return 道具列表
     */
    List<Item> selectByCategory(@Param("category") String category);
    
    /**
     * 查询限时道具
     * 
     * @return 限时道具列表
     */
    List<Item> selectLimitedItems();
    
    /**
     * 更新道具销量
     * 
     * @param itemId 道具ID
     * @param quantity 购买数量
     * @return 影响行数
     */
    int updateSoldCount(@Param("itemId") Long itemId, @Param("quantity") Integer quantity);
}