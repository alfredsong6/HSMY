package com.hsmy.mapper.meditation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.meditation.MeditationSubscription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface MeditationSubscriptionMapper extends BaseMapper<MeditationSubscription> {

    MeditationSubscription selectLatestByUser(@Param("userId") Long userId);

    MeditationSubscription selectActiveByUser(@Param("userId") Long userId,
                                              @Param("now") Date now);

    int expireSubscriptions(@Param("now") Date now);
}
