package com.hsmy.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 虚拟支付订单记录视图.
 */
@Data
public class VirtualPayRecordVO {

    private String outTradeNo;
    private String packageId;
    private BigDecimal amount;
    private Integer coinCount;
    private Integer bonusCoinCount;
    private String status;
    private Date createTime;
    private Date paymentTime;
}
