package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * VIP purchase request.
 */
@Data
public class PurchaseVipRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "VIP package id is required")
    private Long packageId;

    private String paymentMethod;
}
