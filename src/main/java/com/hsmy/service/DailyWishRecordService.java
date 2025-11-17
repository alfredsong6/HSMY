package com.hsmy.service;

import com.hsmy.vo.DailyWishRecordVO;

import java.util.List;

/**
 * 每日愿望记录Service
 *
 * @author HSMY
 * @date 2025/11/17
 */
public interface DailyWishRecordService {

    /**
     * 创建每日愿望记录
     *
     * @param userId 用户ID
     * @param dailyWishRecordVO 愿望内容
     * @return 是否成功
     */
    Boolean createDailyWish(Long userId, DailyWishRecordVO dailyWishRecordVO);

    /**
     * 查询用户的每日愿望记录
     *
     * @param userId 用户ID
     * @return 愿望记录列表
     */
    List<DailyWishRecordVO> listDailyWishes(Long userId);

    /**
     * 查询用户当天愿望记录
     *
     * @param userId 用户ID
     * @return 当天愿望记录列表
     */
    List<DailyWishRecordVO> listTodayDailyWishes(Long userId);

    /**
     * 查询用户最近一次愿望记录
     *
     * @param userId 用户ID
     * @return 最近一次愿望
     */
    DailyWishRecordVO getLastDailyWish(Long userId);
}
