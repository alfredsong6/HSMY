package com.hsmy.mapper.meditation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.meditation.MeditationSession;
import com.hsmy.vo.meditation.MeditationMonthStatDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface MeditationSessionMapper extends BaseMapper<MeditationSession> {

    Integer countByUserAndDate(@Param("userId") Long userId,
                               @Param("start") Date start,
                               @Param("end") Date end);

    Integer sumDurationByUserAndDate(@Param("userId") Long userId,
                                     @Param("start") Date start,
                                     @Param("end") Date end);

    Integer countTotalSessions(@Param("userId") Long userId);

    Integer sumTotalDuration(@Param("userId") Long userId);

    List<MeditationMonthStatDTO> selectMonthStats(@Param("userId") Long userId,
                                                  @Param("start") Date monthStart,
                                                  @Param("end") Date monthEnd);

    Integer countSharesByUserAndDate(@Param("userId") Long userId,
                                     @Param("start") Date start,
                                     @Param("end") Date end);
}
