package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 典籍分段列表返回体
 */
@Data
public class ScriptureSectionsResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 典籍ID
     */
    private Long scriptureId;

    /**
     * 分段列表
     */
    private List<ScriptureSectionVO> sections;
}
