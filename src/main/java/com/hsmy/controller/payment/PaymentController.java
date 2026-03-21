package com.hsmy.controller.payment;

import com.hsmy.annotation.ApiVersion;
import com.hsmy.common.Result;
import com.hsmy.constant.ApiVersionConstant;
import com.hsmy.dto.WechatPayPrepayRequest;
import com.hsmy.exception.BusinessException;
import com.hsmy.service.PaymentService;
import com.hsmy.utils.UserContextUtil;
import com.hsmy.vo.WechatPayPrepayVO;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.RequestParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/payment")
@ApiVersion(ApiVersionConstant.V1_0)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 微信 JSAPI 预下单
     */
    @PostMapping("/wechat/prepay")
    public Result<WechatPayPrepayVO> createWechatPrepay(@Validated @RequestBody WechatPayPrepayRequest request) {
        Long userId = UserContextUtil.requireCurrentUserId();
        String username = UserContextUtil.getCurrentUsername();
        WechatPayPrepayVO response = paymentService.createWechatPrepay(userId, username, request);
        return Result.success(response);
    }

    /**
     * 微信支付结果回调（无需登录）
     */
    @PostMapping("/wechat/notify")
    public ResponseEntity<Map<String, String>> handleWechatNotify(HttpServletRequest request,
                                                                  @RequestBody String body) {
        Map<String, String> response = new HashMap<>(2);
        try {
            RequestParam.Builder builder = new RequestParam.Builder()
                    .serialNumber(request.getHeader("Wechatpay-Serial"))
                    .timestamp(request.getHeader("Wechatpay-Timestamp"))
                    .nonce(request.getHeader("Wechatpay-Nonce"))
                    .signature(request.getHeader("Wechatpay-Signature"))
                    .body(body);
            String signType = request.getHeader("Wechatpay-Signature-Type");
            if (signType != null) {
                builder.signType(signType);
            }
            paymentService.handleWechatPayNotification(builder.build());
            response.put("code", "SUCCESS");
            response.put("message", "成功");
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            log.error("微信支付回调验签失败: {}", e.getMessage());
            response.put("code", "FAIL");
            response.put("message", "验签失败");
            return ResponseEntity.status(401).body(response);
        } catch (BusinessException e) {
            log.error("处理微信支付回调失败: {}", e.getMessage());
            response.put("code", "FAIL");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("处理微信支付回调异常", e);
            response.put("code", "FAIL");
            response.put("message", "处理失败");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 主动同步订单状态（需登录）
     */
    @GetMapping("/wechat/order/{orderNo}/sync")
    public Result<Boolean> syncWechatOrder(@PathVariable String orderNo) {
        boolean terminal = paymentService.syncWechatOrder(orderNo);
        return Result.success(terminal);
    }

    /**
     * 查询订单支付状态（仅查本地记录）
     */
    @GetMapping("/wechat/order/{orderNo}/status")
    public Result<Integer> getWechatOrderStatus(@PathVariable String orderNo) {
        Integer status = paymentService.getPaymentStatus(orderNo);
        return Result.success(status);
    }
}
