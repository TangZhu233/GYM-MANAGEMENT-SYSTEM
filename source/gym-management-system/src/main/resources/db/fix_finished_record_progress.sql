-- 修复历史“已完成”报名记录的课次进度（口径回填）。
-- 背景：早期课程还没有“按节打卡/课次进度”时，历史报名记录只置了 status=1(已完成)，
--       completed_sessions(已完成节数) 为空 → 会员端“我的课程”把已上完的课程误显示成
--       “已完成 0/N 节 · 剩 N 节”，且 status=1 时打卡按钮被禁用，剩余节永远消不掉。
-- 口径（2026-09-09 与用户确认）：报名记录一旦 status=1(已完成) 即代表该课程整门已上完，
--       已完成节数 = 总节数；总节数沿用报名记录快照，为空时以课程当前总节兜底（默认1）。
-- 幂等：可重复执行；仅修正“已完成但已完成节数缺失/不足”的行，不触碰进行中(status=0)记录。
-- 执行：本地 gym_db 已跑过一次；权威 gym_management_system.sql 含同款语句保证全新导入一致。

-- 1) 已完成记录若总节数缺失，先以课程总节兜底
UPDATE class_record cr
LEFT JOIN class_table ct ON cr.class_id = ct.class_id
SET cr.total_sessions = COALESCE(cr.total_sessions, ct.total_sessions, 1)
WHERE cr.status = 1 AND cr.total_sessions IS NULL;

-- 2) 已完成记录的已完成节数与总节数对齐
UPDATE class_record cr
SET cr.completed_sessions = cr.total_sessions
WHERE cr.status = 1
  AND cr.total_sessions IS NOT NULL
  AND (cr.completed_sessions IS NULL OR cr.completed_sessions < cr.total_sessions);
