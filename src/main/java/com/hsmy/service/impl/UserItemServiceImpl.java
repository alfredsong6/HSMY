package com.hsmy.service.impl;

import com.hsmy.entity.Item;
import com.hsmy.entity.UserItem;
import com.hsmy.mapper.UserItemMapper;
import com.hsmy.service.ItemService;
import com.hsmy.service.UserItemService;
import com.hsmy.service.UserService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户道具Service实现类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Service
@RequiredArgsConstructor
public class UserItemServiceImpl implements UserItemService {
    
    private final UserItemMapper userItemMapper;
    private final ItemService itemService;
    
    @Override
    public List<UserItem> getUserItemsByUserId(Long userId) {
        return userItemMapper.selectByUserId(userId);
    }
    
    @Override
    public List<UserItem> getUserItemsByType(Long userId, String itemType) {
        return userItemMapper.selectByUserIdAndType(userId, itemType);
    }
    
    @Override
    public List<UserItem> getEquippedItems(Long userId) {
        return userItemMapper.selectEquippedByUserId(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean purchaseItem(Long userId, Long itemId, Integer price) {
        // 检查道具是否可购买
        if (!itemService.checkItemAvailable(itemId, 1)) {
            throw new RuntimeException("道具不可购买");
        }
        
        // 检查用户是否已拥有该道具
        if (hasItem(userId, itemId)) {
            throw new RuntimeException("您已拥有该道具");
        }
        
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            throw new RuntimeException("道具不存在");
        }
        
        // 创建用户道具记录
        UserItem userItem = new UserItem();
        userItem.setId(IdGenerator.nextId());
        userItem.setUserId(userId);
        userItem.setItemId(itemId);
        userItem.setPurchaseTime(new Date());
        userItem.setPurchasePrice(price);
        userItem.setIsEquipped(0);
        
        int result = userItemMapper.insert(userItem);
        if (result > 0) {
            // 更新道具销量
            itemService.updateSoldCount(itemId, 1);
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean equipItem(Long userId, Long itemId) {
        // 检查用户是否拥有该道具
        if (!hasItem(userId, itemId)) {
            throw new RuntimeException("您未拥有该道具");
        }
        
        // 获取道具信息
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            throw new RuntimeException("道具不存在");
        }
        
        // 先卸下同类型的其他道具
        userItemMapper.unequipByUserIdAndType(userId, item.getItemType());
        
        // 装备当前道具
        return userItemMapper.updateEquipStatus(userId, itemId, 1) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unequipItem(Long userId, Long itemId) {
        return userItemMapper.updateEquipStatus(userId, itemId, 0) > 0;
    }
    
    @Override
    public Boolean hasItem(Long userId, Long itemId) {
        List<UserItem> userItems = userItemMapper.selectByUserId(userId);
        return userItems.stream()
                .anyMatch(userItem -> userItem.getItemId().equals(itemId) &&
                        (userItem.getExpireTime() == null || userItem.getExpireTime().after(new Date())));
    }
    
    @Override
    public Boolean isItemEquipped(Long userId, Long itemId) {
        List<UserItem> equippedItems = userItemMapper.selectEquippedByUserId(userId);
        return equippedItems.stream()
                .anyMatch(userItem -> userItem.getItemId().equals(itemId));
    }
}