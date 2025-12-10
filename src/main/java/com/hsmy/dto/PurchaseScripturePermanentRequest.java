package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 买断典籍购买请求DTO
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Data
public class PurchaseScripturePermanentRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 典籍ID
     */
    @NotNull(message = "典籍ID不能为空")
    private Long scriptureId;

    /**
     * 购买类型：monthly-按月订阅，permanent-买断
     */
    @NotNull(message = "购买类型不能为空")
    private String purchaseType;

    private BigDecimal amount;

}