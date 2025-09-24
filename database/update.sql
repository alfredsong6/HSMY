-- Schema update for knock session and merit record enhancements

ALTER TABLE `t_knock_session`
    ADD COLUMN `session_mode` varchar(30) NOT NULL DEFAULT 'MANUAL' COMMENT '会话模式：MANUAL-手动，AUTO_AUTOEND-自动结束，AUTO_TIMED-定时结束' AFTER `knock_type`,
    ADD COLUMN `limit_type` varchar(20) DEFAULT NULL COMMENT '限制类型：DURATION-时长，COUNT-次数' AFTER `session_mode`,
    ADD COLUMN `limit_value` int(11) DEFAULT NULL COMMENT '限制值，对应秒数或敲击次数' AFTER `limit_type`,
    ADD COLUMN `expected_end_time` datetime DEFAULT NULL COMMENT '预计结束时间' AFTER `limit_value`,
    ADD COLUMN `merit_multiplier` decimal(8,2) DEFAULT 1.00 COMMENT '会话功德倍率' AFTER `merit_gained`,
    ADD COLUMN `prop_snapshot` text COMMENT '道具快照JSON' AFTER `merit_multiplier`,
    ADD COLUMN `status` varchar(20) NOT NULL DEFAULT 'active' COMMENT '会话状态：active-进行中，stopped-手动停止，completed-已完成，timeout-超时结算' AFTER `prop_snapshot`,
    ADD COLUMN `last_heartbeat_time` datetime DEFAULT NULL COMMENT '最后心跳时间' AFTER `status`,
    ADD COLUMN `end_reason` varchar(100) DEFAULT NULL COMMENT '结束原因：auto_end、timeout、manual_stop等' AFTER `last_heartbeat_time`,
    ADD COLUMN `coin_cost` int(11) DEFAULT 0 COMMENT '预扣功德币' AFTER `end_reason`,
    ADD COLUMN `coin_refunded` int(11) DEFAULT 0 COMMENT '已退还功德币' AFTER `coin_cost`,
    ADD COLUMN `wallet_txn_id` varchar(64) DEFAULT NULL COMMENT '钱包流水ID' AFTER `coin_refunded`,
    ADD COLUMN `payment_status` varchar(20) DEFAULT 'RESERVED' COMMENT '支付状态：RESERVED-已预扣，SETTLED-已结算，REFUNDED-已退款' AFTER `wallet_txn_id`;

ALTER TABLE `t_merit_record`
    ADD COLUMN `base_merit` int(11) DEFAULT 0 COMMENT '基础功德值（未计算倍率前）' AFTER `merit_gained`,
    ADD COLUMN `knock_mode` varchar(30) DEFAULT NULL COMMENT '敲击模式：MANUAL、AUTO_AUTOEND、AUTO_TIMED' AFTER `knock_type`,
    MODIFY COLUMN `bonus_rate` decimal(8,2) DEFAULT 1.00 COMMENT '总加成倍率' AFTER `combo_count`,
    ADD COLUMN `prop_snapshot` text COMMENT '道具倍率快照JSON' AFTER `bonus_rate`,
    ADD COLUMN `stat_date` date DEFAULT NULL COMMENT '所属自然日，凌晨清零后重置' AFTER `prop_snapshot`,
    ADD INDEX `idx_stat_date` (`stat_date`);
