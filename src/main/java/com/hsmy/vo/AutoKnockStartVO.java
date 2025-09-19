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
     * 敲击时长（秒）
     * -1表示无限时长，其他值表示具体秒数
     */
    @NotNull(message = "敲击时长不能为空")
    private Integer duration;

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
}