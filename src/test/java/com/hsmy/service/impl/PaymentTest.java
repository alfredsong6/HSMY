package com.hsmy.service.impl;


import com.hsmy.dto.WechatPayPrepayRequest;
import com.hsmy.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class PaymentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PaymentService paymentService;

    @Test
    public void test() {
        WechatPayPrepayRequest wechatPayPrepayRequest = new WechatPayPrepayRequest();
        wechatPayPrepayRequest.setProductId(89012345678901L);
        wechatPayPrepayRequest.setAmount(new BigDecimal("0.01"));
        wechatPayPrepayRequest.setMeritCoins(10);
        wechatPayPrepayRequest.setBonusCoins(10);
        paymentService.createWechatPrepay(1993582099294195712L, "test", wechatPayPrepayRequest);
    }
}
