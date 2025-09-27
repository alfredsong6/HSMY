package com.hsmy.test.controller;

import com.hsmy.controller.knock.KnockController;
import com.hsmy.service.KnockService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.KnockVO;
import com.hsmy.vo.AutoKnockStartVO;
import com.hsmy.vo.AutoKnockStopVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 敲击控制器单元测试
 */
@WebMvcTest(KnockController.class)
class KnockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KnockService knockService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Mock UserContextUtil静态方法
        mockStatic(UserContextUtil.class);
        when(UserContextUtil.requireCurrentUserId()).thenReturn(1L);
    }

    @Test
    void testManualKnock_Success() throws Exception {
        KnockVO knockVO = new KnockVO();
        knockVO.setKnockCount(10);
        knockVO.setKnockSound("default");
        knockVO.setSessionDuration(60);

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("knockCount", 10);
        mockResult.put("meritGained", 10);
        mockResult.put("totalMerit", 1010);
        mockResult.put("sessionId", "knock_sess_123");

        when(knockService.manualKnock(any(KnockVO.class))).thenReturn(mockResult);

        mockMvc.perform(post("/knock/manual")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(knockVO))
                .header("Authorization", "Bearer sess_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.knockCount").value(10))
                .andExpect(jsonPath("$.data.meritGained").value(10));

        verify(knockService).manualKnock(any(KnockVO.class));
    }

    @Test
    void testStartAutoKnock_Success() throws Exception {
        AutoKnockStartVO startVO = new AutoKnockStartVO();
        startVO.setDuration(300);
        startVO.setKnockInterval(1000);
        startVO.setKnockSound("default");

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("sessionId", "auto_knock_sess_123");
        mockResult.put("startTime", System.currentTimeMillis());
        mockResult.put("duration", 300);

        when(knockService.startAutoKnock(eq(1L), any(AutoKnockStartVO.class)))
            .thenReturn(mockResult);

        mockMvc.perform(post("/knock/auto/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(startVO))
                .header("Authorization", "Bearer sess_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.sessionId").value("auto_knock_sess_123"));
    }

    @Test
    void testStopAutoKnock_Success() throws Exception {
        AutoKnockStopVO stopVO = new AutoKnockStopVO();
        stopVO.setSessionId("auto_knock_sess_123");
        stopVO.setActualDuration(250);

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("sessionId", "auto_knock_sess_123");
        mockResult.put("totalKnocks", 250);
        mockResult.put("meritGained", 250);
        mockResult.put("finalMerit", 1250);

        when(knockService.stopAutoKnock(eq(1L), any(AutoKnockStopVO.class)))
            .thenReturn(mockResult);

        mockMvc.perform(post("/knock/auto/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stopVO))
                .header("Authorization", "Bearer sess_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalKnocks").value(250));
    }

    @Test
    void testGetKnockStats_Success() throws Exception {
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("todayKnocks", 100);
        mockStats.put("totalKnocks", 5000);
        mockStats.put("todayMerit", 100);
        mockStats.put("totalMerit", 5000);

        when(knockService.getKnockStats(1L)).thenReturn(mockStats);

        mockMvc.perform(get("/knock/stats")
                .header("Authorization", "Bearer sess_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.todayKnocks").value(100))
                .andExpect(jsonPath("$.data.totalKnocks").value(5000));
    }

    @Test
    void testGetAutoKnockStatus_Success() throws Exception {
        Map<String, Object> mockStatus = new HashMap<>();
        mockStatus.put("isActive", true);
        mockStatus.put("sessionId", "auto_knock_sess_123");
        mockStatus.put("remainingTime", 180);
        mockStatus.put("currentKnocks", 120);

        when(knockService.getAutoKnockStatus(1L)).thenReturn(mockStatus);

        mockMvc.perform(get("/knock/auto/status")
                .header("Authorization", "Bearer sess_123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.sessionId").value("auto_knock_sess_123"));
    }

    @Test
    void testManualKnock_Unauthorized() throws Exception {
        // Mock用户未登录
        when(UserContextUtil.requireCurrentUserId()).thenThrow(new RuntimeException("用户未登录"));

        KnockVO knockVO = new KnockVO();
        knockVO.setKnockCount(10);

        mockMvc.perform(post("/knock/manual")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(knockVO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}