package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.Ranking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 排行榜Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Mapper
public interface RankingMapper extends BaseMapper<Ranking> {
    
    /**
     * 根据榜单类型和日期查询排行榜
     * 
     * @param rankType 榜单类型
     * @param snapshotDate 快照日期
     * @param limit 查询条数
     * @return 排行榜列表
     */
    List<Ranking> selectByTypeAndDate(@Param("rankType") String rankType, 
                                     @Param("snapshotDate") LocalDate snapshotDate, 
                                     @Param("limit") Integer limit);
    
    /**
     * 查询用户在指定榜单中的排名
     * 
     * @param userId 用户ID
     * @param rankType 榜单类型
     * @param snapshotDate 快照日期
     * @return 用户排名信息
     */
    Ranking selectUserRanking(@Param("userId") Long userId, 
                             @Param("rankType") String rankType, 
                             @Param("snapshotDate") LocalDate snapshotDate);
    
    /**
     * 批量插入排行榜数据
     * 
     * @param rankings 排行榜列表
     * @return 影响行数
     */
    int batchInsert(@Param("rankings") List<Ranking> rankings);
    
    /**
     * 删除指定日期之前的排行榜数据
     * 
     * @param beforeDate 截止日期
     * @return 影响行数
     */
    int deleteBeforeDate(@Param("beforeDate") LocalDate beforeDate);
}