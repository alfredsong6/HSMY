package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 更新用户头像请求DTO
 */
@Data
public class UpdateAvatarRequest {
    
    /**
     * 头像URL
     */
    @NotBlank(message = "头像URL不能为空")
    private String avatarUrl;
}