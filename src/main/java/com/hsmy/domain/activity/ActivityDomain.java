package com.hsmy.domain.activity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 活动查询领域对象.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_activity", autoResultMap = true)
public class ActivityDomain extends BaseEntity {

    private String activityName;

    private String activityType;

    @TableField("`description`")
    private String description;

    private String bannerUrl;

    private Date startTime;

    private Date endTime;

    private BigDecimal meritBonusRate;

    private Integer status;

    private Integer sortOrder;

    @TableField(value = "`rules`", typeHandler = JacksonTypeHandler.class)
    private ActivityRule rules;
}

