package com.hsmy.controller.admin;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.service.RankingCalculationService;
import com.hsmy.task.RankingScheduledTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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

}