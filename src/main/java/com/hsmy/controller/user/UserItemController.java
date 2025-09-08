package com.hsmy.controller.user;

import com.hsmy.common.Result;
import com.hsmy.entity.UserItem;
import com.hsmy.service.UserItemService;
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
@RequiredArgsConstructor
public class UserItemController {
    
    private final UserItemService userItemService;
    
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
            // TODO: 从token获取用户ID
            Long userId = 1L; // 临时硬编码
            
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
            // TODO: 从token获取用户ID
            Long userId = 1L; // 临时硬编码
            
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
            // TODO: 从token获取用户ID
            Long userId = 1L; // 临时硬编码
            
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
            // TODO: 从token获取用户ID
            Long userId = 1L; // 临时硬编码
            
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
}