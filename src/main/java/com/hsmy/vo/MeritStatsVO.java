package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户功德统计详情.
 */
@Data
public class MeritStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long totalMerit;
    private Long totalKnocks;
    private Integer maxCombo;
    private Long meritCoins;
    private Integer currentLevel;

    private Long todayMerit;
    private Long todayKnocks;
    private Long weeklyMerit;
    private Long monthlyMerit;
}
