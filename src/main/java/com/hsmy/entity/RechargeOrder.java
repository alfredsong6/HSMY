package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值订单实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_recharge_order")
public class RechargeOrder extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 充值金额（元）
     */
    private BigDecimal amount;
    
    /**
     * 获得功德币
     */
    private Integer meritCoins;
    
    /**
     * 赠送功德币
     */
    private Integer bonusCoins;
    
    /**
     * 支付方式：alipay-支付宝，wechat-微信，bank-银行卡
     */
    private String paymentMethod;
    
    /**
     * 支付状态：0-待支付，1-支付成功，2-支付失败，3-已退款
     */
    private Integer paymentStatus;

    /**
     * Payment status description.
     */
    private String paymentStatusDesc;

    /**
     * 订单状态查询次数
     */
    private Integer queryCount;

    /**
     * 最近一次查询时间
     */
    private Date lastQueryTime;

    /**
     * 支付时间
     */
    private Date paymentTime;
    
    /**
     * 第三方交易号
     */
    private String transactionId;
    
    /**
     * 备注
     */
    private String remark;
}
