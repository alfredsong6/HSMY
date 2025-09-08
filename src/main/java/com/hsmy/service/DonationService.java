package com.hsmy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.entity.Donation;
import com.hsmy.entity.DonationProject;

import java.util.List;
import java.util.Map;

/**
 * 捐赠Service接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
public interface DonationService {
    
    /**
     * 获取捐赠项目列表
     * 
     * @param projectType 项目类型
     * @param status 项目状态
     * @return 项目列表
     */
    List<DonationProject> getDonationProjects(String projectType, Integer status);
    
    /**
     * 根据ID获取项目详情
     * 
     * @param projectId 项目ID
     * @return 项目详情
     */
    DonationProject getProjectById(Long projectId);
    
    /**
     * 进行捐赠
     * 
     * @param userId 用户ID
     * @param projectId 项目ID
     * @param meritCoins 捐赠功德币
     * @param message 祈愿留言
     * @param isAnonymous 是否匿名
     * @return 是否成功
     */
    Boolean makeDonation(Long userId, Long projectId, Integer meritCoins, String message, Boolean isAnonymous);
    
    /**
     * 获取善缘榜
     * 
     * @param projectId 项目ID（为null时查询总榜）
     * @param limit 查询条数
     * @return 善缘榜数据
     */
    List<String> getDonationLeaderboard(Long projectId, Integer limit);
    
    /**
     * 获取用户捐赠记录
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 捐赠记录分页
     */
    Page<Donation> getUserDonations(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取最近捐赠记录
     * 
     * @param projectId 项目ID（为null时查询所有项目）
     * @param limit 查询条数
     * @return 最近捐赠记录
     */
    List<Donation> getRecentDonations(Long projectId, Integer limit);
    
    /**
     * 获取用户捐赠统计
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserDonationStats(Long userId);
    
    /**
     * 检查用户功德币余额是否充足
     * 
     * @param userId 用户ID
     * @param amount 需要的功德币数量
     * @return 是否充足
     */
    Boolean checkUserBalance(Long userId, Integer amount);
}