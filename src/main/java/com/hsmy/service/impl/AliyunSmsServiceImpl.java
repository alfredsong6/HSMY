package com.hsmy.service.impl;

import com.hsmy.config.CommunicationProperties;
import com.hsmy.dto.SmsResult;
import com.hsmy.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 阿里云短信服务实现
 * 注意：此实现需要添加阿里云短信SDK依赖
 */
@Slf4j
@Service("aliyunSmsService")
@RequiredArgsConstructor
public class AliyunSmsServiceImpl implements SmsService {
    
    private final CommunicationProperties communicationProperties;
    
    // TODO: 需要引入阿里云短信SDK
    // import com.aliyuncs.DefaultAcsClient;
    // import com.aliyuncs.IAcsClient;
    // import com.aliyuncs.dysmsapi.model.v20170525.*;
    // import com.aliyuncs.profile.DefaultProfile;
    
    @Override
    public SmsResult sendVerificationCode(String phoneNumber, String code) {
        try {
            CommunicationProperties.AliyunSmsConfig smsConfig = communicationProperties.getSms().getAliyun();
            
            // 验证配置
            if (!isConfigValid(smsConfig)) {
                return SmsResult.failure("CONFIG_ERROR", "阿里云短信配置不完整");
            }
            
            // TODO: 使用阿里云短信SDK发送验证码
            /*
            // 设置鉴权参数，初始化客户端
            DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou", 
                smsConfig.getAccessKeyId(), 
                smsConfig.getAccessKeySecret()
            );
            IAcsClient client = new DefaultAcsClient(profile);
            
            // 组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(phoneNumber);
            request.setSignName(smsConfig.getSignName());
            request.setTemplateCode(smsConfig.getVerificationTemplateId());
            request.setTemplateParam("{\"code\":\"" + code + "\"}");
            
            // 发送短信
            SendSmsResponse response = client.getAcsResponse(request);
            
            if ("OK".equals(response.getCode())) {
                log.info("阿里云短信发送成功，手机号: {}, 验证码: {}, BizId: {}", phoneNumber, code, response.getBizId());
                return SmsResult.success(response.getBizId());
            } else {
                log.error("阿里云短信发送失败，手机号: {}, 错误码: {}, 错误信息: {}", phoneNumber, response.getCode(), response.getMessage());
                return SmsResult.failure(response.getCode(), response.getMessage());
            }
            */
            
            // 开发环境模拟发送成功
            log.info("【阿里云短信模拟】发送验证码到手机号: {}, 验证码: {}", phoneNumber, code);
            return SmsResult.success("MOCK_MESSAGE_ID_" + System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("阿里云短信发送异常，手机号: {}", phoneNumber, e);
            return SmsResult.failure("SYSTEM_ERROR", "短信发送异常：" + e.getMessage());
        }
    }
    
    @Override
    public SmsResult sendNotification(String phoneNumber, String content) {
        try {
            CommunicationProperties.AliyunSmsConfig smsConfig = communicationProperties.getSms().getAliyun();
            
            // 验证配置
            if (!isConfigValid(smsConfig)) {
                return SmsResult.failure("CONFIG_ERROR", "阿里云短信配置不完整");
            }
            
            // TODO: 使用阿里云短信SDK发送通知短信
            /*
            // 这里需要根据实际的通知模板来实现
            // 示例代码类似验证码发送，但使用不同的模板ID和参数
            */
            
            // 开发环境模拟发送成功
            log.info("【阿里云短信模拟】发送通知到手机号: {}, 内容: {}", phoneNumber, content);
            return SmsResult.success("MOCK_NOTIFICATION_ID_" + System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("阿里云通知短信发送异常，手机号: {}", phoneNumber, e);
            return SmsResult.failure("SYSTEM_ERROR", "通知短信发送异常：" + e.getMessage());
        }
    }
    
    /**
     * 验证阿里云短信配置是否完整
     */
    private boolean isConfigValid(CommunicationProperties.AliyunSmsConfig config) {
        return config != null 
                && config.getAccessKeyId() != null && !config.getAccessKeyId().trim().isEmpty()
                && config.getAccessKeySecret() != null && !config.getAccessKeySecret().trim().isEmpty()
                && config.getSignName() != null && !config.getSignName().trim().isEmpty()
                && config.getVerificationTemplateId() != null && !config.getVerificationTemplateId().trim().isEmpty();
    }
}