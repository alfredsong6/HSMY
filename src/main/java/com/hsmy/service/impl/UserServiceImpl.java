package com.hsmy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.entity.User;
import com.hsmy.entity.UserStats;
import com.hsmy.mapper.UserMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.UserService;
import com.hsmy.utils.IdGenerator;
import com.hsmy.vo.LoginVO;
import com.hsmy.vo.RegisterVO;
import com.hsmy.vo.UserQueryVO;
import com.hsmy.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 用户Service实现类
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final UserStatsMapper userStatsMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(RegisterVO registerVO) {
        // 检查用户名是否存在
        if (checkUsernameExists(registerVO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否存在
        if (StrUtil.isNotBlank(registerVO.getPhone()) && checkPhoneExists(registerVO.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }
        
        // 检查邮箱是否存在
        if (StrUtil.isNotBlank(registerVO.getEmail()) && checkEmailExists(registerVO.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        BeanUtil.copyProperties(registerVO, user);
        user.setId(IdGenerator.nextId());
        user.setPassword(SecureUtil.md5(registerVO.getPassword()));
        user.setNickname(StrUtil.isBlank(registerVO.getNickname()) ? registerVO.getUsername() : registerVO.getNickname());
        user.setRegisterTime(new Date());
        user.setStatus(1);
        user.setVipLevel(0);
        user.setCreateBy(registerVO.getUsername());
        user.setUpdateBy(registerVO.getUsername());
        userMapper.insert(user);
        
        // 初始化用户统计数据
        UserStats userStats = new UserStats();
        userStats.setId(IdGenerator.nextId());
        userStats.setUserId(user.getId());
        userStats.setTotalMerit(0L);
        userStats.setMeritCoins(0L);
        userStats.setTotalKnocks(0L);
        userStats.setCurrentLevel(1);
        userStats.setCreateBy(registerVO.getUsername());
        userStats.setUpdateBy(registerVO.getUsername());
        userStatsMapper.insert(userStats);
        
        return user.getId();
    }
    
    @Override
    public UserVO login(LoginVO loginVO) {
        // 根据用户名查询用户
        User user = userMapper.selectByUsername(loginVO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证密码
        String encryptPassword = SecureUtil.md5(loginVO.getPassword());
        if (!encryptPassword.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用或冻结");
        }
        
        // 更新最后登录时间
        userMapper.updateLastLoginTime(user.getId());
        
        // 转换为VO
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        userVO.setPassword(null);
        
        return userVO;
    }
    
    @Override
    public UserVO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        userVO.setPassword(null);
        
        // 查询用户统计信息
        UserStats userStats = userStatsMapper.selectByUserId(userId);
        if (userStats != null) {
            userVO.setTotalMerit(userStats.getTotalMerit());
            userVO.setMeritCoins(userStats.getMeritCoins());
            userVO.setCurrentLevel(userStats.getCurrentLevel());
        }
        
        return userVO;
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUser(UserVO userVO) {
        User user = new User();
        BeanUtil.copyProperties(userVO, user);
        user.setUpdateTime(new Date());
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证旧密码
        String encryptOldPassword = SecureUtil.md5(oldPassword);
        if (!encryptOldPassword.equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码
        user.setPassword(SecureUtil.md5(newPassword));
        user.setUpdateTime(new Date());
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    public Page<UserVO> getUserPage(UserQueryVO queryVO) {
        Page<User> page = new Page<>(queryVO.getPageNum(), queryVO.getPageSize());
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryVO.getUsername()), User::getUsername, queryVO.getUsername());
        wrapper.like(StrUtil.isNotBlank(queryVO.getNickname()), User::getNickname, queryVO.getNickname());
        wrapper.eq(queryVO.getStatus() != null, User::getStatus, queryVO.getStatus());
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> userPage = userMapper.selectPage(page, wrapper);
        
        // 转换为VO
        Page<UserVO> voPage = new Page<>();
        BeanUtil.copyProperties(userPage, voPage, "records");
        voPage.setRecords(BeanUtil.copyToList(userPage.getRecords(), UserVO.class));
        
        return voPage;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        user.setUpdateTime(new Date());
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean resetPassword(Long userId, String newPassword) {
        User user = new User();
        user.setId(userId);
        user.setPassword(SecureUtil.md5(newPassword));
        user.setUpdateTime(new Date());
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    public Boolean checkUsernameExists(String username) {
        return userMapper.selectByUsername(username) != null;
    }
    
    @Override
    public Boolean checkPhoneExists(String phone) {
        return userMapper.selectByPhone(phone) != null;
    }
    
    @Override
    public Boolean checkEmailExists(String email) {
        return userMapper.selectByEmail(email) != null;
    }
}