package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 弹幕设置请求
 */
@Data
public class BulletScreenSettingRequest {

    /**
     * 弹幕开关/模式
     */
    @NotNull(message = "弹幕设置不能为空")
    private Integer bulletScreen;

    /**
     * 弹幕经书ID（bullet_screen=3时必填）
     */
    private Long scriptureId;
}
