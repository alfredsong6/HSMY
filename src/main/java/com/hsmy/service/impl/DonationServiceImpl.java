package com.hsmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.entity.Donation;
import com.hsmy.entity.DonationProject;
import com.hsmy.entity.UserStats;
import com.hsmy.mapper.DonationMapper;
import com.hsmy.mapper.DonationProjectMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.DonationService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 捐赠Service实现类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {
    
    private final DonationMapper donationMapper;
    private final DonationProjectMapper donationProjectMapper;
    private final UserStatsMapper userStatsMapper;
    
    @Override
    public List<DonationProject> getDonationProjects(String projectType, Integer status) {
        LambdaQueryWrapper<DonationProject> queryWrapper = new LambdaQueryWrapper<>();
        
        if (projectType != null) {
            queryWrapper.eq(DonationProject::getProjectType, projectType);
        }
        
        if (status != null) {
            queryWrapper.eq(DonationProject::getStatus, status);
        } else {
            // 默认只查询进行中的项目
            queryWrapper.eq(DonationProject::getStatus, 1);
        }
        
        queryWrapper.orderByDesc(DonationProject::getCreateTime);
        
        return donationProjectMapper.selectList(queryWrapper);
    }
    
    @Override
    public DonationProject getProjectById(Long projectId) {
        return donationProjectMapper.selectById(projectId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean makeDonation(Long userId, Long projectId, Integer meritCoins, String message, Boolean isAnonymous) {
        try {
            // 1. 检查用户功德币余额
            if (!checkUserBalance(userId, meritCoins)) {
                throw new RuntimeException("功德币余额不足");
            }
            
            // 2. 检查项目是否存在且正在进行中
            DonationProject project = getProjectById(projectId);
            if (project == null || project.getStatus() != 1) {
                throw new RuntimeException("项目不存在或已结束");
            }
            
            // 3. 扣除用户功德币
            UserStats userStats = userStatsMapper.selectByUserId(userId);
            if (userStats == null || userStats.getMeritCoins() < meritCoins) {
                throw new RuntimeException("功德币余额不足");
            }
            
            userStats.setMeritCoins(userStats.getMeritCoins() - meritCoins);
            userStatsMapper.updateById(userStats);
            
            // 4. 创建捐赠记录
            Donation donation = new Donation();
            donation.setId(IdGenerator.nextId());
            donation.setUserId(userId);
            donation.setProjectId(projectId);
            donation.setMeritCoinsDonated(meritCoins);
            donation.setMessage(message);
            donation.setIsAnonymous(isAnonymous ? 1 : 0);
            donation.setDonationTime(new Date());
            
            donationMapper.insert(donation);
            
            // 5. 更新项目募集金额和捐赠人数
            project.setCurrentAmount(project.getCurrentAmount() + meritCoins);
            project.setDonorCount(project.getDonorCount() + 1);
            donationProjectMapper.updateById(project);
            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("捐赠失败：" + e.getMessage());
        }
    }
    
    @Override
    public List<String> getDonationLeaderboard(Long projectId, Integer limit) {
        // TODO: 实现善缘榜查询逻辑
        // 这里应该查询捐赠记录，按捐赠总额排序
        // 暂时返回空列表
        return new ArrayList<>();
    }
    
    @Override
    public Page<Donation> getUserDonations(Long userId, Integer pageNum, Integer pageSize) {
        Page<Donation> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Donation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Donation::getUserId, userId);
        queryWrapper.orderByDesc(Donation::getDonationTime);
        
        return donationMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    public List<Donation> getRecentDonations(Long projectId, Integer limit) {
        LambdaQueryWrapper<Donation> queryWrapper = new LambdaQueryWrapper<>();
        
        if (projectId != null) {
            queryWrapper.eq(Donation::getProjectId, projectId);
        }
        
        queryWrapper.orderByDesc(Donation::getDonationTime);
        queryWrapper.last("LIMIT " + limit);
        
        return donationMapper.selectList(queryWrapper);
    }
    
    @Override
    public Map<String, Object> getUserDonationStats(Long userId) {
        // 查询用户所有捐赠记录
        LambdaQueryWrapper<Donation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Donation::getUserId, userId);
        List<Donation> donations = donationMapper.selectList(queryWrapper);
        
        // 统计总捐赠金额
        long totalAmount = donations.stream()
                .mapToLong(Donation::getMeritCoinsDonated)
                .sum();
        
        // 统计捐赠次数
        int donationCount = donations.size();
        
        // 获取最近一次捐赠时间
        Date lastDonationTime = donations.stream()
                .map(Donation::getDonationTime)
                .max(Date::compareTo)
                .orElse(null);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAmount", totalAmount);
        stats.put("donationCount", donationCount);
        stats.put("lastDonationTime", lastDonationTime);
        
        return stats;
    }
    
    @Override
    public Boolean checkUserBalance(Long userId, Integer amount) {
        UserStats userStats = userStatsMapper.selectByUserId(userId);
        return userStats != null && userStats.getMeritCoins() >= amount;
    }
}