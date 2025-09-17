package com.hsmy.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.service.UserService;
import com.hsmy.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 
 * @author HSMY
 * @date 2025/09/07
 */
//@RestController
//@RequestMapping("/api/auth")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 用户注册
     * 
     * @param registerVO 注册信息
     * @return 结果
     */
    @PostMapping("/register")
    public Result<Long> register(@Validated @RequestBody RegisterVO registerVO) {
        // 验证两次密码是否一致
        if (!registerVO.getPassword().equals(registerVO.getConfirmPassword())) {
            return Result.error("两次密码输入不一致");
        }
        
        try {
            Long userId = userService.register(registerVO);
            return Result.success("注册成功", userId);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     * 
     * @param loginVO 登录信息
     * @return 用户信息
     */
    @PostMapping("/login")
    public Result<UserVO> login(@Validated @RequestBody LoginVO loginVO) {
        try {
            UserVO userVO = userService.login(loginVO);
            // TODO: 生成JWT Token
            userVO.setToken("mock-token-" + userVO.getId());
            return Result.success("登录成功", userVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取当前用户信息
     * 
     * @param userId 用户ID（从Token中解析）
     * @return 用户信息
     */
    @GetMapping("/info/{userId}")
    public Result<UserVO> getUserInfo(@PathVariable Long userId) {
        UserVO userVO = userService.getUserVOById(userId);
        if (userVO == null) {
            return Result.error("用户不存在");
        }
        return Result.success(userVO);
    }
    
    /**
     * 更新用户信息
     * 
     * @param userVO 用户信息
     * @return 结果
     */
    @PutMapping("/update")
    public Result<Boolean> updateUser(@RequestBody UserVO userVO) {
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
    public Result<Boolean> changePassword(@Validated @RequestBody ChangePasswordVO changePasswordVO) {
        // 验证两次密码是否一致
        if (!changePasswordVO.getNewPassword().equals(changePasswordVO.getConfirmPassword())) {
            return Result.error("两次密码输入不一致");
        }
        
        try {
            Boolean result = userService.changePassword(
                changePasswordVO.getUserId(),
                changePasswordVO.getOldPassword(),
                changePasswordVO.getNewPassword()
            );
            return result ? Result.success("密码修改成功", true) : Result.error("密码修改失败");
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
    
    /**
     * 重置密码（管理员接口）
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 结果
     */
    @PostMapping("/resetPassword/{userId}")
    public Result<Boolean> resetPassword(@PathVariable Long userId, @RequestParam String newPassword) {
        try {
            Boolean result = userService.resetPassword(userId, newPassword);
            return result ? Result.success("密码重置成功", true) : Result.error("密码重置失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
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
     * 用户登出
     * 
     * @return 结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // TODO: 清除Token等登出逻辑
        return Result.success("登出成功", null);
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
        
        try {
            Boolean result = userService.initializePassword(
                initializePasswordVO.getUserId(),
                initializePasswordVO.getPassword()
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
    public Result<Boolean> resetPasswordWithSms(@Validated @RequestBody ResetPasswordWithSmsVO resetPasswordVO) {
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
            return result ? Result.success("密码重置成功", true) : Result.error("密码重置失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}