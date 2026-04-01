package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Request for creating a virtual payment order.
 */
@Data
public class VirtualPayCreateOrderRequest {

    @NotBlank(message = "packageId must not be blank")
    private String packageId;

    private String code;
}
