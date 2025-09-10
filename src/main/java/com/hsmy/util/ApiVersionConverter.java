package com.hsmy.util;

import com.hsmy.vo.ExchangeVO;
import com.hsmy.vo.v1_1.ExchangeVOV1_1;
import com.hsmy.vo.v1_1.BalanceResponseVOV1_1;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * API版本转换工具类
 * 用于不同版本间的数据转换和兼容性处理
 * 
 * @author HSMY
 * @date 2025/09/10
 */
public class ApiVersionConverter {
    
    /**
     * 将v1.1的ExchangeVO转换为v1.0的ExchangeVO
     * 
     * @param v1_1VO v1.1版本的VO
     * @return v1.0版本的VO
     */
    public static ExchangeVO convertToV1_0(ExchangeVOV1_1 v1_1VO) {
        if (v1_1VO == null) {
            return null;
        }
        
        ExchangeVO v1_0VO = new ExchangeVO();
        BeanUtils.copyProperties(v1_1VO, v1_0VO);
        return v1_0VO;
    }
    
    /**
     * 将v1.0的ExchangeVO转换为v1.1的ExchangeVO
     * 
     * @param v1_0VO v1.0版本的VO
     * @return v1.1版本的VO
     */
    public static ExchangeVOV1_1 convertToV1_1(ExchangeVO v1_0VO) {
        if (v1_0VO == null) {
            return null;
        }
        
        ExchangeVOV1_1 v1_1VO = new ExchangeVOV1_1();
        BeanUtils.copyProperties(v1_0VO, v1_1VO);
        
        // 设置v1.1的默认值
        v1_1VO.setExchangeType(1);  // 默认普通兑换
        v1_1VO.setUseCoupon(false); // 默认不使用优惠券
        v1_1VO.setImmediate(true);  // 默认立即生效
        
        return v1_1VO;
    }
    
    /**
     * 根据API版本转换余额响应数据
     * 
     * @param balanceData 余额数据
     * @param apiVersion API版本
     * @return 转换后的响应数据
     */
    @SuppressWarnings("unchecked")
    public static Object convertBalanceResponse(Map<String, Object> balanceData, String apiVersion) {
        if (balanceData == null) {
            return null;
        }
        
        if ("v1.1".equals(apiVersion)) {
            // V1.1版本返回详细的余额信息
            BalanceResponseVOV1_1 response = new BalanceResponseVOV1_1();
            response.setUserId((Long) balanceData.get("userId"));
            response.setTotalMerit((Long) balanceData.getOrDefault("totalMerit", 0L));
            response.setMeritCoins((Integer) balanceData.getOrDefault("meritCoins", 0));
            response.setTodayMerit((Long) balanceData.getOrDefault("todayMerit", 0L));
            response.setWeeklyMerit((Long) balanceData.getOrDefault("weeklyMerit", 0L));
            response.setMonthlyMerit((Long) balanceData.getOrDefault("monthlyMerit", 0L));
            response.setUserLevel((Integer) balanceData.getOrDefault("userLevel", 1));
            response.setNeedMeritForNextLevel((Long) balanceData.getOrDefault("needMeritForNextLevel", 1000L));
            response.setAccountStatus((Integer) balanceData.getOrDefault("accountStatus", 1));
            response.setLastKnockTime((Long) balanceData.get("lastKnockTime"));
            response.setContinuousSignDays((Integer) balanceData.getOrDefault("continuousSignDays", 0));
            return response;
        } else {
            // V1.0版本返回简单的余额信息
            Map<String, Object> simpleBalance = new HashMap<>();
            simpleBalance.put("userId", balanceData.get("userId"));
            simpleBalance.put("totalMerit", balanceData.getOrDefault("totalMerit", 0L));
            simpleBalance.put("meritCoins", balanceData.getOrDefault("meritCoins", 0));
            return simpleBalance;
        }
    }
    
    /**
     * 检查API版本兼容性
     * 
     * @param clientVersion 客户端版本
     * @param serverVersion 服务器版本
     * @return 是否兼容
     */
    public static boolean isCompatible(String clientVersion, String serverVersion) {
        if (clientVersion == null || serverVersion == null) {
            return false;
        }
        
        // 同版本直接兼容
        if (clientVersion.equals(serverVersion)) {
            return true;
        }
        
        // v1.1向下兼容v1.0
        if ("v1.1".equals(serverVersion) && "v1.0".equals(clientVersion)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 获取推荐的服务器API版本
     * 
     * @param clientVersion 客户端版本
     * @return 推荐的服务器版本
     */
    public static String getRecommendedServerVersion(String clientVersion) {
        if ("v1.0".equals(clientVersion)) {
            return "v1.0";  // 老客户端使用v1.0接口
        } else if ("v1.1".equals(clientVersion)) {
            return "v1.1";  // 新客户端使用v1.1接口
        }
        return "v1.0";  // 默认版本
    }
    
    /**
     * 添加版本信息到响应数据
     * 
     * @param responseData 响应数据
     * @param apiVersion API版本
     * @return 带版本信息的响应数据
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> addVersionInfo(Object responseData, String apiVersion) {
        if (responseData instanceof Map) {
            Map<String, Object> dataMap = (Map<String, Object>) responseData;
            dataMap.put("apiVersion", apiVersion);
            dataMap.put("serverTime", System.currentTimeMillis());
            return dataMap;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", responseData);
        result.put("apiVersion", apiVersion);
        result.put("serverTime", System.currentTimeMillis());
        return result;
    }
}