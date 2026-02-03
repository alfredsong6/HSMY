package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.RechargeOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 充值订单 Mapper
 */
@Mapper
public interface RechargeOrderMapper extends BaseMapper<RechargeOrder> {

    /**
     * 根据订单号更新支付状态
     *
     * @param orderNo        订单号
     * @param paymentStatus  支付状态
     * @param transactionId  第三方交易号
     * @param paymentTime    支付完成时间
     * @return 影响行数
     */
    int updatePaymentStatusByOrderNo(@Param("orderNo") String orderNo,
                                     @Param("paymentStatus") Integer paymentStatus,
                                     @Param("transactionId") String transactionId,
                                     @Param("paymentTime") Date paymentTime,
                                     @Param("paymentStatusDesc") String paymentStatusDesc);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单
     */
    RechargeOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询待补偿的订单列表
     *
     * @param statusList  待查询的状态集合
     * @param beforeTime  创建时间阈值（小于等于该时间）
     * @param limit       最大数量
     * @return 订单列表
     */
    List<RechargeOrder> selectPendingOrders(@Param("statusList") List<Integer> statusList,
                                            @Param("beforeTime") Date beforeTime,
                                            @Param("limit") int limit);

    /**
     * 查询最近一段时间内未支付的订单（限制查询次数）
     */
    List<RechargeOrder> selectRecentPendingOrders(@Param("fromTime") Date fromTime,
                                                  @Param("maxQueryCount") int maxQueryCount,
                                                  @Param("limit") int limit);

    /**
     * 增加订单查询次数并记录时间
     */
    int incrementQueryCount(@Param("orderNo") String orderNo, @Param("queryTime") Date queryTime);
}
