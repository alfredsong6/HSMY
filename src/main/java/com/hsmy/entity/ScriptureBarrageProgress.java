package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 弹幕阅读进度
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_scripture_barrage_progress")
public class ScriptureBarrageProgress extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 典籍ID
     */
    private Long scriptureId;

    /**
     * 全局偏移
     */
    private Integer lastOffset;

    /**
     * 当前卷/章ID
     */
    private Long lastSectionId;

    /**
     * 卷内偏移
     */
    private Integer sectionOffset;

    /**
     * 最近一次获取的limit
     */
    private Integer lastFetchLimit;

    /**
     * 最近一次获取时间
     */
    private Date lastFetchTime;

    /**
     * 是否每日重置进度：0-否，1-是
     */
    private Integer isDailyReset;
}
