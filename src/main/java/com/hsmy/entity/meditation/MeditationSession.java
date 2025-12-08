package com.hsmy.entity.meditation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 冥想会话实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_meditation_session")
public class MeditationSession extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 会话UUID */
    private String sessionId;
    /** 用户ID */
    private Long userId;
    /** 计划冥想时长（秒） */
    private Integer plannedDuration;
    /** 实际冥想时长（秒） */
    private Integer actualDuration;
    /** 开始时间 */
    private Date startTime;
    /** 结束时间 */
    private Date endTime;
    /** 会话状态：STARTED/INTERRUPTED/COMPLETED */
    private String status;
    /** 最后心跳时间 */
    private Date lastHeartbeatTime;
    /** 是否伴随敲击（0/1） */
    private Integer withKnock;
    /** 敲击频率（次/分钟） */
    private Integer knockFrequency;
    /** 冥想心情 */
    private String moodCode;
    /** 冥想领悟 */
    private String insightText;
    /** 是否分享：0-否，1-是 */
    private Integer shareFlag;
    /** 自动生成昵称 */
    private String nicknameGenerated;
    /** 自动生成功德标签 */
    private String meritTag;
    /** 配置快照 JSON */
    private String configSnapshot;
    /** 是否保存（0/1） */
    private Integer saveFlag;
    /** 会话预扣功德币 */
    private Integer coinCost;
    /** 已退还功德币 */
    private Integer coinRefunded;
    /** 支付状态 */
    private String paymentStatus;

}
