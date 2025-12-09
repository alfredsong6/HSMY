package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 保存当前卷阅读进度请求
 */
@Data
public class SaveSectionProgressRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 该卷内字符偏移
     */
    @NotNull(message = "lastPosition不能为空")
    @Min(value = 0, message = "lastPosition不能小于0")
    private Integer lastPosition;

    /**
     * 当前卷进度 0~100
     */
    @NotNull(message = "sectionReadingProgress不能为空")
    @Min(value = 0, message = "sectionReadingProgress不能小于0")
    @Max(value = 100, message = "sectionReadingProgress不能大于100")
    private Double sectionReadingProgress;

    /**
     * 是否完成该卷
     */
    @NotNull(message = "isCompleted不能为空")
    private Integer isCompleted;

    /**
     * 本次阅读耗时（秒，可选累加）
     */
    private Integer spendSeconds;
}
