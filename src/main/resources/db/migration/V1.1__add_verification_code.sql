-- 创建验证码表
CREATE TABLE IF NOT EXISTS `verification_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account` varchar(100) NOT NULL COMMENT '手机号或邮箱',
  `account_type` varchar(20) NOT NULL COMMENT '账号类型：phone/email',
  `code` varchar(10) NOT NULL COMMENT '验证码',
  `business_type` varchar(50) NOT NULL COMMENT '业务类型：register/login/reset_password',
  `used` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已使用：0-未使用，1-已使用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  PRIMARY KEY (`id`),
  KEY `idx_account_business` (`account`,`business_type`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

-- 为用户表的password字段允许NULL值（用于验证码注册后未设置密码的用户）
ALTER TABLE `user` MODIFY COLUMN `password` varchar(100) DEFAULT NULL COMMENT '密码（MD5加密）';

-- 添加索引优化查询性能
ALTER TABLE `user` ADD INDEX IF NOT EXISTS `idx_phone` (`phone`);
ALTER TABLE `user` ADD INDEX IF NOT EXISTS `idx_email` (`email`);