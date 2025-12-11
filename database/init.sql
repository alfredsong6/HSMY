-- 敲敲木鱼数据库初始化脚本
-- MySQL 8.0+
-- 使用雪花算法生成ID，禁用外键约束和自增主键
-- Author: HSMY Team
-- Date: 2025-09-07

-- 创建数据库
-- CREATE DATABASE IF NOT EXISTS `hsmy_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE `hsmy_db`;

-- ========================================
-- 1. 用户相关表
-- ========================================

-- 用户基础信息表
CREATE TABLE IF NOT EXISTS `t_user` (
    `id` BIGINT NOT NULL COMMENT '用户ID，雪花算法生成',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名，唯一',
    `password` VARCHAR(255) NOT NULL COMMENT '密码，加密存储',
    `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
    `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `register_time` DATETIME NOT NULL COMMENT '注册时间',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `status` TINYINT DEFAULT 1 COMMENT '账号状态：0-禁用，1-正常，2-冻结',
    `vip_level` TINYINT DEFAULT 0 COMMENT 'VIP等级：0-普通用户，1-月卡，2-年卡，3-永久',
    `vip_expire_time` DATETIME DEFAULT NULL COMMENT 'VIP到期时间',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_nickname` (`nickname`),
    KEY `idx_status` (`status`),
    KEY `idx_register_time` (`register_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基础信息表';

-- 用户统计表
CREATE TABLE IF NOT EXISTS `t_user_stats` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_merit` BIGINT DEFAULT 0 COMMENT '总功德值',
    `merit_coins` BIGINT DEFAULT 0 COMMENT '功德币余额',
    `total_knocks` BIGINT DEFAULT 0 COMMENT '总敲击次数',
    `today_merit` BIGINT DEFAULT 0 COMMENT '今日功德值',
    `today_knocks` BIGINT DEFAULT 0 COMMENT '今日敲击次数',
    `weekly_merit` BIGINT DEFAULT 0 COMMENT '本周功德值',
    `monthly_merit` BIGINT DEFAULT 0 COMMENT '本月功德值',
    `consecutive_days` INT DEFAULT 0 COMMENT '连续登录天数',
    `total_login_days` INT DEFAULT 0 COMMENT '总登录天数',
    `current_level` INT DEFAULT 1 COMMENT '当前等级',
    `max_combo` INT DEFAULT 0 COMMENT '最高连击数',
    `last_knock_time` DATETIME DEFAULT NULL COMMENT '最后敲击时间',
    `last_login_date` DATE DEFAULT NULL COMMENT '最后登录日期',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_total_merit` (`total_merit`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户统计表';

-- 用户设置表
CREATE TABLE IF NOT EXISTS `t_user_setting` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `sound_enabled` TINYINT DEFAULT 1 COMMENT '音效开关：0-关闭，1-开启',
    `sound_volume` INT DEFAULT 80 COMMENT '音量大小：0-100',
    `vibration_enabled` TINYINT DEFAULT 1 COMMENT '震动反馈：0-关闭，1-开启',
    `daily_reminder` TINYINT DEFAULT 1 COMMENT '每日提醒：0-关闭，1-开启',
    `reminder_time` TIME DEFAULT '09:00:00' COMMENT '提醒时间',
    `privacy_mode` TINYINT DEFAULT 0 COMMENT '隐私模式：0-公开，1-仅好友可见，2-完全隐私',
    `auto_knock_speed` INT DEFAULT 1 COMMENT '自动敲击速度：1-慢速，2-中速，3-快速',
    `theme_id` BIGINT DEFAULT NULL COMMENT '当前主题ID',
    `skin_id` BIGINT DEFAULT NULL COMMENT '当前皮肤ID',
    `sound_id` BIGINT DEFAULT NULL COMMENT '当前音效ID',
    `scripture_id` BIGINT DEFAULT NULL COMMENT '弹幕经书ID(bullet_screen=3时使用)',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设置表';

-- ========================================
-- 2. 功德系统相关表
-- ========================================

-- 功德记录表
CREATE TABLE IF NOT EXISTS `t_merit_record` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merit_gained` INT NOT NULL COMMENT '获得功德值',
    `knock_type` VARCHAR(20) NOT NULL COMMENT '敲击类型：manual-手动，auto-自动',
    `source` VARCHAR(30) NOT NULL COMMENT '来源：knock-敲击，task-任务，login-登录，activity-活动，share-分享',
    `session_id` VARCHAR(50) DEFAULT NULL COMMENT '会话ID，用于统计连击',
    `combo_count` INT DEFAULT 0 COMMENT '连击数',
    `bonus_rate` DECIMAL(5,2) DEFAULT 1.00 COMMENT '加成倍率',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_source` (`source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功德记录表';

-- 功德兑换记录表
CREATE TABLE IF NOT EXISTS `t_exchange_record` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merit_used` BIGINT NOT NULL COMMENT '使用功德值',
    `merit_coins_gained` INT NOT NULL COMMENT '获得功德币',
    `exchange_rate` INT DEFAULT 1000 COMMENT '兑换比例：功德值/功德币',
    `exchange_time` DATETIME NOT NULL COMMENT '兑换时间',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_exchange_time` (`exchange_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功德兑换记录表';

-- 功德等级配置表
CREATE TABLE IF NOT EXISTS `t_merit_level` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `level` INT NOT NULL COMMENT '等级',
    `level_name` VARCHAR(50) NOT NULL COMMENT '等级名称',
    `min_merit` BIGINT NOT NULL COMMENT '最低功德值',
    `max_merit` BIGINT DEFAULT NULL COMMENT '最高功德值，NULL表示无上限',
    `level_benefits` VARCHAR(500) DEFAULT NULL COMMENT '等级特权描述',
    `icon_url` VARCHAR(500) DEFAULT NULL COMMENT '等级图标URL',
    `bonus_rate` DECIMAL(5,2) DEFAULT 1.00 COMMENT '功德加成倍率',
    `daily_exchange_limit` INT DEFAULT 100 COMMENT '每日兑换限额（功德币）',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_level` (`level`),
    KEY `idx_min_merit` (`min_merit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功德等级配置表';

-- ========================================
-- 3. 捐赠系统相关表
-- ========================================

-- 捐赠项目表
CREATE TABLE IF NOT EXISTS `t_donation_project` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `project_name` VARCHAR(100) NOT NULL COMMENT '项目名称',
    `project_type` VARCHAR(30) NOT NULL COMMENT '项目类型：temple-庙宇建设，release-放生，education-助学，environment-环保',
    `description` TEXT COMMENT '项目描述',
    `target_amount` BIGINT DEFAULT NULL COMMENT '目标金额（功德币）',
    `current_amount` BIGINT DEFAULT 0 COMMENT '当前募集金额（功德币）',
    `donor_count` INT DEFAULT 0 COMMENT '捐赠人数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-已结束，1-进行中，2-已完成',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '项目图片URL',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_project_type` (`project_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='捐赠项目表';

-- 捐赠记录表
CREATE TABLE IF NOT EXISTS `t_donation` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `project_id` BIGINT NOT NULL COMMENT '捐赠项目ID',
    `merit_coins_donated` INT NOT NULL COMMENT '捐赠功德币数量',
    `message` VARCHAR(500) DEFAULT NULL COMMENT '祈愿留言',
    `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
    `donation_time` DATETIME NOT NULL COMMENT '捐赠时间',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_donation_time` (`donation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='捐赠记录表';

-- ========================================
-- 4. 道具商城相关表
-- ========================================

-- 道具表
CREATE TABLE IF NOT EXISTS `t_item` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `item_name` VARCHAR(100) NOT NULL COMMENT '道具名称',
    `item_type` VARCHAR(30) NOT NULL COMMENT '道具类型：skin-皮肤，sound-音效，background-背景，frame-头像框',
    `category` VARCHAR(30) DEFAULT NULL COMMENT '道具分类：classic-经典，festival-节日，premium-高级',
    `price` INT NOT NULL COMMENT '价格（功德币）',
    `original_price` INT DEFAULT NULL COMMENT '原价（功德币）',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '道具描述',
    `preview_url` VARCHAR(500) DEFAULT NULL COMMENT '预览图URL',
    `resource_url` VARCHAR(500) DEFAULT NULL COMMENT '资源文件URL',
    `is_limited` TINYINT DEFAULT 0 COMMENT '是否限定：0-否，1-是',
    `limit_time_start` DATETIME DEFAULT NULL COMMENT '限定开始时间',
    `limit_time_end` DATETIME DEFAULT NULL COMMENT '限定结束时间',
    `stock` INT DEFAULT -1 COMMENT '库存数量，-1表示无限',
    `sold_count` INT DEFAULT 0 COMMENT '已售数量',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `is_active` TINYINT DEFAULT 1 COMMENT '是否上架：0-下架，1-上架',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_item_type` (`item_type`),
    KEY `idx_is_active` (`is_active`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='道具表';

-- 用户道具表
CREATE TABLE IF NOT EXISTS `t_user_item` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `item_id` BIGINT NOT NULL COMMENT '道具ID',
    `purchase_time` DATETIME NOT NULL COMMENT '购买时间',
    `purchase_price` INT NOT NULL COMMENT '购买价格（功德币）',
    `is_equipped` TINYINT DEFAULT 0 COMMENT '是否装备：0-否，1-是',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间，NULL表示永久',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_is_equipped` (`is_equipped`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户道具表';

-- 购买记录表
CREATE TABLE IF NOT EXISTS `t_purchase_record` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `item_id` BIGINT NOT NULL COMMENT '道具ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `price` INT NOT NULL COMMENT '购买价格（功德币）',
    `quantity` INT DEFAULT 1 COMMENT '购买数量',
    `total_amount` INT NOT NULL COMMENT '总金额（功德币）',
    `purchase_time` DATETIME NOT NULL COMMENT '购买时间',
    `status` TINYINT DEFAULT 1 COMMENT '订单状态：0-失败，1-成功，2-退款',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_purchase_time` (`purchase_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购买记录表';

-- ========================================
-- 5. 排行榜相关表
-- ========================================

-- 排行榜快照表
CREATE TABLE IF NOT EXISTS `t_ranking` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `rank_type` VARCHAR(20) NOT NULL COMMENT '榜单类型：daily-日榜，weekly-周榜，monthly-月榜，total-总榜',
    `merit_value` BIGINT NOT NULL COMMENT '功德值',
    `ranking_position` INT NOT NULL COMMENT '排名',
    `snapshot_date` DATE NOT NULL COMMENT '快照日期',
    `period` VARCHAR(20) DEFAULT NULL COMMENT '统计周期：如2025-01表示月榜，2025-W01表示周榜',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_rank_type_date` (`rank_type`, `snapshot_date`),
    KEY `idx_ranking_position` (`ranking_position`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排行榜快照表';

-- 排行榜奖励记录表
CREATE TABLE IF NOT EXISTS `t_ranking_reward` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `rank_type` VARCHAR(20) NOT NULL COMMENT '榜单类型：daily-日榜，weekly-周榜，monthly-月榜',
    `ranking_position` INT NOT NULL COMMENT '排名',
    `reward_type` VARCHAR(30) NOT NULL COMMENT '奖励类型：title-称号，skin-皮肤，frame-头像框，merit_coin-功德币',
    `reward_value` VARCHAR(100) NOT NULL COMMENT '奖励内容：道具ID或功德币数量',
    `reward_time` DATETIME NOT NULL COMMENT '奖励发放时间',
    `is_claimed` TINYINT DEFAULT 0 COMMENT '是否已领取：0-未领取，1-已领取',
    `claim_time` DATETIME DEFAULT NULL COMMENT '领取时间',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_claimed` (`is_claimed`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排行榜奖励记录表';

-- ========================================
-- 6. 成就系统相关表
-- ========================================

-- 成就定义表
CREATE TABLE IF NOT EXISTS `t_achievement` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `achievement_name` VARCHAR(100) NOT NULL COMMENT '成就名称',
    `achievement_type` VARCHAR(30) NOT NULL COMMENT '成就类型：knock-敲击，login-登录，merit-功德，social-社交，donate-捐赠',
    `achievement_level` TINYINT DEFAULT 1 COMMENT '成就等级：1-铜，2-银，3-金，4-钻石',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '成就描述',
    `icon_url` VARCHAR(500) DEFAULT NULL COMMENT '成就图标URL',
    `condition_type` VARCHAR(30) NOT NULL COMMENT '条件类型：count-次数，amount-数量，consecutive-连续',
    `condition_value` BIGINT NOT NULL COMMENT '条件值',
    `reward_type` VARCHAR(30) DEFAULT NULL COMMENT '奖励类型：merit-功德，merit_coin-功德币，item-道具',
    `reward_value` VARCHAR(100) DEFAULT NULL COMMENT '奖励内容',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `is_active` TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_achievement_type` (`achievement_type`),
    KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成就定义表';

-- 用户成就表
CREATE TABLE IF NOT EXISTS `t_user_achievement` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `achievement_id` BIGINT NOT NULL COMMENT '成就ID',
    `progress` BIGINT DEFAULT 0 COMMENT '当前进度',
    `is_completed` TINYINT DEFAULT 0 COMMENT '是否完成：0-未完成，1-已完成',
    `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `is_claimed` TINYINT DEFAULT 0 COMMENT '是否已领取奖励：0-未领取，1-已领取',
    `claim_time` DATETIME DEFAULT NULL COMMENT '领取时间',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_achievement` (`user_id`, `achievement_id`),
    KEY `idx_is_completed` (`is_completed`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户成就表';

-- ========================================
-- 7. 任务系统相关表
-- ========================================

-- 任务定义表
CREATE TABLE IF NOT EXISTS `t_task` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `task_name` VARCHAR(100) NOT NULL COMMENT '任务名称',
    `task_type` VARCHAR(30) NOT NULL COMMENT '任务类型：daily-每日任务，weekly-每周任务，achievement-成就任务，activity-活动任务',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '任务描述',
    `icon_url` VARCHAR(500) DEFAULT NULL COMMENT '任务图标URL',
    `target_type` VARCHAR(30) NOT NULL COMMENT '目标类型：knock-敲击，login-登录，share-分享，donate-捐赠',
    `target_value` INT NOT NULL COMMENT '目标值',
    `reward_merit` INT DEFAULT 0 COMMENT '奖励功德值',
    `reward_coins` INT DEFAULT 0 COMMENT '奖励功德币',
    `reward_item_id` BIGINT DEFAULT NULL COMMENT '奖励道具ID',
    `refresh_type` VARCHAR(20) DEFAULT NULL COMMENT '刷新类型：daily-每日刷新，weekly-每周刷新',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `is_active` TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_task_type` (`task_type`),
    KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务定义表';

-- 用户任务进度表
CREATE TABLE IF NOT EXISTS `t_user_task` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `task_date` DATE NOT NULL COMMENT '任务日期',
    `progress` INT DEFAULT 0 COMMENT '当前进度',
    `is_completed` TINYINT DEFAULT 0 COMMENT '是否完成：0-未完成，1-已完成',
    `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `is_claimed` TINYINT DEFAULT 0 COMMENT '是否已领取奖励：0-未领取，1-已领取',
    `claim_time` DATETIME DEFAULT NULL COMMENT '领取时间',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_task_date` (`user_id`, `task_id`, `task_date`),
    KEY `idx_task_date` (`task_date`),
    KEY `idx_is_completed` (`is_completed`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户任务进度表';

-- ========================================
-- 8. 活动系统相关表
-- ========================================

-- 活动定义表
CREATE TABLE IF NOT EXISTS `t_activity` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `activity_name` VARCHAR(100) NOT NULL COMMENT '活动名称',
    `activity_type` VARCHAR(30) NOT NULL COMMENT '活动类型：festival-节日活动，special-特殊活动，regular-常规活动',
    `description` TEXT COMMENT '活动描述',
    `banner_url` VARCHAR(500) DEFAULT NULL COMMENT '活动横幅URL',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `merit_bonus_rate` DECIMAL(5,2) DEFAULT 1.00 COMMENT '功德加成倍率',
    `rules` TEXT COMMENT '活动规则',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-未开始，1-进行中，2-已结束',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_activity_type` (`activity_type`),
    KEY `idx_status` (`status`),
    KEY `idx_start_end_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动定义表';

-- 用户活动参与记录表
CREATE TABLE IF NOT EXISTS `t_user_activity` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `join_time` DATETIME NOT NULL COMMENT '参与时间',
    `merit_gained` BIGINT DEFAULT 0 COMMENT '活动中获得的功德值',
    `coins_gained` INT DEFAULT 0 COMMENT '活动中获得的功德币',
    `extra_data` JSON DEFAULT NULL COMMENT '额外数据，JSON格式',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_join_time` (`join_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户活动参与记录表';

-- ========================================
-- 9. 社交系统相关表
-- ========================================

-- 用户关系表
CREATE TABLE IF NOT EXISTS `t_user_relation` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `target_user_id` BIGINT NOT NULL COMMENT '目标用户ID',
    `relation_type` TINYINT NOT NULL COMMENT '关系类型：1-关注，2-好友，3-拉黑',
    `relation_time` DATETIME NOT NULL COMMENT '建立关系时间',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_relation` (`user_id`, `target_user_id`, `relation_type`),
    KEY `idx_target_user_id` (`target_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关系表';

-- 分享记录表
CREATE TABLE IF NOT EXISTS `t_share_record` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `share_type` VARCHAR(30) NOT NULL COMMENT '分享类型：achievement-成就，ranking-排名，invite-邀请',
    `share_platform` VARCHAR(30) NOT NULL COMMENT '分享平台：wechat-微信，qq-QQ，weibo-微博，link-链接',
    `share_content` VARCHAR(500) DEFAULT NULL COMMENT '分享内容',
    `share_url` VARCHAR(500) DEFAULT NULL COMMENT '分享链接',
    `share_time` DATETIME NOT NULL COMMENT '分享时间',
    `reward_merit` INT DEFAULT 0 COMMENT '获得功德奖励',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_share_time` (`share_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享记录表';

-- 邀请记录表
CREATE TABLE IF NOT EXISTS `t_invite_record` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `inviter_id` BIGINT NOT NULL COMMENT '邀请人ID',
    `invitee_id` BIGINT DEFAULT NULL COMMENT '被邀请人ID',
    `invite_code` VARCHAR(50) NOT NULL COMMENT '邀请码',
    `invite_time` DATETIME NOT NULL COMMENT '邀请时间',
    `register_time` DATETIME DEFAULT NULL COMMENT '注册时间',
    `is_success` TINYINT DEFAULT 0 COMMENT '是否成功：0-待注册，1-已注册',
    `inviter_reward` INT DEFAULT 0 COMMENT '邀请人奖励功德值',
    `invitee_reward` INT DEFAULT 0 COMMENT '被邀请人奖励功德值',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_invite_code` (`invite_code`),
    KEY `idx_inviter_id` (`inviter_id`),
    KEY `idx_invitee_id` (`invitee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀请记录表';

-- ========================================
-- 10. 消息通知相关表
-- ========================================

-- 系统消息表
CREATE TABLE IF NOT EXISTS `t_system_message` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `message_type` VARCHAR(30) NOT NULL COMMENT '消息类型：system-系统通知，activity-活动通知，reward-奖励通知',
    `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型：all-全体用户，user-指定用户，level-指定等级',
    `target_value` VARCHAR(500) DEFAULT NULL COMMENT '目标值：用户ID列表或等级范围',
    `link_url` VARCHAR(500) DEFAULT NULL COMMENT '跳转链接',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-草稿，1-已发布，2-已过期',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_message_type` (`message_type`),
    KEY `idx_status` (`status`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统消息表';

-- 用户消息表
CREATE TABLE IF NOT EXISTS `t_user_message` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `message_id` BIGINT DEFAULT NULL COMMENT '系统消息ID',
    `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `message_type` VARCHAR(30) NOT NULL COMMENT '消息类型：system-系统，reward-奖励，friend-好友，achievement-成就',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    `read_time` DATETIME DEFAULT NULL COMMENT '阅读时间',
    `link_url` VARCHAR(500) DEFAULT NULL COMMENT '跳转链接',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息表';

-- ========================================
-- 11. 验证码系统相关表
-- ========================================

-- 验证码表
CREATE TABLE IF NOT EXISTS `t_verification_code` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `account` VARCHAR(100) NOT NULL COMMENT '账号（手机号或邮箱）',
    `account_type` VARCHAR(20) NOT NULL COMMENT '账号类型：phone-手机号，email-邮箱',
    `code` VARCHAR(10) NOT NULL COMMENT '验证码',
    `business_type` VARCHAR(30) NOT NULL COMMENT '业务类型：register-注册，login-登录，reset-重置密码，bind-绑定',
    `used` TINYINT DEFAULT 0 COMMENT '是否已使用：0-未使用，1-已使用',
    `use_time` DATETIME DEFAULT NULL COMMENT '使用时间',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT '请求IP地址',
    `device_info` VARCHAR(200) DEFAULT NULL COMMENT '设备信息',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_account` (`account`),
    KEY `idx_code` (`code`),
    KEY `idx_business_type` (`business_type`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

-- ========================================
-- 12. 系统日志相关表
-- ========================================

-- 操作日志表
CREATE TABLE IF NOT EXISTS `t_operation_log` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
    `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `operation_desc` VARCHAR(500) DEFAULT NULL COMMENT '操作描述',
    `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方法：GET，POST等',
    `request_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    `request_params` TEXT DEFAULT NULL COMMENT '请求参数',
    `response_result` TEXT DEFAULT NULL COMMENT '响应结果',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `operation_time` DATETIME NOT NULL COMMENT '操作时间',
    `execution_time` INT DEFAULT NULL COMMENT '执行时长（毫秒）',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_operation_time` (`operation_time`),
    KEY `idx_operation_type` (`operation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 敲击会话表
CREATE TABLE IF NOT EXISTS `t_knock_session` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `session_id` VARCHAR(50) NOT NULL COMMENT '会话ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `knock_count` INT DEFAULT 0 COMMENT '敲击次数',
    `merit_gained` INT DEFAULT 0 COMMENT '获得功德值',
    `max_combo` INT DEFAULT 0 COMMENT '最高连击数',
    `knock_type` VARCHAR(20) NOT NULL COMMENT '敲击类型：manual-手动，auto-自动',
    `device_info` VARCHAR(200) DEFAULT NULL COMMENT '设备信息',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_id` (`session_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敲击会话表';

-- ========================================
-- 13. 钱包系统相关表
-- ========================================

-- 用户钱包表（简化版，只管理功德币）
CREATE TABLE IF NOT EXISTS `t_wallet` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `merit_coins` BIGINT DEFAULT 0 COMMENT '功德币余额',
    `frozen_coins` BIGINT DEFAULT 0 COMMENT '冻结功德币',
    `total_recharge_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计充值金额（元）',
    `total_coins_earned` BIGINT DEFAULT 0 COMMENT '累计获得功德币（含赠送）',
    `total_coins_spent` BIGINT DEFAULT 0 COMMENT '累计消费功德币',
    `first_recharge_time` DATETIME DEFAULT NULL COMMENT '首次充值时间',
    `last_recharge_time` DATETIME DEFAULT NULL COMMENT '最后充值时间',
    `wallet_status` TINYINT DEFAULT 1 COMMENT '钱包状态：0-冻结，1-正常，2-异常',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_wallet_status` (`wallet_status`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包表';

-- 充值活动表
CREATE TABLE IF NOT EXISTS `t_recharge_activity` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `activity_name` VARCHAR(100) NOT NULL COMMENT '活动名称',
    `activity_type` VARCHAR(30) NOT NULL COMMENT '活动类型：threshold-满额赠送，multiply-倍数活动，first-首充活动，daily-每日首充',
    `activity_rule` VARCHAR(50) NOT NULL COMMENT '活动规则：如threshold_50表示满50元',
    `threshold_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '门槛金额（元）',
    `bonus_type` VARCHAR(20) NOT NULL COMMENT '奖励类型：fixed-固定数量，percent-百分比',
    `bonus_value` INT NOT NULL COMMENT '奖励值：固定数量或百分比',
    `max_bonus` INT DEFAULT NULL COMMENT '最大奖励数量',
    `daily_limit` INT DEFAULT NULL COMMENT '每日参与限制：NULL表示不限',
    `total_limit` INT DEFAULT NULL COMMENT '总参与次数限制：NULL表示不限',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '活动描述',
    `banner_url` VARCHAR(500) DEFAULT NULL COMMENT '活动横幅URL',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间，NULL表示长期',
    `is_stackable` TINYINT DEFAULT 0 COMMENT '是否可叠加：0-不可叠加，1-可叠加',
    `priority` INT DEFAULT 0 COMMENT '优先级，数值越大优先级越高',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-未开始，1-进行中，2-已结束，3-已下线',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_activity_type` (`activity_type`),
    KEY `idx_status` (`status`),
    KEY `idx_start_end_time` (`start_time`, `end_time`),
    KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值活动表';

-- 充值套餐表
CREATE TABLE IF NOT EXISTS `t_recharge_package` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `package_name` VARCHAR(100) NOT NULL COMMENT '套餐名称',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '充值金额（元）',
    `base_coins` INT NOT NULL COMMENT '基础功德币（1:10比例）',
    `bonus_coins` INT DEFAULT 0 COMMENT '套餐赠送功德币',
    `total_coins` INT NOT NULL COMMENT '总功德币（基础+赠送）',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '套餐描述',
    `tag` VARCHAR(50) DEFAULT NULL COMMENT '标签：hot-热门，recommend-推荐，limited-限时',
    `icon_url` VARCHAR(500) DEFAULT NULL COMMENT '套餐图标URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `is_active` TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    `start_time` DATETIME DEFAULT NULL COMMENT '生效时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '失效时间',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_amount` (`amount`),
    KEY `idx_is_active` (`is_active`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值套餐表';

-- 充值记录表（记录充值兑换功德币）
CREATE TABLE IF NOT EXISTS `t_recharge_record` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `recharge_no` VARCHAR(50) NOT NULL COMMENT '充值单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `wallet_id` BIGINT NOT NULL COMMENT '钱包ID',
    `package_id` BIGINT DEFAULT NULL COMMENT '充值套餐ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '充值金额（元）',
    `base_coins` INT NOT NULL COMMENT '基础功德币',
    `package_bonus` INT DEFAULT 0 COMMENT '套餐赠送功德币',
    `activity_bonus` INT DEFAULT 0 COMMENT '活动赠送功德币',
    `total_coins` INT NOT NULL COMMENT '总获得功德币',
    `activity_ids` VARCHAR(200) DEFAULT NULL COMMENT '参与的活动ID列表，逗号分隔',
    `payment_method` VARCHAR(30) NOT NULL COMMENT '支付方式：alipay-支付宝，wechat-微信，bank-银行卡',
    `payment_channel` VARCHAR(50) DEFAULT NULL COMMENT '支付渠道：具体的支付通道',
    `recharge_status` TINYINT DEFAULT 0 COMMENT '充值状态：0-待支付，1-支付成功，2-支付失败，3-已退款，4-已取消',
    `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `success_time` DATETIME DEFAULT NULL COMMENT '成功时间',
    `third_party_no` VARCHAR(100) DEFAULT NULL COMMENT '第三方交易号',
    `fail_reason` VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
    `device_info` VARCHAR(200) DEFAULT NULL COMMENT '设备信息',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_recharge_no` (`recharge_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_wallet_id` (`wallet_id`),
    KEY `idx_recharge_status` (`recharge_status`),
    KEY `idx_payment_time` (`payment_time`),
    KEY `idx_third_party_no` (`third_party_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值记录表';

-- 充值活动参与记录表
CREATE TABLE IF NOT EXISTS `t_recharge_activity_record` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `recharge_id` BIGINT NOT NULL COMMENT '充值记录ID',
    `recharge_amount` DECIMAL(10,2) NOT NULL COMMENT '充值金额',
    `bonus_coins` INT NOT NULL COMMENT '获得奖励功德币',
    `participate_time` DATETIME NOT NULL COMMENT '参与时间',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_recharge_id` (`recharge_id`),
    KEY `idx_participate_time` (`participate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值活动参与记录表';

-- 功德币交易记录表（简化版）
CREATE TABLE IF NOT EXISTS `t_coin_transaction` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `transaction_no` VARCHAR(50) NOT NULL COMMENT '交易号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `wallet_id` BIGINT NOT NULL COMMENT '钱包ID',
    `transaction_type` VARCHAR(30) NOT NULL COMMENT '交易类型：recharge-充值获得，consume-消费，reward-奖励，exchange-兑换',
    `transaction_subtype` VARCHAR(50) DEFAULT NULL COMMENT '交易子类型：purchase_item-购买道具，donate-捐赠等',
    `merit_coins` BIGINT NOT NULL COMMENT '交易功德币数量（正数为增加，负数为减少）',
    `before_coins` BIGINT DEFAULT 0 COMMENT '交易前功德币',
    `after_coins` BIGINT DEFAULT 0 COMMENT '交易后功德币',
    `related_id` BIGINT DEFAULT NULL COMMENT '关联ID：充值记录ID、购买记录ID等',
    `related_type` VARCHAR(30) DEFAULT NULL COMMENT '关联类型：recharge-充值，purchase-购买等',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '交易描述',
    `transaction_time` DATETIME NOT NULL COMMENT '交易时间',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_transaction_no` (`transaction_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_wallet_id` (`wallet_id`),
    KEY `idx_transaction_type` (`transaction_type`),
    KEY `idx_transaction_time` (`transaction_time`),
    KEY `idx_related_id_type` (`related_id`, `related_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功德币交易记录表';

-- 功德币消费记录表
CREATE TABLE IF NOT EXISTS `t_coin_consumption` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `consumption_no` VARCHAR(50) NOT NULL COMMENT '消费单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `wallet_id` BIGINT NOT NULL COMMENT '钱包ID',
    `consumption_type` VARCHAR(30) NOT NULL COMMENT '消费类型：purchase-购买，donate-捐赠，exchange-兑换',
    `consumption_name` VARCHAR(200) NOT NULL COMMENT '消费名称',
    `merit_coins` BIGINT NOT NULL COMMENT '消费功德币数量',
    `related_id` BIGINT DEFAULT NULL COMMENT '关联ID：道具ID、捐赠项目ID等',
    `related_type` VARCHAR(30) DEFAULT NULL COMMENT '关联类型：item-道具，project-项目等',
    `related_order_no` VARCHAR(50) DEFAULT NULL COMMENT '关联订单号',
    `consumption_status` TINYINT DEFAULT 1 COMMENT '消费状态：0-待处理，1-成功，2-失败，3-已退款',
    `consumption_time` DATETIME NOT NULL COMMENT '消费时间',
    `refund_time` DATETIME DEFAULT NULL COMMENT '退款时间',
    `refund_coins` BIGINT DEFAULT NULL COMMENT '退款功德币',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '消费描述',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_consumption_no` (`consumption_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_wallet_id` (`wallet_id`),
    KEY `idx_consumption_type` (`consumption_type`),
    KEY `idx_consumption_status` (`consumption_status`),
    KEY `idx_consumption_time` (`consumption_time`),
    KEY `idx_related_order_no` (`related_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功德币消费记录表';

-- ========================================
-- 13. 充值支付相关表（原12节内容）
-- ========================================

-- 充值订单表
CREATE TABLE IF NOT EXISTS `t_recharge_order` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '充值金额（元）',
    `merit_coins` INT NOT NULL COMMENT '获得功德币',
    `bonus_coins` INT DEFAULT 0 COMMENT '赠送功德币',
    `payment_method` VARCHAR(30) NOT NULL COMMENT '支付方式：alipay-支付宝，wechat-微信，bank-银行卡',
    `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败，3-已退款',
    `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `transaction_id` VARCHAR(100) DEFAULT NULL COMMENT '第三方交易号',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_payment_status` (`payment_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值订单表';

-- VIP套餐表
CREATE TABLE IF NOT EXISTS `t_vip_package` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `package_name` VARCHAR(100) NOT NULL COMMENT '套餐名称',
    `package_type` TINYINT NOT NULL COMMENT '套餐类型：1-月卡，2-季卡，3-年卡，4-永久',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格（元）',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价（元）',
    `duration_days` INT NOT NULL COMMENT '时长（天），-1表示永久',
    `merit_bonus_rate` DECIMAL(5,2) DEFAULT 1.00 COMMENT '功德加成倍率',
    `daily_merit_bonus` INT DEFAULT 0 COMMENT '每日额外功德值',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '套餐描述',
    `benefits` TEXT DEFAULT NULL COMMENT '套餐权益，JSON格式',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `is_active` TINYINT DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_package_type` (`package_type`),
    KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='VIP套餐表';

-- VIP购买记录表
CREATE TABLE IF NOT EXISTS `t_vip_purchase` (
    `id` BIGINT NOT NULL COMMENT '主键ID，雪花算法生成',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `package_id` BIGINT NOT NULL COMMENT '套餐ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `price` DECIMAL(10,2) NOT NULL COMMENT '实付金额（元）',
    `start_time` DATETIME NOT NULL COMMENT '生效时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '到期时间，NULL表示永久',
    `payment_method` VARCHAR(30) NOT NULL COMMENT '支付方式',
    `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败',
    `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `create_by` VARCHAR(50) DEFAULT 'system' COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT 'system' COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_payment_status` (`payment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='VIP购买记录表';

-- ========================================
-- 初始化基础数据
-- ========================================

-- 插入功德等级配置
INSERT INTO `t_merit_level` (`id`, `level`, `level_name`, `min_merit`, `max_merit`, `level_benefits`, `bonus_rate`, `daily_exchange_limit`) VALUES
(1, 1, '初级修行者', 0, 10000, '基础功能解锁', 1.00, 100),
(2, 2, '虔诚信徒', 10000, 50000, '解锁更多皮肤和音效', 1.10, 150),
(3, 3, '资深修行者', 50000, 150000, '功德获取加成10%', 1.20, 200),
(4, 4, '得道高僧', 150000, 500000, '功德获取加成20%，专属称号', 1.30, 300),
(5, 5, '佛陀境界', 500000, NULL, '功德获取加成30%，所有特权', 1.50, 500);

-- 插入默认捐赠项目
INSERT INTO `t_donation_project` (`id`, `project_name`, `project_type`, `description`, `status`) VALUES
(1, '虚拟庙宇建设', 'temple', '帮助建设线上虚拟庙宇，让更多人感受佛法', 1),
(2, '放生功德池', 'release', '积累放生功德，护生行善', 1),
(3, '助学善缘基金', 'education', '帮助贫困地区儿童接受教育', 1),
(4, '环保护生项目', 'environment', '保护环境，爱护生命', 1);

-- 插入充值套餐（1元=10功德币基础比例）
INSERT INTO `t_recharge_package` (`id`, `package_name`, `amount`, `base_coins`, `bonus_coins`, `total_coins`, `description`, `tag`, `sort_order`, `is_active`) VALUES
(1, '小额充值', 6.00, 60, 0, 60, '充值6元获得60功德币', NULL, 1, 1),
(2, '基础充值', 30.00, 300, 30, 330, '充值30元获得330功德币（额外赠送30）', 'recommend', 2, 1),
(3, '超值充值', 68.00, 680, 100, 780, '充值68元获得780功德币（额外赠送100）', 'hot', 3, 1),
(4, '豪华充值', 128.00, 1280, 280, 1560, '充值128元获得1560功德币（额外赠送280）', 'hot', 4, 1),
(5, '至尊充值', 328.00, 3280, 1000, 4280, '充值328元获得4280功德币（额外赠送1000）', 'recommend', 5, 1),
(6, '限时特惠', 648.00, 6480, 2500, 8980, '充值648元获得8980功德币（额外赠送2500）', 'limited', 6, 1);

-- 插入充值活动
INSERT INTO `t_recharge_activity` (`id`, `activity_name`, `activity_type`, `activity_rule`, `threshold_amount`, `bonus_type`, `bonus_value`, `max_bonus`, `description`, `start_time`, `status`) VALUES
(1, '首充双倍', 'first', 'first_recharge', NULL, 'percent', 100, 1000, '首次充值获得双倍功德币（最高奖励1000）', '2025-01-01 00:00:00', 1),
(2, '满50送10', 'threshold', 'threshold_50', 50.00, 'fixed', 100, NULL, '单笔充值满50元额外赠送100功德币', '2025-01-01 00:00:00', 1),
(3, '满100送30', 'threshold', 'threshold_100', 100.00, 'fixed', 300, NULL, '单笔充值满100元额外赠送300功德币', '2025-01-01 00:00:00', 1),
(4, '满200送80', 'threshold', 'threshold_200', 200.00, 'fixed', 800, NULL, '单笔充值满200元额外赠送800功德币', '2025-01-01 00:00:00', 1),
(5, '每日首充', 'daily', 'daily_first', NULL, 'percent', 20, 200, '每日首次充值额外获得20%功德币（最高奖励200）', '2025-01-01 00:00:00', 1),
(6, '周末特惠', 'multiply', 'weekend_bonus', NULL, 'percent', 50, 500, '周末充值额外获得50%功德币（最高奖励500）', '2025-01-01 00:00:00', 1);

-- 创建索引优化查询性能
CREATE INDEX idx_merit_record_session ON t_merit_record(session_id, create_time);
CREATE INDEX idx_user_stats_daily ON t_user_stats(last_login_date, today_merit);
CREATE INDEX idx_ranking_composite ON t_ranking(rank_type, snapshot_date, ranking_position);
CREATE INDEX idx_coin_transaction_composite ON t_coin_transaction(user_id, transaction_type, transaction_time);
CREATE INDEX idx_recharge_record_composite ON t_recharge_record(user_id, recharge_status, payment_time);
CREATE INDEX idx_coin_consumption_composite ON t_coin_consumption(user_id, consumption_type, consumption_time);
CREATE INDEX idx_recharge_activity_composite ON t_recharge_activity(activity_type, status, start_time);

-- 数据库初始化完成
