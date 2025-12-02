/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50720
 Source Host           : localhost:3306
 Source Schema         : hsmy_db

 Target Server Type    : MySQL
 Target Server Version : 50720
 File Encoding         : 65001

 Date: 02/12/2025 18:29:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_achievement
-- ----------------------------
DROP TABLE IF EXISTS `t_achievement`;
CREATE TABLE `t_achievement`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `achievement_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '成就名称',
  `achievement_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '成就类型：knock-敲击，login-登录，merit-功德，social-社交，donate-捐赠',
  `achievement_level` tinyint(4) NULL DEFAULT 1 COMMENT '成就等级：1-铜，2-银，3-金，4-钻石',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '成就描述',
  `icon_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '成就图标URL',
  `condition_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '条件类型：count-次数，amount-数量，consecutive-连续',
  `condition_value` bigint(20) NOT NULL COMMENT '条件值',
  `reward_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '奖励类型：merit-功德，merit_coin-功德币，item-道具',
  `reward_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '奖励内容',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序序号',
  `is_active` tinyint(4) NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_achievement_type`(`achievement_type`) USING BTREE,
  INDEX `idx_is_active`(`is_active`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '成就定义表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_achievement
-- ----------------------------

-- ----------------------------
-- Table structure for t_activity
-- ----------------------------
DROP TABLE IF EXISTS `t_activity`;
CREATE TABLE `t_activity`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `activity_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动名称',
  `activity_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动类型：festival-节日活动，special-特殊活动，regular-常规活动',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '活动描述',
  `banner_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '活动横幅URL',
  `start_time` datetime(0) NOT NULL COMMENT '开始时间',
  `end_time` datetime(0) NOT NULL COMMENT '结束时间',
  `merit_bonus_rate` decimal(5, 2) NULL DEFAULT 1.00 COMMENT '功德加成倍率',
  `rules` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '活动规则',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态：0-未开始，1-进行中，2-已结束',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序序号',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_type`(`activity_type`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_start_end_time`(`start_time`, `end_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '活动定义表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_activity
-- ----------------------------
INSERT INTO `t_activity` VALUES (890000012345678901, '充值10送得10功德币', 'cash-top-up', '限时赠送5功德币', 'https://static.hsmy.com/banner/recharge-tier-10.jpg', '2024-02-01 00:00:00', '2029-12-31 23:59:59', 1.10, '{\"amount\":10,\"give\":10,\"gift\":5,\"crash-top-up\":15}', 1, 10, 'system', '2025-11-17 17:03:40', 'system', '2025-11-27 11:15:51', 0);
INSERT INTO `t_activity` VALUES (890000012345678902, '充值20得20功德币', 'cash-top-up', '限时赠送10功德币', 'https://static.hsmy.com/banner/recharge-tier-66.jpg', '2024-02-01 00:00:00', '2029-12-31 23:59:59', 1.09, '{\"amount\":20,\"give\":20,\"gift\":10,\"crash-top-up\":30}', 1, 20, 'system', '2025-11-17 17:03:40', 'system', '2025-11-27 11:16:51', 0);
INSERT INTO `t_activity` VALUES (890000012345678903, '充值50得50功德币', 'cash-top-up', '限时赠送20功德币', 'https://static.hsmy.com/banner/recharge-tier-100.jpg', '2024-02-01 00:00:00', '2029-12-31 23:59:59', 1.10, '{\"amount\":50,\"give\":50,\"gift\":20,\"crash-top-up\":70}', 1, 30, 'system', '2025-11-17 17:03:40', 'system', '2025-11-27 11:17:54', 0);
INSERT INTO `t_activity` VALUES (890000012345678904, '充值100得100功德币', 'cash-top-up', '限时赠送50功德币', 'https://static.hsmy.com/banner/recharge-tier-100.jpg', '2024-02-01 00:00:00', '2029-12-31 23:59:59', 1.10, '{\"amount\":100,\"give\":100,\"gift\":20,\"crash-top-up\":150}', 1, 30, 'system', '2025-11-17 17:03:40', 'system', '2025-11-27 11:18:27', 0);

-- ----------------------------
-- Table structure for t_auth_identity
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_identity`;
CREATE TABLE `t_auth_identity`  (
  `id` bigint(20) UNSIGNED NOT NULL COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '关联\r\n  业务用户ID',
  `provider` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份提供\r\n  方：wechat_mini/wechat_app/sms 等',
  `appid_or_client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '对应小程\r\n  序appid或App客户端ID',
  `open_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开放\r\n  平台openId（微信等）',
  `union_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开放\r\n  平台unionId（用于多端合并，可空）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机\r\n  号（provider=sms 时必填）',
  `session_key_enc` varbinary(256) NULL DEFAULT NULL COMMENT '微信\r\n  session_key加密存储',
  `last_login_at` datetime(3) NULL DEFAULT NULL COMMENT '最近\r\n  登录时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_ai_provider_app_open`(`provider`, `appid_or_client_id`, `open_id`) USING BTREE,
  UNIQUE INDEX `uk_ai_provider_union`(`provider`, `union_id`) USING BTREE,
  UNIQUE INDEX `uk_ai_phone`(`phone`) USING BTREE,
  INDEX `idx_ai_user`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '统一登录身份' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_auth_identity
-- ----------------------------
INSERT INTO `t_auth_identity` VALUES (1993526498035781634, 1990331081332756480, 'sms', 'app', NULL, NULL, '17688701711', NULL, '2025-11-26 11:45:29.712', 'system', '2025-11-26 11:45:30', 'system', '2025-11-26 11:45:30', 0);
INSERT INTO `t_auth_identity` VALUES (1993582100082601986, 1993582099294195712, 'wechat_mini', 'wx4d903ff814121a1d', 'olXF_14ZzG084MeIBRCWhz3WoIQo', NULL, '17688701791', 0x397452633068594D352F6D6249384E4F7730347671673D3D, '2025-11-28 10:12:06.800', 'system', '2025-11-26 15:26:26', 'system', '2025-11-28 10:12:06', 0);

-- ----------------------------
-- Table structure for t_auth_token
-- ----------------------------
DROP TABLE IF EXISTS `t_auth_token`;
CREATE TABLE `t_auth_token`  (
  `id` bigint(20) UNSIGNED NOT NULL COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联用户ID',
  `token` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '下发给前端的   \r\n  token/JWT',
  `expires_at` datetime(3) NOT NULL COMMENT '过期时间',
  `revoked_at` datetime(3) NULL DEFAULT NULL COMMENT '吊销时间， \r\n  非空表示失效',
  `client_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'miniapp' COMMENT '客户端类型：miniapp/android/ios',
  `device_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备标识， \r\n  可用于并发控制/风控',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_token_token`(`token`) USING BTREE,
  INDEX `idx_token_user`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '自定义会话Token' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_auth_token
-- ----------------------------
INSERT INTO `t_auth_token` VALUES (1993526498249691138, 1990331081332756480, '8c8b5d0f056641059d423adeed60f826_1764128729727', '2025-12-03 11:45:29.773', NULL, 'app', NULL, 'system', '2025-11-26 11:45:30', 'system', '2025-11-26 11:45:30', 0);
INSERT INTO `t_auth_token` VALUES (1993582100392980482, 1993582099294195712, '3f0377c307324d0ab31e8b140595c5e0_1764141986298', '2025-12-03 15:26:26.362', NULL, 'miniapp', NULL, 'system', '2025-11-26 15:26:26', 'system', '2025-11-26 15:26:26', 0);
INSERT INTO `t_auth_token` VALUES (1993584062454837249, 1993582099294195712, '0072a2f8bbcd47b3b0613f3f6408b748_1764142454132', '2025-12-03 15:34:14.149', NULL, 'miniapp', NULL, 'system', '2025-11-26 15:34:14', 'system', '2025-11-26 15:34:14', 0);
INSERT INTO `t_auth_token` VALUES (1993584318873612290, 1993582099294195712, '8889adc3ef7e4fb78375426e915a8adc_1764142515283', '2025-12-03 15:35:15.290', NULL, 'miniapp', NULL, 'system', '2025-11-26 15:35:15', 'system', '2025-11-26 15:35:15', 0);
INSERT INTO `t_auth_token` VALUES (1993590284478328833, 1993582099294195712, '4b8111d319f349388534035326abbcc9_1764143937593', '2025-12-03 15:58:57.606', NULL, 'miniapp', NULL, 'system', '2025-11-26 15:58:58', 'system', '2025-11-26 15:58:58', 0);
INSERT INTO `t_auth_token` VALUES (1993591523660279809, 1993582099294195712, '177afaee38ec49fe9a9c4abef03fe98e_1764144233028', '2025-12-03 16:03:53.037', NULL, 'miniapp', NULL, 'system', '2025-11-26 16:03:53', 'system', '2025-11-26 16:03:53', 0);
INSERT INTO `t_auth_token` VALUES (1993609678591918081, 1993582099294195712, '496dce2ae8e04524b6a8c98ad3f715d5_1764148561341', '2025-12-03 17:16:01.436', NULL, 'miniapp', NULL, 'system', '2025-11-26 17:16:02', 'system', '2025-11-26 17:16:02', 0);
INSERT INTO `t_auth_token` VALUES (1993869171397611521, 1993582099294195712, 'bdc653acf9c6447aa00a51ba3095a357_1764210429188', '2025-12-04 10:27:09.405', NULL, 'miniapp', NULL, 'system', '2025-11-27 10:27:09', 'system', '2025-11-27 10:27:09', 0);
INSERT INTO `t_auth_token` VALUES (1994227773924704257, 1993582099294195712, 'f552b0417044436eb9349ebbe52c5adc_1764295926877', '2025-12-05 10:12:06.911', NULL, 'miniapp', NULL, 'system', '2025-11-28 10:12:07', 'system', '2025-11-28 10:12:07', 0);

-- ----------------------------
-- Table structure for t_daily_wish_record
-- ----------------------------
DROP TABLE IF EXISTS `t_daily_wish_record`;
CREATE TABLE `t_daily_wish_record`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法  \r\n  生成',
  `wish_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '愿\r\n  望内容',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `birth_time` datetime(0) NULL DEFAULT NULL COMMENT '生  \r\n  辰',
  `user_id` bigint(20) NOT NULL COMMENT '创建用户ID',
  `wish_time` datetime(0) NOT NULL COMMENT '愿望创建 \r\n  时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '每日愿望记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_daily_wish_record
-- ----------------------------
INSERT INTO `t_daily_wish_record` VALUES (1993993245444673536, '平安喜乐', '是是是', '2025-11-27 00:00:00', 1993582099294195712, '2025-11-27 18:40:11', 'system', '2025-11-27 18:40:11', 'system', '2025-11-27 18:40:11', 0);
INSERT INTO `t_daily_wish_record` VALUES (1994225875729649664, '平安喜乐11', '李四', '2025-11-28 02:00:00', 1993582099294195712, '2025-11-28 10:04:34', 'system', '2025-11-28 10:04:34', 'system', '2025-11-28 10:27:31', 0);
INSERT INTO `t_daily_wish_record` VALUES (1994249664244879360, '平安喜乐11', '李四', '2025-11-28 02:00:00', 1993582099294195712, '2025-11-28 11:39:06', 'system', '2025-11-28 11:39:06', 'system', '2025-11-28 11:39:06', 0);

-- ----------------------------
-- Table structure for t_dim_time
-- ----------------------------
DROP TABLE IF EXISTS `t_dim_time`;
CREATE TABLE `t_dim_time`  (
  `id` bigint(20) NOT NULL COMMENT '主键，雪花ID',
  `date_value` date NOT NULL COMMENT '自然日',
  `iso_week` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ISO周，格式YYYY-Www',
  `month_value` char(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '月份，格式YYYY-MM',
  `year_value` char(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '年份，格式YYYY',
  `week_start` date NOT NULL COMMENT '周开始日',
  `week_end` date NOT NULL COMMENT '周结束日',
  `month_start` date NOT NULL COMMENT '月开始日',
  `month_end` date NOT NULL COMMENT '月结束日',
  `quarter` tinyint(4) NOT NULL COMMENT '季度1-4',
  `is_weekend` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否周末：0-否 1-是',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_dim_time_date`(`date_value`) USING BTREE,
  INDEX `idx_dim_time_week`(`iso_week`) USING BTREE,
  INDEX `idx_dim_time_month`(`month_value`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '时间维度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_dim_time
-- ----------------------------
INSERT INTO `t_dim_time` VALUES (1993598568186908672, '2025-11-26', '2025-W48', '2025-11', '2025', '2025-11-24', '2025-11-30', '2025-11-01', '2025-11-30', 4, 0, '2025-11-26 16:31:53', '2025-11-26 16:31:53');
INSERT INTO `t_dim_time` VALUES (1993598568279183360, '2025-11-24', '2025-W48', '2025-11', '2025', '2025-11-24', '2025-11-30', '2025-11-01', '2025-11-30', 4, 0, '2025-11-26 16:31:53', '2025-11-26 16:31:53');
INSERT INTO `t_dim_time` VALUES (1993598568379846656, '2025-11-01', '2025-W44', '2025-11', '2025', '2025-10-27', '2025-11-02', '2025-11-01', '2025-11-30', 4, 1, '2025-11-26 16:31:53', '2025-11-26 16:31:53');
INSERT INTO `t_dim_time` VALUES (1993598568421789696, '2025-01-01', '2025-W01', '2025-01', '2025', '2024-12-30', '2025-01-05', '2025-01-01', '2025-01-31', 1, 0, '2025-11-26 16:31:53', '2025-11-26 16:31:53');
INSERT INTO `t_dim_time` VALUES (1993869174966980608, '2025-11-27', '2025-W48', '2025-11', '2025', '2025-11-24', '2025-11-30', '2025-11-01', '2025-11-30', 4, 0, '2025-11-27 10:27:10', '2025-11-27 10:27:10');
INSERT INTO `t_dim_time` VALUES (1994225773560598528, '2025-11-28', '2025-W48', '2025-11', '2025', '2025-11-24', '2025-11-30', '2025-11-01', '2025-11-30', 4, 0, '2025-11-28 10:04:10', '2025-11-28 10:04:10');
INSERT INTO `t_dim_time` VALUES (1995694158379094016, '2025-12-02', '2025-W49', '2025-12', '2025', '2025-12-01', '2025-12-07', '2025-12-01', '2025-12-31', 4, 0, '2025-12-02 11:19:00', '2025-12-02 11:19:00');
INSERT INTO `t_dim_time` VALUES (1995694333134770176, '2025-12-01', '2025-W49', '2025-12', '2025', '2025-12-01', '2025-12-07', '2025-12-01', '2025-12-31', 4, 0, '2025-12-02 11:19:42', '2025-12-02 11:19:42');

-- ----------------------------
-- Table structure for t_donation
-- ----------------------------
DROP TABLE IF EXISTS `t_donation`;
CREATE TABLE `t_donation`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `project_id` bigint(20) NOT NULL COMMENT '捐赠项目ID',
  `merit_coins_donated` int(11) NOT NULL COMMENT '捐赠功德币数量',
  `message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '祈愿留言',
  `is_anonymous` tinyint(4) NULL DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
  `donation_time` datetime(0) NOT NULL COMMENT '捐赠时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_project_id`(`project_id`) USING BTREE,
  INDEX `idx_donation_time`(`donation_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '捐赠记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_donation
-- ----------------------------

-- ----------------------------
-- Table structure for t_donation_project
-- ----------------------------
DROP TABLE IF EXISTS `t_donation_project`;
CREATE TABLE `t_donation_project`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `project_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目名称',
  `project_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目类型：temple-庙宇建设，release-放生，education-助学，environment-环保',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '项目描述',
  `target_amount` bigint(20) NULL DEFAULT NULL COMMENT '目标金额（功德币）',
  `current_amount` bigint(20) NULL DEFAULT 0 COMMENT '当前募集金额（功德币）',
  `donor_count` int(11) NULL DEFAULT 0 COMMENT '捐赠人数',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-已结束，1-进行中，2-已完成',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目图片URL',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_project_type`(`project_type`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '捐赠项目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_donation_project
-- ----------------------------

-- ----------------------------
-- Table structure for t_exchange_record
-- ----------------------------
DROP TABLE IF EXISTS `t_exchange_record`;
CREATE TABLE `t_exchange_record`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `merit_used` bigint(20) NOT NULL COMMENT '使用功德值',
  `merit_coins_gained` int(11) NOT NULL COMMENT '获得功德币',
  `exchange_rate` int(11) NULL DEFAULT 1000 COMMENT '兑换比例：功德值/功德币',
  `exchange_time` datetime(0) NOT NULL COMMENT '兑换时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_exchange_time`(`exchange_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '功德兑换记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_exchange_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_invite_record
-- ----------------------------
DROP TABLE IF EXISTS `t_invite_record`;
CREATE TABLE `t_invite_record`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `inviter_id` bigint(20) NOT NULL COMMENT '邀请人ID',
  `invitee_id` bigint(20) NULL DEFAULT NULL COMMENT '被邀请人ID',
  `invite_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邀请码',
  `invite_time` datetime(0) NOT NULL COMMENT '邀请时间',
  `register_time` datetime(0) NULL DEFAULT NULL COMMENT '注册时间',
  `is_success` tinyint(4) NULL DEFAULT 0 COMMENT '是否成功：0-待注册，1-已注册',
  `inviter_reward` int(11) NULL DEFAULT 0 COMMENT '邀请人奖励功德值',
  `invitee_reward` int(11) NULL DEFAULT 0 COMMENT '被邀请人奖励功德值',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_invite_code`(`invite_code`) USING BTREE,
  INDEX `idx_inviter_id`(`inviter_id`) USING BTREE,
  INDEX `idx_invitee_id`(`invitee_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邀请记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_invite_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_item
-- ----------------------------
DROP TABLE IF EXISTS `t_item`;
CREATE TABLE `t_item`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '道具名称',
  `item_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '道具类型：skin-皮肤，sound-音效，background-背景，frame-头像框',
  `category` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '道具分类：classic-经典，festival-节日，premium-高级',
  `price` int(11) NOT NULL COMMENT '价格（功德币）',
  `original_price` int(11) NULL DEFAULT NULL COMMENT '原价（功德币）',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '道具描述',
  `preview_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '预览图URL',
  `resource_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源文件URL',
  `is_limited` tinyint(4) NULL DEFAULT 0 COMMENT '是否限定：0-否，1-是',
  `limit_time_start` datetime(0) NULL DEFAULT NULL COMMENT '限定开始时间',
  `limit_time_end` datetime(0) NULL DEFAULT NULL COMMENT '限定结束时间',
  `stock` int(11) NULL DEFAULT -1 COMMENT '库存数量，-1表示无限',
  `sold_count` int(11) NULL DEFAULT 0 COMMENT '已售数量',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序序号',
  `is_active` tinyint(4) NULL DEFAULT 1 COMMENT '是否上架：0-下架，1-上架',
  `usage_mode` tinyint(4) NOT NULL DEFAULT 0 COMMENT '使用模  式：0-永久，1-限时可重复，2-一次性/限次',
  `duration` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '有效时长 h-小时 d-天',
  `max_uses` int(11) NOT NULL DEFAULT -1 COMMENT '最大使用次数，-1 表示不限',
  `stackable` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否允许同一用户叠加购买',
  `cooldown_seconds` int(11) NOT NULL DEFAULT 0 COMMENT '重复使用冷却时间（秒）',
  `auto_expire_type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '过期策略：0-无，1-按duration，2-按活动时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_item_type`(`item_type`) USING BTREE,
  INDEX `idx_is_active`(`is_active`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '道具表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_item
-- ----------------------------
INSERT INTO `t_item` VALUES (1000, '单日体验', 'autoKnock', 'classic', 0, 1, '首次体验', NULL, NULL, 0, NULL, NULL, -1, 2, 1, 1, 1, '1d', -1, 0, 0, 0, 'system', '2025-11-19 11:18:52', 'system', '2025-12-02 18:18:09', 0);
INSERT INTO `t_item` VALUES (1001, '单日价格', 'autoKnock', 'classic', 1, 1, '单日使用', NULL, NULL, 0, NULL, NULL, -1, 0, 1, 1, 1, '1d', -1, 1, 0, 0, 'system', '2025-11-19 11:18:52', 'system', '2025-12-02 18:00:57', 0);
INSERT INTO `t_item` VALUES (1002, '月卡优惠', 'autoKnock', 'classic', 15, 15, '日均=0.5福币', NULL, NULL, 0, NULL, NULL, -1, 0, 2, 1, 1, '31d', -1, 1, 0, 0, 'system', '2025-11-19 11:18:52', 'system', '2025-12-02 18:18:13', 0);
INSERT INTO `t_item` VALUES (1003, '年卡超值', 'autoKnock', 'classic', 99, 99, '日均=0.3福币', NULL, NULL, 1, NULL, NULL, -1, 0, 3, 1, 1, '365d', -1, 1, 0, 0, 'system', '2025-11-19 11:18:52', 'system', '2025-12-02 18:18:16', 0);
INSERT INTO `t_item` VALUES (2000, '单日体验', 'meditation', 'classic', 0, 1, '首次体验', NULL, NULL, 0, NULL, NULL, -1, 2, 1, 1, 1, '1d', -1, 0, 0, 0, 'system', '2025-11-19 11:18:52', 'system', '2025-12-02 18:18:03', 0);
INSERT INTO `t_item` VALUES (2001, '单日价格', 'meditation', 'classic', 1, 1, '单日使用', NULL, NULL, 0, NULL, NULL, -1, 0, 1, 1, 1, '1d', -1, 1, 0, 0, 'system', '2025-11-19 11:18:52', 'system', '2025-12-02 18:00:53', 0);
INSERT INTO `t_item` VALUES (2002, '月卡优惠', 'meditation', 'classic', 15, 15, '日均=0.5福币', NULL, NULL, 0, NULL, NULL, -1, 0, 2, 1, 1, '31d', -1, 1, 0, 0, 'system', '2025-11-19 11:18:52', 'system', '2025-12-02 18:18:19', 0);
INSERT INTO `t_item` VALUES (2003, '年卡超值', 'meditation', 'classic', 99, 99, '日均=0.3福币', NULL, NULL, 1, NULL, NULL, -1, 0, 3, 1, 1, '365d', -1, 1, 0, 0, 'system', '2025-11-19 11:18:52', 'system', '2025-12-02 18:18:22', 0);

-- ----------------------------
-- Table structure for t_knock_session
-- ----------------------------
DROP TABLE IF EXISTS `t_knock_session`;
CREATE TABLE `t_knock_session`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `session_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `start_time` datetime(0) NOT NULL COMMENT '开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
  `knock_count` int(11) NULL DEFAULT 0 COMMENT '敲击次数',
  `merit_gained` int(11) NULL DEFAULT 0 COMMENT '获得功德值',
  `max_combo` int(11) NULL DEFAULT 0 COMMENT '最高连击数',
  `knock_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '敲击类型：manual-手动，auto-自动',
  `session_mode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'MANUAL' COMMENT '会话模式：MANUAL-手动，AUTO_AUTOEND-自动结束，AUTO_TIMED-定时结束',
  `limit_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '限制类型：DURATION-时长，COUNT-次数',
  `limit_value` int(11) NULL DEFAULT NULL COMMENT '限制值，对应秒数或敲击次数',
  `expected_end_time` datetime(0) NULL DEFAULT NULL COMMENT '预计结束时间',
  `merit_multiplier` decimal(8, 2) NULL DEFAULT 1.00 COMMENT '会话功德倍率',
  `prop_snapshot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '道具快照JSON',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'active' COMMENT '会话状态：active-进行中，stopped-手动停止，completed-已完成，timeout-超时结算',
  `last_heartbeat_time` datetime(0) NULL DEFAULT NULL COMMENT '最后心跳时间',
  `end_reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '结束原因：auto_end、timeout、manual_stop等',
  `coin_cost` int(11) NULL DEFAULT 0 COMMENT '预扣功德币',
  `coin_refunded` int(11) NULL DEFAULT 0 COMMENT '已退还功德币',
  `wallet_txn_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '钱包流水ID',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'RESERVED' COMMENT '支付状态：RESERVED-已预扣，SETTLED-已结算，REFUNDED-已退款',
  `device_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备信息',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_session_id`(`session_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_start_time`(`start_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '敲击会话表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_knock_session
-- ----------------------------

-- ----------------------------
-- Table structure for t_meditation_daily_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_meditation_daily_stats`;
CREATE TABLE `t_meditation_daily_stats`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `session_count` int(11) NOT NULL DEFAULT 0 COMMENT '当日冥想次数',
  `total_minutes` int(11) NOT NULL DEFAULT 0 COMMENT '当日累计时长（分钟）',
  `last_mood` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最近一次冥想的心情',
  `last_insight` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最近一次冥想的领悟',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_meditation_stat_user_date`(`user_id`, `stat_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '冥想每日统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_meditation_daily_stats
-- ----------------------------

-- ----------------------------
-- Table structure for t_meditation_session
-- ----------------------------
DROP TABLE IF EXISTS `t_meditation_session`;
CREATE TABLE `t_meditation_session`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `session_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话UUID，便于前端与日志追踪',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `planned_duration` int(11) NOT NULL COMMENT '计划冥想时长（秒）',
  `actual_duration` int(11) NULL DEFAULT NULL COMMENT '实际冥想时长（秒）',
  `start_time` datetime(0) NOT NULL COMMENT '开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
  `status` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话状态：STARTED/INTERRUPTED/COMPLETED ',
  `last_heartbeat_time` datetime(0) NOT NULL COMMENT '最后心跳时间',
  `with_knock` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否伴随敲击：0-否，1-是',
  `knock_frequency` int(11) NULL DEFAULT NULL COMMENT '敲击频率（次/分钟）',
  `mood_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '结束时选择的心情枚举',
  `insight_text` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '一句领悟的话语',
  `nickname_generated` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '系统生成的冥想昵称',
  `merit_tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '自动生成的功德标签',
  `config_snapshot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '本次冥想配置快照(JSON)',
  `save_flag` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否保存：0-丢弃，1-保存',
  `coin_cost` int(11) NULL DEFAULT 0 COMMENT '会话预扣功德币数量',
  `coin_refunded` int(11) NULL DEFAULT 0 COMMENT '已退还的功德币数量',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'RESERVED' COMMENT '支付状态：RESERVED/SETTLED/REFUNDED',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_meditation_session_id`(`session_id`) USING BTREE,
  INDEX `idx_meditation_user_time`(`user_id`, `start_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '冥想会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_meditation_session
-- ----------------------------
INSERT INTO `t_meditation_session` VALUES (1995699989287862274, 'b8b7ac841e0440d7a6123f01018a08cc', 1993582099294195712, 300, 16, '2025-12-02 11:42:10', '2025-12-02 11:42:27', 'INTERRUPTED', '2025-12-02 11:42:21', 0, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, 'SETTLED', 'system', '2025-12-02 11:42:10', 'system', '2025-12-02 11:42:10', 0);
INSERT INTO `t_meditation_session` VALUES (1995700107714035714, 'e14631a416654142b99b01a421650c2b', 1993582099294195712, 300, NULL, '2025-12-02 11:42:39', NULL, 'STARTED', '2025-12-02 11:47:39', 0, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, 'SETTLED', 'system', '2025-12-02 11:42:39', 'system', '2025-12-02 11:42:39', 0);
INSERT INTO `t_meditation_session` VALUES (1995798225394630658, '9e2b80d4fc7f4e8caf4c090d932f175b', 1993582099294195712, 300, NULL, '2025-12-02 18:12:31', NULL, 'STARTED', '2025-12-02 18:17:32', 0, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, 'SETTLED', 'system', '2025-12-02 18:12:32', 'system', '2025-12-02 18:12:32', 0);
INSERT INTO `t_meditation_session` VALUES (1995800589371473922, 'd6b9608839bd4731a64bbb8f77e3cd6c', 1993582099294195712, 300, NULL, '2025-12-02 18:21:55', NULL, 'STARTED', '2025-12-02 18:26:55', 0, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, 'SETTLED', 'system', '2025-12-02 18:21:55', 'system', '2025-12-02 18:21:55', 0);

-- ----------------------------
-- Table structure for t_meditation_subscription
-- ----------------------------
DROP TABLE IF EXISTS `t_meditation_subscription`;
CREATE TABLE `t_meditation_subscription`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `plan_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订阅类型：DAY/WEEK/MONTH',
  `start_time` datetime(0) NOT NULL COMMENT '订阅开始时间',
  `end_time` datetime(0) NOT NULL COMMENT '订阅结束时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'CURRENT' COMMENT '状态：CURRENT/EXPIRED/CANCELLED',
  `coin_cost` int(11) NOT NULL COMMENT '本次订阅消耗功德币数量',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '关联功德币流水ID(t_merit_coin_transaction)',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_meditation_sub_user`(`user_id`, `status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '冥想订阅表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_meditation_subscription
-- ----------------------------

-- ----------------------------
-- Table structure for t_meditation_user_pref
-- ----------------------------
DROP TABLE IF EXISTS `t_meditation_user_pref`;
CREATE TABLE `t_meditation_user_pref`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID（唯一）',
  `default_duration` int(11) NULL DEFAULT NULL COMMENT '默认冥想时长（秒）',
  `default_with_knock` tinyint(4) NULL DEFAULT 0 COMMENT '默认是否伴随敲击：0-否，1-是',
  `default_knock_frequency` int(11) NULL DEFAULT NULL COMMENT '默认敲击频率（次/分钟）',
  `last_update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '偏好更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '冥想用户偏好表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_meditation_user_pref
-- ----------------------------

-- ----------------------------
-- Table structure for t_merit_coin_transaction
-- ----------------------------
DROP TABLE IF EXISTS `t_merit_coin_transaction`;
CREATE TABLE `t_merit_coin_transaction`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `biz_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务类型：如MEDITATION_SUBSCRIBE',
  `biz_id` bigint(20) NULL DEFAULT NULL COMMENT '业务主键（对应业务表ID）',
  `change_amount` int(11) NOT NULL COMMENT '变动金额（正增负减）',
  `balance_after` int(11) NOT NULL COMMENT '变动后的功德币余额',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注说明',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除：0-正常，1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coin_tx_user`(`user_id`) USING BTREE,
  INDEX `idx_coin_tx_biz`(`biz_type`, `biz_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '功德币流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_merit_coin_transaction
-- ----------------------------
INSERT INTO `t_merit_coin_transaction` VALUES (1990978296732483586, 1990331081332756480, 'RECHARGE_PURCHASE', 1990969462173601793, 10, 10, '充值订单WX1990969462173601792到账功德币', 'system', '2025-11-19 10:59:51', 'system', '2025-11-19 10:59:51', 0);
INSERT INTO `t_merit_coin_transaction` VALUES (1990978296891867137, 1990331081332756480, 'RECHARGE_BONUS', 1990969462173601793, 5, 15, '充值订单WX1990969462173601792赠送功德币', 'system', '2025-11-19 10:59:51', 'system', '2025-11-19 10:59:51', 0);
INSERT INTO `t_merit_coin_transaction` VALUES (1990980410644922370, 1990331081332756480, 'SCRIPTURE_SUBSCRIBE', 1990980410380681218, -2, 13, '订阅典籍-心经', 'system', '2025-11-19 11:08:15', 'system', '2025-11-19 11:08:15', 0);
INSERT INTO `t_merit_coin_transaction` VALUES (1990982341660528641, 1990331081332756480, 'SCRIPTURE_PERMANENT', 1990982341446619138, -5, 8, '买断典籍-六字大明咒', 'system', '2025-11-19 11:15:56', 'system', '2025-11-19 11:15:56', 0);
INSERT INTO `t_merit_coin_transaction` VALUES (1991035943733170177, 1990331081332756480, 'ITEM_PURCHASE', 1991035938884685824, -1, 7, '购买道具-自动敲击', 'system', '2025-11-19 14:48:55', 'system', '2025-11-19 14:48:55', 0);
INSERT INTO `t_merit_coin_transaction` VALUES (1993937954938134530, 1993582099294195712, 'RECHARGE_PURCHASE', 1993937906212933633, 50, 61, '充值订单WX1993937906212933632到账功德币', 'system', '2025-11-27 15:00:29', 'system', '2025-11-27 15:00:29', 0);
INSERT INTO `t_merit_coin_transaction` VALUES (1993937955076546561, 1993582099294195712, 'RECHARGE_BONUS', 1993937906212933633, 20, 81, '充值订单WX1993937906212933632赠送功德币', 'system', '2025-11-27 15:00:29', 'system', '2025-11-27 15:00:29', 0);
INSERT INTO `t_merit_coin_transaction` VALUES (1993940327454580738, 1993582099294195712, 'ITEM_PURCHASE', 1993940326796103680, -1, 80, '购买道具-自动敲击', 'system', '2025-11-27 15:09:54', 'system', '2025-11-27 15:09:54', 0);
INSERT INTO `t_merit_coin_transaction` VALUES (1995762105378271234, 1993582099294195712, 'ITEM_PURCHASE', 1995762104115793920, 0, 80, '购买道具-自动敲击', 'system', '2025-12-02 15:49:00', 'system', '2025-12-02 15:49:00', 0);
INSERT INTO `t_merit_coin_transaction` VALUES (1995798090564534273, 1993582099294195712, 'ITEM_PURCHASE', 1995798085917347840, 0, 80, '购买道具-单日体验', 'system', '2025-12-02 18:12:00', 'system', '2025-12-02 18:12:00', 0);
INSERT INTO `t_merit_coin_transaction` VALUES (1995798190560935937, 1993582099294195712, 'ITEM_PURCHASE', 1995798190523289600, 0, 80, '购买道具-单日体验', 'system', '2025-12-02 18:12:23', 'system', '2025-12-02 18:12:23', 0);

-- ----------------------------
-- Table structure for t_merit_level
-- ----------------------------
DROP TABLE IF EXISTS `t_merit_level`;
CREATE TABLE `t_merit_level`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `level` int(11) NOT NULL COMMENT '等级',
  `level_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '等级名称',
  `min_merit` bigint(20) NOT NULL COMMENT '最低功德值',
  `max_merit` bigint(20) NULL DEFAULT NULL COMMENT '最高功德值，NULL表示无上限',
  `level_benefits` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '等级特权描述',
  `icon_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '等级图标URL',
  `bonus_rate` decimal(5, 2) NULL DEFAULT 1.00 COMMENT '功德加成倍率',
  `daily_exchange_limit` int(11) NULL DEFAULT 100 COMMENT '每日兑换限额（功德币）',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_level`(`level`) USING BTREE,
  INDEX `idx_min_merit`(`min_merit`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '功德等级配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_merit_level
-- ----------------------------
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

-- ----------------------------
-- Table structure for t_merit_record
-- ----------------------------
DROP TABLE IF EXISTS `t_merit_record`;
CREATE TABLE `t_merit_record`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `merit_gained` int(11) NOT NULL COMMENT '获得功德值',
  `base_merit` int(11) NULL DEFAULT 0 COMMENT '基础功德值（未计算倍率前）',
  `knock_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '敲击类型：manual-手动，auto-自动',
  `knock_mode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '敲击模式：MANUAL、AUTO_AUTOEND、AUTO_TIMED',
  `source` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '来源：knock-敲击，task-任务，login-登录，activity-活动，share-分享',
  `session_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会话ID，用于统计连击',
  `combo_count` int(11) NULL DEFAULT 0 COMMENT '连击数',
  `bonus_rate` decimal(8, 2) NULL DEFAULT 1.00 COMMENT '总加成倍率',
  `prop_snapshot` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '道具倍率快照JSON',
  `stat_date` date NULL DEFAULT NULL COMMENT '所属自然日，凌晨清零后重置',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_source`(`source`) USING BTREE,
  INDEX `idx_merit_record_session`(`session_id`, `create_time`) USING BTREE,
  INDEX `idx_stat_date`(`stat_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '功德记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_merit_record
-- ----------------------------
INSERT INTO `t_merit_record` VALUES (1994319559808651264, 1993582099294195712, 1, 1, 'manual', 'MANUAL', 'knock', '1764317810069-l6mup', 0, 1.00, NULL, '2025-11-28', '手动敲击获得功德', 'system', '2025-11-28 16:00:00', 'system', '2025-11-28 16:16:50', 0);
INSERT INTO `t_merit_record` VALUES (1994319579492519936, 1993582099294195712, 4, 4, 'manual', 'MANUAL', 'knock', '1764317815063-i70l8', 0, 1.00, NULL, '2025-11-28', '手动敲击获得功德', 'system', '2025-11-28 16:00:00', 'system', '2025-11-28 16:16:55', 0);
INSERT INTO `t_merit_record` VALUES (1994319600535343104, 1993582099294195712, 5, 5, 'manual', 'MANUAL', 'knock', '1764317820065-0krlh', 0, 1.00, NULL, '2025-11-28', '手动敲击获得功德', 'system', '2025-11-28 16:00:00', 'system', '2025-11-28 16:17:00', 0);
INSERT INTO `t_merit_record` VALUES (1994319621427171328, 1993582099294195712, 6, 6, 'manual', 'MANUAL', 'knock', '1764317825059-jerib', 0, 1.00, NULL, '2025-11-28', '手动敲击获得功德', 'system', '2025-11-28 16:00:00', 'system', '2025-11-28 16:17:05', 0);
INSERT INTO `t_merit_record` VALUES (1994319831150759936, 1993582099294195712, 1, 1, 'manual', 'MANUAL', 'knock', '1764317875059-2u325', 0, 1.00, NULL, '2025-11-28', '手动敲击获得功德', 'system', '2025-11-28 16:00:00', 'system', '2025-11-28 16:17:55', 0);
INSERT INTO `t_merit_record` VALUES (1994321876977061888, 1993582099294195712, 3, 3, 'manual', 'MANUAL', 'knock', '1764318362823-cjn5g', 0, 1.00, NULL, '2025-11-28', '手动敲击获得功德', 'system', '2025-11-28 16:00:00', 'system', '2025-11-28 16:26:03', 0);
INSERT INTO `t_merit_record` VALUES (1994321897931804672, 1993582099294195712, 12, 12, 'manual', 'MANUAL', 'knock', '1764318367821-ok2fx', 0, 1.00, NULL, '2025-11-28', '手动敲击获得功德', 'system', '2025-11-28 16:00:00', 'system', '2025-11-28 16:26:08', 0);
INSERT INTO `t_merit_record` VALUES (1995694155069788160, 1993582099294195712, 1, 1, 'manual', 'MANUAL', 'knock', '1764645531758-p95tg', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 11:00:00', 'system', '2025-12-02 11:18:59', 0);
INSERT INTO `t_merit_record` VALUES (1995694174254534656, 1993582099294195712, 16, 16, 'manual', 'MANUAL', 'knock', '1764645541767-izl7m', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 11:00:00', 'system', '2025-12-02 11:19:04', 0);
INSERT INTO `t_merit_record` VALUES (1995694185986002944, 1993582099294195712, 3, 3, 'manual', 'MANUAL', 'knock', '1764645546757-gwm6k', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 11:00:00', 'system', '2025-12-02 11:19:07', 0);
INSERT INTO `t_merit_record` VALUES (1995784315383975936, 1993582099294195712, 1, 1, 'manual', 'MANUAL', 'knock', '1764667028660-6moh0', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 17:00:00', 'system', '2025-12-02 17:17:15', 0);
INSERT INTO `t_merit_record` VALUES (1995793859858272256, 1993582099294195712, 1, 1, 'manual', 'MANUAL', 'knock', '1764669310107-y8r8c', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 17:00:00', 'system', '2025-12-02 17:55:11', 0);
INSERT INTO `t_merit_record` VALUES (1995793878581645312, 1993582099294195712, 5, 5, 'manual', 'MANUAL', 'knock', '1764669315113-upbek', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 17:00:00', 'system', '2025-12-02 17:55:15', 0);
INSERT INTO `t_merit_record` VALUES (1995793899414753280, 1993582099294195712, 4, 4, 'manual', 'MANUAL', 'knock', '1764669320118-7yxlr', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 17:00:00', 'system', '2025-12-02 17:55:20', 0);
INSERT INTO `t_merit_record` VALUES (1995793919916511232, 1993582099294195712, 1, 1, 'manual', 'MANUAL', 'knock', '1764669325118-8q7fh', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 17:00:00', 'system', '2025-12-02 17:55:25', 0);
INSERT INTO `t_merit_record` VALUES (1995796059493896192, 1993582099294195712, 2, 2, 'manual', 'MANUAL', 'knock', '1764669835108-wejos', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 18:00:00', 'system', '2025-12-02 18:03:55', 0);
INSERT INTO `t_merit_record` VALUES (1995796079802716160, 1993582099294195712, 6, 6, 'manual', 'MANUAL', 'knock', '1764669840106-qoxs1', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 18:00:00', 'system', '2025-12-02 18:04:00', 0);
INSERT INTO `t_merit_record` VALUES (1995796100648407040, 1993582099294195712, 4, 4, 'manual', 'MANUAL', 'knock', '1764669845106-6k076', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 18:00:00', 'system', '2025-12-02 18:04:05', 0);
INSERT INTO `t_merit_record` VALUES (1995796226511081472, 1993582099294195712, 2, 2, 'manual', 'MANUAL', 'knock', '1764669875106-zxezg', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 18:00:00', 'system', '2025-12-02 18:04:35', 0);
INSERT INTO `t_merit_record` VALUES (1995798111477436416, 1993582099294195712, 6, 6, 'manual', 'MANUAL', 'knock', '1764670324490-d77js', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 18:00:00', 'system', '2025-12-02 18:12:05', 0);
INSERT INTO `t_merit_record` VALUES (1995798132419596288, 1993582099294195712, 2, 2, 'manual', 'MANUAL', 'knock', '1764670329475-l4xlu', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 18:00:00', 'system', '2025-12-02 18:12:10', 0);
INSERT INTO `t_merit_record` VALUES (1995798153370144768, 1993582099294195712, 3, 3, 'manual', 'MANUAL', 'knock', '1764670334475-ho520', 0, 1.00, NULL, '2025-12-02', '手动敲击获得功德', 'system', '2025-12-02 18:00:00', 'system', '2025-12-02 18:12:15', 0);

-- ----------------------------
-- Table structure for t_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `t_operation_log`;
CREATE TABLE `t_operation_log`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型',
  `operation_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作描述',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求方法：GET，POST等',
  `request_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求URL',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求参数',
  `response_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '响应结果',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户代理',
  `operation_time` datetime(0) NOT NULL COMMENT '操作时间',
  `execution_time` int(11) NULL DEFAULT NULL COMMENT '执行时长（毫秒）',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-失败，1-成功',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误信息',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_operation_time`(`operation_time`) USING BTREE,
  INDEX `idx_operation_type`(`operation_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for t_purchase_record
-- ----------------------------
DROP TABLE IF EXISTS `t_purchase_record`;
CREATE TABLE `t_purchase_record`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `item_id` bigint(20) NOT NULL COMMENT '道具ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `price` int(11) NOT NULL COMMENT '购买价格（功德币）',
  `quantity` int(11) NULL DEFAULT 1 COMMENT '购买数量',
  `total_amount` int(11) NOT NULL COMMENT '总金额（功德币）',
  `purchase_time` datetime(0) NOT NULL COMMENT '购买时间',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '订单状态：0-失败，1-成功，2-退款',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_purchase_time`(`purchase_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '购买记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_purchase_record
-- ----------------------------
INSERT INTO `t_purchase_record` VALUES (1991035941355130880, 1990331081332756480, 1001, 'SHOP17635349347353D49C1A6', 1, 1, 1, '2025-11-19 14:48:55', 1, 'system', '2025-11-19 14:48:55', 'system', '2025-11-19 14:48:55', 0);
INSERT INTO `t_purchase_record` VALUES (1993940327291031552, 1993582099294195712, 1001, 'SHOP17642273943032CDAF54D', 1, 1, 1, '2025-11-27 15:09:54', 1, 'system', '2025-11-27 15:09:54', 'system', '2025-11-27 15:09:54', 0);
INSERT INTO `t_purchase_record` VALUES (1995762104753328128, 1993582099294195712, 1000, 'SHOP1764661739897DFE0E4C5', 0, 1, 0, '2025-12-02 15:49:00', 1, 'system', '2025-12-02 15:49:00', 'system', '2025-12-02 15:49:00', 0);
INSERT INTO `t_purchase_record` VALUES (1995798088073220096, 1993582099294195712, 1000, 'SHOP17646703189896EEA80E0', 0, 1, 0, '2025-12-02 18:11:59', 1, 'system', '2025-12-02 18:11:59', 'system', '2025-12-02 18:11:59', 0);
INSERT INTO `t_purchase_record` VALUES (1995798190556844032, 1993582099294195712, 2000, 'SHOP17646703434231ACE7BDF', 0, 1, 0, '2025-12-02 18:12:23', 1, 'system', '2025-12-02 18:12:23', 'system', '2025-12-02 18:12:23', 0);

-- ----------------------------
-- Table structure for t_ranking
-- ----------------------------
DROP TABLE IF EXISTS `t_ranking`;
CREATE TABLE `t_ranking`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `rank_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '榜单类型：daily-日榜，weekly-周榜，monthly-月榜，total-总榜',
  `merit_value` bigint(20) NOT NULL COMMENT '功德值',
  `ranking_position` int(11) NOT NULL COMMENT '排名',
  `snapshot_date` date NOT NULL COMMENT '快照日期',
  `period` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '统计周期：如2025-01表示月榜，2025-W01表示周榜',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_rank_type_date`(`rank_type`, `snapshot_date`) USING BTREE,
  INDEX `idx_ranking_position`(`ranking_position`) USING BTREE,
  INDEX `idx_ranking_composite`(`rank_type`, `snapshot_date`, `ranking_position`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '排行榜快照表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_ranking
-- ----------------------------
INSERT INTO `t_ranking` VALUES (1, 1993582099294195712, 'total', 20, 1, '2025-11-26', NULL, 'system', '2025-11-26 16:28:24', 'system', '2025-11-26 16:39:52', 0);

-- ----------------------------
-- Table structure for t_ranking_reward
-- ----------------------------
DROP TABLE IF EXISTS `t_ranking_reward`;
CREATE TABLE `t_ranking_reward`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `rank_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '榜单类型：daily-日榜，weekly-周榜，monthly-月榜',
  `ranking_position` int(11) NOT NULL COMMENT '排名',
  `reward_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '奖励类型：title-称号，skin-皮肤，frame-头像框，merit_coin-功德币',
  `reward_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '奖励内容：道具ID或功德币数量',
  `reward_time` datetime(0) NOT NULL COMMENT '奖励发放时间',
  `is_claimed` tinyint(4) NULL DEFAULT 0 COMMENT '是否已领取：0-未领取，1-已领取',
  `claim_time` datetime(0) NULL DEFAULT NULL COMMENT '领取时间',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_is_claimed`(`is_claimed`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '排行榜奖励记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_ranking_reward
-- ----------------------------

-- ----------------------------
-- Table structure for t_recharge_order
-- ----------------------------
DROP TABLE IF EXISTS `t_recharge_order`;
CREATE TABLE `t_recharge_order`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '充值金额（元）',
  `merit_coins` int(11) NOT NULL COMMENT '获得功德币',
  `bonus_coins` int(11) NULL DEFAULT 0 COMMENT '赠送功德币',
  `payment_method` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付方式：alipay-支付宝，wechat-微信，bank-银行卡',
  `payment_status` tinyint(4) NULL DEFAULT 0 COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败，3-已退款',
  `payment_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方交易号',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_payment_status`(`payment_status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_recharge_order
-- ----------------------------
INSERT INTO `t_recharge_order` VALUES (1990969462173601793, 'WX1990969462173601792', 1990331081332756480, 10.00, 10, 5, 'wechat', 1, '2025-11-27 14:54:21', 'MOCK_WX1990969462173601792', '功德充值', 'user845', '2025-11-19 10:24:45', 'system', '2025-11-27 14:54:21', 0);
INSERT INTO `t_recharge_order` VALUES (1993937906212933633, 'WX1993937906212933632', 1993582099294195712, 50.00, 50, 20, 'wechat', 1, '2025-11-27 15:00:28', 'MOCK_WX1993937906212933632', '充值', '1791350', '2025-11-27 15:00:17', 'system', '2025-11-27 15:00:28', 0);

-- ----------------------------
-- Table structure for t_scripture
-- ----------------------------
DROP TABLE IF EXISTS `t_scripture`;
CREATE TABLE `t_scripture`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `scripture_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '典籍名称',
  `scripture_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '典籍类型：sutra-佛经经典，mantra-咒语',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作者/译者',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '典籍描述',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '典籍内容',
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图片URL',
  `audio_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '音频URL',
  `is_hot` tinyint(4) NULL DEFAULT 0 COMMENT '是否热门：0-否，1-是',
  `price` int(11) NOT NULL DEFAULT 1 COMMENT '购买价格（福币）',
  `permanent_price` int(11) NULL DEFAULT NULL COMMENT '买断价格（福币），NULL表示不支持买断',
  `price_unit` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '本' COMMENT '计价单位：本、部、卷、则',
  `duration_months` int(11) NULL DEFAULT 1 COMMENT '购买时长（月）',
  `read_count` bigint(20) NULL DEFAULT 0 COMMENT '阅读次数',
  `purchase_count` int(11) NULL DEFAULT 0 COMMENT '购买次数',
  `difficulty_level` tinyint(4) NULL DEFAULT 1 COMMENT '难度等级：1-初级，2-中级，3-高级',
  `word_count` int(11) NULL DEFAULT 0 COMMENT '字数',
  `category_tags` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类标签，用逗号分隔',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序序号',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_scripture_type`(`scripture_type`) USING BTREE,
  INDEX `idx_is_hot`(`is_hot`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_price`(`price`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE,
  INDEX `idx_permanent_price`(`permanent_price`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '典籍表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_scripture
-- ----------------------------
INSERT INTO `t_scripture` VALUES (1001, '心经', 'sutra', '玄奘', '《般若波罗蜜多心经》是大乘佛教经典之一，全文共260字，是六百卷《大般若经》的精华。', '观自在菩萨，行深般若波罗蜜多时，照见五蕴皆空，度一切苦厄...', NULL, NULL, 1, 2, 24, '部', 1, 0, 1, 1, 260, '般若,心经,大乘', 1, 1, 'system', '2025-09-27 12:09:44', 'system', '2025-11-19 11:08:15', 0);
INSERT INTO `t_scripture` VALUES (1002, '金刚经', 'sutra', '鸠摩罗什', '《金刚般若波罗蜜经》，简称《金刚经》，是大乘佛教般若部重要经典之一。', '如是我闻，一时，佛在舍卫国祇树给孤独园，与大比丘众千二百五十人俱...', NULL, NULL, 1, 5, 60, '部', 1, 0, 0, 2, 5000, '般若,金刚,大乘', 1, 2, 'system', '2025-09-27 12:09:44', 'system', '2025-09-27 12:09:49', 0);
INSERT INTO `t_scripture` VALUES (1003, '大悲咒', 'mantra', '伽梵达摩', '《千手千眼观世音菩萨广大圆满无碍大悲心陀罗尼》，简称《大悲咒》。', '南无、喝啰怛那、哆啰夜耶，南无、阿唎耶，婆卢羯帝、烁钵啰耶...', NULL, NULL, 1, 3, 36, '则', 1, 0, 0, 1, 415, '观音,大悲咒,密咒', 1, 3, 'system', '2025-09-27 12:09:44', 'system', '2025-09-27 12:09:49', 0);
INSERT INTO `t_scripture` VALUES (1004, '楞严经', 'sutra', '般剌蜜帝', '《大佛顶如来密因修证了义诸菩萨万行首楞严经》，简称《楞严经》，为大乘佛教经典。', '如是我闻：一时，佛在室罗筏城，祇桓精舍...', NULL, NULL, 0, 8, 96, '部', 1, 0, 0, 3, 62000, '楞严,如来藏,大乘', 1, 4, 'system', '2025-09-27 12:09:44', 'system', '2025-09-27 12:09:49', 0);
INSERT INTO `t_scripture` VALUES (1005, '六字大明咒', 'mantra', '', '观世音菩萨心咒，藏传佛教中最常见的咒语之一。', '嗡(ōng)嘛(mā)呢(nī)叭(bēi)咪(mēi)吽(hōng)', NULL, NULL, 1, 1, 5, '则', 1, 0, 1, 1, 6, '观音,六字真言,藏传', 1, 5, 'system', '2025-09-27 12:09:44', 'system', '2025-11-19 11:15:55', 0);

-- ----------------------------
-- Table structure for t_share_record
-- ----------------------------
DROP TABLE IF EXISTS `t_share_record`;
CREATE TABLE `t_share_record`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `share_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分享类型：achievement-成就，ranking-排名，invite-邀请',
  `share_platform` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分享平台：wechat-微信，qq-QQ，weibo-微博，link-链接',
  `share_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分享内容',
  `share_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分享链接',
  `share_time` datetime(0) NOT NULL COMMENT '分享时间',
  `reward_merit` int(11) NULL DEFAULT 0 COMMENT '获得功德奖励',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_share_time`(`share_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分享记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_share_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_system_message
-- ----------------------------
DROP TABLE IF EXISTS `t_system_message`;
CREATE TABLE `t_system_message`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
  `message_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息类型：system-系统通知，activity-活动通知，reward-奖励通知',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标类型：all-全体用户，user-指定用户，level-指定等级',
  `target_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目标值：用户ID列表或等级范围',
  `link_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `publish_time` datetime(0) NULL DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态：0-草稿，1-已发布，2-已过期',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_message_type`(`message_type`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_publish_time`(`publish_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_system_message
-- ----------------------------

-- ----------------------------
-- Table structure for t_task
-- ----------------------------
DROP TABLE IF EXISTS `t_task`;
CREATE TABLE `t_task`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `task_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `task_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务类型：daily-每日任务，weekly-每周任务，achievement-成就任务，activity-活动任务',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务描述',
  `icon_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务图标URL',
  `target_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '目标类型：knock-敲击，login-登录，share-分享，donate-捐赠',
  `target_value` int(11) NOT NULL COMMENT '目标值',
  `reward_merit` int(11) NULL DEFAULT 0 COMMENT '奖励功德值',
  `reward_coins` int(11) NULL DEFAULT 0 COMMENT '奖励功德币',
  `reward_item_id` bigint(20) NULL DEFAULT NULL COMMENT '奖励道具ID',
  `refresh_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '刷新类型：daily-每日刷新，weekly-每周刷新',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序序号',
  `is_active` tinyint(4) NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_type`(`task_type`) USING BTREE,
  INDEX `idx_is_active`(`is_active`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务定义表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_task
-- ----------------------------

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint(20) NOT NULL COMMENT '用户ID，雪花算法生成',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名，唯一',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码，加密存储',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像URL',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `gender` tinyint(4) NULL DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `register_time` datetime(0) NOT NULL COMMENT '注册时间',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '账号状态：0-禁用，1-正常，2-冻结',
  `vip_level` tinyint(4) NULL DEFAULT 0 COMMENT 'VIP等级：0-普通用户，1-月卡，2-年卡，3-永久',
  `vip_expire_time` datetime(0) NULL DEFAULT NULL COMMENT 'VIP到期时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE,
  INDEX `idx_nickname`(`nickname`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_register_time`(`register_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户基础信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1971822665190739968, 'user711', '8b8c039f78b8e5601b3c28ae5e85a370', '重新测试昵称', NULL, NULL, '13888888888', 0, NULL, '2025-09-27 14:22:13', NULL, 1, 0, NULL, 'user711', '2025-09-27 14:22:13', 'system', '2025-09-27 15:15:44', 0);
INSERT INTO `t_user` VALUES (1990331081332756480, 'user845', NULL, 'user845', NULL, NULL, '17688701711', 0, NULL, '2025-11-17 16:08:03', NULL, 1, 0, NULL, 'user845', '2025-11-17 16:08:03', 'user845', '2025-11-17 16:08:03', 0);
INSERT INTO `t_user` VALUES (1993582099294195712, '1791350', NULL, '微信用户1791', NULL, NULL, '17688701791', 0, NULL, '2025-11-26 15:26:26', NULL, 1, 2, NULL, '1791350', '2025-11-26 15:26:26', '1791350', '2025-11-26 16:42:03', 0);

-- ----------------------------
-- Table structure for t_user_achievement
-- ----------------------------
DROP TABLE IF EXISTS `t_user_achievement`;
CREATE TABLE `t_user_achievement`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `achievement_id` bigint(20) NOT NULL COMMENT '成就ID',
  `progress` bigint(20) NULL DEFAULT 0 COMMENT '当前进度',
  `is_completed` tinyint(4) NULL DEFAULT 0 COMMENT '是否完成：0-未完成，1-已完成',
  `complete_time` datetime(0) NULL DEFAULT NULL COMMENT '完成时间',
  `is_claimed` tinyint(4) NULL DEFAULT 0 COMMENT '是否已领取奖励：0-未领取，1-已领取',
  `claim_time` datetime(0) NULL DEFAULT NULL COMMENT '领取时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_achievement`(`user_id`, `achievement_id`) USING BTREE,
  INDEX `idx_is_completed`(`is_completed`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户成就表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user_achievement
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_activity
-- ----------------------------
DROP TABLE IF EXISTS `t_user_activity`;
CREATE TABLE `t_user_activity`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `join_time` datetime(0) NOT NULL COMMENT '参与时间',
  `merit_gained` bigint(20) NULL DEFAULT 0 COMMENT '活动中获得的功德值',
  `coins_gained` int(11) NULL DEFAULT 0 COMMENT '活动中获得的功德币',
  `extra_data` json NULL COMMENT '额外数据，JSON格式',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id`) USING BTREE,
  INDEX `idx_join_time`(`join_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户活动参与记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user_activity
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_item
-- ----------------------------
DROP TABLE IF EXISTS `t_user_item`;
CREATE TABLE `t_user_item`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `item_id` bigint(20) NOT NULL COMMENT '道具ID',
  `purchase_time` datetime(0) NOT NULL COMMENT '购买时间',
  `purchase_price` int(11) NOT NULL COMMENT '购买价格（功德币）',
  `remaining_uses` int(11) NULL DEFAULT NULL COMMENT '剩余可用次数，null/-1 表示不限',
  `usage_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态：-未激活/闲置，1-使用中，2-已用完，3-已过期',
  `last_used_time` datetime(0) NULL DEFAULT NULL COMMENT '最近一次使用AFTER',
  `stack_count` int(11) NOT NULL DEFAULT 1 COMMENT '拥有数量/叠加层数',
  `source_type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '来源：1-商城购买，2-活动赠送，3-任务奖励等',
  `metadata` json NULL COMMENT '扩展信息（如活动ID、特性）',
  `is_equipped` tinyint(4) NULL DEFAULT 0 COMMENT '是否装备：0-否，1-是',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间，NULL表示永久',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_item_id`(`item_id`) USING BTREE,
  INDEX `idx_is_equipped`(`is_equipped`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户道具表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user_item
-- ----------------------------
INSERT INTO `t_user_item` VALUES (1995798085917347840, 1993582099294195712, 1000, '2025-12-02 18:11:58', 0, NULL, 0, NULL, 1, 1, NULL, 0, '2025-12-03 18:11:58', 'system', '2025-12-02 18:11:59', 'system', '2025-12-02 18:16:54', 0);
INSERT INTO `t_user_item` VALUES (1995798190523289600, 1993582099294195712, 2000, '2025-12-02 18:12:23', 0, NULL, 0, NULL, 1, 1, NULL, 0, '2025-12-03 18:12:23', 'system', '2025-12-02 18:12:23', 'system', '2025-12-02 18:16:54', 0);

-- ----------------------------
-- Table structure for t_user_message
-- ----------------------------
DROP TABLE IF EXISTS `t_user_message`;
CREATE TABLE `t_user_message`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `message_id` bigint(20) NULL DEFAULT NULL COMMENT '系统消息ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
  `message_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息类型：system-系统，reward-奖励，friend-好友，achievement-成就',
  `is_read` tinyint(4) NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `read_time` datetime(0) NULL DEFAULT NULL COMMENT '阅读时间',
  `link_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_is_read`(`is_read`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user_message
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_period_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_user_period_stats`;
CREATE TABLE `t_user_period_stats`  (
  `id` bigint(20) NOT NULL COMMENT '主键，雪花ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `time_id` bigint(20) NOT NULL COMMENT '关联t_dim_time.id',
  `period_type` enum('DAY','WEEK','MONTH','YEAR') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '统计周期类型',
  `knock_count` bigint(20) NOT NULL DEFAULT 0 COMMENT '周期敲击次数',
  `merit_gained` bigint(20) NOT NULL DEFAULT 0 COMMENT '周期功德增量',
  `max_combo` int(11) NOT NULL DEFAULT 0 COMMENT '周期最大连击数',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `is_deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-否 1-是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_period_time`(`user_id`, `period_type`, `time_id`) USING BTREE,
  INDEX `idx_period_user_type`(`user_id`, `period_type`) USING BTREE,
  INDEX `fk_user_period_time`(`time_id`) USING BTREE,
  CONSTRAINT `fk_user_period_time` FOREIGN KEY (`time_id`) REFERENCES `t_dim_time` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户周期敲击功德统计' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_period_stats
-- ----------------------------
INSERT INTO `t_user_period_stats` VALUES (1994319562094546944, 1993582099294195712, 1994225773560598528, 'DAY', 32, 32, 0, 'system', '2025-11-28 16:16:50', 'system', '2025-11-28 16:26:07', 0);
INSERT INTO `t_user_period_stats` VALUES (1995694159582859264, 1993582099294195712, 1995694158379094016, 'DAY', 57, 57, 0, 'system', '2025-12-02 11:19:00', 'system', '2025-12-02 18:12:14', 0);

-- ----------------------------
-- Table structure for t_user_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_user_relation`;
CREATE TABLE `t_user_relation`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_user_id` bigint(20) NOT NULL COMMENT '目标用户ID',
  `relation_type` tinyint(4) NOT NULL COMMENT '关系类型：1-关注，2-好友，3-拉黑',
  `relation_time` datetime(0) NOT NULL COMMENT '建立关系时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_relation`(`user_id`, `target_user_id`, `relation_type`) USING BTREE,
  INDEX `idx_target_user_id`(`target_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user_relation
-- ----------------------------

-- ----------------------------
-- Table structure for t_user_scripture_purchase
-- ----------------------------
DROP TABLE IF EXISTS `t_user_scripture_purchase`;
CREATE TABLE `t_user_scripture_purchase`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `scripture_id` bigint(20) NOT NULL COMMENT '典籍ID',
  `merit_coins_paid` int(11) NOT NULL COMMENT '支付福币数量',
  `purchase_months` int(11) NULL DEFAULT 1 COMMENT '购买月数',
  `purchase_time` datetime(0) NOT NULL COMMENT '购买时间',
  `expire_time` datetime(0) NOT NULL COMMENT '过期时间',
  `is_expired` tinyint(4) NULL DEFAULT 0 COMMENT '是否过期：0-未过期，1-已过期',
  `read_count` int(11) NULL DEFAULT 0 COMMENT '阅读次数',
  `last_read_time` datetime(0) NULL DEFAULT NULL COMMENT '最后阅读时间',
  `reading_progress` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '阅读进度百分比',
  `last_reading_position` int(11) NULL DEFAULT 0 COMMENT '最后阅读位置（字符位置）',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_scripture`(`user_id`, `scripture_id`, `is_deleted`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_scripture_id`(`scripture_id`) USING BTREE,
  INDEX `idx_expire_time`(`expire_time`) USING BTREE,
  INDEX `idx_is_expired`(`is_expired`) USING BTREE,
  INDEX `idx_purchase_time`(`purchase_time`) USING BTREE,
  INDEX `idx_last_reading_position`(`last_reading_position`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户典籍购买记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user_scripture_purchase
-- ----------------------------
INSERT INTO `t_user_scripture_purchase` VALUES (1990980410380681218, 1990331081332756480, 1001, 2, 1, '2025-11-19 11:08:15', '2025-12-19 11:08:15', 0, 0, NULL, 0.00, 0, 'system', '2025-11-19 11:08:15', 'system', '2025-11-19 11:08:15', 0);
INSERT INTO `t_user_scripture_purchase` VALUES (1990982341446619138, 1990331081332756480, 1005, 5, 0, '2025-11-19 11:15:55', '2099-12-31 00:00:00', 0, 0, NULL, 0.00, 0, 'system', '2025-11-19 11:15:56', 'system', '2025-11-19 11:15:56', 0);

-- ----------------------------
-- Table structure for t_user_setting
-- ----------------------------
DROP TABLE IF EXISTS `t_user_setting`;
CREATE TABLE `t_user_setting`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `sound_enabled` tinyint(4) NULL DEFAULT 1 COMMENT '音效开关：0-关闭，1-开启',
  `sound_volume` int(11) NULL DEFAULT 80 COMMENT '音量大小：0-100',
  `vibration_enabled` tinyint(4) NULL DEFAULT 1 COMMENT '震动反馈：0-关闭，1-开启',
  `daily_reminder` tinyint(4) NULL DEFAULT 1 COMMENT '每日提醒：0-关闭，1-开启',
  `reminder_time` time(0) NULL DEFAULT '09:00:00' COMMENT '提醒时间',
  `privacy_mode` tinyint(4) NULL DEFAULT 0 COMMENT '隐私模式：0-公开，1-仅好友可见，2-完全隐私',
  `auto_knock_speed` int(11) NULL DEFAULT 1 COMMENT '自动敲击速度：1-慢速，2-中速，3-快速',
  `theme_id` bigint(20) NULL DEFAULT NULL COMMENT '当前主题ID',
  `skin_id` bigint(20) NULL DEFAULT NULL COMMENT '当前皮肤ID',
  `sound_id` bigint(20) NULL DEFAULT NULL COMMENT '当前音效ID',
  `bullet_screen` tinyint(4) NULL DEFAULT 1 COMMENT '弹幕设置：1-愿力 2-愿望 3-经书',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户设置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user_setting
-- ----------------------------
INSERT INTO `t_user_setting` VALUES (1990331082314223616, 1990331081332756480, 1, 80, 1, 1, '09:00:00', 0, 2, NULL, NULL, NULL, 3, 'system', '2025-11-17 16:08:03', 'system', '2025-11-17 16:14:13', 0);
INSERT INTO `t_user_setting` VALUES (1993582099730403328, 1993582099294195712, 1, 80, 1, 1, '09:00:00', 0, 2, NULL, NULL, NULL, 2, 'system', '2025-11-26 15:26:26', 'system', '2025-11-28 11:20:37', 0);

-- ----------------------------
-- Table structure for t_user_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_user_stats`;
CREATE TABLE `t_user_stats`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `total_merit` bigint(20) NULL DEFAULT 0 COMMENT '总功德值',
  `merit_coins` bigint(20) NULL DEFAULT 0 COMMENT '功德币余额',
  `total_knocks` bigint(20) NULL DEFAULT 0 COMMENT '总敲击次数',
  `consecutive_days` int(11) NULL DEFAULT 0 COMMENT '连续登录天数',
  `total_login_days` int(11) NULL DEFAULT 0 COMMENT '总登录天数',
  `current_level` int(11) NULL DEFAULT 1 COMMENT '当前等级',
  `max_combo` int(11) NULL DEFAULT 0 COMMENT '最高连击数',
  `last_knock_time` datetime(0) NULL DEFAULT NULL COMMENT '最后敲击时间',
  `last_login_date` date NULL DEFAULT NULL COMMENT '最后登录日期',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE,
  INDEX `idx_total_merit`(`total_merit`) USING BTREE,
  INDEX `idx_update_time`(`update_time`) USING BTREE,
  INDEX `idx_user_stats_daily`(`last_login_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user_stats
-- ----------------------------
INSERT INTO `t_user_stats` VALUES (1971822665643724800, 1971822665190739968, 1000, 1000, 20, 20, 10, 1, 0, NULL, NULL, 'user711', '2025-09-27 14:22:13', 'user711', '2025-09-27 15:39:15', 0);
INSERT INTO `t_user_stats` VALUES (1990331081404059648, 1990331081332756480, 0, 7, 0, 0, 0, 1, 0, NULL, NULL, 'user845', '2025-11-17 16:08:03', 'user845', '2025-11-19 14:48:52', 0);
INSERT INTO `t_user_stats` VALUES (1993582099487133696, 1993582099294195712, 178, 80, 178, 11, 11, 2, 0, '2025-12-02 18:12:14', NULL, '1791350', '2025-11-26 15:26:26', '1791350', '2025-12-02 18:12:23', 0);

-- ----------------------------
-- Table structure for t_user_task
-- ----------------------------
DROP TABLE IF EXISTS `t_user_task`;
CREATE TABLE `t_user_task`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `task_date` date NOT NULL COMMENT '任务日期',
  `progress` int(11) NULL DEFAULT 0 COMMENT '当前进度',
  `is_completed` tinyint(4) NULL DEFAULT 0 COMMENT '是否完成：0-未完成，1-已完成',
  `complete_time` datetime(0) NULL DEFAULT NULL COMMENT '完成时间',
  `is_claimed` tinyint(4) NULL DEFAULT 0 COMMENT '是否已领取奖励：0-未领取，1-已领取',
  `claim_time` datetime(0) NULL DEFAULT NULL COMMENT '领取时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_task_date`(`user_id`, `task_id`, `task_date`) USING BTREE,
  INDEX `idx_task_date`(`task_date`) USING BTREE,
  INDEX `idx_is_completed`(`is_completed`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户任务进度表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user_task
-- ----------------------------

-- ----------------------------
-- Table structure for t_verification_code
-- ----------------------------
DROP TABLE IF EXISTS `t_verification_code`;
CREATE TABLE `t_verification_code`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `account` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号（手机号或邮箱）',
  `account_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号类型：phone-手机号，email-邮箱',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证码',
  `business_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务类型：register-注册，login-登录，reset-重置密码，bind-绑定',
  `used` tinyint(4) NULL DEFAULT 0 COMMENT '是否已使用：0-未使用，1-已使用',
  `use_time` datetime(0) NULL DEFAULT NULL COMMENT '使用时间',
  `expire_time` datetime(0) NOT NULL COMMENT '过期时间',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求IP地址',
  `device_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备信息',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account`(`account`) USING BTREE,
  INDEX `idx_code`(`code`) USING BTREE,
  INDEX `idx_business_type`(`business_type`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  INDEX `idx_expire_time`(`expire_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '验证码表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_verification_code
-- ----------------------------
INSERT INTO `t_verification_code` VALUES (1971808087916548096, '13800000000', 'phone', '385232', 'register', 0, NULL, '2025-09-27 13:29:17', '0:0:0:0:0:0:0:1', NULL, 'system', '2025-09-27 13:24:17', 'system', '2025-09-27 13:24:17', 0);
INSERT INTO `t_verification_code` VALUES (1971816543662772224, '13800000000', 'phone', '809185', 'register', 0, NULL, '2025-09-27 14:02:53', '0:0:0:0:0:0:0:1', NULL, 'system', '2025-09-27 13:57:53', 'system', '2025-09-27 13:57:53', 0);
INSERT INTO `t_verification_code` VALUES (1971822566377132032, '13888888888', 'phone', '369807', 'register', 0, NULL, '2025-09-27 14:26:49', '0:0:0:0:0:0:0:1', NULL, 'system', '2025-09-27 14:21:49', 'system', '2025-09-27 14:21:49', 0);
INSERT INTO `t_verification_code` VALUES (1971822589647130624, 'test@example.com', 'email', '902079', 'login', 0, NULL, '2025-09-27 14:26:55', '0:0:0:0:0:0:0:1', NULL, 'system', '2025-09-27 14:21:55', 'system', '2025-09-27 14:21:54', 1);
INSERT INTO `t_verification_code` VALUES (1990330639072759808, '17688701711', 'phone', '556079', 'register', 1, '2025-11-17 16:08:03', '2025-11-17 16:11:18', '127.0.0.1', NULL, 'system', '2025-11-17 16:06:18', 'system', '2025-11-17 16:06:18', 0);
INSERT INTO `t_verification_code` VALUES (1990331697576677376, '17688701711', 'phone', '640762', 'login', 1, '2025-11-17 16:11:20', '2025-11-17 16:15:30', '127.0.0.1', NULL, 'system', '2025-11-17 16:10:30', 'system', '2025-11-17 16:10:30', 0);
INSERT INTO `t_verification_code` VALUES (1990967977876525056, '17688701711', 'phone', '409212', 'login', 1, '2025-11-19 10:19:13', '2025-11-19 10:23:51', '127.0.0.1', NULL, 'system', '2025-11-19 10:18:51', 'system', '2025-11-19 10:18:51', 0);
INSERT INTO `t_verification_code` VALUES (1993525947772571648, '17688701711', 'phone', '158401', 'login', 1, '2025-11-26 11:43:31', '2025-11-26 11:48:19', '127.0.0.1', NULL, 'system', '2025-11-26 11:43:19', 'system', '2025-11-26 11:43:19', 0);

-- ----------------------------
-- Table structure for t_vip_package
-- ----------------------------
DROP TABLE IF EXISTS `t_vip_package`;
CREATE TABLE `t_vip_package`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '套餐名称',
  `package_type` tinyint(4) NOT NULL COMMENT '套餐类型：1-月卡，2-季卡，3-年卡，4-永久',
  `price` decimal(10, 2) NOT NULL COMMENT '价格（元）',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价（元）',
  `duration_days` int(11) NOT NULL COMMENT '时长（天），-1表示永久',
  `merit_bonus_rate` decimal(5, 2) NULL DEFAULT 1.00 COMMENT '功德加成倍率',
  `daily_merit_bonus` int(11) NULL DEFAULT 0 COMMENT '每日额外功德值',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '套餐描述',
  `benefits` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '套餐权益，JSON格式',
  `sort_order` int(11) NULL DEFAULT 0 COMMENT '排序序号',
  `is_active` tinyint(4) NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_package_type`(`package_type`) USING BTREE,
  INDEX `idx_is_active`(`is_active`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'VIP套餐表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_vip_package
-- ----------------------------

-- ----------------------------
-- Table structure for t_vip_purchase
-- ----------------------------
DROP TABLE IF EXISTS `t_vip_purchase`;
CREATE TABLE `t_vip_purchase`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `package_id` bigint(20) NOT NULL COMMENT '套餐ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `price` decimal(10, 2) NOT NULL COMMENT '实付金额（元）',
  `start_time` datetime(0) NOT NULL COMMENT '生效时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '到期时间，NULL表示永久',
  `payment_method` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付方式',
  `payment_status` tinyint(4) NULL DEFAULT 0 COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败',
  `payment_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_payment_status`(`payment_status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'VIP购买记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_vip_purchase
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
