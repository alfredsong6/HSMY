package com.hsmy.vo.meditation;

import lombok.Data;

import java.util.Date;

@Data
public class MeditationSessionStartResponse {
    private String sessionId;
    private Date startTime;
    private Integer plannedDuration;
    private Integer withKnock;
    private Integer knockFrequency;
    private Integer remainingCoins;
}
