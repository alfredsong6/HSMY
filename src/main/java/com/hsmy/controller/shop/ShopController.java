package com.hsmy.controller.shop;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.entity.Item;
import com.hsmy.exception.BusinessException;
import com.hsmy.service.ItemService;
import com.hsmy.service.UserItemService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.UserItemPurchaseResult;
import lombok.RequiredArgsConstructor;
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
    private final UserItemService userItemService;
    
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
    public Result<Map<String, Object>> purchaseItem(@RequestParam Long itemId,
                                                   @RequestParam(defaultValue = "1") Integer quantity,
                                                   HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            UserItemPurchaseResult purchaseResult = userItemService.purchaseItem(userId, itemId, quantity);
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", true);
            payload.put("orderNo", purchaseResult.getOrderNo());
            payload.put("itemId", purchaseResult.getItemId());
            payload.put("itemName", purchaseResult.getItemName());
            payload.put("quantity", purchaseResult.getQuantity());
            payload.put("totalPrice", purchaseResult.getTotalPrice());
            payload.put("remainingCoins", purchaseResult.getRemainingCoins());
            return Result.success("购买成功", payload);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("购买失败：" + e.getMessage());
        }
    }
}
