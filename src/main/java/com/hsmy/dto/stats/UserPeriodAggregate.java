package com.hsmy.dto.stats;

import lombok.Data;

/**
 * Aggregated view of user statistics over a period.
 */
@Data
public class UserPeriodAggregate {

    private Long userId;

    private Long knockCount;

    private Long meritGained;

    private Integer maxCombo;
}
