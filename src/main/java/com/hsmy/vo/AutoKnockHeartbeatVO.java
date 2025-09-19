package com.hsmy.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 自动敲击心跳请求VO
 *
 * @author HSMY
 * @date 2025/09/19
 */
@Data
public class AutoKnockHeartbeatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @NotNull(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 当前累计敲击次数（由客户端统计）
     */
    @NotNull(message = "当前敲击次数不能为空")
    private Integer currentKnockCount;

    /**
     * 心跳时间（可选，服务端也会记录）
     */
    private LocalDateTime heartbeatTime;

    /**
     * 客户端状态（可选）
     * active-活跃, paused-暂停
     */
    private String clientStatus = "active";
}