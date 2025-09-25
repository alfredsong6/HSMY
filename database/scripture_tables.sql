/*
 典籍功能模块数据表设计
 包含：典籍表、用户典籍购买记录表
 Date: 2025-09-25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
  INDEX `idx_sort_order`(`sort_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '典籍表' ROW_FORMAT = Dynamic;

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
  INDEX `idx_purchase_time`(`purchase_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户典籍购买记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 初始化典籍数据
-- ----------------------------
INSERT INTO `t_scripture` VALUES
(1001, '心经', 'sutra', '玄奘', '《般若波罗蜜多心经》是大乘佛教经典之一，全文共260字，是六百卷《大般若经》的精华。', '观自在菩萨，行深般若波罗蜜多时，照见五蕴皆空，度一切苦厄...', NULL, NULL, 1, 2, '部', 1, 0, 0, 1, 260, '般若,心经,大乘', 1, 1, 'system', NOW(), 'system', NOW(), 0),
(1002, '金刚经', 'sutra', '鸠摩罗什', '《金刚般若波罗蜜经》，简称《金刚经》，是大乘佛教般若部重要经典之一。', '如是我闻，一时，佛在舍卫国祇树给孤独园，与大比丘众千二百五十人俱...', NULL, NULL, 1, 5, '部', 1, 0, 0, 2, 5000, '般若,金刚,大乘', 1, 2, 'system', NOW(), 'system', NOW(), 0),
(1003, '大悲咒', 'mantra', '伽梵达摩', '《千手千眼观世音菩萨广大圆满无碍大悲心陀罗尼》，简称《大悲咒》。', '南无、喝啰怛那、哆啰夜耶，南无、阿唎耶，婆卢羯帝、烁钵啰耶...', NULL, NULL, 1, 3, '则', 1, 0, 0, 1, 415, '观音,大悲咒,密咒', 1, 3, 'system', NOW(), 'system', NOW(), 0),
(1004, '楞严经', 'sutra', '般剌蜜帝', '《大佛顶如来密因修证了义诸菩萨万行首楞严经》，简称《楞严经》，为大乘佛教经典。', '如是我闻：一时，佛在室罗筏城，祇桓精舍...', NULL, NULL, 0, 8, '部', 1, 0, 0, 3, 62000, '楞严,如来藏,大乘', 1, 4, 'system', NOW(), 'system', NOW(), 0),
(1005, '六字大明咒', 'mantra', '', '观世音菩萨心咒，藏传佛教中最常见的咒语之一。', '嗡(ōng)嘛(mā)呢(nī)叭(bēi)咪(mēi)吽(hōng)', NULL, NULL, 1, 1, '则', 1, 0, 0, 1, 6, '观音,六字真言,藏传', 1, 5, 'system', NOW(), 'system', NOW(), 0);

SET FOREIGN_KEY_CHECKS = 1;