package com.hsmy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证白名单配置类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "auth.whitelist")
public class AuthWhiteListProperties {
    
    /**
     * 白名单路径列表，支持AntMatch风格
     */
    private List<String> paths = new ArrayList<>();
    
    /**
     * 是否启用白名单功能
     */
    private Boolean enabled = true;
    
    /**
     * 默认构造函数，初始化默认白名单
     */
    public AuthWhiteListProperties() {
        initDefaultWhiteList();
    }
    
    /**
     * 初始化默认白名单
     */
    private void initDefaultWhiteList() {
        // 认证相关接口
        paths.add("/api/auth/login");
        paths.add("/api/auth/register");  
        paths.add("/api/auth/logout");
        paths.add("/api/auth/code");
        paths.add("/api/auth/health");
        
        // 系统接口
        paths.add("/error");
        paths.add("/favicon.ico");
        
        // 静态资源
        paths.add("/static/**");
        paths.add("/public/**");
        paths.add("/webjars/**");
        
        // 开发工具相关
        paths.add("/actuator/**");
        paths.add("/h2-console/**");
        
        // 文档相关
        paths.add("/doc.html");
        paths.add("/swagger-ui/**");
        paths.add("/swagger-resources/**");
        paths.add("/v2/api-docs");
        paths.add("/v3/api-docs");
    }
    
    /**
     * 添加白名单路径
     * 
     * @param path 路径
     */
    public void addWhiteListPath(String path) {
        if (path != null && !paths.contains(path)) {
            paths.add(path);
        }
    }
    
    /**
     * 移除白名单路径
     * 
     * @param path 路径
     */
    public void removeWhiteListPath(String path) {
        paths.remove(path);
    }
    
    /**
     * 清空白名单
     */
    public void clearWhiteList() {
        paths.clear();
    }
}