package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.UserScriptureProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserScriptureProgressMapper extends BaseMapper<UserScriptureProgress> {

    /**
     * 查询用户在分段上的进度
     */
    UserScriptureProgress selectByUserAndSection(@Param("userId") Long userId, @Param("sectionId") Long sectionId);

    /**
     * 查询用户最近阅读的分段
     */
    UserScriptureProgress selectLatestByUserAndScripture(@Param("userId") Long userId, @Param("scriptureId") Long scriptureId);

    /**
     * 统计已完成分段数量
     */
    Integer countCompletedByScripture(@Param("userId") Long userId, @Param("scriptureId") Long scriptureId);
}
