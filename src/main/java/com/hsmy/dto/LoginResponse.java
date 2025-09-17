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
     * 会话ID（保留字段，兼容旧版本）
     */
    private String sessionId;
    
    /**
     * 访问令牌
     */
    private String token;
    
    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";
    
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