package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * App端手机号登录/注册请求.
 */
@Data
public class AppPhoneLoginRequest {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{4,6}$", message = "验证码格式不正确")
    private String code;

    /**
     * 昵称（注册时可选）.
     */
    private String nickname;

    /**
     * 设备ID（可选，用于token记录）.
     */
    private String deviceId;
}
