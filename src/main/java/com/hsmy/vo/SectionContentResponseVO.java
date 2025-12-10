package com.hsmy.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 获取某卷内容与当前卷进度返回体
 */
@Data
public class SectionContentResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ScriptureInfo scripture;

    private SectionInfo section;

    private ReadingState readingState;

    @Data
    public static class ScriptureInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long id;
        private String name;
        private String type;
    }

    @Data
    public static class SectionInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private String id;
        private Integer sectionNo;
        private String title;
        private String content;
        private String audioUrl;
        private Integer wordCount;
    }

    @Data
    public static class ReadingState implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer lastPosition;
        private Double readingProgress;
        private Integer isCompleted;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date lastReadTime;
    }
}
