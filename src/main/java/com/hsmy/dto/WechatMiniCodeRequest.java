package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 小程序码请求参数
 */
@Data
public class WechatMiniCodeRequest {

    /**
     * 小程序内要打开的页面路径
     */
    @NotBlank(message = "page不能为空")
    private String page;

    /**
     * 场景参数
     */
    //@NotBlank(message = "scene不能为空")
    //@Size(max = 64, message = "scene长度不能超过64")
    private String scene;

    /**
     * 二维码宽度，单位px，取值范围 280-1280（微信建议默认无此参数为430）
     */
    private Integer width;
}
