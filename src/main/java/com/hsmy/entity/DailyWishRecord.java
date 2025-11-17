package com.hsmy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hsmy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 每日愿望记录实体
 *
 * @author HSMY
 * @date 2025/11/17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_daily_wish_record")
public class DailyWishRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 创建用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 愿望内容
     */
    @TableField("wish_content")
    private String wishContent;

    /**
     * 姓名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 生辰
     */
    @TableField("birth_time")
    private LocalDateTime birthTime;

    /**
     * 愿望创建时间
     */
    @TableField("wish_time")
    private LocalDateTime wishTime;
}
