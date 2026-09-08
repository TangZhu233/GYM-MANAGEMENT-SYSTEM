-- 课程多教练任教 + 报名选教练
-- 与权威脚本 gym_management_system.sql 同步；Run once（幂等部分用 IF NOT EXISTS / 不存在则跳过）。
-- class_table.coach_id 保留为主教练（= 任教名单中 coach_id 最小者），名单空则 NULL=待安排。

-- 1) 任教名单表（课程 ↔ 健身教练 多对多）
CREATE TABLE IF NOT EXISTS `class_coach` (
  `class_id` int NOT NULL COMMENT '课程ID(class_table.class_id)',
  `coach_id` int NOT NULL COMMENT '任教教练员工ID(employee.employee_id)',
  PRIMARY KEY (`class_id`, `coach_id`),
  KEY `idx_class_coach_coach_id` (`coach_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='课程任教教练名单';

-- 2) 从现有主教练回填任教名单（coach_id 为 NULL = 已删教练/待安排 → 名单留空）
INSERT INTO `class_coach` (`class_id`, `coach_id`)
SELECT ct.class_id, ct.coach_id FROM class_table ct
WHERE ct.coach_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM class_coach x WHERE x.class_id = ct.class_id AND x.coach_id = ct.coach_id);

-- 3) class_record 记录报名所选教练
SET @has_col = (SELECT COUNT(*) FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='class_record' AND COLUMN_NAME='coach_id');
SET @ddl = IF(@has_col = 0,
  'ALTER TABLE `class_record` ADD COLUMN `coach_id` int NULL DEFAULT NULL COMMENT ''报名所选教练 employee_id'' AFTER `class_id`',
  'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4) 历史记录回填（旧口径 = 按该课当时主教练；无教练课程的记录保持 NULL）
UPDATE class_record cr
JOIN class_table ct ON cr.class_id = ct.class_id
SET cr.coach_id = ct.coach_id
WHERE cr.coach_id IS NULL;

-- 5) 索引与主教练派生列重算（名单空 → coach_id=NULL）
ALTER TABLE class_record ADD INDEX idx_cr_coach_id (coach_id);
UPDATE class_table ct
LEFT JOIN (
  SELECT class_id, MIN(coach_id) AS min_coach FROM class_coach GROUP BY class_id
) r ON r.class_id = ct.class_id
SET ct.coach_id = r.min_coach;

-- 6) 外键（放最后）
ALTER TABLE class_record
  ADD CONSTRAINT fk_class_record_coach FOREIGN KEY (coach_id)
  REFERENCES employee (employee_id) ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE class_coach
  ADD CONSTRAINT fk_class_coach_class FOREIGN KEY (class_id)
  REFERENCES class_table (class_id) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT fk_class_coach_employee FOREIGN KEY (coach_id)
  REFERENCES employee (employee_id) ON DELETE CASCADE ON UPDATE CASCADE;
