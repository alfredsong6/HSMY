package com.hsmy.service.impl;

import com.hsmy.entity.Scripture;
import com.hsmy.entity.UserScripturePurchase;
import com.hsmy.entity.UserStats;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.ScriptureMapper;
import com.hsmy.mapper.UserScripturePurchaseMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.service.PurchaseRecordService;
import com.hsmy.service.UserScripturePurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PurchaseRecordService purchaseRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean purchaseScripture(Long userId, Long scriptureId, Integer purchaseMonths) {
        if (purchaseMonths == null || purchaseMonths <= 0) {
            throw new BusinessException("购买月数必须大于0");
        }
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
        deductMeritCoins(userId, totalAmount);
        purchaseRecordService.createPurchaseRecord(userId, scriptureId, pricePerMonth, purchaseMonths, totalAmount);

        Date now = new Date();
        UserScripturePurchase purchase = new UserScripturePurchase();
        purchase.setUserId(userId);
        purchase.setScriptureId(scriptureId);
        purchase.setMeritCoinsPaid(totalAmount);
        purchase.setPurchaseMonths(purchaseMonths);
        purchase.setPurchaseTime(now);
        purchase.setExpireTime(calculateExpireTime(now, purchaseMonths));
        purchase.setIsExpired(0);
        purchase.setReadCount(0);
        purchase.setLastReadingPosition(0);
        applyReadingSnapshot(existingPurchase, purchase);

        int result = userScripturePurchaseMapper.insert(purchase);
        if (result <= 0) {
            throw new BusinessException("保存订阅记录失败，请稍后再试");
        }

        scriptureMapper.increasePurchaseCount(scriptureId);
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

        deductMeritCoins(userId, permanentPrice);
        purchaseRecordService.createPurchaseRecord(userId, scriptureId, permanentPrice, 1, permanentPrice);

        UserScripturePurchase purchase = new UserScripturePurchase();
        purchase.setUserId(userId);
        purchase.setScriptureId(scriptureId);
        purchase.setMeritCoinsPaid(permanentPrice);
        purchase.setPurchaseMonths(0);
        purchase.setPurchaseTime(new Date());
        purchase.setExpireTime(null);
        purchase.setIsExpired(0);
        purchase.setReadCount(0);
        purchase.setLastReadingPosition(0);
        applyReadingSnapshot(existingPurchase, purchase);

        int result = userScripturePurchaseMapper.insert(purchase);
        if (result <= 0) {
            throw new BusinessException("保存买断记录失败，请稍后再试");
        }

        scriptureMapper.increasePurchaseCount(scriptureId);
        return true;
    }

    @Override
    public List<UserScripturePurchase> getPurchasesByUserId(Long userId) {
        return userScripturePurchaseMapper.selectByUserId(userId);
    }

    @Override
    public List<UserScripturePurchase> getValidPurchasesByUserId(Long userId) {
        return userScripturePurchaseMapper.selectValidPurchasesByUserId(userId);
    }

    @Override
    public Boolean hasUserPurchased(Long userId, Long scriptureId) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        return purchase != null;
    }

    @Override
    public Boolean isUserPurchaseValid(Long userId, Long scriptureId) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (purchase == null || purchase.getIsExpired() == 1) {
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
    public Boolean recordUserReading(Long userId, Long scriptureId) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (purchase == null || purchase.getIsExpired() == 1) {
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
        deductMeritCoins(userId, totalAmount);
        purchaseRecordService.createPurchaseRecord(userId, scriptureId, pricePerMonth, extendMonths, totalAmount);

        Date baseTime = purchase.getExpireTime().after(new Date()) ? purchase.getExpireTime() : new Date();
        Date newExpireTime = calculateExpireTime(baseTime, extendMonths);

        purchase.setExpireTime(newExpireTime);
        purchase.setIsExpired(0);
        purchase.setMeritCoinsPaid((purchase.getMeritCoinsPaid() == null ? 0 : purchase.getMeritCoinsPaid()) + totalAmount);
        purchase.setPurchaseMonths((purchase.getPurchaseMonths() == null ? 0 : purchase.getPurchaseMonths()) + extendMonths);

        int result = userScripturePurchaseMapper.updateById(purchase);
        if (result <= 0) {
            throw new BusinessException("续费失败，请稍后再试");
        }
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
            userScripturePurchaseMapper.updateById(purchase);
        }
    }

    private boolean isPurchaseActive(UserScripturePurchase purchase) {
        if (purchase == null) {
            return false;
        }
        if (purchase.getIsExpired() != null && purchase.getIsExpired() == 1) {
            return false;
        }
        if (purchase.getExpireTime() == null) {
            return true;
        }
        return purchase.getExpireTime().after(new Date());
    }

    private void deductMeritCoins(Long userId, int amount) {
        if (amount <= 0) {
            return;
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
}
