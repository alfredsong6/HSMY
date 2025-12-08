package com.hsmy.vo.meditation;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class MeditationSessionShareVO {

    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    @Min(value = 0, message = "分享标记只能为0或1")
    @Max(value = 1, message = "分享标记只能为0或1")
    private Integer shareFlag;

    //@Size(max = 20, message = "分享渠道长度不能超过20")
    private String shareTarget;
}
