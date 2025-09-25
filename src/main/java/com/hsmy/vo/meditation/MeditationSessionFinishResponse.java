package com.hsmy.vo.meditation;

import lombok.Data;

import java.util.Date;

@Data
public class MeditationSessionFinishResponse {
    private String sessionId;
    private Integer actualDuration;
    private Integer todaySessionCount;
    private Integer todayTotalMinutes;
    private Integer totalSessionCount;
    private Integer totalMinutes;
    private Integer remainingCoins;
    private Date endTime;
}
