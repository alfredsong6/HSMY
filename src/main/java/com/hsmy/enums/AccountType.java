package com.hsmy.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 账号类型枚举.
 */
@Getter
public enum AccountType {
    PHONE("phone"),
    EMAIL("email");

    private final String code;

    AccountType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static AccountType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AccountType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的账号类型: " + code);
    }
}
