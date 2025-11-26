package com.hsmy.service.impl;

import com.hsmy.domain.auth.AuthIdentity;
import com.hsmy.mapper.AuthIdentityMapper;
import com.hsmy.service.AuthIdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 登录身份 service 实现.
 */
@Service
@RequiredArgsConstructor
public class AuthIdentityServiceImpl implements AuthIdentityService {

    private final AuthIdentityMapper authIdentityMapper;

    @Override
    public AuthIdentity getByOpenId(String provider, String appidOrClientId, String openId) {
        if (!StringUtils.hasText(provider) || !StringUtils.hasText(appidOrClientId) || !StringUtils.hasText(openId)) {
            return null;
        }
        return authIdentityMapper.selectByOpenId(provider, appidOrClientId, openId);
    }

    @Override
    public AuthIdentity getByUnionId(String provider, String unionId) {
        if (!StringUtils.hasText(provider) || !StringUtils.hasText(unionId)) {
            return null;
        }
        return authIdentityMapper.selectByUnionId(provider, unionId);
    }

    @Override
    public AuthIdentity getByPhone(String provider, String phone) {
        if (!StringUtils.hasText(provider) || !StringUtils.hasText(phone)) {
            return null;
        }
        return authIdentityMapper.selectByPhone(provider, phone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthIdentity createIdentity(Long userId,
                                       String provider,
                                       String appidOrClientId,
                                       String openId,
                                       String unionId,
                                       String phone,
                                       String sessionKeyEnc) {
        AuthIdentity identity = new AuthIdentity();
        identity.setUserId(userId);
        identity.setProvider(provider);
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
}
