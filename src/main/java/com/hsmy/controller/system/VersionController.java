package com.hsmy.controller.system;

import com.hsmy.common.Result;
import com.hsmy.annotation.ApiVersion;
import com.hsmy.constant.ApiVersionConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * API版本信息控制器
 * 
 * @author HSMY
 * @date 2025/09/10
 */
@RestController
@RequestMapping("/version")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class VersionController {
    
    /**
     * 获取当前支持的API版本列表
     * 
     * @return 版本信息
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getVersionList() {
        Map<String, Object> versionInfo = new HashMap<>();
        
        List<Map<String, Object>> versions = new ArrayList<>();
        
        // v1.0 版本信息
        Map<String, Object> v1_0 = new HashMap<>();
        v1_0.put("version", ApiVersionConstant.V1_0);
        v1_0.put("description", "初始版本");
        v1_0.put("deprecated", false);
        v1_0.put("releaseDate", "2025-09-07");
        v1_0.put("features", Arrays.asList(
            "用户注册登录",
            "功德敲击系统",
            "任务系统",
            "商城功能",
            "排行榜",
            "成就系统",
            "捐赠功能"
        ));
        versions.add(v1_0);
        
        // v1.1 版本信息
        Map<String, Object> v1_1 = new HashMap<>();
        v1_1.put("version", ApiVersionConstant.V1_1);
        v1_1.put("description", "功德系统优化版本");
        v1_1.put("deprecated", false);
        v1_1.put("releaseDate", "2025-09-10");
        v1_1.put("features", Arrays.asList(
            "详细余额信息返回",
            "优化兑换接口响应格式",
            "新增功德统计汇总接口",
            "增强版余额查询",
            "兑换前后余额对比"
        ));
        v1_1.put("compatibleWith", Arrays.asList(ApiVersionConstant.V1_0));
        versions.add(v1_1);
        
        // v2.0 版本信息
        Map<String, Object> v2_0 = new HashMap<>();
        v2_0.put("version", ApiVersionConstant.V2_0);
        v2_0.put("description", "重大更新版本");
        v2_0.put("deprecated", false);
        v2_0.put("releaseDate", "计划中");
        v2_0.put("features", Arrays.asList(
            "全新UI界面支持",
            "性能优化",
            "新增社交功能",
            "高级统计报表"
        ));
        v2_0.put("breaking", true);
        v2_0.put("breakingChanges", Arrays.asList(
            "部分接口响应格式变更",
            "认证机制升级",
            "错误码体系重构"
        ));
        versions.add(v2_0);
        
        versionInfo.put("supportedVersions", versions);
        versionInfo.put("currentVersion", ApiVersionConstant.V1_1);
        versionInfo.put("defaultVersion", ApiVersionConstant.DEFAULT);
        versionInfo.put("latestVersion", ApiVersionConstant.V1_1);
        
        return Result.success("版本信息查询成功", versionInfo);
    }
    
    /**
     * 检查版本兼容性
     * 
     * @param clientVersion 客户端版本
     * @return 兼容性检查结果
     */
    @GetMapping("/compatibility")
    public Result<Map<String, Object>> checkCompatibility(@RequestParam String clientVersion) {
        Map<String, Object> result = new HashMap<>();
        result.put("clientVersion", clientVersion);
        
        // 兼容性检查逻辑
        boolean isCompatible = isVersionCompatible(clientVersion);
        result.put("compatible", isCompatible);
        
        if (isCompatible) {
            result.put("message", "客户端版本兼容");
            result.put("serverVersion", getRecommendedServerVersion(clientVersion));
        } else {
            result.put("message", "客户端版本不兼容，建议升级");
            result.put("minRequiredVersion", ApiVersionConstant.V1_0);
            result.put("latestVersion", ApiVersionConstant.V1_1);
        }
        
        return Result.success(result);
    }
    
    /**
     * 获取版本更新信息
     * 
     * @param currentVersion 当前版本
     * @return 更新信息
     */
    @GetMapping("/updates")
    public Result<Map<String, Object>> getUpdateInfo(@RequestParam String currentVersion) {
        Map<String, Object> updateInfo = new HashMap<>();
        updateInfo.put("currentVersion", currentVersion);
        updateInfo.put("hasUpdate", hasNewerVersion(currentVersion));
        
        if (hasNewerVersion(currentVersion)) {
            updateInfo.put("latestVersion", ApiVersionConstant.V1_1);
            updateInfo.put("updateRequired", false);
            updateInfo.put("updateNotes", Arrays.asList(
                "功德系统体验优化",
                "新增统计汇总功能",
                "修复已知问题"
            ));
        }
        
        return Result.success(updateInfo);
    }
    
    /**
     * 检查版本兼容性
     */
    private boolean isVersionCompatible(String clientVersion) {
        // 简单的版本兼容性检查逻辑
        return ApiVersionConstant.V1_0.equals(clientVersion) || 
               ApiVersionConstant.V1_1.equals(clientVersion);
    }
    
    /**
     * 获取推荐的服务器版本
     */
    private String getRecommendedServerVersion(String clientVersion) {
        // 根据客户端版本推荐对应的服务器版本
        if (ApiVersionConstant.V1_0.equals(clientVersion)) {
            return ApiVersionConstant.V1_0;
        } else if (ApiVersionConstant.V1_1.equals(clientVersion)) {
            return ApiVersionConstant.V1_1;
        }
        return ApiVersionConstant.DEFAULT;
    }
    
    /**
     * 检查是否有更新版本
     */
    private boolean hasNewerVersion(String currentVersion) {
        // 简单的版本比较逻辑
        return ApiVersionConstant.V1_0.equals(currentVersion);
    }
}