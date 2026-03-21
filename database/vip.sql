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
                                 `limit` int(11) DEFAULT null COMMENT '限制购买次数',
                                 `create_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 KEY `idx_package_type` (`package_type`) USING BTREE,
                                 KEY `idx_is_active` (`is_active`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='VIP套餐表';



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