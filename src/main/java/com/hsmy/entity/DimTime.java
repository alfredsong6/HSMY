package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Time dimension record providing reusable calendar metadata.
 */
@Data
@TableName("t_dim_time")
public class DimTime {

    private Long id;

    private Date dateValue;

    private String isoWeek;

    private String monthValue;

    private String yearValue;

    private Date weekStart;

    private Date weekEnd;

    private Date monthStart;

    private Date monthEnd;

    private Integer quarter;

    private Integer isWeekend;

    private Date createTime;

    private Date updateTime;
}
