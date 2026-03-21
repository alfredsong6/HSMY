package com.hsmy.vo;

import lombok.Data;

import java.util.Date;

/**
 * 虚拟支付订单状态视图.
 */
@Data
public class VirtualPayOrderStatusVO {

    private String outTradeNo;
    private String packageId;
    private String status;
    private Integer coinCount;
    private Integer bonusCoinCount;
    private Long balance;
    private Date paymentTime;
}
