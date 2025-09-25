package com.hsmy.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 典籍信息VO
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Data
public class ScriptureVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 典籍ID
     */
    private Long id;

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

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 用户是否已购买（查询时动态填充）
     */
    private Boolean isPurchased;

    /**
     * 用户购买是否有效（未过期）（查询时动态填充）
     */
    private Boolean isPurchaseValid;
}