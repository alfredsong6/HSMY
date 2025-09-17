package com.hsmy.controller.auth;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.dto.*;
import com.hsmy.entity.User;
import com.hsmy.exception.BusinessException;
import com.hsmy.interceptor.LoginInterceptor;
import com.hsmy.service.SessionService;
import com.hsmy.service.UserService;
import com.hsmy.service.VerificationCodeService;
import com.hsmy.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
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
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final SessionService sessionService;
    private final VerificationCodeService verificationCodeService;
    
    /**
     * 发送验证码
     * 
     * @param request 发送验证码请求
     * @param httpRequest HTTP请求
     * @return 发送结果
     */
    @PostMapping("/send-code")
    public Result<String> sendCode(@RequestBody @Validated SendCodeRequest request,
                                   HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        
        // 验证账号格式
        if ("phone".equals(request.getAccountType())) {
            if (!request.getAccount().matches("^1[3-9]\\d{9}$")) {
                throw new BusinessException("手机号格式不正确");
            }
        } else if ("email".equals(request.getAccountType())) {
            if (!request.getAccount().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
                throw new BusinessException("邮箱格式不正确");
            }
        }
        
        // 如果是注册，检查账号是否已存在
        if ("register".equals(request.getBusinessType())) {
            User existingUser = null;
            if ("phone".equals(request.getAccountType())) {
                existingUser = userService.getUserByPhone(request.getAccount());
            } else if ("email".equals(request.getAccountType())) {
                existingUser = userService.getUserByEmail(request.getAccount());
            }
            
            if (existingUser != null) {
                throw new BusinessException("该" + ("phone".equals(request.getAccountType()) ? "手机号" : "邮箱") + "已被注册");
            }
        }
        
        // 发送验证码
        boolean success = verificationCodeService.sendCode(
            request.getAccount(),
            request.getAccountType(),
            request.getBusinessType(),
            ipAddress
        );
        
        if (!success) {
            throw new BusinessException("验证码发送失败，请稍后重试");
        }
        
        return Result.success("验证码已发送，请查收");
    }
    
    /**
     * 验证码注册
     * 
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register-by-code")
    public Result<LoginResponse> registerByCode(@RequestBody @Validated RegisterByCodeRequest request,
                                               HttpServletRequest httpRequest) {
        // 验证验证码
        boolean valid = verificationCodeService.verifyCode(
            request.getAccount(),
            request.getCode(),
            "register"
        );
        
        if (!valid) {
            throw new BusinessException("验证码无效或已过期");
        }
        
        // 调用用户服务进行注册
        Long userId = userService.registerByCode(request);
        
        // 标记验证码已使用
        verificationCodeService.markCodeAsUsed(request.getAccount(), request.getCode(), "register");
        
        // 自动登录
        User user = userService.getUserById(userId);
        String sessionId = sessionService.createSession(user, httpRequest);
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setSessionId(sessionId);  // 兼容旧版本
        response.setToken(sessionId);      // 作为token使用
        response.setTokenType("Bearer");
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        
        log.info("用户通过验证码注册成功，userId: {}, account: {}", userId, request.getAccount());
        return Result.success(response);
    }
    
    /**
     * 设置密码
     * 
     * @param request 设置密码请求
     * @return 设置结果
     */
    @PostMapping("/set-password")
    public Result<String> setPassword(@RequestBody @Validated SetPasswordRequest request) {
        // 验证两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        
        // 获取当前用户
        Long userId = UserContextUtil.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "用户未登录");
        }
        
        // 设置密码
        userService.setPassword(userId, request.getPassword());
        
        log.info("用户设置密码成功，userId: {}", userId);
        return Result.success("密码设置成功");
    }
    
