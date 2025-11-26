package com.hsmy.domain.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 自定义会话/访问令牌表映射.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_auth_token")
public class AuthToken extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联的业务用户ID.
     */
    private Long userId;

    /**
     * token内容（可为SessionId/JWT等）.
     */
    private String token;

    /**
     * 过期时间.
     */
    @TableField("expires_at")
    private Date expiresAt;

    /**
     * 吊销时间（非空则视为失效）.
     */
    @TableField("revoked_at")
    private Date revokedAt;

    /**
     * 客户端类型：miniapp/android/ios等.
     */
    @TableField("client_type")
    private String clientType;

    /**
     * 设备标识.
     */
    @TableField("device_id")
    private String deviceId;
}
