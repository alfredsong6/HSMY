package com.hsmy.task;

import com.hsmy.mapper.UserItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时标记用户道具过期状态。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserItemExpireTask {

    private final UserItemMapper userItemMapper;

    /**
     * 每小时扫描一次过期道具。
     */
    @Scheduled(cron = "0 10 * * * ?")
    @Async("asyncExecutor")
    public void markExpired() {
        int updated = userItemMapper.markExpiredItems();
        if (updated > 0) {
            log.info("用户道具过期扫描：标记 {} 条记录为已过期", updated);
        }
    }
}
