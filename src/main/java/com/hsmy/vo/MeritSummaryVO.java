package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 功德汇总返回对象.
 */
@Data
public class MeritSummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalMerit;
    private Long todayMerit;
    private Long weeklyMerit;
    private Long monthlyMerit;
    private Integer meritCoins;
    private MeritStatsVO userStats;
    private Date statDate;
    private Long dailyMerit;
}
