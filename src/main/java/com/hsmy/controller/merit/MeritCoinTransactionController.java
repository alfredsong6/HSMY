package com.hsmy.controller.merit;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.service.MeritCoinTransactionService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.MeritCoinTransactionDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 功德币流水接口.
 */
@RestController
@RequestMapping("/merit/transactions")
@ApiVersion(ApiVersionConstant.V1_1)
@RequiredArgsConstructor
public class MeritCoinTransactionController {

    private final MeritCoinTransactionService transactionService;

    @GetMapping("/all")
    public Result<List<MeritCoinTransactionDetailVO>> listAll() {
        Long userId = UserContextUtil.requireCurrentUserId();
        return Result.success(transactionService.listAll(userId));
    }

    @GetMapping("/income")
    public Result<List<MeritCoinTransactionDetailVO>> listIncome() {
        Long userId = UserContextUtil.requireCurrentUserId();
        return Result.success(transactionService.listIncome(userId));
    }

    @GetMapping("/expense")
    public Result<List<MeritCoinTransactionDetailVO>> listExpense() {
        Long userId = UserContextUtil.requireCurrentUserId();
        return Result.success(transactionService.listExpense(userId));
    }
}