//    /**
//     * 用户注册（保留原有方法，标记为过时）
//     *
//     * @param registerVO 注册信息
//     * @return 注册结果
//     */
//    @Deprecated
//    @PostMapping("/register")
//    public Result<String> register(@RequestBody @Validated RegisterVO registerVO) {
//        try {
//            // 验证两次密码是否一致
//            if (!registerVO.getPassword().equals(registerVO.getConfirmPassword())) {
//                return Result.error("两次输入的密码不一致");
//            }
//
//            // 调用用户服务进行注册
//            Long userId = userService.register(registerVO);
//
//            log.info("用户注册成功，userId: {}, username: {}", userId, registerVO.getUsername());
//            return Result.success("注册成功");
//        } catch (RuntimeException e) {
//            log.error("用户注册失败：{}", e.getMessage());
//            return Result.error(e.getMessage());
//        } catch (Exception e) {
//            log.error("用户注册失败", e);
//            return Result.error("注册失败：" + e.getMessage());
//        }
//    }
    
    /**
     * 用户登录V2（支持密码和验证码登录）
     * 
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<LoginResponse> loginV2(@RequestBody @Validated LoginRequestV2 loginRequest,
                                        HttpServletRequest request) {
        User user = null;
        
        if ("password".equals(loginRequest.getLoginType())) {
            // 密码登录
            if (!StringUtils.hasText(loginRequest.getPassword())) {
                throw new BusinessException("密码不能为空");
            }
            
            // 根据登录账号查找用户
            user = userService.getUserByLoginAccount(loginRequest.getLoginAccount());
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 验证密码
            String encodedPassword = DigestUtils.md5DigestAsHex(loginRequest.getPassword().getBytes());
            if (!encodedPassword.equals(user.getPassword())) {
                throw new BusinessException("密码错误");
            }
        } else if ("code".equals(loginRequest.getLoginType())) {
            // 验证码登录
            if (!StringUtils.hasText(loginRequest.getCode())) {
                throw new BusinessException("验证码不能为空");
            }
            
            // 验证验证码
            boolean valid = verificationCodeService.verifyCode(
                loginRequest.getLoginAccount(),
                loginRequest.getCode(),
                "login"
            );
            
            if (!valid) {
                throw new BusinessException("验证码无效或已过期");
            }
            
            // 根据账号查找用户
            user = userService.getUserByLoginAccount(loginRequest.getLoginAccount());
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 标记验证码已使用
            verificationCodeService.markCodeAsUsed(loginRequest.getLoginAccount(), loginRequest.getCode(), "login");
        } else {
            throw new BusinessException("不支持的登录方式");
        }
        
        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 创建Session
        String sessionId = sessionService.createSession(user, request);
        if (sessionId == null) {
            throw new BusinessException("登录失败，请稍后重试");
        }
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setSessionId(sessionId);  // 兼容旧版本
        response.setToken(sessionId);      // 作为token使用
        response.setTokenType("Bearer");
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        
        log.info("用户登录成功，userId: {}, username: {}, loginType: {}", 
                user.getId(), user.getUsername(), loginRequest.getLoginType());
        
        return Result.success(response);
    }
    
//    /**
//     * 用户登录（保留原有方法，标记为过时）
//     *
//     * @param loginRequest 登录请求
//     * @return 登录结果
//     */
//    @Deprecated
//    @PostMapping("/login")
//    public Result<LoginResponse> login(@RequestBody @Validated LoginRequest loginRequest,
//                                     HttpServletRequest request) {
//        try {
//            // 根据登录账号查找用户
//            User user = userService.getUserByLoginAccount(loginRequest.getLoginAccount());
//            if (user == null) {
//                return Result.error("用户不存在");
//            }
//
//            // 验证密码
//            String encodedPassword = DigestUtils.md5DigestAsHex(loginRequest.getPassword().getBytes());
//            if (!encodedPassword.equals(user.getPassword())) {
//                return Result.error("密码错误");
//            }
//
//            // 检查用户状态
//            if (user.getStatus() != 1) {
//                return Result.error("账号已被禁用");
//            }
//
//            // 创建Session
//            String sessionId = sessionService.createSession(user, request);
//            if (sessionId == null) {
//                return Result.error("登录失败，请稍后重试");
//            }
//
//            // 构建响应
//            LoginResponse response = new LoginResponse();
//            response.setSessionId(sessionId);
//            response.setUserId(user.getId());
//            response.setUsername(user.getUsername());
//            response.setNickname(user.getNickname());
//            response.setPhone(user.getPhone());
//            response.setEmail(user.getEmail());
//
//            log.info("用户登录成功，userId: {}, username: {}", user.getId(), user.getUsername());
//
//            return Result.success(response);
//        } catch (Exception e) {
//            log.error("登录失败", e);
//            return Result.error("登录失败：" + e.getMessage());
//        }
//    }
//
    /**
     * 用户登出
     * 
     * @param request HTTP请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        // 获取Authorization头
        String authorization = request.getHeader("Authorization");
        String sessionId = null;
        
        // 支持 Bearer token 格式
        if (authorization != null && authorization.startsWith("Bearer ")) {
            sessionId = authorization.substring(7);
        } else if (authorization != null) {
            sessionId = authorization;
        }
        
        if (sessionId != null) {
            // 删除Session
            sessionService.removeSession(sessionId);
            
            // 获取用户ID用于日志
            Long userId = (Long) request.getAttribute(LoginInterceptor.USER_ID_ATTRIBUTE);
            log.info("用户登出成功，userId: {}", userId);
        }
        
        return Result.success("登出成功");
    }
    
    /**
     * 获取当前用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("/user-info")
    public Result<UserSessionContext> getUserInfo() {
        // 使用工具类获取当前用户会话上下文
        UserSessionContext userSessionContext = UserContextUtil.getCurrentUserSessionContext();
        
        if (userSessionContext == null) {
            throw new BusinessException(401, "获取用户信息失败");
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
    }
    
    /**
     * 获取用户的所有活跃Session
     * 
     * @return Session列表
     */
    @GetMapping("/sessions")
    public Result<java.util.List<String>> getUserSessions() {
        Long userId = UserContextUtil.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "用户未登录");
        }
        
        java.util.List<String> sessions = sessionService.getUserSessions(userId);
        return Result.success(sessions);
    }
    
    /**
     * 踢出用户的其他Session（保留当前Session）
     * 
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/kick-other-sessions")
    public Result<String> kickOtherSessions(HttpServletRequest request) {
        Long userId = UserContextUtil.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "用户未登录");
        }
        
        // 获取当前sessionId
        String authorization = request.getHeader("Authorization");
        String currentSessionId = null;
        
        // 支持 Bearer token 格式
        if (authorization != null && authorization.startsWith("Bearer ")) {
            currentSessionId = authorization.substring(7);
        } else if (authorization != null) {
            currentSessionId = authorization;
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
    }
    
    /**
     * 强制踢出用户所有Session（管理员接口）
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    @PostMapping("/kick-user-sessions")
    public Result<String> kickUserSessions(@RequestParam Long userId) {
        // 检查当前用户是否是管理员
        if (!UserContextUtil.isCurrentUserAdmin()) {
            throw new BusinessException(403, "无权限执行此操作");
        }
        
        Integer kickedCount = sessionService.kickOutUserSessions(userId);
        return Result.success("成功踢出用户 " + userId + " 的 " + kickedCount + " 个登录会话");
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
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}