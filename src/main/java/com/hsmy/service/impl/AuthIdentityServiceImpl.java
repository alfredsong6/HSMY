package com.hsmy.service.impl;

import com.hsmy.domain.auth.AuthIdentity;
import com.hsmy.enums.AuthProvider;
import com.hsmy.mapper.AuthIdentityMapper;
import com.hsmy.service.AuthIdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 登录身份 service 实现.
 */
@Service
@RequiredArgsConstructor
public class AuthIdentityServiceImpl implements AuthIdentityService {

    private final AuthIdentityMapper authIdentityMapper;

    @Override
    public AuthIdentity getByOpenId(AuthProvider provider, String appidOrClientId, String openId) {
        String providerCode = toCode(provider);
        if (!StringUtils.hasText(providerCode) || !StringUtils.hasText(appidOrClientId) || !StringUtils.hasText(openId)) {
            return null;
        }
        return authIdentityMapper.selectByOpenId(providerCode, appidOrClientId, openId);
    }

    @Override
    public AuthIdentity getByUnionId(AuthProvider provider, String unionId) {
        String providerCode = toCode(provider);
        if (!StringUtils.hasText(providerCode) || !StringUtils.hasText(unionId)) {
            return null;
        }
        return authIdentityMapper.selectByUnionId(providerCode, unionId);
    }

    @Override
    public AuthIdentity getByProviderAndUserId(AuthProvider provider, Long userId) {
        String providerCode = toCode(provider);
        if (!StringUtils.hasText(providerCode) || userId == null) {
            return null;
        }
        return authIdentityMapper.selectByProviderAndUserId(providerCode, userId);
    }

    @Override
    public AuthIdentity getByPhone(AuthProvider provider, String phone) {
        String providerCode = toCode(provider);
        if (!StringUtils.hasText(providerCode) || !StringUtils.hasText(phone)) {
            return null;
        }
        return authIdentityMapper.selectByPhone(providerCode, phone);
    }

    @Override
    public List<AuthIdentity> listByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return authIdentityMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthIdentity createIdentity(Long userId,
                                       AuthProvider provider,
                                       String appidOrClientId,
                                       String openId,
                                       String unionId,
                                       String phone,
                                       String sessionKeyEnc) {
        String providerCode = toCode(provider);
        if (!StringUtils.hasText(providerCode)) {
            return null;
        }
        AuthIdentity identity = new AuthIdentity();
        identity.setUserId(userId);
        identity.setProvider(providerCode);
        identity.setAppidOrClientId(appidOrClientId);
        identity.setOpenId(openId);
        identity.setUnionId(unionId);
        identity.setPhone(phone);
        identity.setSessionKeyEnc(sessionKeyEnc);
        identity.setLastLoginAt(new Date());
        authIdentityMapper.insert(identity);
        return identity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int touchLogin(Long id,
                          Long userId,
                          String phone,
                          String unionId,
                          String sessionKeyEnc,
                          Date lastLoginAt) {
        Date loginTime = lastLoginAt != null ? lastLoginAt : new Date();
        return authIdentityMapper.touchLogin(id, userId, phone, unionId, sessionKeyEnc, loginTime);
    }

    private String toCode(AuthProvider provider) {
        return provider == null ? null : provider.getCode();
    }
}
