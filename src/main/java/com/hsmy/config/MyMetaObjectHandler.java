package com.hsmy.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis Plus字段自动填充处理器
 * 
 * @author HSMY
 * @date 2025/09/07
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    
    /**
     * 插入时填充
     * 
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充创建时间
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        // 填充更新时间
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        // 填充创建人（实际项目中应从当前登录用户获取）
        this.strictInsertFill(metaObject, "createBy", String.class, "system");
        // 填充更新人
        this.strictInsertFill(metaObject, "updateBy", String.class, "system");
        // 填充删除标记
        this.strictInsertFill(metaObject, "isDeleted", Integer.class, 0);
    }
    
    /**
     * 更新时填充
     * 
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        // 填充更新人（实际项目中应从当前登录用户获取）
        this.strictUpdateFill(metaObject, "updateBy", String.class, "system");
    }
}