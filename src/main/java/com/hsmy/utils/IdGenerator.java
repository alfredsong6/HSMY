package com.hsmy.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * ID生成工具类（雪花算法）
 * 
 * @author HSMY
 * @date 2025/09/07
 */
public class IdGenerator {
    
    /**
     * 雪花算法实例
     */
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);
    
    /**
     * 获取下一个ID
     * 
     * @return ID
     */
    public static Long nextId() {
        return SNOWFLAKE.nextId();
    }
    
    /**
     * 获取下一个ID字符串
     * 
     * @return ID字符串
     */
    public static String nextIdStr() {
        return String.valueOf(SNOWFLAKE.nextId());
    }
}