ALTER TABLE t_recharge_order
    ADD COLUMN package_id varchar(64) NULL COMMENT '虚拟支付档位ID' AFTER order_no,
    ADD COLUMN delivered tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否已发币：0-否，1-是' AFTER payment_status,
    ADD COLUMN delivered_time datetime NULL COMMENT '发币完成时间' AFTER delivered,
    ADD COLUMN attach varchar(512) NULL COMMENT '业务透传信息' AFTER remark,
    ADD COLUMN notify_payload text NULL COMMENT '最近一次支付通知原文' AFTER attach,
    ADD COLUMN notify_time datetime NULL COMMENT '最近一次通知时间' AFTER notify_payload;

CREATE INDEX idx_package_id ON t_recharge_order(package_id);
CREATE INDEX idx_payment_delivered ON t_recharge_order(payment_status, delivered);
