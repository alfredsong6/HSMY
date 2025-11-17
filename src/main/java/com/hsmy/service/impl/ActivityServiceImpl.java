package com.hsmy.service.impl;

import com.hsmy.domain.activity.ActivityDomain;
import com.hsmy.mapper.ActivityMapper;
import com.hsmy.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 活动 Service 实现.
 */
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;

    @Override
    public List<ActivityDomain> listActivities(String activityType, Integer status, Boolean onlyAvailable) {
        String normalizedType = StringUtils.hasText(activityType) ? activityType.trim() : null;
        Date now = Boolean.TRUE.equals(onlyAvailable) ? new Date() : null;
        return activityMapper.selectActivityList(normalizedType, status, now);
    }
}

