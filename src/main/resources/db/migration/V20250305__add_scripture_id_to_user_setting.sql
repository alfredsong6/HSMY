-- Add scripture_id for scripture bullet screen mode
ALTER TABLE t_user_setting
    ADD COLUMN scripture_id BIGINT DEFAULT NULL COMMENT '弹幕经书ID(bullet_screen=3时使用)' AFTER bullet_screen;
