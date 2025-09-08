package com.hsmy.service.impl;

import com.hsmy.entity.Task;
import com.hsmy.entity.UserTask;
import com.hsmy.mapper.TaskMapper;
import com.hsmy.mapper.UserTaskMapper;
import com.hsmy.service.TaskService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 任务Service实现类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    
    private final TaskMapper taskMapper;
    private final UserTaskMapper userTaskMapper;
    
    @Override
    public List<Task> getTasksByType(String taskType) {
        return taskMapper.selectByType(taskType);
    }
    
    @Override
    public List<Task> getAllActiveTasks() {
        return taskMapper.selectAllActive();
    }
    
    @Override
    public List<Task> getDailyTasks() {
        return getTasksByType("daily");
    }
    
    @Override
    public List<Task> getWeeklyTasks() {
        return getTasksByType("weekly");
    }
    
    @Override
    public List<UserTask> getUserTasks(Long userId, LocalDate taskDate) {
        return userTaskMapper.selectByUserIdAndDate(userId, taskDate);
    }
    
    @Override
    public List<UserTask> getUserTodayTasks(Long userId) {
        return getUserTasks(userId, LocalDate.now());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTaskProgress(Long userId, String targetType, Integer progress) {
        LocalDate today = LocalDate.now();
        // 获取该目标类型的所有任务
        List<Task> tasks = taskMapper.selectAllActive();
        boolean hasTaskCompleted = false;
        
        for (Task task : tasks) {
            if (!task.getTargetType().equals(targetType)) {
                continue;
            }
            
            // 检查用户今日该任务记录
            UserTask userTask = userTaskMapper.selectByUserTaskAndDate(userId, task.getId(), today);
            
            if (userTask == null) {
                // 创建新的任务记录
                userTask = new UserTask();
                userTask.setId(IdGenerator.nextId());
                userTask.setUserId(userId);
                userTask.setTaskId(task.getId());
                userTask.setTaskDate(convertLocalDateToDate(today));
                userTask.setProgress(progress);
                userTask.setIsCompleted(0);
                userTask.setIsClaimed(0);
                userTaskMapper.insert(userTask);
            } else if (userTask.getIsCompleted() == 0) {
                // 更新进度
                userTaskMapper.updateProgress(userId, task.getId(), today, progress);
            }
            
            // 检查是否完成任务
            if (userTask.getIsCompleted() == 0 && progress >= task.getTargetValue()) {
                userTaskMapper.completeTask(userId, task.getId(), today);
                hasTaskCompleted = true;
            }
        }
        
        return hasTaskCompleted;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeTask(Long userId, Long taskId, LocalDate taskDate) {
        return userTaskMapper.completeTask(userId, taskId, taskDate) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean claimTaskReward(Long userId, Long taskId, LocalDate taskDate) {
        UserTask userTask = userTaskMapper.selectByUserTaskAndDate(userId, taskId, taskDate);
        if (userTask == null || userTask.getIsCompleted() == 0) {
            throw new RuntimeException("任务未完成，无法领取奖励");
        }
        
        if (userTask.getIsClaimed() == 1) {
            throw new RuntimeException("奖励已领取");
        }
        
        // 标记奖励已领取
        userTask.setIsClaimed(1);
        userTask.setClaimTime(new Date());
        
        // TODO: 实现具体的奖励发放逻辑
        
        return userTaskMapper.updateById(userTask) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean initUserDailyTasks(Long userId, LocalDate taskDate) {
        // 获取所有每日任务
        List<Task> dailyTasks = getDailyTasks();
        
        for (Task task : dailyTasks) {
            // 检查用户是否已有该任务记录
            UserTask existingTask = userTaskMapper.selectByUserTaskAndDate(userId, task.getId(), taskDate);
            if (existingTask == null) {
                // 创建新的任务记录
                UserTask userTask = new UserTask();
                userTask.setId(IdGenerator.nextId());
                userTask.setUserId(userId);
                userTask.setTaskId(task.getId());
                userTask.setTaskDate(convertLocalDateToDate(taskDate));
                userTask.setProgress(0);
                userTask.setIsCompleted(0);
                userTask.setIsClaimed(0);
                userTaskMapper.insert(userTask);
            }
        }
        
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean refreshUserTasks(Long userId, String refreshType) {
        LocalDate today = LocalDate.now();
        
        switch (refreshType) {
            case "daily":
                return initUserDailyTasks(userId, today);
            case "weekly":
                // TODO: 实现周任务刷新逻辑
                break;
            default:
                throw new RuntimeException("不支持的刷新类型：" + refreshType);
        }
        
        return true;
    }
    
    /**
     * 将LocalDate转换为Date
     */
    private Date convertLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant());
    }
}