package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 微信小程序登录/注册请求.
 */
@Data
public class WechatMiniLoginRequest {
    /**
     * wx.login 获取的 code.
     */
    @NotBlank(message = "authCode不能为空")
    private String authCode;

    /**
     * wx.getPhoneNumber 返回的 code.
     */
//    private String phoneCode;

    /**
     * 用户昵称（可选，若不传则使用微信昵称或默认值）.
     */
    private String nickname;

    /**
     * 头像地址.
     */
    private String avatarUrl;
}
