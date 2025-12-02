package com.hsmy.controller.meditation;

import com.hsmy.common.Result;
import com.hsmy.service.MeditationService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.meditation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/meditation")
@RequiredArgsConstructor
@Validated
public class MeditationController {

    private final MeditationService meditationService;

    @PostMapping("/subscription/purchase")
    public Result<MeditationSubscriptionStatusVO> purchase(@Valid @RequestBody MeditationSubscriptionPurchaseVO purchaseVO) {
        Long userId = UserContextUtil.requireCurrentUserId();
        MeditationSubscriptionStatusVO statusVO = meditationService.purchaseSubscription(userId, purchaseVO);
        return Result.success("购买成功", statusVO);
    }

    @GetMapping("/subscription/status")
    public Result<MeditationSubscriptionStatusVO> subscriptionStatus() {
        Long userId = UserContextUtil.requireCurrentUserId();
        MeditationSubscriptionStatusVO statusVO = meditationService.getSubscriptionStatus(userId);
        return Result.success(statusVO);
    }

    @PostMapping("/session/ping")
    public Result<Boolean> ping(@Valid @RequestBody MeditationSessionPingVO pingVO) {
        Long userId = UserContextUtil.requireCurrentUserId();
        meditationService.pingSession(userId, pingVO);
        return Result.success(true);
    }

    @PostMapping("/session/abnormal/settle")
    public Result<Integer> settleAbnormalSessions() {
        Long userId = UserContextUtil.requireCurrentUserId();
        Integer settled = meditationService.settleAbnormalSessions(userId);
        return Result.success(settled);
    }

    @PostMapping("/session/start")
    public Result<MeditationSessionStartResponse> startSession(@Valid @RequestBody MeditationSessionStartVO startVO) {
        Long userId = UserContextUtil.requireCurrentUserId();
        MeditationSessionStartResponse response = meditationService.startSession(userId, startVO);
        return Result.success("开始冥想", response);
    }

    @PostMapping("/session/finish")
    public Result<MeditationSessionFinishResponse> finishSession(@Valid @RequestBody MeditationSessionFinishVO finishVO) {
        Long userId = UserContextUtil.requireCurrentUserId();
        MeditationSessionFinishResponse response = meditationService.finishSession(userId, finishVO);
        return Result.success("冥想已保存", response);
    }

    //@PostMapping("/session/discard")
    public Result<Boolean> discardSession(@Valid @RequestBody MeditationSessionDiscardVO discardVO) {
        Long userId = UserContextUtil.requireCurrentUserId();
        meditationService.discardSession(userId, discardVO);
        return Result.success(true);
    }

    @GetMapping("/stats/summary")
    public Result<MeditationStatsSummaryVO> statsSummary() {
        Long userId = UserContextUtil.requireCurrentUserId();
        MeditationStatsSummaryVO summaryVO = meditationService.getStatsSummary(userId);
        return Result.success(summaryVO);
    }

    @GetMapping("/stats/month")
    public Result<List<MeditationMonthViewVO>> monthStats(@RequestParam(required = false) String month) {
        Long userId = UserContextUtil.requireCurrentUserId();
        List<MeditationMonthViewVO> data = meditationService.getMonthStats(userId, month);
        return Result.success(data);
    }

    @GetMapping("/config/default")
    public Result<MeditationUserPrefVO> getDefaultConfig() {
        Long userId = UserContextUtil.requireCurrentUserId();
        MeditationUserPrefVO vo = meditationService.getUserPreference(userId);
        return Result.success(vo);
    }

    @PutMapping("/config/default")
    public Result<MeditationUserPrefVO> updateDefaultConfig(@Valid @RequestBody MeditationUserPrefVO prefVO) {
        Long userId = UserContextUtil.requireCurrentUserId();
        MeditationUserPrefVO vo = meditationService.updateUserPreference(userId, prefVO);
        return Result.success("配置已更新", vo);
    }
}
