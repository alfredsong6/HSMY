package com.hsmy.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 初始化密码VO
 */
@Data
public class InitializePasswordVO {
    
    /**
     * 用户ID
     */
    //@NotNull(message = "用户ID不能为空")
    private Long userId;
    
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