package com.hsmy.entity.meditation;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 冥想用户偏好实体
 */
@Data
@TableName("t_meditation_user_pref")
public class MeditationUserPref {

    @TableId
    private Long userId;
    private Integer defaultDuration;
    private Integer defaultWithKnock;
    private Integer defaultKnockFrequency;
    private Date lastUpdateTime;
}
