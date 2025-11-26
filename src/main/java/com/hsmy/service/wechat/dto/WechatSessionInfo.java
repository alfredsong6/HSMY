package com.hsmy.service.wechat.dto;

import lombok.Data;

/**
 * 微信 code2Session 响应信息.
 */
@Data
public class WechatSessionInfo {
    private String openId;
    private String unionId;
    private String sessionKey;
}
