package com.hsmy.service.impl;

import cn.hutool.core.util.IdUtil;
import com.hsmy.entity.PurchaseRecord;
import com.hsmy.mapper.PurchaseRecordMapper;
import com.hsmy.service.PurchaseRecordService;
import com.hsmy.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 购买记录Service实现类
 * 
 * @author HSMY
 * @date 2025/09/11
 */
@Service
@RequiredArgsConstructor
public class PurchaseRecordServiceImpl implements PurchaseRecordService {
    
    private final PurchaseRecordMapper purchaseRecordMapper;
    
    @Override
    public PurchaseRecord createPurchaseRecord(Long userId, Long itemId, Integer price, Integer quantity, Integer totalAmount) {
        PurchaseRecord record = new PurchaseRecord();
        record.setId(IdGenerator.nextId());
        record.setUserId(userId);
        record.setItemId(itemId);
        record.setOrderNo(generateOrderNo());
        record.setPrice(price);
        record.setQuantity(quantity);
        record.setTotalAmount(totalAmount);
        record.setPurchaseTime(new Date());
        record.setStatus(1); // 1-成功
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        
        purchaseRecordMapper.insert(record);
        return record;
    }
    
    @Override
    public List<PurchaseRecord> getUserPurchaseRecords(Long userId) {
        return purchaseRecordMapper.selectByUserId(userId);
    }
    
    @Override
    public PurchaseRecord getPurchaseRecordByOrderNo(String orderNo) {
        return purchaseRecordMapper.selectByOrderNo(orderNo);
    }
    
    /**
     * 生成订单号
     * 
     * @return 订单号
     */
    private String generateOrderNo() {
        // 格式：SHOP + 时间戳 + 随机数
        return "SHOP" + System.currentTimeMillis() + IdUtil.randomUUID().substring(0, 8).toUpperCase();
    }
}