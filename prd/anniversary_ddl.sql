-- 周年打卡活动实名信息表 DDL
-- 说明：按 openId 绑定实名信息，记录公司/部门/姓名。基于 MySQL 8.0，雪花ID。

CREATE TABLE IF NOT EXISTS `t_anniversary_punch_realname` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `openid` VARCHAR(128) NOT NULL COMMENT '用户openId',
    `company_name` VARCHAR(100) NOT NULL COMMENT '公司名称',
    `department_name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `real_name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `face_photo_url` VARCHAR(500) DEFAULT NULL COMMENT '人脸照片URL（用于比对）',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`),
    KEY `idx_company_department` (`company_name`, `department_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='周年打卡活动实名信息表';
