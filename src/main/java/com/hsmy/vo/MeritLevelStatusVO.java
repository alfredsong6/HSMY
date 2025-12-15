package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Merit level with user-specific progress status.
 */
@Data
public class MeritLevelStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer level;
    private String levelName;
    private Long minMerit;
    private Long maxMerit;
    private String levelBenefits;
    private String iconUrl;
    private BigDecimal bonusRate;
    private Integer dailyExchangeLimit;

    /**
     * Level completion status for the user: COMPLETED, IN_PROGRESS, NOT_STARTED.
     */
    private String status;

    /**
     * Merit still needed to reach the next level when in progress; otherwise null.
     */
    private Long remainingMerit;

    /**
     * Whether this is the user's current level.
     */
    private Boolean current;
}
