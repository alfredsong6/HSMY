package com.hsmy.service;

/**
 * 验证码服务接口
 */
public interface VerificationCodeService {
    
    /**
     * 发送验证码
     * @param account 手机号或邮箱
     * @param accountType 账号类型：phone/email
     * @param businessType 业务类型：register/login/reset_password
     * @param ipAddress IP地址
     * @return 是否发送成功
     */
    boolean sendCode(String account, com.hsmy.enums.AccountType accountType, com.hsmy.enums.BusinessType businessType, String ipAddress);

    /**
     * 验证验证码
     * @param account 手机号或邮箱
     * @param code 验证码
     * @param businessType 业务类型
     * @return 是否验证成功
     */
    boolean verifyCode(String account, String code, com.hsmy.enums.BusinessType businessType);
    
    /**
     * 验证验证码并标记为已使用
     * @param account 手机号或邮箱
     * @param accountType 账号类型：phone/email
     * @param code 验证码
     * @param businessType 业务类型
     * @return 是否验证成功
     */
    boolean verify(String account, com.hsmy.enums.AccountType accountType, String code, com.hsmy.enums.BusinessType businessType);
    
    /**
     * 标记验证码已使用
     * @param account 手机号或邮箱
     * @param code 验证码
     * @param businessType 业务类型
     */
    void markCodeAsUsed(String account, String code, com.hsmy.enums.BusinessType businessType);
}
