package com.hsmy.dto;

import lombok.Data;

/**
 * 发送验证码结果
 * 
 * @author HSMY
 * @date 2025/09/27
 */
@Data
public class SendCodeResult {
    
    /**
     * 是否发送成功
     */
    private boolean success;
    
    /**
     * 验证码（仅开发环境返回）
     */
    private String code;
    
    /**
     * 消息
     */
    private String message;
    
    public SendCodeResult() {
    }
    
    public SendCodeResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public SendCodeResult(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }
    
    public static SendCodeResult success(String message) {
        return new SendCodeResult(true, message);
    }
    
    public static SendCodeResult success(String code, String message) {
        return new SendCodeResult(true, code, message);
    }
    
    public static SendCodeResult error(String message) {
        return new SendCodeResult(false, message);
    }
}