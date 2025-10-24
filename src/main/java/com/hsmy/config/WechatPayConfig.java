package com.hsmy.config;

import cn.hutool.core.util.StrUtil;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.util.PemUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

/**
 * 微信支付 SDK 配置
 */
@Configuration
@RequiredArgsConstructor
public class WechatPayConfig {

    private final WechatPayProperties properties;
    private final ResourceLoader resourceLoader;

    /**
     * 构建微信支付 SDK 配置
     */
    @Bean
    @ConditionalOnProperty(prefix = "wechatpay", name = "enabled", havingValue = "true")
    public Config wechatPaySdkConfig() {
        validateRequiredProperties();

        RSAAutoCertificateConfig.Builder builder = new RSAAutoCertificateConfig.Builder()
                .merchantId(properties.getMchId())
                .merchantSerialNumber(properties.getSerialNo())
                .apiV3Key(properties.getApiV3Key());

        if (StrUtil.isNotBlank(properties.getPrivateKey())) {
            builder.privateKey(loadPrivateKeyFromContent(properties.getPrivateKey()));
        } else {
            builder.privateKey(loadPrivateKeyFromPath(properties.getPrivateKeyPath()));
        }

        return builder.build();
    }

    /**
     * 解析通知所需的工具
     */
    @Bean
    @ConditionalOnBean(Config.class)
    public NotificationParser wechatPayNotificationParser(Config config) {
        if (config instanceof NotificationConfig) {
            return new NotificationParser((NotificationConfig) config);
        }
        throw new IllegalStateException("当前微信支付配置不支持通知解析");
    }

    private void validateRequiredProperties() {
        Assert.hasText(properties.getAppId(), "wechatpay.app-id 未配置");
        Assert.hasText(properties.getMchId(), "wechatpay.mch-id 未配置");
        Assert.hasText(properties.getSerialNo(), "wechatpay.serial-no 未配置");
        Assert.hasText(properties.getApiV3Key(), "wechatpay.api-v3-key 未配置");
        if (StrUtil.isBlank(properties.getPrivateKey()) && StrUtil.isBlank(properties.getPrivateKeyPath())) {
            throw new IllegalStateException("需配置 wechatpay.private-key 或 wechatpay.private-key-path");
        }
    }

    private PrivateKey loadPrivateKeyFromContent(String privateKeyContent) {
        return PemUtil.loadPrivateKeyFromString(privateKeyContent);
    }

    private PrivateKey loadPrivateKeyFromPath(String location) {
        Assert.hasText(location, "wechatpay.private-key-path 未配置");
        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists()) {
            resource = resourceLoader.getResource("file:" + location);
        }
        if (resource.exists()) {
            try (InputStream inputStream = resource.getInputStream()) {
                String content = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                return PemUtil.loadPrivateKeyFromString(content);
            } catch (IOException e) {
                throw new IllegalStateException("读取微信商户私钥失败: " + location, e);
            }
        }
        return PemUtil.loadPrivateKeyFromPath(location);
    }
}
