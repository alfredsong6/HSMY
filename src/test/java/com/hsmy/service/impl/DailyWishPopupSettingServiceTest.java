package com.hsmy.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyWishPopupSettingServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private DailyWishPopupSettingService service;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        service = new DailyWishPopupSettingService(stringRedisTemplate);
    }

    @Test
    void returnsTrueByDefaultWhenRedisValueMissing() {
        when(valueOperations.get(DailyWishPopupSettingService.REDIS_KEY)).thenReturn(null);

        assertTrue(service.isDailyWishPopupEnabled());
    }

    @Test
    void returnsFalseWhenRedisValueIsFalse() {
        when(valueOperations.get(DailyWishPopupSettingService.REDIS_KEY)).thenReturn("false");

        assertFalse(service.isDailyWishPopupEnabled());
    }

    @Test
    void returnsTrueWhenRedisValueIsTrue() {
        when(valueOperations.get(DailyWishPopupSettingService.REDIS_KEY)).thenReturn("true");

        assertTrue(service.isDailyWishPopupEnabled());
    }
}
