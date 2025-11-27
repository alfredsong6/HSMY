package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.domain.auth.AuthIdentity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 登录身份表 Mapper.
 */
@Mapper
public interface AuthIdentityMapper extends BaseMapper<AuthIdentity> {

    /**
     * 根据 provider + appid/clientId + openId 查询.
     */
    AuthIdentity selectByOpenId(@Param("provider") String provider,
                                @Param("appidOrClientId") String appidOrClientId,
                                @Param("openId") String openId);

    /**
     * 根据 provider + unionId 查询.
     */
    AuthIdentity selectByUnionId(@Param("provider") String provider,
                                 @Param("unionId") String unionId);

    /**
     * 根据 provider + userId 查询.
     */
    AuthIdentity selectByProviderAndUserId(@Param("provider") String provider,
                                           @Param("userId") Long userId);

    /**
     * 根据 provider + phone 查询.
     */
    AuthIdentity selectByPhone(@Param("provider") String provider,
                               @Param("phone") String phone);

    /**
     * 根据 userId 查询全部身份.
     */
    List<AuthIdentity> selectByUserId(@Param("userId") Long userId);

    /**
     * 更新登录元信息.
     */
    int touchLogin(@Param("id") Long id,
                   @Param("userId") Long userId,
                   @Param("phone") String phone,
                   @Param("unionId") String unionId,
                   @Param("sessionKeyEnc") String sessionKeyEnc,
                   @Param("lastLoginAt") Date lastLoginAt);
}
