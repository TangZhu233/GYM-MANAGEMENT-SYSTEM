-- 课程主键改自增：添加课程不再手填编号，由主键自动生成。
-- 背景：新增课程表单此前需手填编号，误用已存在的编号触发主键冲突导致“添加无反应”。
-- Run once. 对已有数据安全（InnoDB 自增从 max(class_id)+1 继续），增量同款已改到权威 gym_management_system.sql。
-- class_table.class_id 被 class_coach/class_equipment/class_record 三张子表外键引用，
-- 因此临时关闭外键检查后再改列（类型未变，仅加 AUTO_INCREMENT，子表数据不受影响）。

SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE class_table
    MODIFY COLUMN class_id INT NOT NULL AUTO_INCREMENT COMMENT '课程id';
SET FOREIGN_KEY_CHECKS = 1;
