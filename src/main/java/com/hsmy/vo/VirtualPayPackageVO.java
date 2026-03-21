package com.hsmy.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 虚拟支付档位视图.
 */
@Data
public class VirtualPayPackageVO {

    private String packageId;
    private String title;
    private String description;
    private BigDecimal amount;
    private Integer coinCount;
    private Integer bonusCoinCount;
    private Boolean enabled;
    private Integer sort;
}
