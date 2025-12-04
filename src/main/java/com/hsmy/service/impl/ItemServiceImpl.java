package com.hsmy.service.impl;

import com.hsmy.entity.Item;
import com.hsmy.entity.UserItem;
import com.hsmy.mapper.ItemMapper;
import com.hsmy.mapper.UserItemMapper;
import com.hsmy.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 道具Service实现类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    
    private final ItemMapper itemMapper;
    private final UserItemMapper userItemMapper;
    
    @Override
    public List<Item> getItemsByType(Long userId, String itemType) {
        List<Item> items = itemMapper.selectByType(itemType);
        if (items == null || userId == null) {
            return items;
        }

        List<UserItem> userItems = userItemMapper.selectOwnedByUserId(userId, itemType);
        Set<Long> ownedItemIds = Optional.ofNullable(userItems)
                .orElseGet(ArrayList::new)
                .stream()
                .map(UserItem::getItemId)
                .collect(Collectors.toSet());

        List<Item> filtered = items.stream()
                .filter(item -> {
                    if (item == null) {
                        return false;
                    }
                    boolean limited = item.getIsLimited() != null && item.getIsLimited() == 1;
                    return !limited || !ownedItemIds.contains(item.getId());
                })
                .collect(Collectors.toCollection(ArrayList::new));
        if (filtered.size() <= 1) {
            return filtered;
        }

        Map<Integer, List<Item>> bySort = new HashMap<>();
        for (Item item : filtered) {
            if (item == null || item.getSortOrder() == null) {
                continue;
            }
            bySort.computeIfAbsent(item.getSortOrder(), k -> new ArrayList<>()).add(item);
        }

        List<Item> result = new ArrayList<>(filtered);
        for (Map.Entry<Integer, List<Item>> entry : bySort.entrySet()) {
            List<Item> sameSortItems = entry.getValue();
            if (sameSortItems.size() < 2) {
                continue;
            }
            Optional<Item> limitedOnce = sameSortItems.stream()
                    .filter(i -> i.getMaxUses() != null && i.getMaxUses() == 1)
                    .findFirst();
            if (!limitedOnce.isPresent()) {
                continue;
            }
            Item maxOnceItem = limitedOnce.get();
            boolean owned = ownedItemIds.contains(maxOnceItem.getId());
            if (owned) {
                result.remove(maxOnceItem);
            } else {
                sameSortItems.stream()
                        .filter(i -> !i.getId().equals(maxOnceItem.getId()))
                        .findFirst()
                        .ifPresent(result::remove);
            }
        }

        return result;
    }
    
    @Override
    public List<Item> getAllActiveItems() {
        return itemMapper.selectAllActive();
    }
    
    @Override
    public List<Item> getItemsByCategory(String category) {
        return itemMapper.selectByCategory(category);
    }
    
    @Override
    public List<Item> getLimitedItems() {
        return itemMapper.selectLimitedItems();
    }
    
    @Override
    public Item getItemById(Long itemId) {
        return itemMapper.selectById(itemId);
    }
    
    @Override
    public Boolean checkItemAvailable(Long itemId, Integer quantity) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            return false;
        }
        
        // 检查是否上架
        if (item.getIsActive() == 0) {
            return false;
        }
        
        // 检查库存
        if (item.getStock() != -1 && item.getStock() < quantity) {
            return false;
        }
        
        // 检查限时道具是否在有效期内
        if (item.getIsLimited() == 1) {
            java.util.Date now = new java.util.Date();
            if (item.getLimitTimeStart() != null && now.before(item.getLimitTimeStart())) {
                return false;
            }
            if (item.getLimitTimeEnd() != null && now.after(item.getLimitTimeEnd())) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSoldCount(Long itemId, Integer quantity) {
        return itemMapper.updateSoldCount(itemId, quantity) > 0;
    }
}
