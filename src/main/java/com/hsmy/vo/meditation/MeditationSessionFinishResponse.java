package com.hsmy.vo.meditation;

import lombok.Data;

import java.util.Date;

@Data
public class MeditationSessionFinishResponse {
    private String sessionId;
    private Integer actualDuration;
    /** 冥想获得的功德值 */
    private Integer meritGained;
    private Integer todaySessionCount;
    private Integer todayTotalMinutes;
    private Integer totalSessionCount;
    private Integer totalMinutes;
    private Integer remainingCoins;
    private Date endTime;
}
