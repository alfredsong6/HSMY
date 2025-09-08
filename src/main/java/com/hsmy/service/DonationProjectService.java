package com.hsmy.service;

import com.hsmy.entity.DonationProject;

import java.util.List;

/**
 * 捐赠项目Service接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
public interface DonationProjectService {
    
    /**
     * 根据项目类型获取项目列表
     * 
     * @param projectType 项目类型
     * @return 项目列表
     */
    List<DonationProject> getProjectsByType(String projectType);
    
    /**
     * 根据状态获取项目列表
     * 
     * @param status 项目状态
     * @return 项目列表
     */
    List<DonationProject> getProjectsByStatus(Integer status);
    
    /**
     * 获取所有进行中的项目
     * 
     * @return 进行中的项目列表
     */
    List<DonationProject> getActiveProjects();
    
    /**
     * 根据ID获取项目详情
     * 
     * @param projectId 项目ID
     * @return 项目详情
     */
    DonationProject getProjectById(Long projectId);
    
    /**
     * 更新项目募集金额和捐赠人数
     * 
     * @param projectId 项目ID
     * @param amount 金额增量
     * @return 是否成功
     */
    Boolean updateProjectAmount(Long projectId, Long amount);
    
    /**
     * 检查项目是否可以接受捐赠
     * 
     * @param projectId 项目ID
     * @return 是否可以捐赠
     */
    Boolean canDonate(Long projectId);
}