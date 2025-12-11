package com.hsmy.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 连续文本流响应
 */
@Data
public class ScriptureTextStreamVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文本块内容（纯文本）
     */
    private String content;

    /**
     * 下一次请求的起始偏移
     */
    private Integer nextOffset;

    /**
     * 是否已到达结尾
     */
    private Boolean isEnd;
}
