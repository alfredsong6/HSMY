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