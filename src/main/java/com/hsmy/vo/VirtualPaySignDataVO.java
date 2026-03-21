package com.hsmy.vo;

import lombok.Data;

/**
 * 虚拟支付签名数据.
 */
@Data
public class VirtualPaySignDataVO {

    private String offerId;
    private Integer buyQuantity;
    private String currencyType;
    private String outTradeNo;
    private String attach;
    private Integer env;
}
