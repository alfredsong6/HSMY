package com.hsmy.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 功德币业务类型枚举.
 */
@Getter
public enum MeritBizType {
    RECHARGE_PURCHASE("RECHARGE_PURCHASE"),
    RECHARGE_BONUS("RECHARGE_BONUS"),
    ITEM_PURCHASE("ITEM_PURCHASE"),
    SCRIPTURE_SUBSCRIBE("SCRIPTURE_SUBSCRIBE"),
    SCRIPTURE_PERMANENT("SCRIPTURE_PERMANENT"),
    SCRIPTURE_RENEW("SCRIPTURE_RENEW"),
    VIP_PURCHASE("VIP_PURCHASE");

    private final String code;

    MeritBizType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static MeritBizType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (MeritBizType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的功德币业务类型: " + code);
    }
}
