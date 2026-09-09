-- 会员每日运动打卡表：每天每会员至多 1 条（member_id, check_date 唯一）。
-- 与「我的课程」上课打卡语义独立；与权威脚本 gym_management_system.sql 同步；Run once（幂等）。
CREATE TABLE IF NOT EXISTS `gym_checkin`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '打卡id',
  `member_id` int NOT NULL COMMENT '会员ID',
  `check_date` date NOT NULL COMMENT '打卡日期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_checkin_member_date` (`member_id`, `check_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact COMMENT '会员每日运动打卡';
