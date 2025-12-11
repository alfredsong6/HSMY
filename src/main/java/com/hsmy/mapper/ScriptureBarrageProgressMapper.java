package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.ScriptureBarrageProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ScriptureBarrageProgressMapper extends BaseMapper<ScriptureBarrageProgress> {

    /**
     * 按用户与典籍查询进度
     */
    ScriptureBarrageProgress selectByUserAndScripture(@Param("userId") Long userId, @Param("scriptureId") Long scriptureId);
}
