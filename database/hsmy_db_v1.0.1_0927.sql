/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : localhost:3306
 Source Schema         : hsmy_db

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 27/09/2025 15:39:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_achievement
-- ----------------------------
DROP TABLE IF EXISTS `t_achievement`;
CREATE TABLE `t_achievement` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `achievement_name` varchar(100) NOT NULL COMMENT '成就名称',
  `achievement_type` varchar(30) NOT NULL COMMENT '成就类型：knock-敲击，login-登录，merit-功德，social-社交，donate-捐赠',
  `achievement_level` tinyint(4) DEFAULT '1' COMMENT '成就等级：1-铜，2-银，3-金，4-钻石',
  `description` varchar(500) DEFAULT NULL COMMENT '成就描述',
  `icon_url` varchar(500) DEFAULT NULL COMMENT '成就图标URL',
  `condition_type` varchar(30) NOT NULL COMMENT '条件类型：count-次数，amount-数量，consecutive-连续',
  `condition_value` bigint(20) NOT NULL COMMENT '条件值',
  `reward_type` varchar(30) DEFAULT NULL COMMENT '奖励类型：merit-功德，merit_coin-功德币，item-道具',
  `reward_value` varchar(100) DEFAULT NULL COMMENT '奖励内容',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序序号',
  `is_active` tinyint(4) DEFAULT '1' COMMENT '是否启用：0-禁用，1-启用',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_achievement_type` (`achievement_type`) USING BTREE,
  KEY `idx_is_active` (`is_active`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='成就定义表';

-- ----------------------------
-- Records of t_achievement
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_activity
-- ----------------------------
DROP TABLE IF EXISTS `t_activity`;
CREATE TABLE `t_activity` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `activity_name` varchar(100) NOT NULL COMMENT '活动名称',
  `activity_type` varchar(30) NOT NULL COMMENT '活动类型：festival-节日活动，special-特殊活动，regular-常规活动',
  `description` text COMMENT '活动描述',
  `banner_url` varchar(500) DEFAULT NULL COMMENT '活动横幅URL',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `merit_bonus_rate` decimal(5,2) DEFAULT '1.00' COMMENT '功德加成倍率',
  `rules` text COMMENT '活动规则',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态：0-未开始，1-进行中，2-已结束',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_activity_type` (`activity_type`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_start_end_time` (`start_time`,`end_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='活动定义表';

-- ----------------------------
-- Records of t_activity
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_dim_time
-- ----------------------------
DROP TABLE IF EXISTS `t_dim_time`;
CREATE TABLE `t_dim_time` (
  `id` bigint(20) NOT NULL COMMENT '主键，雪花ID',
  `date_value` date NOT NULL COMMENT '自然日',
  `iso_week` varchar(8) NOT NULL COMMENT 'ISO周，格式YYYY-Www',
  `month_value` char(7) NOT NULL COMMENT '月份，格式YYYY-MM',
  `year_value` char(4) NOT NULL COMMENT '年份，格式YYYY',
  `week_start` date NOT NULL COMMENT '周开始日',
  `week_end` date NOT NULL COMMENT '周结束日',
  `month_start` date NOT NULL COMMENT '月开始日',
  `month_end` date NOT NULL COMMENT '月结束日',
  `quarter` tinyint(4) NOT NULL COMMENT '季度1-4',
  `is_weekend` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否周末：0-否 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dim_time_date` (`date_value`),
  KEY `idx_dim_time_week` (`iso_week`),
  KEY `idx_dim_time_month` (`month_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间维度表';

-- ----------------------------
-- Records of t_dim_time
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_donation
-- ----------------------------
DROP TABLE IF EXISTS `t_donation`;
CREATE TABLE `t_donation` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `project_id` bigint(20) NOT NULL COMMENT '捐赠项目ID',
  `merit_coins_donated` int(11) NOT NULL COMMENT '捐赠功德币数量',
  `message` varchar(500) DEFAULT NULL COMMENT '祈愿留言',
  `is_anonymous` tinyint(4) DEFAULT '0' COMMENT '是否匿名：0-否，1-是',
  `donation_time` datetime NOT NULL COMMENT '捐赠时间',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_project_id` (`project_id`) USING BTREE,
  KEY `idx_donation_time` (`donation_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='捐赠记录表';

-- ----------------------------
-- Records of t_donation
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_donation_project
-- ----------------------------
DROP TABLE IF EXISTS `t_donation_project`;
CREATE TABLE `t_donation_project` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `project_name` varchar(100) NOT NULL COMMENT '项目名称',
  `project_type` varchar(30) NOT NULL COMMENT '项目类型：temple-庙宇建设，release-放生，education-助学，environment-环保',
  `description` text COMMENT '项目描述',
  `target_amount` bigint(20) DEFAULT NULL COMMENT '目标金额（功德币）',
  `current_amount` bigint(20) DEFAULT '0' COMMENT '当前募集金额（功德币）',
  `donor_count` int(11) DEFAULT '0' COMMENT '捐赠人数',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态：0-已结束，1-进行中，2-已完成',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `image_url` varchar(500) DEFAULT NULL COMMENT '项目图片URL',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_project_type` (`project_type`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='捐赠项目表';

-- ----------------------------
-- Records of t_donation_project
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_exchange_record
-- ----------------------------
DROP TABLE IF EXISTS `t_exchange_record`;
CREATE TABLE `t_exchange_record` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `merit_used` bigint(20) NOT NULL COMMENT '使用功德值',
  `merit_coins_gained` int(11) NOT NULL COMMENT '获得功德币',
  `exchange_rate` int(11) DEFAULT '1000' COMMENT '兑换比例：功德值/功德币',
  `exchange_time` datetime NOT NULL COMMENT '兑换时间',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_exchange_time` (`exchange_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='功德兑换记录表';

-- ----------------------------
-- Records of t_exchange_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_invite_record
-- ----------------------------
DROP TABLE IF EXISTS `t_invite_record`;
CREATE TABLE `t_invite_record` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `inviter_id` bigint(20) NOT NULL COMMENT '邀请人ID',
  `invitee_id` bigint(20) DEFAULT NULL COMMENT '被邀请人ID',
  `invite_code` varchar(50) NOT NULL COMMENT '邀请码',
  `invite_time` datetime NOT NULL COMMENT '邀请时间',
  `register_time` datetime DEFAULT NULL COMMENT '注册时间',
  `is_success` tinyint(4) DEFAULT '0' COMMENT '是否成功：0-待注册，1-已注册',
  `inviter_reward` int(11) DEFAULT '0' COMMENT '邀请人奖励功德值',
  `invitee_reward` int(11) DEFAULT '0' COMMENT '被邀请人奖励功德值',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_invite_code` (`invite_code`) USING BTREE,
  KEY `idx_inviter_id` (`inviter_id`) USING BTREE,
  KEY `idx_invitee_id` (`invitee_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='邀请记录表';

-- ----------------------------
-- Records of t_invite_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_item
-- ----------------------------
DROP TABLE IF EXISTS `t_item`;
CREATE TABLE `t_item` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `item_name` varchar(100) NOT NULL COMMENT '道具名称',
  `item_type` varchar(30) NOT NULL COMMENT '道具类型：skin-皮肤，sound-音效，background-背景，frame-头像框',
  `category` varchar(30) DEFAULT NULL COMMENT '道具分类：classic-经典，festival-节日，premium-高级',
  `price` int(11) NOT NULL COMMENT '价格（功德币）',
  `original_price` int(11) DEFAULT NULL COMMENT '原价（功德币）',
  `description` varchar(500) DEFAULT NULL COMMENT '道具描述',
  `preview_url` varchar(500) DEFAULT NULL COMMENT '预览图URL',
  `resource_url` varchar(500) DEFAULT NULL COMMENT '资源文件URL',
  `is_limited` tinyint(4) DEFAULT '0' COMMENT '是否限定：0-否，1-是',
  `limit_time_start` datetime DEFAULT NULL COMMENT '限定开始时间',
  `limit_time_end` datetime DEFAULT NULL COMMENT '限定结束时间',
  `stock` int(11) DEFAULT '-1' COMMENT '库存数量，-1表示无限',
  `sold_count` int(11) DEFAULT '0' COMMENT '已售数量',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序序号',
  `is_active` tinyint(4) DEFAULT '1' COMMENT '是否上架：0-下架，1-上架',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_item_type` (`item_type`) USING BTREE,
  KEY `idx_is_active` (`is_active`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='道具表';

-- ----------------------------
-- Records of t_item
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_knock_session
-- ----------------------------
DROP TABLE IF EXISTS `t_knock_session`;
CREATE TABLE `t_knock_session` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `session_id` varchar(50) NOT NULL COMMENT '会话ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `knock_count` int(11) DEFAULT '0' COMMENT '敲击次数',
  `merit_gained` int(11) DEFAULT '0' COMMENT '获得功德值',
  `max_combo` int(11) DEFAULT '0' COMMENT '最高连击数',
  `knock_type` varchar(20) NOT NULL COMMENT '敲击类型：manual-手动，auto-自动',
  `session_mode` varchar(30) NOT NULL DEFAULT 'MANUAL' COMMENT '会话模式：MANUAL-手动，AUTO_AUTOEND-自动结束，AUTO_TIMED-定时结束',
  `limit_type` varchar(20) DEFAULT NULL COMMENT '限制类型：DURATION-时长，COUNT-次数',
  `limit_value` int(11) DEFAULT NULL COMMENT '限制值，对应秒数或敲击次数',
  `expected_end_time` datetime DEFAULT NULL COMMENT '预计结束时间',
  `merit_multiplier` decimal(8,2) DEFAULT '1.00' COMMENT '会话功德倍率',
  `prop_snapshot` text COMMENT '道具快照JSON',
  `status` varchar(20) NOT NULL DEFAULT 'active' COMMENT '会话状态：active-进行中，stopped-手动停止，completed-已完成，timeout-超时结算',
  `last_heartbeat_time` datetime DEFAULT NULL COMMENT '最后心跳时间',
  `end_reason` varchar(100) DEFAULT NULL COMMENT '结束原因：auto_end、timeout、manual_stop等',
  `coin_cost` int(11) DEFAULT '0' COMMENT '预扣功德币',
  `coin_refunded` int(11) DEFAULT '0' COMMENT '已退还功德币',
  `wallet_txn_id` varchar(64) DEFAULT NULL COMMENT '钱包流水ID',
  `payment_status` varchar(20) DEFAULT 'RESERVED' COMMENT '支付状态：RESERVED-已预扣，SETTLED-已结算，REFUNDED-已退款',
  `device_info` varchar(200) DEFAULT NULL COMMENT '设备信息',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_session_id` (`session_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_start_time` (`start_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='敲击会话表';

-- ----------------------------
-- Records of t_knock_session
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_meditation_daily_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_meditation_daily_stats`;
CREATE TABLE `t_meditation_daily_stats` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `session_count` int(11) NOT NULL DEFAULT '0' COMMENT '当日冥想次数',
  `total_minutes` int(11) NOT NULL DEFAULT '0' COMMENT '当日累计时长（分钟）',
  `last_mood` varchar(30) DEFAULT NULL COMMENT '最近一次冥想的心情',
  `last_insight` varchar(500) DEFAULT NULL COMMENT '最近一次冥想的领悟',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除：0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_meditation_stat_user_date` (`user_id`,`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='冥想每日统计表';

-- ----------------------------
-- Records of t_meditation_daily_stats
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_meditation_session
-- ----------------------------
DROP TABLE IF EXISTS `t_meditation_session`;
CREATE TABLE `t_meditation_session` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `session_id` varchar(50) NOT NULL COMMENT '会话UUID，便于前端与日志追踪',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `planned_duration` int(11) NOT NULL COMMENT '计划冥想时长（秒）',
  `actual_duration` int(11) DEFAULT NULL COMMENT '实际冥想时长（秒）',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `with_knock` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否伴随敲击：0-否，1-是',
  `knock_frequency` int(11) DEFAULT NULL COMMENT '敲击频率（次/分钟）',
  `mood_code` varchar(30) DEFAULT NULL COMMENT '结束时选择的心情枚举',
  `insight_text` varchar(500) DEFAULT NULL COMMENT '一句领悟的话语',
  `nickname_generated` varchar(100) DEFAULT NULL COMMENT '系统生成的冥想昵称',
  `merit_tag` varchar(100) DEFAULT NULL COMMENT '自动生成的功德标签',
  `config_snapshot` text COMMENT '本次冥想配置快照(JSON)',
  `save_flag` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否保存：0-丢弃，1-保存',
  `coin_cost` int(11) DEFAULT '0' COMMENT '会话预扣功德币数量',
  `coin_refunded` int(11) DEFAULT '0' COMMENT '已退还的功德币数量',
  `payment_status` varchar(20) DEFAULT 'RESERVED' COMMENT '支付状态：RESERVED/SETTLED/REFUNDED',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除：0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_meditation_session_id` (`session_id`) USING BTREE,
  KEY `idx_meditation_user_time` (`user_id`,`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='冥想会话表';

-- ----------------------------
-- Records of t_meditation_session
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_meditation_subscription
-- ----------------------------
DROP TABLE IF EXISTS `t_meditation_subscription`;
CREATE TABLE `t_meditation_subscription` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `plan_type` varchar(20) NOT NULL COMMENT '订阅类型：DAY/WEEK/MONTH',
  `start_time` datetime NOT NULL COMMENT '订阅开始时间',
  `end_time` datetime NOT NULL COMMENT '订阅结束时间',
  `status` varchar(20) NOT NULL DEFAULT 'CURRENT' COMMENT '状态：CURRENT/EXPIRED/CANCELLED',
  `coin_cost` int(11) NOT NULL COMMENT '本次订阅消耗功德币数量',
  `order_id` bigint(20) DEFAULT NULL COMMENT '关联功德币流水ID(t_merit_coin_transaction)',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除：0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_meditation_sub_user` (`user_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='冥想订阅表';

-- ----------------------------
-- Records of t_meditation_subscription
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_meditation_user_pref
-- ----------------------------
DROP TABLE IF EXISTS `t_meditation_user_pref`;
CREATE TABLE `t_meditation_user_pref` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID（唯一）',
  `default_duration` int(11) DEFAULT NULL COMMENT '默认冥想时长（秒）',
  `default_with_knock` tinyint(4) DEFAULT '0' COMMENT '默认是否伴随敲击：0-否，1-是',
  `default_knock_frequency` int(11) DEFAULT NULL COMMENT '默认敲击频率（次/分钟）',
  `last_update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '偏好更新时间',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='冥想用户偏好表';

-- ----------------------------
-- Records of t_meditation_user_pref
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_merit_coin_transaction
-- ----------------------------
DROP TABLE IF EXISTS `t_merit_coin_transaction`;
CREATE TABLE `t_merit_coin_transaction` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `biz_type` varchar(50) NOT NULL COMMENT '业务类型：如MEDITATION_SUBSCRIBE',
  `biz_id` bigint(20) DEFAULT NULL COMMENT '业务主键（对应业务表ID）',
  `change_amount` int(11) NOT NULL COMMENT '变动金额（正增负减）',
  `balance_after` int(11) NOT NULL COMMENT '变动后的功德币余额',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注说明',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除：0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_coin_tx_user` (`user_id`),
  KEY `idx_coin_tx_biz` (`biz_type`,`biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='功德币流水表';

-- ----------------------------
-- Records of t_merit_coin_transaction
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_merit_level
-- ----------------------------
DROP TABLE IF EXISTS `t_merit_level`;
CREATE TABLE `t_merit_level` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `level` int(11) NOT NULL COMMENT '等级',
  `level_name` varchar(50) NOT NULL COMMENT '等级名称',
  `min_merit` bigint(20) NOT NULL COMMENT '最低功德值',
  `max_merit` bigint(20) DEFAULT NULL COMMENT '最高功德值，NULL表示无上限',
  `level_benefits` varchar(500) DEFAULT NULL COMMENT '等级特权描述',
  `icon_url` varchar(500) DEFAULT NULL COMMENT '等级图标URL',
  `bonus_rate` decimal(5,2) DEFAULT '1.00' COMMENT '功德加成倍率',
  `daily_exchange_limit` int(11) DEFAULT '100' COMMENT '每日兑换限额（功德币）',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_level` (`level`) USING BTREE,
  KEY `idx_min_merit` (`min_merit`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='功德等级配置表';

-- ----------------------------
-- Records of t_merit_level
-- ----------------------------
BEGIN;
INSERT INTO `t_merit_level` VALUES (1, 1, '小功绩', 0, 50000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (2, 2, '中功绩', 50000, 100000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (3, 3, '大功绩', 100000, 150000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (4, 4, '大好人', 150000, 200000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (5, 5, '萌善者', 200000, 250000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (6, 6, '微善者', 250000, 300000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (7, 7, '善意者', 300000, 350000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (8, 8, '善举者', 350000, 450000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (9, 9, '常善者', 400000, 450000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (10, 10, '德磬者', 450000, 500000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (11, 11, '小善人', 500000, 600000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (12, 12, '中善人', 600000, 700000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (13, 13, '大善人', 700000, 800000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (14, 14, '善德人', 800000, 900000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (15, 15, '至善人', 900000, 1000000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (16, 16, '圣善人', 1000000, 2000000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (17, 17, '大智者', 2000000, 5000000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (18, 18, '大美满', 5000000, 10000000, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
INSERT INTO `t_merit_level` VALUES (19, 19, '尘圆满', 10000000, NULL, NULL, NULL, 1.00, 0, 'system', '2025-09-27 12:11:51', 'system', '2025-09-27 12:11:51', 0);
COMMIT;

-- ----------------------------
-- Table structure for t_merit_record
-- ----------------------------
DROP TABLE IF EXISTS `t_merit_record`;
CREATE TABLE `t_merit_record` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `merit_gained` int(11) NOT NULL COMMENT '获得功德值',
  `base_merit` int(11) DEFAULT '0' COMMENT '基础功德值（未计算倍率前）',
  `knock_type` varchar(20) NOT NULL COMMENT '敲击类型：manual-手动，auto-自动',
  `knock_mode` varchar(30) DEFAULT NULL COMMENT '敲击模式：MANUAL、AUTO_AUTOEND、AUTO_TIMED',
  `source` varchar(30) NOT NULL COMMENT '来源：knock-敲击，task-任务，login-登录，activity-活动，share-分享',
  `session_id` varchar(50) DEFAULT NULL COMMENT '会话ID，用于统计连击',
  `combo_count` int(11) DEFAULT '0' COMMENT '连击数',
  `bonus_rate` decimal(8,2) DEFAULT '1.00' COMMENT '总加成倍率',
  `prop_snapshot` text COMMENT '道具倍率快照JSON',
  `stat_date` date DEFAULT NULL COMMENT '所属自然日，凌晨清零后重置',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_source` (`source`) USING BTREE,
  KEY `idx_merit_record_session` (`session_id`,`create_time`) USING BTREE,
  KEY `idx_stat_date` (`stat_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='功德记录表';

-- ----------------------------
-- Records of t_merit_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `t_operation_log`;
CREATE TABLE `t_operation_log` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型',
  `operation_desc` varchar(500) DEFAULT NULL COMMENT '操作描述',
  `request_method` varchar(10) DEFAULT NULL COMMENT '请求方法：GET，POST等',
  `request_url` varchar(500) DEFAULT NULL COMMENT '请求URL',
  `request_params` text COMMENT '请求参数',
  `response_result` text COMMENT '响应结果',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  `execution_time` int(11) DEFAULT NULL COMMENT '执行时长（毫秒）',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态：0-失败，1-成功',
  `error_msg` text COMMENT '错误信息',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_operation_time` (`operation_time`) USING BTREE,
  KEY `idx_operation_type` (`operation_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='操作日志表';

-- ----------------------------
-- Records of t_operation_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_purchase_record
-- ----------------------------
DROP TABLE IF EXISTS `t_purchase_record`;
CREATE TABLE `t_purchase_record` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `item_id` bigint(20) NOT NULL COMMENT '道具ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `price` int(11) NOT NULL COMMENT '购买价格（功德币）',
  `quantity` int(11) DEFAULT '1' COMMENT '购买数量',
  `total_amount` int(11) NOT NULL COMMENT '总金额（功德币）',
  `purchase_time` datetime NOT NULL COMMENT '购买时间',
  `status` tinyint(4) DEFAULT '1' COMMENT '订单状态：0-失败，1-成功，2-退款',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_order_no` (`order_no`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_purchase_time` (`purchase_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='购买记录表';

-- ----------------------------
-- Records of t_purchase_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_ranking
-- ----------------------------
DROP TABLE IF EXISTS `t_ranking`;
CREATE TABLE `t_ranking` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `rank_type` varchar(20) NOT NULL COMMENT '榜单类型：daily-日榜，weekly-周榜，monthly-月榜，total-总榜',
  `merit_value` bigint(20) NOT NULL COMMENT '功德值',
  `ranking_position` int(11) NOT NULL COMMENT '排名',
  `snapshot_date` date NOT NULL COMMENT '快照日期',
  `period` varchar(20) DEFAULT NULL COMMENT '统计周期：如2025-01表示月榜，2025-W01表示周榜',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_rank_type_date` (`rank_type`,`snapshot_date`) USING BTREE,
  KEY `idx_ranking_position` (`ranking_position`) USING BTREE,
  KEY `idx_ranking_composite` (`rank_type`,`snapshot_date`,`ranking_position`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='排行榜快照表';

-- ----------------------------
-- Records of t_ranking
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_ranking_reward
-- ----------------------------
DROP TABLE IF EXISTS `t_ranking_reward`;
CREATE TABLE `t_ranking_reward` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `rank_type` varchar(20) NOT NULL COMMENT '榜单类型：daily-日榜，weekly-周榜，monthly-月榜',
  `ranking_position` int(11) NOT NULL COMMENT '排名',
  `reward_type` varchar(30) NOT NULL COMMENT '奖励类型：title-称号，skin-皮肤，frame-头像框，merit_coin-功德币',
  `reward_value` varchar(100) NOT NULL COMMENT '奖励内容：道具ID或功德币数量',
  `reward_time` datetime NOT NULL COMMENT '奖励发放时间',
  `is_claimed` tinyint(4) DEFAULT '0' COMMENT '是否已领取：0-未领取，1-已领取',
  `claim_time` datetime DEFAULT NULL COMMENT '领取时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_is_claimed` (`is_claimed`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='排行榜奖励记录表';

-- ----------------------------
-- Records of t_ranking_reward
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_recharge_order
-- ----------------------------
DROP TABLE IF EXISTS `t_recharge_order`;
CREATE TABLE `t_recharge_order` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '充值金额（元）',
  `merit_coins` int(11) NOT NULL COMMENT '获得功德币',
  `bonus_coins` int(11) DEFAULT '0' COMMENT '赠送功德币',
  `payment_method` varchar(30) NOT NULL COMMENT '支付方式：alipay-支付宝，wechat-微信，bank-银行卡',
  `payment_status` tinyint(4) DEFAULT '0' COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败，3-已退款',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '第三方交易号',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_order_no` (`order_no`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_payment_status` (`payment_status`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='充值订单表';

-- ----------------------------
-- Records of t_recharge_order
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_scripture
-- ----------------------------
DROP TABLE IF EXISTS `t_scripture`;
CREATE TABLE `t_scripture` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `scripture_name` varchar(200) NOT NULL COMMENT '典籍名称',
  `scripture_type` varchar(30) NOT NULL COMMENT '典籍类型：sutra-佛经经典，mantra-咒语',
  `author` varchar(100) DEFAULT NULL COMMENT '作者/译者',
  `description` text COMMENT '典籍描述',
  `content` longtext COMMENT '典籍内容',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '封面图片URL',
  `audio_url` varchar(500) DEFAULT NULL COMMENT '音频URL',
  `is_hot` tinyint(4) DEFAULT '0' COMMENT '是否热门：0-否，1-是',
  `price` int(11) NOT NULL DEFAULT '1' COMMENT '购买价格（福币）',
  `permanent_price` int(11) DEFAULT NULL COMMENT '买断价格（福币），NULL表示不支持买断',
  `price_unit` varchar(10) NOT NULL DEFAULT '本' COMMENT '计价单位：本、部、卷、则',
  `duration_months` int(11) DEFAULT '1' COMMENT '购买时长（月）',
  `read_count` bigint(20) DEFAULT '0' COMMENT '阅读次数',
  `purchase_count` int(11) DEFAULT '0' COMMENT '购买次数',
  `difficulty_level` tinyint(4) DEFAULT '1' COMMENT '难度等级：1-初级，2-中级，3-高级',
  `word_count` int(11) DEFAULT '0' COMMENT '字数',
  `category_tags` varchar(200) DEFAULT NULL COMMENT '分类标签，用逗号分隔',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序序号',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_scripture_type` (`scripture_type`) USING BTREE,
  KEY `idx_is_hot` (`is_hot`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_price` (`price`) USING BTREE,
  KEY `idx_sort_order` (`sort_order`) USING BTREE,
  KEY `idx_permanent_price` (`permanent_price`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='典籍表';

-- ----------------------------
-- Records of t_scripture
-- ----------------------------
BEGIN;
INSERT INTO `t_scripture` VALUES (1001, '心经', 'sutra', '玄奘', '《般若波罗蜜多心经》是大乘佛教经典之一，全文共260字，是六百卷《大般若经》的精华。', '观自在菩萨，行深般若波罗蜜多时，照见五蕴皆空，度一切苦厄...', NULL, NULL, 1, 2, 24, '部', 1, 0, 0, 1, 260, '般若,心经,大乘', 1, 1, 'system', '2025-09-27 12:09:44', 'system', '2025-09-27 12:09:49', 0);
INSERT INTO `t_scripture` VALUES (1002, '金刚经', 'sutra', '鸠摩罗什', '《金刚般若波罗蜜经》，简称《金刚经》，是大乘佛教般若部重要经典之一。', '如是我闻，一时，佛在舍卫国祇树给孤独园，与大比丘众千二百五十人俱...', NULL, NULL, 1, 5, 60, '部', 1, 0, 0, 2, 5000, '般若,金刚,大乘', 1, 2, 'system', '2025-09-27 12:09:44', 'system', '2025-09-27 12:09:49', 0);
INSERT INTO `t_scripture` VALUES (1003, '大悲咒', 'mantra', '伽梵达摩', '《千手千眼观世音菩萨广大圆满无碍大悲心陀罗尼》，简称《大悲咒》。', '南无、喝啰怛那、哆啰夜耶，南无、阿唎耶，婆卢羯帝、烁钵啰耶...', NULL, NULL, 1, 3, 36, '则', 1, 0, 0, 1, 415, '观音,大悲咒,密咒', 1, 3, 'system', '2025-09-27 12:09:44', 'system', '2025-09-27 12:09:49', 0);
INSERT INTO `t_scripture` VALUES (1004, '楞严经', 'sutra', '般剌蜜帝', '《大佛顶如来密因修证了义诸菩萨万行首楞严经》，简称《楞严经》，为大乘佛教经典。', '如是我闻：一时，佛在室罗筏城，祇桓精舍...', NULL, NULL, 0, 8, 96, '部', 1, 0, 0, 3, 62000, '楞严,如来藏,大乘', 1, 4, 'system', '2025-09-27 12:09:44', 'system', '2025-09-27 12:09:49', 0);
INSERT INTO `t_scripture` VALUES (1005, '六字大明咒', 'mantra', '', '观世音菩萨心咒，藏传佛教中最常见的咒语之一。', '嗡(ōng)嘛(mā)呢(nī)叭(bēi)咪(mēi)吽(hōng)', NULL, NULL, 1, 1, 12, '则', 1, 0, 0, 1, 6, '观音,六字真言,藏传', 1, 5, 'system', '2025-09-27 12:09:44', 'system', '2025-09-27 12:09:49', 0);
COMMIT;

-- ----------------------------
-- Table structure for t_share_record
-- ----------------------------
DROP TABLE IF EXISTS `t_share_record`;
CREATE TABLE `t_share_record` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `share_type` varchar(30) NOT NULL COMMENT '分享类型：achievement-成就，ranking-排名，invite-邀请',
  `share_platform` varchar(30) NOT NULL COMMENT '分享平台：wechat-微信，qq-QQ，weibo-微博，link-链接',
  `share_content` varchar(500) DEFAULT NULL COMMENT '分享内容',
  `share_url` varchar(500) DEFAULT NULL COMMENT '分享链接',
  `share_time` datetime NOT NULL COMMENT '分享时间',
  `reward_merit` int(11) DEFAULT '0' COMMENT '获得功德奖励',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_share_time` (`share_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='分享记录表';

-- ----------------------------
-- Records of t_share_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_system_message
-- ----------------------------
DROP TABLE IF EXISTS `t_system_message`;
CREATE TABLE `t_system_message` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `title` varchar(200) NOT NULL COMMENT '消息标题',
  `content` text NOT NULL COMMENT '消息内容',
  `message_type` varchar(30) NOT NULL COMMENT '消息类型：system-系统通知，activity-活动通知，reward-奖励通知',
  `target_type` varchar(20) NOT NULL COMMENT '目标类型：all-全体用户，user-指定用户，level-指定等级',
  `target_value` varchar(500) DEFAULT NULL COMMENT '目标值：用户ID列表或等级范围',
  `link_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态：0-草稿，1-已发布，2-已过期',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_message_type` (`message_type`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_publish_time` (`publish_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='系统消息表';

-- ----------------------------
-- Records of t_system_message
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_task
-- ----------------------------
DROP TABLE IF EXISTS `t_task`;
CREATE TABLE `t_task` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `task_type` varchar(30) NOT NULL COMMENT '任务类型：daily-每日任务，weekly-每周任务，achievement-成就任务，activity-活动任务',
  `description` varchar(500) DEFAULT NULL COMMENT '任务描述',
  `icon_url` varchar(500) DEFAULT NULL COMMENT '任务图标URL',
  `target_type` varchar(30) NOT NULL COMMENT '目标类型：knock-敲击，login-登录，share-分享，donate-捐赠',
  `target_value` int(11) NOT NULL COMMENT '目标值',
  `reward_merit` int(11) DEFAULT '0' COMMENT '奖励功德值',
  `reward_coins` int(11) DEFAULT '0' COMMENT '奖励功德币',
  `reward_item_id` bigint(20) DEFAULT NULL COMMENT '奖励道具ID',
  `refresh_type` varchar(20) DEFAULT NULL COMMENT '刷新类型：daily-每日刷新，weekly-每周刷新',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序序号',
  `is_active` tinyint(4) DEFAULT '1' COMMENT '是否启用：0-禁用，1-启用',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_task_type` (`task_type`) USING BTREE,
  KEY `idx_is_active` (`is_active`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='任务定义表';

-- ----------------------------
-- Records of t_task
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL COMMENT '用户ID，雪花算法生成',
  `username` varchar(50) NOT NULL COMMENT '用户名，唯一',
  `password` varchar(255) DEFAULT NULL COMMENT '密码，加密存储',
  `nickname` varchar(50) NOT NULL COMMENT '昵称',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `gender` tinyint(4) DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `register_time` datetime NOT NULL COMMENT '注册时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint(4) DEFAULT '1' COMMENT '账号状态：0-禁用，1-正常，2-冻结',
  `vip_level` tinyint(4) DEFAULT '0' COMMENT 'VIP等级：0-普通用户，1-月卡，2-年卡，3-永久',
  `vip_expire_time` datetime DEFAULT NULL COMMENT 'VIP到期时间',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE,
  KEY `idx_nickname` (`nickname`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_register_time` (`register_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户基础信息表';

-- ----------------------------
-- Records of t_user
-- ----------------------------
BEGIN;
INSERT INTO `t_user` VALUES (1971822665190739968, 'user711', '8b8c039f78b8e5601b3c28ae5e85a370', '重新测试昵称', NULL, NULL, '13888888888', 0, NULL, '2025-09-27 14:22:13', NULL, 1, 0, NULL, 'user711', '2025-09-27 14:22:13', 'system', '2025-09-27 15:15:44', 0);
COMMIT;

-- ----------------------------
-- Table structure for t_user_achievement
-- ----------------------------
DROP TABLE IF EXISTS `t_user_achievement`;
CREATE TABLE `t_user_achievement` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `achievement_id` bigint(20) NOT NULL COMMENT '成就ID',
  `progress` bigint(20) DEFAULT '0' COMMENT '当前进度',
  `is_completed` tinyint(4) DEFAULT '0' COMMENT '是否完成：0-未完成，1-已完成',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `is_claimed` tinyint(4) DEFAULT '0' COMMENT '是否已领取奖励：0-未领取，1-已领取',
  `claim_time` datetime DEFAULT NULL COMMENT '领取时间',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_achievement` (`user_id`,`achievement_id`) USING BTREE,
  KEY `idx_is_completed` (`is_completed`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户成就表';

-- ----------------------------
-- Records of t_user_achievement
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_user_activity
-- ----------------------------
DROP TABLE IF EXISTS `t_user_activity`;
CREATE TABLE `t_user_activity` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `join_time` datetime NOT NULL COMMENT '参与时间',
  `merit_gained` bigint(20) DEFAULT '0' COMMENT '活动中获得的功德值',
  `coins_gained` int(11) DEFAULT '0' COMMENT '活动中获得的功德币',
  `extra_data` json DEFAULT NULL COMMENT '额外数据，JSON格式',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_activity_id` (`activity_id`) USING BTREE,
  KEY `idx_join_time` (`join_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户活动参与记录表';

-- ----------------------------
-- Records of t_user_activity
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_user_item
-- ----------------------------
DROP TABLE IF EXISTS `t_user_item`;
CREATE TABLE `t_user_item` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `item_id` bigint(20) NOT NULL COMMENT '道具ID',
  `purchase_time` datetime NOT NULL COMMENT '购买时间',
  `purchase_price` int(11) NOT NULL COMMENT '购买价格（功德币）',
  `is_equipped` tinyint(4) DEFAULT '0' COMMENT '是否装备：0-否，1-是',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间，NULL表示永久',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_item_id` (`item_id`) USING BTREE,
  KEY `idx_is_equipped` (`is_equipped`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户道具表';

-- ----------------------------
-- Records of t_user_item
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_user_message
-- ----------------------------
DROP TABLE IF EXISTS `t_user_message`;
CREATE TABLE `t_user_message` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `message_id` bigint(20) DEFAULT NULL COMMENT '系统消息ID',
  `title` varchar(200) NOT NULL COMMENT '消息标题',
  `content` text NOT NULL COMMENT '消息内容',
  `message_type` varchar(30) NOT NULL COMMENT '消息类型：system-系统，reward-奖励，friend-好友，achievement-成就',
  `is_read` tinyint(4) DEFAULT '0' COMMENT '是否已读：0-未读，1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `link_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_is_read` (`is_read`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户消息表';

-- ----------------------------
-- Records of t_user_message
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_user_period_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_user_period_stats`;
CREATE TABLE `t_user_period_stats` (
  `id` bigint(20) NOT NULL COMMENT '主键，雪花ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `time_id` bigint(20) NOT NULL COMMENT '关联t_dim_time.id',
  `period_type` enum('DAY','WEEK','MONTH','YEAR') NOT NULL COMMENT '统计周期类型',
  `knock_count` bigint(20) NOT NULL DEFAULT '0' COMMENT '周期敲击次数',
  `merit_gained` bigint(20) NOT NULL DEFAULT '0' COMMENT '周期功德增量',
  `max_combo` int(11) NOT NULL DEFAULT '0' COMMENT '周期最大连击数',
  `create_by` varchar(50) NOT NULL DEFAULT 'system',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(50) NOT NULL DEFAULT 'system',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_period_time` (`user_id`,`period_type`,`time_id`),
  KEY `idx_period_user_type` (`user_id`,`period_type`),
  KEY `fk_user_period_time` (`time_id`),
  CONSTRAINT `fk_user_period_time` FOREIGN KEY (`time_id`) REFERENCES `t_dim_time` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户周期敲击功德统计';

-- ----------------------------
-- Records of t_user_period_stats
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_user_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_user_relation`;
CREATE TABLE `t_user_relation` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_user_id` bigint(20) NOT NULL COMMENT '目标用户ID',
  `relation_type` tinyint(4) NOT NULL COMMENT '关系类型：1-关注，2-好友，3-拉黑',
  `relation_time` datetime NOT NULL COMMENT '建立关系时间',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_relation` (`user_id`,`target_user_id`,`relation_type`) USING BTREE,
  KEY `idx_target_user_id` (`target_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户关系表';

-- ----------------------------
-- Records of t_user_relation
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_user_scripture_purchase
-- ----------------------------
DROP TABLE IF EXISTS `t_user_scripture_purchase`;
CREATE TABLE `t_user_scripture_purchase` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `scripture_id` bigint(20) NOT NULL COMMENT '典籍ID',
  `merit_coins_paid` int(11) NOT NULL COMMENT '支付福币数量',
  `purchase_months` int(11) DEFAULT '1' COMMENT '购买月数',
  `purchase_time` datetime NOT NULL COMMENT '购买时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `is_expired` tinyint(4) DEFAULT '0' COMMENT '是否过期：0-未过期，1-已过期',
  `read_count` int(11) DEFAULT '0' COMMENT '阅读次数',
  `last_read_time` datetime DEFAULT NULL COMMENT '最后阅读时间',
  `reading_progress` decimal(5,2) DEFAULT '0.00' COMMENT '阅读进度百分比',
  `last_reading_position` int(11) DEFAULT '0' COMMENT '最后阅读位置（字符位置）',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_scripture` (`user_id`,`scripture_id`,`is_deleted`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_scripture_id` (`scripture_id`) USING BTREE,
  KEY `idx_expire_time` (`expire_time`) USING BTREE,
  KEY `idx_is_expired` (`is_expired`) USING BTREE,
  KEY `idx_purchase_time` (`purchase_time`) USING BTREE,
  KEY `idx_last_reading_position` (`last_reading_position`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户典籍购买记录表';

-- ----------------------------
-- Records of t_user_scripture_purchase
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_user_setting
-- ----------------------------
DROP TABLE IF EXISTS `t_user_setting`;
CREATE TABLE `t_user_setting` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `sound_enabled` tinyint(4) DEFAULT '1' COMMENT '音效开关：0-关闭，1-开启',
  `sound_volume` int(11) DEFAULT '80' COMMENT '音量大小：0-100',
  `vibration_enabled` tinyint(4) DEFAULT '1' COMMENT '震动反馈：0-关闭，1-开启',
  `daily_reminder` tinyint(4) DEFAULT '1' COMMENT '每日提醒：0-关闭，1-开启',
  `reminder_time` time DEFAULT '09:00:00' COMMENT '提醒时间',
  `privacy_mode` tinyint(4) DEFAULT '0' COMMENT '隐私模式：0-公开，1-仅好友可见，2-完全隐私',
  `auto_knock_speed` int(11) DEFAULT '1' COMMENT '自动敲击速度：1-慢速，2-中速，3-快速',
  `theme_id` bigint(20) DEFAULT NULL COMMENT '当前主题ID',
  `skin_id` bigint(20) DEFAULT NULL COMMENT '当前皮肤ID',
  `sound_id` bigint(20) DEFAULT NULL COMMENT '当前音效ID',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户设置表';

-- ----------------------------
-- Records of t_user_setting
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_user_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_user_stats`;
CREATE TABLE `t_user_stats` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `total_merit` bigint(20) DEFAULT '0' COMMENT '总功德值',
  `merit_coins` bigint(20) DEFAULT '0' COMMENT '功德币余额',
  `total_knocks` bigint(20) DEFAULT '0' COMMENT '总敲击次数',
  `consecutive_days` int(11) DEFAULT '0' COMMENT '连续登录天数',
  `total_login_days` int(11) DEFAULT '0' COMMENT '总登录天数',
  `current_level` int(11) DEFAULT '1' COMMENT '当前等级',
  `max_combo` int(11) DEFAULT '0' COMMENT '最高连击数',
  `last_knock_time` datetime DEFAULT NULL COMMENT '最后敲击时间',
  `last_login_date` date DEFAULT NULL COMMENT '最后登录日期',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_id` (`user_id`) USING BTREE,
  KEY `idx_total_merit` (`total_merit`) USING BTREE,
  KEY `idx_update_time` (`update_time`) USING BTREE,
  KEY `idx_user_stats_daily` (`last_login_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户统计表';

-- ----------------------------
-- Records of t_user_stats
-- ----------------------------
BEGIN;
INSERT INTO `t_user_stats` VALUES (1971822665643724800, 1971822665190739968, 1000, 1000, 20, 20, 10, 1, 0, NULL, NULL, 'user711', '2025-09-27 14:22:13', 'user711', '2025-09-27 15:39:15', 0);
COMMIT;

-- ----------------------------
-- Table structure for t_user_task
-- ----------------------------
DROP TABLE IF EXISTS `t_user_task`;
CREATE TABLE `t_user_task` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `task_date` date NOT NULL COMMENT '任务日期',
  `progress` int(11) DEFAULT '0' COMMENT '当前进度',
  `is_completed` tinyint(4) DEFAULT '0' COMMENT '是否完成：0-未完成，1-已完成',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `is_claimed` tinyint(4) DEFAULT '0' COMMENT '是否已领取奖励：0-未领取，1-已领取',
  `claim_time` datetime DEFAULT NULL COMMENT '领取时间',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_task_date` (`user_id`,`task_id`,`task_date`) USING BTREE,
  KEY `idx_task_date` (`task_date`) USING BTREE,
  KEY `idx_is_completed` (`is_completed`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户任务进度表';

-- ----------------------------
-- Records of t_user_task
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_verification_code
-- ----------------------------
DROP TABLE IF EXISTS `t_verification_code`;
CREATE TABLE `t_verification_code` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `account` varchar(100) NOT NULL COMMENT '账号（手机号或邮箱）',
  `account_type` varchar(20) NOT NULL COMMENT '账号类型：phone-手机号，email-邮箱',
  `code` varchar(10) NOT NULL COMMENT '验证码',
  `business_type` varchar(30) NOT NULL COMMENT '业务类型：register-注册，login-登录，reset-重置密码，bind-绑定',
  `used` tinyint(4) DEFAULT '0' COMMENT '是否已使用：0-未使用，1-已使用',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `ip_address` varchar(50) DEFAULT NULL COMMENT '请求IP地址',
  `device_info` varchar(200) DEFAULT NULL COMMENT '设备信息',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_account` (`account`) USING BTREE,
  KEY `idx_code` (`code`) USING BTREE,
  KEY `idx_business_type` (`business_type`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_expire_time` (`expire_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='验证码表';

-- ----------------------------
-- Records of t_verification_code
-- ----------------------------
BEGIN;
INSERT INTO `t_verification_code` VALUES (1971808087916548096, '13800000000', 'phone', '385232', 'register', 0, NULL, '2025-09-27 13:29:17', '0:0:0:0:0:0:0:1', NULL, 'system', '2025-09-27 13:24:17', 'system', '2025-09-27 13:24:17', 0);
INSERT INTO `t_verification_code` VALUES (1971816543662772224, '13800000000', 'phone', '809185', 'register', 0, NULL, '2025-09-27 14:02:53', '0:0:0:0:0:0:0:1', NULL, 'system', '2025-09-27 13:57:53', 'system', '2025-09-27 13:57:53', 0);
INSERT INTO `t_verification_code` VALUES (1971822566377132032, '13888888888', 'phone', '369807', 'register', 0, NULL, '2025-09-27 14:26:49', '0:0:0:0:0:0:0:1', NULL, 'system', '2025-09-27 14:21:49', 'system', '2025-09-27 14:21:49', 0);
INSERT INTO `t_verification_code` VALUES (1971822589647130624, 'test@example.com', 'email', '902079', 'login', 0, NULL, '2025-09-27 14:26:55', '0:0:0:0:0:0:0:1', NULL, 'system', '2025-09-27 14:21:55', 'system', '2025-09-27 14:21:54', 1);
COMMIT;

-- ----------------------------
-- Table structure for t_vip_package
-- ----------------------------
DROP TABLE IF EXISTS `t_vip_package`;
CREATE TABLE `t_vip_package` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `package_name` varchar(100) NOT NULL COMMENT '套餐名称',
  `package_type` tinyint(4) NOT NULL COMMENT '套餐类型：1-月卡，2-季卡，3-年卡，4-永久',
  `price` decimal(10,2) NOT NULL COMMENT '价格（元）',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价（元）',
  `duration_days` int(11) NOT NULL COMMENT '时长（天），-1表示永久',
  `merit_bonus_rate` decimal(5,2) DEFAULT '1.00' COMMENT '功德加成倍率',
  `daily_merit_bonus` int(11) DEFAULT '0' COMMENT '每日额外功德值',
  `description` varchar(500) DEFAULT NULL COMMENT '套餐描述',
  `benefits` text COMMENT '套餐权益，JSON格式',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序序号',
  `is_active` tinyint(4) DEFAULT '1' COMMENT '是否启用：0-禁用，1-启用',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_package_type` (`package_type`) USING BTREE,
  KEY `idx_is_active` (`is_active`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='VIP套餐表';

-- ----------------------------
-- Records of t_vip_package
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_vip_purchase
-- ----------------------------
DROP TABLE IF EXISTS `t_vip_purchase`;
CREATE TABLE `t_vip_purchase` (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `package_id` bigint(20) NOT NULL COMMENT '套餐ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `price` decimal(10,2) NOT NULL COMMENT '实付金额（元）',
  `start_time` datetime NOT NULL COMMENT '生效时间',
  `end_time` datetime DEFAULT NULL COMMENT '到期时间，NULL表示永久',
  `payment_method` varchar(30) NOT NULL COMMENT '支付方式',
  `payment_status` tinyint(4) DEFAULT '0' COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_order_no` (`order_no`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_payment_status` (`payment_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='VIP购买记录表';

-- ----------------------------
-- Records of t_vip_purchase
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
