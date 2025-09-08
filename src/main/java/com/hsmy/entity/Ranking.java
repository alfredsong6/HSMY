package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 排行榜快照实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_ranking")
public class Ranking extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 榜单类型：daily-日榜，weekly-周榜，monthly-月榜，total-总榜
     */
    private String rankType;
    
    /**
     * 功德值
     */
    private Long meritValue;
    
    /**
     * 排名
     */
    private Integer rankingPosition;
    
    /**
     * 快照日期
     */
    private Date snapshotDate;
    
    /**
     * 统计周期：如2025-01表示月榜，2025-W01表示周榜
     */
    private String period;
    
    /**
     * 关联的用户信息（用于联表查询）
     */
    @TableField(exist = false)
    private User user;
}