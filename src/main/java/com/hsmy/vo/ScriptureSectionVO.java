package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 典籍分段元数据VO
 */
@Data
public class ScriptureSectionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分段ID
     */
    private Long id;

    /**
     * 分段ID（用于前端一致字段名）
     */
    private Long sectionId;

    private Integer sectionNo;

    private String title;

    private Integer wordCount;

    private Integer durationSeconds;

    private Integer isFree;
}
