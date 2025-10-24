package com.hsmy.task;

import com.hsmy.config.WechatPayProperties;
import com.hsmy.entity.RechargeOrder;
import com.hsmy.mapper.RechargeOrderMapper;
import com.hsmy.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 微信订单状态补偿任务，兜底处理漏回调场景。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatOrderCompensationTask {

    private static final int PENDING_STATUS = 0;
    private static final int BATCH_LIMIT = 50;
    private static final int DELAY_SECONDS = 120;

    private final PaymentService paymentService;
    private final RechargeOrderMapper rechargeOrderMapper;
    private final WechatPayProperties wechatPayProperties;

    /**
     * 每分钟轮询一次未完成订单，兜底同步支付状态。
     */
    @Scheduled(cron = "0 */1 * * * ?")
    @Async("asyncExecutor")
    public void compensatePendingOrders() {
        if (!wechatPayProperties.isEnabled()) {
            return;
        }
        Date beforeTime = Date.from(Instant.now().minusSeconds(DELAY_SECONDS));
        List<RechargeOrder> pendingOrders = rechargeOrderMapper.selectPendingOrders(
                Collections.singletonList(PENDING_STATUS), beforeTime, BATCH_LIMIT);
        if (pendingOrders.isEmpty()) {
            return;
        }

        for (RechargeOrder order : pendingOrders) {
            try {
                boolean terminal = paymentService.syncWechatOrder(order.getOrderNo());
                if (terminal) {
                    log.info("订单 {} 补偿同步完成", order.getOrderNo());
                }
            } catch (Exception ex) {
                log.error("订单 {} 补偿同步失败", order.getOrderNo(), ex);
            }
        }
    }
}
