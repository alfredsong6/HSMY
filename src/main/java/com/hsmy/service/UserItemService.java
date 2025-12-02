package com.hsmy.service;

import com.hsmy.entity.UserItem;
import com.hsmy.vo.UserItemPurchaseResult;

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
     * @param quantity 购买数量
     * @return 购买结果
     */
    UserItemPurchaseResult purchaseItem(Long userId, Long itemId, Integer quantity);
    
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

    /**
     * 消耗一次性/限次道具的使用次数。
     *
     * @param userId 用户ID
     * @param itemId 道具ID
     */
    void consumeItem(Long userId, Long itemId);

    /**
     * 查询自动能力状态（自动敲击/冥想等）。
     *
     * @param userId   用户ID
     * @param itemType 道具类型
     * @return 能力状态
     */
    com.hsmy.vo.AutoAbilityStatusVO getAutoAbilityStatus(Long userId, String itemType);
}
