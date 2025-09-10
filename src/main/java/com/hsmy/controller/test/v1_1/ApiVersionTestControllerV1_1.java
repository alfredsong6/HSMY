package com.hsmy.controller.test.v1_1;

import com.hsmy.common.Result;
import com.hsmy.annotation.ApiVersion;
import com.hsmy.constant.ApiVersionConstant;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * API版本测试控制器 V1.1
 * 
 * @author HSMY
 * @date 2025/09/10
 */
@RestController
@RequestMapping("/test")
@ApiVersion(ApiVersionConstant.V1_1)
public class ApiVersionTestControllerV1_1 {
    
    /**
     * V1.1版本的测试接口
     * 
     * @return 版本信息
     */
    @GetMapping("/version")
    public Result<Map<String, Object>> getVersionV1_1() {
        Map<String, Object> data = new HashMap<>();
        data.put("version", "v1.1");
        data.put("message", "这是V1.1版本的接口");
        data.put("path", "/api/v1.1/test/version");
        data.put("timestamp", System.currentTimeMillis());
        data.put("newFeatures", new String[]{"增强版响应", "更多详细信息", "性能优化"});
        return Result.success("V1.1版本接口调用成功", data);
    }
    
    /**
     * V1.1版本的用户信息接口（增强版）
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getUserV1_1(@PathVariable Long userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("version", "v1.1");
        data.put("userId", userId);
        data.put("userName", "用户" + userId);
        data.put("userLevel", userId % 10 + 1); // 模拟用户等级
        data.put("totalScore", userId * 100); // 模拟总分
        data.put("path", "/api/v1.1/test/user/" + userId);
        data.put("enhanced", true);
        return Result.success("V1.1用户信息（增强版）", data);
    }
    
    /**
     * V1.1版本新增的详细信息接口
     * 
     * @param userId 用户ID
     * @return 详细信息
     */
    @GetMapping("/user/{userId}/details")
    public Result<Map<String, Object>> getUserDetailsV1_1(@PathVariable Long userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("version", "v1.1");
        data.put("userId", userId);
        data.put("detailLevel", "full");
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalLogins", userId * 5);
        statistics.put("lastActiveTime", System.currentTimeMillis());
        statistics.put("preferredLanguage", "zh-CN");
        data.put("statistics", statistics);
        data.put("path", "/api/v1.1/test/user/" + userId + "/details");
        return Result.success("V1.1用户详细信息", data);
    }
}