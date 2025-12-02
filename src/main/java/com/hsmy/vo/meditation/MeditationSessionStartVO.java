package com.hsmy.vo.meditation;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class MeditationSessionStartVO {

    @NotNull(message = "计划时长不能为空")
    @Min(value = 60, message = "计划时长不能少于60秒")
    private Integer plannedDuration;

    private Date startTime;

    private Integer withKnock; // 0/1

    private Integer knockFrequency; // 60-100
}
