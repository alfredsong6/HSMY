package com.hsmy.service.impl;

import com.hsmy.dto.UserSessionContext;
import com.hsmy.entity.User;
import com.hsmy.entity.UserStats;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Session管理Service实现类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserStatsMapper userStatsMapper;
    
    /**
     * Session前缀
     */
    private static final String SESSION_PREFIX = "hsmy:session:";
    
    /**
     * 用户Session列表前缀（用于支持多端登录）
     */
    private static final String USER_SESSIONS_PREFIX = "hsmy:user:sessions:";
    
    /**
     * Session过期时间（7天）
     */
    private static final long SESSION_TIMEOUT = 7 * 24 * 60 * 60;
    
    @Override
    public String createSession(User user, HttpServletRequest request) {
        try {
            // 生成唯一的sessionId
            String sessionId = generateSessionId();
            String sessionKey = SESSION_PREFIX + sessionId;
            
            // 创建用户会话上下文
            UserSessionContext sessionContext = buildUserSessionContext(user, request, sessionId);
            
            // 将会话上下文存储到Redis中，设置过期时间
            redisTemplate.opsForValue().set(sessionKey, sessionContext, SESSION_TIMEOUT, TimeUnit.SECONDS);
            
            // 将sessionId添加到用户的session列表中（支持多端登录）
            String userSessionsKey = USER_SESSIONS_PREFIX + user.getId();
            redisTemplate.opsForSet().add(userSessionsKey, sessionId);
            redisTemplate.expire(userSessionsKey, SESSION_TIMEOUT, TimeUnit.SECONDS);
            
            log.info("创建Session成功，userId: {}, username: {}, sessionId: {}, loginIp: {}", 
                    user.getId(), user.getUsername(), sessionId, getClientIp(request));
            
            return sessionId;
        } catch (Exception e) {
            log.error("创建Session失败，userId: {}, username: {}", user.getId(), user.getUsername(), e);
            return null;
        }
    }
    
    @Override
    public UserSessionContext getUserSessionContext(String sessionId) {
        try {
            String sessionKey = SESSION_PREFIX + sessionId;
            Object sessionData = redisTemplate.opsForValue().get(sessionKey);
            
            if (sessionData instanceof UserSessionContext) {
                UserSessionContext sessionContext = (UserSessionContext) sessionData;
                
                // 刷新过期时间并更新最后访问时间
                sessionContext.setLastAccessTime(new Date());
                redisTemplate.opsForValue().set(sessionKey, sessionContext, SESSION_TIMEOUT, TimeUnit.SECONDS);
                
                // 同时刷新用户Session列表的过期时间
                String userSessionsKey = USER_SESSIONS_PREFIX + sessionContext.getUserId();
                redisTemplate.expire(userSessionsKey, SESSION_TIMEOUT, TimeUnit.SECONDS);
                
                return sessionContext;
            }
            
            return null;
        } catch (Exception e) {
            log.error("获取Session上下文失败，sessionId: {}", sessionId, e);
            return null;
        }
    }
    
    @Override
    public Long getUserIdBySession(String sessionId) {
        UserSessionContext sessionContext = getUserSessionContext(sessionId);
        return sessionContext != null ? sessionContext.getUserId() : null;
    }
    
    @Override
    public Boolean refreshSession(String sessionId) {
        try {
            String sessionKey = SESSION_PREFIX + sessionId;
            
            // 检查session是否存在
            UserSessionContext sessionContext = (UserSessionContext) redisTemplate.opsForValue().get(sessionKey);
            if (sessionContext != null) {
                // 更新最后访问时间
                sessionContext.setLastAccessTime(new Date());
                
                // 重新设置过期时间
                redisTemplate.opsForValue().set(sessionKey, sessionContext, SESSION_TIMEOUT, TimeUnit.SECONDS);
                
                // 同时刷新用户Session列表的过期时间
                String userSessionsKey = USER_SESSIONS_PREFIX + sessionContext.getUserId();
                redisTemplate.expire(userSessionsKey, SESSION_TIMEOUT, TimeUnit.SECONDS);
                
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("刷新Session失败，sessionId: {}", sessionId, e);
            return false;
        }
    }
    
    @Override
    public Boolean updateSessionUser(String sessionId, User user) {
        try {
            String sessionKey = SESSION_PREFIX + sessionId;
            UserSessionContext sessionContext = (UserSessionContext) redisTemplate.opsForValue().get(sessionKey);
            
            if (sessionContext != null) {
                // 更新用户基础信息
                updateSessionContextFromUser(sessionContext, user);
                sessionContext.setLastAccessTime(new Date());
                
                // 保存更新后的session
                redisTemplate.opsForValue().set(sessionKey, sessionContext, SESSION_TIMEOUT, TimeUnit.SECONDS);
                
                log.info("更新Session用户信息成功，userId: {}, sessionId: {}", user.getId(), sessionId);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("更新Session用户信息失败，userId: {}, sessionId: {}", user.getId(), sessionId, e);
            return false;
        }
    }
    
    @Override
    public Boolean removeSession(String sessionId) {
        try {
            String sessionKey = SESSION_PREFIX + sessionId;
            
            // 先获取session信息
            UserSessionContext sessionContext = (UserSessionContext) redisTemplate.opsForValue().get(sessionKey);
            
            // 删除session
            Boolean sessionDeleted = redisTemplate.delete(sessionKey);
            
            // 从用户session列表中移除
            if (sessionContext != null) {
                String userSessionsKey = USER_SESSIONS_PREFIX + sessionContext.getUserId();
                redisTemplate.opsForSet().remove(userSessionsKey, sessionId);
                
                log.info("删除Session成功，userId: {}, sessionId: {}", sessionContext.getUserId(), sessionId);
            }
            
            return sessionDeleted;
        } catch (Exception e) {
            log.error("删除Session失败，sessionId: {}", sessionId, e);
            return false;
        }
    }
    
    @Override
    public Boolean existsSession(String sessionId) {
        try {
            String sessionKey = SESSION_PREFIX + sessionId;
            return redisTemplate.hasKey(sessionKey);
        } catch (Exception e) {
            log.error("检查Session存在性失败，sessionId: {}", sessionId, e);
            return false;
        }
    }
    
    @Override
    public List<String> getUserSessions(Long userId) {
        try {
            String userSessionsKey = USER_SESSIONS_PREFIX + userId;
            Set<Object> sessionIds = redisTemplate.opsForSet().members(userSessionsKey);
            
            List<String> activeSessions = new ArrayList<>();
            if (sessionIds != null) {
                for (Object sessionId : sessionIds) {
                    String sessionKey = SESSION_PREFIX + sessionId.toString();
                    if (Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey))) {
                        activeSessions.add(sessionId.toString());
                    } else {
                        // 清理失效的sessionId
                        redisTemplate.opsForSet().remove(userSessionsKey, sessionId);
                    }
                }
            }
            
            return activeSessions;
        } catch (Exception e) {
            log.error("获取用户Session列表失败，userId: {}", userId, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public Integer kickOutUserSessions(Long userId) {
        try {
            List<String> sessionIds = getUserSessions(userId);
            int kickedCount = 0;
            
            for (String sessionId : sessionIds) {
                if (removeSession(sessionId)) {
                    kickedCount++;
                }
            }
            
            // 清空用户session列表
            String userSessionsKey = USER_SESSIONS_PREFIX + userId;
            redisTemplate.delete(userSessionsKey);
            
            log.info("踢出用户所有Session成功，userId: {}, 踢出数量: {}", userId, kickedCount);
            return kickedCount;
        } catch (Exception e) {
            log.error("踢出用户Session失败，userId: {}", userId, e);
            return 0;
        }
    }
    
    /**
     * 生成sessionId
     * 
     * @return sessionId
     */
    private String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "") + "_" + System.currentTimeMillis();
    }
    
    /**
     * 构建用户会话上下文
     * 
     * @param user 用户信息
     * @param request HTTP请求
     * @param sessionId SessionId
     * @return 用户会话上下文
     */
    private UserSessionContext buildUserSessionContext(User user, HttpServletRequest request, String sessionId) {
        UserSessionContext sessionContext = new UserSessionContext();
        
        // 基础用户信息
        sessionContext.setUserId(user.getId());
        sessionContext.setUsername(user.getUsername());
        sessionContext.setNickname(user.getNickname());
        sessionContext.setPhone(user.getPhone());
        sessionContext.setEmail(user.getEmail());
        sessionContext.setStatus(user.getStatus());
        sessionContext.setSessionId(sessionId);
        
        // 获取用户统计信息
        try {
            UserStats userStats = userStatsMapper.selectByUserId(user.getId());
            if (userStats != null) {
                sessionContext.setCurrentLevel(userStats.getCurrentLevel());
                sessionContext.setMeritCoins(userStats.getMeritCoins() != null ? userStats.getMeritCoins().intValue() : 0);
                sessionContext.setTotalMerit(userStats.getTotalMerit());
            } else {
                // 设置默认值
                sessionContext.setCurrentLevel(1);
                sessionContext.setMeritCoins(0);
                sessionContext.setTotalMerit(0L);
            }
        } catch (Exception e) {
            log.warn("获取用户统计信息失败，userId: {}, 将使用默认值", user.getId(), e);
            sessionContext.setCurrentLevel(1);
            sessionContext.setMeritCoins(0);
            sessionContext.setTotalMerit(0L);
        }
        
        // 会话相关信息
        Date now = new Date();
        sessionContext.setLoginTime(now);
        sessionContext.setLastAccessTime(now);
        sessionContext.setLoginIp(getClientIp(request));
        sessionContext.setUserAgent(request.getHeader("User-Agent"));
        
        // 权限相关（可根据实际需求扩展）
        sessionContext.setIsAdmin(isAdmin(user));
        
        return sessionContext;
    }
    
    /**
     * 从User对象更新SessionContext
     * 
     * @param sessionContext 会话上下文
     * @param user 用户信息
     */
    private void updateSessionContextFromUser(UserSessionContext sessionContext, User user) {
        sessionContext.setUsername(user.getUsername());
        sessionContext.setNickname(user.getNickname());
        sessionContext.setPhone(user.getPhone());
        sessionContext.setEmail(user.getEmail());
        sessionContext.setStatus(user.getStatus());
        sessionContext.setIsAdmin(isAdmin(user));
        
        // 更新用户统计信息
        try {
            UserStats userStats = userStatsMapper.selectByUserId(user.getId());
            if (userStats != null) {
                sessionContext.setCurrentLevel(userStats.getCurrentLevel());
                sessionContext.setMeritCoins(userStats.getMeritCoins() != null ? userStats.getMeritCoins().intValue() : 0);
                sessionContext.setTotalMerit(userStats.getTotalMerit());
            }
        } catch (Exception e) {
            log.warn("更新用户统计信息失败，userId: {}", user.getId(), e);
        }
    }
    
    /**
     * 获取客户端IP地址
     * 
     * @param request HTTP请求
     * @return IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        ip = request.getHeader("Proxy-Client-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 判断用户是否是管理员
     * 
     * @param user 用户信息
     * @return 是否是管理员
     */
    private Boolean isAdmin(User user) {
        // 这里可以根据实际业务逻辑判断
        // 例如：根据角色、权限等判断
        // 暂时简单判断用户名是否为admin
        return "admin".equalsIgnoreCase(user.getUsername());
    }
}