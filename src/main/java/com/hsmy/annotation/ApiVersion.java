package com.hsmy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API版本注解
 * 用于标识接口支持的版本
 * 
 * @author HSMY
 * @date 2025/09/10
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    
    /**
     * 版本号，格式：v1, v2, v1.1, v2.0 等
     */
    String value() default "v1";
    
    /**
     * 是否已废弃
     */
    boolean deprecated() default false;
    
    /**
     * 废弃说明
     */
    String deprecatedMessage() default "";
}