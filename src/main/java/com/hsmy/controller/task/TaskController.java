package com.hsmy.controller.task;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.entity.Task;
import com.hsmy.entity.UserTask;
import com.hsmy.service.TaskService;
import com.hsmy.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务Controller
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@RestController
@RequestMapping("/task")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    /**
     * 获取用户今日任务列表
     * 
     * @param request HTTP请求
     * @return 今日任务列表
     */
    @GetMapping("/daily")
    public Result<List<UserTask>> getDailyTasks(HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            List<UserTask> dailyTasks = taskService.getUserTodayTasks(userId);
            return Result.success(dailyTasks);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户任务列表
     * 
     * @param taskType 任务类型（可选）
     * @param request HTTP请求
     * @return 任务列表
     */
    @GetMapping("/list")
    public Result<List<UserTask>> getUserTasks(@RequestParam(required = false) String taskType,
                                              HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            List<UserTask> userTasks;
            if ("daily".equals(taskType)) {
                userTasks = taskService.getUserTodayTasks(userId);
            } else {
                userTasks = taskService.getUserTasks(userId, LocalDate.now());
            }
            
            return Result.success(userTasks);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 领取任务奖励
     * 
     * @param taskId 任务ID
     * @param request HTTP请求
     * @return 领取结果
     */
    @PostMapping("/claim")
    public Result<Map<String, Object>> claimTaskReward(@RequestParam Long taskId,
                                                      HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            boolean success = taskService.claimTaskReward(userId, taskId, LocalDate.now());
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("taskId", taskId);
                result.put("message", "任务奖励领取成功");
                return Result.success("领取成功", result);
            } else {
                return Result.error("领取失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取任务进度
     * 
     * @param taskId 任务ID
     * @param request HTTP请求
     * @return 任务进度
     */
    @GetMapping("/progress/{taskId}")
    public Result<Map<String, Object>> getTaskProgress(@PathVariable Long taskId,
                                                      HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            List<UserTask> userTasks = taskService.getUserTodayTasks(userId);
            UserTask targetTask = userTasks.stream()
                    .filter(task -> task.getTaskId().equals(taskId))
                    .findFirst()
                    .orElse(null);
            
            if (targetTask == null) {
                return Result.error("任务不存在");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("taskId", taskId);
            result.put("progress", targetTask.getProgress());
            result.put("targetValue", targetTask.getTask() != null ? targetTask.getTask().getTargetValue() : 0);
            result.put("isCompleted", targetTask.getIsCompleted());
            result.put("isClaimed", targetTask.getIsClaimed());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}