package com.hsmy.service;

import com.hsmy.domain.auth.AuthIdentity;
import com.hsmy.enums.AuthProvider;

import java.util.Date;
import java.util.List;

/**
 * 登录身份 service.
 */
public interface AuthIdentityService {

    /**
     * 根据 openId 查询身份.
     */
    AuthIdentity getByOpenId(AuthProvider provider, String appidOrClientId, String openId);

    /**
     * 根据 unionId 查询身份.
     */
    AuthIdentity getByUnionId(AuthProvider provider, String unionId);

    /**
     * 根据 provider + userId 查询身份.
     */
    AuthIdentity getByProviderAndUserId(AuthProvider provider, Long userId);

    /**
     * 根据手机号查询身份.
     */
    AuthIdentity getByPhone(AuthProvider provider, String phone);

    /**
     * 根据 userId 查询全部身份.
     */
    List<AuthIdentity> listByUserId(Long userId);

    /**
     * 创建身份记录.
     */
    AuthIdentity createIdentity(Long userId,
                                AuthProvider provider,
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
