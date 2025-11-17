package com.hsmy.service;

import com.hsmy.domain.activity.ActivityDomain;

import java.util.List;

/**
 * 活动 Service.
 */
public interface ActivityService {

    /**
     * 查询活动列表.
     *
     * @param activityType 活动类型
     * @param status 状态
     * @param onlyAvailable 是否只返回当前有效活动
     * @return 活动集合
     */
    List<ActivityDomain> listActivities(String activityType, Integer status, Boolean onlyAvailable);
}

