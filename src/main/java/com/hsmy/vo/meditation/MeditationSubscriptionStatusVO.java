package com.hsmy.vo.meditation;

import lombok.Data;

import java.util.Date;

@Data
public class MeditationSubscriptionStatusVO {
    private boolean active;
    private String planType;
    private Date startTime;
    private Date endTime;
    private long remainingSeconds;
    private Integer remainingCoins;
}
