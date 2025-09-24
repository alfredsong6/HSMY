package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
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
     * 会话模式：MANUAL、AUTO_AUTOEND、AUTO_TIMED
     */
    private String sessionMode;

    /**
     * 限制类型：DURATION、COUNT
     */
    private String limitType;

    /**
     * 限制值，对应秒数或次数
     */
    private Integer limitValue;

    /**
     * 预计结束时间
     */
    private Date expectedEndTime;

    /**
     * 会话功德倍率
     */
    private BigDecimal meritMultiplier;

    /**
     * 道具快照
     */
    private String propSnapshot;

    /**
     * 会话状态
     */
    private String status;

    /**
     * 最后心跳时间
     */
    private Date lastHeartbeatTime;

    /**
     * 结束原因
     */
    private String endReason;

    /**
     * 预扣功德币
     */
    private Integer coinCost;

    /**
     * 已退还功德币
     */
    private Integer coinRefunded;

    /**
     * 钱包流水ID
     */
    private String walletTxnId;

    /**
     * 支付状态
     */
    private String paymentStatus;

    /**
     * 设备信息
     */
    private String deviceInfo;
}
