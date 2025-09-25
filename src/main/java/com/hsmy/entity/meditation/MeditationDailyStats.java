package com.hsmy.entity.meditation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 冥想每日统计实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_meditation_daily_stats")
public class MeditationDailyStats extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private Date statDate;
    private Integer sessionCount;
    private Integer totalMinutes;
    private String lastMood;
    private String lastInsight;
}
