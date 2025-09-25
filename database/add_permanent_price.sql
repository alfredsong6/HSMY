-- 为t_scripture表添加买断价格字段
-- Date: 2025-09-25

ALTER TABLE `t_scripture`
ADD COLUMN `permanent_price` INT NULL DEFAULT NULL COMMENT '买断价格（福币），NULL表示不支持买断' AFTER `price`;

-- 更新示例数据，设置买断价格
UPDATE `t_scripture` SET `permanent_price` = `price` * 12 WHERE `id` IN (1001, 1002, 1003, 1004, 1005);

-- 为新字段添加索引
ALTER TABLE `t_scripture`
ADD INDEX `idx_permanent_price`(`permanent_price`) USING BTREE;