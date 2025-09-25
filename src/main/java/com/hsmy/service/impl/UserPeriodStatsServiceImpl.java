package com.hsmy.service.impl;

import com.hsmy.entity.DimTime;
import com.hsmy.entity.UserPeriodStats;
import com.hsmy.dto.stats.UserPeriodAggregate;
import com.hsmy.enums.PeriodType;
import com.hsmy.mapper.UserPeriodStatsMapper;
import com.hsmy.service.TimeDimensionService;
import com.hsmy.service.UserPeriodStatsService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserPeriodStatsServiceImpl implements UserPeriodStatsService {

    private static final String SYSTEM_USER = "system";

    private final UserPeriodStatsMapper userPeriodStatsMapper;
    private final TimeDimensionService timeDimensionService;

    @Override
    public void recordKnock(Long userId, long knockCount, long meritGained, int comboCount, java.util.Date eventTime) {
        LocalDate eventDate = toLocalDate(eventTime);

        DimTime day = timeDimensionService.ensureDate(eventDate);

        upsert(userId, day.getId(), PeriodType.DAY, knockCount, meritGained, comboCount);
    }

    @Override
    public Map<PeriodType, UserPeriodStats> loadCurrentPeriods(Long userId, java.util.Date referenceTime) {
        LocalDate refDate = toLocalDate(referenceTime);

        DimTime day = timeDimensionService.ensureDate(refDate);
        DimTime week = timeDimensionService.ensureDate(refDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        DimTime month = timeDimensionService.ensureDate(refDate.withDayOfMonth(1));
        DimTime year = timeDimensionService.ensureDate(refDate.withDayOfYear(1));

        Map<PeriodType, UserPeriodStats> result = new EnumMap<>(PeriodType.class);
        result.put(PeriodType.DAY, userPeriodStatsMapper.selectByUserAndPeriod(userId, PeriodType.DAY.name(), day.getId()));
        result.put(PeriodType.WEEK, userPeriodStatsMapper.selectByUserAndPeriod(userId, PeriodType.WEEK.name(), week.getId()));
        result.put(PeriodType.MONTH, userPeriodStatsMapper.selectByUserAndPeriod(userId, PeriodType.MONTH.name(), month.getId()));
        result.put(PeriodType.YEAR, userPeriodStatsMapper.selectByUserAndPeriod(userId, PeriodType.YEAR.name(), year.getId()));
        return result;
    }

    @Override
    public void aggregatePeriods(LocalDate targetDate) {
        LocalDate today = LocalDate.now();
        LocalDate effectiveDate = targetDate != null ? targetDate : today.minusDays(1);

        if (effectiveDate.isAfter(today)) {
            effectiveDate = today;
        }

        aggregatePeriod(PeriodType.WEEK, effectiveDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), effectiveDate);
        aggregatePeriod(PeriodType.MONTH, effectiveDate.withDayOfMonth(1), effectiveDate);
        aggregatePeriod(PeriodType.YEAR, effectiveDate.withDayOfYear(1), effectiveDate);
    }

    private void aggregatePeriod(PeriodType periodType, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return;
        }

        List<Long> dayTimeIds = collectDayTimeIds(startDate, endDate);
        if (dayTimeIds.isEmpty()) {
            return;
        }

        List<UserPeriodAggregate> aggregates = userPeriodStatsMapper.aggregateDailyStatsByTimeIds(dayTimeIds);
        if (aggregates == null || aggregates.isEmpty()) {
            return;
        }

        Long periodTimeId = timeDimensionService.ensureDate(startDate).getId();
        for (UserPeriodAggregate aggregate : aggregates) {
            if (aggregate.getUserId() == null) {
                continue;
            }
            saveAggregate(periodType, periodTimeId, aggregate);
        }
    }

    private void saveAggregate(PeriodType periodType, Long periodTimeId, UserPeriodAggregate aggregate) {
        UserPeriodStats stats = new UserPeriodStats();
        stats.setId(IdGenerator.nextId());
        stats.setUserId(aggregate.getUserId());
        stats.setTimeId(periodTimeId);
        stats.setPeriodType(periodType.name());
        stats.setKnockCount(aggregate.getKnockCount() != null ? aggregate.getKnockCount() : 0L);
        stats.setMeritGained(aggregate.getMeritGained() != null ? aggregate.getMeritGained() : 0L);
        stats.setMaxCombo(aggregate.getMaxCombo() != null ? aggregate.getMaxCombo() : 0);
        stats.setCreateBy(SYSTEM_USER);
        stats.setUpdateBy(SYSTEM_USER);
        stats.setIsDeleted(0);
        userPeriodStatsMapper.replacePeriodStats(stats);
    }

    private List<Long> collectDayTimeIds(LocalDate startDate, LocalDate endDate) {
        List<Long> ids = new ArrayList<>();
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            DimTime day = timeDimensionService.ensureDate(cursor);
            ids.add(day.getId());
            cursor = cursor.plusDays(1);
        }
        return ids;
    }

    private void upsert(Long userId, Long timeId, PeriodType periodType, long knockCount, long meritGained, int comboCount) {
        UserPeriodStats stats = new UserPeriodStats();
        stats.setId(IdGenerator.nextId());
        stats.setUserId(userId);
        stats.setTimeId(timeId);
        stats.setPeriodType(periodType.name());
        stats.setKnockCount(knockCount);
        stats.setMeritGained(meritGained);
        stats.setMaxCombo(Math.max(comboCount, 0));
        stats.setCreateBy(SYSTEM_USER);
        stats.setUpdateBy(SYSTEM_USER);
        stats.setIsDeleted(0);
        userPeriodStatsMapper.upsertPeriodStats(stats);
    }

    private LocalDate toLocalDate(java.util.Date date) {
        if (date == null) {
            return LocalDate.now();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
