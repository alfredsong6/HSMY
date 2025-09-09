package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 增强的登录请求DTO
 * 支持密码登录和验证码登录
 */
@Data
public class LoginRequestV2 {
    
    /**
     * 登录账号（用户名/手机号/邮箱）
     */
    @NotBlank(message = "登录账号不能为空")
    private String loginAccount;
    
    /**
     * 登录方式：password/code
     */
    @NotBlank(message = "登录方式不能为空")
    private String loginType;
    
    /**
     * 密码（密码登录时必填）
     */
    private String password;
    
    /**
     * 验证码（验证码登录时必填）
     */
    private String code;
}