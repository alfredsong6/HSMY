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

 Date: 19/09/2025 18:30:30
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '成就定义表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '活动定义表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_activity
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '捐赠记录表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '捐赠项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_donation_project
-- ----------------------------
INSERT INTO `t_donation_project` VALUES (1, '虚拟庙宇建设', 'temple', '帮助建设线上虚拟庙宇，让更多人感受佛法', NULL, 0, 0, 1, NULL, NULL, NULL, 'system', '2025-09-08 14:17:09', 'system', '2025-09-08 14:17:09', 0);
INSERT INTO `t_donation_project` VALUES (2, '放生功德池', 'release', '积累放生功德，护生行善', NULL, 0, 0, 1, NULL, NULL, NULL, 'system', '2025-09-08 14:17:09', 'system', '2025-09-08 14:17:09', 0);
INSERT INTO `t_donation_project` VALUES (3, '助学善缘基金', 'education', '帮助贫困地区儿童接受教育', NULL, 0, 0, 1, NULL, NULL, NULL, 'system', '2025-09-08 14:17:09', 'system', '2025-09-08 14:17:09', 0);
INSERT INTO `t_donation_project` VALUES (4, '环保护生项目', 'environment', '保护环境，爱护生命', NULL, 0, 0, 1, NULL, NULL, NULL, 'system', '2025-09-08 14:17:09', 'system', '2025-09-08 14:17:09', 0);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '功德兑换记录表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邀请记录表' ROW_FORMAT = Dynamic;

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
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_item_type`(`item_type`) USING BTREE,
  INDEX `idx_is_active`(`is_active`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '道具表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_item
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '敲击会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_knock_session
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '功德等级配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_merit_level
-- ----------------------------
INSERT INTO `t_merit_level` VALUES (1, 1, '初级修行者', 0, 10000, '基础功能解锁', NULL, 1.00, 100, 'system', '2025-09-08 14:17:09', 'system', '2025-09-08 14:17:09', 0);
INSERT INTO `t_merit_level` VALUES (2, 2, '虔诚信徒', 10000, 50000, '解锁更多皮肤和音效', NULL, 1.10, 150, 'system', '2025-09-08 14:17:09', 'system', '2025-09-08 14:17:09', 0);
INSERT INTO `t_merit_level` VALUES (3, 3, '资深修行者', 50000, 150000, '功德获取加成10%', NULL, 1.20, 200, 'system', '2025-09-08 14:17:09', 'system', '2025-09-08 14:17:09', 0);
INSERT INTO `t_merit_level` VALUES (4, 4, '得道高僧', 150000, 500000, '功德获取加成20%，专属称号', NULL, 1.30, 300, 'system', '2025-09-08 14:17:09', 'system', '2025-09-08 14:17:09', 0);
INSERT INTO `t_merit_level` VALUES (5, 5, '佛陀境界', 500000, NULL, '功德获取加成30%，所有特权', NULL, 1.50, 500, 'system', '2025-09-08 14:17:09', 'system', '2025-09-08 14:17:09', 0);

-- ----------------------------
-- Table structure for t_merit_record
-- ----------------------------
DROP TABLE IF EXISTS `t_merit_record`;
CREATE TABLE `t_merit_record`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID，雪花算法生成',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `merit_gained` int(11) NOT NULL COMMENT '获得功德值',
  `knock_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '敲击类型：manual-手动，auto-自动',
  `source` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '来源：knock-敲击，task-任务，login-登录，activity-活动，share-分享',
  `session_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '会话ID，用于统计连击',
  `combo_count` int(11) NULL DEFAULT 0 COMMENT '连击数',
  `bonus_rate` decimal(5, 2) NULL DEFAULT 1.00 COMMENT '加成倍率',
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
  INDEX `idx_merit_record_session`(`session_id`, `create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '功德记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_merit_record
