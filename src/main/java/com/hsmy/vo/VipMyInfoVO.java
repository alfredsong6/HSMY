package com.hsmy.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hsmy.entity.VipBenefits;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Current user vip info.
 */
@Data
public class VipMyInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer vipLevel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date vipExpireTime;

    private Boolean active;

    private Long packageId;
    private String packageName;
    private Integer packageType;

    private VipBenefits benefits;
}
