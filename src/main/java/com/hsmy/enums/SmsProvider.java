package com.hsmy.enums;

/**
 * 短信服务提供商枚举
 */
public enum SmsProvider {
    
    /**
     * 阿里云短信
     */
    ALIYUN("aliyun"),
    
    /**
     * 腾讯云短信
     */
    TENCENT("tencent");
    
    private final String code;
    
    SmsProvider(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    public static SmsProvider fromCode(String code) {
        for (SmsProvider provider : values()) {
            if (provider.getCode().equals(code)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unsupported SMS provider: " + code);
    }
}