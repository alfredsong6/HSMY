package com.hsmy.vo.v1_1;

import com.hsmy.vo.ExchangeVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 功德兑换VO V1.1版本
 * 
 * 相比v1.0版本新增：
 * 1. 兑换类型选择
 * 2. 是否使用优惠券
 * 3. 兑换备注
 * 
 * @author HSMY
 * @date 2025/09/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExchangeVOV1_1 extends ExchangeVO {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 兑换类型 1:普通兑换 2:折扣兑换 3:VIP兑换
     */
    @Min(value = 1, message = "兑换类型最小值为1")
    @Max(value = 3, message = "兑换类型最大值为3")
    private Integer exchangeType = 1;
    
    /**
     * 是否使用优惠券
     */
    private Boolean useCoupon = false;
    
    /**
     * 优惠券ID
     */
    private Long couponId;
    
    /**
     * 兑换备注
     */
    private String remark;
    
    /**
     * 是否立即生效
     */
    private Boolean immediate = true;
}