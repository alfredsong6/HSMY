package com.hsmy.service;

import com.hsmy.dto.SmsResult;

/**
 * 短信服务接口
 */
public interface SmsService {
    
    /**
     * 发送验证码短信
     * 
     * @param phoneNumber 手机号
     * @param code 验证码
     * @return 发送结果
     */
    SmsResult sendVerificationCode(String phoneNumber, String code);
    
    /**
     * 发送通知短信
     * 
     * @param phoneNumber 手机号
     * @param content 短信内容
     * @return 发送结果
     */
    SmsResult sendNotification(String phoneNumber, String content);
}