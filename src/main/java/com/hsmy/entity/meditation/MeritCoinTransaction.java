package com.hsmy.entity.meditation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 功德币流水实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_merit_coin_transaction")
public class MeritCoinTransaction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String bizType;
    private Long bizId;
    private Integer changeAmount;
    private Integer balanceAfter;
    private String remark;
}
