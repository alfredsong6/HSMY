package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 排行榜奖励记录实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_ranking_reward")
public class RankingReward extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 榜单类型：daily-日榜，weekly-周榜，monthly-月榜
     */
    private String rankType;
    
    /**
     * 排名
     */
    private Integer rankingPosition;
    
    /**
     * 奖励类型：title-称号，skin-皮肤，frame-头像框，merit_coin-功德币
     */
    private String rewardType;
    
    /**
     * 奖励内容：道具ID或功德币数量
     */
    private String rewardValue;
    
    /**
     * 奖励发放时间
     */
    private Date rewardTime;
    
    /**
     * 是否已领取：0-未领取，1-已领取
     */
    private Integer isClaimed;
    
    /**
     * 领取时间
     */
    private Date claimTime;
    
    /**
     * 过期时间
     */
    private Date expireTime;
}