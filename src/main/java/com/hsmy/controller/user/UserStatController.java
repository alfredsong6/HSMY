package com.hsmy.controller.user;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.entity.UserStats;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户统计查询接口.
 */
@RestController
@RequestMapping("/user/stats")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class UserStatController {

    private final UserStatsMapper userStatsMapper;

    /**
     * 获取当前登录用户的统计信息.
     */
    @GetMapping("/self")
    public Result<UserStats> getCurrentUserStats() {
        Long userId = UserContextUtil.requireCurrentUserId();
        return Result.success(resolveUserStats(userId));
    }

    /**
     * 获取指定用户的统计信息.
     */
    @GetMapping("/{userId}")
    public Result<UserStats> getUserStats(@PathVariable Long userId) {
        return Result.success(resolveUserStats(userId));
    }

    private UserStats resolveUserStats(Long userId) {
        UserStats stats = userStatsMapper.selectByUserId(userId);
        if (stats == null) {
            stats = new UserStats();
            stats.setUserId(userId);
            stats.setTotalMerit(0L);
            stats.setMeritCoins(0L);
            stats.setTotalKnocks(0L);
            stats.setConsecutiveDays(0);
            stats.setTotalLoginDays(0);
            stats.setCurrentLevel(1);
            stats.setMaxCombo(0);
        }
        if (stats.getTotalMerit() == null) {
            stats.setTotalMerit(0L);
        }
        if (stats.getMeritCoins() == null) {
            stats.setMeritCoins(0L);
        }
        if (stats.getTotalKnocks() == null) {
            stats.setTotalKnocks(0L);
        }
        if (stats.getConsecutiveDays() == null) {
            stats.setConsecutiveDays(0);
        }
        if (stats.getTotalLoginDays() == null) {
            stats.setTotalLoginDays(0);
        }
        if (stats.getCurrentLevel() == null) {
            stats.setCurrentLevel(1);
        }
        if (stats.getMaxCombo() == null) {
            stats.setMaxCombo(0);
        }
        return stats;
    }
}
