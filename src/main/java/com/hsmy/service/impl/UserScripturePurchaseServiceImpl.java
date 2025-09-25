package com.hsmy.service.impl;

import com.hsmy.entity.Scripture;
import com.hsmy.entity.UserScripturePurchase;
import com.hsmy.mapper.ScriptureMapper;
import com.hsmy.mapper.UserScripturePurchaseMapper;
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

    @Override
    @Transactional
    public Boolean purchaseScripture(Long userId, Long scriptureId, Integer purchaseMonths) {
        // 检查典籍是否存在且可购买
        Scripture scripture = scriptureMapper.selectById(scriptureId);
        if (scripture == null || scripture.getStatus() != 1) {
            return false;
        }

        // 检查用户是否已购买
        UserScripturePurchase existingPurchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (existingPurchase != null && existingPurchase.getIsExpired() == 0) {
            // 如果已购买且未过期，可以考虑续期逻辑
            return false;
        }

        // 创建购买记录
        UserScripturePurchase purchase = new UserScripturePurchase();
        purchase.setUserId(userId);
        purchase.setScriptureId(scriptureId);
        purchase.setMeritCoinsPaid(scripture.getPrice() * purchaseMonths);
        purchase.setPurchaseMonths(purchaseMonths);
        purchase.setPurchaseTime(new Date());

        // 计算过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, purchaseMonths);
        purchase.setExpireTime(calendar.getTime());

        purchase.setIsExpired(0);
        purchase.setReadCount(0);

        int result = userScripturePurchaseMapper.insert(purchase);

        // 更新典籍购买次数
        if (result > 0) {
            scriptureMapper.increasePurchaseCount(scriptureId);
        }

        return result > 0;
    }

    @Override
    @Transactional
    public Boolean purchaseScripturePermanent(Long userId, Long scriptureId) {
        // 检查典籍是否存在且支持买断
        Scripture scripture = scriptureMapper.selectById(scriptureId);
        if (scripture == null || scripture.getStatus() != 1 || scripture.getPermanentPrice() == null) {
            return false;
        }

        // 检查用户是否已购买
        UserScripturePurchase existingPurchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (existingPurchase != null && existingPurchase.getIsExpired() == 0) {
            // 如果已有有效购买，不允许重复购买
            return false;
        }

        // 创建买断购买记录
        UserScripturePurchase purchase = new UserScripturePurchase();
        purchase.setUserId(userId);
        purchase.setScriptureId(scriptureId);
        purchase.setMeritCoinsPaid(scripture.getPermanentPrice());
        purchase.setPurchaseMonths(0); // 买断模式设为0
        purchase.setPurchaseTime(new Date());
        purchase.setExpireTime(null); // 买断模式无过期时间
        purchase.setIsExpired(0);
        purchase.setReadCount(0);

        int result = userScripturePurchaseMapper.insert(purchase);

        // 更新典籍购买次数
        if (result > 0) {
            scriptureMapper.increasePurchaseCount(scriptureId);
        }

        return result > 0;
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

        // 如果过期时间为null，表示买断模式，永久有效
        if (purchase.getExpireTime() == null) {
            return true;
        }

        // 否则检查是否过期
        return purchase.getExpireTime().after(new Date());
    }

    @Override
    @Transactional
    public Boolean updateReadingProgress(Long userId, Long scriptureId, Double readingProgress) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (purchase == null) {
            return false;
        }

        int result = userScripturePurchaseMapper.updateReadingRecord(purchase.getId(), readingProgress, new Date());
        return result > 0;
    }

    @Override
    @Transactional
    public Boolean updateLastReadingPosition(Long userId, Long scriptureId, Integer lastReadingPosition) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (purchase == null) {
            return false;
        }

        int result = userScripturePurchaseMapper.updateLastReadingPosition(purchase.getId(), lastReadingPosition, new Date());
        return result > 0;
    }

    @Override
    @Transactional
    public Boolean recordUserReading(Long userId, Long scriptureId) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (purchase == null || purchase.getIsExpired() == 1) {
            return false;
        }

        // 增加阅读次数
        int result1 = userScripturePurchaseMapper.increaseReadCount(purchase.getId());

        // 增加典籍阅读次数
        int result2 = scriptureMapper.increaseReadCount(scriptureId);

        return result1 > 0 && result2 > 0;
    }

    @Override
    public List<UserScripturePurchase> getExpiringSoonPurchases(Integer days) {
        return userScripturePurchaseMapper.selectExpiringSoon(days);
    }

    @Override
    @Transactional
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
    @Transactional
    public Boolean renewScripture(Long userId, Long scriptureId, Integer extendMonths) {
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectByUserAndScripture(userId, scriptureId);
        if (purchase == null) {
            return false;
        }

        Scripture scripture = scriptureMapper.selectById(scriptureId);
        if (scripture == null) {
            return false;
        }

        // 计算新的过期时间
        Calendar calendar = Calendar.getInstance();
        Date baseTime = purchase.getExpireTime().after(new Date()) ? purchase.getExpireTime() : new Date();
        calendar.setTime(baseTime);
        calendar.add(Calendar.MONTH, extendMonths);

        // 更新购买记录
        purchase.setExpireTime(calendar.getTime());
        purchase.setIsExpired(0);
        purchase.setMeritCoinsPaid(purchase.getMeritCoinsPaid() + (scripture.getPrice() * extendMonths));
        purchase.setPurchaseMonths(purchase.getPurchaseMonths() + extendMonths);

        int result = userScripturePurchaseMapper.updateById(purchase);
        return result > 0;
    }
}