package com.hsmy.vo;

import lombok.Data;

/**
 * 自动能力状态查询结果.
 */
@Data
public class AutoAbilityStatusVO {
    private boolean enable;
    private Integer usageMode;
    private Integer remainingUses;
    private Integer usageStatus;
    private Long userItemId;
    private Long itemId;
}
