package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 道具实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_item")
public class Item extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 道具名称
     */
    private String itemName;
    
    /**
     * 道具类型：skin-皮肤，sound-音效，background-背景，frame-头像框
     */
    private String itemType;
    
    /**
     * 道具分类：classic-经典，festival-节日，premium-高级
     */
    private String category;
    
    /**
     * 价格（功德币）
     */
    private Integer price;
    
    /**
     * 原价（功德币）
     */
    private Integer originalPrice;
    
    /**
     * 道具描述
     */
    private String description;
    
    /**
     * 预览图URL
     */
    private String previewUrl;
    
    /**
     * 资源文件URL
     */
    private String resourceUrl;
    
    /**
     * 是否限定：0-否，1-是
     */
    private Integer isLimited;
    
    /**
     * 限定开始时间
     */
    private Date limitTimeStart;
    
    /**
     * 限定结束时间
     */
    private Date limitTimeEnd;
    
    /**
     * 库存数量，-1表示无限
     */
    private Integer stock;
    
    /**
     * 已售数量
     */
    private Integer soldCount;
    
    /**
     * 排序序号
     */
    private Integer sortOrder;
    
    /**
     * 是否上架：0-下架，1-上架
     */
    private Integer isActive;

    /**
     * 使用模式：0-永久，1-限时可重复，2-一次性/限次
     */
    private Integer usageMode;

    /**
     * 有效时长（小时），仅限时道具生效
     */
    private Integer durationHours;

    /**
     * 最大使用次数，-1 表示不限
     */
    private Integer maxUses;

    /**
     * 是否允许叠加购买
     */
    private Integer stackable;

    /**
     * 冷却时间（秒）
     */
    private Integer cooldownSeconds;

    /**
     * 过期策略：0-无，1-按duration，2-按活动窗口
     */
    private Integer autoExpireType;
}
