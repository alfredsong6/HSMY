package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 购买记录实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_purchase_record")
public class PurchaseRecord extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 道具ID
     */
    private Long itemId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 购买价格（功德币）
     */
    private Integer price;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 总金额（功德币）
     */
    private Integer totalAmount;
    
    /**
     * 购买时间
     */
    private Date purchaseTime;
    
    /**
     * 订单状态：0-失败，1-成功，2-退款
     */
    private Integer status;
}