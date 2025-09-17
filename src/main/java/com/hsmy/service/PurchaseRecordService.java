package com.hsmy.service;

import com.hsmy.entity.PurchaseRecord;

import java.util.List;

/**
 * 购买记录Service接口
 * 
 * @author HSMY
 * @date 2025/09/11
 */
public interface PurchaseRecordService {
    
    /**
     * 创建购买记录
     * 
     * @param userId 用户ID
     * @param itemId 道具ID
     * @param price 单价
     * @param quantity 数量
     * @param totalAmount 总金额
     * @return 购买记录
     */
    PurchaseRecord createPurchaseRecord(Long userId, Long itemId, Integer price, Integer quantity, Integer totalAmount);
    
    /**
     * 获取用户购买记录
     * 
     * @param userId 用户ID
     * @return 购买记录列表
     */
    List<PurchaseRecord> getUserPurchaseRecords(Long userId);
    
    /**
     * 根据订单号获取购买记录
     * 
     * @param orderNo 订单号
     * @return 购买记录
     */
    PurchaseRecord getPurchaseRecordByOrderNo(String orderNo);
}