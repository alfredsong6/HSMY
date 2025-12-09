-- Schema adjustments for scripture sections and per-section progress
-- Date: 2025-02-XX

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- t_scripture: add section/meta fields
ALTER TABLE `t_scripture`
    ADD COLUMN `permanent_price` INT NULL DEFAULT NULL COMMENT '买断价格（福币），NULL表示不支持买断' AFTER `price`,
    ADD COLUMN `total_word_count` INT NULL DEFAULT 0 COMMENT '整本总字数（分段汇总）' AFTER `word_count`,
    ADD COLUMN `section_count` INT NULL DEFAULT 0 COMMENT '分段/卷总数' AFTER `total_word_count`,
    ADD COLUMN `preview_section_count` INT NULL DEFAULT 0 COMMENT '试读分段数' AFTER `section_count`;

-- 典籍分段表
DROP TABLE IF EXISTS `t_scripture_section`;
CREATE TABLE `t_scripture_section` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `scripture_id` BIGINT NOT NULL COMMENT '典籍ID',
    `section_no` INT NOT NULL COMMENT '分段序号（从1开始）',
    `title` VARCHAR(200) NULL DEFAULT NULL COMMENT '分段标题',
    `content` LONGTEXT NULL COMMENT '分段正文（可存Markdown）',
    `audio_url` VARCHAR(500) NULL DEFAULT NULL COMMENT '分段音频URL',
    `word_count` INT NULL DEFAULT 0 COMMENT '该分段字数',
    `duration_seconds` INT NULL DEFAULT 0 COMMENT '朗读/播放时长（秒，可选）',
    `is_free` TINYINT NULL DEFAULT 0 COMMENT '是否可试读：0-否，1-是',
    `sort_order` INT NULL DEFAULT 0 COMMENT '排序序号，通常与 section_no 一致',
    `create_by` VARCHAR(50) NULL DEFAULT 'system',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) NULL DEFAULT 'system',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_scripture_section` (`scripture_id`, `section_no`),
    KEY `idx_scripture` (`scripture_id`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='典籍分段表';

-- 用户分段进度表
DROP TABLE IF EXISTS `t_user_scripture_progress`;
CREATE TABLE `t_user_scripture_progress` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `scripture_id` BIGINT NOT NULL COMMENT '典籍ID',
    `section_id` BIGINT NOT NULL COMMENT '分段ID（t_scripture_section.id）',
    `reading_progress` DECIMAL(5,2) NULL DEFAULT 0.00 COMMENT '该分段进度百分比',
    `last_position` INT NULL DEFAULT 0 COMMENT '分段内最后阅读位置（字符偏移或段落序号）',
    `last_read_time` DATETIME NULL DEFAULT NULL COMMENT '最后阅读时间',
    `spend_seconds` INT NULL DEFAULT 0 COMMENT '累计阅读时长（秒，可选）',
    `is_completed` TINYINT NULL DEFAULT 0 COMMENT '是否已完成该分段',
    `create_by` VARCHAR(50) NULL DEFAULT 'system',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) NULL DEFAULT 'system',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_section` (`user_id`, `section_id`),
    KEY `idx_user_scripture` (`user_id`, `scripture_id`),
    KEY `idx_last_read_time` (`last_read_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户典籍分段进度表';

-- t_user_scripture_purchase: 购买类型、状态与分段快照
ALTER TABLE `t_user_scripture_purchase`
    ADD COLUMN `purchase_type` VARCHAR(20) NOT NULL DEFAULT 'lease' COMMENT 'lease-租赁, permanent-买断, free-赠送, trial-试读' AFTER `scripture_id`,
    ADD COLUMN `activated_time` DATETIME NULL COMMENT '生效时间' AFTER `purchase_time`,
    ADD COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1-有效 2-过期 3-退款/失效' AFTER `expire_time`,
    ADD COLUMN `last_reading_position` INT NULL DEFAULT 0 COMMENT '最后阅读位置（字符偏移）' AFTER `reading_progress`,
    ADD COLUMN `last_section_id` BIGINT NULL COMMENT '最后阅读的分段ID' AFTER `last_reading_position`,
    ADD COLUMN `completed_sections` INT NULL DEFAULT 0 COMMENT '已完成分段数' AFTER `last_section_id`;

-- 统一状态/过期索引
ALTER TABLE `t_user_scripture_purchase`
    ADD INDEX `idx_status`(`status`) USING BTREE,
    ADD INDEX `idx_last_section_id`(`last_section_id`) USING BTREE;

SET FOREIGN_KEY_CHECKS = 1;
