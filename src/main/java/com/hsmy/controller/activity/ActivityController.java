package com.hsmy.controller.activity;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.domain.activity.ActivityDomain;
import com.hsmy.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 活动接口.
 */
@RestController
@RequestMapping("/activity")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /**
     * 获取活动列表.
     *
     * @param activityType 活动类型
     * @param status 活动状态
     * @param onlyAvailable 是否仅返回当前有效活动
     * @return 活动集合
     */
    @GetMapping("/list")
    public Result<List<ActivityDomain>> listActivities(
            @RequestParam(required = false) String activityType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "true") Boolean onlyAvailable) {
        try {
            List<ActivityDomain> activities = activityService.listActivities(activityType, status, onlyAvailable);
            return Result.success("查询成功", activities);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

