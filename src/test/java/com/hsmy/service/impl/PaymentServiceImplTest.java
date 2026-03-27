package com.hsmy.service.impl;

import com.hsmy.config.WechatPayProperties;
import com.hsmy.domain.activity.ActivityDomain;
import com.hsmy.domain.activity.ActivityRule;
import com.hsmy.domain.auth.AuthIdentity;
import com.hsmy.dto.WechatPayPrepayRequest;
import com.hsmy.entity.RechargeOrder;
import com.hsmy.enums.AuthProvider;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.ActivityMapper;
import com.hsmy.mapper.RechargeOrderMapper;
import com.hsmy.service.AuthIdentityService;
import com.hsmy.service.VirtualPaymentService;
import com.hsmy.service.wechat.WechatPayClient;
import com.hsmy.vo.WechatPayPrepayVO;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private ActivityMapper activityMapper;
    @Mock
    private WechatPayProperties wechatPayProperties;
    @Mock
    private RechargeOrderMapper rechargeOrderMapper;
    @Mock
    private ObjectProvider<Config> wechatPayConfigProvider;
    @Mock
    private WechatPayClient wechatPayClient;
    @Mock
    private ObjectProvider<com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension> jsapiServiceProvider;
    @Mock
    private ObjectProvider<NotificationParser> notificationParserProvider;
    @Mock
    private AuthIdentityService authIdentityService;
    @Mock
    private VirtualPaymentService virtualPaymentService;
    @Mock
    private PaymentOrderProcessor paymentOrderProcessor;
    @Mock
    private WechatPayNotificationAsyncService wechatPayNotificationAsyncService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;

    private PaymentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PaymentServiceImpl(
                activityMapper,
                wechatPayProperties,
                rechargeOrderMapper,
                wechatPayConfigProvider,
                wechatPayClient,
                jsapiServiceProvider,
                notificationParserProvider,
                authIdentityService,
                virtualPaymentService,
                paymentOrderProcessor,
                wechatPayNotificationAsyncService,
                redisTemplate
        );
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(service, "activeProfile", "prod");
    }

    @Test
    void createWechatPrepay_returnsCachedResponse_whenIdempotencyHit() {
        WechatPayPrepayRequest request = buildRequest();
        request.setIdempotencyKey("idem-1");
        when(wechatPayProperties.isEnabled()).thenReturn(true);

        WechatPayPrepayVO cached = new WechatPayPrepayVO();
        cached.setOrderNo("ORDER-1");
        when(valueOperations.get(anyString())).thenReturn(cached);

        WechatPayPrepayVO result = service.createWechatPrepay(1L, "tester", request);

        assertSame(cached, result);
        verify(wechatPayClient, never()).prepay(any());
        verify(rechargeOrderMapper, never()).insert(any(RechargeOrder.class));
    }

    @Test
    void createWechatPrepay_rejectsWhenIdempotencyLockBusy() {
        WechatPayPrepayRequest request = buildRequest();
        request.setIdempotencyKey("idem-2");
        when(wechatPayProperties.isEnabled()).thenReturn(true);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(valueOperations.setIfAbsent(anyString(), any(), anyLong(), any(TimeUnit.class))).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createWechatPrepay(1L, "tester", request));

        assertEquals("请求处理中，请勿重复提交", ex.getMessage());
    }

    @Test
    void createWechatPrepay_cachesResponseOnSuccess() {
        WechatPayPrepayRequest request = buildRequest();
        request.setIdempotencyKey("idem-3");
        when(wechatPayProperties.isEnabled()).thenReturn(true);
        when(wechatPayProperties.getNotifyUrl()).thenReturn("https://notify");
        when(wechatPayProperties.getAppId()).thenReturn("appId");
        when(wechatPayProperties.getMchId()).thenReturn("mchId");
        when(wechatPayProperties.getDescription()).thenReturn("desc");
        when(wechatPayProperties.getCurrency()).thenReturn("CNY");
        when(valueOperations.get(anyString())).thenReturn(null);
        when(valueOperations.setIfAbsent(anyString(), any(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        ActivityDomain activity = buildActivity(request.getAmount(), request.getMeritCoins(), request.getBonusCoins());
        when(activityMapper.selectById(request.getProductId())).thenReturn(activity);

        AuthIdentity identity = new AuthIdentity();
        identity.setUserId(1L);
        identity.setOpenId("openid");
        when(authIdentityService.getByProviderAndUserId(AuthProvider.WECHAT_MINI, 1L)).thenReturn(identity);

        PrepayWithRequestPaymentResponse response = new PrepayWithRequestPaymentResponse();
        response.setAppId("appId");
        response.setTimeStamp("ts");
        response.setNonceStr("nonce");
        response.setPackageVal("prepay_id=prepay123");
        response.setSignType("RSA");
        response.setPaySign("sign");
        when(wechatPayClient.prepay(any())).thenReturn(response);

        WechatPayPrepayVO result = service.createWechatPrepay(1L, "tester", request);

        assertEquals("prepay123", result.getPrepayId());
        verify(rechargeOrderMapper).insert(any(RechargeOrder.class));
        verify(valueOperations).set(anyString(), any(WechatPayPrepayVO.class), anyLong(), eq(TimeUnit.SECONDS));
        verify(redisTemplate).delete(anyString());
    }

    @Test
    void validateProduct_skipsAmountCheckInTestProfile() {
        ReflectionTestUtils.setField(service, "activeProfile", "test");
        long userId = 1993582099294195712L;
        WechatPayPrepayRequest request = buildRequest();
        request.setAmount(new BigDecimal("0.01"));
        when(wechatPayProperties.isEnabled()).thenReturn(true);
        when(wechatPayProperties.getNotifyUrl()).thenReturn("https://notify");
        when(wechatPayProperties.getAppId()).thenReturn("appId");
        when(wechatPayProperties.getMchId()).thenReturn("mchId");
        when(wechatPayProperties.getDescription()).thenReturn("desc");
        when(wechatPayProperties.getCurrency()).thenReturn("CNY");
        when(valueOperations.get(anyString())).thenReturn(null);
        when(valueOperations.setIfAbsent(anyString(), any(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        ActivityDomain activity = buildActivity(new BigDecimal("100.00"), request.getMeritCoins(), request.getBonusCoins());
        when(activityMapper.selectById(request.getProductId())).thenReturn(activity);

        AuthIdentity identity = new AuthIdentity();
        identity.setUserId(userId);
        identity.setOpenId("openid");
        when(authIdentityService.getByProviderAndUserId(AuthProvider.WECHAT_MINI, userId)).thenReturn(identity);

        PrepayWithRequestPaymentResponse response = new PrepayWithRequestPaymentResponse();
        response.setAppId("appId");
        response.setTimeStamp("ts");
        response.setNonceStr("nonce");
        response.setPackageVal("prepay_id=prepay456");
        response.setSignType("RSA");
        response.setPaySign("sign");
        when(wechatPayClient.prepay(any())).thenReturn(response);

        WechatPayPrepayVO result = service.createWechatPrepay(userId, "tester", request);

        assertEquals("prepay456", result.getPrepayId());
    }

    @Test
    void validateProduct_rejectsAmountMismatchInNonTestProfile() {
        ReflectionTestUtils.setField(service, "activeProfile", "prod");
        WechatPayPrepayRequest request = buildRequest();
        request.setAmount(new BigDecimal("9.99"));
        when(wechatPayProperties.isEnabled()).thenReturn(true);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(valueOperations.setIfAbsent(anyString(), any(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        ActivityDomain activity = buildActivity(new BigDecimal("100.00"), request.getMeritCoins(), request.getBonusCoins());
        when(activityMapper.selectById(request.getProductId())).thenReturn(activity);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createWechatPrepay(1L, "tester", request));

        verify(wechatPayClient, never()).prepay(any());
    }

    @Test
    void syncWechatOrder_delegatesToVirtualPaymentService_whenOrderIsVirtual() {
        when(wechatPayProperties.isEnabled()).thenReturn(true);
        RechargeOrder order = new RechargeOrder();
        order.setOrderNo("COIN123");
        order.setPaymentMethod("wechat_virtual");
        order.setPaymentStatus(0);
        when(rechargeOrderMapper.selectByOrderNo("COIN123")).thenReturn(order);
        when(virtualPaymentService.syncWechatOrder("COIN123")).thenReturn(true);

        boolean result = service.syncWechatOrder("COIN123");

        assertEquals(true, result);
        verify(virtualPaymentService).syncWechatOrder("COIN123");
        verify(wechatPayClient, never()).queryOrder(any());
    }

    @Test
    void syncWechatOrder_usesNormalWechatFlow_whenOrderIsNormal() {
        when(wechatPayProperties.isEnabled()).thenReturn(true);
        when(wechatPayProperties.getMchId()).thenReturn("mchId");
        RechargeOrder order = new RechargeOrder();
        order.setOrderNo("WX123");
        order.setPaymentMethod("wechat");
        order.setPaymentStatus(0);
        when(rechargeOrderMapper.selectByOrderNo("WX123")).thenReturn(order);

        Transaction transaction = new Transaction();
        transaction.setTradeState(Transaction.TradeStateEnum.SUCCESS);
        when(wechatPayClient.queryOrder(any())).thenReturn(transaction);
        when(paymentOrderProcessor.applyTradeState("WX123", transaction)).thenReturn(true);

        boolean result = service.syncWechatOrder("WX123");

        assertEquals(true, result);
        verify(wechatPayClient).queryOrder(any());
        verify(paymentOrderProcessor).applyTradeState("WX123", transaction);
        verify(paymentOrderProcessor).grantMeritCoins("WX123");
        verify(virtualPaymentService, never()).syncWechatOrder(anyString());
    }

    private WechatPayPrepayRequest buildRequest() {
        WechatPayPrepayRequest request = new WechatPayPrepayRequest();
        request.setProductId(10L);
        request.setAmount(new BigDecimal("100.00"));
        request.setMeritCoins(10);
        request.setBonusCoins(5);
        request.setDescription("desc");
        return request;
    }

    private ActivityDomain buildActivity(BigDecimal amount, Integer meritCoins, Integer bonusCoins) {
        ActivityRule rule = new ActivityRule();
        rule.setAmount(amount);
        rule.setGive(BigDecimal.valueOf(meritCoins == null ? 0 : meritCoins));
        rule.setGift(BigDecimal.valueOf(bonusCoins == null ? 0 : bonusCoins));
        ActivityDomain domain = new ActivityDomain();
        domain.setRules(rule);
        return domain;
    }
}
