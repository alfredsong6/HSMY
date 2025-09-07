package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 捐赠项目实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_donation_project")
public class DonationProject extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 项目名称
     */
    private String projectName;
    
    /**
     * 项目类型：temple-庙宇建设，release-放生，education-助学，environment-环保
     */
    private String projectType;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 目标金额（功德币）
     */
    private Long targetAmount;
    
    /**
     * 当前募集金额（功德币）
     */
    private Long currentAmount;
    
    /**
     * 捐赠人数
     */
    private Integer donorCount;
    
    /**
     * 状态：0-已结束，1-进行中，2-已完成
     */
    private Integer status;
    
    /**
     * 开始时间
     */
    private Date startTime;
    
    /**
     * 结束时间
     */
    private Date endTime;
    
    /**
     * 项目图片URL
     */
    private String imageUrl;
}