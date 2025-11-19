package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户道具实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_item")
public class UserItem extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 道具ID
     */
    private Long itemId;
    
    /**
     * 购买时间
     */
    private Date purchaseTime;
    
    /**
     * 购买价格（功德币）
     */
    private Integer purchasePrice;

    /**
     * 剩余可用次数
     */
    private Integer remainingUses;

    /**
     * 状态：0-未激活，1-使用中，2-已用完，3-已过期
     */
    private Integer usageStatus;

    /**
     * 最近一次使用时间
     */
    private Date lastUsedTime;

    /**
     * 叠加数量
     */
    private Integer stackCount;

    /**
     * 道具来源：1-商城，2-活动，3-任务等
     */
    private Integer sourceType;

    /**
     * 扩展信息（JSON）
     */
    private String metadata;

    /**
     * 是否装备：0-否，1-是
     */
    private Integer isEquipped;
    
    /**
     * 过期时间，NULL表示永久
     */
    private Date expireTime;
    
    /**
     * 关联的道具信息（用于联表查询）
     */
    @TableField(exist = false)
    private Item item;
}
