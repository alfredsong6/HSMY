ALTER TABLE t_recharge_order
    ADD COLUMN payment_status_desc VARCHAR(255) DEFAULT NULL COMMENT 'payment status description' AFTER payment_status;

ALTER TABLE t_recharge_order
    MODIFY COLUMN payment_status TINYINT DEFAULT 0 COMMENT 'payment status: 0-pending,1-success,2-failed,3-refund,4-closed';
