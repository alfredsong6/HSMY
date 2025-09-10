package com.hsmy.controller.shop;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.entity.Item;
import com.hsmy.service.ItemService;
import com.hsmy.utils.UserContextUtil;
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
        try {
            List<Item> items;
            
            if (itemType != null) {
                items = itemService.getItemsByType(itemType);
            } else if (category != null) {
                items = itemService.getItemsByCategory(category);
            } else {
                items = itemService.getAllActiveItems();
            }
            
            return Result.success(items);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取限时道具列表
     * 
     * @return 限时道具列表
     */
    @GetMapping("/items/limited")
    public Result<List<Item>> getLimitedItems() {
        try {
            List<Item> items = itemService.getLimitedItems();
            return Result.success(items);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取道具详情
     * 
     * @param itemId 道具ID
     * @return 道具详情
     */
    @GetMapping("/items/{itemId}")
    public Result<Item> getItemDetail(@PathVariable Long itemId) {
        try {
            Item item = itemService.getItemById(itemId);
            if (item == null) {
                return Result.error("道具不存在");
            }
            return Result.success(item);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
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
            
            // 检查道具是否可购买
            if (!itemService.checkItemAvailable(itemId, quantity)) {
                return Result.error("道具不可购买或库存不足");
            }
            
            Item item = itemService.getItemById(itemId);
            if (item == null) {
                return Result.error("道具不存在");
            }
            
            // TODO: 实现购买逻辑
            // 1. 检查用户功德币余额
            // 2. 扣除功德币
            // 3. 添加用户道具
            // 4. 更新道具销量
            // 5. 记录购买记录
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("itemId", itemId);
            result.put("itemName", item.getItemName());
            result.put("quantity", quantity);
            result.put("totalPrice", item.getPrice() * quantity);
            
            return Result.success("购买成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}