package com.hsmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsmy.entity.Donation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 捐赠记录Mapper接口
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Mapper
public interface DonationMapper extends BaseMapper<Donation> {
    
    /**
     * 查询用户捐赠记录
     * 
     * @param userId 用户ID
     * @return 捐赠记录列表
     */
    List<Donation> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 查询项目捐赠记录
     * 
     * @param projectId 项目ID
     * @param limit 限制数量
     * @return 捐赠记录列表
     */
    List<Map<String, Object>> selectByProjectId(@Param("projectId") Long projectId, @Param("limit") Integer limit);
    
    /**
     * 统计用户捐赠总额
     * 
     * @param userId 用户ID
     * @return 捐赠总额
     */
    Long sumUserDonation(@Param("userId") Long userId);
    
    /**
     * 查询善缘榜
     * 
     * @param limit 限制数量
     * @return 善缘榜列表
     */
    List<Map<String, Object>> selectDonationRanking(@Param("limit") Integer limit);
}