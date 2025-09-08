package com.hsmy.service;

import com.hsmy.dto.UserSessionContext;
import com.hsmy.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Session管理Service接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
public interface SessionService {
    
    /**
     * 创建用户Session
     * 
     * @param user 用户信息
     * @param request HTTP请求（用于获取IP、User-Agent等信息）
     * @return sessionId
     */
    String createSession(User user, HttpServletRequest request);
    
    /**
     * 根据SessionId获取用户会话上下文
     * 
     * @param sessionId sessionId
     * @return 用户会话上下文，不存在返回null
     */
    UserSessionContext getUserSessionContext(String sessionId);
    
    /**
     * 获取Session中的用户ID（兼容旧接口）
     * 
     * @param sessionId sessionId
     * @return 用户ID，不存在返回null
     */
    Long getUserIdBySession(String sessionId);
    
    /**
     * 刷新Session过期时间并更新最后访问时间
     * 
     * @param sessionId sessionId
     * @return 是否成功
     */
    Boolean refreshSession(String sessionId);
    
    /**
     * 更新Session中的用户信息
     * 
     * @param sessionId sessionId
     * @param user 最新的用户信息
     * @return 是否成功
     */
    Boolean updateSessionUser(String sessionId, User user);
    
    /**
     * 删除Session
     * 
     * @param sessionId sessionId
     * @return 是否成功
     */
    Boolean removeSession(String sessionId);
    
    /**
     * 检查Session是否存在
     * 
     * @param sessionId sessionId
     * @return 是否存在
     */
    Boolean existsSession(String sessionId);
    
    /**
     * 获取用户的所有Session（支持多端登录场景）
     * 
     * @param userId 用户ID
     * @return SessionId列表
     */
    java.util.List<String> getUserSessions(Long userId);
    
    /**
     * 踢出用户的所有Session（强制下线）
     * 
     * @param userId 用户ID
     * @return 踢出的Session数量
     */
    Integer kickOutUserSessions(Long userId);
}