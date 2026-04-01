package com.hsmy.controller.payment;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.dto.VirtualPayCreateOrderRequest;
import com.hsmy.exception.BusinessException;
import com.hsmy.service.VirtualPaymentService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.VirtualPayBalanceVO;
import com.hsmy.vo.VirtualPayCreateOrderVO;
import com.hsmy.vo.VirtualPayOrderStatusVO;
import com.hsmy.vo.VirtualPayPackageVO;
import com.hsmy.vo.VirtualPayRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 微信虚拟支付控制器.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/virtual-pay")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class VirtualPaymentController {

    private final VirtualPaymentService virtualPaymentService;

    @GetMapping("/packages")
    public Result<List<VirtualPayPackageVO>> listPackages() {
        return Result.success(virtualPaymentService.listPackages());
    }

    @PostMapping("/coin/create")
    public Result<VirtualPayCreateOrderVO> createOrder(@Validated @RequestBody VirtualPayCreateOrderRequest request) {
        Long userId = UserContextUtil.requireCurrentUserId();
        String username = UserContextUtil.getCurrentUsername();
        return Result.success(virtualPaymentService.createOrder(userId, username, request));
    }

    @PostMapping(value = "/notify", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> handleNotify(@RequestBody String body,
                                               @RequestHeader Map<String, String> headers) {
        try {
            virtualPaymentService.handleNotify(body, headers);
            return ResponseEntity.ok("success");
        } catch (BusinessException e) {
            log.error("处理微信虚拟支付回调失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body("fail");
        } catch (Exception e) {
            log.error("处理微信虚拟支付回调异常", e);
            return ResponseEntity.internalServerError().body("fail");
        }
    }

    @GetMapping("/order/status")
    public Result<VirtualPayOrderStatusVO> queryOrderStatus(@RequestParam("outTradeNo") String outTradeNo) {
        Long userId = UserContextUtil.requireCurrentUserId();
        return Result.success(virtualPaymentService.queryOrderStatus(userId, outTradeNo));
    }

    @PostMapping("/order/confirm")
    public Result<VirtualPayOrderStatusVO> confirmOrder(@RequestParam("outTradeNo") String outTradeNo) {
        Long userId = UserContextUtil.requireCurrentUserId();
        return Result.success(virtualPaymentService.confirmOrder(userId, outTradeNo));
    }

    @GetMapping("/balance")
    public Result<VirtualPayBalanceVO> queryBalance() {
        Long userId = UserContextUtil.requireCurrentUserId();
        return Result.success(virtualPaymentService.queryBalance(userId));
    }

    @GetMapping("/orders")
    public Result<List<VirtualPayRecordVO>> listOrders() {
        Long userId = UserContextUtil.requireCurrentUserId();
        return Result.success(virtualPaymentService.listOrders(userId));
    }
}
