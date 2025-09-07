package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * VIP购买记录实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_vip_purchase")
public class VipPurchase extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 套餐ID
     */
    private Long packageId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 实付金额（元）
     */
    private BigDecimal price;
    
    /**
     * 生效时间
     */
    private Date startTime;
    
    /**
     * 到期时间，NULL表示永久
     */
    private Date endTime;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 支付状态：0-待支付，1-支付成功，2-支付失败
     */
    private Integer paymentStatus;
    
    /**
     * 支付时间
     */
    private Date paymentTime;
}