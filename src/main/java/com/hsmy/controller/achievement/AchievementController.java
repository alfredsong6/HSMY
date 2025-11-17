package com.hsmy.controller.achievement;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.entity.Achievement;
import com.hsmy.entity.UserAchievement;
import com.hsmy.service.AchievementService;
import com.hsmy.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成就Controller
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@RestController
@RequestMapping("/achievement")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class AchievementController {
    
    private final AchievementService achievementService;
    
    /**
     * 获取用户成就列表
     * 
     * @param achievementType 成就类型（可选）
     * @param request HTTP请求
     * @return 用户成就列表
     */
    @GetMapping("/list")
    public Result<List<UserAchievement>> getUserAchievements(@RequestParam(required = false) String achievementType,
                                                           HttpServletRequest request) {
        Long userId = UserContextUtil.requireCurrentUserId();
        
        List<UserAchievement> userAchievements = achievementService.getUserAchievements(userId);
        
        // 如果指定了类型，进行过滤
        if (achievementType != null) {
            userAchievements = userAchievements.stream()
                    .filter(achievement -> achievementType.equals(achievement.getAchievement().getAchievementType()))
                    .collect(Collectors.toList());
        }
        
        return Result.success(userAchievements);
    }
    
    /**
     * 获取用户已完成的成就
     * 
     * @param request HTTP请求
     * @return 已完成成就列表
     */
    @GetMapping("/completed")
    public Result<List<UserAchievement>> getCompletedAchievements(HttpServletRequest request) {
        Long userId = UserContextUtil.requireCurrentUserId();
        
        List<UserAchievement> completedAchievements = achievementService.getCompletedAchievements(userId);
        return Result.success(completedAchievements);
    }
    
    /**
     * 获取所有成就列表
     * 
     * @param achievementType 成就类型（可选）
     * @return 所有成就列表
     */
    @GetMapping("/all")
    public Result<List<Achievement>> getAllAchievements(@RequestParam(required = false) String achievementType) {
        List<Achievement> achievements;
        if (achievementType != null) {
            achievements = achievementService.getAchievementsByType(achievementType);
        } else {
            achievements = achievementService.getAllActiveAchievements();
        }
        
        return Result.success(achievements);
    }
    
    /**
     * 领取成就奖励
     * 
     * @param achievementId 成就ID
     * @param request HTTP请求
     * @return 领取结果
     */
    @PostMapping("/claim")
    public Result<Map<String, Object>> claimAchievementReward(@RequestParam Long achievementId,
                                                             HttpServletRequest request) {
        Long userId = UserContextUtil.requireCurrentUserId();
        
        boolean success = achievementService.claimAchievementReward(userId, achievementId);
        if (success) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("achievementId", achievementId);
            result.put("message", "成就奖励领取成功");
            return Result.success("领取成功", result);
        } else {
            return Result.error("领取失败");
        }
    }
    
    /**
     * 获取成就统计
     * 
     * @param request HTTP请求
     * @return 成就统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getAchievementStats(HttpServletRequest request) {
        Long userId = UserContextUtil.requireCurrentUserId();
        
        List<UserAchievement> allAchievements = achievementService.getUserAchievements(userId);
        List<UserAchievement> completedAchievements = achievementService.getCompletedAchievements(userId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAchievements", allAchievements.size());
        stats.put("completedAchievements", completedAchievements.size());
        stats.put("completionRate", allAchievements.isEmpty() ? 0 : (double) completedAchievements.size() / allAchievements.size() * 100);
        
        return Result.success(stats);
    }
}
