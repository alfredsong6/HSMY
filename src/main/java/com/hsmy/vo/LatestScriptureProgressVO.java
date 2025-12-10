package com.hsmy.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户最近阅读进度VO
 */
@Data
public class LatestScriptureProgressVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String scriptureId;

    private String scriptureName;

    private String coverUrl;

    private Section currentSection;

    private Integer lastPosition;

    private BigDecimal readingProgress;

    private Integer completedSections;

    private Integer totalSections;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastReadTime;

    /**
     * 权限状态：1-正常 2-过期 3-退款/失效
     */
    private Integer status;

    @Data
    public static class Section implements Serializable {
        private static final long serialVersionUID = 1L;
        private String sectionId;
        private Integer sectionNo;
        private String title;
    }
}
