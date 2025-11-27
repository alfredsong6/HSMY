package com.hsmy.domain.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 充值档位规则定义.
 */
@Data
public class ActivityRule {

    /**
     * 充值金额.
     */
    private BigDecimal amount;

    /**
     * 充值赠送功德币数量.
     */
    private BigDecimal gift;

    /**
     * 获得功德币数量.
     */
    private BigDecimal give;

    /**
     * 总获得功德币（充值 + 赠送）.
     */
    @JsonProperty("crash-top-up")
    private BigDecimal crashTopUp;
}

