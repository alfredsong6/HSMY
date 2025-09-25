package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.DimTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * Mapper for time dimension records.
 */
@Mapper
public interface DimTimeMapper extends BaseMapper<DimTime> {

    DimTime selectByDate(@Param("date") Date date);
}
