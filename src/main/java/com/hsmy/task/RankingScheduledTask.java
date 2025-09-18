package com.hsmy.task;

import com.hsmy.service.RankingCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * 排行榜定时任务
 * 支持并发执行
 *
 * @author HSMY
 * @date 2025/09/18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RankingScheduledTask {

    private final RankingCalculationService rankingCalculationService;

    /**
     * 生成日榜数据
     * 每天凌晨0点5分执行
     */
    @Scheduled(cron = "0 5 0 * * ?")
    @Async("asyncExecutor")
    public CompletableFuture<Void> generateDailyRanking() {
        log.info("开始执行日榜生成任务，线程：{}", Thread.currentThread().getName());
        try {
            Date today = new Date();
            int count = rankingCalculationService.generateDailyRanking(today);
            log.info("日榜生成任务完成，生成 {} 条记录", count);
        } catch (Exception e) {
            log.error("日榜生成任务执行失败", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 生成周榜数据
     * 每周一凌晨0点10分执行
     */
    @Scheduled(cron = "0 10 0 ? * MON")
    @Async("asyncExecutor")
    public CompletableFuture<Void> generateWeeklyRanking() {
        log.info("开始执行周榜生成任务，线程：{}", Thread.currentThread().getName());
        try {
            Date today = new Date();
            int count = rankingCalculationService.generateWeeklyRanking(today);
            log.info("周榜生成任务完成，生成 {} 条记录", count);
        } catch (Exception e) {
            log.error("周榜生成任务执行失败", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 生成月榜数据
     * 每月1日凌晨0点15分执行
     */
    @Scheduled(cron = "0 15 0 1 * ?")
    @Async("asyncExecutor")
    public CompletableFuture<Void> generateMonthlyRanking() {
        log.info("开始执行月榜生成任务，线程：{}", Thread.currentThread().getName());
        try {
            Date today = new Date();
            int count = rankingCalculationService.generateMonthlyRanking(today);
            log.info("月榜生成任务完成，生成 {} 条记录", count);
        } catch (Exception e) {
            log.error("月榜生成任务执行失败", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 生成总榜数据
     * 每天凌晨0点20分执行
     */
    @Scheduled(cron = "0 20 0 * * ?")
    @Async("asyncExecutor")
    public CompletableFuture<Void> generateTotalRanking() {
        log.info("开始执行总榜生成任务，线程：{}", Thread.currentThread().getName());
        try {
            Date today = new Date();
            int count = rankingCalculationService.generateTotalRanking(today);
            log.info("总榜生成任务完成，生成 {} 条记录", count);
        } catch (Exception e) {
            log.error("总榜生成任务执行失败", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 清理过期排名数据
     * 每天凌晨0点30分执行，保留最近30天的数据
     */
    @Scheduled(cron = "0 30 0 * * ?")
    @Async("asyncExecutor")
    public CompletableFuture<Void> cleanOldRankings() {
        log.info("开始执行排名数据清理任务，线程：{}", Thread.currentThread().getName());
        try {
            int count = rankingCalculationService.cleanOldRankings(30);
            log.info("排名数据清理任务完成，清理 {} 条记录", count);
        } catch (Exception e) {
            log.error("排名数据清理任务执行失败", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 用于测试的立即执行方法（并发执行所有任务）
     * 可以通过JMX或管理接口手动触发
     */
    @Async("asyncExecutor")
    public CompletableFuture<Void> executeAllRankingsNow() {
        log.info("手动触发所有排行榜生成任务，线程：{}", Thread.currentThread().getName());
        Date now = new Date();

        // 并发执行所有排行榜生成任务
        CompletableFuture<Integer> dailyFuture = CompletableFuture.supplyAsync(() -> {
            try {
                log.info("开始生成日榜，线程：{}", Thread.currentThread().getName());
                return rankingCalculationService.generateDailyRanking(now);
            } catch (Exception e) {
                log.error("生成日榜失败", e);
                return 0;
            }
        });

        CompletableFuture<Integer> weeklyFuture = CompletableFuture.supplyAsync(() -> {
            try {
                log.info("开始生成周榜，线程：{}", Thread.currentThread().getName());
                return rankingCalculationService.generateWeeklyRanking(now);
            } catch (Exception e) {
                log.error("生成周榜失败", e);
                return 0;
            }
        });

        CompletableFuture<Integer> monthlyFuture = CompletableFuture.supplyAsync(() -> {
            try {
                log.info("开始生成月榜，线程：{}", Thread.currentThread().getName());
                return rankingCalculationService.generateMonthlyRanking(now);
            } catch (Exception e) {
                log.error("生成月榜失败", e);
                return 0;
            }
        });

        CompletableFuture<Integer> totalFuture = CompletableFuture.supplyAsync(() -> {
            try {
                log.info("开始生成总榜，线程：{}", Thread.currentThread().getName());
                return rankingCalculationService.generateTotalRanking(now);
            } catch (Exception e) {
                log.error("生成总榜失败", e);
                return 0;
            }
        });

        // 等待所有任务完成
        CompletableFuture.allOf(dailyFuture, weeklyFuture, monthlyFuture, totalFuture)
            .thenRun(() -> {
                try {
                    log.info("所有排行榜生成完成");
                    log.info("日榜: {} 条", dailyFuture.get());
                    log.info("周榜: {} 条", weeklyFuture.get());
                    log.info("月榜: {} 条", monthlyFuture.get());
                    log.info("总榜: {} 条", totalFuture.get());
                } catch (Exception e) {
                    log.error("获取结果失败", e);
                }
            });

        return CompletableFuture.completedFuture(null);
    }
}