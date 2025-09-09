package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 发送验证码请求DTO
 */
@Data
public class SendCodeRequest {
    
    /**
     * 手机号或邮箱
     */
    @NotBlank(message = "账号不能为空")
    private String account;
    
    /**
     * 账号类型：phone/email
     */
    @NotBlank(message = "账号类型不能为空")
    @Pattern(regexp = "^(phone|email)$", message = "账号类型只能是phone或email")
    private String accountType;
    
    /**
     * 业务类型：register/login/reset_password
     */
    @NotBlank(message = "业务类型不能为空")
    @Pattern(regexp = "^(register|login|reset_password)$", message = "业务类型不合法")
    private String businessType;
}