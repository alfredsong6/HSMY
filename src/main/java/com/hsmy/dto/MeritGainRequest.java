package com.hsmy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功德增长请求参数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeritGainRequest {

    /**
     * 用户ID。
     */
    private Long userId;

    /**
     * 本次增加的功德值（必填，正数）。
     */
    private Long meritDelta;

    /**
     * 本次累计的敲击次数，可为空。
     */
    private Long knockDelta;

    /**
     * 是否更新最后敲击时间，默认 false。
     */
    @Builder.Default
    private boolean updateLastKnockTime = false;
}
