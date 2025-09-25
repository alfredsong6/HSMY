package com.hsmy.entity.meditation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 冥想订阅实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_meditation_subscription")
public class MeditationSubscription extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String planType;
    private Date startTime;
    private Date endTime;
    private String status;
    private Integer coinCost;
    private Long orderId;
}
