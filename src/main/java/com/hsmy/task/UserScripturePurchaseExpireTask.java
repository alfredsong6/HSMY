package com.hsmy.task;

import com.hsmy.entity.UserScripturePurchase;
import com.hsmy.service.UserScripturePurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时扫描典籍订阅的过期状态。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserScripturePurchaseExpireTask {

    /**
     * 提前 N 天感知到期，方便后续扩展提醒。
     */
    private static final int EXPIRING_WINDOW_DAYS = 3;

    private final UserScripturePurchaseService userScripturePurchaseService;

    /**
     * 每30分钟刷新一次订阅状态，并记录即将过期的数据，便于告警/通知。
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    @Async("asyncExecutor")
    public void refreshPurchaseStatus() {
        Integer expired = userScripturePurchaseService.updateExpiredStatus();
        if (expired != null && expired > 0) {
            log.info("典籍订阅过期扫描：标记 {} 条记录为已过期", expired);
        }

        List<UserScripturePurchase> expiringSoon = userScripturePurchaseService.getExpiringSoonPurchases(EXPIRING_WINDOW_DAYS);
        if (!expiringSoon.isEmpty()) {
            log.info("典籍订阅过期扫描：{} 条记录将在 {} 天内到期", expiringSoon.size(), EXPIRING_WINDOW_DAYS);
        }
    }
}

