package com.hsmy.service.impl;

import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.model.Transaction.TradeStateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WechatPayNotificationAsyncService {

    private final PaymentOrderProcessor paymentOrderProcessor;

    @Async("asyncExecutor")
    @Transactional(rollbackFor = Exception.class)
    public void handle(Transaction transaction) {
        if (transaction == null) {
            log.warn("微信支付回调交易为空，忽略处理");
            return;
        }
        String orderNo = transaction.getOutTradeNo();
        TradeStateEnum tradeStateEnum = transaction.getTradeState();
        String transactionId = transaction.getTransactionId();

        log.info("异步处理微信支付回调，orderNo={}, tradeState={}, transactionId={}",
                orderNo, tradeStateEnum != null ? tradeStateEnum.name() : "UNKNOWN", transactionId);

        try {
            boolean terminal = paymentOrderProcessor.applyTradeState(orderNo, transaction);
            if (terminal && tradeStateEnum == TradeStateEnum.SUCCESS) {
                paymentOrderProcessor.grantMeritCoins(orderNo);
            }
        } catch (Exception e) {
            log.error("异步处理微信支付回调失败，orderNo={}", orderNo, e);
        }
    }
}
