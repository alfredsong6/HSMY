package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.PurchaseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购买记录Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/11
 */
@Mapper
public interface PurchaseRecordMapper extends BaseMapper<PurchaseRecord> {
    
    /**
     * 根据用户ID查询购买记录
     * 
     * @param userId 用户ID
     * @return 购买记录列表
     */
    List<PurchaseRecord> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据订单号查询购买记录
     * 
     * @param orderNo 订单号
     * @return 购买记录
     */
    PurchaseRecord selectByOrderNo(@Param("orderNo") String orderNo);
}