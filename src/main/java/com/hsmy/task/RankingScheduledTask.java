package com.hsmy.task;

import com.hsmy.service.RankingCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.Set;

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
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String TOTAL_RANKING_CACHE_KEY_PREFIX = "ranking:total:";



    /**
     * 生成总榜数据
     * 每个整点执行
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Async("asyncExecutor")
    public CompletableFuture<Void> generateTotalRanking() {
        log.info("开始执行总榜生成任务，线程：{}", Thread.currentThread().getName());
        try {
            Date today = new Date();
            int count = rankingCalculationService.generateTotalRanking(today);
            log.info("总榜生成任务完成，生成 {} 条记录", count);
            evictTotalRankingCache();
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


    
    private void evictTotalRankingCache() {
        Set<String> keys = redisTemplate.keys(TOTAL_RANKING_CACHE_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("已清理总榜缓存 {} 条", keys.size());
        }
    }
}
