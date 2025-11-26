package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.domain.auth.AuthToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 登录token Mapper.
 */
@Mapper
public interface AuthTokenMapper extends BaseMapper<AuthToken> {

    /**
     * 根据 token 查询.
     */
    AuthToken selectByToken(@Param("token") String token);

    /**
     * 吊销 token.
     */
    int revokeByToken(@Param("token") String token);
}
