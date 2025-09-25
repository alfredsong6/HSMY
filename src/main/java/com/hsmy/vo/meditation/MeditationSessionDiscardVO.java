package com.hsmy.vo.meditation;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MeditationSessionDiscardVO {

    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
}
