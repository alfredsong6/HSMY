package com.hsmy.config.mock;

import cn.hutool.json.JSONUtil;
import com.wechat.pay.java.core.cipher.AeadCipher;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * dev/test 环境下的微信回调解析 mock.
 */
@Configuration
@Profile({"dev", "test"})
public class MockWechatNotificationConfig {

    @Bean
    @Primary
    public NotificationParser mockNotificationParser() {
        NotificationConfig config = new NotificationConfig() {
            @Override
            public String getSignType() {
                return "";
            }

            @Override
            public String getCipherType() {
                return "";
            }

            @Override
            public com.wechat.pay.java.core.cipher.Verifier createVerifier() {
                return null;
            }

            @Override
            public AeadCipher createAeadCipher() {
                return null;
            }


        };

        return new NotificationParser(config) {
            @Override
            public <T> T parse(RequestParam requestParam, Class<T> clazz) {
                return JSONUtil.toBean(requestParam.getBody(), clazz);
            }
        };
    }
}
