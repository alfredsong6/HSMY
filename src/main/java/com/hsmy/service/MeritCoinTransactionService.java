package com.hsmy.service;

import com.hsmy.vo.MeritCoinTransactionDetailVO;

import java.util.List;

/**
 * 功德币流水查询服务.
 */
public interface MeritCoinTransactionService {

    List<MeritCoinTransactionDetailVO> listAll(Long userId);

    List<MeritCoinTransactionDetailVO> listIncome(Long userId);

    List<MeritCoinTransactionDetailVO> listExpense(Long userId);
}

