package com.hsmy.utils;

import com.hsmy.dto.UserSessionContext;
import com.hsmy.interceptor.LoginInterceptor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
public class UserContextUtil {
    
    /**
     * 获取当前登录用户ID
     * 
     * @return 用户ID，未登录返回null
     */
    public static Long getCurrentUserId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Object userId = request.getAttribute(LoginInterceptor.USER_ID_ATTRIBUTE);
                if (userId != null) {
                    return Long.valueOf(userId.toString());
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取当前登录用户的会话上下文
     * 
     * @return 用户会话上下文，未登录返回null
     */
    public static UserSessionContext getCurrentUserSessionContext() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Object sessionContext = request.getAttribute(LoginInterceptor.USER_SESSION_CONTEXT_ATTRIBUTE);
                if (sessionContext instanceof UserSessionContext) {
                    return (UserSessionContext) sessionContext;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取当前登录用户名
     * 
     * @return 用户名，未登录返回null
     */
    public static String getCurrentUsername() {
        UserSessionContext context = getCurrentUserSessionContext();
        return context != null ? context.getUsername() : null;
    }
    
    /**
     * 获取当前登录用户昵称
     * 
     * @return 昵称，未登录返回null
     */
    public static String getCurrentNickname() {
        UserSessionContext context = getCurrentUserSessionContext();
        return context != null ? context.getNickname() : null;
    }
    
    /**
     * 获取当前登录用户手机号
     * 
     * @return 手机号，未登录返回null
     */
    public static String getCurrentPhone() {
        UserSessionContext context = getCurrentUserSessionContext();
        return context != null ? context.getPhone() : null;
    }
    
    /**
     * 获取当前登录用户邮箱
     * 
     * @return 邮箱，未登录返回null
     */
    public static String getCurrentEmail() {
        UserSessionContext context = getCurrentUserSessionContext();
        return context != null ? context.getEmail() : null;
    }
    
    /**
     * 获取当前登录用户功德币数量
     * 
     * @return 功德币数量，未登录返回null
     */
    public static Integer getCurrentMeritCoins() {
        UserSessionContext context = getCurrentUserSessionContext();
        return context != null ? context.getMeritCoins() : null;
    }
    
    /**
     * 获取当前登录用户总功德值
     * 
     * @return 总功德值，未登录返回null
     */
    public static Long getCurrentTotalMerit() {
        UserSessionContext context = getCurrentUserSessionContext();
        return context != null ? context.getTotalMerit() : null;
    }
    
    /**
     * 获取当前登录用户等级
     * 
     * @return 用户等级，未登录返回null
     */
    public static Integer getCurrentLevel() {
        UserSessionContext context = getCurrentUserSessionContext();
        return context != null ? context.getCurrentLevel() : null;
    }
    
    /**
     * 获取当前登录用户的登录IP
     * 
     * @return 登录IP，未登录返回null
     */
    public static String getCurrentLoginIp() {
        UserSessionContext context = getCurrentUserSessionContext();
        return context != null ? context.getLoginIp() : null;
    }
    
    /**
     * 检查当前用户是否是管理员
     * 
     * @return 是否是管理员，未登录返回false
     */
    public static Boolean isCurrentUserAdmin() {
        UserSessionContext context = getCurrentUserSessionContext();
        return context != null && Boolean.TRUE.equals(context.getIsAdmin());
    }
    
    /**
     * 获取当前登录用户ID，如果未登录抛出异常
     * 
     * @return 用户ID
     * @throws RuntimeException 未登录时抛出
     */
    public static Long requireCurrentUserId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        return userId;
    }
    
    /**
     * 获取当前登录用户会话上下文，如果未登录抛出异常
     * 
     * @return 用户会话上下文
     * @throws RuntimeException 未登录时抛出
     */
    public static UserSessionContext requireCurrentUserSessionContext() {
        UserSessionContext context = getCurrentUserSessionContext();
        if (context == null) {
            throw new RuntimeException("用户未登录");
        }
        return context;
    }
    
    /**
     * 检查当前用户是否有指定权限（扩展方法，可根据需要实现）
     * 
     * @param permission 权限标识
     * @return 是否有权限
     */
    public static Boolean hasPermission(String permission) {
        UserSessionContext context = getCurrentUserSessionContext();
        if (context == null) {
            return false;
        }
        
        // 管理员拥有所有权限
        if (Boolean.TRUE.equals(context.getIsAdmin())) {
            return true;
        }
        
        // 这里可以根据实际业务逻辑判断权限
        // 例如：解析context.getPermissions()字符串
        // 暂时返回false
        return false;
    }
    
    /**
     * 检查当前用户是否有指定角色（扩展方法，可根据需要实现）
     * 
     * @param role 角色标识
     * @return 是否有角色
     */
    public static Boolean hasRole(String role) {
        UserSessionContext context = getCurrentUserSessionContext();
        if (context == null) {
            return false;
        }
        
        // 这里可以根据实际业务逻辑判断角色
        // 例如：解析context.getRoles()字符串
        // 暂时返回false
        return false;
    }
}