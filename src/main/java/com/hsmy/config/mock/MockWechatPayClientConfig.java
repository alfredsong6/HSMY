package com.hsmy.config.mock;

import com.hsmy.service.wechat.WechatPayClient;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * dev/test 环境使用的微信支付 Mock 客户端.
 */
@Configuration
@Profile({"dev", "test"})
@Slf4j
public class MockWechatPayClientConfig {

    @Bean
    @Primary
    public WechatPayClient mockWechatPayClient() {
        return new WechatPayClient() {
            @Override
            public PrepayWithRequestPaymentResponse prepay(PrepayRequest request) {
                log.info("[MOCK] 微信预下单 orderNo={}", request.getOutTradeNo());
                PrepayWithRequestPaymentResponse resp = new PrepayWithRequestPaymentResponse();
                resp.setAppId(request.getAppid());
                resp.setPackageVal("prepay_id=MOCK_" + request.getOutTradeNo());
                resp.setNonceStr("mock-nonce");
                resp.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));
                resp.setSignType("RSA");
                resp.setPaySign("mock-sign");
                return resp;
            }

            @Override
            public Transaction queryOrder(QueryOrderByOutTradeNoRequest request) {
                log.info("[MOCK] 查询订单状态 orderNo={}", request.getOutTradeNo());
                Transaction transaction = new Transaction();
                transaction.setOutTradeNo(request.getOutTradeNo());
                transaction.setTransactionId("MOCK_" + request.getOutTradeNo());
                transaction.setTradeState(Transaction.TradeStateEnum.SUCCESS);
                transaction.setSuccessTime(java.time.OffsetDateTime.now().toString());
                return transaction;
            }
        };
    }
}

