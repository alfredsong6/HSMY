package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 典籍分段（卷/章）实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_scripture_section")
public class ScriptureSection extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 典籍ID
     */
    private Long scriptureId;

    /**
     * 分段序号（从1开始）
     */
    private Integer sectionNo;

    /**
     * 分段标题
     */
    private String title;

    /**
     * 分段正文（可存Markdown）
     */
    private String content;

    /**
     * 分段音频URL
     */
    private String audioUrl;

    /**
     * 分段字数
     */
    private Integer wordCount;

    /**
     * 朗读/播放时长（秒）
     */
    private Integer durationSeconds;

    /**
     * 是否可试读：0-否，1-是
     */
    private Integer isFree;

    /**
     * 排序序号
     */
    private Integer sortOrder;
}
