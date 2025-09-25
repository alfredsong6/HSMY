package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户典籍购买统计VO
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Data
public class UserPurchaseStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总购买次数
     */
    private Integer totalPurchases;

    /**
     * 有效购买数量（未过期）
     */
    private Integer validPurchases;

    /**
     * 即将过期数量（7天内过期）
     */
    private Integer expiringSoon;

    /**
     * 已过期数量
     */
    private Integer expired;

    /**
     * 总阅读次数
     */
    private Long totalReadCount;

    /**
     * 平均阅读进度（百分比）
     */
    private Double averageProgress;

    public UserPurchaseStatsVO() {
    }

    public UserPurchaseStatsVO(Integer totalPurchases, Integer validPurchases, Integer expiringSoon) {
        this.totalPurchases = totalPurchases;
        this.validPurchases = validPurchases;
        this.expiringSoon = expiringSoon;
    }

    public UserPurchaseStatsVO(Integer totalPurchases, Integer validPurchases, Integer expiringSoon,
                              Integer expired, Long totalReadCount, Double averageProgress) {
        this.totalPurchases = totalPurchases;
        this.validPurchases = validPurchases;
        this.expiringSoon = expiringSoon;
        this.expired = expired;
        this.totalReadCount = totalReadCount;
        this.averageProgress = averageProgress;
    }
}