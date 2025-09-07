package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsername(@Param("username") String username);
    
    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    User selectByPhone(@Param("phone") String phone);
    
    /**
     * 根据邮箱查询用户
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    User selectByEmail(@Param("email") String email);
    
    /**
     * 批量查询用户信息
     * 
     * @param userIds 用户ID列表
     * @return 用户列表
     */
    List<User> selectBatchByIds(@Param("userIds") List<Long> userIds);
    
    /**
     * 更新用户最后登录时间
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    int updateLastLoginTime(@Param("userId") Long userId);
    
    /**
     * 统计用户总数
     * 
     * @return 用户总数
     */
    Long countTotal();
}