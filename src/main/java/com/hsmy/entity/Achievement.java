package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 成就定义实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_achievement")
public class Achievement extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 成就名称
     */
    private String achievementName;
    
    /**
     * 成就类型：knock-敲击，login-登录，merit-功德，social-社交，donate-捐赠
     */
    private String achievementType;
    
    /**
     * 成就等级：1-铜，2-银，3-金，4-钻石
     */
    private Integer achievementLevel;
    
    /**
     * 成就描述
     */
    private String description;
    
    /**
     * 成就图标URL
     */
    private String iconUrl;
    
    /**
     * 条件类型：count-次数，amount-数量，consecutive-连续
     */
    private String conditionType;
    
    /**
     * 条件值
     */
    private Long conditionValue;
    
    /**
     * 奖励类型：merit-功德，merit_coin-功德币，item-道具
     */
    private String rewardType;
    
    /**
     * 奖励内容
     */
    private String rewardValue;
    
    /**
     * 排序序号
     */
    private Integer sortOrder;
    
    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer isActive;
}