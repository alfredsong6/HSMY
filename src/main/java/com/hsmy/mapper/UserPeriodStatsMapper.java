package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.UserPeriodStats;
import com.hsmy.dto.stats.UserPeriodAggregate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for user period-based statistics.
 */
@Mapper
public interface UserPeriodStatsMapper extends BaseMapper<UserPeriodStats> {

    UserPeriodStats selectByUserAndPeriod(@Param("userId") Long userId,
                                          @Param("periodType") String periodType,
                                          @Param("timeId") Long timeId);

    int upsertPeriodStats(UserPeriodStats stats);

    int replacePeriodStats(UserPeriodStats stats);

    List<UserPeriodAggregate> aggregateDailyStatsByTimeIds(@Param("timeIds") List<Long> timeIds);

    List<UserPeriodStats> selectByUserAndTypes(@Param("userId") Long userId,
                                               @Param("periodTypes") List<String> periodTypes,
                                               @Param("timeIds") List<Long> timeIds);
}
