package com.hsmy.controller.merit;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.common.Result;
import com.hsmy.entity.MeritRecord;
import com.hsmy.service.MeritService;
import com.hsmy.vo.ExchangeVO;
import com.hsmy.vo.KnockVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * 功德控制器
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@RestController
@RequestMapping("/merit")
@RequiredArgsConstructor
public class MeritController {
    
    private final MeritService meritService;
    
    /**
     * 手动敲击
     * 
     * @param knockVO 敲击信息
     * @return 获得功德值
     */
    @PostMapping("/knock/manual")
    public Result<Integer> manualKnock(@Validated @RequestBody KnockVO knockVO) {
        try {
            Integer merit = meritService.manualKnock(knockVO);
            return Result.success("敲击成功", merit);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 开始自动敲击
     * 
     * @param userId 用户ID
     * @param duration 持续时间（秒）
     * @return 会话ID
     */
    @PostMapping("/knock/auto/start")
    public Result<String> startAutoKnock(@RequestParam Long userId, 
                                        @RequestParam(defaultValue = "60") Integer duration) {
        try {
            String sessionId = meritService.startAutoKnock(userId, duration);
            return Result.success("自动敲击已开始", sessionId);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 停止自动敲击
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 结果
     */
    @PostMapping("/knock/auto/stop")
    public Result<Boolean> stopAutoKnock(@RequestParam Long userId, 
                                        @RequestParam String sessionId) {
        try {
            Boolean result = meritService.stopAutoKnock(userId, sessionId);
            return result ? Result.success("自动敲击已停止", true) : Result.error("停止失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 功德兑换功德币
     * 
     * @param exchangeVO 兑换信息
     * @return 兑换结果
     */
    @PostMapping("/exchange")
    public Result<Map<String, Object>> exchangeMerit(@Validated @RequestBody ExchangeVO exchangeVO) {
        try {
            Map<String, Object> result = meritService.exchangeMerit(exchangeVO);
            return Result.success("兑换成功", result);
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