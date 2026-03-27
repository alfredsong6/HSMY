package com.hsmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsmy.config.WechatPayProperties;
import com.hsmy.domain.auth.AuthIdentity;
import com.hsmy.entity.RechargeOrder;
import com.hsmy.entity.UserStats;
import com.hsmy.enums.AuthProvider;
import com.hsmy.mapper.ActivityMapper;
import com.hsmy.mapper.RechargeOrderMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.mapper.meditation.MeritCoinTransactionMapper;
import com.hsmy.service.AuthIdentityService;
import com.hsmy.service.wechat.WechatMiniAuthService;
import com.hsmy.vo.VirtualPayBalanceVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirtualPaymentServiceImplTest {

    @Mock
    private ActivityMapper activityMapper;
    @Mock
    private RechargeOrderMapper rechargeOrderMapper;
    @Mock
    private UserStatsMapper userStatsMapper;
    @Mock
    private MeritCoinTransactionMapper meritCoinTransactionMapper;
    @Mock
    private WechatPayProperties wechatPayProperties;
    @Mock
    private AuthIdentityService authIdentityService;
    @Mock
    private WechatMiniAuthService wechatMiniAuthService;
    @Mock
    private RestTemplateBuilder restTemplateBuilder;
    @Mock
    private RestTemplate restTemplate;

    private VirtualPaymentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new VirtualPaymentServiceImpl(
                activityMapper,
                rechargeOrderMapper,
                userStatsMapper,
                meritCoinTransactionMapper,
                wechatPayProperties,
                new ObjectMapper(),
                authIdentityService,
                wechatMiniAuthService,
                restTemplateBuilder
        );
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
    }

    @Test
    void syncWechatOrder_returnsFalse_whenVirtualOrderStillPending() {
        RechargeOrder order = new RechargeOrder();
        order.setOrderNo("COIN100");
        order.setUserId(1L);
        order.setPaymentStatus(0);
        order.setPaymentMethod("wechat_virtual");
        when(rechargeOrderMapper.selectByOrderNoForUpdate("COIN100")).thenReturn(order);
        when(wechatPayProperties.getVirtual()).thenReturn(newVirtualConfig());
        when(wechatMiniAuthService.getAccessToken()).thenReturn("token");
        when(authIdentityService.getByProviderAndUserId(AuthProvider.WECHAT_MINI, 1L)).thenReturn(buildIdentity());
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"errcode\":0,\"errmsg\":\"ok\",\"order\":{\"status\":1}}"));

        boolean result = service.syncWechatOrder("COIN100");

        assertEquals(false, result);
        verify(rechargeOrderMapper, never()).updatePaymentStatusByOrderNo(eq("COIN100"), any(), any(), any(), any());
        verify(rechargeOrderMapper, never()).markDelivered(eq("COIN100"), any(Date.class));
    }

    @Test
    void syncWechatOrder_marksOrderSuccessAndDelivered_whenVirtualOrderPaid() {
        RechargeOrder order = new RechargeOrder();
        order.setId(100L);
        order.setOrderNo("COIN200");
        order.setUserId(1L);
        order.setPaymentStatus(0);
        order.setPaymentMethod("wechat_virtual");
        order.setDelivered(0);
        order.setMeritCoins(10);
        order.setBonusCoins(5);
        when(rechargeOrderMapper.selectByOrderNoForUpdate("COIN200")).thenReturn(order);
        when(wechatPayProperties.getVirtual()).thenReturn(newVirtualConfig());
        when(wechatMiniAuthService.getAccessToken()).thenReturn("token");
        when(authIdentityService.getByProviderAndUserId(AuthProvider.WECHAT_MINI, 1L)).thenReturn(buildIdentity());
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"errcode\":0,\"errmsg\":\"ok\",\"order\":{\"status\":2,\"wxpay_order_id\":\"wxpay123\",\"paid_time\":1710000000}}"));
        when(meritCoinTransactionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        boolean result = service.syncWechatOrder("COIN200");

        assertEquals(true, result);
        verify(rechargeOrderMapper).updatePaymentStatusByOrderNo(eq("COIN200"), eq(1), eq("wxpay123"), any(Date.class), any(String.class));
        verify(userStatsMapper).addMeritCoins(1L, 10L);
        verify(userStatsMapper).addMeritCoins(1L, 5L);
        verify(rechargeOrderMapper).markDelivered(eq("COIN200"), any(Date.class));
    }

    @Test
    void queryBalance_returnsLocalAndWechatBalance() {
        when(wechatPayProperties.getVirtual()).thenReturn(newVirtualConfig());
        when(wechatMiniAuthService.getAccessToken()).thenReturn("token");
        AuthIdentity identity = buildIdentity();
        identity.setSessionKeyEnc("session-key");
        when(authIdentityService.getByProviderAndUserId(AuthProvider.WECHAT_MINI, 1L)).thenReturn(identity);
        UserStats userStats = new UserStats();
        userStats.setUserId(1L);
        userStats.setMeritCoins(88L);
        when(userStatsMapper.selectByUserId(1L)).thenReturn(userStats);
        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"errcode\":0,\"errmsg\":\"ok\",\"balance\":120}"));

        VirtualPayBalanceVO result = service.queryBalance(1L);

        assertEquals(Long.valueOf(1L), result.getUserId());
        assertEquals(Long.valueOf(88L), result.getBalance());
        assertEquals(Long.valueOf(120L), result.getWechatBalance());
    }

    private WechatPayProperties.VirtualPay newVirtualConfig() {
        WechatPayProperties.VirtualPay virtualPay = new WechatPayProperties.VirtualPay();
        virtualPay.setEnabled(true);
        virtualPay.setEnv(1);
        virtualPay.setAppKey("app-key");
        return virtualPay;
    }

    private AuthIdentity buildIdentity() {
        AuthIdentity identity = new AuthIdentity();
        identity.setUserId(1L);
        identity.setOpenId("openid");
        return identity;
    }
}
