package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.UserSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户设置Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Mapper
public interface UserSettingMapper extends BaseMapper<UserSetting> {
    
    /**
     * 根据用户ID查询用户设置
     * 
     * @param userId 用户ID
     * @return 用户设置信息
     */
    UserSetting selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID更新设置
     * 
     * @param userSetting 用户设置信息
     * @return 影响行数
     */
    int updateByUserId(UserSetting userSetting);
}