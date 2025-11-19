package com.hsmy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户道具状态.
 */
@Getter
@AllArgsConstructor
public enum UserItemUsageStatusEnum {

    INACTIVE(0),
    ACTIVE(1),
    USED(2),
    EXPIRED(3);

    private final int code;
}

