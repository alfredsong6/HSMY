package com.hsmy.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 短信重置密码VO
 */
@Data
public class ResetPasswordWithSmsVO {
    
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    /**
     * 短信验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;
    
    /**
     * 新密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}