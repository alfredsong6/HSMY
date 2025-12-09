package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 典籍分段元数据VO
 */
@Data
public class ScriptureSectionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer sectionNo;

    private String title;

    private Integer wordCount;

    private Integer durationSeconds;

    private Integer isFree;
}
