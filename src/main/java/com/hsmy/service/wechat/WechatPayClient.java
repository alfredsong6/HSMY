package com.hsmy.service.wechat;

import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.jsapi.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.model.Transaction;

/**
 * 微信支付客户端抽象，方便在不同环境下替换实现。
 */
public interface WechatPayClient {

    PrepayWithRequestPaymentResponse prepay(PrepayRequest request);

    Transaction queryOrder(QueryOrderByOutTradeNoRequest request);

    void closeOrder(CloseOrderRequest request);
}
