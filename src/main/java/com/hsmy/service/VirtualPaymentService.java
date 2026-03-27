package com.hsmy.service;

import com.hsmy.dto.VirtualPayCreateOrderRequest;
import com.hsmy.vo.VirtualPayBalanceVO;
import com.hsmy.vo.VirtualPayCreateOrderVO;
import com.hsmy.vo.VirtualPayOrderStatusVO;
import com.hsmy.vo.VirtualPayPackageVO;
import com.hsmy.vo.VirtualPayRecordVO;

import java.util.List;
import java.util.Map;

/**
 * 微信虚拟支付服务.
 */
public interface VirtualPaymentService {

    List<VirtualPayPackageVO> listPackages();

    VirtualPayCreateOrderVO createOrder(Long userId, String username, VirtualPayCreateOrderRequest request);

    void handleNotify(String body, Map<String, String> headers);

    boolean syncWechatOrder(String orderNo);

    VirtualPayOrderStatusVO queryOrderStatus(Long userId, String outTradeNo);

    /**
     * Returns both the local balance and the latest WeChat balance.
     */
    VirtualPayBalanceVO queryBalance(Long userId);

    List<VirtualPayRecordVO> listOrders(Long userId);
}
