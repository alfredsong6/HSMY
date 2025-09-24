package com.hsmy.controller.merit;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.entity.MeritRecord;
import com.hsmy.service.MeritService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.ExchangeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 功德控制器 V1.1
 * 
 * 相比v1.0版本的变化：
 * 1. 增加了更详细的余额信息返回
 * 2. 优化了兑换接口的响应格式
 * 3. 新增了功德统计汇总接口
 * 
 * @author HSMY
 * @date 2025/09/10
 */
@RestController
@RequestMapping("/merit")
@ApiVersion(ApiVersionConstant.V1_1)
@RequiredArgsConstructor
public class MeritController {
    
    private final MeritService meritService;
    
//    /**
//     * 手动敲击
//     *
//     * @param knockVO 敲击信息
//     * @return 获得功德值
//     */
//    @PostMapping("/knock/manual")
//    public Result<Integer> manualKnock(@Validated @RequestBody KnockVO knockVO) {
//        try {
//            Integer merit = meritService.manualKnock(knockVO);
//            return Result.success("敲击成功", merit);
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }
//    }
//
//    /**
//     * 开始自动敲击
//     *
//     * @param userId 用户ID
//     * @param duration 持续时间（秒）
//     * @return 会话ID
//     */
//    @PostMapping("/knock/auto/start")
//    public Result<String> startAutoKnock(@RequestParam Long userId,
//                                        @RequestParam(defaultValue = "60") Integer duration) {
//        try {
//            String sessionId = meritService.startAutoKnock(userId, duration);
//            return Result.success("自动敲击已开始", sessionId);
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }
//    }
//
//    /**
//     * 停止自动敲击
//     *
//     * @param userId 用户ID
//     * @param sessionId 会话ID
//     * @return 结果
//     */
//    @PostMapping("/knock/auto/stop")
//    public Result<Boolean> stopAutoKnock(@RequestParam Long userId,
//                                        @RequestParam String sessionId) {
//        try {
//            Boolean result = meritService.stopAutoKnock(userId, sessionId);
//            return result ? Result.success("自动敲击已停止", true) : Result.error("停止失败");
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }
//    }
    
    /**
     * 获取功德和功德币余额（V1.1增强版）
     * 
     * 相比v1.0增加了更多统计信息
     * 
     * @param request HTTP请求
     * @return 余额信息
     */
    @PostMapping("/balance")
    public Result<Map<String, Object>> getBalance(HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            // V1.1版本返回更详细的余额信息
            Map<String, Object> balance = new HashMap<>();
            balance.put("userId", userId);
            balance.put("totalMerit", meritService.getTotalMerit(userId));
            balance.put("meritCoins", meritService.getMeritCoins(userId));
            balance.put("todayMerit", meritService.getTodayMerit(userId));
            balance.put("weeklyMerit", meritService.getWeeklyMerit(userId));
            balance.put("monthlyMerit", meritService.getMonthlyMerit(userId));
            balance.put("apiVersion", "v1.1");
            
            return Result.success("查询成功", balance);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 功德兑换功德币（V1.1增强版）
     * 
     * 相比v1.0增加了兑换前后的余额对比
     * 
     * @param exchangeVO 兑换信息
     * @param request HTTP请求
     * @return 兑换结果
     */
    @PostMapping("/exchange")
    public Result<Map<String, Object>> exchangeMerit(@Validated @RequestBody ExchangeVO exchangeVO, 
                                                    HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            exchangeVO.setUserId(userId);
            
            // V1.1版本记录兑换前的余额
            Map<String, Object> beforeBalance = new HashMap<>();
            beforeBalance.put("totalMerit", meritService.getTotalMerit(userId));
            beforeBalance.put("meritCoins", meritService.getMeritCoins(userId));
            
            Map<String, Object> exchangeResult = meritService.exchangeMerit(exchangeVO);
            
            // V1.1版本增加兑换前后对比
            Map<String, Object> result = new HashMap<>(exchangeResult);
            result.put("beforeBalance", beforeBalance);
            Map<String, Object> afterBalance = new HashMap<>();
            afterBalance.put("totalMerit", meritService.getTotalMerit(userId));
            afterBalance.put("meritCoins", meritService.getMeritCoins(userId));
            result.put("afterBalance", afterBalance);
            result.put("apiVersion", "v1.1");
            
            return Result.success("兑换成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取功德获取历史
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param request HTTP请求
     * @return 功德记录
     */
    @GetMapping("/history")
    public Result<Page<MeritRecord>> getMeritHistory(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            Page<MeritRecord> page = meritService.getMeritRecords(userId, startDate, endDate, pageNum, pageSize);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取功德统计汇总（V1.1新增接口）
     * 
     * @param request HTTP请求
     * @return 统计信息汇总
     */
    @GetMapping("/summary")
    public Result<Map<String, Object>> getMeritSummary() {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            Map<String, Object> summary = new HashMap<>();
            //summary.put("userId", userId);
            LocalDate localToday = LocalDate.now();
            Date today = java.sql.Date.valueOf(localToday);
            summary.put("totalMerit", meritService.getTotalMerit(userId));
            summary.put("todayMerit", meritService.getTodayMerit(userId));
            summary.put("weeklyMerit", meritService.getWeeklyMerit(userId));
            summary.put("monthlyMerit", meritService.getMonthlyMerit(userId));
            summary.put("meritCoins", meritService.getMeritCoins(userId));
            summary.put("userStats", meritService.getMeritStats(userId));
            summary.put("statDate", today);
            summary.put("dailyMerit", meritService.getMeritByStatDate(userId, today));
            //summary.put("apiVersion", "v1.1");
            //summary.put("newFeatures", new String[]{"详细余额信息", "兑换前后对比", "统计汇总接口"});
            
            return Result.success("查询成功", summary);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取指定自然日的功德统计
     *
     * @param date 自然日（可选，默认当天）
     * @return 功德统计
     */
    @GetMapping("/daily")
    public Result<Map<String, Object>> getDailyMerit(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            Date requestDate = date != null ? date : new Date();
            LocalDate local = requestDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Date targetDate = java.sql.Date.valueOf(local);
            Long merit = meritService.getMeritByStatDate(userId, targetDate);
            Map<String, Object> payload = new HashMap<>();
            payload.put("statDate", targetDate);
            payload.put("merit", merit);
            return Result.success(payload);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取功德统计
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    @GetMapping("/stats/{userId}")
    public Result<Map<String, Object>> getMeritStats(@PathVariable Long userId) {
        Map<String, Object> stats = meritService.getMeritStats(userId);
        return Result.success(stats);
    }
    
    /**
     * 获取功德记录
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 功德记录
     */
    @GetMapping("/records")
    public Result<Page<MeritRecord>> getMeritRecords(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Page<MeritRecord> page = meritService.getMeritRecords(userId, startDate, endDate, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 获取今日功德
     * 
     * @param userId 用户ID
     * @return 今日功德值
     */
    @GetMapping("/today/{userId}")
    public Result<Long> getTodayMerit(@PathVariable Long userId) {
        Long merit = meritService.getTodayMerit(userId);
        return Result.success(merit);
    }
    
    /**
     * 获取本周功德
     * 
     * @param userId 用户ID
     * @return 本周功德值
     */
    @GetMapping("/weekly/{userId}")
    public Result<Long> getWeeklyMerit(@PathVariable Long userId) {
        Long merit = meritService.getWeeklyMerit(userId);
        return Result.success(merit);
    }
    
    /**
     * 获取本月功德
     * 
     * @param userId 用户ID
     * @return 本月功德值
     */
    @GetMapping("/monthly/{userId}")
    public Result<Long> getMonthlyMerit(@PathVariable Long userId) {
        Long merit = meritService.getMonthlyMerit(userId);
        return Result.success(merit);
    }
}
