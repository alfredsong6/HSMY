package com.hsmy.service.wechat.dto;

import lombok.Data;

/**
 * 微信手机号信息.
 */
@Data
public class WechatPhoneInfo {
    /**
     * 完整手机号（含国家码时可追加）.
     */
    private String phoneNumber;

    /**
     * 不带国家码的纯手机号.
     */
    private String purePhoneNumber;
}
