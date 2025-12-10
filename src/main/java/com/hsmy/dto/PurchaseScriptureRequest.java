package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购买典籍请求DTO
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Data
public class PurchaseScriptureRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 典籍ID
     */
    @NotNull(message = "典籍ID不能为空")
    private Long scriptureId;

    private BigDecimal amount;
}