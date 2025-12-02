package com.hsmy.service.impl;

import com.hsmy.entity.Item;
import com.hsmy.entity.PurchaseRecord;
import com.hsmy.entity.UserItem;
import com.hsmy.entity.UserStats;
import com.hsmy.entity.meditation.MeritCoinTransaction;
import com.hsmy.vo.AutoAbilityStatusVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hsmy.enums.ItemUsageModeEnum;
import com.hsmy.enums.MeritBizType;
import com.hsmy.enums.UserItemSourceEnum;
import com.hsmy.enums.UserItemUsageStatusEnum;
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
import java.util.Optional;

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
        Item item = itemService.getItemById(itemId);
        if (item == null || item.getIsActive() == null || item.getIsActive() != 1) {
            throw new BusinessException("道具不存在或已下架");
        }
        if (!itemService.checkItemAvailable(itemId, quantity)) {
            throw new BusinessException("道具不可购买或库存不足");
        }
        ItemUsageModeEnum usageModeEnum = ItemUsageModeEnum.from(item.getUsageMode());
        boolean stackable = item.getStackable() != null && item.getStackable() == 1;
        boolean consumable = usageModeEnum == ItemUsageModeEnum.CONSUMABLE;
        boolean allowMultiple = stackable || consumable;
        if (!allowMultiple && quantity > 1) {
            throw new BusinessException("该道具一次仅支持购买1件");
        }
        if (!allowMultiple && hasItem(userId, itemId)) {
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

        Date now = new Date();
        UserItem userItem = new UserItem();
        userItem.setId(IdGenerator.nextId());
        userItem.setUserId(userId);
        userItem.setItemId(itemId);
        userItem.setPurchaseTime(now);
        userItem.setPurchasePrice(unitPrice);
        userItem.setRemainingUses(calculateInitialUses(item, quantity));
        userItem.setUsageStatus(UserItemUsageStatusEnum.INACTIVE.getCode());
        userItem.setLastUsedTime(null);
        userItem.setStackCount(allowMultiple ? quantity : 1);
        userItem.setSourceType(UserItemSourceEnum.SHOP.getCode());
        userItem.setMetadata(null);
        userItem.setIsEquipped(0);
        userItem.setExpireTime(determineExpireTime(now, item));
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
        result.setExpireTime(userItem.getExpireTime());
        result.setUsageMode(usageModeEnum.getCode());
        result.setRemainingUses(userItem.getRemainingUses());
        result.setStackCount(userItem.getStackCount());
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
        Date now = new Date();
        return userItems.stream()
                .anyMatch(userItem -> userItem.getItemId().equals(itemId) &&
                        (userItem.getExpireTime() == null || userItem.getExpireTime().after(now)) &&
                        (userItem.getUsageStatus() == null || !userItem.getUsageStatus().equals(UserItemUsageStatusEnum.USED.getCode())) &&
                        (userItem.getRemainingUses() == null || userItem.getRemainingUses() > 0));
    }
    
    @Override
    public Boolean isItemEquipped(Long userId, Long itemId) {
        List<UserItem> equippedItems = userItemMapper.selectEquippedByUserId(userId);
        return equippedItems.stream()
                .anyMatch(userItem -> userItem.getItemId().equals(itemId));
    }

    @Override
    public AutoAbilityStatusVO getAutoAbilityStatus(Long userId, String itemType) {
        AutoAbilityStatusVO vo = new AutoAbilityStatusVO();
        List<Item> items = itemService.getItemsByType(null, itemType);
        if (items == null || items.isEmpty()) {
            vo.setEnable(false);
            return vo;
        }
        Item baseItem = items.get(0);
        vo.setItemId(baseItem.getId());
        vo.setUsageMode(baseItem.getUsageMode());

        List<UserItem> userItems = userItemMapper.selectByUserIdAndType(userId, itemType);
        UserItem usable = Optional.ofNullable(userItems).orElseGet(java.util.ArrayList::new)
                .stream()
                .filter(ui -> ui.getIsDeleted() == null || ui.getIsDeleted() == 0)
                .filter(ui -> ui.getExpireTime() == null || ui.getExpireTime().after(new Date()))
                .filter(ui -> ui.getRemainingUses() == null || ui.getRemainingUses() > 0)
                .findFirst()
                .orElse(null);

        if (usable != null) {
            vo.setEnable(true);
            vo.setUserItemId(usable.getId());
            vo.setRemainingUses(usable.getRemainingUses());
            vo.setUsageStatus(usable.getUsageStatus());
        } else {
            vo.setEnable(false);
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void consumeItem(Long userId, Long itemId) {
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            throw new BusinessException("道具不存在");
        }
        ItemUsageModeEnum usageMode = ItemUsageModeEnum.from(item.getUsageMode());
        if (usageMode != ItemUsageModeEnum.CONSUMABLE) {
            throw new BusinessException("非一次性道具无需消耗");
        }

        LambdaQueryWrapper<UserItem> query = new LambdaQueryWrapper<>();
        query.eq(UserItem::getUserId, userId)
                .eq(UserItem::getItemId, itemId)
                .eq(UserItem::getIsDeleted, 0)
                .gt(UserItem::getRemainingUses, 0);
        UserItem userItem = userItemMapper.selectOne(query);
        if (userItem == null) {
            throw new BusinessException("未找到可用的道具记录");
        }

        int remaining = Optional.ofNullable(userItem.getRemainingUses()).orElse(0);
        if (remaining <= 0) {
            throw new BusinessException("道具可用次数不足");
        }
        remaining -= 1;
        userItem.setRemainingUses(remaining);
        userItem.setUsageStatus(remaining == 0 ? UserItemUsageStatusEnum.USED.getCode() : UserItemUsageStatusEnum.ACTIVE.getCode());
        userItem.setLastUsedTime(new Date());
        userItemMapper.updateById(userItem);
    }

    private void recordMeritCoinTransaction(Long userId, Long userItemId, String itemName, int totalPrice, long balanceAfter) {
        MeritCoinTransaction tx = new MeritCoinTransaction();
        tx.setUserId(userId);
        tx.setBizType(MeritBizType.ITEM_PURCHASE.getCode());
        tx.setBizId(userItemId);
        tx.setChangeAmount(-totalPrice);
        tx.setBalanceAfter(Math.toIntExact(balanceAfter));
        tx.setRemark("购买道具-" + itemName);
        meritCoinTransactionMapper.insert(tx);
    }

    /**
     * 根据道具模板计算初始次数.
     */
    private Integer calculateInitialUses(Item item, int quantity) {
        Integer maxUses = item.getMaxUses();
        ItemUsageModeEnum usageMode = ItemUsageModeEnum.from(item.getUsageMode());
        if (usageMode == ItemUsageModeEnum.CONSUMABLE) {
            int base = (maxUses != null && maxUses > 0) ? maxUses : 1;
            return base * quantity;
        }
        if (usageMode == ItemUsageModeEnum.TIMED_REPEAT && maxUses != null && maxUses > 0) {
            return maxUses * quantity;
        }
        return null;
    }

    /**
     * 计算过期时间.
     */
    private Date determineExpireTime(Date purchaseTime, Item item) {
        String duration = item.getDuration();
        if (duration == null || duration.trim().isEmpty()) {
            return null;
        }
        String normalized = duration.trim().toLowerCase();
        if (normalized.endsWith("d")) {
            String numberPart = normalized.substring(0, normalized.length() - 1);
            try {
                int days = Integer.parseInt(numberPart);
                if (days > 0) {
                    return Date.from(purchaseTime.toInstant().plusSeconds(days * 24L * 3600L));
                }
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
