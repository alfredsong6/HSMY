package com.hsmy.utils;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.List;

/**
 * 白名单工具类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
public class WhiteListUtil {
    
    private static final PathMatcher pathMatcher = new AntPathMatcher();
    
    /**
     * 检查路径是否在白名单中
     * 
     * @param requestPath 请求路径
     * @param whiteListPaths 白名单路径列表
     * @return 是否在白名单中
     */
    public static boolean isInWhiteList(String requestPath, List<String> whiteListPaths) {
        if (requestPath == null || whiteListPaths == null || whiteListPaths.isEmpty()) {
            return false;
        }
        
        // 标准化请求路径
        String normalizedPath = normalizePath(requestPath);
        
        // 遍历白名单进行匹配
        for (String whiteListPath : whiteListPaths) {
            if (whiteListPath != null && pathMatcher.match(whiteListPath, normalizedPath)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 标准化路径
     * 移除多余的斜杠，确保路径格式统一
     * 
     * @param path 原始路径
     * @return 标准化后的路径
     */
    private static String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        
        // 移除查询参数
        int queryIndex = path.indexOf('?');
        if (queryIndex != -1) {
            path = path.substring(0, queryIndex);
        }
        
        // 移除锚点
        int anchorIndex = path.indexOf('#');
        if (anchorIndex != -1) {
            path = path.substring(0, anchorIndex);
        }
        
        // 标准化路径分隔符
        path = path.replaceAll("//+", "/");
        
        // 确保以/开头
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        
        // 移除末尾的/（根路径除外）
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        
        return path;
    }
    
    /**
     * 检查单个路径模式是否匹配
     * 
     * @param pattern 路径模式（支持*和**通配符）
     * @param path 实际路径
     * @return 是否匹配
     */
    public static boolean matchPattern(String pattern, String path) {
        if (pattern == null || path == null) {
            return false;
        }
        
        return pathMatcher.match(pattern, normalizePath(path));
    }
    
    /**
     * 获取PathMatcher实例
     * 
     * @return PathMatcher实例
     */
    public static PathMatcher getPathMatcher() {
        return pathMatcher;
    }
}