package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新阅读进度请求DTO
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Data
public class UpdateReadingProgressRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 典籍ID
     */
    @NotNull(message = "典籍ID不能为空")
    private Long scriptureId;

    /**
     * 阅读进度百分比（0-100）
     */
    @NotNull(message = "阅读进度不能为空")
    @Min(value = 0, message = "阅读进度不能小于0")
    @Max(value = 100, message = "阅读进度不能大于100")
    private Double readingProgress;
}