package com.hsmy.controller.mock;

import cn.hutool.json.JSONUtil;
import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.service.PaymentService;
import com.hsmy.vo.mock.MockWechatNotifyRequest;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

/**
 * 微信支付 Mock Controller，仅在 dev/test 环境使用。
 */
@RestController
@RequestMapping("/mock/wechat")
@ApiVersion(ApiVersionConstant.V1_1)
@Profile({"dev", "test"})
@RequiredArgsConstructor
public class MockWechatController {

    private final PaymentService paymentService;

    /**
     * 模拟微信回调。
     */
    @PostMapping("/notify")
    public Result<Void> mockNotify(@RequestBody MockWechatNotifyRequest request) {
        Transaction transaction = new Transaction();
        transaction.setOutTradeNo(request.getOrderNo());
        transaction.setTransactionId("MOCK_" + request.getOrderNo());
        transaction.setTradeState(request.getTradeState());
        transaction.setSuccessTime(OffsetDateTime.now().toString());

        RequestParam param = new RequestParam.Builder()
                .serialNumber("mock-serial")
                .nonce("mock-nonce")
                .signature("mock-signature")
                .timestamp(String.valueOf(System.currentTimeMillis() / 1000))
                .body(JSONUtil.toJsonStr(transaction))
                .build();

        paymentService.handleWechatPayNotification(param);
        return Result.success();
    }
}

