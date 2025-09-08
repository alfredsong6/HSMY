package com.hsmy.dto;

import lombok.Data;

/**
 * 登录响应DTO
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Data
public class LoginResponse {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
}