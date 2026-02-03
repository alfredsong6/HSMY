package com.hsmy.service.impl;

import com.hsmy.entity.User;
import com.hsmy.entity.UserStats;
import com.hsmy.entity.VipBenefits;
import com.hsmy.entity.VipPackage;
import com.hsmy.entity.VipPurchase;
import com.hsmy.entity.meditation.MeritCoinTransaction;
import com.hsmy.enums.MeritBizType;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.UserMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.mapper.VipPackageMapper;
import com.hsmy.mapper.VipPurchaseMapper;
import com.hsmy.mapper.meditation.MeritCoinTransactionMapper;
import com.hsmy.service.VipPurchaseService;
import com.hsmy.utils.IdGenerator;
import com.hsmy.vo.VipMyInfoVO;
import com.hsmy.vo.VipPurchaseResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * VIP purchase service implementation.
 */
@Service
@RequiredArgsConstructor
public class VipPurchaseServiceImpl implements VipPurchaseService {

    private static final DateTimeFormatter ORDER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final VipPackageMapper vipPackageMapper;
    private final VipPurchaseMapper vipPurchaseMapper;
    private final UserMapper userMapper;
    private final UserStatsMapper userStatsMapper;
    private final MeritCoinTransactionMapper meritCoinTransactionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VipPurchaseResultVO purchaseVip(Long userId, Long packageId, String paymentMethod) {
        if (userId == null) {
            throw new BusinessException("User not logged in");
        }
        if (packageId == null) {
            throw new BusinessException("VIP package id is required");
        }

        VipPackage vipPackage = vipPackageMapper.selectActiveById(packageId);
        if (vipPackage == null) {
            throw new BusinessException("VIP package not found or inactive");
        }

        Integer limitTimes = vipPackage.getLimitTimes();
        if (limitTimes != null && limitTimes > 0) {
            int count = vipPurchaseMapper.countSuccessByUserAndPackage(userId, packageId);
            if (count >= limitTimes) {
                throw new BusinessException("Purchase limit reached");
            }
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("User not found");
        }

        Date now = new Date();
        Integer durationDays = vipPackage.getDurationDays();
        boolean permanent = durationDays == null || durationDays <= 0;
        Date currentExpire = user.getVipExpireTime();
        boolean currentActive = user.getVipLevel() != null
                && user.getVipLevel() > 0
                && (currentExpire == null || currentExpire.after(now));

        Date startTime = now;
        if (!permanent && currentExpire != null && currentExpire.after(now)) {
            startTime = currentExpire;
        }
        Date endTime = null;
        if (!permanent) {
            endTime = Date.from(startTime.toInstant().plusSeconds(durationDays * 24L * 3600L));
        }

        VipPurchase purchase = new VipPurchase();
        purchase.setId(IdGenerator.nextId());
        purchase.setUserId(userId);
        purchase.setPackageId(packageId);
        purchase.setOrderNo(generateOrderNo());
        purchase.setPrice(vipPackage.getPrice());
        purchase.setStartTime(startTime);
        purchase.setEndTime(endTime);
        purchase.setPaymentMethod(resolvePaymentMethod(paymentMethod));
        purchase.setPaymentStatus(1);
        purchase.setPaymentTime(now);
        vipPurchaseMapper.insert(purchase);

        Integer newVipLevel = resolveVipLevel(vipPackage);
        Date newExpireTime = endTime;

        if (permanent) {
            newExpireTime = null;
        } else if (currentActive) {
            Integer currentLevel = user.getVipLevel();
            if (currentExpire == null) {
                newVipLevel = currentLevel;
                newExpireTime = null;
            } else if (currentLevel != null && newVipLevel != null && currentLevel > newVipLevel) {
                newVipLevel = currentLevel;
            }
        }

        User update = new User();
        update.setId(userId);
        update.setVipLevel(newVipLevel);
        update.setVipExpireTime(newExpireTime);
        update.setUpdateTime(now);
        userMapper.updateById(update);
        recordMeritCoinTransaction(userId, purchase.getId(), vipPackage);

        VipPurchaseResultVO result = new VipPurchaseResultVO();
        result.setOrderNo(purchase.getOrderNo());
        result.setPackageId(packageId);
        result.setPackageName(vipPackage.getPackageName());
        result.setPrice(vipPackage.getPrice());
        result.setStartTime(purchase.getStartTime());
        result.setEndTime(purchase.getEndTime());
        result.setVipLevel(newVipLevel);
        return result;
    }

    @Override
    public VipMyInfoVO getMyVipInfo(Long userId) {
        if (userId == null) {
            throw new BusinessException("User not logged in");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("User not found");
        }

        Date now = new Date();
        Date expireTime = user.getVipExpireTime();
        boolean active = user.getVipLevel() != null
                && user.getVipLevel() > 0
                && (expireTime == null || expireTime.after(now));

        VipMyInfoVO vo = new VipMyInfoVO();
        vo.setVipLevel(user.getVipLevel());
        vo.setVipExpireTime(expireTime);
        vo.setActive(active);

        if (active) {
            VipPurchase latest = vipPurchaseMapper.selectLatestSuccessByUserId(userId);
            if (latest != null) {
                VipPackage pkg = vipPackageMapper.selectById(latest.getPackageId());
                if (pkg != null) {
                    vo.setPackageId(pkg.getId());
                    vo.setPackageName(pkg.getPackageName());
                    vo.setPackageType(pkg.getPackageType());
                    vo.setBenefits(parseBenefits(pkg.getBenefits()));
                }
            }
        }

        return vo;
    }

    private String resolvePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            return "manual";
        }
        return paymentMethod.trim();
    }

    private Integer resolveVipLevel(VipPackage vipPackage) {
        if (vipPackage == null) {
            return 0;
        }
        Integer packageType = vipPackage.getPackageType();
        return packageType != null ? packageType : 0;
    }

    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(ORDER_DATE_FORMATTER);
        long seq = Math.abs(IdGenerator.nextId() % 1_000_000L);
        String seqPart = String.format("%06d", seq);
        return "VIP" + timePart + seqPart;
    }

    private void recordMeritCoinTransaction(Long userId, Long purchaseId, VipPackage vipPackage) {
        UserStats stats = userStatsMapper.selectByUserId(userId);
        long balance = stats != null && stats.getMeritCoins() != null ? stats.getMeritCoins() : 0L;
        MeritCoinTransaction tx = new MeritCoinTransaction();
        tx.setUserId(userId);
        tx.setBizType(MeritBizType.VIP_PURCHASE.getCode());
        tx.setBizId(purchaseId);
        tx.setChangeAmount(0);
        tx.setBalanceAfter(Math.toIntExact(balance));
        String packageName = vipPackage != null ? vipPackage.getPackageName() : String.valueOf(purchaseId);
        tx.setRemark("Purchase VIP-" + packageName);
        meritCoinTransactionMapper.insert(tx);
    }

    private VipBenefits parseBenefits(String benefitsJson) {
        if (benefitsJson == null) {
            return null;
        }
        String trimmed = benefitsJson.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            if (trimmed.startsWith("[")) {
                List<Object> items = OBJECT_MAPPER.readValue(trimmed, new TypeReference<List<Object>>() {});
                VipBenefits benefits = new VipBenefits();
                benefits.put("items", items);
                return benefits;
            }
            return OBJECT_MAPPER.readValue(trimmed, VipBenefits.class);
        } catch (Exception e) {
            VipBenefits benefits = new VipBenefits();
            benefits.put("raw", benefitsJson);
            return benefits;
        }
    }
}
