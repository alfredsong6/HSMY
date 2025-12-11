package com.hsmy.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

/**
 * 用户设置返回VO
 */
@Data
public class UserSettingVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private Integer soundEnabled;
    private Integer soundVolume;
    private Integer vibrationEnabled;
    private Integer dailyReminder;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime reminderTime;
    private Integer privacyMode;
    private Integer autoKnockSpeed;
    private Long themeId;
    private Long skinId;
    private Long soundId;
    private Integer bulletScreen;
    private String scriptureId;
    private String scriptureName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
