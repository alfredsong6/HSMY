package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Data
public class LoginRequest {
    
    /**
     * 用户名/手机号/邮箱
     */
    @NotBlank(message = "登录账号不能为空")
    private String loginAccount;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}