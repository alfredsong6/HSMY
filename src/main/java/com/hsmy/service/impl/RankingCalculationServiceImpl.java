package com.hsmy.service.impl;

import com.hsmy.entity.Ranking;
import com.hsmy.mapper.RankingMapper;
import com.hsmy.service.RankingCalculationService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateDailyRanking(Date date) {
        log.info("开始生成日榜数据，日期：{}", date);

        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);

        // 查询当日功德排名数据
        String sql = "SELECT user_id, SUM(merit_gained) AS total_merit " +
                    "FROM t_merit_record " +
                    "WHERE DATE(create_time) = ? " +
                    "AND is_deleted = 0 " +
                    "GROUP BY user_id " +
                    "HAVING total_merit > 0 " +
                    "ORDER BY total_merit DESC " +
                    "LIMIT 1000";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, dateStr);

        // 构建排名数据
        List<Ranking> rankings = new ArrayList<>();
        int position = 1;
        for (Map<String, Object> result : results) {
            Ranking ranking = new Ranking();
            ranking.setId(IdGenerator.nextId());
            ranking.setUserId(((Number) result.get("user_id")).longValue());
            ranking.setRankType("daily");
            ranking.setMeritValue(((Number) result.get("total_merit")).longValue());
            ranking.setRankingPosition(position++);
            ranking.setSnapshotDate(date);
            ranking.setPeriod(dateStr);
            ranking.setCreateTime(new Date());
            ranking.setCreateBy("system");
            rankings.add(ranking);
        }

        // 批量插入
        if (!rankings.isEmpty()) {
            rankingMapper.batchInsert(rankings);
            log.info("成功生成日榜数据 {} 条", rankings.size());
        }

        return rankings.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateWeeklyRanking(Date date) {
        log.info("开始生成周榜数据，日期：{}", date);

        // 计算本周的开始和结束日期
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 获取周数
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = localDate.get(weekFields.weekOfYear());
        String period = String.format("%d-W%02d", localDate.getYear(), weekNumber);

        // 查询本周功德排名数据
        String sql = "SELECT user_id, SUM(merit_gained) AS total_merit " +
                    "FROM t_merit_record " +
                    "WHERE DATE(create_time) >= ? " +
                    "AND DATE(create_time) <= ? " +
                    "AND is_deleted = 0 " +
                    "GROUP BY user_id " +
                    "HAVING total_merit > 0 " +
                    "ORDER BY total_merit DESC " +
                    "LIMIT 1000";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql,
            java.sql.Date.valueOf(weekStart),
            java.sql.Date.valueOf(weekEnd));

        // 构建排名数据
        List<Ranking> rankings = new ArrayList<>();
        int position = 1;
        for (Map<String, Object> result : results) {
            Ranking ranking = new Ranking();
            ranking.setId(IdGenerator.nextId());
            ranking.setUserId(((Number) result.get("user_id")).longValue());
            ranking.setRankType("weekly");
            ranking.setMeritValue(((Number) result.get("total_merit")).longValue());
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
            log.info("成功生成周榜数据 {} 条，周期：{}", rankings.size(), period);
        }

        return rankings.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateMonthlyRanking(Date date) {
        log.info("开始生成月榜数据，日期：{}", date);

        // 计算本月的开始和结束日期
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate monthStart = localDate.withDayOfMonth(1);
        LocalDate monthEnd = localDate.withDayOfMonth(localDate.lengthOfMonth());

        String period = String.format("%d-%02d", localDate.getYear(), localDate.getMonthValue());

        // 查询本月功德排名数据
        String sql = "SELECT user_id, SUM(merit_gained) AS total_merit " +
                    "FROM t_merit_record " +
                    "WHERE DATE(create_time) >= ? " +
                    "AND DATE(create_time) <= ? " +
                    "AND is_deleted = 0 " +
                    "GROUP BY user_id " +
                    "HAVING total_merit > 0 " +
                    "ORDER BY total_merit DESC " +
                    "LIMIT 1000";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql,
            java.sql.Date.valueOf(monthStart),
            java.sql.Date.valueOf(monthEnd));

        // 构建排名数据
        List<Ranking> rankings = new ArrayList<>();
        int position = 1;
        for (Map<String, Object> result : results) {
            Ranking ranking = new Ranking();
            ranking.setId(IdGenerator.nextId());
            ranking.setUserId(((Number) result.get("user_id")).longValue());
            ranking.setRankType("monthly");
            ranking.setMeritValue(((Number) result.get("total_merit")).longValue());
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
            log.info("成功生成月榜数据 {} 条，周期：{}", rankings.size(), period);
        }

        return rankings.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateTotalRanking(Date date) {
        log.info("开始生成总榜数据，日期：{}", date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String period = "total-" + sdf.format(date);

        // 查询总功德排名数据
        String sql = "SELECT user_id, SUM(merit_gained) AS total_merit " +
                    "FROM t_merit_record " +
                    "WHERE is_deleted = 0 " +
                    "GROUP BY user_id " +
                    "HAVING total_merit > 0 " +
                    "ORDER BY total_merit DESC " +
                    "LIMIT 1000";

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        // 构建排名数据
        List<Ranking> rankings = new ArrayList<>();
        int position = 1;
        for (Map<String, Object> result : results) {
            Ranking ranking = new Ranking();
            ranking.setId(IdGenerator.nextId());
            ranking.setUserId(((Number) result.get("user_id")).longValue());
            ranking.setRankType("total");
            ranking.setMeritValue(((Number) result.get("total_merit")).longValue());
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