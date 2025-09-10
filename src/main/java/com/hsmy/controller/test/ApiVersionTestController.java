package com.hsmy.controller.test;

import com.hsmy.common.Result;
import com.hsmy.annotation.ApiVersion;
import com.hsmy.constant.ApiVersionConstant;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * API版本测试控制器
 * 
 * @author HSMY
 * @date 2025/09/10
 */
@RestController
@RequestMapping("/test")
@ApiVersion(ApiVersionConstant.V1_0)
public class ApiVersionTestController {
    
    /**
     * V1.0版本的测试接口
     * 
     * @return 版本信息
     */
    @GetMapping("/version")
    public Result<Map<String, Object>> getVersionV1_0() {
        Map<String, Object> data = new HashMap<>();
        data.put("version", "v1.0");
        data.put("message", "这是V1.0版本的接口");
        data.put("path", "/api/v1.0/test/version");
        data.put("timestamp", System.currentTimeMillis());
        return Result.success("V1.0版本接口调用成功", data);
    }
    
    /**
     * V1.0版本的用户信息接口
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getUserV1_0(@PathVariable Long userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("version", "v1.0");
        data.put("userId", userId);
        data.put("userName", "用户" + userId);
        data.put("path", "/api/v1.0/test/user/" + userId);
        return Result.success("V1.0用户信息", data);
    }
}