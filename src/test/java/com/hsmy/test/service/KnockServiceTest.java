package com.hsmy.test.service;

import com.hsmy.service.impl.KnockServiceImpl;
import com.hsmy.mapper.KnockRecordMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.entity.UserStats;
import com.hsmy.vo.KnockVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 敲击服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class KnockServiceTest {

    @Mock
    private KnockRecordMapper knockRecordMapper;
    
    @Mock
    private UserStatsMapper userStatsMapper;
    
    private KnockServiceImpl knockService;

    @BeforeEach
    void setUp() {
        knockService = new KnockServiceImpl(knockRecordMapper, userStatsMapper);
    }

    @Test
    void testManualKnock_Success() {
        // 准备测试数据
        KnockVO knockVO = new KnockVO();
        knockVO.setUserId(1L);
        knockVO.setKnockCount(10);
        knockVO.setKnockMode("MANUAL");
        knockVO.setKnockSound("default");

        UserStats userStats = createTestUserStats(1L);
        
        // 模拟数据库操作
        when(userStatsMapper.selectByUserId(1L)).thenReturn(userStats);
        when(knockRecordMapper.insert(any())).thenReturn(1);
        when(userStatsMapper.updateById(any())).thenReturn(1);

        // 执行测试
        Map<String, Object> result = knockService.manualKnock(knockVO);

        // 验证结果
        assertNotNull(result);
        assertEquals(10, result.get("knockCount"));
        assertNotNull(result.get("meritGained"));
        assertTrue((Integer) result.get("meritGained") > 0);
        
        // 验证方法调用
        verify(userStatsMapper).selectByUserId(1L);
        verify(knockRecordMapper).insert(any());
        verify(userStatsMapper).updateById(any());
    }

    @Test
    void testManualKnock_UserNotExists() {
        KnockVO knockVO = new KnockVO();
        knockVO.setUserId(999L);
        knockVO.setKnockCount(10);

        when(userStatsMapper.selectByUserId(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            knockService.manualKnock(knockVO);
        });
    }

    @Test
    void testManualKnock_InvalidKnockCount() {
        KnockVO knockVO = new KnockVO();
        knockVO.setUserId(1L);
        knockVO.setKnockCount(0); // 无效的敲击次数

        assertThrows(IllegalArgumentException.class, () -> {
            knockService.manualKnock(knockVO);
        });
    }

    @Test
    void testConcurrentKnock() throws InterruptedException {
        // 并发测试
        int threadCount = 10;
        int knocksPerThread = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger totalOperations = new AtomicInteger(0);

        UserStats userStats = createTestUserStats(1L);
        when(userStatsMapper.selectByUserId(1L)).thenReturn(userStats);
        when(knockRecordMapper.insert(any())).thenReturn(1);
        when(userStatsMapper.updateById(any())).thenReturn(1);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < knocksPerThread; j++) {
                        KnockVO knockVO = new KnockVO();
                        knockVO.setUserId(1L);
                        knockVO.setKnockCount(1);
                        knockVO.setKnockMode("MANUAL");
                        
                        knockService.manualKnock(knockVO);
                        totalOperations.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 验证所有操作都成功执行
        assertEquals(threadCount * knocksPerThread, totalOperations.get());
        
        // 验证数据库操作次数
        verify(knockRecordMapper, times(threadCount * knocksPerThread)).insert(any());
    }

    @Test
    void testMeritCalculation() {
        // 测试功德计算逻辑
        KnockVO knockVO = new KnockVO();
        knockVO.setUserId(1L);
        knockVO.setKnockCount(10);
        knockVO.setKnockMode("MANUAL");

        UserStats userStats = createTestUserStats(1L);
        when(userStatsMapper.selectByUserId(1L)).thenReturn(userStats);
        when(knockRecordMapper.insert(any())).thenReturn(1);
        when(userStatsMapper.updateById(any())).thenReturn(1);

        Map<String, Object> result = knockService.manualKnock(knockVO);
        
        // 验证功德计算
        Integer meritGained = (Integer) result.get("meritGained");
        assertNotNull(meritGained);
        assertTrue(meritGained >= 10); // 基础功德应该至少等于敲击次数
    }

    @Test
    void testKnockStats() {
        UserStats userStats = createTestUserStats(1L);
        when(userStatsMapper.selectByUserId(1L)).thenReturn(userStats);

        Map<String, Object> stats = knockService.getKnockStats(1L);

        assertNotNull(stats);
        assertNotNull(stats.get("totalKnocks"));
        assertNotNull(stats.get("totalMerit"));
    }

    private UserStats createTestUserStats(Long userId) {
        UserStats stats = new UserStats();
        stats.setUserId(userId);
        stats.setTotalMerit(1000L);
        stats.setMeritCoins(500L);
        stats.setTotalKnocks(100L);
        stats.setCreateTime(new Date());
        stats.setUpdateTime(new Date());
        return stats;
    }
}