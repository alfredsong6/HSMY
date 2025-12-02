package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.UserItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户道具Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/08
 */
@Mapper
public interface UserItemMapper extends BaseMapper<UserItem> {
    
    /**
     * 根据用户ID查询用户道具列表
     * 
     * @param userId 用户ID
     * @return 用户道具列表
     */
    List<UserItem> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和道具类型查询道具
     * 
     * @param userId 用户ID
     * @param itemType 道具类型
     * @return 用户道具列表
     */
    List<UserItem> selectByUserIdAndType(@Param("userId") Long userId, @Param("itemType") String itemType);
    
    /**
     * 根据用户ID查询已装备的道具
     * 
     * @param userId 用户ID
     * @return 已装备道具列表
     */
    List<UserItem> selectEquippedByUserId(@Param("userId") Long userId);
    
    /**
     * 更新道具装备状态
     * 
     * @param userId 用户ID
     * @param itemId 道具ID
     * @param isEquipped 是否装备
     * @return 影响行数
     */
    int updateEquipStatus(@Param("userId") Long userId, @Param("itemId") Long itemId, @Param("isEquipped") Integer isEquipped);
    
    /**
     * 取消用户某类型道具的装备状态
     * 
     * @param userId 用户ID
     * @param itemType 道具类型
     * @return 影响行数
     */
    int unequipByUserIdAndType(@Param("userId") Long userId, @Param("itemType") String itemType);

    /**
     * 将已过期的用户道具标记为过期状态。
     *
     * @return 影响行数
     */
    int markExpiredItems();
}
