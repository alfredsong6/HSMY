package com.hsmy.service;

import com.hsmy.entity.UserPeriodStats;
import com.hsmy.enums.PeriodType;

import java.util.Date;
import java.util.Map;

import java.time.LocalDate;

public interface UserPeriodStatsService {

    void recordKnock(Long userId, long knockCount, long meritGained, int comboCount, Date eventTime);

    Map<PeriodType, UserPeriodStats> loadCurrentPeriods(Long userId, Date referenceTime);

    void aggregatePeriods(LocalDate targetDate);
}
