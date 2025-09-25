package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.UserStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * User statistics mapper for lifetime counters.
 */
@Mapper
public interface UserStatsMapper extends BaseMapper<UserStats> {

    UserStats selectByUserId(@Param("userId") Long userId);

    int addMerit(@Param("userId") Long userId, @Param("merit") Long merit);

    int addMeritCoins(@Param("userId") Long userId, @Param("coins") Long coins);

    int reduceMeritCoins(@Param("userId") Long userId, @Param("coins") Long coins);

    int updateKnockStats(@Param("userId") Long userId,
                         @Param("knockCount") Long knockCount,
                         @Param("merit") Long merit);
}
