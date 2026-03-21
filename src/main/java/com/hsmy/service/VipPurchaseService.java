package com.hsmy.service;

import com.hsmy.vo.VipMyInfoVO;
import com.hsmy.vo.VipPurchaseResultVO;

/**
 * VIP purchase service.
 */
public interface VipPurchaseService {

    /**
     * Purchase vip package for user.
     */
    VipPurchaseResultVO purchaseVip(Long userId, Long packageId, String paymentMethod);

    /**
     * Get current user's vip info.
     */
    VipMyInfoVO getMyVipInfo(Long userId);
}
