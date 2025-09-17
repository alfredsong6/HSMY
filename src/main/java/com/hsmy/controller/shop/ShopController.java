package com.hsmy.controller.shop;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.entity.Item;
import com.hsmy.entity.PurchaseRecord;
import com.hsmy.entity.UserStats;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.ItemService;
import com.hsmy.service.MeritService;
import com.hsmy.service.PurchaseRecordService;
import com.hsmy.service.UserItemService;
import com.hsmy.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商城Controller
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@RestController
@RequestMapping("/shop")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class ShopController {
    
    private final ItemService itemService;
    private final MeritService meritService;
    private final UserItemService userItemService;
    private final PurchaseRecordService purchaseRecordService;
    private final UserStatsMapper userStatsMapper;
    
    /**
     * 获取道具列表
     * 
     * @param itemType 道具类型（可选）
     * @param category 道具分类（可选）
     * @return 道具列表
     */
    @GetMapping("/items")
    public Result<List<Item>> getItems(@RequestParam(required = false) String itemType,
                                      @RequestParam(required = false) String category) {
        List<Item> items;
        
        if (itemType != null) {
            items = itemService.getItemsByType(itemType);
        } else if (category != null) {
            items = itemService.getItemsByCategory(category);
        } else {
            items = itemService.getAllActiveItems();
        }
        
        return Result.success(items);
    }
    
    /**
     * 获取限时道具列表
     * 
     * @return 限时道具列表
     */
    @GetMapping("/items/limited")
    public Result<List<Item>> getLimitedItems() {
        List<Item> items = itemService.getLimitedItems();
        return Result.success(items);
    }
    
    /**
     * 获取道具详情
     * 
     * @param itemId 道具ID
     * @return 道具详情
     */
    @GetMapping("/items/{itemId}")
    public Result<Item> getItemDetail(@PathVariable Long itemId) {
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            throw new BusinessException("道具不存在");
        }
        return Result.success(item);
    }
    
    /**
     * 购买道具
     * 
     * @param itemId 道具ID
     * @param quantity 购买数量（默认1）
     * @param request HTTP请求
     * @return 购买结果
     */
    @PostMapping("/purchase")
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> purchaseItem(@RequestParam Long itemId,
                                                   @RequestParam(defaultValue = "1") Integer quantity,
                                                   HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            // 检查道具是否可购买
            if (!itemService.checkItemAvailable(itemId, quantity)) {
                return Result.error("道具不可购买或库存不足");
            }
            
            Item item = itemService.getItemById(itemId);
            if (item == null) {
                return Result.error("道具不存在");
            }
            
            // 计算总价
            Integer totalPrice = item.getPrice() * quantity;
            
            // 1. 检查用户功德币余额
            UserStats userStats = userStatsMapper.selectByUserId(userId);
            if (userStats == null) {
                return Result.error("用户统计信息不存在");
            }
            
            if (userStats.getMeritCoins() < totalPrice) {
                return Result.error("功德币余额不足，当前余额：" + userStats.getMeritCoins() + "，需要：" + totalPrice);
            }
            
            // 2. 扣除功德币
            int updateResult = userStatsMapper.reduceMeritCoins(userId, totalPrice.longValue());
            if (updateResult <= 0) {
                return Result.error("扣除功德币失败");
            }
            
            // 3. 添加用户道具
            for (int i = 0; i < quantity; i++) {
                Boolean purchaseResult = userItemService.purchaseItem(userId, itemId, item.getPrice());
                if (!purchaseResult) {
                    throw new RuntimeException("添加用户道具失败");
                }
            }
            
            // 4. 更新道具销量
            Boolean updateSoldResult = itemService.updateSoldCount(itemId, quantity);
            if (!updateSoldResult) {
                throw new RuntimeException("更新道具销量失败");
            }
            
            // 5. 记录购买记录
            PurchaseRecord purchaseRecord = purchaseRecordService.createPurchaseRecord(
                userId, itemId, item.getPrice(), quantity, totalPrice
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("orderNo", purchaseRecord.getOrderNo());
            result.put("itemId", itemId);
            result.put("itemName", item.getItemName());
            result.put("quantity", quantity);
            result.put("totalPrice", totalPrice);
            result.put("remainingCoins", userStats.getMeritCoins() - totalPrice);
            
            return Result.success("购买成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}