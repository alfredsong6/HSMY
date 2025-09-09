package com.hsmy.enums;

/**
 * 文件存储类型枚举
 */
public enum StorageType {
    
    /**
     * 本地存储
     */
    LOCAL("local"),
    
    /**
     * 腾讯云COS
     */
    TENCENT_COS("tencent-cos"),
    
    /**
     * 阿里云OSS
     */
    ALIYUN_OSS("aliyun-oss");
    
    private final String code;
    
    StorageType(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    public static StorageType fromCode(String code) {
        for (StorageType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported storage type: " + code);
    }
}