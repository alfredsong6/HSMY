package com.hsmy.service;

import com.hsmy.entity.Ranking;

import java.time.LocalDate;
import java.util.List;

/**
 * 排行榜Service接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
public interface RankingService {
    
    /**
     * 获取排行榜数据
     * 
     * @param rankType 榜单类型
     * @param snapshotDate 快照日期
     * @param limit 查询条数
     * @return 排行榜列表
     */
    List<Ranking> getRankingList(String rankType, LocalDate snapshotDate, Integer limit);
    
    /**
     * 获取用户排名
     * 
     * @param userId 用户ID
     * @param rankType 榜单类型
     * @param snapshotDate 快照日期
     * @return 用户排名信息
     */
    Ranking getUserRanking(Long userId, String rankType, LocalDate snapshotDate);
    
    /**
     * 获取今日排行榜
     * 
     * @param limit 查询条数
     * @return 排行榜列表
     */
    List<Ranking> getTodayRanking(Integer limit);
    
    /**
     * 获取本周排行榜
     * 
     * @param limit 查询条数
     * @return 排行榜列表
     */
    List<Ranking> getWeeklyRanking(Integer limit);
    
    /**
     * 获取总排行榜
     * 
     * @param limit 查询条数
     * @return 排行榜列表
     */
    List<Ranking> getTotalRanking(Integer limit);
    
    /**
     * 获取用户今日排名
     * 
     * @param userId 用户ID
     * @return 用户排名信息
     */
    Ranking getUserTodayRanking(Long userId);
    
    /**
     * 生成排行榜快照
     * 
     * @param rankType 榜单类型
     * @param snapshotDate 快照日期
     * @return 是否成功
     */
    Boolean generateRankingSnapshot(String rankType, LocalDate snapshotDate);
    
    /**
     * 清理过期排行榜数据
     * 
     * @param beforeDate 截止日期
     * @return 清理条数
     */
    Integer cleanExpiredRankings(LocalDate beforeDate);
}