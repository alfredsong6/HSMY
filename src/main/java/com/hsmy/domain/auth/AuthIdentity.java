package com.hsmy.domain.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 第三方/本地登录身份表映射.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_auth_identity")
public class AuthIdentity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联的业务用户ID.
     */
    private Long userId;

    /**
     * 身份提供方：wechat_mini/wechat_app/sms等.
     */
    private String provider;

    /**
     * 小程序appid或App客户端ID.
     */
    @TableField("appid_or_client_id")
    private String appidOrClientId;

    /**
     * 开放平台openId.
     */
    @TableField("open_id")
    private String openId;

    /**
     * 开放平台unionId.
     */
    @TableField("union_id")
    private String unionId;

    /**
     * 绑定手机号（provider=sms时必填）.
     */
    private String phone;

    /**
     * 微信session_key等敏感凭据的加密存储.
     */
    @TableField("session_key_enc")
    private String sessionKeyEnc;

    /**
     * 最近登录时间.
     */
    @TableField("last_login_at")
    private Date lastLoginAt;
}
