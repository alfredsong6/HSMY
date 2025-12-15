package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 更新用户昵称请求DTO.
 */
@Data
public class UpdateNicknameRequest {

    /**
     * 新昵称.
     */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 32, message = "昵称长度不能超过32个字符")
    private String nickname;
}
