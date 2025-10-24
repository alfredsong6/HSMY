package com.hsmy.service.impl;

import cn.hutool.core.util.StrUtil;
import com.hsmy.config.WechatPayProperties;
import com.hsmy.dto.WechatPayPrepayRequest;
import com.hsmy.entity.RechargeOrder;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.RechargeOrderMapper;
import com.hsmy.service.PaymentService;
import com.hsmy.utils.IdGenerator;
import com.hsmy.vo.WechatPayPrepayVO;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.model.Transaction.TradeStateEnum;
import com.wechat.pay.java.core.exception.ServiceException;
import org.springframework.beans.factory.ObjectProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * 支付服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final String PAYMENT_METHOD_WECHAT = "wechat";
    private static final int STATUS_PENDING = 0;
    private static final int STATUS_SUCCESS = 1;
    private static final int STATUS_FAILED = 2;
    private static final int STATUS_REFUND = 3;

    private final WechatPayProperties wechatPayProperties;
    private final RechargeOrderMapper rechargeOrderMapper;
    private final ObjectProvider<Config> wechatPayConfigProvider;
    private final ObjectProvider<NotificationParser> notificationParserProvider;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WechatPayPrepayVO createWechatPrepay(Long userId, String username, WechatPayPrepayRequest request) {
        if (!wechatPayProperties.isEnabled()) {
            throw new BusinessException("微信支付功能未启用");
        }
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("支付金额必须大于0");
        }

        String orderNo = generateOrderNo();
        RechargeOrder order = buildRechargeOrder(userId, username, request, orderNo, amount);
        rechargeOrderMapper.insert(order);

        PrepayRequest prepayRequest = buildPrepayRequest(orderNo, request, amount);
        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(getWechatPayConfig()).build();
        try {
            PrepayWithRequestPaymentResponse response = service.prepayWithRequestPayment(prepayRequest);
            return buildPrepayResponse(orderNo, response);
        } catch (ServiceException e) {
            log.error("微信预下单失败，orderNo={}, errorCode={}, message={}", orderNo, e.getErrorCode(), e.getErrorMessage());
            rechargeOrderMapper.updatePaymentStatusByOrderNo(orderNo, STATUS_FAILED, null, null);
            throw new BusinessException("微信支付下单失败：" + e.getErrorMessage(), e);
        } catch (Exception e) {
            log.error("微信预下单发生异常，orderNo={}", orderNo, e);
            rechargeOrderMapper.updatePaymentStatusByOrderNo(orderNo, STATUS_FAILED, null, null);
            throw new BusinessException("微信支付下单异常，请稍后再试", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleWechatPayNotification(RequestParam requestParam) {
        NotificationParser parser = getNotificationParser();
        Transaction transaction = parser.parse(requestParam, Transaction.class);
        String orderNo = transaction.getOutTradeNo();
        TradeStateEnum tradeStateEnum = transaction.getTradeState();
        String transactionId = transaction.getTransactionId();

        log.info("收到微信支付通知，orderNo={}, tradeState={}, transactionId={}", orderNo,
                tradeStateEnum != null ? tradeStateEnum.name() : "UNKNOWN", transactionId);

        boolean terminal = applyTradeState(orderNo, transaction);
        if (terminal) {
            log.info("订单 {} 状态已更新为 {}", orderNo, tradeStateEnum);
        } else {
            log.info("订单 {} 仍处于待支付状态，交易状态 {}", orderNo, tradeStateEnum);
        }
    }

    @Override
    public boolean syncWechatOrder(String orderNo) {
        if (!wechatPayProperties.isEnabled()) {
            log.debug("微信支付未启用，跳过订单 {} 同步", orderNo);
            return false;
        }
        RechargeOrder order = rechargeOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.warn("未找到订单，无法同步，orderNo={}", orderNo);
            return false;
        }
        if (!Objects.equals(order.getPaymentStatus(), STATUS_PENDING)) {
            log.debug("订单 {} 已处于终态，当前状态 {}", orderNo, order.getPaymentStatus());
            return true;
        }

        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(getWechatPayConfig()).build();
        QueryOrderByOutTradeNoRequest queryRequest = new QueryOrderByOutTradeNoRequest();
        queryRequest.setOutTradeNo(orderNo);
        queryRequest.setMchid(wechatPayProperties.getMchId());
        try {
            Transaction transaction = service.queryOrderByOutTradeNo(queryRequest);
            TradeStateEnum tradeStateEnum = transaction.getTradeState();
            boolean terminal = applyTradeState(orderNo, transaction);
            if (terminal) {
                log.info("主动同步订单 {} 成功，交易状态 {}", orderNo, tradeStateEnum);
            } else {
                log.debug("主动同步订单 {} 未完成支付，交易状态 {}", orderNo, tradeStateEnum);
            }
            return terminal;
        } catch (ServiceException e) {
            if ("ORDER_NOT_EXISTS".equalsIgnoreCase(e.getErrorCode())) {
                log.warn("微信侧不存在订单记录，orderNo={}, errMsg={}", orderNo, e.getErrorMessage());
            } else {
                log.error("主动同步订单 {} 失败，code={}, message={}", orderNo, e.getErrorCode(), e.getErrorMessage());
            }
            return false;
        } catch (Exception e) {
            log.error("主动同步订单 {} 失败", orderNo, e);
            return false;
        }
    }

    private RechargeOrder buildRechargeOrder(Long userId, String username, WechatPayPrepayRequest request,
                                             String orderNo, BigDecimal amount) {
        RechargeOrder order = new RechargeOrder();
        order.setId(IdGenerator.nextId());
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAmount(amount);
        order.setMeritCoins(request.getMeritCoins() == null ? 0 : request.getMeritCoins());
        order.setBonusCoins(request.getBonusCoins() == null ? 0 : request.getBonusCoins());
        order.setPaymentMethod(PAYMENT_METHOD_WECHAT);
        order.setPaymentStatus(STATUS_PENDING);
        order.setRemark(StrUtil.blankToDefault(request.getDescription(), wechatPayProperties.getDescription()));
        String operator = StrUtil.blankToDefault(username, "system");
        order.setCreateBy(operator);
        order.setUpdateBy(operator);
        return order;
    }

    private PrepayRequest buildPrepayRequest(String orderNo, WechatPayPrepayRequest request, BigDecimal amount) {
        if (StrUtil.isBlank(wechatPayProperties.getNotifyUrl())) {
            throw new BusinessException("未配置微信支付回调地址");
        }
        PrepayRequest prepayRequest = new PrepayRequest();
        prepayRequest.setAppid(wechatPayProperties.getAppId());
        prepayRequest.setMchid(wechatPayProperties.getMchId());
        prepayRequest.setDescription(StrUtil.blankToDefault(request.getDescription(), wechatPayProperties.getDescription()));
        prepayRequest.setOutTradeNo(orderNo);
        prepayRequest.setNotifyUrl(wechatPayProperties.getNotifyUrl());
        if (StrUtil.isNotBlank(request.getAttach())) {
            prepayRequest.setAttach(request.getAttach());
        }

        Amount sdkAmount = new Amount();
        sdkAmount.setCurrency(StrUtil.blankToDefault(wechatPayProperties.getCurrency(), "CNY"));
        sdkAmount.setTotal(convertAmountToFen(amount));
        prepayRequest.setAmount(sdkAmount);

        Payer payer = new Payer();
        payer.setOpenid(request.getPayerOpenId());
        prepayRequest.setPayer(payer);

        return prepayRequest;
    }

    private WechatPayPrepayVO buildPrepayResponse(String orderNo, PrepayWithRequestPaymentResponse response) {
        WechatPayPrepayVO vo = new WechatPayPrepayVO();
        vo.setOrderNo(orderNo);
        vo.setAppId(response.getAppId());
        vo.setTimeStamp(response.getTimeStamp());
        vo.setNonceStr(response.getNonceStr());
        String packageVal = response.getPackageVal();
        vo.setPackageValue(packageVal);
        vo.setSignType(response.getSignType());
        vo.setPaySign(response.getPaySign());
        vo.setPrepayId(extractPrepayId(packageVal));
        return vo;
    }

    private String extractPrepayId(String packageVal) {
        if (StrUtil.isBlank(packageVal)) {
            return null;
        }
        String keyword = "prepay_id=";
        int index = packageVal.indexOf(keyword);
        if (index < 0) {
            return null;
        }
        return packageVal.substring(index + keyword.length());
    }

    private Config getWechatPayConfig() {
        Config config = wechatPayConfigProvider.getIfAvailable();
        if (config == null) {
            throw new BusinessException("微信支付功能未启用");
        }
        return config;
    }

    private NotificationParser getNotificationParser() {
        NotificationParser parser = notificationParserProvider.getIfAvailable();
        if (parser == null) {
            throw new BusinessException("微信支付回调解析器未初始化");
        }
        return parser;
    }

    private String generateOrderNo() {
        return "WX" + IdGenerator.nextIdStr();
    }

    private int convertAmountToFen(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact();
    }

    private Date convertToDate(String successTime) {
        if (StrUtil.isBlank(successTime)) {
            return null;
        }
        try {
            return Date.from(OffsetDateTime.parse(successTime).toInstant());
        } catch (Exception e) {
            log.warn("解析微信支付时间失败: {}", successTime, e);
            return null;
        }
    }

    private boolean applyTradeState(String orderNo, Transaction transaction) {
        // 先检查订单状态,避免重复处理
        RechargeOrder order = rechargeOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.warn("订单不存在,无法更新状态,orderNo={}", orderNo);
            return false;
        }
        if (!Objects.equals(order.getPaymentStatus(), STATUS_PENDING)) {
            log.info("订单已处于终态,跳过处理,orderNo={}, currentStatus={}", orderNo, order.getPaymentStatus());
            return true;
        }

        TradeStateEnum tradeStateEnum = transaction.getTradeState();
        String transactionId = transaction.getTransactionId();
        Date paymentTime = convertToDate(transaction.getSuccessTime());
        if (tradeStateEnum == null) {
            return false;
        }

        int affectedRows = 0;
        switch (tradeStateEnum) {
            case SUCCESS:
                affectedRows = rechargeOrderMapper.updatePaymentStatusByOrderNo(orderNo, STATUS_SUCCESS, transactionId, paymentTime);
                break;
            case REFUND:
                affectedRows = rechargeOrderMapper.updatePaymentStatusByOrderNo(orderNo, STATUS_REFUND, transactionId, paymentTime);
                break;
            case CLOSED:
            case PAYERROR:
            case REVOKED:
                affectedRows = rechargeOrderMapper.updatePaymentStatusByOrderNo(orderNo, STATUS_FAILED, transactionId, paymentTime);
                break;
            case NOTPAY:
            case USERPAYING:
            default:
                return false;
        }

        if (affectedRows > 0) {
            log.info("订单状态更新成功,orderNo={}, status={}", orderNo, tradeStateEnum);
            return true;
        } else {
            log.warn("订单状态更新失败(可能已被其他线程处理),orderNo={}", orderNo);
            return true;
        }
    }
}
