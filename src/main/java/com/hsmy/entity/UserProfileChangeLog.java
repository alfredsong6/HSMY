package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User profile change log entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_profile_change_log")
public class UserProfileChangeLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String changeType;
    private String oldValue;
    private String newValue;
    private String remark;
}
