package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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

    /**
     * 购买月数（1-12月）
     */
    @NotNull(message = "购买月数不能为空")
    @Min(value = 1, message = "购买月数最少为1月")
    @Max(value = 12, message = "购买月数最多为12月")
    private Integer purchaseMonths;
}