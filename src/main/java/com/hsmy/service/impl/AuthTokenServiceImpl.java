package com.hsmy.service.impl;

import com.hsmy.domain.auth.AuthToken;
import com.hsmy.mapper.AuthTokenMapper;
import com.hsmy.service.AuthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 登录token service 实现.
 */
@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final AuthTokenMapper authTokenMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthToken recordToken(Long userId, String token, Date expiresAt, String clientType, String deviceId) {
        AuthToken authToken = new AuthToken();
        authToken.setUserId(userId);
        authToken.setToken(token);
        authToken.setExpiresAt(expiresAt);
        authToken.setClientType(clientType);
        authToken.setDeviceId(deviceId);
        authTokenMapper.insert(authToken);
        return authToken;
    }

    @Override
    public AuthToken getByToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        return authTokenMapper.selectByToken(token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revokeByToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        return authTokenMapper.revokeByToken(token) > 0;
    }
}
