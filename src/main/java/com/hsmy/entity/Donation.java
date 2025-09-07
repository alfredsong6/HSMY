package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 捐赠记录实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_donation")
public class Donation extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 捐赠项目ID
     */
    private Long projectId;
    
    /**
     * 捐赠功德币数量
     */
    private Integer meritCoinsDonated;
    
    /**
     * 祈愿留言
     */
    private String message;
    
    /**
     * 是否匿名：0-否，1-是
     */
    private Integer isAnonymous;
    
    /**
     * 捐赠时间
     */
    private Date donationTime;
}