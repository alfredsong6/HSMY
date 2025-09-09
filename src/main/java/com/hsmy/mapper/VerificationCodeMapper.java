package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 验证码Mapper
 */
@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {
    
    /**
     * 获取最近发送时间
     */
    LocalDateTime getLastSendTime(@Param("account") String account, 
                                 @Param("businessType") String businessType);
    
    /**
     * 获取今日发送次数
     */
    Integer getTodaySendCount(@Param("account") String account);
}