package com.hsmy.controller.auth;

import com.hsmy.common.Result;
import com.hsmy.dto.LoginRequest;
import com.hsmy.dto.LoginResponse;
import com.hsmy.dto.UserSessionContext;
import com.hsmy.entity.User;
import com.hsmy.interceptor.LoginInterceptor;
import com.hsmy.service.SessionService;
import com.hsmy.service.UserService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.RegisterVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证Controller
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final SessionService sessionService;
    
    /**
     * 用户注册
     * 
     * @param registerVO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody @Validated RegisterVO registerVO) {
        try {
            // 验证两次密码是否一致
            if (!registerVO.getPassword().equals(registerVO.getConfirmPassword())) {
                return Result.error("两次输入的密码不一致");
            }
            
            // 调用用户服务进行注册
            Long userId = userService.register(registerVO);
            
            log.info("用户注册成功，userId: {}, username: {}", userId, registerVO.getUsername());
            return Result.success("注册成功");
        } catch (RuntimeException e) {
            log.error("用户注册失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return Result.error("注册失败：" + e.getMessage());
        }
    }
    
    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Validated LoginRequest loginRequest, 
                                     HttpServletRequest request) {
        try {
            // 根据登录账号查找用户
            User user = userService.getUserByLoginAccount(loginRequest.getLoginAccount());
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 验证密码
            String encodedPassword = DigestUtils.md5DigestAsHex(loginRequest.getPassword().getBytes());
            if (!encodedPassword.equals(user.getPassword())) {
                return Result.error("密码错误");
            }
            
            // 检查用户状态
            if (user.getStatus() != 1) {
                return Result.error("账号已被禁用");
            }
            
            // 创建Session
            String sessionId = sessionService.createSession(user, request);
            if (sessionId == null) {
                return Result.error("登录失败，请稍后重试");
            }
            
            // 构建响应
            LoginResponse response = new LoginResponse();
            response.setSessionId(sessionId);
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setNickname(user.getNickname());
            response.setPhone(user.getPhone());
            response.setEmail(user.getEmail());
            
            log.info("用户登录成功，userId: {}, username: {}", user.getId(), user.getUsername());
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error("登录失败：" + e.getMessage());
        }
    }
    
    /**
     * 用户登出
     * 
     * @param request HTTP请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        try {
            // 获取sessionId
            String sessionId = request.getHeader("X-Session-Id");
            if (sessionId == null) {
                sessionId = request.getParameter("sessionId");
            }
            
            if (sessionId != null) {
                // 删除Session
                sessionService.removeSession(sessionId);
                
                // 获取用户ID用于日志
                Long userId = (Long) request.getAttribute(LoginInterceptor.USER_ID_ATTRIBUTE);
                log.info("用户登出成功，userId: {}", userId);
            }
            
            return Result.success("登出成功");
        } catch (Exception e) {
            log.error("登出失败", e);
            return Result.error("登出失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("/user-info")
    public Result<UserSessionContext> getUserInfo() {
        try {
            // 使用工具类获取当前用户会话上下文
            UserSessionContext userSessionContext = UserContextUtil.getCurrentUserSessionContext();
            
            if (userSessionContext == null) {
                return Result.error("获取用户信息失败");
            }
            
            // 清空敏感信息（可选）
            UserSessionContext safeContext = new UserSessionContext();
            safeContext.setUserId(userSessionContext.getUserId());
            safeContext.setUsername(userSessionContext.getUsername());
            safeContext.setNickname(userSessionContext.getNickname());
            safeContext.setPhone(userSessionContext.getPhone());
            safeContext.setEmail(userSessionContext.getEmail());
            safeContext.setCurrentLevel(userSessionContext.getCurrentLevel());
            safeContext.setMeritCoins(userSessionContext.getMeritCoins());
            safeContext.setTotalMerit(userSessionContext.getTotalMerit());
            safeContext.setLoginTime(userSessionContext.getLoginTime());
            safeContext.setLastAccessTime(userSessionContext.getLastAccessTime());
            safeContext.setIsAdmin(userSessionContext.getIsAdmin());
            // 不返回sessionId, loginIp, userAgent等敏感信息
            
            return Result.success(safeContext);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户的所有活跃Session
     * 
     * @return Session列表
     */
    @GetMapping("/sessions")
    public Result<java.util.List<String>> getUserSessions() {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            java.util.List<String> sessions = sessionService.getUserSessions(userId);
            return Result.success(sessions);
        } catch (Exception e) {
            log.error("获取用户Session列表失败", e);
            return Result.error("获取Session列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 踢出用户的其他Session（保留当前Session）
     * 
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/kick-other-sessions")
    public Result<String> kickOtherSessions(HttpServletRequest request) {
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            // 获取当前sessionId
            String currentSessionId = request.getHeader("X-Session-Id");
            if (currentSessionId == null) {
                currentSessionId = request.getParameter("sessionId");
            }
            
            // 获取所有session
            java.util.List<String> allSessions = sessionService.getUserSessions(userId);
            int kickedCount = 0;
            
            // 踢出除当前session外的所有session
            for (String sessionId : allSessions) {
                if (!sessionId.equals(currentSessionId)) {
                    if (sessionService.removeSession(sessionId)) {
                        kickedCount++;
                    }
                }
            }
            
            return Result.success("成功踢出 " + kickedCount + " 个其他登录会话");
        } catch (Exception e) {
            log.error("踢出其他Session失败", e);
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 强制踢出用户所有Session（管理员接口）
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    @PostMapping("/kick-user-sessions")
    public Result<String> kickUserSessions(@RequestParam Long userId) {
        try {
            // 检查当前用户是否是管理员
            if (!UserContextUtil.isCurrentUserAdmin()) {
                return Result.error("无权限执行此操作");
            }
            
            Integer kickedCount = sessionService.kickOutUserSessions(userId);
            return Result.success("成功踢出用户 " + userId + " 的 " + kickedCount + " 个登录会话");
        } catch (Exception e) {
            log.error("踢出用户Session失败，userId: {}", userId, e);
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 健康检查接口（不需要登录）
     * 
     * @return 健康状态
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("服务正常");
    }
}