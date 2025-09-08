package com.hsmy.service;

import com.hsmy.entity.Task;
import com.hsmy.entity.UserTask;

import java.time.LocalDate;
import java.util.List;

/**
 * 任务Service接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
public interface TaskService {
    
    /**
     * 根据任务类型获取任务列表
     * 
     * @param taskType 任务类型
     * @return 任务列表
     */
    List<Task> getTasksByType(String taskType);
    
    /**
     * 获取所有启用的任务
     * 
     * @return 任务列表
     */
    List<Task> getAllActiveTasks();
    
    /**
     * 获取每日任务列表
     * 
     * @return 每日任务列表
     */
    List<Task> getDailyTasks();
    
    /**
     * 获取每周任务列表
     * 
     * @return 每周任务列表
     */
    List<Task> getWeeklyTasks();
    
    /**
     * 根据用户ID和日期获取用户任务
     * 
     * @param userId 用户ID
     * @param taskDate 任务日期
     * @return 用户任务列表
     */
    List<UserTask> getUserTasks(Long userId, LocalDate taskDate);
    
    /**
     * 获取用户今日任务
     * 
     * @param userId 用户ID
     * @return 用户任务列表
     */
    List<UserTask> getUserTodayTasks(Long userId);
    
    /**
     * 更新用户任务进度
     * 
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param progress 进度值
     * @return 是否有任务完成
     */
    Boolean updateTaskProgress(Long userId, String targetType, Integer progress);
    
    /**
     * 完成任务
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param taskDate 任务日期
     * @return 是否成功
     */
    Boolean completeTask(Long userId, Long taskId, LocalDate taskDate);
    
    /**
     * 领取任务奖励
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param taskDate 任务日期
     * @return 是否成功
     */
    Boolean claimTaskReward(Long userId, Long taskId, LocalDate taskDate);
    
    /**
     * 初始化用户每日任务
     * 
     * @param userId 用户ID
     * @param taskDate 任务日期
     * @return 是否成功
     */
    Boolean initUserDailyTasks(Long userId, LocalDate taskDate);
    
    /**
     * 刷新用户任务
     * 
     * @param userId 用户ID
     * @param refreshType 刷新类型
     * @return 是否成功
     */
    Boolean refreshUserTasks(Long userId, String refreshType);
}