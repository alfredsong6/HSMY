package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    
    /**
     * 根据任务类型查询任务列表
     * 
     * @param taskType 任务类型
     * @return 任务列表
     */
    List<Task> selectByType(@Param("taskType") String taskType);
    
    /**
     * 查询所有启用的任务
     * 
     * @return 任务列表
     */
    List<Task> selectAllActive();
    
    /**
     * 根据刷新类型查询任务
     * 
     * @param refreshType 刷新类型
     * @return 任务列表
     */
    List<Task> selectByRefreshType(@Param("refreshType") String refreshType);
}