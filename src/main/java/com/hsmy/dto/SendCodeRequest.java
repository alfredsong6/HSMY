package com.hsmy.dto;

import lombok.Data;

import com.hsmy.enums.AccountType;
import com.hsmy.enums.BusinessType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
    @NotNull(message = "账号类型不能为空")
    private AccountType accountType;
    
    /**
     * 业务类型：register/login/reset_password
     */
    @NotNull(message = "业务类型不能为空")
    private BusinessType businessType;
}
