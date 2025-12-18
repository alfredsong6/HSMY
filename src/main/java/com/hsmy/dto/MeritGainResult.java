package com.hsmy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功德增长处理结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeritGainResult {

    /**
     * 处理后的累计功德。
     */
    private Long totalMeritAfter;

    /**
     * 处理后的当前等级。
     */
    private Integer levelAfter;

    /**
     * 是否发生升级。
     */
    private boolean levelUp;
}
