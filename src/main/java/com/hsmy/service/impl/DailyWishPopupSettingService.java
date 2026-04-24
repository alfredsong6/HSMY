package com.hsmy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Reads the daily wish popup switch from Redis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyWishPopupSettingService {

    public static final String REDIS_KEY = "hsmy:dailyWish:popup:enabled";

    private final StringRedisTemplate stringRedisTemplate;

    public boolean isDailyWishPopupEnabled() {
        String value;
        try {
            value = stringRedisTemplate.opsForValue().get(REDIS_KEY);
        } catch (Exception e) {
            log.warn("读取每日愿望弹窗开关失败，按开启处理，key={}", REDIS_KEY, e);
            return true;
        }
        if (!StringUtils.hasText(value)) {
            return true;
        }
        String normalized = value.trim().toLowerCase();
        return !("false".equals(normalized)
                || "0".equals(normalized)
                || "off".equals(normalized)
                || "no".equals(normalized));
    }
}
