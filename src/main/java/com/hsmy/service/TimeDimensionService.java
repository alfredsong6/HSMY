package com.hsmy.service;

import com.hsmy.entity.DimTime;

import java.time.LocalDate;

public interface TimeDimensionService {

    DimTime ensureDate(LocalDate date);
}
