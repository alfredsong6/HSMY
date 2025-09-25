SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 冥想会话表
CREATE TABLE IF NOT EXISTS `t_meditation_session` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `session_id` varchar(50) NOT NULL COMMENT '会话UUID，便于前端与日志追踪',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `planned_duration` int(11) NOT NULL COMMENT '计划冥想时长（秒）',
  `actual_duration` int(11) NULL DEFAULT NULL COMMENT '实际冥想时长（秒）',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `with_knock` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否伴随敲击：0-否，1-是',
  `knock_frequency` int(11) NULL DEFAULT NULL COMMENT '敲击频率（次/分钟）',
  `mood_code` varchar(30) NULL DEFAULT NULL COMMENT '结束时选择的心情枚举',
  `insight_text` varchar(500) NULL DEFAULT NULL COMMENT '一句领悟的话语',
  `nickname_generated` varchar(100) NULL DEFAULT NULL COMMENT '系统生成的冥想昵称',
  `merit_tag` varchar(100) NULL DEFAULT NULL COMMENT '自动生成的功德标签',
  `config_snapshot` text NULL COMMENT '本次冥想配置快照(JSON)',
  `save_flag` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否保存：0-丢弃，1-保存',
  `coin_cost` int(11) NULL DEFAULT 0 COMMENT '会话预扣功德币数量',
  `coin_refunded` int(11) NULL DEFAULT 0 COMMENT '已退还的功德币数量',
  `payment_status` varchar(20) NULL DEFAULT 'RESERVED' COMMENT '支付状态：RESERVED/SETTLED/REFUNDED',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_meditation_session_id` (`session_id`) USING BTREE,
  KEY `idx_meditation_user_time` (`user_id`, `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='冥想会话表';

-- 冥想每日统计表
CREATE TABLE IF NOT EXISTS `t_meditation_daily_stats` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `session_count` int(11) NOT NULL DEFAULT 0 COMMENT '当日冥想次数',
  `total_minutes` int(11) NOT NULL DEFAULT 0 COMMENT '当日累计时长（分钟）',
  `last_mood` varchar(30) NULL DEFAULT NULL COMMENT '最近一次冥想的心情',
  `last_insight` varchar(500) NULL DEFAULT NULL COMMENT '最近一次冥想的领悟',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_meditation_stat_user_date` (`user_id`, `stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='冥想每日统计表';

-- 冥想订阅表
CREATE TABLE IF NOT EXISTS `t_meditation_subscription` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `plan_type` varchar(20) NOT NULL COMMENT '订阅类型：DAY/WEEK/MONTH',
  `start_time` datetime NOT NULL COMMENT '订阅开始时间',
  `end_time` datetime NOT NULL COMMENT '订阅结束时间',
  `status` varchar(20) NOT NULL DEFAULT 'CURRENT' COMMENT '状态：CURRENT/EXPIRED/CANCELLED',
  `coin_cost` int(11) NOT NULL COMMENT '本次订阅消耗功德币数量',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '关联功德币流水ID(t_merit_coin_transaction)',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_meditation_sub_user` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='冥想订阅表';

-- 冥想用户偏好表
CREATE TABLE IF NOT EXISTS `t_meditation_user_pref` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID（唯一）',
  `default_duration` int(11) NULL DEFAULT NULL COMMENT '默认冥想时长（秒）',
  `default_with_knock` tinyint(4) NULL DEFAULT 0 COMMENT '默认是否伴随敲击：0-否，1-是',
  `default_knock_frequency` int(11) NULL DEFAULT NULL COMMENT '默认敲击频率（次/分钟）',
  `last_updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '偏好更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='冥想用户偏好表';

-- 功德币流水表
CREATE TABLE IF NOT EXISTS `t_merit_coin_transaction` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `biz_type` varchar(50) NOT NULL COMMENT '业务类型：如MEDITATION_SUBSCRIBE',
  `biz_id` bigint(20) NULL DEFAULT NULL COMMENT '业务主键（对应业务表ID）',
  `change_amount` int(11) NOT NULL COMMENT '变动金额（正增负减）',
  `balance_after` int(11) NOT NULL COMMENT '变动后的功德币余额',
  `remark` varchar(255) NULL DEFAULT NULL COMMENT '备注说明',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_coin_tx_user` (`user_id`),
  KEY `idx_coin_tx_biz` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='功德币流水表';

SET FOREIGN_KEY_CHECKS = 1;
