package com.hsmy.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 敲击次数
     */
    private Integer knockCount = 1;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 连击数
     */
    private Integer comboCount;
}