-- ----------------------------
INSERT INTO `t_merit_record` VALUES (1001, 1968213494687993856, 100, '1', '1', 'session_001', 1, 1.00, '普通敲击木鱼', 'system', '2024-11-01 08:30:00', 'system', '2024-11-01 08:30:00', 0);
INSERT INTO `t_merit_record` VALUES (1002, 1968213494687993856, 150, '2', '1', 'session_001', 5, 1.50, '连击敲击木鱼x5', 'system', '2024-11-01 09:15:00', 'system', '2024-11-01 09:15:00', 0);
INSERT INTO `t_merit_record` VALUES (1003, 1968213494687993856, 120, '1', '2', 'session_002', 1, 1.00, '普通敲击钟声', 'system', '2024-11-02 10:00:00', 'system', '2024-11-02 10:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1004, 1968213494687993856, 200, '2', '3', 'session_002', 10, 2.00, '连击敲击鼓声x10', 'system', '2024-11-03 14:20:00', 'system', '2024-11-03 14:20:00', 0);
INSERT INTO `t_merit_record` VALUES (1005, 1968213494687993856, 80, '1', '4', 'session_003', 1, 1.00, '普通念经', 'system', '2024-11-05 07:00:00', 'system', '2024-11-05 07:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1006, 1968213494687993856, 300, '2', '1', 'session_003', 15, 3.00, '连击敲击木鱼x15', 'system', '2024-11-06 18:30:00', 'system', '2024-11-06 18:30:00', 0);
INSERT INTO `t_merit_record` VALUES (1007, 1968213494687993856, 90, '1', '5', 'session_004', 1, 1.00, '普通供香', 'system', '2024-11-07 11:45:00', 'system', '2024-11-07 11:45:00', 0);
INSERT INTO `t_merit_record` VALUES (1008, 1968213494687993856, 250, '2', '2', 'session_004', 8, 2.50, '连击敲击钟声x8', 'system', '2024-11-08 16:00:00', 'system', '2024-11-08 16:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1009, 1968213494687993856, 110, '1', '3', 'session_005', 1, 1.00, '普通敲击鼓声', 'system', '2024-11-12 09:30:00', 'system', '2024-11-12 09:30:00', 0);
INSERT INTO `t_merit_record` VALUES (1010, 1968213494687993856, 180, '2', '4', 'session_005', 6, 1.80, '连击念经x6', 'system', '2024-11-13 13:15:00', 'system', '2024-11-13 13:15:00', 0);
INSERT INTO `t_merit_record` VALUES (1011, 1968213494687993856, 130, '1', '1', 'session_006', 1, 1.00, '普通敲击木鱼', 'system', '2024-11-14 08:00:00', 'system', '2024-11-14 08:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1012, 1968213494687993856, 400, '2', '5', 'session_006', 20, 4.00, '连击供香x20', 'system', '2024-11-15 20:30:00', 'system', '2024-11-15 20:30:00', 0);
INSERT INTO `t_merit_record` VALUES (1013, 1968213494687993856, 95, '1', '2', 'session_007', 1, 1.00, '普通敲击钟声', 'system', '2024-11-20 06:45:00', 'system', '2024-11-20 06:45:00', 0);
INSERT INTO `t_merit_record` VALUES (1014, 1968213494687993856, 220, '2', '3', 'session_007', 11, 2.20, '连击敲击鼓声x11', 'system', '2024-11-21 15:20:00', 'system', '2024-11-21 15:20:00', 0);
INSERT INTO `t_merit_record` VALUES (1015, 1968213494687993856, 105, '1', '4', 'session_008', 1, 1.00, '普通念经', 'system', '2024-11-22 12:00:00', 'system', '2024-11-22 12:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1016, 1968213494687993856, 350, '2', '1', 'session_008', 18, 3.50, '连击敲击木鱼x18', 'system', '2024-11-23 19:45:00', 'system', '2024-11-23 19:45:00', 0);
INSERT INTO `t_merit_record` VALUES (1017, 1968213494687993856, 125, '1', '5', 'session_009', 1, 1.00, '普通供香', 'system', '2024-12-01 07:30:00', 'system', '2024-12-01 07:30:00', 0);
INSERT INTO `t_merit_record` VALUES (1018, 1968213494687993856, 160, '2', '2', 'session_009', 7, 1.60, '连击敲击钟声x7', 'system', '2024-12-02 10:15:00', 'system', '2024-12-02 10:15:00', 0);
INSERT INTO `t_merit_record` VALUES (1019, 1968213494687993856, 115, '1', '1', 'session_010', 1, 1.00, '普通敲击木鱼', 'system', '2024-12-03 14:00:00', 'system', '2024-12-03 14:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1020, 1968213494687993856, 280, '2', '3', 'session_010', 14, 2.80, '连击敲击鼓声x14', 'system', '2024-12-04 17:30:00', 'system', '2024-12-04 17:30:00', 0);
INSERT INTO `t_merit_record` VALUES (1021, 1968213494687993856, 85, '1', '4', 'session_011', 1, 1.00, '普通念经', 'system', '2024-12-08 08:00:00', 'system', '2024-12-08 08:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1022, 1968213494687993856, 190, '2', '5', 'session_011', 9, 1.90, '连击供香x9', 'system', '2024-12-09 11:30:00', 'system', '2024-12-09 11:30:00', 0);
INSERT INTO `t_merit_record` VALUES (1023, 1968213494687993856, 140, '1', '2', 'session_012', 1, 1.00, '普通敲击钟声', 'system', '2024-12-10 13:45:00', 'system', '2024-12-10 13:45:00', 0);
INSERT INTO `t_merit_record` VALUES (1024, 1968213494687993856, 320, '2', '1', 'session_012', 16, 3.20, '连击敲击木鱼x16', 'system', '2024-12-11 16:15:00', 'system', '2024-12-11 16:15:00', 0);
INSERT INTO `t_merit_record` VALUES (1025, 1968213494687993856, 98, '1', '3', 'session_013', 1, 1.00, '普通敲击鼓声', 'system', '2024-12-15 09:00:00', 'system', '2024-12-15 09:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1026, 1968213494687993856, 240, '2', '4', 'session_013', 12, 2.40, '连击念经x12', 'system', '2024-12-16 14:30:00', 'system', '2024-12-16 14:30:00', 0);
INSERT INTO `t_merit_record` VALUES (1027, 1968213494687993856, 135, '1', '5', 'session_014', 1, 1.00, '普通供香', 'system', '2024-12-17 10:00:00', 'system', '2024-12-17 10:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1028, 1968213494687993856, 380, '2', '2', 'session_014', 19, 3.80, '连击敲击钟声x19', 'system', '2024-12-18 18:45:00', 'system', '2024-12-18 18:45:00', 0);
INSERT INTO `t_merit_record` VALUES (1029, 1968213494687993856, 145, '1', '1', 'session_015', 1, 1.00, '普通敲击木鱼', 'system', '2025-01-01 00:30:00', 'system', '2025-01-01 00:30:00', 0);
INSERT INTO `t_merit_record` VALUES (1030, 1968213494687993856, 260, '2', '3', 'session_015', 13, 2.60, '连击敲击鼓声x13', 'system', '2025-01-02 08:15:00', 'system', '2025-01-02 08:15:00', 0);
INSERT INTO `t_merit_record` VALUES (1031, 1968213494687993856, 108, '1', '4', 'session_016', 1, 1.00, '普通念经', 'system', '2025-01-03 12:30:00', 'system', '2025-01-03 12:30:00', 0);
INSERT INTO `t_merit_record` VALUES (1032, 1968213494687993856, 420, '2', '5', 'session_016', 21, 4.20, '连击供香x21', 'system', '2025-01-04 19:00:00', 'system', '2025-01-04 19:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1033, 1968213494687993856, 118, '1', '2', 'session_017', 1, 1.00, '普通敲击钟声', 'system', '2025-01-06 07:45:00', 'system', '2025-01-06 07:45:00', 0);
INSERT INTO `t_merit_record` VALUES (1034, 1968213494687993856, 170, '2', '1', 'session_017', 8, 1.70, '连击敲击木鱼x8', 'system', '2025-01-07 15:00:00', 'system', '2025-01-07 15:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1035, 1968213494687993856, 155, '1', '3', 'session_018', 1, 1.00, '普通敲击鼓声', 'system', '2025-01-08 11:20:00', 'system', '2025-01-08 11:20:00', 0);
INSERT INTO `t_merit_record` VALUES (1036, 1968213494687993856, 340, '2', '4', 'session_018', 17, 3.40, '连击念经x17', 'system', '2025-01-09 20:15:00', 'system', '2025-01-09 20:15:00', 0);
INSERT INTO `t_merit_record` VALUES (1037, 1968213494687993856, 128, '1', '5', 'session_019', 1, 1.00, '普通供香', 'system', '2025-01-13 09:30:00', 'system', '2025-01-13 09:30:00', 0);
INSERT INTO `t_merit_record` VALUES (1038, 1968213494687993856, 210, '2', '2', 'session_019', 10, 2.10, '连击敲击钟声x10', 'system', '2025-01-14 13:00:00', 'system', '2025-01-14 13:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1039, 1968213494687993856, 102, '1', '1', 'session_020', 1, 1.00, '普通敲击木鱼', 'system', '2025-01-15 16:45:00', 'system', '2025-01-15 16:45:00', 0);
INSERT INTO `t_merit_record` VALUES (1040, 1968213494687993856, 500, '2', '3', 'session_020', 25, 1.00, '连击敲击鼓声x25(最高连击)', 'system', '2025-09-13 21:30:00', 'system', '2025-09-18 15:43:02', 0);
INSERT INTO `t_merit_record` VALUES (1041, 1968213494687993856, 112, '1', '4', 'session_021', 1, 1.00, '普通念经', 'system', '2025-01-20 08:00:00', 'system', '2025-01-20 08:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1042, 1968213494687993856, 270, '2', '5', 'session_021', 12, 1.00, '连击供香x12', 'system', '2025-01-21 14:30:00', 'system', '2025-09-18 15:44:17', 0);
INSERT INTO `t_merit_record` VALUES (1043, 1968213494687993856, 122, '1', '2', 'session_022', 1, 1.00, '普通敲击钟声', 'system', '2025-01-22 10:15:00', 'system', '2025-01-22 10:15:00', 0);
INSERT INTO `t_merit_record` VALUES (1044, 1968213494687993856, 360, '2', '1', 'session_022', 18, 1.00, '连击敲击木鱼x18', 'system', '2025-01-23 17:45:00', 'system', '2025-09-18 15:44:13', 0);
INSERT INTO `t_merit_record` VALUES (1045, 1968213494687993856, 138, '1', '3', 'session_023', 1, 1.00, '普通敲击鼓声', 'system', '2025-09-18 09:00:00', 'system', '2025-09-18 15:41:21', 0);
INSERT INTO `t_merit_record` VALUES (1046, 1968213494687993856, 230, '2', '4', 'session_023', 11, 1.00, '连击念经x11', 'system', '2025-01-28 13:20:00', 'system', '2025-09-18 15:44:11', 0);
INSERT INTO `t_merit_record` VALUES (1047, 1968213494687993856, 150, '1', '5', 'session_024', 1, 1.00, '普通供香', 'system', '2025-01-29 11:00:00', 'system', '2025-01-29 11:00:00', 0);
INSERT INTO `t_merit_record` VALUES (1048, 1968213494687993856, 450, '2', '2', 'session_024', 22, 1.00, '连击敲击钟声x22', 'system', '2025-09-18 19:30:00', 'system', '2025-09-18 15:42:46', 0);
INSERT INTO `t_merit_record` VALUES (1049, 1964959257782784000, 420, '2', '2', 'session_024', 22, 1.00, '连击敲击钟声x22', 'system', '2025-09-18 19:30:00', 'system', '2025-09-18 15:42:46', 0);
INSERT INTO `t_merit_record` VALUES (1968885484285464576, 1968213494687993856, 36, 'manual', 'knock', '85dd78a23f6644ef9a347d7e6936ca4c', 0, 1.00, '手动敲击获得功德(本小时累计功德: 36)', 'system', '2025-09-19 09:00:00', 'system', '2025-09-19 11:50:54', 0);
INSERT INTO `t_merit_record` VALUES (1968885606440374272, 1968213494687993856, 4, 'manual', 'knock', '5c77d250bb88492e96025830f1924deb', 0, 1.00, '手动敲击获得功德(本小时累计功德: 4)', 'system', '2025-09-19 10:00:00', 'system', '2025-09-19 11:51:23', 0);
INSERT INTO `t_merit_record` VALUES (1968885678695649280, 1968213494687993856, 12, 'manual', 'knock', '6f2beb70917b493c93e5cc553e0a199d', 0, 1.00, '手动敲击获得功德', 'system', '2025-09-19 11:00:00', 'system', '2025-09-19 11:51:41', 0);
INSERT INTO `t_merit_record` VALUES (1968895560278740992, 1968213494687993856, 32, 'manual', 'knock', '6cd7e9411ced4b0ab0ae6f8f9227485b', 0, 1.00, '手动敲击获得功德', 'system', '2025-09-19 12:00:00', 'system', '2025-09-19 12:30:57', 0);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '购买记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_purchase_record
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '排行榜快照表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_ranking
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '排行榜奖励记录表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_recharge_order
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分享记录表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统消息表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务定义表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1964959257782784000, 'testuser_1757317768_9927', 'e10adc3949ba59abbe56e057f20f883e', '测试用户_1757317768', NULL, 'testuser_1757317768_9927@example.com', '13814437942', 0, NULL, '2025-09-08 15:49:29', NULL, 1, 0, NULL, 'testuser_1757317768_9927', '2025-09-08 15:49:29', 'testuser_1757317768_9927', '2025-09-08 15:49:29', 0);
INSERT INTO `t_user` VALUES (1964959314317807616, 'testuser_1757317782_3663', 'e10adc3949ba59abbe56e057f20f883e', '测试用户_1757317782', NULL, 'testuser_1757317782_3663@example.com', '13849730749', 0, NULL, '2025-09-08 15:49:42', NULL, 1, 0, NULL, 'testuser_1757317782_3663', '2025-09-08 15:49:42', 'testuser_1757317782_3663', '2025-09-08 15:49:42', 0);
INSERT INTO `t_user` VALUES (1964959316603703296, 'testuser_1757317782_9441', 'e10adc3949ba59abbe56e057f20f883e', '测试用户_1757317782', NULL, 'testuser_1757317782_9441@example.com', '13837896320', 0, NULL, '2025-09-08 15:49:43', NULL, 1, 0, NULL, 'testuser_1757317782_9441', '2025-09-08 15:49:43', 'testuser_1757317782_9441', '2025-09-08 15:49:43', 0);
INSERT INTO `t_user` VALUES (1964959319036399616, 'testuser_1757317783_2019', 'e10adc3949ba59abbe56e057f20f883e', '测试用户_1757317783', NULL, 'testuser_1757317783_2019@example.com', '13884982957', 0, NULL, '2025-09-08 15:49:43', NULL, 1, 0, NULL, 'testuser_1757317783_2019', '2025-09-08 15:49:43', 'testuser_1757317783_2019', '2025-09-08 15:49:43', 0);
INSERT INTO `t_user` VALUES (1968213494687993856, '1706', 'c33367701511b4f6020ec61ded352059', '测试用户1', '/api/file/uploads/avatar/20250918/090685be65c5496491bb1f3b9103ba78.jpeg', NULL, '17688701791', 0, NULL, '2025-09-17 15:20:39', NULL, 1, 0, NULL, '1706', '2025-09-17 15:20:39', '1706', '2025-09-18 10:45:08', 0);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户成就表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户活动参与记录表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户道具表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_item
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_message
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_relation
-- ----------------------------

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
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'system' COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_setting
-- ----------------------------
INSERT INTO `t_user_setting` VALUES (1964959301177053184, 1, 1, 80, 1, 1, '09:00:00', 0, 2, NULL, NULL, NULL, 'system', '2025-09-08 15:49:39', 'system', '2025-09-08 15:49:39', 0);

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
  `today_merit` bigint(20) NULL DEFAULT 0 COMMENT '今日功德值',
  `today_knocks` bigint(20) NULL DEFAULT 0 COMMENT '今日敲击次数',
  `weekly_merit` bigint(20) NULL DEFAULT 0 COMMENT '本周功德值',
  `monthly_merit` bigint(20) NULL DEFAULT 0 COMMENT '本月功德值',
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
  INDEX `idx_user_stats_daily`(`last_login_date`, `today_merit`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_stats
-- ----------------------------
INSERT INTO `t_user_stats` VALUES (1964959259003326464, 1964959257782784000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, NULL, NULL, 'testuser_1757317768_9927', '2025-09-08 15:49:29', 'testuser_1757317768_9927', '2025-09-08 15:49:29', 0);
INSERT INTO `t_user_stats` VALUES (1964959314326196224, 1964959314317807616, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, NULL, NULL, 'testuser_1757317782_3663', '2025-09-08 15:49:42', 'testuser_1757317782_3663', '2025-09-08 15:49:42', 0);
INSERT INTO `t_user_stats` VALUES (1964959316607897600, 1964959316603703296, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, NULL, NULL, 'testuser_1757317782_9441', '2025-09-08 15:49:43', 'testuser_1757317782_9441', '2025-09-08 15:49:43', 0);
INSERT INTO `t_user_stats` VALUES (1964959319053176832, 1964959319036399616, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, NULL, NULL, 'testuser_1757317783_2019', '2025-09-08 15:49:43', 'testuser_1757317783_2019', '2025-09-08 15:49:43', 0);
INSERT INTO `t_user_stats` VALUES (1968213494738325504, 1968213494687993856, 84, 0, 84, 84, 84, 84, 84, 0, 0, 1, 0, '2025-09-19 12:30:56', NULL, '1706', '2025-09-17 15:20:39', '1706', '2025-09-19 12:30:56', 0);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户任务进度表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '验证码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_verification_code
-- ----------------------------
INSERT INTO `t_verification_code` VALUES (1968212992193597440, '17688701791', 'phone', '264464', 'register', 1, '2025-09-17 15:20:39', '2025-09-17 15:23:40', '0:0:0:0:0:0:0:1', NULL, 'system', '2025-09-17 15:18:40', 'system', '2025-09-17 15:18:40', 0);
INSERT INTO `t_verification_code` VALUES (1968248549795827712, '17688701791', 'phone', '246838', 'reset_password', 1, '2025-09-17 17:42:02', '2025-09-17 17:44:57', '0:0:0:0:0:0:0:1', NULL, 'system', '2025-09-17 17:39:57', 'system', '2025-09-17 17:39:57', 0);
INSERT INTO `t_verification_code` VALUES (1968249369274748928, '17688701791', 'phone', '028102', 'login', 1, '2025-09-17 17:44:03', '2025-09-17 17:48:13', '0:0:0:0:0:0:0:1', NULL, 'system', '2025-09-17 17:43:13', 'system', '2025-09-17 17:43:13', 0);

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'VIP套餐表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'VIP购买记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_vip_purchase
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
