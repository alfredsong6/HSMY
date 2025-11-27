package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 微信支付预下单请求
 */
@Data
public class WechatPayPrepayRequest {

    /**
     * 商品ID（关联活动ID）
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 充值金额（单位：元）
     */
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0")
    private BigDecimal amount;

    /**
     * 购买功德币数量
     */
    private Integer meritCoins;

    /**
     * 赠送功德币数量
     */
    private Integer bonusCoins;

    /**
     * 订单描述
     */
    private String description;

    /**
     * JSAPI 支付需提供用户 openid
     */
    //@NotBlank(message = "微信 openId 不能为空")
    private String payerOpenId;

    /**
     * 自定义附加数据（会在回调中原样返回）
     */
    private String attach;
}
