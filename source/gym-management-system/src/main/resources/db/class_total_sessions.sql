-- 课程总课时（节）: 给 class_table 增加 total_sessions（一门课含几节课/几次课）。
-- 会员报名该课程时按此节数扣减卡内课时，报名记录以此记录总节数与已完成节数。
-- Run once after the column is absent. Backup before running.

ALTER TABLE class_table
    ADD COLUMN total_sessions INT NOT NULL DEFAULT 1 COMMENT '总课时(节)';

-- 回填已存在的报名记录总节数（沿用对应课程总课时，默认 1 节）
UPDATE class_record cr
JOIN class_table ct ON cr.class_id = ct.class_id
SET cr.total_sessions = COALESCE(cr.total_sessions, ct.total_sessions, 1)
WHERE cr.total_sessions IS NULL;
