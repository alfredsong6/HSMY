package com.hsmy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.dto.RegisterByCodeRequest;
import com.hsmy.entity.User;
import com.hsmy.entity.UserStats;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.UserMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.UserService;
import com.hsmy.service.UserSettingService;
import com.hsmy.service.VerificationCodeService;
import com.hsmy.utils.IdGenerator;
import com.hsmy.vo.LoginVO;
import com.hsmy.vo.RegisterVO;
import com.hsmy.vo.UserQueryVO;
import com.hsmy.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.Random;

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
    private final VerificationCodeService verificationCodeService;
    private final UserSettingService userSettingService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(RegisterVO registerVO) {
        // 如果用户名为空，生成随机用户名
        String username = registerVO.getUsername();
        if (StrUtil.isBlank(username)) {
            // 生成随机用户名 nickname_xxxx
            username = generateRandomUsername();
            registerVO.setUsername(username);
        }
        
        // 检查用户名是否存在
        if (checkUsernameExists(username)) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查手机号是否存在
        if (StrUtil.isNotBlank(registerVO.getPhone()) && checkPhoneExists(registerVO.getPhone())) {
            throw new BusinessException("手机号已被注册");
        }
        
        // 检查邮箱是否存在
        if (StrUtil.isNotBlank(registerVO.getEmail()) && checkEmailExists(registerVO.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        BeanUtil.copyProperties(registerVO, user);
        user.setId(IdGenerator.nextId());
        
        // 密码可以为空，如果不为空则加密
        if (StrUtil.isNotBlank(registerVO.getPassword())) {
            user.setPassword(SecureUtil.md5(registerVO.getPassword()));
        } else {
            user.setPassword(""); // 设置空密码
        }
        
        // 用户名和昵称相同
        user.setNickname(username);
        user.setRegisterTime(new Date());
        user.setStatus(1);
        user.setVipLevel(0);
        user.setCreateBy(username);
        user.setUpdateBy(username);
        userMapper.insert(user);
        
        // 初始化用户统计数据
        UserStats userStats = new UserStats();
        userStats.setId(IdGenerator.nextId());
        userStats.setUserId(user.getId());
        userStats.setTotalMerit(0L);
        userStats.setMeritCoins(0L);
        userStats.setTotalKnocks(0L);
        userStats.setCurrentLevel(1);
        userStats.setCreateBy(username);
        userStats.setUpdateBy(username);
        userStatsMapper.insert(userStats);
        
        return user.getId();
    }
    
    @Override
    public UserVO login(LoginVO loginVO) {
        // 根据用户名查询用户
        User user = userMapper.selectByUsername(loginVO.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证密码
        String encryptPassword = SecureUtil.md5(loginVO.getPassword());
        if (!encryptPassword.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用或冻结");
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
    public UserVO getUserVOById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        if (userVO.getPassword() == null || Strings.isBlank(userVO.getPassword())) {
            userVO.setPassword("0");
        } else {
            userVO.setPassword("1");
        }
        
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
    public User getUserEntityById(Long userId) {
        return userMapper.selectById(userId);
    }
    
    @Override
    public User getUserByLoginAccount(String loginAccount) {
        // 先尝试用户名查找
        User user = userMapper.selectByUsername(loginAccount);
        if (user != null) {
            return user;
        }
        
        // 尝试手机号查找
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, loginAccount);
        user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            return user;
        }
        
        // 尝试邮箱查找
        queryWrapper.clear();
        queryWrapper.eq(User::getEmail, loginAccount);
        return userMapper.selectOne(queryWrapper);
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
            throw new BusinessException("用户不存在");
        }
        
        // 验证旧密码
        String encryptOldPassword = SecureUtil.md5(oldPassword);
        if (!encryptOldPassword.equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
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
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long registerByCode(RegisterByCodeRequest request) {
        // 检查账号是否已存在
        if ("phone".equals(request.getAccountType())) {
            if (checkPhoneExists(request.getAccount())) {
                throw new BusinessException("该手机号已被注册");
            }
        } else if ("email".equals(request.getAccountType())) {
            if (checkEmailExists(request.getAccount())) {
                throw new BusinessException("该邮箱已被注册");
            }
        }
        
        // 生成默认用户名
        String defaultUsername = generateDefaultUsername(request.getNickname());
        
        // 创建用户
        User user = new User();
        user.setId(IdGenerator.nextId());
        user.setUsername(defaultUsername);
        user.setNickname(StrUtil.isBlank(request.getNickname()) ? defaultUsername : request.getNickname());
        
        // 设置手机号或邮箱
        if ("phone".equals(request.getAccountType())) {
            user.setPhone(request.getAccount());
        } else if ("email".equals(request.getAccountType())) {
            user.setEmail(request.getAccount());
        }
        
        // 初始密码为空，需要用户后续设置
        user.setPassword(null);
        user.setRegisterTime(new Date());
        user.setStatus(1);
        user.setVipLevel(0);
        user.setCreateBy(defaultUsername);
        user.setUpdateBy(defaultUsername);
        
        userMapper.insert(user);
        
        // 初始化用户统计数据
        UserStats userStats = new UserStats();
        userStats.setId(IdGenerator.nextId());
        userStats.setUserId(user.getId());
        userStats.setTotalMerit(0L);
        userStats.setMeritCoins(0L);
        userStats.setTotalKnocks(0L);
        userStats.setCurrentLevel(1);
        userStats.setCreateBy(defaultUsername);
        userStats.setUpdateBy(defaultUsername);
        userStatsMapper.insert(userStats);

        Boolean initFlag = userSettingService.initUserDefaultSetting(user.getId());
        if (!initFlag) {
            throw new BusinessException("初始化用户设置失败");
        }

        return user.getId();
    }
    
    @Override
    public User getUserByPhone(String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public User getUserByEmail(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setPassword(Long userId, String password) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 使用MD5加密密码（与原有系统保持一致）
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setUpdateTime(new Date());
        
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAvatar(Long userId, String avatarUrl) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setAvatarUrl(avatarUrl);
        user.setUpdateTime(new Date());
        
        return userMapper.updateById(user) > 0;
    }
    
    /**
     * 生成默认用户名
     * 格式：nickName + 3位随机数
     */
    private String generateDefaultUsername(String nickname) {
        String baseUsername = StrUtil.isBlank(nickname) ? "user" : nickname;
        
        // 移除特殊字符，只保留字母和数字
        baseUsername = baseUsername.replaceAll("[^a-zA-Z0-9]", "");
        
        // 如果处理后为空，使用默认值
        if (StrUtil.isBlank(baseUsername)) {
            baseUsername = "user";
        }
        
        // 限制长度
        if (baseUsername.length() > 17) {
            baseUsername = baseUsername.substring(0, 17);
        }
        
        // 生成3位随机数
        Random random = new Random();
        int randomNum = random.nextInt(900) + 100; // 100-999
        
        String username = baseUsername + randomNum;
        
        // 检查用户名是否存在，如果存在则重新生成
        int retryCount = 0;
        while (checkUsernameExists(username) && retryCount < 10) {
            randomNum = random.nextInt(900) + 100;
            username = baseUsername + randomNum;
            retryCount++;
        }
        
        // 如果重试10次后仍然存在，使用时间戳
        if (checkUsernameExists(username)) {
            username = baseUsername + System.currentTimeMillis() % 1000;
        }
        
        return username;
    }
    
    /**
     * 生成随机用户名
     * 格式：nickname_xxxx
     */
    private String generateRandomUsername() {
        String baseUsername = "nickname";
        
        // 生成4位随机数
        Random random = new Random();
        int randomNum = random.nextInt(9000) + 1000; // 1000-9999
        
        String username = baseUsername + "_" + randomNum;
        
        // 检查用户名是否存在，如果存在则重新生成
        int retryCount = 0;
        while (checkUsernameExists(username) && retryCount < 10) {
            randomNum = random.nextInt(9000) + 1000;
            username = baseUsername + "_" + randomNum;
            retryCount++;
        }
        
        // 如果重试10次后仍然存在，使用时间戳
        if (checkUsernameExists(username)) {
            username = baseUsername + "_" + System.currentTimeMillis() % 10000;
        }
        
        return username;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean initializePassword(Long userId, String password) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查用户密码是否为空，只有密码为空时才允许初始化
        if (StrUtil.isNotBlank(user.getPassword())) {
            throw new BusinessException("用户已设置密码，无法初始化");
        }
        
        // 设置密码
        user.setPassword(SecureUtil.md5(password));
        user.setUpdateTime(new Date());
        
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean resetPasswordWithSms(String phone, String code, String newPassword) {
        // 验证短信验证码
        boolean isValid = verificationCodeService.verify(phone, "phone", code, "reset_password");
        if (!isValid) {
            throw new BusinessException("验证码错误或已过期");
        }
        
        // 根据手机号查找用户
        User user = getUserByPhone(phone);
        if (user == null) {
            throw new BusinessException("该手机号未注册");
        }
        
        // 重置密码
        user.setPassword(SecureUtil.md5(newPassword));
        user.setUpdateTime(new Date());
        
        return userMapper.updateById(user) > 0;
    }
}