package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户关系实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_relation")
public class UserRelation extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 目标用户ID
     */
    private Long targetUserId;
    
    /**
     * 关系类型：1-关注，2-好友，3-拉黑
     */
    private Integer relationType;
    
    /**
     * 建立关系时间
     */
    private Date relationTime;
}