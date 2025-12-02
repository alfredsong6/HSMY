package com.hsmy.controller.user;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.entity.UserItem;
import com.hsmy.service.UserItemService;
import com.hsmy.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户道具Controller
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@RestController
@RequestMapping("/user")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class UserItemController {
    
    private final UserItemService userItemService;
    
//    /**
//     * 购买道具
//     *
//     * @param itemId 道具ID
//     * @param quantity 购买数量
//     * @param request HTTP请求
//     * @return 购买结果
//     */
//    @PostMapping("/items/purchase")
//    public Result<UserItemPurchaseResult> purchaseItem(@RequestParam Long itemId,
//                                                       @RequestParam(defaultValue = "1") Integer quantity,
//                                                       HttpServletRequest request) {
//        try {
//            Long userId = UserContextUtil.requireCurrentUserId();
//            UserItemPurchaseResult result = userItemService.purchaseItem(userId, itemId, quantity);
//            return Result.success("购买成功", result);
//        } catch (BusinessException e) {
//            return Result.error(e.getMessage());
//        } catch (Exception e) {
//            return Result.error("购买失败：" + e.getMessage());
//        }
//    }
    
    /**
     * 获取用户道具列表
     * 
     * @param itemType 道具类型（可选）
     * @param request HTTP请求
     * @return 用户道具列表
     */
    @GetMapping("/items")
    public Result<List<UserItem>> getUserItems(@RequestParam(required = false) String itemType,
                                              HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            List<UserItem> userItems;
            if (itemType != null) {
                userItems = userItemService.getUserItemsByType(userId, itemType);
            } else {
                userItems = userItemService.getUserItemsByUserId(userId);
            }
            
            return Result.success(userItems);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户已装备的道具
     * 
     * @param request HTTP请求
     * @return 已装备道具列表
     */
    @GetMapping("/items/equipped")
    public Result<List<UserItem>> getEquippedItems(HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            List<UserItem> equippedItems = userItemService.getEquippedItems(userId);
            return Result.success(equippedItems);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 装备道具
     * 
     * @param itemId 道具ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/equip")
    public Result<Map<String, Object>> equipItem(@RequestParam Long itemId,
                                                HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            boolean success = userItemService.equipItem(userId, itemId);
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("itemId", itemId);
                result.put("message", "道具装备成功");
                return Result.success("装备成功", result);
            } else {
                return Result.error("装备失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 卸下道具
     * 
     * @param itemId 道具ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/unequip")
    public Result<Map<String, Object>> unequipItem(@RequestParam Long itemId,
                                                  HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            boolean success = userItemService.unequipItem(userId, itemId);
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("itemId", itemId);
                result.put("message", "道具卸下成功");
                return Result.success("卸下成功", result);
            } else {
                return Result.error("卸下失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 消耗一次性/限次道具的使用次数。
     */
    @PostMapping("/items/{itemId}/consume")
    public Result<Boolean> consumeItem(@PathVariable Long itemId) {
        Long userId = UserContextUtil.requireCurrentUserId();
        userItemService.consumeItem(userId, itemId);
        return Result.success(true);
    }

    /**
     * 自动敲击能力状态
     */
    @GetMapping("/items/auto-knock/status")
    public Result<com.hsmy.vo.AutoAbilityStatusVO> autoKnockStatus() {
        Long userId = UserContextUtil.requireCurrentUserId();
        return Result.success(userItemService.getAutoAbilityStatus(userId, "autoKnock"));
    }

    /**
     * 自动冥想能力状态
     */
    @GetMapping("/items/auto-meditation/status")
    public Result<com.hsmy.vo.AutoAbilityStatusVO> autoMeditationStatus() {
        Long userId = UserContextUtil.requireCurrentUserId();
        return Result.success(userItemService.getAutoAbilityStatus(userId, "meditation"));
    }
}
