package com.hsmy.service.impl;

import com.hsmy.entity.Item;
import com.hsmy.entity.PurchaseRecord;
import com.hsmy.entity.UserItem;
import com.hsmy.entity.UserStats;
import com.hsmy.entity.meditation.MeritCoinTransaction;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.UserItemMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.mapper.meditation.MeritCoinTransactionMapper;
import com.hsmy.service.ItemService;
import com.hsmy.service.PurchaseRecordService;
import com.hsmy.service.UserItemService;
import com.hsmy.utils.IdGenerator;
import com.hsmy.vo.UserItemPurchaseResult;
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
    private final UserStatsMapper userStatsMapper;
    private final PurchaseRecordService purchaseRecordService;
    private final MeritCoinTransactionMapper meritCoinTransactionMapper;

    private static final String BIZ_TYPE_ITEM_PURCHASE = "ITEM_PURCHASE";
    
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
    public UserItemPurchaseResult purchaseItem(Long userId, Long itemId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("购买数量必须大于0");
        }
        if (quantity > 1) {
            throw new BusinessException("该道具一次仅支持购买1件");
        }
        Item item = itemService.getItemById(itemId);
        if (item == null || item.getIsActive() == null || item.getIsActive() != 1) {
            throw new BusinessException("道具不存在或已下架");
        }
        if (!itemService.checkItemAvailable(itemId, quantity)) {
            throw new BusinessException("道具不可购买或库存不足");
        }
        if (hasItem(userId, itemId)) {
            throw new BusinessException("您已拥有该道具");
        }

        int unitPrice = item.getPrice();
        int totalPrice = unitPrice * quantity;

        UserStats stats = userStatsMapper.selectByUserId(userId);
        long balance = stats != null && stats.getMeritCoins() != null ? stats.getMeritCoins() : 0L;
        if (balance < totalPrice) {
            throw new BusinessException("功德币余额不足，当前余额：" + balance + "，需要：" + totalPrice);
        }
        int updated = userStatsMapper.reduceMeritCoins(userId, (long) totalPrice);
        if (updated <= 0) {
            throw new BusinessException("扣除功德币失败，请稍后再试");
        }

        UserItem userItem = new UserItem();
        userItem.setId(IdGenerator.nextId());
        userItem.setUserId(userId);
        userItem.setItemId(itemId);
        userItem.setPurchaseTime(new Date());
        userItem.setPurchasePrice(unitPrice);
        userItem.setIsEquipped(0);
        int insertResult = userItemMapper.insert(userItem);
        if (insertResult <= 0) {
            throw new BusinessException("添加用户道具失败");
        }

        Boolean soldUpdated = itemService.updateSoldCount(itemId, quantity);
        if (!Boolean.TRUE.equals(soldUpdated)) {
            throw new BusinessException("更新道具销量失败");
        }
        PurchaseRecord record = purchaseRecordService.createPurchaseRecord(userId, itemId, unitPrice, quantity, totalPrice);
        long remainingCoins = balance - totalPrice;
        recordMeritCoinTransaction(userId, userItem.getId(), item.getItemName(), totalPrice, remainingCoins);

        UserItemPurchaseResult result = new UserItemPurchaseResult();
        result.setOrderNo(record.getOrderNo());
        result.setItemId(itemId);
        result.setItemName(item.getItemName());
        result.setQuantity(quantity);
        result.setTotalPrice(totalPrice);
        result.setRemainingCoins(remainingCoins);
        return result;
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

    private void recordMeritCoinTransaction(Long userId, Long userItemId, String itemName, int totalPrice, long balanceAfter) {
        MeritCoinTransaction tx = new MeritCoinTransaction();
        tx.setUserId(userId);
        tx.setBizType(BIZ_TYPE_ITEM_PURCHASE);
        tx.setBizId(userItemId);
        tx.setChangeAmount(-totalPrice);
        tx.setBalanceAfter(Math.toIntExact(balanceAfter));
        tx.setRemark("购买道具-" + itemName);
        meritCoinTransactionMapper.insert(tx);
    }
}
