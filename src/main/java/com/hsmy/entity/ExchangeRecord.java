package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 功德兑换记录实体类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_exchange_record")
public class ExchangeRecord extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 使用功德值
     */
    private Long meritUsed;
    
    /**
     * 获得功德币
     */
    private Integer meritCoinsGained;
    
    /**
     * 兑换比例：功德值/功德币
     */
    private Integer exchangeRate;
    
    /**
     * 兑换时间
     */
    private Date exchangeTime;
}