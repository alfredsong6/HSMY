package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户反馈实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_feedback")
public class UserFeedback extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 提交用户ID
     */
    private Long userId;

    /**
     * 反馈类型
     */
    private String feedbackType;

    /**
     * 反馈来源
     */
    private String feedbackSource;

    /**
     * 反馈标题
     */
    private String title;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 是否允许联系：0-否 1-是
     */
    private Integer isContactable;

    /**
     * 处理状态
     */
    private String status;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 客户端版本号
     */
    private String appVersion;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 操作系统类型
     */
    private String osType;

    /**
     * 操作系统版本
     */
    private String osVersion;

    /**
     * 附件地址（JSON数组字符串）
     */
    private String attachUrls;

    /**
     * 处理人ID
     */
    private Long processedBy;

    /**
     * 处理时间
     */
    private Date processedTime;

    /**
     * 处理备注
     */
    private String remark;
}
