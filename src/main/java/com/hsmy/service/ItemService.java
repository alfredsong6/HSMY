package com.hsmy.service;

import com.hsmy.entity.Item;

import java.util.List;

/**
 * 道具Service接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
public interface ItemService {
    
    /**
     * 根据道具类型获取道具列表
     * 
     * @param itemType 道具类型
     * @return 道具列表
     */
    List<Item> getItemsByType(String itemType);
    
    /**
     * 获取所有上架的道具
     * 
     * @return 道具列表
     */
    List<Item> getAllActiveItems();
    
    /**
     * 根据分类获取道具列表
     * 
     * @param category 道具分类
     * @return 道具列表
     */
    List<Item> getItemsByCategory(String category);
    
    /**
     * 获取限时道具列表
     * 
     * @return 限时道具列表
     */
    List<Item> getLimitedItems();
    
    /**
     * 根据ID获取道具信息
     * 
     * @param itemId 道具ID
     * @return 道具信息
     */
    Item getItemById(Long itemId);
    
    /**
     * 检查道具是否可购买
     * 
     * @param itemId 道具ID
     * @param quantity 购买数量
     * @return 是否可购买
     */
    Boolean checkItemAvailable(Long itemId, Integer quantity);
    
    /**
     * 更新道具销量
     * 
     * @param itemId 道具ID
     * @param quantity 购买数量
     * @return 是否成功
     */
    Boolean updateSoldCount(Long itemId, Integer quantity);
}