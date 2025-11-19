package com.hsmy.service.wechat.impl;

import com.hsmy.service.wechat.WechatPayClient;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 默认微信支付客户端，调用真实微信 SDK。
 */
@Component
@Profile({"prod"})
@RequiredArgsConstructor
public class DefaultWechatPayClient implements WechatPayClient {

    private final ObjectProvider<Config> configProvider;

    @Override
    public PrepayWithRequestPaymentResponse prepay(PrepayRequest request) {
        return getService().prepayWithRequestPayment(request);
    }

    @Override
    public Transaction queryOrder(QueryOrderByOutTradeNoRequest request) {
        return getService().queryOrderByOutTradeNo(request);
    }

    private JsapiServiceExtension getService() {
        Config config = configProvider.getIfAvailable();
        if (config == null) {
            throw new IllegalStateException("微信支付未配置");
        }
        return new JsapiServiceExtension.Builder().config(config).build();
    }
}

