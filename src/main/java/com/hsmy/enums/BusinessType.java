package com.hsmy.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 业务类型枚举.
 */
@Getter
public enum BusinessType {
    REGISTER("register"),
    LOGIN("login"),
    RESET_PASSWORD("reset_password");

    private final String code;

    BusinessType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static BusinessType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (BusinessType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的业务类型: " + code);
    }
}
