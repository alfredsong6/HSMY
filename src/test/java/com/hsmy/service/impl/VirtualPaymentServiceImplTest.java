package com.hsmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hsmy.domain.activity.ActivityDomain;
import com.hsmy.domain.activity.ActivityRule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsmy.config.WechatPayProperties;
import com.hsmy.domain.auth.AuthIdentity;
import com.hsmy.dto.VirtualPayCreateOrderRequest;
import com.hsmy.entity.RechargeOrder;
import com.hsmy.entity.UserStats;
import com.hsmy.enums.AuthProvider;
import com.hsmy.mapper.ActivityMapper;
import com.hsmy.mapper.RechargeOrderMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.mapper.meditation.MeritCoinTransactionMapper;
import com.hsmy.service.AuthIdentityService;
import com.hsmy.service.VirtualOrderShortPollingService;
import com.hsmy.service.wechat.WechatMiniAuthService;
import com.hsmy.service.wechat.dto.WechatSessionInfo;
import com.hsmy.vo.VirtualPayCreateOrderVO;
import com.hsmy.vo.VirtualPayBalanceVO;
import com.hsmy.vo.VirtualPayOrderStatusVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
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
    @Mock
    private VirtualOrderShortPollingService shortPollingService;

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
                restTemplateBuilder,
                shortPollingService
        );
    }

    @Test
    void syncWechatOrder_returnsFalse_whenVirtualOrderStillPending() {
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
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
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
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
    void syncWechatOrder_completesLocalDelivery_whenOrderAlreadyPaidButUndelivered() {
        RechargeOrder order = new RechargeOrder();
        order.setId(101L);
        order.setOrderNo("COIN201");
        order.setUserId(1L);
        order.setPaymentStatus(1);
        order.setPaymentMethod("wechat_virtual");
        order.setDelivered(0);
        order.setMeritCoins(8);
        order.setBonusCoins(2);
        when(rechargeOrderMapper.selectByOrderNoForUpdate("COIN201")).thenReturn(order);
        when(wechatPayProperties.getVirtual()).thenReturn(newVirtualConfig());
        when(meritCoinTransactionMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        boolean result = service.syncWechatOrder("COIN201");

        assertEquals(true, result);
        verify(userStatsMapper).addMeritCoins(1L, 8L);
        verify(userStatsMapper).addMeritCoins(1L, 2L);
        verify(rechargeOrderMapper).markDelivered(eq("COIN201"), any(Date.class));
        verify(restTemplateBuilder, never()).build();
    }

    @Test
    void queryBalance_returnsLocalAndWechatBalance() {
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
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

    @Test
    void createOrder_refreshesSessionKeyAndSignsWithLatestValue_whenCodeProvided() {
        when(wechatPayProperties.getVirtual()).thenReturn(newVirtualConfig());
        when(activityMapper.selectById(100L)).thenReturn(buildActivityDomain());

        AuthIdentity identity = buildIdentity();
        identity.setId(9L);
        identity.setSessionKeyEnc("stored-session-key");
        when(authIdentityService.getByProviderAndUserId(AuthProvider.WECHAT_MINI, 1L)).thenReturn(identity);
        when(wechatMiniAuthService.getDefaultAppId()).thenReturn("wx-mini-app");
        when(authIdentityService.touchLogin(eq(9L), eq(1L), isNull(), eq("union-new"), eq("latest-session-key"), any(Date.class)))
                .thenReturn(1);

        WechatSessionInfo sessionInfo = new WechatSessionInfo();
        sessionInfo.setSessionKey("latest-session-key");
        sessionInfo.setUnionId("union-new");
        when(wechatMiniAuthService.code2Session("wx-mini-app", "mini-login-code")).thenReturn(sessionInfo);

        VirtualPayCreateOrderRequest request = new VirtualPayCreateOrderRequest();
        request.setPackageId("activity_100");
        setRequestCode(request, "mini-login-code");

        VirtualPayCreateOrderVO result = service.createOrder(1L, "tester", request);

        assertEquals(hmacSha256Hex(result.getSignData(), "latest-session-key"), result.getSignature());
        assertEquals(hmacSha256Hex("requestVirtualPayment&" + result.getSignData(), "app-key"), result.getPaySig());
        verify(wechatMiniAuthService).code2Session("wx-mini-app", "mini-login-code");
        verify(authIdentityService).touchLogin(eq(9L), eq(1L), isNull(), eq("union-new"), eq("latest-session-key"), any(Date.class));
    }

    @Test
    void createOrder_usesStoredSessionKey_whenCodeMissing() {
        when(wechatPayProperties.getVirtual()).thenReturn(newVirtualConfig());
        when(activityMapper.selectById(100L)).thenReturn(buildActivityDomain());

        AuthIdentity identity = buildIdentity();
        identity.setId(9L);
        identity.setSessionKeyEnc("stored-session-key");
        when(authIdentityService.getByProviderAndUserId(AuthProvider.WECHAT_MINI, 1L)).thenReturn(identity);

        VirtualPayCreateOrderRequest request = new VirtualPayCreateOrderRequest();
        request.setPackageId("activity_100");

        VirtualPayCreateOrderVO result = service.createOrder(1L, "tester", request);

        assertEquals(hmacSha256Hex(result.getSignData(), "stored-session-key"), result.getSignature());
        verify(wechatMiniAuthService, never()).code2Session(any(), any());
        verify(authIdentityService, never()).touchLogin(any(), any(), any(), any(), any(), any());
    }

    @Test
    void confirmOrder_returnsSyncedStatus_withoutRegisteringPolling_whenImmediateSyncSucceeds() {
        RechargeOrder pendingOrder = new RechargeOrder();
        pendingOrder.setOrderNo("COIN300");
        pendingOrder.setUserId(1L);
        pendingOrder.setPackageId("activity_100");
        pendingOrder.setPaymentMethod("wechat_virtual");
        pendingOrder.setPaymentStatus(0);
        pendingOrder.setDelivered(0);
        pendingOrder.setMeritCoins(10);
        pendingOrder.setBonusCoins(5);

        RechargeOrder deliveredOrder = new RechargeOrder();
        deliveredOrder.setOrderNo("COIN300");
        deliveredOrder.setUserId(1L);
        deliveredOrder.setPackageId("activity_100");
        deliveredOrder.setPaymentMethod("wechat_virtual");
        deliveredOrder.setPaymentStatus(1);
        deliveredOrder.setDelivered(1);
        deliveredOrder.setMeritCoins(10);
        deliveredOrder.setBonusCoins(5);

        when(rechargeOrderMapper.selectByOrderNo("COIN300")).thenReturn(pendingOrder, deliveredOrder);

        VirtualPaymentServiceImpl spyService = spy(service);
        doReturn(true).when(spyService).syncWechatOrder("COIN300");

        VirtualPayOrderStatusVO result = spyService.confirmOrder(1L, "COIN300");

        assertEquals("DELIVERED", result.getStatus());
        verify(shortPollingService, never()).ensurePolling("COIN300");
    }

    @Test
    void confirmOrder_registersShortPolling_whenImmediateSyncStillPending() {
        RechargeOrder pendingOrder = new RechargeOrder();
        pendingOrder.setOrderNo("COIN301");
        pendingOrder.setUserId(1L);
        pendingOrder.setPackageId("activity_100");
        pendingOrder.setPaymentMethod("wechat_virtual");
        pendingOrder.setPaymentStatus(0);
        pendingOrder.setDelivered(0);
        pendingOrder.setMeritCoins(10);
        pendingOrder.setBonusCoins(5);

        RechargeOrder refreshedPendingOrder = new RechargeOrder();
        refreshedPendingOrder.setOrderNo("COIN301");
        refreshedPendingOrder.setUserId(1L);
        refreshedPendingOrder.setPackageId("activity_100");
        refreshedPendingOrder.setPaymentMethod("wechat_virtual");
        refreshedPendingOrder.setPaymentStatus(0);
        refreshedPendingOrder.setDelivered(0);
        refreshedPendingOrder.setMeritCoins(10);
        refreshedPendingOrder.setBonusCoins(5);

        when(rechargeOrderMapper.selectByOrderNo("COIN301")).thenReturn(pendingOrder, refreshedPendingOrder);

        VirtualPaymentServiceImpl spyService = spy(service);
        doReturn(false).when(spyService).syncWechatOrder("COIN301");

        VirtualPayOrderStatusVO result = spyService.confirmOrder(1L, "COIN301");

        assertEquals("CREATED", result.getStatus());
        verify(shortPollingService).ensurePolling("COIN301");
    }

    private WechatPayProperties.VirtualPay newVirtualConfig() {
        WechatPayProperties.VirtualPay virtualPay = new WechatPayProperties.VirtualPay();
        virtualPay.setEnabled(true);
        virtualPay.setEnv(1);
        virtualPay.setAppKey("app-key");
        virtualPay.setOfferId("offer-id");
        return virtualPay;
    }

    private ActivityDomain buildActivityDomain() {
        ActivityRule rule = new ActivityRule();
        rule.setAmount(new BigDecimal("6.66"));
        rule.setGive(new BigDecimal("60"));
        rule.setGift(new BigDecimal("6"));

        ActivityDomain activity = new ActivityDomain();
        activity.setId(100L);
        activity.setActivityName("Coin Pack");
        activity.setDescription("Virtual coin pack");
        activity.setStatus(1);
        activity.setRules(rule);
        return activity;
    }

    private AuthIdentity buildIdentity() {
        AuthIdentity identity = new AuthIdentity();
        identity.setUserId(1L);
        identity.setOpenId("openid");
        return identity;
    }

    private void setRequestCode(VirtualPayCreateOrderRequest request, String code) {
        assertDoesNotThrow(() -> {
            Field codeField = request.getClass().getDeclaredField("code");
            codeField.setAccessible(true);
            codeField.set(request, code);
        }, "VirtualPayCreateOrderRequest should support code");
    }

    private String hmacSha256Hex(String content, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            fail(e);
            return null;
        }
    }
}
