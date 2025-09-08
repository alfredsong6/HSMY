package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.UserTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户任务Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Mapper
public interface UserTaskMapper extends BaseMapper<UserTask> {
    
    /**
     * 根据用户ID和日期查询任务
     * 
     * @param userId 用户ID
     * @param taskDate 任务日期
     * @return 用户任务列表
     */
    List<UserTask> selectByUserIdAndDate(@Param("userId") Long userId, @Param("taskDate") LocalDate taskDate);
    
    /**
     * 根据用户ID、任务ID和日期查询
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param taskDate 任务日期
     * @return 用户任务信息
     */
    UserTask selectByUserTaskAndDate(@Param("userId") Long userId, @Param("taskId") Long taskId, @Param("taskDate") LocalDate taskDate);
    
    /**
     * 更新任务进度
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param taskDate 任务日期
     * @param progress 进度
     * @return 影响行数
     */
    int updateProgress(@Param("userId") Long userId, @Param("taskId") Long taskId, @Param("taskDate") LocalDate taskDate, @Param("progress") Integer progress);
    
    /**
     * 完成任务
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param taskDate 任务日期
     * @return 影响行数
     */
    int completeTask(@Param("userId") Long userId, @Param("taskId") Long taskId, @Param("taskDate") LocalDate taskDate);
    
    /**
     * 查询用户今日完成的任务
     * 
     * @param userId 用户ID
     * @param taskDate 任务日期
     * @return 已完成任务列表
     */
    List<UserTask> selectCompletedByUserIdAndDate(@Param("userId") Long userId, @Param("taskDate") LocalDate taskDate);
}