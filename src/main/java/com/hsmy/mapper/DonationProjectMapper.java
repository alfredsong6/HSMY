package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.DonationProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 捐赠项目Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Mapper
public interface DonationProjectMapper extends BaseMapper<DonationProject> {
    
    /**
     * 查询进行中的项目
     * 
     * @return 项目列表
     */
    List<DonationProject> selectActiveProjects();
    
    /**
     * 更新项目募集金额
     * 
     * @param projectId 项目ID
     * @param amount 金额
     * @return 影响行数
     */
    int updateCurrentAmount(@Param("projectId") Long projectId, @Param("amount") Integer amount);
    
    /**
     * 增加捐赠人数
     * 
     * @param projectId 项目ID
     * @return 影响行数
     */
    int incrementDonorCount(@Param("projectId") Long projectId);
}