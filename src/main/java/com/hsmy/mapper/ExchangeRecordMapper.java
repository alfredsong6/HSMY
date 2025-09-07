package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.ExchangeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 功德兑换记录Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Mapper
public interface ExchangeRecordMapper extends BaseMapper<ExchangeRecord> {
    
    /**
     * 查询用户兑换记录
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 兑换记录列表
     */
    List<ExchangeRecord> selectByUserIdAndDate(@Param("userId") Long userId,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate);
    
    /**
     * 统计用户今日兑换功德币
     * 
     * @param userId 用户ID
     * @return 今日兑换功德币总数
     */
    Integer sumTodayCoins(@Param("userId") Long userId);
    
    /**
     * 统计用户累计兑换功德币
     * 
     * @param userId 用户ID
     * @return 累计兑换功德币总数
     */
    Long sumTotalCoins(@Param("userId") Long userId);
}