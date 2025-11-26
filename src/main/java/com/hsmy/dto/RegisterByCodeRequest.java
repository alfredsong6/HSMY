package com.hsmy.dto;

import lombok.Data;

import com.hsmy.enums.AccountType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 验证码注册请求DTO
 */
@Data
public class RegisterByCodeRequest {
    
    /**
     * 手机号或邮箱
     */
    @NotBlank(message = "账号不能为空")
    private String account;
    
    /**
     * 账号类型：phone/email
     */
    @NotNull(message = "账号类型不能为空")
    private AccountType accountType;
    
    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码格式不正确")
    private String code;
    
    /**
     * 昵称（可选）
     */
    private String nickname;
}
