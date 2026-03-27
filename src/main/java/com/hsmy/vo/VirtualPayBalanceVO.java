package com.hsmy.vo;

import lombok.Data;

/**
 * 虚拟支付余额视图.
 */
@Data
public class VirtualPayBalanceVO {

    private Long userId;
    private Long balance;

    /**
     * Latest balance returned by WeChat virtual payment API.
     */
    private Long wechatBalance;
}
