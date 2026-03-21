package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.VipPurchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * VIP purchase mapper.
 */
@Mapper
public interface VipPurchaseMapper extends BaseMapper<VipPurchase> {

    /**
     * Count successful purchases by user and package.
     */
    int countSuccessByUserAndPackage(@Param("userId") Long userId, @Param("packageId") Long packageId);

    /**
     * Grouped successful purchase counts by package for a user.
     */
    List<Map<String, Object>> selectSuccessCountGroupByPackage(@Param("userId") Long userId);

    /**
     * Select latest successful purchase by user.
     */
    VipPurchase selectLatestSuccessByUserId(@Param("userId") Long userId);
}
