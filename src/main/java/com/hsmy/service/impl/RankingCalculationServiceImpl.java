package com.hsmy.service.impl;

import com.hsmy.entity.Ranking;
import com.hsmy.mapper.RankingMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.RankingCalculationService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * 排名计算服务实现类
 *
 * @author HSMY
 * @date 2025/09/18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RankingCalculationServiceImpl implements RankingCalculationService {

    private final RankingMapper rankingMapper;
    private final UserStatsMapper userStatsMapper;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateTotalRanking(Date date) {
        log.info("开始生成总榜数据，日期：{}", date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String period = "total-" + sdf.format(date);

        // 查询总功德排名数据（基于用户累计功德总值）
        List<com.hsmy.entity.UserStats> topStats = userStatsMapper.selectTopTotalMerit(1000);

        // 构建排名数据
        List<Ranking> rankings = new ArrayList<>();
        int position = 1;
        for (com.hsmy.entity.UserStats stat : topStats) {
            if (stat.getTotalMerit() == null || stat.getTotalMerit() <= 0) {
                continue;
            }
            Ranking ranking = new Ranking();
            ranking.setId(IdGenerator.nextId());
            ranking.setUserId(stat.getUserId());
            ranking.setRankType("total");
            ranking.setMeritValue(stat.getTotalMerit());
            ranking.setRankingPosition(position++);
            ranking.setSnapshotDate(date);
            ranking.setPeriod(period);
            ranking.setCreateTime(new Date());
            ranking.setCreateBy("system");
            rankings.add(ranking);
        }

        // 批量插入
        if (!rankings.isEmpty()) {
            rankingMapper.batchInsert(rankings);
            log.info("成功生成总榜数据 {} 条", rankings.size());
        }

        return rankings.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanOldRankings(int daysToKeep) {
        log.info("开始清理 {} 天前的排名数据", daysToKeep);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -daysToKeep);
        Date beforeDate = calendar.getTime();

        int deleted = rankingMapper.deleteBeforeDate(beforeDate);
        log.info("成功清理 {} 条过期排名数据", deleted);

        return deleted;
    }
}
