package com.hsmy.service;

import com.hsmy.dto.SmsResult;

/**
 * 邮件服务接口
 */
public interface EmailService {
    
    /**
     * 发送验证码邮件
     * 
     * @param email 邮箱地址
     * @param code 验证码
     * @return 发送结果
     */
    SmsResult sendVerificationCode(String email, String code);
    
    /**
     * 发送通知邮件
     * 
     * @param email 邮箱地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 发送结果
     */
    SmsResult sendNotification(String email, String subject, String content);
}