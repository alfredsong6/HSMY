package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Aggregated user statistics (lifetime counters only).
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_stats")
public class UserStats extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * User identifier.
     */
    private Long userId;

    /**
     * Lifetime merit value accumulated.
     */
    private Long totalMerit;

    /**
     * Remaining merit coins balance.
     */
    private Long meritCoins;

    /**
     * Lifetime knock count.
     */
    private Long totalKnocks;

    /**
     * Consecutive login days.
     */
    private Integer consecutiveDays;

    /**
     * Total login days.
     */
    private Integer totalLoginDays;

    /**
     * Current merit level.
     */
    private Integer currentLevel;

    /**
     * Highest combo ever achieved.
     */
    private Integer maxCombo;

    /**
     * Last knock timestamp.
     */
    private Date lastKnockTime;

    /**
     * Last login date.
     */
    private Date lastLoginDate;

    public Integer getContinuousSignDays() {
        return consecutiveDays;
    }
}
