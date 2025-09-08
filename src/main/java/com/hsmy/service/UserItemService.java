package com.hsmy.service;

import com.hsmy.entity.UserItem;

import java.util.List;

/**
 * 用户道具Service接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
public interface UserItemService {
    
    /**
     * 根据用户ID获取用户道具列表
     * 
     * @param userId 用户ID
     * @return 用户道具列表
     */
    List<UserItem> getUserItemsByUserId(Long userId);
    
    /**
     * 根据用户ID和道具类型获取道具列表
     * 
     * @param userId 用户ID
     * @param itemType 道具类型
     * @return 用户道具列表
     */
    List<UserItem> getUserItemsByType(Long userId, String itemType);
    
    /**
     * 获取用户已装备的道具
     * 
     * @param userId 用户ID
     * @return 已装备道具列表
     */
    List<UserItem> getEquippedItems(Long userId);
    
    /**
     * 购买道具
     * 
     * @param userId 用户ID
     * @param itemId 道具ID
     * @param price 购买价格
     * @return 是否成功
     */
    Boolean purchaseItem(Long userId, Long itemId, Integer price);
    
    /**
     * 装备道具
     * 
     * @param userId 用户ID
     * @param itemId 道具ID
     * @return 是否成功
     */
    Boolean equipItem(Long userId, Long itemId);
    
    /**
     * 卸下道具
     * 
     * @param userId 用户ID
     * @param itemId 道具ID
     * @return 是否成功
     */
    Boolean unequipItem(Long userId, Long itemId);
    
    /**
     * 检查用户是否拥有道具
     * 
     * @param userId 用户ID
     * @param itemId 道具ID
     * @return 是否拥有
     */
    Boolean hasItem(Long userId, Long itemId);
    
    /**
     * 检查道具是否已装备
     * 
     * @param userId 用户ID
     * @param itemId 道具ID
     * @return 是否已装备
     */
    Boolean isItemEquipped(Long userId, Long itemId);
}