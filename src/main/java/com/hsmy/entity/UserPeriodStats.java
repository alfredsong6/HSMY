package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Period-based knock and merit statistics for a user.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_period_stats")
public class UserPeriodStats extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long timeId;

    /**
     * Aggregation window type: DAY, WEEK, MONTH, YEAR.
     */
    private String periodType;

    /**
     * Knock count within the aggregation window.
     */
    private Long knockCount;

    /**
     * Merit gained within the aggregation window.
     */
    private Long meritGained;

    /**
     * Maximum combo within the aggregation window.
     */
    private Integer maxCombo;
}
