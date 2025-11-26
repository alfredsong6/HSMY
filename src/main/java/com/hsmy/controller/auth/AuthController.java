package com.hsmy.controller.auth;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.domain.auth.AuthIdentity;
import com.hsmy.dto.*;
import com.hsmy.entity.User;
import com.hsmy.enums.AccountType;
import com.hsmy.enums.BusinessType;
import com.hsmy.exception.BusinessException;
import com.hsmy.interceptor.LoginInterceptor;
import com.hsmy.service.*;
import com.hsmy.service.wechat.WechatMiniAuthService;
import com.hsmy.service.wechat.dto.WechatPhoneInfo;
import com.hsmy.service.wechat.dto.WechatSessionInfo;
import com.hsmy.utils.UserContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    private final AuthIdentityService authIdentityService;
    private final AuthTokenService authTokenService;
    private final WechatMiniAuthService wechatMiniAuthService;

    private static final String PROVIDER_WECHAT_MINI = "wechat_mini";
    private static final String PROVIDER_SMS = "sms";
    private static final String CLIENT_MINIAPP = "miniapp";
    private static final String CLIENT_APP = "app";
    private static final long TOKEN_EXPIRE_SECONDS = TimeUnit.DAYS.toSeconds(7);
    
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
        if (request.getAccountType() == com.hsmy.enums.AccountType.PHONE) {
            if (!request.getAccount().matches("^1[3-9]\\d{9}$")) {
                throw new BusinessException("手机号格式不正确");
            }
        } else if (request.getAccountType() == com.hsmy.enums.AccountType.EMAIL) {
            if (!request.getAccount().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
                throw new BusinessException("邮箱格式不正确");
            }
        }
        
        // 如果是注册，检查账号是否已存在
        if (request.getBusinessType() == com.hsmy.enums.BusinessType.REGISTER) {
            User existingUser = null;
            if (request.getAccountType() == com.hsmy.enums.AccountType.PHONE) {
                existingUser = userService.getUserByPhone(request.getAccount());
            } else if (request.getAccountType() == com.hsmy.enums.AccountType.EMAIL) {
                existingUser = userService.getUserByEmail(request.getAccount());
            }
            
            if (existingUser != null) {
                throw new BusinessException("该" + (request.getAccountType() == com.hsmy.enums.AccountType.PHONE ? "手机号" : "邮箱") + "已被注册");
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
            com.hsmy.enums.BusinessType.REGISTER
        );
        
        if (!valid) {
            throw new BusinessException("验证码无效或已过期");
        }
        
        // 调用用户服务进行注册
        Long userId = userService.registerByCode(request);
        
        // 标记验证码已使用
        verificationCodeService.markCodeAsUsed(request.getAccount(), request.getCode(), BusinessType.REGISTER);
        
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
     * 微信小程序登录/自动注册.
     *
     * @param request 小程序登录请求（openId+手机号）
     * @return 登录结果
     */
    @PostMapping("/mini/login")
    public Result<LoginResponse> miniProgramLogin(@RequestBody @Validated WechatMiniLoginRequest request,
                                                  HttpServletRequest httpRequest) {
        String appId = wechatMiniAuthService.getDefaultAppId();
        if (!StringUtils.hasText(appId)) {
            throw new BusinessException("小程序appId未配置");
        }

        // 1) code2session 获取 openId/unionId/sessionKey
        WechatSessionInfo sessionInfo = wechatMiniAuthService.code2Session(appId, request.getAuthCode());

        // 2) 通过 phoneCode + sessionKey 获取手机号（后端调微信接口）
        WechatPhoneInfo phoneInfo = wechatMiniAuthService.getPhoneNumber(request.getPhoneCode(), sessionInfo.getSessionKey());
        log.info("获取手机号，phoneCode: {}, phoneInfo: {}", request.getPhoneCode(), phoneInfo);
        String phone = StringUtils.hasText(phoneInfo.getPurePhoneNumber()) ? phoneInfo.getPurePhoneNumber() : phoneInfo.getPhoneNumber();
        if (!StringUtils.hasText(phone)) {
            throw new BusinessException("获取手机号失败");
        }

        // 3) 查找身份记录
        AuthIdentity identity = authIdentityService.getByOpenId(PROVIDER_WECHAT_MINI, appId, sessionInfo.getOpenId());
        User user = identity != null && identity.getUserId() != null ? userService.getUserById(identity.getUserId()) : null;

        // 4) unionId 合并
        if (user == null && StringUtils.hasText(sessionInfo.getUnionId())) {
            AuthIdentity unionIdentity = authIdentityService.getByUnionId(PROVIDER_WECHAT_MINI, sessionInfo.getUnionId());
            if (unionIdentity != null && unionIdentity.getUserId() != null) {
                user = userService.getUserById(unionIdentity.getUserId());
                identity = identity == null ? unionIdentity : identity;
            }
        }

        // 5) 手机号复用/创建
        if (user == null) {
            user = userService.getUserByPhone(phone);
            if (user == null) {
                RegisterByCodeRequest registerRequest = new RegisterByCodeRequest();
                registerRequest.setAccount(phone);
                registerRequest.setAccountType(AccountType.PHONE);
                registerRequest.setCode("000000"); // 后端直注册，不校验短信
                registerRequest.setNickname(resolveNickname(phone, request.getNickname()));
                Long userId = userService.registerByCode(registerRequest);
                user = userService.getUserById(userId);
                log.info("微信小程序自动注册新用户，userId: {}, openId: {}", user.getId(), sessionInfo.getOpenId());
            }
        }

        // 6) 维护身份
        if (identity == null) {
            authIdentityService.createIdentity(user.getId(), PROVIDER_WECHAT_MINI, appId,
                    sessionInfo.getOpenId(), sessionInfo.getUnionId(), phone, sessionInfo.getSessionKey());
        } else {
            authIdentityService.touchLogin(identity.getId(), user.getId(), phone,
                    sessionInfo.getUnionId(), sessionInfo.getSessionKey(), new Date());
        }

        ensureUserEnabled(user);

        String sessionId = sessionService.createSession(user, httpRequest);
        if (sessionId == null) {
            throw new BusinessException("登录失败，请稍后重试");
        }
        recordAuthToken(user.getId(), sessionId, CLIENT_MINIAPP, null);

        log.info("微信小程序登录成功，userId: {}, openId: {}", user.getId(), sessionInfo.getOpenId());
        return Result.success(buildLoginResponse(user, sessionId));
    }

    /**
     * App端手机号登录/注册.
     *
     * @param request 登录请求
     * @return 登录结果
     */
    @PostMapping("/app/login")
    public Result<LoginResponse> appPhoneLogin(@RequestBody @Validated AppPhoneLoginRequest request,
                                               HttpServletRequest httpRequest) {
        boolean valid = verificationCodeService.verify(request.getPhone(),
                com.hsmy.enums.AccountType.PHONE, request.getCode(), com.hsmy.enums.BusinessType.LOGIN);
        if (!valid) {
            throw new BusinessException("验证码无效或已过期");
        }

        User user = userService.getUserByPhone(request.getPhone());
        if (user == null) {
            RegisterByCodeRequest registerRequest = new RegisterByCodeRequest();
            registerRequest.setAccount(request.getPhone());
            registerRequest.setAccountType(com.hsmy.enums.AccountType.PHONE);
            registerRequest.setCode(request.getCode());
            registerRequest.setNickname(request.getNickname());
            Long userId = userService.registerByCode(registerRequest);
            user = userService.getUserById(userId);
            log.info("App端手机号注册成功，userId: {}", user.getId());
        }

        // 维护手机号身份映射
        AuthIdentity phoneIdentity = authIdentityService.getByPhone(PROVIDER_SMS, request.getPhone());
        if (phoneIdentity == null) {
            authIdentityService.createIdentity(user.getId(), PROVIDER_SMS, CLIENT_APP, null, null, request.getPhone(), null);
        } else {
            authIdentityService.touchLogin(phoneIdentity.getId(), user.getId(), request.getPhone(), null, null, new Date());
        }

        ensureUserEnabled(user);

        String sessionId = sessionService.createSession(user, httpRequest);
        if (sessionId == null) {
            throw new BusinessException("登录失败，请稍后重试");
        }
        recordAuthToken(user.getId(), sessionId, CLIENT_APP, request.getDeviceId());

        log.info("App端手机号登录成功，userId: {}", user.getId());
        return Result.success(buildLoginResponse(user, sessionId));
    }
    
//    /**
//     * 设置密码
//     *
//     * @param request 设置密码请求
//     * @return 设置结果
//     */
//    @PostMapping("/set-password")
//    public Result<String> setPassword(@RequestBody @Validated SetPasswordRequest request) {
//        // 验证两次密码是否一致
//        if (!request.getPassword().equals(request.getConfirmPassword())) {
//            throw new BusinessException("两次输入的密码不一致");
//        }
//
//        // 获取当前用户
//        Long userId = UserContextUtil.getCurrentUserId();
//        if (userId == null) {
//            throw new BusinessException(401, "用户未登录");
//        }
//
//        // 设置密码
//        userService.setPassword(userId, request.getPassword());
//
//        log.info("用户设置密码成功，userId: {}", userId);
//        return Result.success("密码设置成功");
//    }
//
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
                com.hsmy.enums.BusinessType.LOGIN
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
            verificationCodeService.markCodeAsUsed(loginRequest.getLoginAccount(), loginRequest.getCode(), com.hsmy.enums.BusinessType.LOGIN);
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
    
//    /**
//     * 获取当前用户信息
//     *
//     * @return 用户信息
//     */
//    @GetMapping("/user-info")
//    public Result<UserSessionContext> getUserInfo() {
//        // 使用工具类获取当前用户会话上下文
//        UserSessionContext userSessionContext = UserContextUtil.getCurrentUserSessionContext();
//
//        if (userSessionContext == null) {
//            throw new BusinessException(401, "获取用户信息失败");
//        }
//
//        // 清空敏感信息（可选）
//        UserSessionContext safeContext = new UserSessionContext();
//        safeContext.setUserId(userSessionContext.getUserId());
//        safeContext.setUsername(userSessionContext.getUsername());
//        safeContext.setNickname(userSessionContext.getNickname());
//        safeContext.setPhone(userSessionContext.getPhone());
//        safeContext.setEmail(userSessionContext.getEmail());
//        safeContext.setCurrentLevel(userSessionContext.getCurrentLevel());
//        safeContext.setMeritCoins(userSessionContext.getMeritCoins());
//        safeContext.setTotalMerit(userSessionContext.getTotalMerit());
//        safeContext.setLoginTime(userSessionContext.getLoginTime());
//        safeContext.setLastAccessTime(userSessionContext.getLastAccessTime());
//        safeContext.setIsAdmin(userSessionContext.getIsAdmin());
//        // 不返回sessionId, loginIp, userAgent等敏感信息
//
//        return Result.success(safeContext);
//    }
    
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

    private void ensureUserEnabled(User user) {
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
    }

    private void recordAuthToken(Long userId, String token, String clientType, String deviceId) {
        Date expiresAt = new Date(System.currentTimeMillis() + TOKEN_EXPIRE_SECONDS * 1000);
        authTokenService.recordToken(userId, token, expiresAt, clientType, deviceId);
    }

    private String resolveNickname(String phone, String candidate) {
        if (StringUtils.hasText(candidate)) {
            return candidate;
        }
        String suffix = phone.length() >= 4 ? phone.substring(phone.length() - 4) : phone;
        return "微信用户" + suffix;
    }

    private LoginResponse buildLoginResponse(User user, String sessionId) {
        LoginResponse response = new LoginResponse();
        response.setSessionId(sessionId);
        response.setToken(sessionId);
        response.setTokenType("Bearer");
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        return response;
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
