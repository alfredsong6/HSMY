package com.hsmy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户道具来源.
 */
@Getter
@AllArgsConstructor
public enum UserItemSourceEnum {

    SHOP(1, "商城购买"),
    EVENT(2, "活动奖励"),
    TASK(3, "任务奖励");

    private final int code;
    private final String desc;
}

