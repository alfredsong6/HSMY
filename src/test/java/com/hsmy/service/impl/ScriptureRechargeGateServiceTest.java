package com.hsmy.service.impl;

import com.hsmy.mapper.RechargeOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptureRechargeGateServiceTest {

    private static final Long USER_ID = 100L;

    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private RechargeOrderMapper rechargeOrderMapper;

    private ScriptureRechargeGateService service;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        service = new ScriptureRechargeGateService(stringRedisTemplate, rechargeOrderMapper);
    }

    @Test
    void allowsListWhenGateDisabledInRedis() {
        when(valueOperations.get(ScriptureRechargeGateService.REDIS_KEY)).thenReturn("false");

        assertTrue(service.canReturnScriptureLists(USER_ID));
        verify(rechargeOrderMapper, never()).countSuccessfulRechargeOrders(USER_ID);
    }

    @Test
    void isGateEnabledReturnsFalseWhenRedisValueIsFalse() {
        when(valueOperations.get(ScriptureRechargeGateService.REDIS_KEY)).thenReturn("false");

        assertFalse(service.isGateEnabled());
    }

    @Test
    void isGateEnabledReturnsTrueByDefault() {
        when(valueOperations.get(ScriptureRechargeGateService.REDIS_KEY)).thenReturn(null);

        assertTrue(service.isGateEnabled());
    }

    @Test
    void blocksListByDefaultWhenUserHasNoSuccessfulRecharge() {
        when(valueOperations.get(ScriptureRechargeGateService.REDIS_KEY)).thenReturn(null);
        when(rechargeOrderMapper.countSuccessfulRechargeOrders(USER_ID)).thenReturn(0);

        assertFalse(service.canReturnScriptureLists(USER_ID));
    }

    @Test
    void allowsListByDefaultWhenUserHasSuccessfulRecharge() {
        when(valueOperations.get(ScriptureRechargeGateService.REDIS_KEY)).thenReturn(null);
        when(rechargeOrderMapper.countSuccessfulRechargeOrders(USER_ID)).thenReturn(1);

        assertTrue(service.canReturnScriptureLists(USER_ID));
    }
}
