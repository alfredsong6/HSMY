package com.hsmy.vo.meditation;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class MeditationSessionReflectionVO {

    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    @Size(max = 50, message = "心情代码长度不能超过50")
    private String moodCode;

    @Size(max = 500, message = "感悟内容长度不能超过500")
    private String insightText;
}
