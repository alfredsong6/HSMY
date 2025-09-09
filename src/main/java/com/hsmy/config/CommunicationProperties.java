package com.hsmy.config;

import com.hsmy.enums.SmsProvider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 通信服务配置（短信、邮件）
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "communication")
public class CommunicationProperties {
    
    /**
     * 短信配置
     */
    private SmsConfig sms = new SmsConfig();
    
    /**
     * 邮件配置
     */
    private EmailConfig email = new EmailConfig();
    
    @Data
    public static class SmsConfig {
        /**
         * 是否启用短信功能
         */
        private Boolean enabled = true;
        
        /**
         * 短信服务提供商
         */
        private SmsProvider provider = SmsProvider.ALIYUN;
        
        /**
         * 阿里云短信配置
         */
        private AliyunSmsConfig aliyun = new AliyunSmsConfig();
        
        /**
         * 腾讯云短信配置
         */
        private TencentSmsConfig tencent = new TencentSmsConfig();
    }
    
    @Data
    public static class EmailConfig {
        /**
         * 是否启用邮件功能
         */
        private Boolean enabled = true;
        
        /**
         * SMTP服务器地址
         */
        private String host = "smtp.163.com";
        
        /**
         * SMTP端口
         */
        private Integer port = 25;
        
        /**
         * 发送方邮箱
         */
        private String from;
        
        /**
         * 发送方邮箱密码/授权码
         */
        private String password;
        
        /**
         * 发送方名称
         */
        private String fromName = "敲敲木鱼";
        
        /**
         * 是否启用SSL
         */
        private Boolean ssl = false;
        
        /**
         * 是否启用TLS
         */
        private Boolean tls = true;
    }
    
    @Data
    public static class AliyunSmsConfig {
        /**
         * AccessKey ID
         */
        private String accessKeyId;
        
        /**
         * AccessKey Secret
         */
        private String accessKeySecret;
        
        /**
         * 短信签名
         */
        private String signName;
        
        /**
         * 验证码模板ID
         */
        private String verificationTemplateId;
        
        /**
         * 地域节点
         */
        private String endpoint = "dysmsapi.aliyuncs.com";
    }
    
    @Data
    public static class TencentSmsConfig {
        /**
         * SecretId
         */
        private String secretId;
        
        /**
         * SecretKey
         */
        private String secretKey;
        
        /**
         * 短信应用ID
         */
        private String appId;
        
        /**
         * 短信签名
         */
        private String signName;
        
        /**
         * 验证码模板ID
         */
        private String verificationTemplateId;
        
        /**
         * 地域
         */
        private String region = "ap-beijing";
    }
}