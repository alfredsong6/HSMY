package com.hsmy.controller.ranking;

import com.hsmy.common.Result;
import com.hsmy.entity.Ranking;
import com.hsmy.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 排行榜Controller
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {
    
    private final RankingService rankingService;
    
    /**
     * 获取日榜
     * 
     * @param limit 查询条数（默认100）
     * @return 日榜数据
     */
    @GetMapping("/daily")
    public Result<List<Ranking>> getDailyRanking(@RequestParam(defaultValue = "100") Integer limit) {
        try {
            List<Ranking> rankings = rankingService.getTodayRanking(limit);
            return Result.success(rankings);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取周榜
     * 
     * @param limit 查询条数（默认100）
     * @return 周榜数据
     */
    @GetMapping("/weekly")
    public Result<List<Ranking>> getWeeklyRanking(@RequestParam(defaultValue = "100") Integer limit) {
        try {
            List<Ranking> rankings = rankingService.getWeeklyRanking(limit);
            return Result.success(rankings);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取总榜
     * 
     * @param limit 查询条数（默认100）
     * @return 总榜数据
     */
    @GetMapping("/total")
    public Result<List<Ranking>> getTotalRanking(@RequestParam(defaultValue = "100") Integer limit) {
        try {
            List<Ranking> rankings = rankingService.getTotalRanking(limit);
            return Result.success(rankings);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户排名信息
     * 
     * @param userId 用户ID
     * @param request HTTP请求
     * @return 用户在各榜单中的排名
     */
    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getUserRanking(@PathVariable Long userId, HttpServletRequest request) {
        try {
            // TODO: 实现获取用户在各榜单中排名的逻辑
            // 1. 获取用户今日排名
            // 2. 获取用户本周排名  
            // 3. 获取用户总榜排名
            // 4. 组装返回数据
            
            Ranking todayRanking = rankingService.getUserTodayRanking(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("dailyRanking", todayRanking);
            result.put("weeklyRanking", null); // TODO: 实现
            result.put("totalRanking", null);  // TODO: 实现
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取我的排名信息
     * 
     * @param request HTTP请求
     * @return 当前用户的排名信息
     */
    @GetMapping("/my")
    public Result<Map<String, Object>> getMyRanking(HttpServletRequest request) {
        try {
            // TODO: 从token获取用户ID
            Long userId = 1L; // 临时硬编码
            
            Ranking todayRanking = rankingService.getUserTodayRanking(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("dailyRanking", todayRanking);
            result.put("weeklyRanking", null); // TODO: 实现
            result.put("totalRanking", null);  // TODO: 实现
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}