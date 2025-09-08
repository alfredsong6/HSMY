package com.hsmy.service.impl;

import com.hsmy.entity.Ranking;
import com.hsmy.mapper.RankingMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.RankingService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 排行榜Service实现类
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {
    
    private final RankingMapper rankingMapper;
    private final UserStatsMapper userStatsMapper;
    
    @Override
    public List<Ranking> getRankingList(String rankType, LocalDate snapshotDate, Integer limit) {
        return rankingMapper.selectByTypeAndDate(rankType, snapshotDate, limit);
    }
    
    @Override
    public Ranking getUserRanking(Long userId, String rankType, LocalDate snapshotDate) {
        return rankingMapper.selectUserRanking(userId, rankType, snapshotDate);
    }
    
    @Override
    public List<Ranking> getTodayRanking(Integer limit) {
        return getRankingList("daily", LocalDate.now(), limit);
    }
    
    @Override
    public List<Ranking> getWeeklyRanking(Integer limit) {
        // 获取本周一的日期作为周榜快照日期
        LocalDate monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        return getRankingList("weekly", monday, limit);
    }
    
    @Override
    public List<Ranking> getTotalRanking(Integer limit) {
        return getRankingList("total", LocalDate.now(), limit);
    }
    
    @Override
    public Ranking getUserTodayRanking(Long userId) {
        return getUserRanking(userId, "daily", LocalDate.now());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean generateRankingSnapshot(String rankType, LocalDate snapshotDate) {
        try {
            List<Ranking> rankings = new ArrayList<>();
            
            // 根据不同榜单类型生成快照
            switch (rankType) {
                case "daily":
                    // 生成日榜快照，基于当日功德值排序
                    // TODO: 实现具体的排行榜生成逻辑
                    break;
                case "weekly":
                    // 生成周榜快照，基于本周功德值排序
                    // TODO: 实现具体的排行榜生成逻辑
                    break;
                case "total":
                    // 生成总榜快照，基于总功德值排序
                    // TODO: 实现具体的排行榜生成逻辑
                    break;
                default:
                    throw new RuntimeException("不支持的榜单类型：" + rankType);
            }
            
            if (!rankings.isEmpty()) {
                // 为每个排名生成ID
                for (int i = 0; i < rankings.size(); i++) {
                    Ranking ranking = rankings.get(i);
                    ranking.setId(IdGenerator.nextId());
                    ranking.setRankingPosition(i + 1);
                    ranking.setSnapshotDate(convertLocalDateToDate(snapshotDate));
                }
                
                // 批量插入排行榜数据
                return rankingMapper.batchInsert(rankings) > 0;
            }
            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("生成排行榜快照失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanExpiredRankings(LocalDate beforeDate) {
        return rankingMapper.deleteBeforeDate(beforeDate);
    }
    
    /**
     * 将LocalDate转换为Date
     */
    private Date convertLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant());
    }
}