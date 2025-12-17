package com.hsmy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户反馈提交请求
 */
@Data
public class UserFeedbackRequest {

    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空")
    @Size(max = 2000, message = "反馈内容长度不能超过2000字符")
    private String content;

    /**
     * 联系方式
     */
    @NotBlank(message = "联系方式不能为空")
    @Size(max = 100, message = "联系方式长度不能超过100字符")
    private String contact;

    /**
     * 反馈来源：APP/WEB/MINI_PROGRAM等
     */
    @NotBlank(message = "反馈来源不能为空")
    @Size(max = 20, message = "反馈来源长度不能超过20字符")
    private String feedbackSource;
}
