package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 验证码实体
 */
@Data
@TableName("t_verification_code")
public class VerificationCode  extends BaseEntity {

    
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