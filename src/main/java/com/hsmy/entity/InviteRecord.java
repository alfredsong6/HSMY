package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 邀请记录实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_invite_record")
public class InviteRecord extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 邀请人ID
     */
    private Long inviterId;
    
    /**
     * 被邀请人ID
     */
    private Long inviteeId;
    
    /**
     * 邀请码
     */
    private String inviteCode;
    
    /**
     * 邀请时间
     */
    private Date inviteTime;
    
    /**
     * 注册时间
     */
    private Date registerTime;
    
    /**
     * 是否成功：0-待注册，1-已注册
     */
    private Integer isSuccess;
    
    /**
     * 邀请人奖励功德值
     */
    private Integer inviterReward;
    
    /**
     * 被邀请人奖励功德值
     */
    private Integer inviteeReward;
}