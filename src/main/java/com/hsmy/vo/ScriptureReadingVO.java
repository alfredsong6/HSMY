package com.hsmy.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 典籍阅读内容VO
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Data
public class ScriptureReadingVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 典籍ID
     */
    private Long scriptureId;

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
     * 典籍完整内容
     */
    private String content;

    /**
     * 分段ID
     */
    private Long sectionId;

    /**
     * 分段序号
     */
    private Integer sectionNo;

    /**
     * 分段标题
     */
    private String sectionTitle;

    /**
     * 分段字数
     */
    private Integer sectionWordCount;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 音频URL
     */
    private String audioUrl;

    /**
     * 字数
     */
    private Integer wordCount;

    /**
     * 分类标签，用逗号分隔
     */
    private String categoryTags;

    /**
     * 用户当前阅读进度百分比
     */
    private BigDecimal readingProgress;

    /**
     * 当前分段进度百分比
     */
    private BigDecimal sectionProgress;

    /**
     * 最后阅读位置（字符位置）
     */
    private Integer lastReadingPosition;

    /**
     * 用户阅读次数
     */
    private Integer readCount;

    /**
     * 最后阅读时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastReadTime;

    /**
     * 购买过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    /**
     * 剩余天数
     */
    private Long remainingDays;

    /**
     * 是否即将过期（7天内）
     */
    private Boolean isExpiringSoon;

    /**
     * 是否为买断模式
     */
    private Boolean isPermanent;

    /**
     * 支付的福币数量
     */
    private Integer meritCoinsPaid;

    /**
     * 购买月数
     */
    private Integer purchaseMonths;

    /**
     * 根据阅读进度计算的当前阅读位置（字符位置）
     */
    private Integer currentPosition;

    /**
     * 建议的阅读起始位置（考虑段落边界）
     */
    private Integer suggestedStartPosition;
}
