package com.hsmy.service.impl;

import com.hsmy.config.FileStorageProperties;
import com.hsmy.entity.Ranking;
import com.hsmy.mapper.RankingMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.RankingService;
import com.hsmy.utils.DateUtil;
import com.hsmy.utils.IdGenerator;
import com.hsmy.vo.RankingUserVO;
import com.hsmy.vo.RankingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private final FileStorageProperties fileStorageProperties;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String TOTAL_RANKING_CACHE_KEY_PREFIX = "hsmy:ranking:total:";
    private static final Duration TOTAL_RANKING_TTL = Duration.ofMinutes(60);
    
    @Override
    public List<Ranking> getRankingList(String rankType, LocalDate snapshotDate, Integer limit) {
        Date queryDate = DateUtil.localDateToDate(snapshotDate);
        return rankingMapper.selectByTypeAndDate(rankType, queryDate, limit);
    }
    
    @Override
    public Ranking getUserRanking(Long userId, String rankType, LocalDate snapshotDate) {
        return rankingMapper.selectUserRanking(userId, rankType, DateUtil.localDateToDate(snapshotDate));
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
    public List<RankingVO> getTotalRanking(Integer limit) {
        // String cacheKey = buildTotalRankingCacheKey(limit);
        Object cached = redisTemplate.opsForValue().get(TOTAL_RANKING_CACHE_KEY_PREFIX);
        List<Ranking> rankings = null;
        if (cached instanceof List) {
            try {
                @SuppressWarnings("unchecked")
                List<Ranking> cachedRankings = (List<Ranking>) cached;
                rankings = cachedRankings;
            } catch (ClassCastException ignored) {
                rankings = null;
            }
        }
        
        if (rankings == null) {
            rankings = getRankingList("total", LocalDate.now(), limit);
            // enrichAvatarForRankings(rankings);
            redisTemplate.opsForValue().set(TOTAL_RANKING_CACHE_KEY_PREFIX, rankings, TOTAL_RANKING_TTL.getSeconds(), TimeUnit.SECONDS);
        }
        if (rankings == null || rankings.isEmpty()) {
            return Collections.emptyList();
        }
        return rankings.stream()
                .map(this::convertToRankingVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public Ranking getUserTodayRanking(Long userId) {
        return getUserRanking(userId, "daily", LocalDate.now());
    }
    
    @Override
    public Ranking getUserWeeklyRanking(Long userId) {
        // 获取本周一的日期作为周榜快照日期
        LocalDate monday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        return getUserRanking(userId, "weekly", monday);
    }
    
    @Override
    public Ranking getUserTotalRanking(Long userId) {
        return getUserRanking(userId, "total", LocalDate.now());
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
                    ranking.setSnapshotDate(DateUtil.localDateToDate(snapshotDate));
                }
                
                // 批量插入排行榜数据
                return rankingMapper.batchInsert(rankings) > 0;
            }
            
            // 刷新缓存
            evictTotalRankingCache();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("生成排行榜快照失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer cleanExpiredRankings(LocalDate beforeDate) {
        return rankingMapper.deleteBeforeDate(DateUtil.localDateToDate(beforeDate));
    }
    
    private RankingVO convertToRankingVO(Ranking ranking) {
        if (ranking == null) {
            return null;
        }
        RankingVO vo = new RankingVO();
        vo.setUserId(ranking.getUserId() == null ? null : String.valueOf(ranking.getUserId()));
        vo.setRankType(ranking.getRankType());
        vo.setMeritValue(ranking.getMeritValue());
        vo.setRankingPosition(ranking.getRankingPosition());
        vo.setSnapshotDate(ranking.getSnapshotDate());
        vo.setPeriod(ranking.getPeriod());
        if (ranking.getUser() != null) {
            RankingUserVO userVO = new RankingUserVO();
            userVO.setId(ranking.getUser().getId() == null ? null : String.valueOf(ranking.getUser().getId()));
            userVO.setNickname(ranking.getUser().getNickname());
            userVO.setAvatarUrl(ranking.getUser().getAvatarUrl());
            userVO.setAvatarBase64Encoded(ranking.getUser().isAvatarBase64Encoded());
            userVO.setAvatarBase64Content(ranking.getUser().getAvatarBase64Content());
            vo.setUser(userVO);
        }
        return vo;
    }
    
    private void enrichAvatarForRankings(List<Ranking> rankings) {
        if (rankings == null || rankings.isEmpty()) {
            return;
        }
        for (Ranking ranking : rankings) {
            if (ranking == null || ranking.getUser() == null) {
                continue;
            }
            String avatarUrl = ranking.getUser().getAvatarUrl();
            if (!StringUtils.hasText(avatarUrl)) {
                continue;
            }
            if (avatarUrl.toLowerCase().startsWith("https")) {
                continue;
            }
            try {
                String relativePath = resolveRelativePath(avatarUrl);
                Path avatarPath = Paths.get(fileStorageProperties.getLocal().getRootPath(), relativePath);
                if (!Files.exists(avatarPath)) {
                    continue;
                }
                byte[] fileBytes = Files.readAllBytes(avatarPath);
                String mimeType = resolveMimeType(avatarPath);
                String base64Data = Base64.getEncoder().encodeToString(fileBytes);
                ranking.getUser().setAvatarBase64Content("data:" + mimeType + ";base64," + base64Data);
                ranking.getUser().setAvatarBase64Encoded(true);
            } catch (Exception e) {
                // 保持静默，避免影响排行榜返回
            }
        }
    }
    
    private String resolveRelativePath(String avatarUrl) {
        String relativePath = avatarUrl;
        try {
            if (avatarUrl.contains("://")) {
                relativePath = URI.create(avatarUrl).getPath();
            }
        } catch (Exception e) {
            // ignore
        }
        String urlPrefix = fileStorageProperties.getLocal().getUrlPrefix();
        if (StringUtils.hasText(urlPrefix) && relativePath.startsWith(urlPrefix)) {
            relativePath = relativePath.substring(urlPrefix.length());
        }
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return relativePath;
    }
    
    private String resolveMimeType(Path path) {
        try {
            String detected = Files.probeContentType(path);
            if (StringUtils.hasText(detected)) {
                return detected;
            }
        } catch (Exception e) {
            // ignore
        }
        String filename = path.getFileName().toString().toLowerCase();
        if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else if (filename.endsWith(".bmp")) {
            return "image/bmp";
        } else if (filename.endsWith(".webp")) {
            return "image/webp";
        }
        return "application/octet-stream";
    }
    
    private String buildTotalRankingCacheKey(Integer limit) {
        int safeLimit = (limit != null && limit > 0) ? limit : 0;
        return TOTAL_RANKING_CACHE_KEY_PREFIX + safeLimit;
    }
    
    private void evictTotalRankingCache() {
        Set<String> keys = redisTemplate.keys(TOTAL_RANKING_CACHE_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
