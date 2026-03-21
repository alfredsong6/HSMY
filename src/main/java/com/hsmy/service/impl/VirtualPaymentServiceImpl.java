package com.hsmy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsmy.config.WechatPayProperties;
import com.hsmy.domain.auth.AuthIdentity;
import com.hsmy.domain.activity.ActivityDomain;
import com.hsmy.domain.activity.ActivityRule;
import com.hsmy.dto.VirtualPayCreateOrderRequest;
import com.hsmy.entity.RechargeOrder;
import com.hsmy.entity.UserStats;
import com.hsmy.entity.meditation.MeritCoinTransaction;
import com.hsmy.enums.MeritBizType;
import com.hsmy.exception.BusinessException;
import com.hsmy.mapper.ActivityMapper;
import com.hsmy.mapper.RechargeOrderMapper;
import com.hsmy.mapper.UserStatsMapper;
import com.hsmy.mapper.meditation.MeritCoinTransactionMapper;
import com.hsmy.enums.AuthProvider;
import com.hsmy.service.VirtualPaymentService;
import com.hsmy.service.AuthIdentityService;
import com.hsmy.utils.IdGenerator;
import com.hsmy.vo.VirtualPayBalanceVO;
import com.hsmy.vo.VirtualPayCreateOrderVO;
import com.hsmy.vo.VirtualPayOrderStatusVO;
import com.hsmy.vo.VirtualPayPackageVO;
import com.hsmy.vo.VirtualPayRecordVO;
import com.hsmy.vo.VirtualPaySignDataVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 微信虚拟支付服务实现.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VirtualPaymentServiceImpl implements VirtualPaymentService {

    private static final String ACTIVITY_TYPE_CASH_TOP_UP = "cash-top-up";
    private static final String PAYMENT_METHOD_WECHAT_VIRTUAL = "wechat_virtual";
    private static final String MODE_SHORT_SERIES_COIN = "short_series_coin";
    private static final String CURRENCY_TYPE = "CNY";
    private static final String PAY_METHOD_NAME = "requestMidasPaymentGameItem";
    private static final int STATUS_PENDING = 0;
    private static final int STATUS_SUCCESS = 1;
    private static final int STATUS_FAILED = 2;
    private static final int STATUS_CLOSED = 3;
    private static final int DELIVERED_NO = 0;
    private static final int DELIVERED_YES = 1;

    private final ActivityMapper activityMapper;
    private final RechargeOrderMapper rechargeOrderMapper;
    private final UserStatsMapper userStatsMapper;
    private final MeritCoinTransactionMapper meritCoinTransactionMapper;
    private final WechatPayProperties wechatPayProperties;
    private final ObjectMapper objectMapper;
    private final AuthIdentityService authIdentityService;

    @Override
    public List<VirtualPayPackageVO> listPackages() {
        List<ActivityDomain> activities = activityMapper.selectActivityList(ACTIVITY_TYPE_CASH_TOP_UP, 1, new Date());
        if (activities == null || activities.isEmpty()) {
            return Collections.emptyList();
        }
        List<VirtualPayPackageVO> result = new ArrayList<>();
        for (ActivityDomain activity : activities) {
            VirtualRechargePackage pkg = toRechargePackage(activity);
            if (pkg != null) {
                result.add(toPackageVO(pkg));
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VirtualPayCreateOrderVO createOrder(Long userId, String username, VirtualPayCreateOrderRequest request) {
        ensureVirtualPayEnabled();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        VirtualRechargePackage rechargePackage = resolveRechargePackage(request.getPackageId());
        String orderNo = generateVirtualOrderNo();
        String operator = StringUtils.hasText(username) ? username : "system";
        String attach = buildAttachJson(userId, rechargePackage);

        RechargeOrder order = new RechargeOrder();
        order.setId(IdGenerator.nextId());
        order.setOrderNo(orderNo);
        order.setPackageId(rechargePackage.getPackageId());
        order.setUserId(userId);
        order.setAmount(rechargePackage.getAmount());
        order.setMeritCoins(rechargePackage.getCoinCount());
        order.setBonusCoins(rechargePackage.getBonusCoinCount());
        order.setPaymentMethod(PAYMENT_METHOD_WECHAT_VIRTUAL);
        order.setPaymentStatus(STATUS_PENDING);
        order.setDelivered(DELIVERED_NO);
        order.setRemark(rechargePackage.getTitle());
        order.setAttach(attach);
        order.setCreateBy(operator);
        order.setUpdateBy(operator);
        rechargeOrderMapper.insert(order);

        VirtualPaySignDataVO signData = buildSignData(order, rechargePackage, attach);
        String signDataJson = toSignDataJson(signData);
        String sessionKey = resolveSessionKey(userId);
        VirtualPayCreateOrderVO response = new VirtualPayCreateOrderVO();
        response.setMode(MODE_SHORT_SERIES_COIN);
        response.setOfferId(signData.getOfferId());
        response.setOutTradeNo(orderNo);
        response.setSignData(signDataJson);
        response.setPaySig(signPayData(signDataJson));
        response.setSignature(signUserData(signDataJson, sessionKey));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleNotify(String body, Map<String, String> headers) {
        ensureVirtualPayEnabled();
        if (!StringUtils.hasText(body)) {
            throw new BusinessException("通知报文为空");
        }
        if (!verifyNotify(body, headers)) {
            throw new BusinessException("通知验签失败");
        }

        NotifyPayload notifyPayload = parseNotify(body);
        RechargeOrder order = rechargeOrderMapper.selectByOrderNoForUpdate(notifyPayload.getOutTradeNo());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        rechargeOrderMapper.updateNotifyInfoByOrderNo(order.getOrderNo(), body);
        if (Objects.equals(order.getDelivered(), DELIVERED_YES)) {
            return;
        }

        if (!notifyPayload.isSuccess()) {
            rechargeOrderMapper.updatePaymentStatusByOrderNo(order.getOrderNo(), STATUS_FAILED,
                    notifyPayload.getTransactionId(), notifyPayload.getPaymentTime());
            return;
        }

        if (!Objects.equals(order.getPaymentStatus(), STATUS_SUCCESS)) {
            rechargeOrderMapper.updatePaymentStatusByOrderNo(order.getOrderNo(), STATUS_SUCCESS,
                    notifyPayload.getTransactionId(), notifyPayload.getPaymentTime());
            order.setPaymentStatus(STATUS_SUCCESS);
            order.setTransactionId(notifyPayload.getTransactionId());
            order.setPaymentTime(notifyPayload.getPaymentTime());
        }

        deliverCoins(order);
        rechargeOrderMapper.markDelivered(order.getOrderNo(), new Date());
    }

    @Override
    public VirtualPayOrderStatusVO queryOrderStatus(Long userId, String outTradeNo) {
        RechargeOrder order = rechargeOrderMapper.selectByOrderNo(outTradeNo);
        if (order == null || !Objects.equals(order.getUserId(), userId)) {
            throw new BusinessException("订单不存在");
        }
        return toOrderStatusVO(order);
    }

    @Override
    public VirtualPayBalanceVO queryBalance(Long userId) {
        VirtualPayBalanceVO balanceVO = new VirtualPayBalanceVO();
        balanceVO.setUserId(userId);
        balanceVO.setBalance(queryRemainingCoins(userId));
        return balanceVO;
    }

    @Override
    public List<VirtualPayRecordVO> listOrders(Long userId) {
        List<RechargeOrder> orders = rechargeOrderMapper.selectUserRechargeOrders(userId, PAYMENT_METHOD_WECHAT_VIRTUAL);
        if (orders == null || orders.isEmpty()) {
            return Collections.emptyList();
        }
        List<VirtualPayRecordVO> result = new ArrayList<>(orders.size());
        for (RechargeOrder order : orders) {
            VirtualPayRecordVO record = new VirtualPayRecordVO();
            record.setOutTradeNo(order.getOrderNo());
            record.setPackageId(order.getPackageId());
            record.setAmount(order.getAmount());
            record.setCoinCount(order.getMeritCoins());
            record.setBonusCoinCount(order.getBonusCoins());
            record.setStatus(resolveDisplayStatus(order));
            record.setCreateTime(order.getCreateTime());
            record.setPaymentTime(order.getPaymentTime());
            result.add(record);
        }
        return result;
    }

    private void ensureVirtualPayEnabled() {
        if (!wechatPayProperties.getVirtual().isEnabled()) {
            throw new BusinessException("微信虚拟支付未启用");
        }
    }

    private VirtualPaySignDataVO buildSignData(RechargeOrder order, VirtualRechargePackage rechargePackage, String attach) {
        String offerId = wechatPayProperties.getVirtual().getOfferId();
        if (!StringUtils.hasText(offerId)) {
            throw new BusinessException("未配置微信虚拟支付offerId");
        }
        VirtualPaySignDataVO signData = new VirtualPaySignDataVO();
        signData.setOfferId(offerId);
        signData.setBuyQuantity(rechargePackage.getCoinCount());
        signData.setCurrencyType(CURRENCY_TYPE);
        signData.setOutTradeNo(order.getOrderNo());
        signData.setAttach(attach);
        signData.setEnv(wechatPayProperties.getVirtual().getEnv());
        return signData;
    }

    private VirtualPayOrderStatusVO toOrderStatusVO(RechargeOrder order) {
        VirtualPayOrderStatusVO vo = new VirtualPayOrderStatusVO();
        vo.setOutTradeNo(order.getOrderNo());
        vo.setPackageId(order.getPackageId());
        vo.setStatus(resolveDisplayStatus(order));
        vo.setCoinCount(order.getMeritCoins());
        vo.setBonusCoinCount(order.getBonusCoins());
        vo.setBalance(queryRemainingCoins(order.getUserId()));
        vo.setPaymentTime(order.getPaymentTime());
        return vo;
    }

    private String resolveDisplayStatus(RechargeOrder order) {
        if (Objects.equals(order.getPaymentStatus(), STATUS_PENDING)) {
            return "CREATED";
        }
        if (Objects.equals(order.getPaymentStatus(), STATUS_SUCCESS)) {
            return Objects.equals(order.getDelivered(), DELIVERED_YES) ? "DELIVERED" : "PAID";
        }
        if (Objects.equals(order.getPaymentStatus(), STATUS_FAILED)) {
            return "FAILED";
        }
        if (Objects.equals(order.getPaymentStatus(), STATUS_CLOSED)) {
            return "CLOSED";
        }
        return "UNKNOWN";
    }

    private String generateVirtualOrderNo() {
        return "COIN" + IdGenerator.nextIdStr();
    }

    private String buildAttachJson(Long userId, VirtualRechargePackage rechargePackage) {
        try {
            Map<String, Object> attach = new LinkedHashMap<>();
            attach.put("userId", userId);
            attach.put("packageId", rechargePackage.getPackageId());
            attach.put("coinCount", rechargePackage.getCoinCount());
            attach.put("bonusCoinCount", rechargePackage.getBonusCoinCount());
            return objectMapper.writeValueAsString(attach);
        } catch (Exception e) {
            throw new BusinessException("构建attach失败", e);
        }
    }

    private boolean verifyNotify(String body, Map<String, String> headers) {
        String appKey = wechatPayProperties.getVirtual().getAppKey();
        if (!StringUtils.hasText(appKey)) {
            throw new BusinessException("未配置微信虚拟支付appKey");
        }
        String signature = firstNonBlank(headers, "x-virtualpay-signature", "x-virtual-pay-signature",
                "wechatpay-signature", "pay-event-sig", "x-pay-event-sig");
        String event = firstNonBlank(headers, "x-virtualpay-event", "x-virtual-pay-event", "pay-event", "x-pay-event");
        if (!StringUtils.hasText(signature)) {
            return false;
        }
        String expected = buildNotifySignature(body, event, appKey);
        return signature.equalsIgnoreCase(expected);
    }

    private NotifyPayload parseNotify(String body) {
        try {
            Map<String, Object> payload = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() { });
            String outTradeNo = stringValue(payload, "outTradeNo", "out_trade_no");
            if (!StringUtils.hasText(outTradeNo)) {
                throw new BusinessException("通知缺少订单号");
            }
            NotifyPayload notifyPayload = new NotifyPayload();
            notifyPayload.setOutTradeNo(outTradeNo);
            notifyPayload.setTransactionId(stringValue(payload, "transactionId", "transaction_id", "wxOrderId", "order_id"));
            notifyPayload.setSuccess(resolveSuccess(payload));
            notifyPayload.setPaymentTime(resolveNotifyTime(payload));
            return notifyPayload;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("解析通知失败", e);
        }
    }

    private boolean resolveSuccess(Map<String, Object> payload) {
        Object explicitSuccess = payload.get("success");
        if (explicitSuccess instanceof Boolean) {
            return (Boolean) explicitSuccess;
        }
        String payState = stringValue(payload, "payState", "pay_state", "tradeState", "trade_state", "status");
        if (!StringUtils.hasText(payState)) {
            return false;
        }
        String normalized = payState.trim().toUpperCase(Locale.ROOT);
        return "SUCCESS".equals(normalized) || "PAID".equals(normalized) || "DELIVERED".equals(normalized);
    }

    private Date resolveNotifyTime(Map<String, Object> payload) {
        String time = stringValue(payload, "successTime", "success_time", "payTime", "pay_time", "paymentTime");
        if (!StringUtils.hasText(time)) {
            return new Date();
        }
        try {
            return Date.from(OffsetDateTime.parse(time).toInstant());
        } catch (Exception e) {
            log.warn("解析虚拟支付通知时间失败: {}", time, e);
            return new Date();
        }
    }

    private String signPayData(String signDataJson) {
        String appKey = wechatPayProperties.getVirtual().getAppKey();
        if (!StringUtils.hasText(appKey)) {
            throw new BusinessException("未配置微信虚拟支付appKey");
        }
        return hmacSha256Hex(PAY_METHOD_NAME + "&" + signDataJson, appKey);
    }

    private String signUserData(String signDataJson, String sessionKey) {
        if (!StringUtils.hasText(sessionKey)) {
            throw new BusinessException("未找到用户session_key，无法生成signature");
        }
        return hmacSha256Hex(signDataJson, sessionKey);
    }

    private void deliverCoins(RechargeOrder order) {
        if (!Objects.equals(order.getPaymentStatus(), STATUS_SUCCESS)) {
            throw new BusinessException("订单未支付成功，不能发币");
        }
        if (Objects.equals(order.getDelivered(), DELIVERED_YES)) {
            return;
        }
        int purchaseCoins = order.getMeritCoins() == null ? 0 : order.getMeritCoins();
        int bonusCoins = order.getBonusCoins() == null ? 0 : order.getBonusCoins();
        if (purchaseCoins > 0 && !existsTransaction(order.getId(), MeritBizType.RECHARGE_PURCHASE)) {
            long balanceAfter = addCoinsAndGetBalance(order.getUserId(), purchaseCoins);
            recordTransaction(order, purchaseCoins, balanceAfter, MeritBizType.RECHARGE_PURCHASE,
                    String.format("虚拟支付订单%s到账功德币", order.getOrderNo()));
        }
        if (bonusCoins > 0 && !existsTransaction(order.getId(), MeritBizType.RECHARGE_BONUS)) {
            long balanceAfter = addCoinsAndGetBalance(order.getUserId(), bonusCoins);
            recordTransaction(order, bonusCoins, balanceAfter, MeritBizType.RECHARGE_BONUS,
                    String.format("虚拟支付订单%s赠送功德币", order.getOrderNo()));
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
        tx.setBalanceAfter((int) balanceAfter);
        tx.setRemark(remark);
        meritCoinTransactionMapper.insert(tx);
    }

    private VirtualRechargePackage resolveRechargePackage(String packageId) {
        if (!StringUtils.hasText(packageId)) {
            throw new BusinessException("packageId不能为空");
        }
        if (!packageId.startsWith("activity_")) {
            throw new BusinessException("非法的packageId");
        }
        Long activityId;
        try {
            activityId = Long.valueOf(packageId.substring("activity_".length()));
        } catch (Exception e) {
            throw new BusinessException("非法的packageId");
        }
        ActivityDomain activity = activityMapper.selectById(activityId);
        VirtualRechargePackage rechargePackage = toRechargePackage(activity);
        if (rechargePackage == null || !Boolean.TRUE.equals(rechargePackage.getEnabled())) {
            throw new BusinessException("充值档位不存在或已下架");
        }
        return rechargePackage;
    }

    private VirtualPayPackageVO toPackageVO(VirtualRechargePackage rechargePackage) {
        VirtualPayPackageVO vo = new VirtualPayPackageVO();
        vo.setPackageId(rechargePackage.getPackageId());
        vo.setTitle(rechargePackage.getTitle());
        vo.setDescription(rechargePackage.getDescription());
        vo.setAmount(rechargePackage.getAmount());
        vo.setCoinCount(rechargePackage.getCoinCount());
        vo.setBonusCoinCount(rechargePackage.getBonusCoinCount());
        vo.setEnabled(rechargePackage.getEnabled());
        vo.setSort(rechargePackage.getSort());
        return vo;
    }

    private VirtualRechargePackage toRechargePackage(ActivityDomain activity) {
        if (activity == null || activity.getRules() == null || !Objects.equals(activity.getStatus(), 1)) {
            return null;
        }
        ActivityRule rule = activity.getRules();
        if (rule.getAmount() == null || rule.getGive() == null) {
            return null;
        }
        VirtualRechargePackage pkg = new VirtualRechargePackage();
        pkg.setPackageId("activity_" + activity.getId());
        pkg.setTitle(activity.getActivityName());
        pkg.setDescription(activity.getDescription());
        pkg.setAmount(rule.getAmount());
        pkg.setCoinCount(rule.getGive().intValue());
        pkg.setBonusCoinCount(rule.getGift() == null ? 0 : rule.getGift().intValue());
        pkg.setEnabled(true);
        pkg.setSort(activity.getSortOrder());
        return pkg;
    }

    private String stringValue(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Object value = payload.get(key);
            if (value != null && StringUtils.hasText(String.valueOf(value))) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    private String firstNonBlank(Map<String, String> headers, String... keys) {
        for (String key : keys) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (key.equalsIgnoreCase(entry.getKey()) && StringUtils.hasText(entry.getValue())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private String resolveSessionKey(Long userId) {
        AuthIdentity identity = authIdentityService.getByProviderAndUserId(AuthProvider.WECHAT_MINI, userId);
        if (identity == null || !StringUtils.hasText(identity.getSessionKeyEnc())) {
            throw new BusinessException("未找到用户微信session_key，请重新登录");
        }
        return identity.getSessionKeyEnc();
    }

    private String buildNotifySignature(String body, String event, String key) {
        if (StringUtils.hasText(event)) {
            try {
                Map<String, Object> map = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() { });
                Object payload = map.get("payload");
                if (payload != null) {
                    String payloadText = payload instanceof String ? (String) payload : objectMapper.writeValueAsString(payload);
                    return hmacSha256Hex(event + "&" + payloadText, key);
                }
            } catch (Exception e) {
                log.warn("按event+payload验签失败，降级使用原始body验签", e);
            }
        }
        return hmacSha256Hex(body, key);
    }

    private String toSignDataJson(VirtualPaySignDataVO signData) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("offerId", signData.getOfferId());
            payload.put("buyQuantity", signData.getBuyQuantity());
            payload.put("currencyType", signData.getCurrencyType());
            payload.put("outTradeNo", signData.getOutTradeNo());
            payload.put("attach", signData.getAttach());
            payload.put("env", signData.getEnv());
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new BusinessException("构造signData失败", e);
        }
    }

    private String hmacSha256Hex(String content, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (Exception e) {
            throw new BusinessException("生成签名失败", e);
        }
    }

    private String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @lombok.Data
    private static class VirtualRechargePackage {
        private String packageId;
        private String title;
        private String description;
        private BigDecimal amount;
        private Integer coinCount;
        private Integer bonusCoinCount;
        private Boolean enabled;
        private Integer sort;
    }

    @lombok.Data
    private static class NotifyPayload {
        private String outTradeNo;
        private String transactionId;
        private boolean success;
        private Date paymentTime;
    }
}
