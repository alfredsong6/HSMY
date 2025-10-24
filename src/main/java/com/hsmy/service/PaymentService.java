package com.hsmy.service;

import com.hsmy.dto.WechatPayPrepayRequest;
import com.hsmy.vo.WechatPayPrepayVO;
import com.wechat.pay.java.core.notification.RequestParam;

/**
 * 支付服务
 */
public interface PaymentService {

    /**
     * 发起微信预支付（JSAPI）
     *
     * @param userId   用户ID
     * @param username 用户名（用于记录操作人）
     * @param request  预下单请求
     * @return 预下单返回参数
     */
    WechatPayPrepayVO createWechatPrepay(Long userId, String username, WechatPayPrepayRequest request);

    /**
     * 处理微信支付回调通知
     *
     * @param notificationRequest 通知请求
     */
    void handleWechatPayNotification(RequestParam requestParam);

    /**
     * 主动查询并同步微信订单状态
     *
     * @param orderNo 商户订单号
     * @return 订单是否已进入终态（成功/失败/退款）
     */
    boolean syncWechatOrder(String orderNo);
}
