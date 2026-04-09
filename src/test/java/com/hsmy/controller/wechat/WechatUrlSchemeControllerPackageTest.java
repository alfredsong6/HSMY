package com.hsmy.controller.wechat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WechatUrlSchemeControllerPackageTest {

    @Test
    void controllerShouldStayUnderHsmyBasePackage() {
        assertEquals("com.hsmy.controller.wechat", WechatUrlSchemeController.class.getPackage().getName());
    }
}
