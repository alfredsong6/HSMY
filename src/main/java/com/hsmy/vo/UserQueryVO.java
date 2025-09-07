package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询VO
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
public class UserQueryVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
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
     * 账号状态
     */
    private Integer status;
    
    /**
     * VIP等级
     */
    private Integer vipLevel;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}