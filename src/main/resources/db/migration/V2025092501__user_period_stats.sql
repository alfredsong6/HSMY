-- Flyway migration: introduce time dimension and user period statistics
CREATE TABLE IF NOT EXISTS `t_dim_time` (
    `id` BIGINT NOT NULL COMMENT '主键，雪花ID',
    `date_value` DATE NOT NULL COMMENT '自然日',
    `iso_week` VARCHAR(8) NOT NULL COMMENT 'ISO周，格式YYYY-Www',
    `month_value` CHAR(7) NOT NULL COMMENT '月份，格式YYYY-MM',
    `year_value` CHAR(4) NOT NULL COMMENT '年份，格式YYYY',
    `week_start` DATE NOT NULL COMMENT '周开始日',
    `week_end` DATE NOT NULL COMMENT '周结束日',
    `month_start` DATE NOT NULL COMMENT '月开始日',
    `month_end` DATE NOT NULL COMMENT '月结束日',
    `quarter` TINYINT NOT NULL COMMENT '季度1-4',
    `is_weekend` TINYINT NOT NULL DEFAULT 0 COMMENT '是否周末：0-否 1-是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dim_time_date` (`date_value`),
    KEY `idx_dim_time_week` (`iso_week`),
    KEY `idx_dim_time_month` (`month_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间维度表';

CREATE TABLE IF NOT EXISTS `t_user_period_stats` (
    `id` BIGINT NOT NULL COMMENT '主键，雪花ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `time_id` BIGINT NOT NULL COMMENT '关联t_dim_time.id',
    `period_type` ENUM('DAY','WEEK','MONTH','YEAR') NOT NULL COMMENT '统计周期类型',
    `knock_count` BIGINT NOT NULL DEFAULT 0 COMMENT '周期敲击次数',
    `merit_gained` BIGINT NOT NULL DEFAULT 0 COMMENT '周期功德增量',
    `max_combo` INT NOT NULL DEFAULT 0 COMMENT '周期最大连击数',
    `create_by` VARCHAR(50) NOT NULL DEFAULT 'system',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_by` VARCHAR(50) NOT NULL DEFAULT 'system',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-否 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_period_time` (`user_id`,`period_type`,`time_id`),
    KEY `idx_period_user_type` (`user_id`,`period_type`),
    CONSTRAINT `fk_user_period_time` FOREIGN KEY (`time_id`) REFERENCES `t_dim_time` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户周期敲击功德统计';

ALTER TABLE `t_user_stats`
    DROP COLUMN IF EXISTS `today_merit`,
    DROP COLUMN IF EXISTS `today_knocks`,
    DROP COLUMN IF EXISTS `weekly_merit`,
    DROP COLUMN IF EXISTS `monthly_merit`;
