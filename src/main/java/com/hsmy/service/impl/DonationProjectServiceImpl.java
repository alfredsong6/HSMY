package com.hsmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hsmy.entity.DonationProject;
import com.hsmy.mapper.DonationProjectMapper;
import com.hsmy.service.DonationProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 捐赠项目Service实现类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Service
@RequiredArgsConstructor
public class DonationProjectServiceImpl implements DonationProjectService {
    
    private final DonationProjectMapper donationProjectMapper;
    
    @Override
    public List<DonationProject> getProjectsByType(String projectType) {
        LambdaQueryWrapper<DonationProject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DonationProject::getProjectType, projectType);
        queryWrapper.eq(DonationProject::getStatus, 1); // 只查询进行中的项目
        queryWrapper.orderByDesc(DonationProject::getCreateTime);
        
        return donationProjectMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<DonationProject> getProjectsByStatus(Integer status) {
        LambdaQueryWrapper<DonationProject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DonationProject::getStatus, status);
        queryWrapper.orderByDesc(DonationProject::getCreateTime);
        
        return donationProjectMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<DonationProject> getActiveProjects() {
        return getProjectsByStatus(1);
    }
    
    @Override
    public DonationProject getProjectById(Long projectId) {
        return donationProjectMapper.selectById(projectId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProjectAmount(Long projectId, Long amount) {
        try {
            DonationProject project = getProjectById(projectId);
            if (project == null) {
                return false;
            }
            
            project.setCurrentAmount(project.getCurrentAmount() + amount);
            project.setDonorCount(project.getDonorCount() + 1);
            
            return donationProjectMapper.updateById(project) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public Boolean canDonate(Long projectId) {
        DonationProject project = getProjectById(projectId);
        if (project == null) {
            return false;
        }
        
        // 检查项目状态是否为进行中
        if (project.getStatus() != 1) {
            return false;
        }
        
        // 检查项目是否过期
        Date now = new Date();
        if (project.getEndTime() != null && now.after(project.getEndTime())) {
            return false;
        }
        
        // 检查是否达到目标金额
        if (project.getTargetAmount() != null && 
            project.getCurrentAmount() >= project.getTargetAmount()) {
            return false;
        }
        
        return true;
    }
}