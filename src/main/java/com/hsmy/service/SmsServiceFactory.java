package com.hsmy.service;

import com.hsmy.config.CommunicationProperties;
import com.hsmy.enums.SmsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 短信服务工厂
 */
@Service
@RequiredArgsConstructor
public class SmsServiceFactory {
    
    private final ApplicationContext applicationContext;
    private final CommunicationProperties communicationProperties;
    
    /**
     * 获取当前配置的短信服务
     */
    public SmsService getSmsService() {
        if (!communicationProperties.getSms().getEnabled()) {
            throw new RuntimeException("短信功能未启用");
        }
        
        SmsProvider provider = communicationProperties.getSms().getProvider();
        
        switch (provider) {
            case ALIYUN:
                return applicationContext.getBean("aliyunSmsService", SmsService.class);
            case TENCENT:
                return applicationContext.getBean("tencentSmsService", SmsService.class);
            default:
                throw new IllegalArgumentException("Unsupported SMS provider: " + provider);
        }
    }
}