package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 典籍实体类
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_scripture")
public class Scripture extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 典籍名称
     */
    private String scriptureName;

    /**
     * 典籍类型：sutra-佛经经典，mantra-咒语
     */
    private String scriptureType;

    /**
     * 作者/译者
     */
    private String author;

    /**
     * 典籍描述
     */
    private String description;

    /**
     * 典籍内容
     */
    private String content;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 音频URL
     */
    private String audioUrl;

    /**
     * 是否热门：0-否，1-是
     */
    private Integer isHot;

    /**
     * 购买价格（福币）
     */
    private Integer price;

    /**
     * 买断价格（福币），NULL表示不支持买断
     */
    private Integer permanentPrice;

    /**
     * 计价单位：本、部、卷、则
     */
    private String priceUnit;

    /**
     * 购买时长（月）
     */
    private Integer durationMonths;

    /**
     * 阅读次数
     */
    private Long readCount;

    /**
     * 购买次数
     */
    private Integer purchaseCount;

    /**
     * 难度等级：1-初级，2-中级，3-高级
     */
    private Integer difficultyLevel;

    /**
     * 字数
     */
    private Integer wordCount;

    /**
     * 整本总字数（分段汇总）
     */
    private Integer totalWordCount;

    /**
     * 分段/卷总数
     */
    private Integer sectionCount;

    /**
     * 试读分段数
     */
    private Integer previewSectionCount;

    /**
     * 分类标签，用逗号分隔
     */
    private String categoryTags;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 排序序号
     */
    private Integer sortOrder;
}
