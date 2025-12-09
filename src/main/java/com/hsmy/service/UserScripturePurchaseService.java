package com.hsmy.service;

import com.hsmy.entity.UserScripturePurchase;

import java.util.Date;
import java.util.List;

/**
 * 用户典籍购买记录Service接口
 *
 * @author HSMY
 * @date 2025/09/25
 */
public interface UserScripturePurchaseService {

    /**
     * 购买典籍
     *
     * @param userId          用户ID
     * @param scriptureId     典籍ID
     * @param purchaseMonths  购买月数
     * @return 是否成功
     */
    Boolean purchaseScripture(Long userId, Long scriptureId, Integer purchaseMonths);

    /**
     * 买断典籍
     *
     * @param userId      用户ID
     * @param scriptureId 典籍ID
     * @return 是否成功
     */
    Boolean purchaseScripturePermanent(Long userId, Long scriptureId);

    /**
     * 根据用户ID获取购买记录
     *
     * @param userId 用户ID
     * @return 购买记录列表
     */
    List<UserScripturePurchase> getPurchasesByUserId(Long userId);

    /**
     * 获取用户有效的典籍购买记录
     *
     * @param userId 用户ID
     * @return 有效购买记录列表
     */
    List<UserScripturePurchase> getValidPurchasesByUserId(Long userId);

    /**
     * 检查用户是否购买了指定典籍
     *
     * @param userId      用户ID
     * @param scriptureId 典籍ID
     * @return 是否已购买
     */
    Boolean hasUserPurchased(Long userId, Long scriptureId);

    /**
     * 检查用户典籍购买是否有效（未过期）
     *
     * @param userId      用户ID
     * @param scriptureId 典籍ID
     * @return 是否有效
     */
    Boolean isUserPurchaseValid(Long userId, Long scriptureId);

    /**
     * 更新阅读记录
     *
     * @param userId          用户ID
     * @param scriptureId     典籍ID
     * @param readingProgress 阅读进度
     * @return 是否成功
     */
    Boolean updateReadingProgress(Long userId, Long scriptureId, Double readingProgress);

    /**
     * 更新最后阅读位置
     *
     * @param userId              用户ID
     * @param scriptureId         典籍ID
     * @param lastReadingPosition 最后阅读位置
     * @return 是否成功
     */
    Boolean updateLastReadingPosition(Long userId, Long scriptureId, Integer lastReadingPosition);

    /**
     * 记录用户阅读行为
     *
     * @param userId      用户ID
     * @param scriptureId 典籍ID
     * @return 是否成功
     */
    Boolean recordUserReading(Long userId, Long scriptureId);

    /**
     * 更新分段阅读进度并同步整本快照
     *
     * @param userId 用户ID
     * @param scriptureId 典籍ID
     * @param sectionId 分段ID
     * @param lastPosition 分段内位置
     * @param sectionProgress 分段进度
     * @param totalProgress 整本进度（可为null表示不更新整本）
     * @param spendSeconds 阅读耗时（秒，可选）
     * @param completed 是否完成分段
     * @return 是否成功
     */
    Boolean updateSectionProgress(Long userId, Long scriptureId, Long sectionId,
                                  Integer lastPosition, Double sectionProgress,
                                  Double totalProgress, Integer spendSeconds, boolean completed);

    /**
     * 获取即将过期的购买记录
     *
     * @param days 天数
     * @return 即将过期的购买记录列表
     */
    List<UserScripturePurchase> getExpiringSoonPurchases(Integer days);

    /**
     * 批量更新过期状态
     *
     * @return 更新数量
     */
    Integer updateExpiredStatus();

    /**
     * 统计用户购买的典籍总数
     *
     * @param userId 用户ID
     * @return 购买总数
     */
    Integer countUserPurchases(Long userId);

    /**
     * 统计典籍的购买用户数
     *
     * @param scriptureId 典籍ID
     * @return 购买用户数
     */
    Integer countScripturePurchasers(Long scriptureId);

    /**
     * 获取用户购买记录详情
     *
     * @param userId      用户ID
     * @param scriptureId 典籍ID
     * @return 购买记录
     */
    UserScripturePurchase getUserPurchaseDetail(Long userId, Long scriptureId);

    /**
     * 续费典籍
     *
     * @param userId         用户ID
     * @param scriptureId    典籍ID
     * @param extendMonths   续费月数
     * @return 是否成功
     */
    Boolean renewScripture(Long userId, Long scriptureId, Integer extendMonths);
}
