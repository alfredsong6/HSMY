-- Add status to meditation sessions to track lifecycle
ALTER TABLE t_meditation_session
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'START' COMMENT '会话状态';

-- Backfill existing rows
UPDATE t_meditation_session
SET status = CASE
                 WHEN save_flag = 0 THEN 'INTERRUPTED'
                 WHEN end_time IS NOT NULL THEN 'COMPLETED'
                 ELSE 'START'
             END;
