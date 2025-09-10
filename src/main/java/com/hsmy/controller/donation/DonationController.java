package com.hsmy.controller.donation;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.entity.Donation;
import com.hsmy.entity.DonationProject;
import com.hsmy.service.DonationService;
import com.hsmy.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 捐赠Controller
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@RestController
@RequestMapping("/donation")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class DonationController {
    
    private final DonationService donationService;
    
    /**
     * 获取捐赠项目列表
     * 
     * @param projectType 项目类型（可选）
     * @param status 项目状态（可选）
     * @return 项目列表
     */
    @GetMapping("/projects")
    public Result<List<DonationProject>> getDonationProjects(@RequestParam(required = false) String projectType,
                                                            @RequestParam(required = false) Integer status) {
        try {
            // TODO: 实现获取捐赠项目列表逻辑
            List<DonationProject> projects = donationService.getDonationProjects(projectType, status);
            return Result.success(projects);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取项目详情
     * 
     * @param projectId 项目ID
     * @return 项目详情
     */
    @GetMapping("/projects/{projectId}")
    public Result<DonationProject> getProjectDetail(@PathVariable Long projectId) {
        try {
            DonationProject project = donationService.getProjectById(projectId);
            if (project == null) {
                return Result.error("项目不存在");
            }
            return Result.success(project);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 进行捐赠
     * 
     * @param donationRequest 捐赠请求
     * @param request HTTP请求
     * @return 捐赠结果
     */
    @PostMapping("/donate")
    public Result<Map<String, Object>> makeDonation(@Validated @RequestBody DonationRequest donationRequest,
                                                   HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            // 检查最低捐赠金额
            if (donationRequest.getMeritCoins() < 10) {
                return Result.error("最低捐赠金额为10功德币");
            }
            
            // TODO: 实现捐赠逻辑
            // 1. 检查用户功德币余额
            // 2. 扣除功德币
            // 3. 记录捐赠记录
            // 4. 更新项目募集金额
            // 5. 如果金额>=100可留言祈愿
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("projectId", donationRequest.getProjectId());
            result.put("amount", donationRequest.getMeritCoins());
            result.put("message", donationRequest.getMessage() != null ? donationRequest.getMessage() : "");
            result.put("isAnonymous", donationRequest.getIsAnonymous());
            
            return Result.success("捐赠成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取善缘榜
     * 
     * @param projectId 项目ID（可选，不传则显示总榜）
     * @param limit 查询条数（默认100）
     * @return 善缘榜数据
     */
    @GetMapping("/leaderboard")
    public Result<List<String>> getDonationLeaderboard(@RequestParam(required = false) Long projectId,
                                                                   @RequestParam(defaultValue = "100") Integer limit) {
        try {
            // TODO: 实现善缘榜查询逻辑
            List<String> leaderboard = donationService.getDonationLeaderboard(projectId, limit);
            return Result.success(leaderboard);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户捐赠记录
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param request HTTP请求
     * @return 捐赠记录
     */
    @GetMapping("/my-donations")
    public Result<Page<Donation>> getMyDonations(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            Page<Donation> donations = donationService.getUserDonations(userId, pageNum, pageSize);
            return Result.success(donations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取最近捐赠记录
     * 
     * @param projectId 项目ID（可选）
     * @param limit 查询条数（默认20）
     * @return 最近捐赠记录
     */
    @GetMapping("/recent")
    public Result<List<Donation>> getRecentDonations(@RequestParam(required = false) Long projectId,
                                                    @RequestParam(defaultValue = "20") Integer limit) {
        try {
            List<Donation> recentDonations = donationService.getRecentDonations(projectId, limit);
            return Result.success(recentDonations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户捐赠统计
     * 
     * @param request HTTP请求
     * @return 捐赠统计信息
     */
    @GetMapping("/stats")
    public Result<String> getDonationStats(HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.requireCurrentUserId();
            
            //String stats = donationService.getUserDonationStats(userId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 捐赠请求VO
     */
    public static class DonationRequest {
        private Long projectId;
        private Integer meritCoins;
        private String message;
        private Boolean isAnonymous = false;
        
        // getters and setters
        public Long getProjectId() { return projectId; }
        public void setProjectId(Long projectId) { this.projectId = projectId; }
        
        public Integer getMeritCoins() { return meritCoins; }
        public void setMeritCoins(Integer meritCoins) { this.meritCoins = meritCoins; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Boolean getIsAnonymous() { return isAnonymous; }
        public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    }
}