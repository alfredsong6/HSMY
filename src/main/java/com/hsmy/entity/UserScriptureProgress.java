package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户典籍分段进度实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_scripture_progress")
public class UserScriptureProgress extends BaseEntity {

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
     * 分段ID
     */
    private Long sectionId;

    /**
     * 分段阅读进度百分比
     */
    private BigDecimal readingProgress;

    /**
     * 分段内最后阅读位置（字符偏移或段落序号）
     */
    private Integer lastPosition;

    /**
     * 最后阅读时间
     */
    private Date lastReadTime;

    /**
     * 累计阅读时长（秒）
     */
    private Integer spendSeconds;

    /**
     * 是否完成分段
     */
    private Integer isCompleted;
}
