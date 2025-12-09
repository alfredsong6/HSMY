package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.ScriptureSection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScriptureSectionMapper extends BaseMapper<ScriptureSection> {

    /**
     * 按典籍查询分段列表
     */
    List<ScriptureSection> selectByScriptureId(@Param("scriptureId") Long scriptureId);

    /**
     * 获取典籍的首段
     */
    ScriptureSection selectFirstSection(@Param("scriptureId") Long scriptureId);

    /**
     * 根据典籍与序号查询分段
     */
    ScriptureSection selectByScriptureAndNo(@Param("scriptureId") Long scriptureId, @Param("sectionNo") Integer sectionNo);
}
