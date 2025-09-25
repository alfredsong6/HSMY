package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.UserScripturePurchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 用户典籍购买记录Mapper接口
 *
 * @author HSMY
 * @date 2025/09/25
 */
@Mapper
public interface UserScripturePurchaseMapper extends BaseMapper<UserScripturePurchase> {

    /**
     * 根据用户ID查询购买记录
     *
     * @param userId 用户ID
     * @return 购买记录列表
     */
    List<UserScripturePurchase> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和典籍ID查询购买记录
     *
     * @param userId      用户ID
     * @param scriptureId 典籍ID
     * @return 购买记录
     */
    UserScripturePurchase selectByUserAndScripture(@Param("userId") Long userId, @Param("scriptureId") Long scriptureId);

    /**
     * 查询用户有效的典籍购买记录
     *
     * @param userId 用户ID
     * @return 有效购买记录列表
     */
    List<UserScripturePurchase> selectValidPurchasesByUserId(@Param("userId") Long userId);

    /**
     * 查询即将过期的购买记录
     *
     * @param days 天数
     * @return 即将过期的购买记录列表
     */
    List<UserScripturePurchase> selectExpiringSoon(@Param("days") Integer days);

    /**
     * 更新阅读记录
     *
     * @param id               购买记录ID
     * @param readingProgress  阅读进度
     * @param lastReadTime     最后阅读时间
     * @return 影响行数
     */
    int updateReadingRecord(@Param("id") Long id, @Param("readingProgress") Double readingProgress, @Param("lastReadTime") Date lastReadTime);

    /**
     * 更新最后阅读位置
     *
     * @param id                    购买记录ID
     * @param lastReadingPosition   最后阅读位置
     * @param lastReadTime          最后阅读时间
     * @return 影响行数
     */
    int updateLastReadingPosition(@Param("id") Long id, @Param("lastReadingPosition") Integer lastReadingPosition, @Param("lastReadTime") Date lastReadTime);

    /**
     * 增加阅读次数
     *
     * @param id 购买记录ID
     * @return 影响行数
     */
    int increaseReadCount(@Param("id") Long id);

    /**
     * 批量更新过期状态
     *
     * @return 影响行数
     */
    int batchUpdateExpiredStatus();

    /**
     * 统计用户购买的典籍总数
     *
     * @param userId 用户ID
     * @return 购买总数
     */
    Integer countPurchasesByUserId(@Param("userId") Long userId);

    /**
     * 查询典籍的购买用户数
     *
     * @param scriptureId 典籍ID
     * @return 购买用户数
     */
    Integer countUsersByScriptureId(@Param("scriptureId") Long scriptureId);
}