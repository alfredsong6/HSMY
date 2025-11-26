package com.hsmy.service;

import com.hsmy.domain.auth.AuthIdentity;

import java.util.Date;

/**
 * 登录身份 service.
 */
public interface AuthIdentityService {

    /**
     * 根据 openId 查询身份.
     */
    AuthIdentity getByOpenId(String provider, String appidOrClientId, String openId);

    /**
     * 根据 unionId 查询身份.
     */
    AuthIdentity getByUnionId(String provider, String unionId);

    /**
     * 根据手机号查询身份.
     */
    AuthIdentity getByPhone(String provider, String phone);

    /**
     * 创建身份记录.
     */
    AuthIdentity createIdentity(Long userId,
                                String provider,
                                String appidOrClientId,
                                String openId,
                                String unionId,
                                String phone,
                                String sessionKeyEnc);

    /**
     * 更新登录相关信息.
     */
    int touchLogin(Long id,
                   Long userId,
                   String phone,
                   String unionId,
                   String sessionKeyEnc,
                   Date lastLoginAt);
}
