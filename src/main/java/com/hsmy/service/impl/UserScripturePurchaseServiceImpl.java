package com.hsmy.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsmy.entity.Scripture;
import com.hsmy.entity.ScriptureSection;
import com.hsmy.entity.UserScripturePurchase;
import com.hsmy.entity.UserStats;
import com.hsmy.entity.meditation.MeritCoinTransaction;
import com.hsmy.enums.MeritBizType;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.ScriptureMapper;
import com.hsmy.mapper.UserScripturePurchaseMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.mapper.meditation.MeritCoinTransactionMapper;
import com.hsmy.service.ScriptureSectionService;
import com.hsmy.service.UserScriptureProgressService;
import com.hsmy.service.UserScripturePurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 用户典籍购买记录Service实现类
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Service
@RequiredArgsConstructor
public class UserScripturePurchaseServiceImpl implements UserScripturePurchaseService {

    private final UserScripturePurchaseMapper userScripturePurchaseMapper;
    private final ScriptureMapper scriptureMapper;
    private final UserStatsMapper userStatsMapper;
    private final MeritCoinTransactionMapper meritCoinTransactionMapper;
    private final UserScriptureProgressService userScriptureProgressService;
    private final ScriptureSectionService scriptureSectionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean purchaseScripture(Long userId, Long scriptureId, Integer purchaseMonths) {
//        if (purchaseMonths == null || purchaseMonths <= 0) {
//            throw new BusinessException("购买月数必须大于0");
//        }
        Scripture scripture = scriptureMapper.selectById(scriptureId);
        ensureScriptureAvailable(scripture);
        Integer pricePerMonth = scripture.getPrice();
        ensurePriceConfigured(pricePerMonth);

        UserScripturePurchase existingPurchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        refreshExpiredFlag(existingPurchase);
        if (isPurchaseActive(existingPurchase)) {
            throw new BusinessException("典籍仍在有效期内，请勿重复购买");
        }

        int totalAmount = pricePerMonth * purchaseMonths;
        long balanceAfter = deductMeritCoins(userId, totalAmount);

        Date now = new Date();
        UserScripturePurchase purchase = new UserScripturePurchase();
        purchase.setUserId(userId);
        purchase.setScriptureId(scriptureId);
        purchase.setPurchaseType("lease");
        purchase.setMeritCoinsPaid(totalAmount);
        purchase.setPurchaseMonths(purchaseMonths);
        purchase.setPurchaseTime(now);
        purchase.setActivatedTime(now);
        purchase.setExpireTime(calculateExpireTime(now, purchaseMonths));
        purchase.setStatus(1);
        purchase.setIsExpired(0);
        purchase.setReadCount(0);
        purchase.setLastReadingPosition(0);
        purchase.setLastSectionId(null);
        purchase.setCompletedSections(0);
        applyReadingSnapshot(existingPurchase, purchase);

        int result = userScripturePurchaseMapper.insert(purchase);
        if (result <= 0) {
            throw new BusinessException("保存订阅记录失败，请稍后再试");
        }

        scriptureMapper.increasePurchaseCount(scriptureId);
        recordMeritCoinTransaction(userId, purchase.getId(), MeritBizType.SCRIPTURE_SUBSCRIBE,
                String.format("订阅典籍-%s", scripture.getScriptureName()), totalAmount, balanceAfter);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean purchaseScripturePermanent(Long userId, Long scriptureId) {
        Scripture scripture = scriptureMapper.selectById(scriptureId);
        ensureScriptureAvailable(scripture);
        Integer permanentPrice = scripture.getPermanentPrice();
        if (permanentPrice == null || permanentPrice <= 0) {
            throw new BusinessException("该典籍暂不支持买断");
        }

        UserScripturePurchase existingPurchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        refreshExpiredFlag(existingPurchase);
        if (existingPurchase != null && existingPurchase.getExpireTime() == null && (existingPurchase.getIsExpired() == null || existingPurchase.getIsExpired() == 0)) {
            throw new BusinessException("您已买断该典籍，无需重复购买");
        }

        long balanceAfter = deductMeritCoins(userId, permanentPrice);

        Date now = new Date();
        if (existingPurchase == null) {
            UserScripturePurchase purchase = new UserScripturePurchase();
            purchase.setUserId(userId);
            purchase.setScriptureId(scriptureId);
            purchase.setPurchaseType("permanent");
            purchase.setMeritCoinsPaid(permanentPrice);
            purchase.setPurchaseMonths(0);
            purchase.setPurchaseTime(now);
            purchase.setActivatedTime(now);
            purchase.setExpireTime(null);
            purchase.setStatus(1);
            purchase.setIsExpired(0);
            purchase.setReadCount(0);
            purchase.setLastReadingPosition(0);
            purchase.setLastSectionId(null);
            purchase.setCompletedSections(0);
            applyReadingSnapshot(existingPurchase, purchase);

            int result = userScripturePurchaseMapper.insert(purchase);
            if (result <= 0) {
                throw new BusinessException("保存买断记录失败，请稍后再试");
            }

            // 初始化首段进度为0，便于后续整体进度计算
            ScriptureSection firstSection = scriptureSectionService.getFirstSection(scriptureId);
            if (firstSection != null) {
                userScriptureProgressService.saveSectionProgress(userId, scriptureId, firstSection.getId(), 0D, 0, 0, false);
            }
        } else {
            // 已有 trial/lease 记录，升级为永久，保留已有阅读进度等数据
            existingPurchase.setPurchaseType("permanent");
            existingPurchase.setMeritCoinsPaid((existingPurchase.getMeritCoinsPaid() == null ? 0 : existingPurchase.getMeritCoinsPaid()) + permanentPrice);
            existingPurchase.setPurchaseMonths(0);
            existingPurchase.setPurchaseTime(now);
            existingPurchase.setActivatedTime(now);
            existingPurchase.setExpireTime(null);
            existingPurchase.setStatus(1);
            existingPurchase.setIsExpired(0);
            // 保留 readCount/lastSectionId/completedSections/readingProgress/lastReadingPosition 等
            int result = userScripturePurchaseMapper.updateById(existingPurchase);
            if (result <= 0) {
                throw new BusinessException("保存买断记录失败，请稍后再试");
            }
        }

        scriptureMapper.increasePurchaseCount(scriptureId);
        recordMeritCoinTransaction(userId, scriptureId, MeritBizType.SCRIPTURE_PERMANENT,
                String.format("买断典籍-%s", scripture.getScriptureName()), permanentPrice, balanceAfter);
        return true;
    }

    @Override
    public List<UserScripturePurchase> getPurchasesByUserId(Long userId) {
        return userScripturePurchaseMapper.selectByUserId(userId);
    }

    @Override
    public Page<UserScripturePurchase> getPurchasesByUserId(Long userId, Integer pageNum, Integer pageSize) {
        Page<UserScripturePurchase> page = new Page<>(pageNum, pageSize);
        return userScripturePurchaseMapper.selectByUserIdPage(page, userId);
    }

    @Override
    public List<UserScripturePurchase> getValidPurchasesByUserId(Long userId) {
        return userScripturePurchaseMapper.selectValidPurchasesByUserId(userId);
    }

    @Override
    public Boolean hasUserPurchased(Long userId, Long scriptureId) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectValidPurchaseByUserAndScripture(userId, scriptureId);
        if (purchase == null) {
            return false;
        }
        return purchase.getStatus().equals(1) && purchase.getExpireTime() == null;
    }

