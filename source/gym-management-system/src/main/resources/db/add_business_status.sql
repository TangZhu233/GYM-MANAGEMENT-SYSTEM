-- 营业状态表：单行持久化（1=营业中 0=打烊）。全站提示用，不拦截线上操作。
-- 与权威脚本 gym_management_system.sql 同步；Run once（幂等：IF NOT EXISTS + 无则插入）。

CREATE TABLE IF NOT EXISTS `business_status`  (
  `id` int NOT NULL DEFAULT 1 COMMENT '主键(固定1)',
  `status` int NOT NULL DEFAULT 1 COMMENT '营业状态 1营业中 0打烊',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact COMMENT '营业状态';

INSERT INTO `business_status` (`id`, `status`)
SELECT 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `business_status` WHERE `id` = 1);
