package com.hsmy.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.dto.LoginResponse;
import com.hsmy.dto.UpdateNicknameRequest;
import com.hsmy.entity.User;
import com.hsmy.service.SessionService;
import com.hsmy.service.UserService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@RestController
@RequestMapping("/user")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final SessionService sessionService;
    
//    /**
//     * 用户注册
//     *
//     * @param registerVO 注册信息
//     * @return 结果
//     */
//    @PostMapping("/register")
//    public Result<Long> register(@Validated @RequestBody RegisterVO registerVO) {
//        // 验证两次密码是否一致
//        if (!registerVO.getPassword().equals(registerVO.getConfirmPassword())) {
//            return Result.error("两次密码输入不一致");
//        }
//
//        try {
//            Long userId = userService.register(registerVO);
//            return Result.success("注册成功", userId);
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }
//    }
//
//    /**
//     * 用户登录
//     *
//     * @param loginVO 登录信息
//     * @return 用户信息
//     */
//    @PostMapping("/login")
//    public Result<UserVO> login(@Validated @RequestBody LoginVO loginVO) {
//        try {
//            UserVO userVO = userService.login(loginVO);
//            // TODO: 生成JWT Token
//            userVO.setToken("mock-token-" + userVO.getId());
//            return Result.success("登录成功", userVO);
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }
//    }
    
    /**
     * 获取当前用户信息
     * 
     * @param userId 用户ID（从Token中解析）
     * @return 用户信息
     */
    @GetMapping("/info/{userId}")
    public Result<UserVO> getUserInfo(@PathVariable Long userId) {
        Long currentUserId = UserContextUtil.getCurrentUserId();
        UserVO userVO = userService.getUserVOById(userId);
        if (userVO == null) {
            return Result.error("用户不存在");
        }
        return Result.success(userVO);
    }

    @GetMapping("/self/info")
    public Result<UserVO> getUserInfo() {
        Long userId = UserContextUtil.getCurrentUserId();
        UserVO userVO = userService.getUserVOById(userId);
        if (userVO == null) {
            return Result.error("用户不存在");
        }
        return Result.success(userVO);
    }

    /**
     * 修改当前用户昵称.
     */
    @PostMapping("/nickname")
    public Result<Boolean> updateNickname(@Validated @RequestBody UpdateNicknameRequest request) {
        Long userId = UserContextUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        try {
            Boolean success = userService.updateNickname(userId, request.getNickname());
            return success ? Result.success("昵称更新成功", true) : Result.error("昵称更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     * 
     * @param userVO 用户信息
     * @return 结果
     */
    @PutMapping("/update")
    public Result<Boolean> updateUser(@RequestBody UserVO userVO) {
        Long currentUserId = UserContextUtil.getCurrentUserId();
        userVO.setId(currentUserId);
        try {
            Boolean result = userService.updateUser(userVO);
            return result ? Result.success("更新成功", true) : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改密码
     * 
     * @param changePasswordVO 修改密码信息
     * @return 结果
     */
    @PostMapping("/changePassword")
    public Result<LoginResponse> changePassword(@Validated @RequestBody ChangePasswordVO changePasswordVO, HttpServletRequest request) {
        // 验证两次密码是否一致
        if (!changePasswordVO.getNewPassword().equals(changePasswordVO.getConfirmPassword())) {
            return Result.error("两次密码输入不一致");
        }

        // 验证新密码不能与旧密码相同
        if (changePasswordVO.getOldPassword().equals(changePasswordVO.getNewPassword())) {
            return Result.error("新密码不能与旧密码相同");
        }
        Long currentUserId = UserContextUtil.getCurrentUserId();
        try {
            Boolean result = userService.changePassword(
                    currentUserId,
                changePasswordVO.getOldPassword(),
                changePasswordVO.getNewPassword()
            );

            if (result) {
                // 踢掉用户的所有旧Session
                sessionService.kickOutUserSessions(currentUserId);

                // 获取用户信息并创建新Session
                User user = userService.getUserById(currentUserId);
                String newSessionId = sessionService.createSession(user, request);

                // 构建登录响应
                LoginResponse response = new LoginResponse();
                response.setSessionId(newSessionId);
                response.setToken(newSessionId);
                response.setTokenType("Bearer");
                response.setUserId(user.getId());
                response.setUsername(user.getUsername());
                response.setNickname(user.getNickname());
                response.setPhone(user.getPhone());
                response.setEmail(user.getEmail());

                return Result.success("密码修改成功", response);
            } else {
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询用户列表（管理员接口）
     * 
     * @param queryVO 查询条件
     * @return 用户列表
     */
    @PostMapping("/page")
    public Result<Page<UserVO>> getUserPage(@RequestBody UserQueryVO queryVO) {
        Page<UserVO> page = userService.getUserPage(queryVO);
        return Result.success(page);
    }
    
    /**
     * 更新用户状态（管理员接口）
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 结果
     */
    @PutMapping("/status/{userId}/{status}")
    public Result<Boolean> updateUserStatus(@PathVariable Long userId, @PathVariable Integer status) {
        try {
            Boolean result = userService.updateUserStatus(userId, status);
            return result ? Result.success("状态更新成功", true) : Result.error("状态更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
//    /**
//     * 重置密码（管理员接口）
//     *
//     * @param userId 用户ID
//     * @param newPassword 新密码
//     * @return 结果
//     */
//    @PostMapping("/resetPassword/{userId}")
//    public Result<Boolean> resetPassword(@PathVariable Long userId, @RequestParam String newPassword) {
//        try {
//            Boolean result = userService.resetPassword(userId, newPassword);
//            return result ? Result.success("密码重置成功", true) : Result.error("密码重置失败");
//        } catch (Exception e) {
//            return Result.error(e.getMessage());
//        }
//    }
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 结果
     */
    @GetMapping("/check/username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        Boolean exists = userService.checkUsernameExists(username);
        return Result.success(exists);
    }
    
    /**
     * 检查手机号是否存在
     * 
     * @param phone 手机号
     * @return 结果
     */
    @GetMapping("/check/phone")
    public Result<Boolean> checkPhone(@RequestParam String phone) {
        Boolean exists = userService.checkPhoneExists(phone);
        return Result.success(exists);
    }
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 结果
     */
    @GetMapping("/check/email")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        Boolean exists = userService.checkEmailExists(email);
        return Result.success(exists);
    }

    /**
     * 用户退出
     *
     * @return 结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String sessionId = null;

        if (StringUtils.hasText(authorization)) {
            if (authorization.startsWith("Bearer ")) {
                sessionId = authorization.substring(7);
            } else {
                sessionId = authorization;
            }
        }

        if (!StringUtils.hasText(sessionId)) {
            sessionId = request.getHeader("X-Session-Id");
        }

        if (!StringUtils.hasText(sessionId)) {
            sessionId = request.getParameter("sessionId");
        }

        if (StringUtils.hasText(sessionId)) {
            sessionService.removeSession(sessionId);
        }

        return Result.success("退出成功", null);
    }
    
    /**
     * 初始化密码
     * 
     * @param initializePasswordVO 初始化密码信息
     * @return 结果
     */
    @PostMapping("/initializePassword")
    public Result<Boolean> initializePassword(@Validated @RequestBody InitializePasswordVO initializePasswordVO) {
        // 验证两次密码是否一致
        if (!initializePasswordVO.getPassword().equals(initializePasswordVO.getConfirmPassword())) {
            return Result.error("两次密码输入不一致");
        }
        Long userId = UserContextUtil.requireCurrentUserId();
        try {
            Boolean result = userService.initializePassword(
                    userId, initializePasswordVO.getPassword()
            );
            return result ? Result.success("密码初始化成功", true) : Result.error("密码初始化失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 通过短信验证码重置密码
     * 
     * @param resetPasswordVO 重置密码信息
     * @return 结果
     */
    @PostMapping("/resetPasswordWithSms")
    public Result<LoginResponse> resetPasswordWithSms(@Validated @RequestBody ResetPasswordWithSmsVO resetPasswordVO, HttpServletRequest request) {
        // 验证两次密码是否一致
        if (!resetPasswordVO.getPassword().equals(resetPasswordVO.getConfirmPassword())) {
            return Result.error("两次密码输入不一致");
        }

        try {
            Boolean result = userService.resetPasswordWithSms(
                resetPasswordVO.getPhone(),
                resetPasswordVO.getCode(),
                resetPasswordVO.getPassword()
            );

            if (result) {
                // 根据手机号获取用户信息
                User user = userService.getUserByPhone(resetPasswordVO.getPhone());
                if (user == null) {
                    return Result.error("用户不存在");
                }

                // 踢掉用户的所有旧Session
                sessionService.kickOutUserSessions(user.getId());

                // 创建新Session
                String newSessionId = sessionService.createSession(user, request);

                // 构建登录响应
                LoginResponse response = new LoginResponse();
                response.setSessionId(newSessionId);
                response.setToken(newSessionId);
                response.setTokenType("Bearer");
                response.setUserId(user.getId());
                response.setUsername(user.getUsername());
                response.setNickname(user.getNickname());
                response.setPhone(user.getPhone());
                response.setEmail(user.getEmail());

                return Result.success("密码重置成功", response);
            } else {
                return Result.error("密码重置失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
