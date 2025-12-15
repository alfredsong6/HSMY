package com.hsmy.controller.merit;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.service.MeritLevelService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.MeritLevelProgressVO;
import com.hsmy.vo.MeritLevelStatusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Merit level query APIs.
 */
@RestController
@RequestMapping("/merit/levels")
@ApiVersion(ApiVersionConstant.V1_1)
@RequiredArgsConstructor
public class MeritLevelController {

    private final MeritLevelService meritLevelService;

    /**
     * 获取当前登录用户的功德等级信息（基于userStats.total_merit）.
     */
    @GetMapping("/current")
    public Result<MeritLevelStatusVO> getCurrentUserLevel() {
        Long userId = UserContextUtil.requireCurrentUserId();
        return Result.success(meritLevelService.getUserCurrentLevel(userId));
    }

    /**
     * 获取指定用户的功德等级信息（基于userStats.total_merit）.
     */
    @GetMapping("/current/{userId}")
    public Result<MeritLevelStatusVO> getUserLevel(@PathVariable Long userId) {
        return Result.success(meritLevelService.getUserCurrentLevel(userId));
    }

    /**
     * 查询当前用户的全部功德等级列表及完成状态.
     */
    @GetMapping("/progress")
    public Result<MeritLevelProgressVO> getCurrentUserLevelProgress() {
        Long userId = UserContextUtil.requireCurrentUserId();
        return Result.success(meritLevelService.getUserLevelProgress(userId));
    }

    /**
     * 查询指定用户的全部功德等级列表及完成状态.
     */
    @GetMapping("/progress/{userId}")
    public Result<MeritLevelProgressVO> getUserLevelProgress(@PathVariable Long userId) {
        return Result.success(meritLevelService.getUserLevelProgress(userId));
    }
}
