package com.hsmy.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 手动敲击VO
 *
 * @author HSMY
 * @date 2025/09/07
 */
@Data
public class KnockVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    //@NotNull(message = "用户ID不能为空")
    private Long userId;

    private String requestId;

    /**
     * 敲击次数
     */
    private Integer knockCount;

    /**
     * 敲击类型: 1-普通敲击, 2-连击敲击
     */
    private Integer knockType = 1;

    /**
     * 来源: 1-木鱼, 2-钟声, 3-鼓声, 4-念经, 5-供香
     */
    private Integer source = 1;

    /**
     * 敲击时间（由前端传入）
     */
    @NotNull(message = "敲击时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date knockTime;

    /**
     * 功德值（单次敲击获得的功德）
     */
    private Integer meritValue = 1;

    /**
     * 倍率值（由道具或活动提供）
     */
    private Double multiplier = 1.0;

    /**
     * 道具快照JSON
     */
    private String propSnapshot;

    /**
     * 敲击模式：MANUAL、AUTO_AUTOEND、AUTO_TIMED
     */
    private String knockMode = "MANUAL";

    /**
     * 限制类型：DURATION、COUNT（仅自动敲击适用）
     */
    private String limitType;

    /**
     * 限制值：秒或次数（仅自动敲击适用）
     */
    private Integer limitValue;

    /**
     * 会话ID（保留字段，暂不使用）
     */
    private String sessionId;

    /**
     * 连击数（保留字段，暂不使用）
     */
    private Integer comboCount;
}
