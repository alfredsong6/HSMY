package com.hsmy.service.impl;

import com.hsmy.config.CommunicationProperties;
import com.hsmy.dto.SmsResult;
import com.hsmy.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 腾讯云短信服务实现
 * 注意：此实现需要添加腾讯云短信SDK依赖
 */
@Slf4j
@Service("tencentSmsService")
@RequiredArgsConstructor
public class TencentSmsServiceImpl implements SmsService {
    
    private final CommunicationProperties communicationProperties;
    
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    // TODO: 需要引入腾讯云短信SDK
    // import com.tencentcloudapi.common.Credential;
    // import com.tencentcloudapi.common.profile.ClientProfile;
    // import com.tencentcloudapi.common.profile.HttpProfile;
    // import com.tencentcloudapi.sms.v20210111.SmsClient;
    // import com.tencentcloudapi.sms.v20210111.models.*;
    
    @Override
    public SmsResult sendVerificationCode(String phoneNumber, String code) {
        try {
            // 开发环境直接输出到控制台
            if ("dev".equals(activeProfile)) {
                log.info("【开发环境】发送验证码到手机号: {}, 验证码: {}", phoneNumber, code);
                return SmsResult.success("DEV_MESSAGE_ID_" + System.currentTimeMillis());
            }
            
            CommunicationProperties.TencentSmsConfig smsConfig = communicationProperties.getSms().getTencent();
            
            // 验证配置
            if (!isConfigValid(smsConfig)) {
                return SmsResult.failure("CONFIG_ERROR", "腾讯云短信配置不完整");
            }
            
            // TODO: 使用腾讯云短信SDK发送验证码
            /*
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey
            Credential cred = new Credential(smsConfig.getSecretId(), smsConfig.getSecretKey());
            
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            
            // 实例化要请求产品的client对象
            SmsClient client = new SmsClient(cred, smsConfig.getRegion(), clientProfile);
            
            // 实例化一个请求对象
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet = {"+86" + phoneNumber};
            req.setPhoneNumberSet(phoneNumberSet);
            req.setSmsSdkAppId(smsConfig.getAppId());
            req.setSignName(smsConfig.getSignName());
            req.setTemplateId(smsConfig.getVerificationTemplateId());
            
            // 模板参数: 验证码
            String[] templateParamSet = {code};
            req.setTemplateParamSet(templateParamSet);
            
            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            
            SendStatus[] statusSet = resp.getSendStatusSet();
            if (statusSet.length > 0) {
                SendStatus status = statusSet[0];
                if ("Ok".equals(status.getCode())) {
                    log.info("腾讯云短信发送成功，手机号: {}, 验证码: {}, SerialNo: {}", phoneNumber, code, status.getSerialNo());
                    return SmsResult.success(status.getSerialNo());
                } else {
                    log.error("腾讯云短信发送失败，手机号: {}, 错误码: {}, 错误信息: {}", phoneNumber, status.getCode(), status.getMessage());
                    return SmsResult.failure(status.getCode(), status.getMessage());
                }
            } else {
                log.error("腾讯云短信发送失败，手机号: {}, 无返回状态", phoneNumber);
                return SmsResult.failure("NO_STATUS", "无返回状态");
            }
            */
            
            // 非开发环境模拟发送成功（实际应该调用腾讯云SDK）
            log.info("【腾讯云短信模拟】发送验证码到手机号: {}, 验证码: {}", phoneNumber, code);
            return SmsResult.success("MOCK_SERIAL_NO_" + System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("腾讯云短信发送异常，手机号: {}", phoneNumber, e);
            return SmsResult.failure("SYSTEM_ERROR", "短信发送异常：" + e.getMessage());
        }
    }
    
    @Override
    public SmsResult sendNotification(String phoneNumber, String content) {
        try {
            CommunicationProperties.TencentSmsConfig smsConfig = communicationProperties.getSms().getTencent();
            
            // 验证配置
            if (!isConfigValid(smsConfig)) {
                return SmsResult.failure("CONFIG_ERROR", "腾讯云短信配置不完整");
            }
            
            // TODO: 使用腾讯云短信SDK发送通知短信
            /*
            // 这里需要根据实际的通知模板来实现
            // 示例代码类似验证码发送，但使用不同的模板ID和参数
            */
            
            // 开发环境模拟发送成功
            log.info("【腾讯云短信模拟】发送通知到手机号: {}, 内容: {}", phoneNumber, content);
            return SmsResult.success("MOCK_NOTIFICATION_SERIAL_" + System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("腾讯云通知短信发送异常，手机号: {}", phoneNumber, e);
            return SmsResult.failure("SYSTEM_ERROR", "通知短信发送异常：" + e.getMessage());
        }
    }
    
    /**
     * 验证腾讯云短信配置是否完整
     */
    private boolean isConfigValid(CommunicationProperties.TencentSmsConfig config) {
        return config != null 
                && config.getSecretId() != null && !config.getSecretId().trim().isEmpty()
                && config.getSecretKey() != null && !config.getSecretKey().trim().isEmpty()
                && config.getAppId() != null && !config.getAppId().trim().isEmpty()
                && config.getSignName() != null && !config.getSignName().trim().isEmpty()
                && config.getVerificationTemplateId() != null && !config.getVerificationTemplateId().trim().isEmpty();
    }
}