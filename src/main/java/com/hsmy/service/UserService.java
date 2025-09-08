package com.hsmy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.entity.User;
import com.hsmy.vo.LoginVO;
import com.hsmy.vo.RegisterVO;
import com.hsmy.vo.UserQueryVO;
import com.hsmy.vo.UserVO;

/**
 * 用户Service接口
 * 
 * @author HSMY
 * @date 2025/09/07
 */
public interface UserService {
    
    /**
     * 用户注册
     * 
     * @param registerVO 注册信息
     * @return 用户ID
     */
    Long register(RegisterVO registerVO);
    
    /**
     * 用户登录
     * 
     * @param loginVO 登录信息
     * @return 用户信息
     */
    UserVO login(LoginVO loginVO);
    
    /**
     * 根据ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long userId);
    
    /**
     * 根据ID获取用户实体
     * 
     * @param userId 用户ID
     * @return 用户实体
     */
    User getUserEntityById(Long userId);
    
    /**
     * 根据登录账号获取用户（支持用户名/手机号/邮箱）
     * 
     * @param loginAccount 登录账号
     * @return 用户实体
     */
    User getUserByLoginAccount(String loginAccount);
    
    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 更新用户信息
     * 
     * @param userVO 用户信息
     * @return 是否成功
     */
    Boolean updateUser(UserVO userVO);
    
    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    Boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 分页查询用户列表
     * 
     * @param queryVO 查询条件
     * @return 用户列表
     */
    Page<UserVO> getUserPage(UserQueryVO queryVO);
    
    /**
     * 更新用户状态
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    Boolean updateUserStatus(Long userId, Integer status);
    
    /**
     * 重置密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    Boolean resetPassword(Long userId, String newPassword);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    Boolean checkUsernameExists(String username);
    
    /**
     * 检查手机号是否存在
     * 
     * @param phone 手机号
     * @return 是否存在
     */
    Boolean checkPhoneExists(String phone);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    Boolean checkEmailExists(String email);
}