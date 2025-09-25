package com.hsmy.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户典籍购买记录VO
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Data
public class UserScripturePurchaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 购买记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 典籍ID
     */
    private Long scriptureId;

    /**
     * 典籍名称
     */
    private String scriptureName;

    /**
     * 典籍类型
     */
    private String scriptureType;

    /**
     * 典籍封面URL
     */
    private String coverUrl;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date purchaseTime;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastReadTime;

    /**
     * 阅读进度百分比
     */
    private BigDecimal readingProgress;

    /**
     * 剩余天数
     */
    private Long remainingDays;

    /**
     * 是否即将过期（7天内）
     */
    private Boolean isExpiringSoon;

    /**
     * 是否为买断模式
     */
    private Boolean isPermanent;
}