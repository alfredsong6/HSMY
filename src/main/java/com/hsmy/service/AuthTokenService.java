package com.hsmy.service;

import com.hsmy.domain.auth.AuthToken;

import java.util.Date;

/**
 * 登录token service.
 */
public interface AuthTokenService {

    /**
     * 记录一次登录token.
     */
    AuthToken recordToken(Long userId, String token, Date expiresAt, String clientType, String deviceId);

    /**
     * 通过token查询.
     */
    AuthToken getByToken(String token);

    /**
     * 吊销token.
     */
    boolean revokeByToken(String token);
}
