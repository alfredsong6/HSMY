package com.hsmy.task;

import com.hsmy.service.UserPeriodStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Aggregates knock statistics for weekly, monthly, and yearly periods once per day.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserPeriodStatsScheduledTask {

    private final UserPeriodStatsService userPeriodStatsService;

    /**
     * Aggregate higher-level period statistics every night after the day rolls over.
     */
    @Scheduled(cron = "0 2 0 * * ?")
    @Async("asyncExecutor")
    public void aggregatePeriodStats() {
        LocalDate targetDate = LocalDate.now().minusDays(1);
        long start = System.currentTimeMillis();
        try {
            userPeriodStatsService.aggregatePeriods(targetDate);
            log.info("Aggregated period stats for {} in {} ms", targetDate, System.currentTimeMillis() - start);
        } catch (Exception ex) {
            log.error("Failed to aggregate period stats for {}", targetDate, ex);
        }
    }
}
