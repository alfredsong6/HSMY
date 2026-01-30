package com.hsmy.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hsmy.entity.RechargeOrder;
import com.hsmy.entity.UserStats;
import com.hsmy.entity.meditation.MeritCoinTransaction;
import com.hsmy.enums.MeritBizType;
import com.hsmy.mapper.RechargeOrderMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.mapper.meditation.MeritCoinTransactionMapper;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.model.Transaction.TradeStateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOrderProcessor {

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_SUCCESS = 1;
    private static final int STATUS_FAILED = 2;
    private static final int STATUS_REFUND = 3;
    private static final MeritBizType BIZ_TYPE_RECHARGE_PURCHASE = MeritBizType.RECHARGE_PURCHASE;
    private static final MeritBizType BIZ_TYPE_RECHARGE_BONUS = MeritBizType.RECHARGE_BONUS;

    private final RechargeOrderMapper rechargeOrderMapper;
    private final UserStatsMapper userStatsMapper;
    private final MeritCoinTransactionMapper meritCoinTransactionMapper;

    public boolean applyTradeState(String orderNo, Transaction transaction) {
        RechargeOrder order = rechargeOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.warn("订单不存在，无法更新状态，orderNo={}", orderNo);
            return false;
        }
        if (!Objects.equals(order.getPaymentStatus(), STATUS_PENDING)) {
            log.info("订单已处于终态，跳过处理，orderNo={}, currentStatus={}", orderNo, order.getPaymentStatus());
            return true;
        }

        TradeStateEnum tradeStateEnum = transaction.getTradeState();
        String transactionId = transaction.getTransactionId();
        Date paymentTime = convertToDate(transaction.getSuccessTime());
        if (tradeStateEnum == null) {
            return false;
        }

        int affectedRows;
        switch (tradeStateEnum) {
            case SUCCESS:
                affectedRows = rechargeOrderMapper.updatePaymentStatusByOrderNo(orderNo, STATUS_SUCCESS, transactionId, paymentTime);
                break;
            case REFUND:
                affectedRows = rechargeOrderMapper.updatePaymentStatusByOrderNo(orderNo, STATUS_REFUND, transactionId, paymentTime);
                break;
            case CLOSED:
            case PAYERROR:
            case REVOKED:
                affectedRows = rechargeOrderMapper.updatePaymentStatusByOrderNo(orderNo, STATUS_FAILED, transactionId, paymentTime);
                break;
            case NOTPAY:
            case USERPAYING:
            default:
                return false;
        }

        if (affectedRows > 0) {
            log.info("订单状态更新成功，orderNo={}, status={}", orderNo, tradeStateEnum);
            return true;
        }
        log.warn("订单状态更新失败，可能已被其他线程处理，orderNo={}", orderNo);
        return true;
    }

    public void grantMeritCoins(String orderNo) {
        RechargeOrder order = rechargeOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.warn("充值订单 {} 不存在，无法发放功德币", orderNo);
            return;
        }
        if (!Objects.equals(order.getPaymentStatus(), STATUS_SUCCESS)) {
            log.debug("订单 {} 当前状态非成功({})，跳过发放功德币", orderNo, order.getPaymentStatus());
            return;
        }
        if (order.getUserId() == null) {
            log.warn("订单 {} 缺少用户信息，无法发放功德币", orderNo);
            return;
        }
        int purchaseCoins = order.getMeritCoins() != null ? order.getMeritCoins() : 0;
        int bonusCoins = order.getBonusCoins() != null ? order.getBonusCoins() : 0;
        if (purchaseCoins <= 0 && bonusCoins <= 0) {
            log.info("订单 {} 未配置功德币收益，跳过发放", orderNo);
            return;
        }

        if (purchaseCoins > 0 && !existsTransaction(order.getId(), BIZ_TYPE_RECHARGE_PURCHASE)) {
            long balanceAfter = addCoinsAndGetBalance(order.getUserId(), purchaseCoins);
            recordTransaction(order, purchaseCoins, balanceAfter, BIZ_TYPE_RECHARGE_PURCHASE,
                    String.format("充值订单 %s 到账功德币", orderNo));
            log.info("订单 {} 发放 {} 功德币到账记录成功", orderNo, purchaseCoins);
        } else if (purchaseCoins > 0) {
            log.info("订单 {} 的到账功德币已处理，跳过重复发放", orderNo);
        }

        if (bonusCoins > 0 && !existsTransaction(order.getId(), BIZ_TYPE_RECHARGE_BONUS)) {
            long balanceAfter = addCoinsAndGetBalance(order.getUserId(), bonusCoins);
            recordTransaction(order, bonusCoins, balanceAfter, BIZ_TYPE_RECHARGE_BONUS,
                    String.format("充值订单 %s 赠送功德币", orderNo));
            log.info("订单 {} 发放 {} 赠送功德币记录成功", orderNo, bonusCoins);
        } else if (bonusCoins > 0) {
            log.info("订单 {} 的赠送功德币已处理，跳过重复发放", orderNo);
        }
    }

    private boolean existsTransaction(Long orderId, MeritBizType bizType) {
        LambdaQueryWrapper<MeritCoinTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeritCoinTransaction::getBizId, orderId)
                .eq(MeritCoinTransaction::getBizType, bizType.getCode());
        return meritCoinTransactionMapper.selectCount(wrapper) > 0;
    }

    private long addCoinsAndGetBalance(Long userId, int coins) {
        if (coins <= 0) {
            return queryRemainingCoins(userId);
        }
        userStatsMapper.addMeritCoins(userId, (long) coins);
        return queryRemainingCoins(userId);
    }

    private long queryRemainingCoins(Long userId) {
        UserStats stats = userStatsMapper.selectByUserId(userId);
        return stats != null && stats.getMeritCoins() != null ? stats.getMeritCoins() : 0L;
    }

    private void recordTransaction(RechargeOrder order, int changeAmount, long balanceAfter,
                                   MeritBizType bizType, String remark) {
        MeritCoinTransaction tx = new MeritCoinTransaction();
        tx.setUserId(order.getUserId());
        tx.setBizType(bizType.getCode());
        tx.setBizId(order.getId());
        tx.setChangeAmount(changeAmount);
        tx.setBalanceAfter(Math.toIntExact(balanceAfter));
        tx.setRemark(remark);
        meritCoinTransactionMapper.insert(tx);
    }

    private Date convertToDate(String successTime) {
        if (StrUtil.isBlank(successTime)) {
            return null;
        }
        try {
            return Date.from(OffsetDateTime.parse(successTime).toInstant());
        } catch (Exception e) {
            log.warn("解析微信支付时间失败: {}", successTime, e);
            return null;
        }
    }
}
