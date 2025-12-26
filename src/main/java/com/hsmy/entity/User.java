package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（加密存储）
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
    
    @TableField(exist = false)
    private String avatarBase64Content;
    
    @TableField(exist = false)
    private boolean avatarBase64Encoded;
    
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
    private Date birthday;
    
    /**
     * 注册时间
     */
    private Date registerTime;
    
    /**
     * 最后登录时间
     */
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
    private Date vipExpireTime;
}