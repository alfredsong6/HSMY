package com.hsmy.controller.system;

import com.hsmy.common.Result;
import com.hsmy.config.AuthWhiteListProperties;
import com.hsmy.utils.WhiteListUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 白名单管理Controller
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Slf4j
@RestController
@RequestMapping("/api/system/whitelist")
@RequiredArgsConstructor
public class WhiteListController {
    
    private final AuthWhiteListProperties authWhiteListProperties;
    
    /**
     * 获取白名单配置
     * 
     * @return 白名单配置
     */
    @GetMapping("/config")
    public Result<Map<String, Object>> getWhiteListConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("enabled", authWhiteListProperties.getEnabled());
        config.put("paths", authWhiteListProperties.getPaths());
        config.put("pathCount", authWhiteListProperties.getPaths().size());
        
        return Result.success(config);
    }
    
    /**
     * 获取白名单路径列表
     * 
     * @return 白名单路径列表
     */
    @GetMapping("/paths")
    public Result<List<String>> getWhiteListPaths() {
        return Result.success(authWhiteListProperties.getPaths());
    }
    
    /**
     * 添加白名单路径
     * 
     * @param path 要添加的路径
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<String> addWhiteListPath(@RequestParam String path) {
        try {
            if (path == null || path.trim().isEmpty()) {
                return Result.error("路径不能为空");
            }
            
            path = path.trim();
            authWhiteListProperties.addWhiteListPath(path);
            
            log.info("添加白名单路径成功: {}", path);
            return Result.success("添加成功");
        } catch (Exception e) {
            log.error("添加白名单路径失败: {}", path, e);
            return Result.error("添加失败: " + e.getMessage());
        }
    }
    
    /**
     * 移除白名单路径
     * 
     * @param path 要移除的路径
     * @return 操作结果
     */
    @PostMapping("/remove")
    public Result<String> removeWhiteListPath(@RequestParam String path) {
        try {
            if (path == null || path.trim().isEmpty()) {
                return Result.error("路径不能为空");
            }
            
            path = path.trim();
            authWhiteListProperties.removeWhiteListPath(path);
            
            log.info("移除白名单路径成功: {}", path);
            return Result.success("移除成功");
        } catch (Exception e) {
            log.error("移除白名单路径失败: {}", path, e);
            return Result.error("移除失败: " + e.getMessage());
        }
    }
    
    /**
     * 启用/禁用白名单功能
     * 
     * @param enabled 是否启用
     * @return 操作结果
     */
    @PostMapping("/toggle")
    public Result<String> toggleWhiteList(@RequestParam Boolean enabled) {
        try {
            authWhiteListProperties.setEnabled(enabled);
            
            String action = enabled ? "启用" : "禁用";
            log.info("{}白名单功能成功", action);
            return Result.success(action + "成功");
        } catch (Exception e) {
            log.error("切换白名单状态失败", e);
            return Result.error("操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查路径是否在白名单中
     * 
     * @param path 要检查的路径
     * @return 检查结果
     */
    @GetMapping("/check")
    public Result<Map<String, Object>> checkPath(@RequestParam String path) {
        try {
            boolean inWhiteList = WhiteListUtil.isInWhiteList(path, authWhiteListProperties.getPaths());
            
            Map<String, Object> result = new HashMap<>();
            result.put("path", path);
            result.put("inWhiteList", inWhiteList);
            result.put("whiteListEnabled", authWhiteListProperties.getEnabled());
            result.put("finalResult", authWhiteListProperties.getEnabled() && inWhiteList);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("检查路径失败: {}", path, e);
            return Result.error("检查失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试当前请求路径
     * 
     * @param request HTTP请求
     * @return 当前请求路径信息
     */
    @GetMapping("/test-current")
    public Result<Map<String, Object>> testCurrentPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        Map<String, Object> result = new HashMap<>();
        result.put("requestURI", requestURI);
        result.put("contextPath", contextPath);
        result.put("method", request.getMethod());
        
        // 计算实际路径
        String actualPath = requestURI;
        if (contextPath != null && !contextPath.isEmpty() && requestURI.startsWith(contextPath)) {
            actualPath = requestURI.substring(contextPath.length());
        }
        result.put("actualPath", actualPath);
        
        // 检查是否在白名单中
        boolean inWhiteList = WhiteListUtil.isInWhiteList(actualPath, authWhiteListProperties.getPaths());
        result.put("inWhiteList", inWhiteList);
        result.put("whiteListEnabled", authWhiteListProperties.getEnabled());
        
        return Result.success(result);
    }
}