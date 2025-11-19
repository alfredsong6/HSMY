package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户购买道具的结果视图.
 */
@Data
public class UserItemPurchaseResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;
    private Long itemId;
    private String itemName;
    private Integer quantity;
    private Integer totalPrice;
    private Long remainingCoins;

    private Date expireTime;
    private Integer usageMode;
    private Integer remainingUses;
    private Integer stackCount;
}
