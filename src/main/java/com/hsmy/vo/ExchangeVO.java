package com.hsmy.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 功德兑换VO
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
public class ExchangeVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 兑换功德值
     */
    @NotNull(message = "兑换功德值不能为空")
    @Min(value = 1000, message = "最少兑换1000功德值")
    private Long meritAmount;
}