package com.hsmy.vo.meditation;

import lombok.Data;

@Data
public class MeditationStatsSummaryVO {
    private Integer todayCount;
    private Integer todayMinutes;
    private Integer totalCount;
    private Integer totalMinutes;
}
