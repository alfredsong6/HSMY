package com.hsmy.service.wechat;

import com.hsmy.service.wechat.dto.WechatPhoneInfo;
import com.hsmy.service.wechat.dto.WechatSessionInfo;

/**
 * 微信小程序认证相关服务.
 */
public interface WechatMiniAuthService {

    /**
     * 通过 jsCode 换取 session 信息.
     */
    WechatSessionInfo code2Session(String appId, String jsCode);

    /**
     * 通过 phoneCode 获取手机号.
     */
    WechatPhoneInfo getPhoneNumber(String sessionKey);

    /**
     * 默认 appId.
     */
    String getDefaultAppId();
}