    @Override
    public Boolean isUserPurchaseValid(Long userId, Long scriptureId) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectValidPurchaseByUserAndScripture(userId, scriptureId);
        refreshExpiredFlag(purchase);
        if (purchase == null) {
            return false;
        }

        if ((purchase.getStatus() != null && purchase.getStatus() != 1) || (purchase.getIsExpired() != null && purchase.getIsExpired() == 1)) {
            return false;
        }

        if (purchase.getExpireTime() == null) {
            return true;
        }
        return purchase.getExpireTime().after(new Date());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateReadingProgress(Long userId, Long scriptureId, Double readingProgress) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (purchase == null) {
            return false;
        }

        int result = userScripturePurchaseMapper.updateReadingRecord(purchase.getId(), readingProgress, new Date());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateLastReadingPosition(Long userId, Long scriptureId, Integer lastReadingPosition) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (purchase == null) {
            return false;
        }

        int result = userScripturePurchaseMapper.updateLastReadingPosition(purchase.getId(), lastReadingPosition, new Date());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSectionProgress(Long userId, Long scriptureId, Long sectionId,
                                         Integer lastPosition, Double sectionProgress,
                                         Double totalProgress, Integer spendSeconds, boolean completed) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (purchase == null) {
            return false;
        }
        refreshExpiredFlag(purchase);
        if (!isPurchaseActive(purchase)) {
            return false;
        }

