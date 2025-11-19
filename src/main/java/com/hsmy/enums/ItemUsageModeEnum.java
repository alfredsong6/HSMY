package com.hsmy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 道具使用模式枚举.
 */
@Getter
@AllArgsConstructor
public enum ItemUsageModeEnum {

    PERMANENT(0),          // 永久拥有
    TIMED_REPEAT(1),       // 限时可重复
    CONSUMABLE(2);         // 一次性/限次数

    private final int code;

    public static ItemUsageModeEnum from(Integer code) {
        if (code == null) {
            return PERMANENT;
        }
        for (ItemUsageModeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return PERMANENT;
    }

    public boolean eq(Integer code) {
        return code != null && code == this.code;
    }
}

