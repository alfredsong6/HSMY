package com.hsmy.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 开始阅读状态返回
 */
@Data
public class StartReadingStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态：trial_not_exceeded / trial_exceeded / valid / expired
     */
    private String status;

    /**
     * 允许阅读标识
     */
    private Boolean readFlag;

    /**
     * 购买类型
     */
    private String purchaseType;

    /**
     * 过期时间（可能为null）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    /**
     * 已完成分段数
     */
    private Integer completedSections;

    /**
     * 总分段数
     */
    private Integer totalSections;
}
