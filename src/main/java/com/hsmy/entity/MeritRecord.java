package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 功德记录实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_merit_record")
public class MeritRecord extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 获得功德值（含倍率）
     */
    private Integer meritGained;

    /**
     * 基础功德值（未乘倍率）
     */
    private Integer baseMerit;

    /**
     * 敲击类型：manual-手动，auto-自动
     */
    private String knockType;

    /**
     * 敲击模式：MANUAL、AUTO_AUTOEND、AUTO_TIMED
     */
    private String knockMode;

    /**
     * 来源：knock-敲击，task-任务，login-登录，activity-活动，share-分享
     */
    private String source;

    /**
     * 会话ID，用于统计连击
     */
    private String sessionId;

    /**
     * 连击数
     */
    private Integer comboCount;

    /**
     * 总加成倍率
     */
    private BigDecimal bonusRate;

    /**
     * 道具倍率快照
     */
    private String propSnapshot;

    /**
     * 所属自然日（凌晨清零）
     */
    private Date statDate;

    /**
     * 描述
     */
    private String description;
}
