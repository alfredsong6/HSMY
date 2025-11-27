package com.hsmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hsmy.entity.Item;
import com.hsmy.entity.RechargeOrder;
import com.hsmy.entity.Scripture;
import com.hsmy.entity.UserScripturePurchase;
import com.hsmy.entity.UserItem;
import com.hsmy.entity.meditation.MeritCoinTransaction;
import com.hsmy.enums.MeritBizType;
import com.hsmy.mapper.ItemMapper;
import com.hsmy.mapper.RechargeOrderMapper;
import com.hsmy.mapper.ScriptureMapper;
import com.hsmy.mapper.UserItemMapper;
import com.hsmy.mapper.UserScripturePurchaseMapper;
import com.hsmy.mapper.meditation.MeritCoinTransactionMapper;
import com.hsmy.service.MeritCoinTransactionService;
import com.hsmy.vo.MeritCoinTransactionDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 功德币流水查询实现.
 */
@Service
@RequiredArgsConstructor
public class MeritCoinTransactionServiceImpl implements MeritCoinTransactionService {

    private final MeritCoinTransactionMapper transactionMapper;
    private final RechargeOrderMapper rechargeOrderMapper;
    private final UserItemMapper userItemMapper;
    private final ItemMapper itemMapper;
    private final UserScripturePurchaseMapper userScripturePurchaseMapper;
    private final ScriptureMapper scriptureMapper;

    @Override
    public List<MeritCoinTransactionDetailVO> listAll(Long userId) {
        return queryTransactions(userId, TransactionFilter.ALL);
    }

    @Override
    public List<MeritCoinTransactionDetailVO> listIncome(Long userId) {
        return queryTransactions(userId, TransactionFilter.INCOME);
    }

    @Override
    public List<MeritCoinTransactionDetailVO> listExpense(Long userId) {
        return queryTransactions(userId, TransactionFilter.EXPENSE);
    }

    private List<MeritCoinTransactionDetailVO> queryTransactions(Long userId, TransactionFilter filter) {
        LambdaQueryWrapper<MeritCoinTransaction> wrapper = new LambdaQueryWrapper<MeritCoinTransaction>()
                .eq(MeritCoinTransaction::getUserId, userId)
                .eq(MeritCoinTransaction::getIsDeleted, 0)
                .orderByDesc(MeritCoinTransaction::getCreateTime);
        switch (filter) {
            case INCOME:
                wrapper.gt(MeritCoinTransaction::getChangeAmount, 0);
                break;
            case EXPENSE:
                wrapper.lt(MeritCoinTransaction::getChangeAmount, 0);
                break;
            default:
                break;
        }

        List<MeritCoinTransaction> records = transactionMapper.selectList(wrapper);
        List<MeritCoinTransactionDetailVO> result = new ArrayList<>(records.size());
        for (MeritCoinTransaction record : records) {
            result.add(buildDetailVO(record));
        }
        return result;
    }

    private MeritCoinTransactionDetailVO buildDetailVO(MeritCoinTransaction record) {
        MeritCoinTransactionDetailVO vo = new MeritCoinTransactionDetailVO();
        vo.setId(record.getId());
        vo.setBizType(record.getBizType());
        vo.setChangeAmount(record.getChangeAmount());
        vo.setBalanceAfter(record.getBalanceAfter());
        vo.setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime());
        vo.setDetail(resolveDetail(record));
        return vo;
    }

    private MeritCoinTransactionDetailVO.Detail resolveDetail(MeritCoinTransaction tx) {
        MeritBizType bizType;
        try {
            bizType = MeritBizType.fromCode(tx.getBizType());
        } catch (IllegalArgumentException e) {
            bizType = null;
        }
        if (bizType == MeritBizType.RECHARGE_PURCHASE || bizType == MeritBizType.RECHARGE_BONUS) {
            return buildRechargeDetail(tx);
        }
        if (bizType == MeritBizType.ITEM_PURCHASE) {
            return buildItemDetail(tx);
        }
        if (bizType == MeritBizType.SCRIPTURE_SUBSCRIBE
                || bizType == MeritBizType.SCRIPTURE_PERMANENT
                || bizType == MeritBizType.SCRIPTURE_RENEW) {
            return buildScriptureDetail(tx);
        }
        MeritCoinTransactionDetailVO.Detail detail = new MeritCoinTransactionDetailVO.Detail();
        detail.setTitle("功德币变动");
        detail.setDescription(tx.getRemark());
        detail.setExtraInfo("");
        return detail;
    }

    private MeritCoinTransactionDetailVO.Detail buildRechargeDetail(MeritCoinTransaction tx) {
        MeritCoinTransactionDetailVO.Detail detail = new MeritCoinTransactionDetailVO.Detail();
        RechargeOrder order = rechargeOrderMapper.selectById(tx.getBizId());
        if (order != null) {
            detail.setTitle("充值订单");
            detail.setDescription("订单号：" + order.getOrderNo());
            detail.setExtraInfo("支付金额：" + order.getAmount() + " 元，功德币：" + order.getMeritCoins());
        } else {
            detail.setTitle("充值订单");
            detail.setDescription("充值信息已失效");
            detail.setExtraInfo("");
        }
        return detail;
    }

    private MeritCoinTransactionDetailVO.Detail buildItemDetail(MeritCoinTransaction tx) {
        MeritCoinTransactionDetailVO.Detail detail = new MeritCoinTransactionDetailVO.Detail();
        Item item = itemMapper.selectById(tx.getBizId());
        if (item != null) {
            detail.setTitle("购买道具");
            detail.setItemName(item.getItemName());
            detail.setDescription("道具：" + item.getItemName());
            detail.setExtraInfo("购入价格：" + item.getPrice());
            return detail;
        }
        UserItem userItem = userItemMapper.selectById(tx.getBizId());
        if (userItem != null) {
            Item fallbackItem = itemMapper.selectById(userItem.getItemId());
            String itemName = fallbackItem != null ? fallbackItem.getItemName() : String.valueOf(userItem.getItemId());
            detail.setTitle("购买道具");
            detail.setItemName(itemName);
            detail.setDescription("道具：" + itemName);
            detail.setExtraInfo("购入价格：" + userItem.getPurchasePrice());
            return detail;
        }
        detail.setTitle("购买道具");
        detail.setDescription("道具信息已失效");
        detail.setExtraInfo("");
        return detail;
    }

    private MeritCoinTransactionDetailVO.Detail buildScriptureDetail(MeritCoinTransaction tx) {
        MeritCoinTransactionDetailVO.Detail detail = new MeritCoinTransactionDetailVO.Detail();
        UserScripturePurchase purchase = userScripturePurchaseMapper.selectById(tx.getBizId());
        if (purchase != null) {
            Scripture scripture = scriptureMapper.selectById(purchase.getScriptureId());
            String scriptureName = scripture != null ? scripture.getScriptureName() : String.valueOf(purchase.getScriptureId());
            detail.setTitle("购买典籍");
            detail.setDescription("典籍：" + scriptureName);
            StringBuilder extra = new StringBuilder();
            if (purchase.getPurchaseMonths() != null && purchase.getPurchaseMonths() > 0) {
                extra.append("周期：").append(purchase.getPurchaseMonths()).append("月；");
            } else {
                extra.append("买断");
            }
            detail.setExtraInfo(extra.toString());
        } else {
            detail.setTitle("购买典籍");
            detail.setDescription("典籍信息已失效");
            detail.setExtraInfo("");
        }
        return detail;
    }

    private enum TransactionFilter {
        ALL, INCOME, EXPENSE
    }
}
