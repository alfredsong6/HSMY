package com.hsmy.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 自动敲击停止请求VO
 *
 * @author HSMY
 * @date 2025/09/19
 */
@Data
public class AutoKnockStopVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @NotNull(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 敲击次数（由客户端统计）
     */
    @NotNull(message = "敲击次数不能为空")
    private Integer knockCount;

    /**
     * 实际持续时间（秒）
     * 客户端记录的实际敲击时间
     */
    private Integer actualDuration;
}