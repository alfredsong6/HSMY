package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 验证码实体
 */
@Data
@TableName("verification_code")
public class VerificationCode {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 手机号或邮箱
     */
    private String account;
    
    /**
     * 账号类型：phone/email
     */
    private String accountType;
    
    /**
     * 验证码
     */
    private String code;
    
    /**
     * 业务类型：register/login/reset_password
     */
    private String businessType;
    
    /**
     * 是否已使用
     */
    private Boolean used;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 使用时间
     */
    private LocalDateTime useTime;
    
    /**
     * IP地址
     */
    private String ipAddress;
}