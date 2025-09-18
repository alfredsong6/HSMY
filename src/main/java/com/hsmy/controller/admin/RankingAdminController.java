package com.hsmy.controller.admin;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.service.RankingCalculationService;
import com.hsmy.task.RankingScheduledTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 排行榜管理接口
 * 用于手动触发排行榜生成和管理
 *
 * @author HSMY
 * @date 2025/09/18
 */
@Slf4j
@RestController
@RequestMapping("/admin/rankings")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class RankingAdminController {

    private final RankingCalculationService rankingCalculationService;
    private final RankingScheduledTask rankingScheduledTask;

    /**
     * 手动生成所有排行榜
     */
    @PostMapping("/generate/all")
    public Result<Map<String, Integer>> generateAllRankings() {
        log.info("管理员手动触发生成所有排行榜");

        Date now = new Date();
        Map<String, Integer> result = new HashMap<>();

        try {
            result.put("daily", rankingCalculationService.generateDailyRanking(now));
            result.put("weekly", rankingCalculationService.generateWeeklyRanking(now));
            result.put("monthly", rankingCalculationService.generateMonthlyRanking(now));
            result.put("total", rankingCalculationService.generateTotalRanking(now));

            return Result.success("排行榜生成成功", result);
        } catch (Exception e) {
            log.error("生成排行榜失败", e);
            return Result.error("生成失败：" + e.getMessage());
        }
    }

    /**
     * 手动生成日榜
     *
     * @param date 指定日期（可选，默认今天）
     */
    @PostMapping("/generate/daily")
    public Result<Integer> generateDailyRanking(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

        Date targetDate = date != null ? date : new Date();
        log.info("管理员手动生成日榜，日期：{}", targetDate);

        try {
            int count = rankingCalculationService.generateDailyRanking(targetDate);
            return Result.success("日榜生成成功，共 " + count + " 条记录", count);
        } catch (Exception e) {
            log.error("生成日榜失败", e);
            return Result.error("生成失败：" + e.getMessage());
        }
    }

    /**
     * 手动生成周榜
     *
     * @param date 指定日期（可选，默认今天）
     */
    @PostMapping("/generate/weekly")
    public Result<Integer> generateWeeklyRanking(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

        Date targetDate = date != null ? date : new Date();
        log.info("管理员手动生成周榜，日期：{}", targetDate);

        try {
            int count = rankingCalculationService.generateWeeklyRanking(targetDate);
            return Result.success("周榜生成成功，共 " + count + " 条记录", count);
        } catch (Exception e) {
            log.error("生成周榜失败", e);
            return Result.error("生成失败：" + e.getMessage());
        }
    }

    /**
     * 手动生成月榜
     *
     * @param date 指定日期（可选，默认今天）
     */
    @PostMapping("/generate/monthly")
    public Result<Integer> generateMonthlyRanking(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

        Date targetDate = date != null ? date : new Date();
        log.info("管理员手动生成月榜，日期：{}", targetDate);

        try {
            int count = rankingCalculationService.generateMonthlyRanking(targetDate);
            return Result.success("月榜生成成功，共 " + count + " 条记录", count);
        } catch (Exception e) {
            log.error("生成月榜失败", e);
            return Result.error("生成失败：" + e.getMessage());
        }
    }

    /**
     * 手动生成总榜
     */
    @PostMapping("/generate/total")
    public Result<Integer> generateTotalRanking() {
        Date now = new Date();
        log.info("管理员手动生成总榜");

        try {
            int count = rankingCalculationService.generateTotalRanking(now);
            return Result.success("总榜生成成功，共 " + count + " 条记录", count);
        } catch (Exception e) {
            log.error("生成总榜失败", e);
            return Result.error("生成失败：" + e.getMessage());
        }
    }

    /**
     * 清理过期排行榜数据
     *
     * @param daysToKeep 保留天数（默认30天）
     */
    @PostMapping("/clean")
    public Result<Integer> cleanOldRankings(
            @RequestParam(defaultValue = "30") Integer daysToKeep) {

        log.info("管理员手动清理排行榜数据，保留 {} 天", daysToKeep);

        try {
            int count = rankingCalculationService.cleanOldRankings(daysToKeep);
            return Result.success("清理成功，删除 " + count + " 条记录", count);
        } catch (Exception e) {
            log.error("清理排行榜数据失败", e);
            return Result.error("清理失败：" + e.getMessage());
        }
    }

    /**
     * 触发定时任务立即执行
     */
    @PostMapping("/trigger")
    public Result<String> triggerScheduledTask() {
        log.info("管理员触发定时任务立即执行");

        try {
            rankingScheduledTask.executeAllRankingsNow();
            return Result.success("定时任务触发成功");
        } catch (Exception e) {
            log.error("触发定时任务失败", e);
            return Result.error("触发失败：" + e.getMessage());
        }
    }
}