package com.hsmy.vo.mock;

import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 模拟微信回调请求.
 */
@Data
public class MockWechatNotifyRequest {

    @NotBlank
    private String orderNo;

    private Transaction.TradeStateEnum tradeState = Transaction.TradeStateEnum.SUCCESS;
}

