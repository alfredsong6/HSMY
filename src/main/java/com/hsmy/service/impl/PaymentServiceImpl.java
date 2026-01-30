package com.hsmy.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hsmy.config.WechatPayProperties;
import com.hsmy.domain.activity.ActivityDomain;
import com.hsmy.domain.activity.ActivityRule;
import com.hsmy.domain.auth.AuthIdentity;
import com.hsmy.dto.WechatPayPrepayRequest;
import com.hsmy.entity.RechargeOrder;
import com.hsmy.entity.UserStats;
import com.hsmy.entity.meditation.MeritCoinTransaction;
import com.hsmy.enums.AuthProvider;
import com.hsmy.enums.MeritBizType;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.ActivityMapper;
import com.hsmy.mapper.RechargeOrderMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.mapper.meditation.MeritCoinTransactionMapper;
import com.hsmy.service.AuthIdentityService;
import com.hsmy.service.PaymentService;
import com.hsmy.service.wechat.WechatPayClient;
import com.hsmy.utils.IdGenerator;
import com.hsmy.vo.WechatPayPrepayVO;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.model.Transaction.TradeStateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    private static final AuthProvider PROVIDER_WECHAT_MINI = AuthProvider.WECHAT_MINI;
    private static final MeritBizType BIZ_TYPE_RECHARGE_PURCHASE = MeritBizType.RECHARGE_PURCHASE;
    private static final MeritBizType BIZ_TYPE_RECHARGE_BONUS = MeritBizType.RECHARGE_BONUS;
    private static final String INVALID_PRODUCT_MESSAGE = "商品信息已失效，请重新刷新页面";
    private static final String IDEMPOTENCY_CACHE_PREFIX = "hsmy:payment:wechat:prepay:";
    private static final String IDEMPOTENCY_LOCK_PREFIX = "hsmy:payment:wechat:prepay:lock:";
    private static final long IDEMPOTENCY_CACHE_TTL_SECONDS = 300;
    private static final long IDEMPOTENCY_LOCK_TTL_SECONDS = 30;

    private final ActivityMapper activityMapper;
    private final WechatPayProperties wechatPayProperties;
    private final RechargeOrderMapper rechargeOrderMapper;
    private final UserStatsMapper userStatsMapper;
    private final MeritCoinTransactionMapper meritCoinTransactionMapper;
    private final ObjectProvider<Config> wechatPayConfigProvider;
    private final WechatPayClient wechatPayClient;
    private final ObjectProvider<com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension> jsapiServiceProvider;
    private final ObjectProvider<NotificationParser> notificationParserProvider;
    private final AuthIdentityService authIdentityService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Override
    public WechatPayPrepayVO createWechatPrepay(Long userId, String username, WechatPayPrepayRequest request) {
        if (request == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (!wechatPayProperties.isEnabled()) {
            throw new BusinessException("微信支付功能未启用");
        }
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        String idempotencyKey = buildIdempotencyKey(userId, request);
        String cacheKey = buildIdempotencyCacheKey(idempotencyKey);
        WechatPayPrepayVO cached = getCachedPrepay(cacheKey);
        if (cached != null) {
            return cached;
        }

        String lockKey = buildIdempotencyLockKey(idempotencyKey);
        if (!tryAcquireIdempotencyLock(lockKey)) {
            cached = getCachedPrepay(cacheKey);
            if (cached != null) {
                return cached;
            }
            throw new BusinessException("请求处理中，请勿重复提交");
        }

        String orderNo = null;
        try {
            validateProduct(request);
            AuthIdentity identity = authIdentityService.getByProviderAndUserId(PROVIDER_WECHAT_MINI, userId);
            if (identity == null || !Objects.equals(identity.getUserId(), userId)) {
                log.error("微信身份验证失败，userId={}, provider={}", userId, PROVIDER_WECHAT_MINI);
                throw new BusinessException("未找到对应的微信身份，请重新登录");
            }
            BigDecimal amount = request.getAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("支付金额必须大于0");
            }

            orderNo = generateOrderNo();
            RechargeOrder order = buildRechargeOrder(userId, username, request, orderNo, amount);
            rechargeOrderMapper.insert(order);

            PrepayRequest prepayRequest = buildPrepayRequest(orderNo, request, amount);
            Payer payer = new Payer();
            payer.setOpenid(identity.getOpenId());
            prepayRequest.setPayer(payer);
            PrepayWithRequestPaymentResponse response = wechatPayClient.prepay(prepayRequest);
            WechatPayPrepayVO vo = buildPrepayResponse(orderNo, response);
            cachePrepay(cacheKey, vo);
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (ServiceException e) {
            log.warn("微信预下单发生错误，orderNo={}", orderNo, e);
            log.error("微信预下单失败，orderNo={}, errorCode={}, message={}", orderNo, e.getErrorCode(), e.getErrorMessage());
            if (orderNo != null) {
                rechargeOrderMapper.updatePaymentStatusByOrderNo(orderNo, STATUS_FAILED, null, null);
            }
            throw new BusinessException("微信支付下单失败：" + e.getErrorMessage(), e);
        } catch (Exception e) {
            log.error("微信预下单发生异常，orderNo={}", orderNo, e);
            if (orderNo != null) {
                rechargeOrderMapper.updatePaymentStatusByOrderNo(orderNo, STATUS_FAILED, null, null);
            }
            throw new BusinessException("微信支付下单异常，请稍后再试", e);
        } finally {
            releaseIdempotencyLock(lockKey);
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
            if (tradeStateEnum == TradeStateEnum.SUCCESS) {
                try {
                    grantMeritCoins(orderNo);
                } catch (Exception e) {
                    log.error("订单 {} 充值到账处理失败", orderNo, e);
                }
            }
        } else {
            log.info("订单 {} 仍处于待支付状态，交易状态 {}", orderNo, tradeStateEnum);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

        QueryOrderByOutTradeNoRequest queryRequest = new QueryOrderByOutTradeNoRequest();
        queryRequest.setOutTradeNo(orderNo);
        queryRequest.setMchid(wechatPayProperties.getMchId());
        try {
            Transaction transaction = wechatPayClient.queryOrder(queryRequest);
            TradeStateEnum tradeStateEnum = transaction.getTradeState();
            boolean terminal = applyTradeState(orderNo, transaction);
            if (terminal) {
                log.info("主动同步订单 {} 成功，交易状态 {}", orderNo, tradeStateEnum);
                if (tradeStateEnum == TradeStateEnum.SUCCESS) {
                    try {
                        grantMeritCoins(orderNo);
                    } catch (Exception e) {
                        log.error("主动同步订单 {} 充值到账处理失败", orderNo, e);
                    }
                }
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

    @Override
    public Integer getPaymentStatus(String orderNo) {
        RechargeOrder order = rechargeOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order.getPaymentStatus();
    }

    @Override
    public void closeWechatOrder(String orderNo) {
        if (!wechatPayProperties.isEnabled()) {
            log.debug("微信支付未启用，跳过关单，orderNo={}", orderNo);
            return;
        }
        CloseOrderRequest request = new CloseOrderRequest();
        request.setMchid(wechatPayProperties.getMchId());
        request.setOutTradeNo(orderNo);
        try {
            wechatPayClient.closeOrder(request);
            rechargeOrderMapper.updatePaymentStatusByOrderNo(orderNo, STATUS_FAILED, null, new Date());
            log.info("关单成功，orderNo={}", orderNo);
        } catch (ServiceException e) {
            log.error("关单失败，orderNo={}, code={}, message={}", orderNo, e.getErrorCode(), e.getErrorMessage());
            throw new BusinessException("关单失败：" + e.getErrorMessage(), e);
        } catch (Exception e) {
            log.error("关单异常，orderNo={}", orderNo, e);
            throw new BusinessException("关单异常", e);
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

    private String buildIdempotencyKey(Long userId, WechatPayPrepayRequest request) {
        String clientKey = request.getIdempotencyKey();
        String baseKey;
        if (StrUtil.isNotBlank(clientKey)) {
            baseKey = "client:" + clientKey;
        } else {
            BigDecimal amount = request.getAmount();
            int meritCoins = request.getMeritCoins() == null ? 0 : request.getMeritCoins();
            int bonusCoins = request.getBonusCoins() == null ? 0 : request.getBonusCoins();
            String amountText = amount == null ? "null" : amount.toPlainString();
            baseKey = "auto:" + request.getProductId() + ":" + amountText + ":" + meritCoins + ":" + bonusCoins;
        }
        String raw = userId + ":" + baseKey;
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String buildIdempotencyCacheKey(String idempotencyKey) {
        return IDEMPOTENCY_CACHE_PREFIX + idempotencyKey;
    }

    private String buildIdempotencyLockKey(String idempotencyKey) {
        return IDEMPOTENCY_LOCK_PREFIX + idempotencyKey;
    }

    private WechatPayPrepayVO getCachedPrepay(String cacheKey) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object cached = ops.get(cacheKey);
        if (cached instanceof WechatPayPrepayVO) {
            return (WechatPayPrepayVO) cached;
        }
        return null;
    }

    private void cachePrepay(String cacheKey, WechatPayPrepayVO vo) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(cacheKey, vo, IDEMPOTENCY_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
    }

    private boolean tryAcquireIdempotencyLock(String lockKey) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Boolean locked = ops.setIfAbsent(lockKey, "1", IDEMPOTENCY_LOCK_TTL_SECONDS, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(locked);
    }

    private void releaseIdempotencyLock(String lockKey) {
        redisTemplate.delete(lockKey);
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

    private void grantMeritCoins(String orderNo) {
        RechargeOrder order = rechargeOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.warn("充值订单 {} 不存在，无法发放功德币", orderNo);
            return;
        }
        if (!Objects.equals(order.getPaymentStatus(), STATUS_SUCCESS)) {
            log.debug("订单 {} 当前状态非成功({})，跳过发放功德币", orderNo, order.getPaymentStatus());
            return;
        }
        if (order.getUserId() == null) {
            log.warn("订单 {} 缺少用户信息，无法发放功德币", orderNo);
            return;
        }
        int purchaseCoins = order.getMeritCoins() != null ? order.getMeritCoins() : 0;
        int bonusCoins = order.getBonusCoins() != null ? order.getBonusCoins() : 0;
        if (purchaseCoins <= 0 && bonusCoins <= 0) {
            log.info("订单 {} 未配置功德币收益，跳过发放", orderNo);
            return;
        }

        if (purchaseCoins > 0 && !existsTransaction(order.getId(), BIZ_TYPE_RECHARGE_PURCHASE)) {
            long balanceAfter = addCoinsAndGetBalance(order.getUserId(), purchaseCoins);
            recordTransaction(order, purchaseCoins, balanceAfter, BIZ_TYPE_RECHARGE_PURCHASE,
                    String.format("充值订单%s到账功德币", orderNo));
            log.info("订单 {} 发放 {} 枚功德币到账记录成功", orderNo, purchaseCoins);
        } else if (purchaseCoins > 0) {
            log.info("订单 {} 的到账功德币已处理，跳过重复发放", orderNo);
        }

        if (bonusCoins > 0 && !existsTransaction(order.getId(), BIZ_TYPE_RECHARGE_BONUS)) {
            long balanceAfter = addCoinsAndGetBalance(order.getUserId(), bonusCoins);
            recordTransaction(order, bonusCoins, balanceAfter, BIZ_TYPE_RECHARGE_BONUS,
                    String.format("充值订单%s赠送功德币", orderNo));
            log.info("订单 {} 发放 {} 枚赠送功德币记录成功", orderNo, bonusCoins);
        } else if (bonusCoins > 0) {
            log.info("订单 {} 的赠送功德币已处理，跳过重复发放", orderNo);
        }
    }

    private boolean existsTransaction(Long orderId, MeritBizType bizType) {
        LambdaQueryWrapper<MeritCoinTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MeritCoinTransaction::getBizId, orderId)
                .eq(MeritCoinTransaction::getBizType, bizType.getCode());
        return meritCoinTransactionMapper.selectCount(wrapper) > 0;
    }

    private long addCoinsAndGetBalance(Long userId, int coins) {
        if (coins <= 0) {
            return queryRemainingCoins(userId);
        }
        userStatsMapper.addMeritCoins(userId, (long) coins);
        return queryRemainingCoins(userId);
    }

    private long queryRemainingCoins(Long userId) {
        UserStats stats = userStatsMapper.selectByUserId(userId);
        return stats != null && stats.getMeritCoins() != null ? stats.getMeritCoins() : 0L;
    }

    private void recordTransaction(RechargeOrder order, int changeAmount, long balanceAfter,
                                   MeritBizType bizType, String remark) {
        MeritCoinTransaction tx = new MeritCoinTransaction();
        tx.setUserId(order.getUserId());
        tx.setBizType(bizType.getCode());
        tx.setBizId(order.getId());
        tx.setChangeAmount(changeAmount);
        tx.setBalanceAfter(Math.toIntExact(balanceAfter));
        tx.setRemark(remark);
        meritCoinTransactionMapper.insert(tx);
    }

    private int convertAmountToFen(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact();
    }

    private void validateProduct(WechatPayPrepayRequest request) {
        Long productId = request.getProductId();
        if (productId == null) {
            throw new BusinessException(INVALID_PRODUCT_MESSAGE);
        }
        ActivityDomain activity = activityMapper.selectById(productId);
        if (activity == null || activity.getRules() == null) {
            throw new BusinessException(INVALID_PRODUCT_MESSAGE);
        }
        ActivityRule rule = activity.getRules();
        if (isTestProfile()) {
            return;
        }
        if (rule.getAmount() == null || request.getAmount() == null ||
                rule.getAmount().compareTo(request.getAmount()) != 0) {
            throw new BusinessException(INVALID_PRODUCT_MESSAGE);
        }
        BigDecimal requestMeritCoins = BigDecimal.valueOf(request.getMeritCoins() == null ? 0 : request.getMeritCoins());
        BigDecimal requestBonusCoins = BigDecimal.valueOf(request.getBonusCoins() == null ? 0 : request.getBonusCoins());
        if (rule.getGive() == null || rule.getGive().compareTo(requestMeritCoins) != 0) {
            throw new BusinessException(INVALID_PRODUCT_MESSAGE);
        }
        if (rule.getGift() == null || rule.getGift().compareTo(requestBonusCoins) != 0) {
            throw new BusinessException(INVALID_PRODUCT_MESSAGE);
        }
    }

    private boolean isTestProfile() {
        if (StrUtil.isBlank(activeProfile)) {
            return true;
        }
        String[] profiles = activeProfile.split(",");
        for (String profile : profiles) {
            if ("prod".equals(profile.trim())) {
                return false;
            }
        }
        return true;
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
