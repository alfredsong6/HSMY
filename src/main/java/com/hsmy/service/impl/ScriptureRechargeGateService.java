package com.hsmy.service.impl;

import com.hsmy.mapper.RechargeOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Controls whether scripture lists require a successful recharge record.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptureRechargeGateService {

    public static final String REDIS_KEY = "hsmy:scripture:recharge-gate:enabled";

    private final StringRedisTemplate stringRedisTemplate;
    private final RechargeOrderMapper rechargeOrderMapper;

    public boolean canReturnScriptureLists(Long userId) {
        if (!isGateEnabled()) {
            return true;
        }
        if (userId == null) {
            return false;
        }
        Integer count = rechargeOrderMapper.countSuccessfulRechargeOrders(userId);
        return count != null && count > 0;
        //return false;
    }

    public boolean isGateEnabled() {
        String value;
        try {
            value = stringRedisTemplate.opsForValue().get(REDIS_KEY);
        } catch (Exception e) {
            log.warn("读取典籍充值门禁开关失败，按开启处理，key={}", REDIS_KEY, e);
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
