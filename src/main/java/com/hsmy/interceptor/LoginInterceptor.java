package com.hsmy.interceptor;

import com.alibaba.fastjson.JSON;
import com.hsmy.common.Result;
import com.hsmy.config.AuthWhiteListProperties;
import com.hsmy.dto.UserSessionContext;
import com.hsmy.service.SessionService;
import com.hsmy.utils.WhiteListUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录拦截器
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {
    
    private final SessionService sessionService;
    private final AuthWhiteListProperties authWhiteListProperties;
    
    /**
     * Session头名称
     */
    private static final String SESSION_HEADER = "X-Session-Id";
    
    /**
     * 用户ID请求属性名
     */
    public static final String USER_ID_ATTRIBUTE = "userId";
    
    /**
     * 用户会话上下文请求属性名
     */
    public static final String USER_SESSION_CONTEXT_ATTRIBUTE = "userSessionContext";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 跨域预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        String requestPath = getRequestPath(request);
        
        // 检查是否在白名单中
        if (authWhiteListProperties.getEnabled() && 
            WhiteListUtil.isInWhiteList(requestPath, authWhiteListProperties.getPaths())) {
            log.debug("白名单路径放行: {}", requestPath);
            return true;
        }
        
        // 获取sessionId
        String sessionId = getSessionId(request);
        
        if (!StringUtils.hasText(sessionId)) {
            log.warn("请求未携带sessionId，请求路径: {}", requestPath);
            writeErrorResponse(response, "未登录或登录已过期");
            return false;
        }
        
        // 验证session并获取用户会话上下文
        UserSessionContext userSessionContext = sessionService.getUserSessionContext(sessionId);
        if (userSessionContext == null) {
            log.warn("无效的sessionId: {}, 请求路径: {}", sessionId, requestPath);
            writeErrorResponse(response, "登录已过期，请重新登录");
            return false;
        }
        
        // 检查用户状态
        if (userSessionContext.getStatus() == null || userSessionContext.getStatus() != 1) {
            log.warn("用户状态异常，userId: {}, status: {}, 请求路径: {}", 
                    userSessionContext.getUserId(), userSessionContext.getStatus(), requestPath);
            writeErrorResponse(response, "账号已被禁用");
            return false;
        }
        
        // 将用户ID和完整的会话上下文都设置到request属性中
        request.setAttribute(USER_ID_ATTRIBUTE, userSessionContext.getUserId());
        request.setAttribute(USER_SESSION_CONTEXT_ATTRIBUTE, userSessionContext);
        
        log.debug("用户认证成功，userId: {}, username: {}, sessionId: {}, 请求路径: {}, IP: {}", 
                userSessionContext.getUserId(), userSessionContext.getUsername(), sessionId, requestPath, userSessionContext.getLoginIp());
        
        return true;
    }
    
    /**
     * 获取请求路径
     */
    private String getRequestPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        
        // 移除context path
        if (StringUtils.hasText(contextPath) && requestURI.startsWith(contextPath)) {
            requestURI = requestURI.substring(contextPath.length());
        }
        
        return requestURI;
    }
    
    /**
     * 获取sessionId
     * 优先从Header中获取，其次从参数中获取
     */
    private String getSessionId(HttpServletRequest request) {
        // 优先从请求头获取
        String sessionId = request.getHeader(SESSION_HEADER);
        
        if (!StringUtils.hasText(sessionId)) {
            // 从请求参数获取
            sessionId = request.getParameter("sessionId");
        }
        
        return sessionId;
    }
    
    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // 设置跨域响应头
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, X-Session-Id, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        
        Result<Object> result = Result.error(401, message);
        
        try (PrintWriter writer = response.getWriter()) {
            writer.write(JSON.toJSONString(result));
            writer.flush();
        }
    }
}