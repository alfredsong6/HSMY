package com.hsmy.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户会话上下文信息
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Data
public class UserSessionContext implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
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
    
    /**
     * 用户状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 当前等级
     */
    private Integer currentLevel;
    
    /**
     * 功德币数量
     */
    private Integer meritCoins;
    
    /**
     * 总功德值
     */
    private Long totalMerit;
    
    /**
     * 登录时间
     */
    private Date loginTime;
    
    /**
     * 最后访问时间
     */
    private Date lastAccessTime;
    
    /**
     * 登录IP
     */
    private String loginIp;
    
    /**
     * User-Agent
     */
    private String userAgent;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 是否是管理员
     */
    private Boolean isAdmin;
    
    /**
     * 角色列表（可选，根据需要扩展）
     */
    private String roles;
    
    /**
     * 权限列表（可选，根据需要扩展）
     */
    private String permissions;
}