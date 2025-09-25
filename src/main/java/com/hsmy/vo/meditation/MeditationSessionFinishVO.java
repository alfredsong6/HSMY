package com.hsmy.vo.meditation;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MeditationSessionFinishVO {

    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    @NotNull(message = "实际时长不能为空")
    @Min(value = 1, message = "实际时长必须大于0")
    private Integer actualDuration;

    private Integer withKnock;
    private Integer knockFrequency;

    private String moodCode;
    private String insightText;
}
