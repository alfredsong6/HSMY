package com.hsmy.vo.meditation;

import lombok.Data;

import java.util.Date;

@Data
public class MeditationMonthStatDTO {
    private Date statDate;
    private Integer totalMinutes;
    private Integer sessionCount;
    private String lastMood;
    private String lastInsight;
}
