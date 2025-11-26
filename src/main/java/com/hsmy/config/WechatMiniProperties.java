package com.hsmy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序基础配置.
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.mini")
public class WechatMiniProperties {

    /**
     * 小程序 appid.
     */
    private String appId;

    /**
     * 小程序 secret.
     */
    private String secret;
}
