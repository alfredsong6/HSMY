package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 典籍查询条件VO
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Data
public class ScriptureQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 典籍类型：sutra-佛经经典，mantra-咒语
     */
    private String scriptureType;

    /**
     * 是否热门：0-否，1-是
     */
    private Integer isHot;

    /**
     * 最低价格
     */
    private Integer minPrice;

    /**
     * 最高价格
     */
    private Integer maxPrice;

    /**
     * 难度等级：1-初级，2-中级，3-高级
     */
    private Integer difficultyLevel;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 标签
     */
    private String tag;

    /**
     * 排序字段：read_count-阅读次数，purchase_count-购买次数，create_time-创建时间，price-价格
     */
    private String sortField;

    /**
     * 排序方向：asc-升序，desc-降序
     */
    private String sortOrder;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}