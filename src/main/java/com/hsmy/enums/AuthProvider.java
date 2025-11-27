package com.hsmy.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 身份提供方枚举.
 */
@Getter
public enum AuthProvider {
    WECHAT_MINI("wechat_mini"),
    WECHAT_APP("wechat_app"),
    SMS("sms");

    private final String code;

    AuthProvider(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static AuthProvider fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AuthProvider provider : values()) {
            if (provider.code.equalsIgnoreCase(code)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("不支持的身份提供方: " + code);
    }
}
