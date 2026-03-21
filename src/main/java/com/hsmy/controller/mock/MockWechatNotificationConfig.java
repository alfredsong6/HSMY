package com.hsmy.controller.mock;

import cn.hutool.json.JSONUtil;
import com.wechat.pay.java.core.cipher.AeadCipher;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import lombok.extern.slf4j.Slf4j;

/**
 * dev/test 环境下的微信回调解析 mock.
 */
@Slf4j
public class MockWechatNotificationConfig {

    public NotificationParser mockNotificationParser() {
        log.info("------------------mockNotificationParser-----------------");
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
