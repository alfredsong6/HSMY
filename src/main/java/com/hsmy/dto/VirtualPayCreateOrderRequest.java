package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 创建虚拟支付订单请求.
 */
@Data
public class VirtualPayCreateOrderRequest {

    @NotBlank(message = "packageId不能为空")
    private String packageId;
}
