package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.DailyWishRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 每日愿望记录Mapper
 *
 * @author HSMY
 * @date 2025/11/17
 */
@Mapper
public interface DailyWishRecordMapper extends BaseMapper<DailyWishRecord> {

    /**
     * 根据用户ID查询愿望记录
     *
     * @param userId 用户ID
     * @return 愿望记录列表
     */
    List<DailyWishRecord> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询指定时间范围内的愿望记录
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 愿望记录列表
     */
    List<DailyWishRecord> selectByUserIdAndTimeRange(@Param("userId") Long userId,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);

    /**
     * 查询用户最近一次愿望记录
     *
     * @param userId 用户ID
     * @return 愿望记录
     */
    DailyWishRecord selectLatestByUserId(@Param("userId") Long userId);
}
