SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS `gym_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `gym_db`;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `admin_account` int NOT NULL COMMENT '管理员账号',
  `admin_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '管理员密码',
  PRIMARY KEY (`admin_account`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1001, '123456');
INSERT INTO `admin` VALUES (1002, '123456');
INSERT INTO `admin` VALUES (1003, '123456');

-- ----------------------------
-- Table structure for class_table
-- ----------------------------
DROP TABLE IF EXISTS `class_table`;
CREATE TABLE `class_table`  (
  `class_id` int NOT NULL COMMENT '课程id',
  `class_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程名称',
  `class_begin` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开课时间',
  `class_duration` int NULL DEFAULT NULL COMMENT '课程时长(分钟)',
  `class_level` int NULL DEFAULT 1 COMMENT '课程等级 1入门 2进阶 3专业',
  `coach_id` int NULL DEFAULT NULL COMMENT '教练ID FK->employee',
  `max_capacity` int NULL DEFAULT NULL COMMENT '最大容量',
  `class_description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '课程描述',
  `price` double NULL DEFAULT NULL COMMENT '价格',
  `total_sessions` int NOT NULL DEFAULT 1 COMMENT '总课时(节)',
  PRIMARY KEY (`class_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ----------------------------
-- Records of class_table
-- ----------------------------
INSERT INTO `class_table` VALUES (1, '增肌', '2026年1月1日 15:00', 60, 1, 101038721, 42, '增肌训练课程', 200, 1);
INSERT INTO `class_table` VALUES (2, '瑜伽', '2026年1月2日 10:20', 90, 2, 101068283, 20, '瑜伽课程', 150, 1);
INSERT INTO `class_table` VALUES (3, '减脂', '2026年3月6日 18:00', 90, 2, 101053687, 40, '减脂训练课程', 180, 1);
INSERT INTO `class_table` VALUES (4, '运动康复', '2026年2月2日 10:00', 45, 3, 101045354, 12, '运动康复课程', 300, 1);
INSERT INTO `class_table` VALUES (5, '综合格斗', '2026年2月3日 15:00', 60, 2, 101038721, 20, '综合格斗训练', 220, 1);
INSERT INTO `class_table` VALUES (6, '塑形', '2026年2月3日 15:00', 60, 1, 101068283, 18, '塑形训练课程', 160, 1);
INSERT INTO `class_table` VALUES (7, '普拉提', '2026年3月1日 17:30', 60, 2, 101053687, 22, '普拉提课程', 200, 1);
INSERT INTO `class_table` VALUES (8, '爵士舞', '2026年2月22日 09:00', 90, 1, 101045354, 28, '爵士舞课程', 130, 1);
INSERT INTO `class_table` VALUES (9, '杠铃操', '2026年2月4日 15:00', 60, 2, 101038721, 18, '杠铃操课程', 170, 1);
INSERT INTO `class_table` VALUES (10, '动感单车', '2026年3月8日 15:00', 45, 1, 101068283, 34, '动感单车课程', 120, 1);
INSERT INTO `class_table` VALUES (11, '健美操', '2026年2月22日 18:00', 60, 1, 101053687, 20, '健美操课程', 140, 1);


-- ----------------------------
-- Table structure for class_record (报名记录)
-- ----------------------------
DROP TABLE IF EXISTS `class_record`;
CREATE TABLE `class_record`  (
  `record_id` int NOT NULL AUTO_INCREMENT COMMENT '报名记录id',
  `member_id` int NOT NULL COMMENT '会员ID',
  `class_id` int NOT NULL COMMENT '课程ID',
  `join_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `status` int NULL DEFAULT 0 COMMENT '0已报名 1已完成 2已取消',
  `total_sessions` int NULL DEFAULT NULL COMMENT '总课时',
  `completed_sessions` int NULL DEFAULT NULL COMMENT '已完成课时',
  `rating` double NULL DEFAULT NULL COMMENT '评分',
  PRIMARY KEY (`record_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ----------------------------
-- Records of class_record
-- ----------------------------
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026002, 2, '2026-01-02 10:20:00', 0, 8.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026003, 2, '2026-01-02 10:20:00', 0, 9.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026008, 4, '2026-02-02 10:00:00', 0, 7.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026008, 1, '2026-01-01 15:00:00', 0, 6.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026006, 1, '2026-01-01 15:00:00', 0, 5.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026006, 2, '2026-01-02 10:20:00', 0, 8.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026002, 1, '2026-01-01 15:00:00', 0, 9.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026004, 1, '2026-01-01 15:00:00', 0, 4.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026004, 3, '2026-03-06 18:00:00', 0, NULL);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026012, 3, '2026-03-06 18:00:00', 0, 7.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026009, 3, '2026-03-06 18:00:00', 0, 8.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026002, 5, '2026-02-03 15:00:00', 1, 8.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026003, 5, '2026-02-03 15:00:00', 1, 7.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026005, 6, '2026-02-03 15:00:00', 0, 6.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026007, 7, '2026-03-01 17:30:00', 0, NULL);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026010, 7, '2026-03-01 17:30:00', 0, 9.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026001, 8, '2026-02-22 09:00:00', 1, 7.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026009, 8, '2026-02-22 09:00:00', 0, 8.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026011, 9, '2026-02-04 15:00:00', 0, 6.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026012, 10, '2026-03-08 15:00:00', 0, 7.5);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026005, 10, '2026-03-08 15:00:00', 0, 5.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026010, 11, '2026-02-22 18:00:00', 0, 8.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026007, 11, '2026-02-22 18:00:00', 0, NULL);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026008, 3, '2026-03-06 18:00:00', 0, 9.0);
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES (2026001, 2, '2026-01-02 10:20:00', 1, 7.0);

-- Demo data for operation dashboard: make enrollment distribution visibly tiered.
INSERT INTO `class_record` (member_id, class_id, join_time, status, rating) VALUES
(2026013, 1, '2026-01-01 15:00:00', 1, 8.8),
(2026014, 1, '2026-01-01 15:00:00', 0, 9.0),
(2026015, 1, '2026-01-01 15:00:00', 0, 8.5),
(2026016, 1, '2026-01-01 15:00:00', 0, NULL),
(2026017, 1, '2026-01-01 15:00:00', 0, 8.8),
(2026018, 1, '2026-01-01 15:00:00', 1, 9.0),
(2026019, 1, '2026-01-01 15:00:00', 0, 8.5),
(2026020, 1, '2026-01-01 15:00:00', 0, NULL),
(2026021, 1, '2026-01-01 15:00:00', 0, 8.8),
(2026022, 1, '2026-01-01 15:00:00', 0, 9.0),
(2026023, 1, '2026-01-01 15:00:00', 1, 8.5),
(2026024, 1, '2026-01-01 15:00:00', 0, NULL),
(2026025, 1, '2026-01-01 15:00:00', 0, 8.8),
(2026026, 1, '2026-01-01 15:00:00', 0, 9.0),
(2026027, 1, '2026-01-01 15:00:00', 0, 8.5),
(2026028, 1, '2026-01-01 15:00:00', 1, NULL),
(2026029, 1, '2026-01-01 15:00:00', 0, 8.8),
(2026030, 1, '2026-01-01 15:00:00', 0, 9.0),
(2026031, 1, '2026-01-01 15:00:00', 0, 8.5),
(2026032, 1, '2026-01-01 15:00:00', 0, NULL),
(2026033, 1, '2026-01-01 15:00:00', 1, 8.8),
(2026034, 1, '2026-01-01 15:00:00', 0, 9.0),
(2026035, 1, '2026-01-01 15:00:00', 0, 8.5),
(2026036, 1, '2026-01-01 15:00:00', 0, NULL),
(2026037, 1, '2026-01-01 15:00:00', 0, 8.8),
(2026038, 1, '2026-01-01 15:00:00', 1, 9.0),
(2026039, 1, '2026-01-01 15:00:00', 0, 8.5),
(2026040, 1, '2026-01-01 15:00:00', 0, NULL),
(2026041, 1, '2026-01-01 15:00:00', 0, 8.8),
(2026042, 1, '2026-01-01 15:00:00', 0, 9.0),
(2026043, 1, '2026-01-01 15:00:00', 1, 8.5),
(2026044, 1, '2026-01-01 15:00:00', 0, NULL),
(2026045, 1, '2026-01-01 15:00:00', 0, 8.8),
(2026046, 1, '2026-01-01 15:00:00', 0, 9.0),
(2026047, 1, '2026-01-01 15:00:00', 0, 8.5),
(2026048, 1, '2026-01-01 15:00:00', 1, NULL),
(2026049, 1, '2026-01-01 15:00:00', 0, 8.8),
(2026050, 1, '2026-01-01 15:00:00', 0, 9.0),
(2026051, 2, '2026-01-02 10:20:00', 1, 8.9),
(2026052, 2, '2026-01-02 10:20:00', 0, 8.6),
(2026053, 2, '2026-01-02 10:20:00', 0, 9.1),
(2026054, 2, '2026-01-02 10:20:00', 0, NULL),
(2026055, 2, '2026-01-02 10:20:00', 0, 8.9),
(2026056, 2, '2026-01-02 10:20:00', 1, 8.6),
(2026057, 2, '2026-01-02 10:20:00', 0, 9.1),
(2026058, 2, '2026-01-02 10:20:00', 0, NULL),
(2026059, 2, '2026-01-02 10:20:00', 0, 8.9),
(2026060, 2, '2026-01-02 10:20:00', 0, 8.6),
(2026013, 2, '2026-01-02 10:20:00', 1, 9.1),
(2026014, 2, '2026-01-02 10:20:00', 0, NULL),
(2026015, 2, '2026-01-02 10:20:00', 0, 8.9),
(2026016, 2, '2026-01-02 10:20:00', 0, 8.6),
(2026017, 3, '2026-03-06 18:00:00', 1, 7.8),
(2026018, 3, '2026-03-06 18:00:00', 0, 8.1),
(2026019, 3, '2026-03-06 18:00:00', 0, 7.5),
(2026020, 3, '2026-03-06 18:00:00', 0, NULL),
(2026021, 3, '2026-03-06 18:00:00', 0, 7.8),
(2026022, 3, '2026-03-06 18:00:00', 1, 8.1),
(2026023, 3, '2026-03-06 18:00:00', 0, 7.5),
(2026024, 3, '2026-03-06 18:00:00', 0, NULL),
(2026025, 3, '2026-03-06 18:00:00', 0, 7.8),
(2026026, 3, '2026-03-06 18:00:00', 0, 8.1),
(2026027, 3, '2026-03-06 18:00:00', 1, 7.5),
(2026028, 3, '2026-03-06 18:00:00', 0, NULL),
(2026029, 3, '2026-03-06 18:00:00', 0, 7.8),
(2026030, 3, '2026-03-06 18:00:00', 0, 8.1),
(2026031, 3, '2026-03-06 18:00:00', 0, 7.5),
(2026032, 3, '2026-03-06 18:00:00', 1, NULL),
(2026033, 3, '2026-03-06 18:00:00', 0, 7.8),
(2026034, 3, '2026-03-06 18:00:00', 0, 8.1),
(2026035, 3, '2026-03-06 18:00:00', 0, 7.5),
(2026036, 3, '2026-03-06 18:00:00', 0, NULL),
(2026037, 3, '2026-03-06 18:00:00', 1, 7.8),
(2026038, 3, '2026-03-06 18:00:00', 0, 8.1),
(2026039, 3, '2026-03-06 18:00:00', 0, 7.5),
(2026040, 3, '2026-03-06 18:00:00', 0, NULL),
(2026041, 3, '2026-03-06 18:00:00', 0, 7.8),
(2026042, 3, '2026-03-06 18:00:00', 1, 8.1),
(2026043, 3, '2026-03-06 18:00:00', 0, 7.5),
(2026044, 3, '2026-03-06 18:00:00', 0, NULL),
(2026045, 3, '2026-03-06 18:00:00', 0, 7.8),
(2026046, 3, '2026-03-06 18:00:00', 0, 8.1),
(2026047, 3, '2026-03-06 18:00:00', 1, 7.5),
(2026048, 3, '2026-03-06 18:00:00', 0, NULL),
(2026049, 4, '2026-02-02 10:00:00', 1, 7.2),
(2026050, 4, '2026-02-02 10:00:00', 0, NULL),
(2026051, 5, '2026-02-03 15:00:00', 1, 8.2),
(2026052, 5, '2026-02-03 15:00:00', 0, 8.4),
(2026053, 5, '2026-02-03 15:00:00', 0, 7.9),
(2026054, 5, '2026-02-03 15:00:00', 0, NULL),
(2026055, 5, '2026-02-03 15:00:00', 0, 8.2),
(2026056, 5, '2026-02-03 15:00:00', 1, 8.4),
(2026057, 5, '2026-02-03 15:00:00', 0, 7.9),
(2026058, 5, '2026-02-03 15:00:00', 0, NULL),
(2026059, 5, '2026-02-03 15:00:00', 0, 8.2),
(2026060, 5, '2026-02-03 15:00:00', 0, 8.4),
(2026013, 5, '2026-02-03 15:00:00', 1, 7.9),
(2026014, 5, '2026-02-03 15:00:00', 0, NULL),
(2026015, 5, '2026-02-03 15:00:00', 0, 8.2),
(2026016, 5, '2026-02-03 15:00:00', 0, 8.4),
(2026017, 5, '2026-02-03 15:00:00', 0, 7.9),
(2026018, 5, '2026-02-03 15:00:00', 1, NULL),
(2026019, 5, '2026-02-03 15:00:00', 0, 8.2),
(2026020, 5, '2026-02-03 15:00:00', 0, 8.4),
(2026021, 5, '2026-02-03 15:00:00', 0, 7.9),
(2026022, 5, '2026-02-03 15:00:00', 0, NULL),
(2026023, 6, '2026-02-03 15:00:00', 1, 7.0),
(2026024, 6, '2026-02-03 15:00:00', 0, 7.4),
(2026025, 6, '2026-02-03 15:00:00', 0, NULL),
(2026026, 6, '2026-02-03 15:00:00', 0, 7.0),
(2026027, 7, '2026-03-01 17:30:00', 1, 9.2),
(2026028, 7, '2026-03-01 17:30:00', 0, 8.9),
(2026029, 7, '2026-03-01 17:30:00', 0, 9.0),
(2026030, 7, '2026-03-01 17:30:00', 0, NULL),
(2026031, 7, '2026-03-01 17:30:00', 0, 9.2),
(2026032, 7, '2026-03-01 17:30:00', 1, 8.9),
(2026033, 7, '2026-03-01 17:30:00', 0, 9.0),
(2026034, 7, '2026-03-01 17:30:00', 0, NULL),
(2026035, 7, '2026-03-01 17:30:00', 0, 9.2),
(2026036, 7, '2026-03-01 17:30:00', 0, 8.9),
(2026037, 7, '2026-03-01 17:30:00', 1, 9.0),
(2026038, 7, '2026-03-01 17:30:00', 0, NULL),
(2026039, 8, '2026-02-22 09:00:00', 1, 8.0),
(2026040, 8, '2026-02-22 09:00:00', 0, 8.3),
(2026041, 8, '2026-02-22 09:00:00', 0, 7.8),
(2026042, 8, '2026-02-22 09:00:00', 0, NULL),
(2026043, 8, '2026-02-22 09:00:00', 0, 8.0),
(2026044, 8, '2026-02-22 09:00:00', 1, 8.3),
(2026045, 8, '2026-02-22 09:00:00', 0, 7.8),
(2026046, 8, '2026-02-22 09:00:00', 0, NULL),
(2026047, 8, '2026-02-22 09:00:00', 0, 8.0),
(2026048, 8, '2026-02-22 09:00:00', 0, 8.3),
(2026049, 8, '2026-02-22 09:00:00', 1, 7.8),
(2026050, 8, '2026-02-22 09:00:00', 0, NULL),
(2026051, 8, '2026-02-22 09:00:00', 0, 8.0),
(2026052, 8, '2026-02-22 09:00:00', 0, 8.3),
(2026053, 8, '2026-02-22 09:00:00', 0, 7.8),
(2026054, 8, '2026-02-22 09:00:00', 1, NULL),
(2026055, 8, '2026-02-22 09:00:00', 0, 8.0),
(2026056, 8, '2026-02-22 09:00:00', 0, 8.3),
(2026057, 8, '2026-02-22 09:00:00', 0, 7.8),
(2026058, 8, '2026-02-22 09:00:00', 0, NULL),
(2026059, 8, '2026-02-22 09:00:00', 1, 8.0),
(2026060, 8, '2026-02-22 09:00:00', 0, 8.3),
(2026013, 8, '2026-02-22 09:00:00', 0, 7.8),
(2026014, 8, '2026-02-22 09:00:00', 0, NULL),
(2026015, 9, '2026-02-04 15:00:00', 1, 7.6),
(2026016, 9, '2026-02-04 15:00:00', 0, 7.9),
(2026017, 9, '2026-02-04 15:00:00', 0, NULL),
(2026018, 9, '2026-02-04 15:00:00', 0, 7.6),
(2026019, 9, '2026-02-04 15:00:00', 0, 7.9),
(2026020, 9, '2026-02-04 15:00:00', 1, NULL),
(2026021, 10, '2026-03-08 15:00:00', 1, 8.4),
(2026022, 10, '2026-03-08 15:00:00', 0, 8.7),
(2026023, 10, '2026-03-08 15:00:00', 0, 8.1),
(2026024, 10, '2026-03-08 15:00:00', 0, NULL),
(2026025, 10, '2026-03-08 15:00:00', 0, 8.4),
(2026026, 10, '2026-03-08 15:00:00', 1, 8.7),
(2026027, 10, '2026-03-08 15:00:00', 0, 8.1),
(2026028, 10, '2026-03-08 15:00:00', 0, NULL),
(2026029, 10, '2026-03-08 15:00:00', 0, 8.4),
(2026030, 10, '2026-03-08 15:00:00', 0, 8.7),
(2026031, 10, '2026-03-08 15:00:00', 1, 8.1),
(2026032, 10, '2026-03-08 15:00:00', 0, NULL),
(2026033, 10, '2026-03-08 15:00:00', 0, 8.4),
(2026034, 10, '2026-03-08 15:00:00', 0, 8.7),
(2026035, 10, '2026-03-08 15:00:00', 0, 8.1),
(2026036, 10, '2026-03-08 15:00:00', 1, NULL),
(2026037, 10, '2026-03-08 15:00:00', 0, 8.4),
(2026038, 10, '2026-03-08 15:00:00', 0, 8.7),
(2026039, 10, '2026-03-08 15:00:00', 0, 8.1),
(2026040, 10, '2026-03-08 15:00:00', 0, NULL),
(2026041, 10, '2026-03-08 15:00:00', 1, 8.4),
(2026042, 10, '2026-03-08 15:00:00', 0, 8.7),
(2026043, 10, '2026-03-08 15:00:00', 0, 8.1),
(2026044, 10, '2026-03-08 15:00:00', 0, NULL),
(2026045, 10, '2026-03-08 15:00:00', 0, 8.4),
(2026046, 10, '2026-03-08 15:00:00', 1, 8.7),
(2026047, 10, '2026-03-08 15:00:00', 0, 8.1),
(2026048, 10, '2026-03-08 15:00:00', 0, NULL),
(2026049, 10, '2026-03-08 15:00:00', 0, 8.4),
(2026050, 11, '2026-02-22 18:00:00', 1, 8.1),
(2026051, 11, '2026-02-22 18:00:00', 0, 8.5),
(2026052, 11, '2026-02-22 18:00:00', 0, NULL),
(2026053, 11, '2026-02-22 18:00:00', 0, 8.1),
(2026054, 11, '2026-02-22 18:00:00', 0, 8.5),
(2026055, 11, '2026-02-22 18:00:00', 1, NULL),
(2026056, 11, '2026-02-22 18:00:00', 0, 8.1),
(2026057, 11, '2026-02-22 18:00:00', 0, 8.5);


-- ----------------------------
-- Table structure for class_equipment (课程关联器材)
-- ----------------------------
DROP TABLE IF EXISTS `class_equipment`;
CREATE TABLE `class_equipment`  (
  `class_id` int NOT NULL COMMENT '课程ID',
  `equipment_id` int NOT NULL COMMENT '器材ID',
  PRIMARY KEY (`class_id`, `equipment_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci;



-- ----------------------------
-- Table structure for member
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member`  (
  `member_id` int NOT NULL COMMENT '会员账号',
  `member_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '123456' COMMENT '会员密码',
  `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员姓名',
  `gender` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '性别',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `phone` varchar(20) NULL DEFAULT NULL COMMENT '电话',
  `register_date` date NULL DEFAULT NULL COMMENT '办卡时间',
  `total_class_count` int NULL DEFAULT NULL COMMENT '购买课时',
  `remain_class_count` int NULL DEFAULT NULL COMMENT '剩余课时',
  PRIMARY KEY (`member_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ----------------------------
-- Records of member
-- ----------------------------
INSERT INTO `member` VALUES (2026001, '123456', '张三', '女', 24, '19900000004', '2025-06-05', 40, 40);
INSERT INTO `member` VALUES (2026002, '123456', '李四', '男', 31, '19900000002', '2026-01-01', 50, 50);
INSERT INTO `member` VALUES (2026003, '123456', '王五', '男', 31, '19900000003', '2026-01-01', 40, 40);
INSERT INTO `member` VALUES (2026004, '123456', '马六', '女', 23, '19900000001', '2026-01-16', 30, 30);
INSERT INTO `member` VALUES (2026005, '123456', 'Tom', '男', 24, '19900000006', '2026-02-26', 30, 30);
INSERT INTO `member` VALUES (2026006, '123456', 'Tylor', '女', 19, '19900000009', '2026-02-27', 30, 30);
INSERT INTO `member` VALUES (2026007, '123456', 'Jack', '男', 33, '19900000007', '2026-02-27', 30, 30);
INSERT INTO `member` VALUES (2026008, '123456', 'Mike', '男', 36, '19900000010', '2026-02-28', 30, 30);
INSERT INTO `member` VALUES (2026009, '123456', 'Emma', '女', 25, '19900000008', '2026-03-01', 50, 50);
INSERT INTO `member` VALUES (2026010, '123456', 'Ava', '女', 28, '19900000005', '2026-03-02', 30, 30);
INSERT INTO `member` VALUES (2026011, '123456', 'Chloe', '女', 27, '19900000059', '2026-03-23', 30, 30);
INSERT INTO `member` VALUES (2026012, '123456', 'Lily', '女', 25, '19900000060', '2026-03-27', 30, 30);
INSERT INTO `member` VALUES (2026013, '123456', '会员13', '女', 33, '19900000011', '2026-02-14', 40, 40);
INSERT INTO `member` VALUES (2026014, '123456', '会员14', '男', 34, '19900000012', '2026-03-15', 50, 50);
INSERT INTO `member` VALUES (2026015, '123456', '会员15', '女', 35, '19900000013', '2026-04-16', 60, 60);
INSERT INTO `member` VALUES (2026016, '123456', '会员16', '男', 36, '19900000014', '2026-05-17', 30, 30);
INSERT INTO `member` VALUES (2026017, '123456', '会员17', '女', 37, '19900000015', '2026-06-18', 40, 40);
INSERT INTO `member` VALUES (2026018, '123456', '会员18', '男', 38, '19900000016', '2026-01-19', 50, 50);
INSERT INTO `member` VALUES (2026019, '123456', '会员19', '女', 39, '19900000017', '2026-02-20', 60, 60);
INSERT INTO `member` VALUES (2026020, '123456', '会员20', '男', 40, '19900000018', '2026-03-21', 30, 30);
INSERT INTO `member` VALUES (2026021, '123456', '会员21', '女', 41, '19900000019', '2026-04-22', 40, 40);
INSERT INTO `member` VALUES (2026022, '123456', '会员22', '男', 42, '19900000020', '2026-05-23', 50, 50);
INSERT INTO `member` VALUES (2026023, '123456', '会员23', '女', 43, '19900000021', '2026-06-24', 60, 60);
INSERT INTO `member` VALUES (2026024, '123456', '会员24', '男', 20, '19900000022', '2026-01-25', 30, 30);
INSERT INTO `member` VALUES (2026025, '123456', '会员25', '女', 21, '19900000023', '2026-02-26', 40, 40);
INSERT INTO `member` VALUES (2026026, '123456', '会员26', '男', 22, '19900000024', '2026-03-01', 50, 50);
INSERT INTO `member` VALUES (2026027, '123456', '会员27', '女', 23, '19900000025', '2026-04-02', 60, 60);
INSERT INTO `member` VALUES (2026028, '123456', '会员28', '男', 24, '19900000026', '2026-05-03', 30, 30);
INSERT INTO `member` VALUES (2026029, '123456', '会员29', '女', 25, '19900000027', '2026-06-04', 40, 40);
INSERT INTO `member` VALUES (2026030, '123456', '会员30', '男', 26, '19900000028', '2026-01-05', 50, 50);
INSERT INTO `member` VALUES (2026031, '123456', '会员31', '女', 27, '19900000029', '2026-02-06', 60, 60);
INSERT INTO `member` VALUES (2026032, '123456', '会员32', '男', 28, '19900000030', '2026-03-07', 30, 30);
INSERT INTO `member` VALUES (2026033, '123456', '会员33', '女', 29, '19900000031', '2026-04-08', 40, 40);
INSERT INTO `member` VALUES (2026034, '123456', '会员34', '男', 30, '19900000032', '2026-05-09', 50, 50);
INSERT INTO `member` VALUES (2026035, '123456', '会员35', '女', 31, '19900000033', '2026-06-10', 60, 60);
INSERT INTO `member` VALUES (2026036, '123456', '会员36', '男', 32, '19900000034', '2026-01-11', 30, 30);
INSERT INTO `member` VALUES (2026037, '123456', '会员37', '女', 33, '19900000035', '2026-02-12', 40, 40);
INSERT INTO `member` VALUES (2026038, '123456', '会员38', '男', 34, '19900000036', '2026-03-13', 50, 50);
INSERT INTO `member` VALUES (2026039, '123456', '会员39', '女', 35, '19900000037', '2026-04-14', 60, 60);
INSERT INTO `member` VALUES (2026040, '123456', '会员40', '男', 36, '19900000038', '2026-05-15', 30, 30);
INSERT INTO `member` VALUES (2026041, '123456', '会员41', '女', 37, '19900000039', '2026-06-16', 40, 40);
INSERT INTO `member` VALUES (2026042, '123456', '会员42', '男', 38, '19900000040', '2026-01-17', 50, 50);
INSERT INTO `member` VALUES (2026043, '123456', '会员43', '女', 39, '19900000041', '2026-02-18', 60, 60);
INSERT INTO `member` VALUES (2026044, '123456', '会员44', '男', 40, '19900000042', '2026-03-19', 30, 30);
INSERT INTO `member` VALUES (2026045, '123456', '会员45', '女', 41, '19900000043', '2026-04-20', 40, 40);
INSERT INTO `member` VALUES (2026046, '123456', '会员46', '男', 42, '19900000044', '2026-05-21', 50, 50);
INSERT INTO `member` VALUES (2026047, '123456', '会员47', '女', 43, '19900000045', '2026-06-22', 60, 60);
INSERT INTO `member` VALUES (2026048, '123456', '会员48', '男', 20, '19900000046', '2026-01-23', 30, 30);
INSERT INTO `member` VALUES (2026049, '123456', '会员49', '女', 21, '19900000047', '2026-02-24', 40, 40);
INSERT INTO `member` VALUES (2026050, '123456', '会员50', '男', 22, '19900000048', '2026-03-25', 50, 50);
INSERT INTO `member` VALUES (2026051, '123456', '会员51', '女', 23, '19900000049', '2026-04-26', 60, 60);
INSERT INTO `member` VALUES (2026052, '123456', '会员52', '男', 24, '19900000050', '2026-05-01', 30, 30);
INSERT INTO `member` VALUES (2026053, '123456', '会员53', '女', 25, '19900000051', '2026-06-02', 40, 40);
INSERT INTO `member` VALUES (2026054, '123456', '会员54', '男', 26, '19900000052', '2026-01-03', 50, 50);
INSERT INTO `member` VALUES (2026055, '123456', '会员55', '女', 27, '19900000053', '2026-02-04', 60, 60);
INSERT INTO `member` VALUES (2026056, '123456', '会员56', '男', 28, '19900000054', '2026-03-05', 30, 30);
INSERT INTO `member` VALUES (2026057, '123456', '会员57', '女', 29, '19900000055', '2026-04-06', 40, 40);
INSERT INTO `member` VALUES (2026058, '123456', '会员58', '男', 30, '19900000056', '2026-05-07', 50, 50);
INSERT INTO `member` VALUES (2026059, '123456', '会员59', '女', 31, '19900000057', '2026-06-08', 60, 60);
INSERT INTO `member` VALUES (2026060, '123456', '会员60', '男', 32, '19900000058', '2026-01-09', 30, 30);

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `employee_id` int NOT NULL COMMENT '员工账号',
  `employee_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '员工姓名',
  `gender` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `entry_date` date NULL DEFAULT NULL COMMENT '入职时间',
  `position` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职务',
  PRIMARY KEY (`employee_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES (101038721, '教练1', '女', 26, '2026-06-29', '健身教练');
INSERT INTO `employee` VALUES (101068283, '教练2', '男', 34, '2025-01-06', '健身教练');
INSERT INTO `employee` VALUES (101053687, '教练3', '男', 30, '2025-06-06', '健身教练');
INSERT INTO `employee` VALUES (101045354, '教练4', '男', 24, '2026-01-07', '健身教练');
INSERT INTO `employee` VALUES (101058973, '保洁1', '女', 48, '2024-08-24', '保洁员');
INSERT INTO `employee` VALUES (101034208, '保洁2', '女', 48, '2025-08-01', '保洁员');

-- ----------------------------
-- Table structure for equipment
-- ----------------------------
DROP TABLE IF EXISTS `equipment`;
CREATE TABLE `equipment`  (
  `equipment_id` int NOT NULL AUTO_INCREMENT COMMENT '器材id',
  `equipment_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '器材名称',
  `equipment_location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '器材位置',
  `equipment_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '器材状态',
  `equipment_message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '器材备注信息',
  PRIMARY KEY (`equipment_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of equipment
-- ----------------------------
INSERT INTO `equipment` VALUES (1, '哑铃1', '1号房间', '正常', '');
INSERT INTO `equipment` VALUES (2, '杠铃1', '2号房间', '损坏', '待维修');
INSERT INTO `equipment` VALUES (3, '跑步机1', '2号房间', '维修中', '联系厂家维修');
INSERT INTO `equipment` VALUES (4, '跑步机2', '2号房间', '正常', '');
INSERT INTO `equipment` VALUES (5, '跑步机3', '2号房间', '正常', '');
INSERT INTO `equipment` VALUES (6, '杠铃1', '1号房间', '正常', '');
INSERT INTO `equipment` VALUES (7, '杠铃2', '1号房间', '正常', '');
-- ----------------------------
-- Records of class_equipment
-- ----------------------------
INSERT INTO `class_equipment` VALUES (1, 1);
INSERT INTO `class_equipment` VALUES (1, 2);
INSERT INTO `class_equipment` VALUES (2, 3);
INSERT INTO `class_equipment` VALUES (3, 4);
INSERT INTO `class_equipment` VALUES (4, 5);
INSERT INTO `class_equipment` VALUES (5, 1);
INSERT INTO `class_equipment` VALUES (5, 2);
INSERT INTO `class_equipment` VALUES (6, 6);
INSERT INTO `class_equipment` VALUES (7, 7);
INSERT INTO `class_equipment` VALUES (8, 3);

-- ----------------------------
-- Indexes for query and analysis performance
-- ----------------------------
ALTER TABLE `class_record` ADD UNIQUE INDEX `uk_cr_member_class` (`member_id`, `class_id`);
ALTER TABLE `class_record` ADD INDEX `idx_cr_member_id` (`member_id`);
ALTER TABLE `class_record` ADD INDEX `idx_cr_class_id` (`class_id`);
ALTER TABLE `class_record` ADD INDEX `idx_cr_rating` (`rating`);
ALTER TABLE `class_record` ADD INDEX `idx_cr_class_rating` (`class_id`, `rating`);
ALTER TABLE `class_record` ADD INDEX `idx_class_record_join_time_record` (`join_time` DESC, `record_id` DESC);
ALTER TABLE `class_record` ADD INDEX `idx_class_record_class_time_record` (`class_id`, `join_time` DESC, `record_id` DESC);
ALTER TABLE `class_record` ADD INDEX `idx_class_record_status_time_record` (`status`, `join_time` DESC, `record_id` DESC);
ALTER TABLE `class_record` ADD INDEX `idx_class_record_rating_time_record` (`rating`, `join_time` DESC, `record_id` DESC);
ALTER TABLE `class_table` ADD INDEX `idx_class_coach_id` (`coach_id`);
ALTER TABLE `class_equipment` ADD INDEX `idx_ce_equipment_id` (`equipment_id`);
ALTER TABLE `member` ADD INDEX `idx_member_name` (`member_name`);
ALTER TABLE `employee` ADD INDEX `idx_employee_position` (`position`);
ALTER TABLE `equipment` ADD INDEX `idx_equipment_name` (`equipment_name`);

-- ----------------------------
-- Foreign key constraints for table relationships
-- ----------------------------
ALTER TABLE `class_record`
  ADD CONSTRAINT `fk_class_record_member`
  FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
  ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `class_record`
  ADD CONSTRAINT `fk_class_record_class`
  FOREIGN KEY (`class_id`) REFERENCES `class_table` (`class_id`)
  ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `class_table`
  ADD CONSTRAINT `fk_class_table_coach`
  FOREIGN KEY (`coach_id`) REFERENCES `employee` (`employee_id`)
  ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `class_equipment`
  ADD CONSTRAINT `fk_class_equipment_class`
  FOREIGN KEY (`class_id`) REFERENCES `class_table` (`class_id`)
  ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `class_equipment`
  ADD CONSTRAINT `fk_class_equipment_equipment`
  FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`equipment_id`)
  ON DELETE CASCADE ON UPDATE CASCADE;

-- ----------------------------
-- Views for course analysis and rating details
-- ----------------------------
CREATE OR REPLACE VIEW `v_course_operation_summary` AS
SELECT ct.class_id AS classId,
       ct.class_name AS className,
       e.employee_name AS coach,
       COUNT(cr.record_id) AS enrolledCount,
       ct.max_capacity AS maxCapacity,
       CASE
           WHEN ct.max_capacity IS NULL OR ct.max_capacity = 0 THEN 0
           ELSE ROUND(COUNT(cr.record_id) * 100.0 / NULLIF(ct.max_capacity, 0), 1)
       END AS capacityUsage,
       ROUND(AVG(cr.rating), 1) AS averageRating,
       SUM(CASE WHEN cr.record_id IS NOT NULL AND cr.rating IS NULL THEN 1 ELSE 0 END) AS unratedCount
FROM `class_table` ct
LEFT JOIN `class_record` cr ON ct.class_id = cr.class_id
LEFT JOIN `employee` e ON ct.coach_id = e.employee_id
GROUP BY ct.class_id, ct.class_name, e.employee_name, ct.max_capacity;

CREATE OR REPLACE VIEW `v_member_course_rating` AS
SELECT m.member_id,
       m.member_name,
       m.gender,
       ct.class_id,
       ct.class_name,
       cr.record_id,
       cr.join_time,
       cr.status,
       cr.rating
FROM `member` m
JOIN `class_record` cr ON m.member_id = cr.member_id
JOIN `class_table` ct ON cr.class_id = ct.class_id
WHERE cr.status = 1
  AND cr.rating IS NOT NULL;

-- ----------------------------
-- Triggers for maintaining remaining class count
-- ----------------------------
DELIMITER $$

DROP TRIGGER IF EXISTS `trg_class_record_insert`$$
CREATE TRIGGER `trg_class_record_insert`
AFTER INSERT ON `class_record`
FOR EACH ROW
BEGIN
    UPDATE `member`
    SET remain_class_count = remain_class_count - 1
    WHERE member_id = NEW.member_id;
END$$

DROP TRIGGER IF EXISTS `trg_class_record_delete`$$
CREATE TRIGGER `trg_class_record_delete`
AFTER DELETE ON `class_record`
FOR EACH ROW
BEGIN
    UPDATE `member`
    SET remain_class_count = remain_class_count + 1
    WHERE member_id = OLD.member_id;
END$$

-- ----------------------------
-- Stored procedures for member course statistics
-- ----------------------------
DROP PROCEDURE IF EXISTS `GetMemberStats`$$
CREATE PROCEDURE `GetMemberStats`(IN mid INT)
BEGIN
    SELECT m.member_name,
           COUNT(cr.record_id) AS course_count,
           ROUND(IFNULL(AVG(cr.rating), 0), 1) AS avg_rating,
           (SELECT ct.class_name
            FROM class_record cr2
            JOIN class_table ct ON cr2.class_id = ct.class_id
            WHERE cr2.member_id = mid AND cr2.rating IS NOT NULL
            ORDER BY cr2.rating DESC
            LIMIT 1) AS favorite_course
    FROM member m
    LEFT JOIN class_record cr ON m.member_id = cr.member_id AND cr.rating IS NOT NULL
    WHERE m.member_id = mid
    GROUP BY m.member_id, m.member_name;
END$$

DROP PROCEDURE IF EXISTS `CalcMemberActivity`$$
CREATE PROCEDURE `CalcMemberActivity`()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE mid INT;
    DECLARE cnt INT;
    DECLARE avg_r DECIMAL(5,1);
    DECLARE cur CURSOR FOR
        SELECT cr.member_id, COUNT(*), IFNULL(AVG(cr.rating), 0)
        FROM class_record cr
        WHERE cr.rating IS NOT NULL
        GROUP BY cr.member_id;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    DROP TEMPORARY TABLE IF EXISTS tmp_member_activity;
    CREATE TEMPORARY TABLE tmp_member_activity (
        member_id INT,
        member_name VARCHAR(255),
        activity DECIMAL(10,1)
    );

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO mid, cnt, avg_r;
        IF done THEN
            LEAVE read_loop;
        END IF;

        INSERT INTO tmp_member_activity
        SELECT mid, m.member_name, cnt * 10 + avg_r * 10
        FROM member m
        WHERE m.member_id = mid;
    END LOOP;
    CLOSE cur;

    SELECT * FROM tmp_member_activity ORDER BY activity DESC;
    DROP TEMPORARY TABLE IF EXISTS tmp_member_activity;
END$$

DELIMITER ;

SET FOREIGN_KEY_CHECKS = 1;
