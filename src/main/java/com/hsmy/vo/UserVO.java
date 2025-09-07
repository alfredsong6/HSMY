package com.hsmy.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息VO
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
public class UserVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（不返回给前端）
     */
    private String password;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 性别（0-未知，1-男，2-女）
     */
    private Integer gender;
    
    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    
    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registerTime;
    
    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;
    
    /**
     * 账号状态（0-禁用，1-正常，2-冻结）
     */
    private Integer status;
    
    /**
     * VIP等级（0-普通用户，1-月卡，2-年卡，3-永久）
     */
    private Integer vipLevel;
    
    /**
     * VIP到期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date vipExpireTime;
    
    /**
     * 总功德值
     */
    private Long totalMerit;
    
    /**
     * 功德币余额
     */
    private Long meritCoins;
    
    /**
     * 当前等级
     */
    private Integer currentLevel;
    
    /**
     * Token（登录成功后返回）
     */
    private String token;
}