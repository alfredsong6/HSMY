package com.hsmy.service.impl;

import com.hsmy.entity.DimTime;
import com.hsmy.mapper.DimTimeMapper;
import com.hsmy.service.TimeDimensionService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TimeDimensionServiceImpl implements TimeDimensionService {

    private final DimTimeMapper dimTimeMapper;

    @Override
    public DimTime ensureDate(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        Date sqlDate = Date.valueOf(date);
        DimTime existing = dimTimeMapper.selectByDate(sqlDate);
        if (existing != null) {
            return existing;
        }

        DimTime record = buildRecord(date);
        try {
            dimTimeMapper.insert(record);
            return record;
        } catch (DuplicateKeyException ex) {
            return dimTimeMapper.selectByDate(sqlDate);
        }
    }

    private DimTime buildRecord(LocalDate date) {
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDate monthStart = date.withDayOfMonth(1);
        LocalDate monthEnd = date.withDayOfMonth(date.lengthOfMonth());
        LocalDate yearStart = date.withDayOfYear(1);

        DimTime record = new DimTime();
        record.setId(IdGenerator.nextId());
        record.setDateValue(Date.valueOf(date));
        int isoWeek = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        record.setIsoWeek(String.format("%d-W%02d", date.get(IsoFields.WEEK_BASED_YEAR), isoWeek));
        record.setMonthValue(String.format("%d-%02d", date.getYear(), date.getMonthValue()));
        record.setYearValue(String.valueOf(date.getYear()));
        record.setWeekStart(Date.valueOf(weekStart));
        record.setWeekEnd(Date.valueOf(weekEnd));
        record.setMonthStart(Date.valueOf(monthStart));
        record.setMonthEnd(Date.valueOf(monthEnd));
        record.setQuarter((date.getMonthValue() - 1) / 3 + 1);
        record.setIsWeekend(isWeekend(date) ? 1 : 0);
        java.util.Date now = new java.util.Date();
        record.setCreateTime(now);
        record.setUpdateTime(now);
        return record;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }
}
