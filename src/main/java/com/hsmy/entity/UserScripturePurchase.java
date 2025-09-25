package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户典籍购买记录实体类
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_scripture_purchase")
public class UserScripturePurchase extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 典籍ID
     */
    private Long scriptureId;

    /**
     * 支付福币数量
     */
    private Integer meritCoinsPaid;

    /**
     * 购买月数
     */
    private Integer purchaseMonths;

    /**
     * 购买时间
     */
    private Date purchaseTime;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 是否过期：0-未过期，1-已过期
     */
    private Integer isExpired;

    /**
     * 阅读次数
     */
    private Integer readCount;

    /**
     * 最后阅读时间
     */
    private Date lastReadTime;

    /**
     * 阅读进度百分比
     */
    private BigDecimal readingProgress;

    /**
     * 最后阅读位置（字符位置）
     */
    private Integer lastReadingPosition;
}