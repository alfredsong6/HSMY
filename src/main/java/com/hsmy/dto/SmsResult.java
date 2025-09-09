package com.hsmy.dto;

import lombok.Data;

/**
 * 短信发送结果DTO
 */
@Data
public class SmsResult {
    
    /**
     * 是否发送成功
     */
    private boolean success;
    
    /**
     * 消息ID（用于追踪）
     */
    private String messageId;
    
    /**
     * 错误码
     */
    private String errorCode;
    
    /**
     * 错误消息
     */
    private String errorMessage;
    
    /**
     * 创建成功结果
     */
    public static SmsResult success(String messageId) {
        SmsResult result = new SmsResult();
        result.setSuccess(true);
        result.setMessageId(messageId);
        return result;
    }
    
    /**
     * 创建失败结果
     */
    public static SmsResult failure(String errorCode, String errorMessage) {
        SmsResult result = new SmsResult();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
}