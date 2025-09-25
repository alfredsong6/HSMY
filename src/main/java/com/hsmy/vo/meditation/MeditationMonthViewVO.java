package com.hsmy.vo.meditation;

import lombok.Data;

import java.util.Date;

@Data
public class MeditationMonthViewVO {
    private Date statDate;
    private Integer sessionCount;
    private Integer totalMinutes;
    private String mood;
    private String insight;
}
