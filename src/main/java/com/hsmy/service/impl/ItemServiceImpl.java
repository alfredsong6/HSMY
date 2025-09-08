package com.hsmy.service.impl;

import com.hsmy.entity.Item;
import com.hsmy.mapper.ItemMapper;
import com.hsmy.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    
    @Override
    public List<Item> getItemsByType(String itemType) {
        return itemMapper.selectByType(itemType);
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