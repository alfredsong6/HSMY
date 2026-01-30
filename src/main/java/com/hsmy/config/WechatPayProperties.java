package com.hsmy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置属性
 *
 * <p>通过 {@code wechatpay.*} 前缀读取配置。敏感值需通过环境变量或外部化配置注入。</p>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechatpay")
public class WechatPayProperties {

    /**
     * 是否启用微信支付能力
     */
    private boolean enabled = false;

    /**
     * 微信 AppID（公众号/小程序）
     */
    private String appId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户证书序列号
     */
    private String serialNo;

    /**
     * API v3 密钥
     */
    private String apiV3Key;

    /**
     * 商户私钥文件路径（支持 file:/ 与 classpath: 前缀）
     */
    private String privateKeyPath;

    /**
     * 商户私钥内容（PEM）。当提供该字段时优先生效
     */
    private String privateKey;

    private String publicKeyPath;

    private String publicKey;

    private String publicKeyId;

    /**
     * 支付结果回调地址
     */
    private String notifyUrl;

    /**
     * 订单描述
     */
    private String description = "功德充值";

    /**
     * 交易币种，默认人民币
     */
    private String currency = "CNY";
}
