package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.VipPackage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * VIP package mapper.
 */
@Mapper
public interface VipPackageMapper extends BaseMapper<VipPackage> {

    /**
     * Select all active vip packages.
     */
    List<VipPackage> selectAllActive();

    /**
     * Select active vip package by id.
     */
    VipPackage selectActiveById(@Param("id") Long id);
}
