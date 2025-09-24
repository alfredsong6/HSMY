package com.hsmy.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 自动敲击启动请求VO
 *
 * @author HSMY
 * @date 2025/09/19
 */
@Data
public class AutoKnockStartVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话模式：AUTO_AUTOEND-自动结束，AUTO_TIMED-定时结束
     */
    @NotNull(message = "会话模式不能为空")
    private String mode = "AUTO_TIMED";

    /**
     * 限制类型：DURATION、COUNT
     */
    @NotNull(message = "限制类型不能为空")
    private String limitType = "DURATION";

    /**
     * 限制值：秒或次数
     */
    @NotNull(message = "限制值不能为空")
    private Integer limitValue = 60;

    /**
     * 每秒敲击次数（可选，默认3）
     */
    private Integer knocksPerSecond = 3;

    /**
     * 倍率值（可选，默认1.0）
     * 用于计算功德值的加成
     */
    private Double multiplier = 1.0;

    /**
     * 来源类型（可选）
     * 1-木鱼, 2-钟声, 3-鼓声, 4-念经, 5-供香
     */
    private Integer source = 1;

    /**
     * 道具快照JSON
     */
    private String propSnapshot;
}
