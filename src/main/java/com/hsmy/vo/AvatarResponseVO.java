package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户头像响应.
 */
@Data
public class AvatarResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String avatarUrl;
    private String base64Content;
    private boolean base64Encoded;
}
