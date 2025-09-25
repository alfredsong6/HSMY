package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新最后阅读位置请求DTO
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Data
public class UpdateLastReadingPositionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 典籍ID
     */
    @NotNull(message = "典籍ID不能为空")
    private Long scriptureId;

    /**
     * 最后阅读位置（字符位置）
     */
    @NotNull(message = "阅读位置不能为空")
    @Min(value = 0, message = "阅读位置不能小于0")
    private Integer lastReadingPosition;
}