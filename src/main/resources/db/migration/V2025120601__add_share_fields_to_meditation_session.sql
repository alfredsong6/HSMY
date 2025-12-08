-- Add share info for meditation sessions
ALTER TABLE t_meditation_session
    ADD COLUMN share_flag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否分享：0-否，1-是' AFTER insight_text,
    ADD COLUMN share_target VARCHAR(20) NULL COMMENT '分享渠道：FRIEND/MOMENTS' AFTER share_flag;
