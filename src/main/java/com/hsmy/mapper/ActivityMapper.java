package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.domain.activity.ActivityDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 活动 mapper.
 */
@Mapper
public interface ActivityMapper extends BaseMapper<ActivityDomain> {

    /**
     * 查询活动列表.
     *
     * @param activityType 活动类型
     * @param status 状态
     * @param currentTime 当前时间（只返回在有效期内的数据时使用）
     * @return 活动集合
     */
    List<ActivityDomain> selectActivityList(@Param("activityType") String activityType,
                                            @Param("status") Integer status,
                                            @Param("currentTime") Date currentTime);
}

