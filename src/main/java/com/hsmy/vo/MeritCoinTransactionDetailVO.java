package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 功德币流水详情视图.
 */
@Data
public class MeritCoinTransactionDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer changeAmount;
    private Integer balanceAfter;
    private String bizType;
    private String remark;
    private Date createTime;
    private Detail detail;

    @Data
    public static class Detail implements Serializable {
        private static final long serialVersionUID = 1L;
        private String title;
        private String description;
        private String extraInfo;
    }
}

