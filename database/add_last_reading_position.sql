-- 为t_user_scripture_purchase表添加最后阅读位置字段
-- Date: 2025-09-25

ALTER TABLE `t_user_scripture_purchase`
ADD COLUMN `last_reading_position` INT NULL DEFAULT 0 COMMENT '最后阅读位置（字符位置）' AFTER `reading_progress`;

-- 添加索引以提升查询性能
ALTER TABLE `t_user_scripture_purchase`
ADD INDEX `idx_last_reading_position`(`last_reading_position`) USING BTREE;