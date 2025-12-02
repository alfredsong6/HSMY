-- Adjust status values and track heartbeat
ALTER TABLE t_meditation_session
    MODIFY status VARCHAR(20) NOT NULL DEFAULT 'STARTED' COMMENT '会话状态';

UPDATE t_meditation_session SET status = 'STARTED' WHERE status = 'START';

ALTER TABLE t_meditation_session
    ADD COLUMN last_heartbeat_time DATETIME NULL COMMENT '最后一次心跳时间';
