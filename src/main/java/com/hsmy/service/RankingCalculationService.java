package com.hsmy.service;

import com.hsmy.entity.Ranking;
import java.util.Date;
import java.util.List;

/**
 * 排名计算服务接口
 *
 * @author HSMY
 * @date 2025/09/18
 */
public interface RankingCalculationService {

    /**
     * 生成日榜数据
     *
     * @param date 日期
     * @return 生成的排名数量
     */
    int generateDailyRanking(Date date);

    /**
     * 生成周榜数据
     *
     * @param date 日期（所在周的任意一天）
     * @return 生成的排名数量
     */
    int generateWeeklyRanking(Date date);

    /**
     * 生成月榜数据
     *
     * @param date 日期（所在月的任意一天）
     * @return 生成的排名数量
     */
    int generateMonthlyRanking(Date date);

    /**
     * 生成总榜数据
     *
     * @param date 快照日期
     * @return 生成的排名数量
     */
    int generateTotalRanking(Date date);

    /**
     * 清理过期的排名数据
     *
     * @param daysToKeep 保留天数
     * @return 清理的记录数
     */
    int cleanOldRankings(int daysToKeep);
}