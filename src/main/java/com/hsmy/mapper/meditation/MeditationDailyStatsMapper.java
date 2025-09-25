package com.hsmy.mapper.meditation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.meditation.MeditationDailyStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface MeditationDailyStatsMapper extends BaseMapper<MeditationDailyStats> {

    MeditationDailyStats selectByUserAndDate(@Param("userId") Long userId,
                                             @Param("statDate") Date statDate);
}
