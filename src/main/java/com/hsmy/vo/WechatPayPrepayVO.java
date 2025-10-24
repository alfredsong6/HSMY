package com.hsmy.vo;

import lombok.Data;

/**
 * 微信支付预下单响应
 */
@Data
public class WechatPayPrepayVO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 微信 appId
     */
    private String appId;

    /**
     * 时间戳
     */
    private String timeStamp;

    /**
     * 随机串
     */
    private String nonceStr;

    /**
     * 预支付信息
     */
    private String packageValue;

    /**
     * 签名类型
     */
    private String signType;

    /**
     * 签名值
     */
    private String paySign;

    /**
     * 微信预支付单号
     */
    private String prepayId;
}
