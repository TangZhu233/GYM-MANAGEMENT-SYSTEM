-- 会员身高/体重列：与管理员端/用户端表单 & 权威脚本 gym_management_system.sql 同步
-- 说明：此前 member 表无 height/weight 列，表单必填却无法落库；现补列并接入增删改查。
ALTER TABLE `member`
    ADD COLUMN `height` int NULL DEFAULT NULL COMMENT '身高(cm)' AFTER `age`,
    ADD COLUMN `weight` int NULL DEFAULT NULL COMMENT '体重(kg)' AFTER `height`;