        boolean sectionDone = completed || (sectionProgress != null && sectionProgress >= 100D);
        userScriptureProgressService.saveSectionProgress(userId, scriptureId, sectionId, sectionProgress, lastPosition, spendSeconds, sectionDone);
        Integer completedSections = userScriptureProgressService.countCompletedSections(userId, scriptureId);
        int completedCount = completedSections == null ? 0 : completedSections;
        Date now = new Date();
        Double safeTotalProgress = totalProgress == null
                ? (purchase.getReadingProgress() == null ? 0D : purchase.getReadingProgress().doubleValue())
                : totalProgress;
        int result = userScripturePurchaseMapper.updateSectionSnapshot(
                purchase.getId(),
                safeTotalProgress,
                lastPosition,
                sectionId,
                completedCount,
                now,
                1,
                0);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean recordUserReading(Long userId, Long scriptureId) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        refreshExpiredFlag(purchase);
        if (!isPurchaseActive(purchase)) {
            return false;
        }

        int result1 = userScripturePurchaseMapper.increaseReadCount(purchase.getId());
        int result2 = scriptureMapper.increaseReadCount(scriptureId);

        return result1 > 0 && result2 > 0;
    }

    @Override
    public List<UserScripturePurchase> getExpiringSoonPurchases(Integer days) {
        return userScripturePurchaseMapper.selectExpiringSoon(days);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateExpiredStatus() {
        return userScripturePurchaseMapper.batchUpdateExpiredStatus();
    }

    @Override
    public Integer countUserPurchases(Long userId) {
        return userScripturePurchaseMapper.countPurchasesByUserId(userId);
    }

    @Override
    public Integer countScripturePurchasers(Long scriptureId) {
        return userScripturePurchaseMapper.countUsersByScriptureId(scriptureId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserScripturePurchase ensureTrialPurchase(Long userId, Long scriptureId) {
        UserScripturePurchase existing = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (existing != null) {
            return existing;
        }
        Scripture scripture = scriptureMapper.selectById(scriptureId);
        ensureScriptureAvailable(scripture);

        UserScripturePurchase trial = new UserScripturePurchase();
        Date now = new Date();
        trial.setUserId(userId);
        trial.setScriptureId(scriptureId);
        trial.setPurchaseType("trial");
        trial.setMeritCoinsPaid(0);
        trial.setPurchaseMonths(0);
        trial.setPurchaseTime(now);
        trial.setActivatedTime(now);
        trial.setExpireTime(null);
        trial.setStatus(1);
        trial.setIsExpired(0);
        trial.setReadCount(0);
        trial.setLastReadingPosition(0);
        trial.setLastSectionId(null);
        trial.setCompletedSections(0);
        trial.setReadingProgress(BigDecimal.ZERO);
        trial.setIsDeleted(0);
        userScripturePurchaseMapper.insert(trial);
        return trial;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserScripturePurchase ensureFreePermanentPurchase(Long userId, Scripture scripture) {
        UserScripturePurchase existing = userScripturePurchaseMapper.selectByUserAndScripture(userId, scripture.getId());
        if (existing != null && "permanent".equalsIgnoreCase(existing.getPurchaseType())) {
            return existing;
        }
        ensureScriptureAvailable(scripture);
        if (scripture.getPrice() == null || scripture.getPrice() != 0) {
            throw new BusinessException("该典籍并非免费，无法创建免费购买记录");
        }
        Date now = new Date();
        UserScripturePurchase free = new UserScripturePurchase();
        free.setUserId(userId);
        free.setScriptureId(scripture.getId());
        free.setPurchaseType("permanent");
        free.setMeritCoinsPaid(0);
        free.setPurchaseMonths(0);
        free.setPurchaseTime(now);
        free.setActivatedTime(now);
        free.setExpireTime(null);
        free.setStatus(1);
        free.setIsExpired(0);
        free.setReadCount(0);
        free.setLastReadingPosition(0);
        free.setLastSectionId(existing != null ? existing.getLastSectionId() : null);
        free.setCompletedSections(existing != null ? existing.getCompletedSections() : 0);
        free.setReadingProgress(existing != null && existing.getReadingProgress() != null ? existing.getReadingProgress() : BigDecimal.ZERO);
        free.setIsDeleted(0);
        userScripturePurchaseMapper.insert(free);
        return free;
    }

    @Override
    public UserScripturePurchase getUserPurchaseDetail(Long userId, Long scriptureId) {
        return userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean renewScripture(Long userId, Long scriptureId, Integer extendMonths) {
        if (extendMonths == null || extendMonths <= 0) {
            throw new BusinessException("续费月数必须在1-12月之间");
        }
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (purchase == null) {
            throw new BusinessException("尚未购买该典籍，无法续费");
        }
        if (purchase.getExpireTime() == null) {
            throw new BusinessException("买断典籍无需续费");
        }

        Scripture scripture = scriptureMapper.selectById(scriptureId);
        ensureScriptureAvailable(scripture);
        Integer pricePerMonth = scripture.getPrice();
        ensurePriceConfigured(pricePerMonth);

        int totalAmount = pricePerMonth * extendMonths;
        long balanceAfter = deductMeritCoins(userId, totalAmount);

        Date baseTime = purchase.getExpireTime().after(new Date()) ? purchase.getExpireTime() : new Date();
        Date newExpireTime = calculateExpireTime(baseTime, extendMonths);

        purchase.setExpireTime(newExpireTime);
        purchase.setIsExpired(0);
        purchase.setStatus(1);
        purchase.setMeritCoinsPaid((purchase.getMeritCoinsPaid() == null ? 0 : purchase.getMeritCoinsPaid()) + totalAmount);
        purchase.setPurchaseMonths((purchase.getPurchaseMonths() == null ? 0 : purchase.getPurchaseMonths()) + extendMonths);

        int result = userScripturePurchaseMapper.updateById(purchase);
        if (result <= 0) {
            throw new BusinessException("续费失败，请稍后再试");
        }
        recordMeritCoinTransaction(userId, purchase.getId(), MeritBizType.SCRIPTURE_RENEW,
                String.format("续费典籍-%s", scripture.getScriptureName()), totalAmount, balanceAfter);
        return true;
    }

    private void ensureScriptureAvailable(Scripture scripture) {
        if (scripture == null || scripture.getStatus() == null || scripture.getStatus() != 1) {
            throw new BusinessException("该典籍不存在或已下架");
        }
    }

    private void ensurePriceConfigured(Integer price) {
        if (price == null || price <= 0) {
            throw new BusinessException("该典籍暂未配置价格");
        }
    }

    private void refreshExpiredFlag(UserScripturePurchase purchase) {
        if (purchase == null) {
            return;
        }
        if (purchase.getExpireTime() != null && purchase.getExpireTime().before(new Date())
                && (purchase.getIsExpired() == null || purchase.getIsExpired() == 0)) {
            purchase.setIsExpired(1);
            purchase.setStatus(2);
            userScripturePurchaseMapper.updateById(purchase);
        }
    }

    private boolean isPurchaseActive(UserScripturePurchase purchase) {
        if (purchase == null) {
            return false;
        }
        if ((purchase.getStatus() != null && purchase.getStatus() != 1)
                || (purchase.getIsExpired() != null && purchase.getIsExpired() == 1)) {
            return false;
        }
        if (purchase.getExpireTime() == null) {
            return true;
        }
        return purchase.getExpireTime().after(new Date());
    }

    private long deductMeritCoins(Long userId, int amount) {
        if (amount <= 0) {
            return queryRemainingCoins(userId);
        }
        UserStats stats = userStatsMapper.selectByUserId(userId);
        long balance = stats != null && stats.getMeritCoins() != null ? stats.getMeritCoins() : 0L;
        if (balance < amount) {
            throw new BusinessException("功德币余额不足，当前余额为" + balance + "，需要" + amount);
        }
        int updated = userStatsMapper.reduceMeritCoins(userId, (long) amount);
        if (updated <= 0) {
            throw new BusinessException("扣除功德币失败，请稍后再试");
        }
        return balance - amount;
    }

    private Date calculateExpireTime(Date base, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    private void applyReadingSnapshot(UserScripturePurchase source, UserScripturePurchase target) {
        if (source == null || target == null) {
            return;
        }
        if (source.getReadCount() != null) {
            target.setReadCount(source.getReadCount());
        }
        if (source.getLastReadTime() != null) {
            target.setLastReadTime(source.getLastReadTime());
        }
        if (source.getReadingProgress() != null) {
            target.setReadingProgress(source.getReadingProgress());
        }
        if (source.getLastReadingPosition() != null) {
            target.setLastReadingPosition(source.getLastReadingPosition());
        }
    }

    private void recordMeritCoinTransaction(Long userId, Long purchaseId, MeritBizType bizType,
                                            String remark, int amount, long balanceAfter) {
        MeritCoinTransaction tx = new MeritCoinTransaction();
        tx.setUserId(userId);
        tx.setBizType(bizType.getCode());
        tx.setBizId(purchaseId);
        tx.setChangeAmount(-amount);
        tx.setBalanceAfter(Math.toIntExact(balanceAfter));
        tx.setRemark(remark);
        meritCoinTransactionMapper.insert(tx);
    }

    private long queryRemainingCoins(Long userId) {
        UserStats stats = userStatsMapper.selectByUserId(userId);
        return stats != null && stats.getMeritCoins() != null ? stats.getMeritCoins() : 0L;
    }
}
