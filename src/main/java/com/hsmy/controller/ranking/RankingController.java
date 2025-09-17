package com.hsmy.controller.ranking;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.entity.Ranking;
import com.hsmy.service.RankingService;
import com.hsmy.utils.UserContextUtil;
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
@RequestMapping("/rankings")
@ApiVersion(ApiVersionConstant.V1_0)
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
        List<Ranking> rankings = rankingService.getTodayRanking(limit);
        return Result.success(rankings);
    }
    
    /**
     * 获取周榜
     * 
     * @param limit 查询条数（默认100）
     * @return 周榜数据
     */
    @GetMapping("/weekly")
    public Result<List<Ranking>> getWeeklyRanking(@RequestParam(defaultValue = "100") Integer limit) {
        List<Ranking> rankings = rankingService.getWeeklyRanking(limit);
        return Result.success(rankings);
    }
    
    /**
     * 获取总榜
     * 
     * @param limit 查询条数（默认100）
     * @return 总榜数据
     */
    @GetMapping("/total")
    public Result<List<Ranking>> getTotalRanking(@RequestParam(defaultValue = "100") Integer limit) {
        List<Ranking> rankings = rankingService.getTotalRanking(limit);
        return Result.success(rankings);
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
        // 1. 获取用户今日排名
        Ranking todayRanking = rankingService.getUserTodayRanking(userId);
        
        // 2. 获取用户本周排名  
        Ranking weeklyRanking = rankingService.getUserWeeklyRanking(userId);
        
        // 3. 获取用户总榜排名
        Ranking totalRanking = rankingService.getUserTotalRanking(userId);
        
        // 4. 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("dailyRanking", todayRanking);
        result.put("weeklyRanking", weeklyRanking);
        result.put("totalRanking", totalRanking);
        
        // 添加汇总信息
//            Map<String, Object> summary = new HashMap<>();
//            summary.put("hasDaily", todayRanking != null);
//            summary.put("hasWeekly", weeklyRanking != null);
//            summary.put("hasTotal", totalRanking != null);
//
//            if (todayRanking != null) {
//                summary.put("todayRank", todayRanking.getRank());
//                summary.put("todayScore", todayRanking.getScore());
//            }
//            if (weeklyRanking != null) {
//                summary.put("weeklyRank", weeklyRanking.getRank());
//                summary.put("weeklyScore", weeklyRanking.getScore());
//            }
//            if (totalRanking != null) {
//                summary.put("totalRank", totalRanking.getRank());
//                summary.put("totalScore", totalRanking.getScore());
//            }
        
        //result.put("summary", summary);
        
        return Result.success("查询成功", result);
    }
    
    /**
     * 获取我的排名信息
     * 
     * @param request HTTP请求
     * @return 当前用户的排名信息
     */
    @GetMapping("/my")
    public Result<Map<String, Object>> getMyRanking(HttpServletRequest request) {
        Long userId = UserContextUtil.requireCurrentUserId();
        return getUserRanking(userId, request);
    }
}