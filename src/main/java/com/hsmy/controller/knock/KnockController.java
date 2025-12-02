package com.hsmy.controller.knock;

import com.hsmy.common.Result;
import com.hsmy.exception.BusinessException;
import com.hsmy.service.KnockService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.AutoKnockHeartbeatVO;
import com.hsmy.vo.AutoKnockStartVO;
import com.hsmy.vo.AutoKnockStopVO;
import com.hsmy.vo.KnockVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Map;

/**
 * 敲击功能Controller
 *
 * @author HSMY
 * @date 2025/09/08
 */
@Slf4j
@RestController
@RequestMapping("/knock")
@RequiredArgsConstructor
public class KnockController {

    private final KnockService knockService;

    /**
     * 手动敲击
     *
     * @param knockVO 敲击参数
     * @param request HTTP请求
     * @return 敲击结果
     */
    @PostMapping("/manual")
    public Result<Map<String, Object>> manualKnock(@Validated @RequestBody KnockVO knockVO, HttpServletRequest request) {
        if (knockVO == null || knockVO.getRequestId() == null) {
            throw new BusinessException("参数错误");
        }
        // 获取用户ID
        Long userId = UserContextUtil.requireCurrentUserId();
        knockVO.setUserId(userId);
        knockVO.setSessionId(knockVO.getRequestId());
        knockVO.setKnockMode("MANUAL");

        // 调用服务层处理业务逻辑
        knockService.manualKnock(knockVO);

        return Result.success();
    }

    /**
     * 开始自动敲击
     *
     * @param startVO 自动敲击开始请求参数
     * @param request HTTP请求
     * @return 操作结果
     */
    //@PostMapping("/auto/start")
    public Result<Map<String, Object>> startAutoKnock(@Validated @RequestBody AutoKnockStartVO startVO,
                                                      HttpServletRequest request) {
        try {
            // 获取用户ID
            Long userId = UserContextUtil.requireCurrentUserId();

            // 调用服务层处理业务逻辑
            Map<String, Object> result = knockService.startAutoKnock(userId, startVO);

            return Result.success("自动敲击已开始", result);
        } catch (Exception e) {
            log.error("开始自动敲击失败", e);
            return Result.error("开始自动敲击失败：" + e.getMessage());
        }
    }

    /**
     * 停止自动敲击
     *
     * @param stopVO 自动敲击停止请求参数
     * @param request HTTP请求
     * @return 操作结果
     */
    //@PostMapping("/auto/stop")
    public Result<Map<String, Object>> stopAutoKnock(@Validated @RequestBody AutoKnockStopVO stopVO, HttpServletRequest request) {
        try {
            // 获取用户ID
            Long userId = UserContextUtil.requireCurrentUserId();

            // 调用服务层处理业务逻辑
            Map<String, Object> result = knockService.stopAutoKnock(userId, stopVO);

            return Result.success("自动敲击已停止", result);
        } catch (Exception e) {
            log.error("停止自动敲击失败", e);
            return Result.error("停止自动敲击失败：" + e.getMessage());
        }
    }

    /**
     * 自动敲击心跳检测
     *
     * @param heartbeatVO 心跳检测请求参数
     * @param request HTTP请求
     * @return 操作结果
     */
    //@PostMapping("/auto/heartbeat")
    public Result<Map<String, Object>> autoKnockHeartbeat(@Validated @RequestBody AutoKnockHeartbeatVO heartbeatVO, HttpServletRequest request) {
        try {
            // 获取用户ID
            Long userId = UserContextUtil.requireCurrentUserId();

            // 调用服务层处理业务逻辑
            Map<String, Object> result = knockService.heartbeat(userId, heartbeatVO);

            return Result.success("心跳更新成功", result);
        } catch (Exception e) {
            log.error("心跳更新失败", e);
            return Result.error("心跳更新失败：" + e.getMessage());
        }
    }

    /**
     * 定时检查并自动结算超时的会话
     * 每分钟执行一次，剔除超过1分钟没有心跳的会话
     */
    //@Scheduled(fixedDelay = 60000) // 每1分钟检查一次
    public void checkAndSettleExpiredSessions() {
        knockService.cleanupTimeoutSessions();
    }

    /**
     * 获取敲击统计
     *
     * @return 统计数据
     */
    @GetMapping("/stats/periods")
    public Result<Map<String, Object>> getKnockPeriodStats(@RequestParam(value = "referenceDate", required = false)
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            Map<String, Object> stats = knockService.getKnockPeriodStats(userId, referenceDate);
            return Result.success("获取周期统计成功", stats);
        } catch (Exception e) {
            log.error("获取周期敲击统计失败", e);
            return Result.error("获取周期统计失败：" + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getKnockStats(HttpServletRequest request) {
        try {
            // 获取用户ID
            Long userId = UserContextUtil.requireCurrentUserId();

            // 调用服务层处理业务逻辑
            Map<String, Object> stats = knockService.getKnockStats(userId);

            return Result.success("获取统计成功", stats);
        } catch (Exception e) {
            log.error("获取敲击统计失败", e);
            return Result.error("获取统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前自动敲击状态
     *
     * @param request HTTP请求
     * @return 自动敲击状态
     */
    @GetMapping("/auto/status")
    public Result<Map<String, Object>> getAutoKnockStatus(HttpServletRequest request) {
        try {
            // 获取用户ID
            Long userId = UserContextUtil.requireCurrentUserId();

            // 调用服务层处理业务逻辑
            Map<String, Object> status = knockService.getAutoKnockStatus(userId);

            return Result.success("获取状态成功", status);
        } catch (Exception e) {
            log.error("获取自动敲击状态失败", e);
            return Result.error("获取状态失败：" + e.getMessage());
        }
    }
}
