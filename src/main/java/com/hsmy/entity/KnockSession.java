package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 敲击会话实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_knock_session")
public class KnockSession extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 开始时间
     */
    private Date startTime;
    
    /**
     * 结束时间
     */
    private Date endTime;
    
    /**
     * 敲击次数
     */
    private Integer knockCount;
    
    /**
     * 获得功德值
     */
    private Integer meritGained;
    
    /**
     * 最高连击数
     */
    private Integer maxCombo;
    
    /**
     * 敲击类型：manual-手动，auto-自动
     */
    private String knockType;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
}