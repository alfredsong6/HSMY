package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新分段阅读进度请求
 */
@Data
public class UpdateSectionProgressRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 典籍ID
     */
    @NotNull(message = "典籍ID不能为空")
    private Long scriptureId;

    /**
     * 分段ID
     */
    @NotNull(message = "分段ID不能为空")
    private Long sectionId;

    /**
     * 分段进度百分比（0-100）
     */
    @NotNull(message = "分段进度不能为空")
    @Min(value = 0, message = "分段进度不能小于0")
    @Max(value = 100, message = "分段进度不能大于100")
    private Double sectionProgress;

    /**
     * 整本进度百分比（0-100）
     */
    @NotNull(message = "整本进度不能为空")
    @Min(value = 0, message = "整本进度不能小于0")
    @Max(value = 100, message = "整本进度不能大于100")
    private Double totalProgress;

    /**
     * 分段内最后阅读位置
     */
    @NotNull(message = "阅读位置不能为空")
    @Min(value = 0, message = "阅读位置不能小于0")
    private Integer lastPosition;

    /**
     * 本次阅读耗时（秒，可选）
     */
    private Integer spendSeconds;
}
