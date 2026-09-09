-- 删除 class_table.class_begin（开课时间）：课程无单次排期时间，UI 各处已去掉该显示。
-- Run once. 权威 gym_management_system.sql 已同步删除列定义与种子插入中的时间值。
ALTER TABLE class_table
    DROP COLUMN class_begin;
