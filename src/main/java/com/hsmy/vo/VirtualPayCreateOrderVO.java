package com.hsmy.vo;

import lombok.Data;

/**
 * 虚拟支付下单返回.
 */
@Data
public class VirtualPayCreateOrderVO {

    private String mode;
    private String offerId;
    private String outTradeNo;
    private String signData;
    private String paySig;
    private String signature;
}
