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
    private String scriptureId;

    /**
     * 购买类型
     */
    private String purchaseType;

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
     * 典籍音频URL（可选）
     */
    private String audioUrl;

    /**
     * 典籍简介
     */
    private String description;

    /**
     * 是否热门
     */
    private Integer isHot;

    /**
     * 购买价格
     */
    private Integer price;

    /**
     * 买断价格
     */
    private Integer permanentPrice;

    /**
     * 计价单位
     */
    private String priceUnit;

    /**
     * 购买时长（月）
     */
    private Integer durationMonths;

    /**
     * 难度等级
     */
    private Integer difficultyLevel;

    /**
     * 总字数（分段汇总）
     */
    private Integer totalWordCount;

    /**
     * 字数（旧字段兼容）
     */
    private Integer wordCount;

    /**
     * 分段总数
     */
    private Integer sectionCount;

    /**
     * 试读分段数
     */
    private Integer previewSectionCount;

    /**
     * 是否可试读
     */
    private Boolean canPreview;

    /**
     * 分类标签
     */
    private String categoryTags;

    /**
     * 排序序号
     */
    private Integer sortOrder;

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
     * 状态：1-有效 2-过期 3-退款/失效
     */
    private Integer status;

    /**
     * 是否已购买（列表语义：有记录即true）
     */
    private Boolean isPurchased;

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
     * 最后阅读的分段ID
     */
    private Long lastSectionId;

    /**
     * 已完成分段数
     */
    private Integer completedSections;

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
