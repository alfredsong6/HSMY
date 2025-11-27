package com.hsmy.controller.wish;

import com.hsmy.common.Result;
import com.hsmy.service.DailyWishRecordService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.DailyWishRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 每日愿望记录Controller
 *
 * @author HSMY
 * @date 2025/11/17
 */
@RestController
@RequestMapping("/wish/daily-records")
@RequiredArgsConstructor
public class DailyWishRecordController {

    private final DailyWishRecordService dailyWishRecordService;

    /**
     * 创建每日愿望记录
     *
     * @param dailyWishRecordVO 愿望信息
     * @return 创建结果
     */
    @PostMapping
    public Result<Map<String, Object>> createDailyWish(@Validated @RequestBody DailyWishRecordVO dailyWishRecordVO) {
        Long userId = UserContextUtil.requireCurrentUserId();

        boolean success = dailyWishRecordService.createDailyWish(userId, dailyWishRecordVO);
        if (success) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "愿望记录创建成功");
            return Result.success("创建成功", result);
        }
        return Result.error("创建失败");
    }

    /**
     * 查询每日愿望记录
     *
     * @return 愿望记录列表
     */
    @GetMapping
    public Result<List<DailyWishRecordVO>> listDailyWishes() {
        Long userId = UserContextUtil.requireCurrentUserId();
        List<DailyWishRecordVO> records = dailyWishRecordService.listDailyWishes(userId);
        return Result.success(records);
    }

    /**
     * 查看当天愿望记录
     *
     * @return 当天愿望记录
     */
    @GetMapping("/today")
    public Result<DailyWishRecordVO> getTodayDailyWish() {
        Long userId = UserContextUtil.requireCurrentUserId();
        DailyWishRecordVO record = dailyWishRecordService.getTodayDailyWish(userId);
        return Result.success(record);
    }

    /**
     * 获取最近一次愿望记录
     *
     * @return 最近愿望
     */
    @GetMapping("/last")
    public Result<DailyWishRecordVO> getLastDailyWish() {
        Long userId = UserContextUtil.requireCurrentUserId();
        DailyWishRecordVO record = dailyWishRecordService.getLastDailyWish(userId);
        return Result.success(record);
    }
}
