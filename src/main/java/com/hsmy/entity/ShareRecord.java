package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 分享记录实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_share_record")
public class ShareRecord extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 分享类型：achievement-成就，ranking-排名，invite-邀请
     */
    private String shareType;
    
    /**
     * 分享平台：wechat-微信，qq-QQ，weibo-微博，link-链接
     */
    private String sharePlatform;
    
    /**
     * 分享内容
     */
    private String shareContent;
    
    /**
     * 分享链接
     */
    private String shareUrl;
    
    /**
     * 分享时间
     */
    private Date shareTime;
    
    /**
     * 获得功德奖励
     */
    private Integer rewardMerit;
}