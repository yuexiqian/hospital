/*
 Navicat Premium Dump SQL

 Source Server         : aa
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : hospital_db

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 16/12/2025 21:28:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bill
-- ----------------------------
DROP TABLE IF EXISTS `bill`;
CREATE TABLE `bill`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `patient_id` bigint UNSIGNED NOT NULL COMMENT '就诊人ID',
  `item_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '费用项目名称，如门诊药品费、CT检查费',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '费用类型：DRUG/EXAM/LAB/REGISTER/OTHER',
  `dept_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '科室名称',
  `doctor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '医生名称',
  `amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '费用总金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'UNPAID' COMMENT 'UNPAID/PAID',
  `pay_method` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式：SELF_SERVICE/CASH/WECHAT/ALIPAY等',
  `create_time` datetime NULL DEFAULT NULL COMMENT '费用产生时间',
  `paid_time` datetime NULL DEFAULT NULL COMMENT '费用支付时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_bill_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_bill_patient`(`patient_id` ASC) USING BTREE,
  INDEX `idx_bill_status`(`status` ASC) USING BTREE,
  INDEX `idx_bill_user_status`(`user_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '费用账单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bill
-- ----------------------------
INSERT INTO `bill` VALUES (1, 1, 1, '门诊药品费', 'DRUG', '呼吸内科', '张三', 44.60, 'PAID', 'SELF_SERVICE', '2025-12-07 10:50:49', '2025-12-07 11:11:53');
INSERT INTO `bill` VALUES (2, 6, 6, '处方缴费（处方ID:4）', 'PRESCRIPTION', '呼吸内科', '张测试', 0.00, 'PAID', 'SELF_SERVICE', '2025-12-12 15:29:05', '2025-12-12 15:29:18');
INSERT INTO `bill` VALUES (3, 8, 7, '处方缴费（处方ID:5）', 'PRESCRIPTION', NULL, NULL, 0.00, 'PAID', 'SELF_SERVICE', '2025-12-16 14:26:00', '2025-12-16 20:13:05');
INSERT INTO `bill` VALUES (4, 8, 8, '处方缴费（处方ID:6）', 'PRESCRIPTION', NULL, NULL, 148.00, 'PAID', 'SELF_SERVICE', '2025-12-16 20:15:56', '2025-12-16 20:43:11');
INSERT INTO `bill` VALUES (5, 8, 7, '处方缴费（处方ID:7）', 'PRESCRIPTION', NULL, NULL, 136.40, 'UNPAID', NULL, '2025-12-16 20:44:30', NULL);
INSERT INTO `bill` VALUES (6, 8, 8, '处方缴费（处方ID:8）', 'PRESCRIPTION', NULL, NULL, 9.00, 'UNPAID', NULL, '2025-12-16 20:53:23', NULL);
INSERT INTO `bill` VALUES (7, 1, 1, '处方缴费（处方ID:9）', 'PRESCRIPTION', NULL, NULL, 2.40, 'PAID', 'SELF_SERVICE', '2025-12-16 20:58:22', '2025-12-16 21:02:11');

-- ----------------------------
-- Table structure for bill_detail
-- ----------------------------
DROP TABLE IF EXISTS `bill_detail`;
CREATE TABLE `bill_detail`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `bill_id` bigint UNSIGNED NOT NULL COMMENT '所属费用ID, 对应 bill.id',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'DRUG/EXAM/LAB/OTHER',
  `item_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品名或项目名',
  `spec` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格',
  `unit_price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位：盒/瓶/次/项',
  `subtotal_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '金额小计',
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_bill_detail_bill`(`bill_id` ASC) USING BTREE,
  CONSTRAINT `fk_bill_detail_bill` FOREIGN KEY (`bill_id`) REFERENCES `bill` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '费用明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bill_detail
-- ----------------------------
INSERT INTO `bill_detail` VALUES (1, 1, 'DRUG', '阿莫西林胶囊', '0.25g*24粒/盒', 12.30, 2, '盒', 24.60, '2025-12-07 10:50:49');
INSERT INTO `bill_detail` VALUES (2, 1, 'DRUG', '蒲地蓝消炎口服液', '10ml*6支/盒', 20.00, 1, '盒', 20.00, '2025-12-07 10:50:49');
INSERT INTO `bill_detail` VALUES (3, 2, 'DRUG', '盐酸氨溴索片', '30mg*20片/盒', 0.00, 1, '盒', 0.00, NULL);
INSERT INTO `bill_detail` VALUES (4, 3, 'DRUG', '盐酸氨溴索片', '30mg*20片/盒', 0.00, 15, '盒', 0.00, NULL);
INSERT INTO `bill_detail` VALUES (5, 3, 'DRUG', '阿莫西林胶囊', '0.5g*24粒/盒', 0.00, 9, '盒', 0.00, NULL);
INSERT INTO `bill_detail` VALUES (6, 4, 'DRUG', '盐酸氨溴索片', '30mg*20片/盒', 1.80, 10, '盒', 18.00, NULL);
INSERT INTO `bill_detail` VALUES (7, 4, 'DRUG', '布地奈德气雾剂', '200μg*200揿/支', 65.00, 2, '盒', 130.00, NULL);
INSERT INTO `bill_detail` VALUES (8, 5, 'DRUG', '阿莫西林胶囊', '0.5g*24粒/盒', 0.80, 8, '盒', 6.40, NULL);
INSERT INTO `bill_detail` VALUES (9, 5, 'DRUG', '布地奈德气雾剂', '200μg*200揿/支', 65.00, 2, '盒', 130.00, NULL);
INSERT INTO `bill_detail` VALUES (10, 6, 'DRUG', '盐酸氨溴索片', '30mg*20片/盒', 1.80, 5, '盒', 9.00, NULL);
INSERT INTO `bill_detail` VALUES (11, 7, 'DRUG', '阿莫西林胶囊', '0.5g*24粒/盒', 0.80, 3, '盒', 2.40, NULL);

-- ----------------------------
-- Table structure for dept
-- ----------------------------
DROP TABLE IF EXISTS `dept`;
CREATE TABLE `dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '科室ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '科室名称，例如：呼吸内科',
  `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '科室编码，如 NEU, PED 等',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型：门诊/急诊/住院等，可选',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '门诊位置，如 门诊楼2F-203',
  `opening_hours` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '门诊时间，如 周一至周五 8:00-17:30',
  `specialty` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '科室擅长疾病/特色',
  `floor` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所在楼层或位置，如 3F 东区',
  `status` int NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '科室信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dept
-- ----------------------------
INSERT INTO `dept` VALUES (1, '呼吸内科', 'RESP', '内科', '主要负责呼吸系统疾病的诊断与治疗，如咳嗽、发热、胸闷、哮喘等。', '门诊楼2F-203', '周一至周五 8:00-17:30', '慢性咳嗽、支气管炎、哮喘、肺炎等', '3楼', 1, '2025-11-22 10:24:08');
INSERT INTO `dept` VALUES (2, '消化内科', 'GI', '内科', NULL, NULL, NULL, NULL, '3楼', 1, '2025-11-22 10:24:08');
INSERT INTO `dept` VALUES (3, '皮肤科', 'DERM', '外科', NULL, NULL, NULL, NULL, '2楼', 1, '2025-11-22 10:24:08');
INSERT INTO `dept` VALUES (4, '全科门诊', 'GP', '综合', NULL, NULL, NULL, NULL, '1楼', 1, '2025-11-22 10:24:08');
INSERT INTO `dept` VALUES (5, '神经内科', 'NEURO', '内科', NULL, NULL, NULL, NULL, '4楼', 1, '2025-11-22 10:24:08');
INSERT INTO `dept` VALUES (6, '骨科', 'ORTHO', '外科', NULL, NULL, NULL, NULL, '4楼', 1, '2025-11-22 10:24:08');
INSERT INTO `dept` VALUES (7, '心血管内科', 'CARD', '内科', '诊治心脏和血管相关疾病，如高血压、冠心病、心律失常等', '门诊楼3F-308', '周一至周六 8:00-17:30，周日急诊', '高血压、冠心病、心力衰竭、心律失常', '3楼', 1, '2025-12-06 22:56:12');
INSERT INTO `dept` VALUES (8, '内分泌科', 'ENDO', '内科', '诊治内分泌系统疾病，如糖尿病、甲状腺疾病、骨质疏松等', '门诊楼4F-405', '周一至周五 8:00-17:30', '糖尿病、甲状腺疾病、代谢综合征', '4楼', 1, '2025-12-06 22:56:12');
INSERT INTO `dept` VALUES (9, '肾内科', 'RENAL', '内科', '诊治肾脏相关疾病，如肾炎、肾衰竭、尿毒症等', '门诊楼3F-312', '周一至周五 8:30-17:00', '肾炎、肾衰竭、血液透析', '3楼', 1, '2025-12-06 22:56:12');
INSERT INTO `dept` VALUES (10, '眼科', 'OPHTH', '专科', '诊治眼部疾病，提供视力检查和眼科手术', '门诊楼2F-218', '周一至周六 8:30-17:00', '白内障、青光眼、近视手术', '2楼', 1, '2025-12-06 22:56:12');
INSERT INTO `dept` VALUES (11, '耳鼻喉科', 'ENT', '专科', '诊治耳、鼻、喉部疾病，提供听力检查和喉镜检查', '门诊楼2F-222', '周一至周五 8:00-17:30', '鼻炎、中耳炎、喉炎、听力障碍', '2楼', 1, '2025-12-06 22:56:12');
INSERT INTO `dept` VALUES (12, '儿科', 'PED', '专科', '专门诊治儿童常见病和多发病，提供儿童保健服务', '门诊楼3F-315', '周一至周日 8:00-20:00（夜间急诊）', '儿童呼吸道感染、消化道疾病、生长发育评估', '3楼', 1, '2025-12-06 23:12:26');
INSERT INTO `dept` VALUES (13, '妇产科', 'GYN', '专科', '提供女性生殖系统疾病的诊治和孕产期保健服务', '门诊楼2F-225', '周一至周六 8:00-17:30，周日急诊', '妇科炎症、妇科肿瘤、孕产期保健、计划生育', '2楼', 1, '2025-12-06 23:12:26');

-- ----------------------------
-- Table structure for dispense_record
-- ----------------------------
DROP TABLE IF EXISTS `dispense_record`;
CREATE TABLE `dispense_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `prescription_id` bigint NOT NULL COMMENT '处方ID',
  `pharmacist_id` bigint NOT NULL COMMENT '药师ID（pharmacist 表主键）',
  `pharmacist_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `dispensed_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `total_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '本次发药金额（可选）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dispense_time` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of dispense_record
-- ----------------------------
INSERT INTO `dispense_record` VALUES (1, 1, 4, 'P0001', '2025-12-11 15:50:05', 0.00, '', '2025-12-11 15:50:05', '2025-12-11 15:50:05', '2025-12-11 15:50:05.620634');
INSERT INTO `dispense_record` VALUES (2, 2, 4, 'P0001', '2025-12-11 15:58:18', 0.00, '', '2025-12-11 15:58:18', '2025-12-11 15:58:18', '2025-12-11 15:58:18.399826');

-- ----------------------------
-- Table structure for doctor
-- ----------------------------
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '医生ID',
  `user_id` bigint NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '医生姓名',
  `title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职称：主任医师、副主任、主治等',
  `dept_id` bigint NOT NULL COMMENT '所属科室ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话（可选）',
  `status` int NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gender` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `specialty` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `profile` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `schedule` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `work_days` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `daily_quota` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_doctor_user_id`(`user_id` ASC) USING BTREE,
  INDEX `fk_doctor_dept`(`dept_id` ASC) USING BTREE,
  INDEX `idx_doctor_dept`(`dept_id` ASC) USING BTREE,
  INDEX `idx_doctor_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_doctor_dept` FOREIGN KEY (`dept_id`) REFERENCES `dept` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 110 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '医生信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of doctor
-- ----------------------------
INSERT INTO `doctor` VALUES (1, NULL, '张三', '主治医师', 1, '13800000001', 1, '2025-11-22 10:24:25', 'M', '慢性咳嗽、支气管炎、哮喘等', '从事呼吸科临床工作10余年……', '周一/周三/周五 上午', 'MON,WED,FRI', 30);
INSERT INTO `doctor` VALUES (2, NULL, '李四', '副主任医师', 1, '13800000002', 1, '2025-11-22 10:24:25', 'M', '肺炎、老年呼吸系统感染等', '擅长慢性阻塞性肺疾病诊治……', '周二/周四 上午', 'TUE,THU', 20);
INSERT INTO `doctor` VALUES (3, NULL, '王五', '主任医师', 3, '13800000003', 1, '2025-11-22 10:24:25', 'M', '胃炎、消化性溃疡、胃食管反流', '主任医师，消化内科专家，擅长各类胃肠道疾病的综合诊治。', '周一至周五 全天', '1,2,3,4,5', 40);
INSERT INTO `doctor` VALUES (4, NULL, '赵六', '主治医师', 4, '13800000004', 1, '2025-11-22 10:24:25', 'F', '小儿发热、咳嗽、厌食', '儿科主治医师，擅长儿童常见呼吸道及消化道疾病的诊治。', '周日上午', '6', 15);
INSERT INTO `doctor` VALUES (5, 5, '张测试', '主治医师', 1, '13600000001', 1, '2025-12-11 10:18:35', 'F', '内科', '门诊测试医生账号', '8:00-11:30', '1,2,3,4,5', 50);
INSERT INTO `doctor` VALUES (6, NULL, '陈华', '主任医师', 1, '13800001111', 1, '2025-12-06 22:56:12', 'M', '慢性阻塞性肺疾病、哮喘', '呼吸内科主任，从事呼吸科临床工作20余年，擅长重症呼吸系统疾病的诊治', '周一/周三/周五 上午', 'MON,WED,FRI', 25);
INSERT INTO `doctor` VALUES (7, NULL, '林芳', '主任医师', 1, '13800001112', 1, '2025-12-06 22:56:12', 'F', '肺部感染、支气管扩张', '医学博士，长期从事呼吸系统疾病的研究和临床工作，经验丰富', '周二/周四 全天', 'TUE,THU', 30);
INSERT INTO `doctor` VALUES (8, NULL, '王建国', '主任医师', 1, '13800001113', 1, '2025-12-06 22:56:12', 'M', '肺癌早期诊断、呼吸内镜', '呼吸内镜专家，擅长肺部肿瘤的早期诊断和治疗', '周一至周五 下午', '1,2,3,4,5', 20);
INSERT INTO `doctor` VALUES (9, NULL, '张敏', '副主任医师', 1, '13800001114', 1, '2025-12-06 22:56:12', 'F', '哮喘、慢性咳嗽', '副主任医师，擅长儿童及成人哮喘的综合治疗', '周一/周三/周五 全天', 'MON,WED,FRI', 30);
INSERT INTO `doctor` VALUES (10, NULL, '李伟', '副主任医师', 1, '13800001115', 1, '2025-12-06 22:56:12', 'M', '肺炎、呼吸衰竭', '呼吸重症监护室负责人，擅长危重呼吸系统疾病的救治', '周二/周四 上午', 'TUE,THU', 25);
INSERT INTO `doctor` VALUES (11, NULL, '赵琳', '副主任医师', 1, '13800001116', 1, '2025-12-06 22:56:12', 'F', '肺功能检查、戒烟指导', '肺功能室主任，擅长肺功能评估和呼吸康复治疗', '周一至周五 上午', '1,2,3,4,5', 35);
INSERT INTO `doctor` VALUES (12, NULL, '刘强', '副主任医师', 1, '13800001117', 1, '2025-12-06 22:56:12', 'M', '支气管炎、胸膜疾病', '副主任医师，擅长胸膜疾病的诊断和治疗', '周二/周四/周六 上午', 'TUE,THU,SAT', 30);
INSERT INTO `doctor` VALUES (13, NULL, '孙丽', '副主任医师', 1, '13800001118', 1, '2025-12-06 22:56:12', 'F', '睡眠呼吸暂停、鼾症', '睡眠呼吸疾病专家，擅长睡眠呼吸障碍的诊治', '周三/周五/周日 上午', 'WED,FRI,SUN', 20);
INSERT INTO `doctor` VALUES (14, NULL, '周明', '主任医师', 2, '13800002111', 1, '2025-12-06 22:56:12', 'M', '胃溃疡、胃癌早期诊断', '消化内科主任，内镜专家，擅长消化道肿瘤的早期诊断', '周一/周三/周五 上午', 'MON,WED,FRI', 25);
INSERT INTO `doctor` VALUES (15, NULL, '吴芳', '主任医师', 2, '13800002112', 1, '2025-12-06 22:56:12', 'F', '炎症性肠病、结肠炎', '医学博士，长期从事炎症性肠病的临床研究', '周二/周四 全天', 'TUE,THU', 30);
INSERT INTO `doctor` VALUES (16, NULL, '郑强', '主任医师', 2, '13800002113', 1, '2025-12-06 22:56:12', 'M', '肝病、肝硬化', '肝病专家，擅长各类肝脏疾病的诊断和治疗', '周一至周五 下午', '1,2,3,4,5', 20);
INSERT INTO `doctor` VALUES (17, NULL, '黄静', '副主任医师', 2, '13800002114', 1, '2025-12-06 22:56:12', 'F', '胃炎、消化不良', '副主任医师，擅长功能性胃肠病的诊治', '周一/周三/周五 全天', 'MON,WED,FRI', 30);
INSERT INTO `doctor` VALUES (18, NULL, '钱伟', '副主任医师', 2, '13800002115', 1, '2025-12-06 22:56:12', 'M', '胰腺炎、胆道疾病', '副主任医师，擅长胰腺和胆道疾病的治疗', '周二/周四 上午', 'TUE,THU', 25);
INSERT INTO `doctor` VALUES (19, NULL, '周琳', '副主任医师', 2, '13800002116', 1, '2025-12-06 22:56:12', 'F', '肠易激综合征、便秘', '胃肠功能性疾病专家，擅长便秘的综合治疗', '周一至周五 上午', '1,2,3,4,5', 35);
INSERT INTO `doctor` VALUES (20, NULL, '孙明', '副主任医师', 2, '13800002117', 1, '2025-12-06 22:56:12', 'M', '胃食管反流、Barrett食管', '副主任医师，擅长食管疾病的诊断和治疗', '周二/周四/周六 上午', 'TUE,THU,SAT', 30);
INSERT INTO `doctor` VALUES (21, NULL, '李娜', '副主任医师', 2, '13800002118', 1, '2025-12-06 22:56:12', 'F', '幽门螺杆菌感染、胃镜检查', '内镜专家，擅长幽门螺杆菌的根除治疗', '周三/周五/周日 上午', 'WED,FRI,SUN', 20);
INSERT INTO `doctor` VALUES (22, NULL, '马华', '主任医师', 3, '13800003111', 1, '2025-12-06 22:56:12', 'M', '银屑病、红斑狼疮', '皮肤科主任，擅长自身免疫性皮肤病的诊治', '周一/周三/周五 上午', 'MON,WED,FRI', 25);
INSERT INTO `doctor` VALUES (23, NULL, '朱芳', '主任医师', 3, '13800003112', 1, '2025-12-06 22:56:12', 'F', '痤疮、玫瑰痤疮', '医学博士，擅长痤疮及相关皮肤病的综合治疗', '周二/周四 全天', 'TUE,THU', 30);
INSERT INTO `doctor` VALUES (24, NULL, '秦伟', '主任医师', 3, '13800003113', 1, '2025-12-06 22:56:12', 'M', '皮肤肿瘤、皮肤外科', '皮肤外科专家，擅长皮肤肿瘤的手术治疗', '周一至周五 下午', '1,2,3,4,5', 20);
INSERT INTO `doctor` VALUES (25, NULL, '冯静', '副主任医师', 3, '13800003114', 1, '2025-12-06 22:56:12', 'F', '湿疹、皮炎', '副主任医师，擅长过敏性皮肤病的诊治', '周一/周三/周五 全天', 'MON,WED,FRI', 30);
INSERT INTO `doctor` VALUES (26, NULL, '陈伟', '副主任医师', 3, '13800003115', 1, '2025-12-06 22:56:12', 'M', '白癜风、色素性疾病', '副主任医师，擅长色素性皮肤病的治疗', '周二/周四 上午', 'TUE,THU', 25);
INSERT INTO `doctor` VALUES (27, NULL, '韩梅', '副主任医师', 3, '13800003116', 1, '2025-12-06 22:56:12', 'F', '脱发、毛发疾病', '毛发疾病专家，擅长脱发的综合治疗', '周一至周五 上午', '1,2,3,4,5', 35);
INSERT INTO `doctor` VALUES (28, NULL, '何强', '副主任医师', 3, '13800003117', 1, '2025-12-06 22:56:12', 'M', '性病、皮肤感染', '副主任医师，擅长感染性皮肤病的诊治', '周二/周四/周六 上午', 'TUE,THU,SAT', 30);
INSERT INTO `doctor` VALUES (29, NULL, '高娜', '副主任医师', 3, '13800003118', 1, '2025-12-06 22:56:12', 'F', '激光美容、皮肤年轻化', '医学美容专家，擅长激光治疗各种皮肤问题', '周三/周五/周日 上午', 'WED,FRI,SUN', 20);
INSERT INTO `doctor` VALUES (30, NULL, '宋华', '主任医师', 4, '13800004111', 1, '2025-12-06 22:56:12', 'M', '慢性病管理、健康评估', '全科医学科主任，擅长多系统疾病的综合管理', '周一/周三/周五 上午', 'MON,WED,FRI', 40);
INSERT INTO `doctor` VALUES (31, NULL, '唐芳', '主任医师', 4, '13800004112', 1, '2025-12-06 22:56:12', 'F', '老年病、多重用药管理', '老年医学专家，擅长老年人常见病的综合治疗', '周二/周四 全天', 'TUE,THU', 45);
INSERT INTO `doctor` VALUES (32, NULL, '许强', '主任医师', 4, '13800004113', 1, '2025-12-06 22:56:12', 'M', '体检报告解读、健康咨询', '健康管理专家，擅长健康风险评估和预防指导', '周一至周五 下午', '1,2,3,4,5', 35);
INSERT INTO `doctor` VALUES (33, NULL, '魏静', '副主任医师', 4, '13800004114', 1, '2025-12-06 22:56:12', 'F', '常见病诊疗、疫苗接种', '副主任医师，擅长常见呼吸道和消化道疾病的治疗', '周一/周三/周五 全天', 'MON,WED,FRI', 50);
INSERT INTO `doctor` VALUES (34, NULL, '谢伟', '副主任医师', 4, '13800004115', 1, '2025-12-06 22:56:12', 'M', '高血压、糖尿病管理', '慢性病管理专家，擅长三高的长期管理', '周二/周四 上午', 'TUE,THU', 40);
INSERT INTO `doctor` VALUES (35, NULL, '曹梅', '副主任医师', 4, '13800004116', 1, '2025-12-06 22:56:12', 'F', '儿科常见病、儿童保健', '儿科全科医生，擅长儿童常见病的诊治', '周一至周五 上午', '1,2,3,4,5', 45);
INSERT INTO `doctor` VALUES (36, NULL, '苏强', '副主任医师', 4, '13800004117', 1, '2025-12-06 22:56:12', 'M', '急诊处理、外伤处理', '急诊全科医生，擅长急症的初步处理', '周二/周四/周六 上午', 'TUE,THU,SAT', 50);
INSERT INTO `doctor` VALUES (37, NULL, '吕娜', '副主任医师', 4, '13800004118', 1, '2025-12-06 22:56:12', 'F', '妇科常见病、孕前咨询', '女性健康专家，擅长女性常见病的诊治', '周三/周五/周日 上午', 'WED,FRI,SUN', 40);
INSERT INTO `doctor` VALUES (38, NULL, '董华', '主任医师', 5, '13800005111', 1, '2025-12-06 22:56:12', 'M', '脑血管病、中风防治', '神经内科主任，擅长脑血管疾病的防治', '周一/周三/周五 上午', 'MON,WED,FRI', 25);
INSERT INTO `doctor` VALUES (39, NULL, '姜芳', '主任医师', 5, '13800005112', 1, '2025-12-06 22:56:12', 'F', '帕金森病、运动障碍', '运动障碍疾病专家，擅长帕金森病的综合治疗', '周二/周四 全天', 'TUE,THU', 30);
INSERT INTO `doctor` VALUES (40, NULL, '沈强', '主任医师', 5, '13800005113', 1, '2025-12-06 22:56:12', 'M', '癫痫、脑电图', '癫痫专家，擅长难治性癫痫的诊断和治疗', '周一至周五 下午', '1,2,3,4,5', 20);
INSERT INTO `doctor` VALUES (41, NULL, '范静', '副主任医师', 5, '13800005114', 1, '2025-12-06 22:56:12', 'F', '头痛、偏头痛', '头痛专家，擅长各种头痛的诊断和治疗', '周一/周三/周五 全天', 'MON,WED,FRI', 30);
INSERT INTO `doctor` VALUES (42, NULL, '彭伟', '副主任医师', 5, '13800005115', 1, '2025-12-06 22:56:12', 'M', '睡眠障碍、失眠', '睡眠医学专家，擅长失眠的综合治疗', '周二/周四 上午', 'TUE,THU', 25);
INSERT INTO `doctor` VALUES (43, NULL, '严梅', '副主任医师', 5, '13800005116', 1, '2025-12-06 22:56:12', 'F', '眩晕、平衡障碍', '眩晕专家，擅长前庭功能障碍的诊治', '周一至周五 上午', '1,2,3,4,5', 35);
INSERT INTO `doctor` VALUES (44, NULL, '于强', '副主任医师', 5, '13800005117', 1, '2025-12-06 22:56:12', 'M', '神经免疫疾病、多发性硬化', '副主任医师，擅长神经免疫性疾病的治疗', '周二/周四/周六 上午', 'TUE,THU,SAT', 30);
INSERT INTO `doctor` VALUES (45, NULL, '钟娜', '副主任医师', 5, '13800005118', 1, '2025-12-06 22:56:12', 'F', '认知障碍、痴呆', '认知障碍专家，擅长早期痴呆的诊断和干预', '周三/周五/周日 上午', 'WED,FRI,SUN', 20);
INSERT INTO `doctor` VALUES (46, NULL, '陆华', '主任医师', 6, '13800006111', 1, '2025-12-06 22:56:12', 'M', '关节置换、关节镜手术', '骨科主任，关节外科专家，擅长髋膝关节置换', '周一/周三/周五 上午', 'MON,WED,FRI', 20);
INSERT INTO `doctor` VALUES (47, NULL, '丁芳', '主任医师', 6, '13800006112', 1, '2025-12-06 22:56:12', 'F', '脊柱外科、腰椎间盘突出', '脊柱外科专家，擅长微创脊柱手术', '周二/周四 全天', 'TUE,THU', 25);
INSERT INTO `doctor` VALUES (48, NULL, '顾强', '主任医师', 6, '13800006113', 1, '2025-12-06 22:56:12', 'M', '创伤骨科、复杂骨折', '创伤骨科专家，擅长复杂骨折的手术治疗', '周一至周五 下午', '1,2,3,4,5', 15);
INSERT INTO `doctor` VALUES (49, NULL, '孟静', '副主任医师', 6, '13800006114', 1, '2025-12-06 22:56:12', 'F', '手外科、显微外科', '手外科专家，擅长断指再植和手部功能重建', '周一/周三/周五 全天', 'MON,WED,FRI', 25);
INSERT INTO `doctor` VALUES (50, NULL, '田伟', '副主任医师', 6, '13800006115', 1, '2025-12-06 22:56:12', 'M', '运动医学、关节镜', '运动医学专家，擅长膝关节镜手术', '周二/周四 上午', 'TUE,THU', 20);
INSERT INTO `doctor` VALUES (51, NULL, '江梅', '副主任医师', 6, '13800006116', 1, '2025-12-06 22:56:12', 'F', '骨质疏松、骨代谢疾病', '骨质疏松专家，擅长骨质疏松的综合治疗', '周一至周五 上午', '1,2,3,4,5', 30);
INSERT INTO `doctor` VALUES (52, NULL, '方强', '副主任医师', 6, '13800006117', 1, '2025-12-06 22:56:12', 'M', '小儿骨科、先天性畸形', '小儿骨科专家，擅长儿童骨骼畸形的矫正', '周二/周四/周六 上午', 'TUE,THU,SAT', 25);
INSERT INTO `doctor` VALUES (53, NULL, '罗娜', '副主任医师', 6, '13800006118', 1, '2025-12-06 22:56:12', 'F', '足踝外科、足部畸形', '足踝外科专家，擅长足部畸形的矫正手术', '周三/周五/周日 上午', 'WED,FRI,SUN', 20);
INSERT INTO `doctor` VALUES (54, NULL, '梁华', '主任医师', 7, '13800007111', 1, '2025-12-06 22:56:12', 'M', '冠心病、介入治疗', '心血管内科主任，擅长冠状动脉介入手术', '周一/周三/周五 上午', 'MON,WED,FRI', 20);
INSERT INTO `doctor` VALUES (55, NULL, '夏芳', '主任医师', 7, '13800007112', 1, '2025-12-06 22:56:12', 'F', '高血压、心力衰竭', '高血压专家，擅长难治性高血压的治疗', '周二/周四 全天', 'TUE,THU', 25);
INSERT INTO `doctor` VALUES (56, NULL, '徐强', '主任医师', 7, '13800007113', 1, '2025-12-06 22:56:12', 'M', '心律失常、起搏器植入', '心律失常专家，擅长心脏起搏器植入术', '周一至周五 下午', '1,2,3,4,5', 15);
INSERT INTO `doctor` VALUES (57, NULL, '汪静', '副主任医师', 7, '13800007114', 1, '2025-12-06 22:56:12', 'F', '心肌病、心脏超声', '心脏超声专家，擅长心肌病的诊断', '周一/周三/周五 全天', 'MON,WED,FRI', 25);
INSERT INTO `doctor` VALUES (58, NULL, '毛伟', '副主任医师', 7, '13800007115', 1, '2025-12-06 22:56:12', 'M', '心脏康复、二级预防', '心脏康复专家，擅长心脏病患者的康复指导', '周二/周四 上午', 'TUE,THU', 20);
INSERT INTO `doctor` VALUES (59, NULL, '邵梅', '副主任医师', 7, '13800007116', 1, '2025-12-06 22:56:12', 'F', '血脂异常、动脉硬化', '血脂专家，擅长血脂管理和动脉硬化防治', '周一至周五 上午', '1,2,3,4,5', 30);
INSERT INTO `doctor` VALUES (60, NULL, '常强', '副主任医师', 7, '13800007117', 1, '2025-12-06 22:56:12', 'M', '心脏急症、胸痛中心', '心脏急症专家，擅长急性胸痛的鉴别诊断', '周二/周四/周六 上午', 'TUE,THU,SAT', 25);
INSERT INTO `doctor` VALUES (61, NULL, '黎娜', '副主任医师', 7, '13800007118', 1, '2025-12-06 22:56:12', 'F', '女性心脏病、围产期心脏病', '女性心脏病专家，擅长女性特殊时期心脏问题', '周三/周五/周日 上午', 'WED,FRI,SUN', 20);
INSERT INTO `doctor` VALUES (62, NULL, '傅华', '主任医师', 8, '13800008111', 1, '2025-12-06 22:56:12', 'M', '糖尿病、胰岛功能', '内分泌科主任，糖尿病专家', '周一/周三/周五 上午', 'MON,WED,FRI', 25);
INSERT INTO `doctor` VALUES (63, NULL, '蓝芳', '主任医师', 8, '13800008112', 1, '2025-12-06 22:56:12', 'F', '甲状腺疾病、甲状腺结节', '甲状腺疾病专家，擅长甲状腺结节的诊治', '周二/周四 全天', 'TUE,THU', 30);
INSERT INTO `doctor` VALUES (64, NULL, '席强', '主任医师', 8, '13800008113', 1, '2025-12-06 22:56:12', 'M', '垂体疾病、肾上腺疾病', '垂体肾上腺疾病专家', '周一至周五 下午', '1,2,3,4,5', 20);
INSERT INTO `doctor` VALUES (65, NULL, '麻静', '副主任医师', 8, '13800008114', 1, '2025-12-06 22:56:12', 'F', '妊娠期糖尿病、内分泌妊娠管理', '妊娠期内分泌专家', '周一/周三/周五 全天', 'MON,WED,FRI', 30);
INSERT INTO `doctor` VALUES (66, NULL, '詹伟', '副主任医师', 8, '13800008115', 1, '2025-12-06 22:56:12', 'M', '肥胖症、代谢综合征', '肥胖症专家，擅长体重管理和代谢病治疗', '周二/周四 上午', 'TUE,THU', 25);
INSERT INTO `doctor` VALUES (67, NULL, '管梅', '副主任医师', 8, '13800008116', 1, '2025-12-06 22:56:12', 'F', '骨质疏松、钙磷代谢', '骨代谢专家，擅长骨质疏松的综合治疗', '周一至周五 上午', '1,2,3,4,5', 35);
INSERT INTO `doctor` VALUES (68, NULL, '霍强', '副主任医师', 8, '13800008117', 1, '2025-12-06 22:56:12', 'M', '痛风、高尿酸血症', '痛风专家，擅长痛风和高尿酸血症的治疗', '周二/周四/周六 上午', 'TUE,THU,SAT', 30);
INSERT INTO `doctor` VALUES (69, NULL, '万娜', '副主任医师', 8, '13800008118', 1, '2025-12-06 22:56:12', 'F', '儿童内分泌、生长发育', '小儿内分泌专家，擅长儿童生长发育问题', '周三/周五/周日 上午', 'WED,FRI,SUN', 20);
INSERT INTO `doctor` VALUES (70, NULL, '苗华', '主任医师', 9, '13800009111', 1, '2025-12-06 23:12:26', 'M', '肾炎、肾病综合征', '肾内科主任，擅长原发性肾小球疾病的诊治', '周一/周三/周五 上午', 'MON,WED,FRI', 20);
INSERT INTO `doctor` VALUES (71, NULL, '文芳', '主任医师', 9, '13800009112', 1, '2025-12-06 23:12:26', 'F', '糖尿病肾病、高血压肾病', '继发性肾病专家，擅长糖尿病肾病的综合治疗', '周二/周四 全天', 'TUE,THU', 25);
INSERT INTO `doctor` VALUES (72, NULL, '花强', '主任医师', 9, '13800009113', 1, '2025-12-06 23:12:26', 'M', '肾衰竭、血液透析', '肾衰竭专家，擅长终末期肾病的替代治疗', '周一至周五 下午', '1,2,3,4,5', 15);
INSERT INTO `doctor` VALUES (73, NULL, '方静', '副主任医师', 9, '13800009114', 1, '2025-12-06 23:12:26', 'F', '狼疮性肾炎、血管炎肾病', '自身免疫性肾病专家，擅长狼疮性肾炎的治疗', '周一/周三/周五 全天', 'MON,WED,FRI', 25);
INSERT INTO `doctor` VALUES (74, NULL, '任伟', '副主任医师', 9, '13800009115', 1, '2025-12-06 23:12:26', 'M', '血液透析、血管通路', '透析专家，擅长血液透析和血管通路建立', '周二/周四 上午', 'TUE,THU', 20);
INSERT INTO `doctor` VALUES (75, NULL, '袁梅', '副主任医师', 9, '13800009116', 1, '2025-12-06 23:12:26', 'F', '腹膜透析、家庭透析', '腹膜透析专家，擅长腹膜透析治疗和指导', '周一至周五 上午', '1,2,3,4,5', 30);
INSERT INTO `doctor` VALUES (76, NULL, '柳强', '副主任医师', 9, '13800009117', 1, '2025-12-06 23:12:26', 'M', '肾移植术后管理、免疫抑制', '肾移植专家，擅长肾移植术后管理', '周二/周四/周六 上午', 'TUE,THU,SAT', 25);
INSERT INTO `doctor` VALUES (77, NULL, '鲍娜', '副主任医师', 9, '13800009118', 1, '2025-12-06 23:12:26', 'F', '儿童肾病、遗传性肾病', '小儿肾病专家，擅长儿童肾脏疾病的诊治', '周三/周五/周日 上午', 'WED,FRI,SUN', 20);
INSERT INTO `doctor` VALUES (78, NULL, '史华', '主任医师', 10, '13800010111', 1, '2025-12-06 23:12:26', 'M', '白内障、人工晶体植入', '眼科主任，白内障手术专家，擅长超声乳化手术', '周一/周三/周五 上午', 'MON,WED,FRI', 30);
INSERT INTO `doctor` VALUES (79, NULL, '唐芳', '主任医师', 10, '13800010112', 1, '2025-12-06 23:12:26', 'F', '青光眼、眼压管理', '青光眼专家，擅长各类青光眼的诊断和治疗', '周二/周四 全天', 'TUE,THU', 35);
INSERT INTO `doctor` VALUES (80, NULL, '费强', '主任医师', 10, '13800010113', 1, '2025-12-06 23:12:26', 'M', '屈光手术、近视矫正', '屈光手术专家，擅长近视、远视、散光的激光矫正', '周一至周五 下午', '1,2,3,4,5', 25);
INSERT INTO `doctor` VALUES (81, NULL, '廉静', '副主任医师', 10, '13800010114', 1, '2025-12-06 23:12:26', 'F', '眼底病、视网膜疾病', '眼底病专家，擅长糖尿病视网膜病变的治疗', '周一/周三/周五 全天', 'MON,WED,FRI', 40);
INSERT INTO `doctor` VALUES (82, NULL, '岑伟', '副主任医师', 10, '13800010115', 1, '2025-12-06 23:12:26', 'M', '角膜病、角膜移植', '角膜病专家，擅长角膜疾病的诊治和角膜移植', '周二/周四 上午', 'TUE,THU', 30);
INSERT INTO `doctor` VALUES (83, NULL, '薛梅', '副主任医师', 10, '13800010116', 1, '2025-12-06 23:12:26', 'F', '小儿眼科、斜视弱视', '小儿眼科专家，擅长儿童眼病的诊治', '周一至周五 上午', '1,2,3,4,5', 45);
INSERT INTO `doctor` VALUES (84, NULL, '雷强', '副主任医师', 10, '13800010117', 1, '2025-12-06 23:12:26', 'M', '眼外伤、眼整形', '眼外伤专家，擅长眼部创伤修复和整形', '周二/周四/周六 上午', 'TUE,THU,SAT', 35);
INSERT INTO `doctor` VALUES (85, NULL, '贺娜', '副主任医师', 10, '13800010118', 1, '2025-12-06 23:12:26', 'F', '干眼症、眼表疾病', '眼表疾病专家，擅长干眼症的综合治疗', '周三/周五/周日 上午', 'WED,FRI,SUN', 30);
INSERT INTO `doctor` VALUES (86, NULL, '倪华', '主任医师', 11, '13800011111', 1, '2025-12-06 23:12:26', 'M', '鼻内镜手术、鼻窦炎', '耳鼻喉科主任，鼻内镜手术专家', '周一/周三/周五 上午', 'MON,WED,FRI', 25);
INSERT INTO `doctor` VALUES (87, NULL, '汤芳', '主任医师', 11, '13800011112', 1, '2025-12-06 23:12:26', 'F', '喉癌、头颈肿瘤', '头颈肿瘤专家，擅长喉癌的诊断和治疗', '周二/周四 全天', 'TUE,THU', 30);
INSERT INTO `doctor` VALUES (88, NULL, '滕强', '主任医师', 11, '13800011113', 1, '2025-12-06 23:12:26', 'M', '中耳炎、听力重建', '耳科专家，擅长中耳炎手术和听力重建', '周一至周五 下午', '1,2,3,4,5', 20);
INSERT INTO `doctor` VALUES (89, NULL, '罗静', '副主任医师', 11, '13800011114', 1, '2025-12-06 23:12:26', 'F', '过敏性鼻炎、鼻息肉', '鼻科专家，擅长过敏性鼻炎的综合治疗', '周一/周三/周五 全天', 'MON,WED,FRI', 30);
INSERT INTO `doctor` VALUES (90, NULL, '毕伟', '副主任医师', 11, '13800011115', 1, '2025-12-06 23:12:26', 'M', '睡眠呼吸暂停、鼾症手术', '睡眠呼吸疾病专家，擅长鼾症的手术治疗', '周二/周四 上午', 'TUE,THU', 25);
INSERT INTO `doctor` VALUES (91, NULL, '安梅', '副主任医师', 11, '13800011116', 1, '2025-12-06 23:12:26', 'F', '儿童耳鼻喉疾病、扁桃体腺样体', '小儿耳鼻喉专家，擅长儿童常见病的诊治', '周一至周五 上午', '1,2,3,4,5', 35);
INSERT INTO `doctor` VALUES (92, NULL, '常强', '副主任医师', 11, '13800011117', 1, '2025-12-06 23:12:26', 'M', '眩晕、耳石症', '眩晕专家，擅长耳石症的诊断和复位治疗', '周二/周四/周六 上午', 'TUE,THU,SAT', 30);
INSERT INTO `doctor` VALUES (93, NULL, '乐娜', '副主任医师', 11, '13800011118', 1, '2025-12-06 23:12:26', 'F', '嗓音疾病、喉显微手术', '嗓音专家，擅长嗓音疾病的诊断和喉显微手术', '周三/周五/周日 上午', 'WED,FRI,SUN', 20);
INSERT INTO `doctor` VALUES (94, NULL, '于华', '主任医师', 12, '13800012111', 1, '2025-12-06 23:12:26', 'M', '儿童呼吸系统疾病、哮喘', '儿科主任，擅长儿童哮喘和重症肺炎的诊治', '周一/周三/周五 上午', 'MON,WED,FRI', 40);
INSERT INTO `doctor` VALUES (95, NULL, '卜芳', '主任医师', 12, '13800012112', 1, '2025-12-06 23:12:26', 'F', '新生儿疾病、早产儿管理', '新生儿专家，擅长高危新生儿的救治', '周二/周四 全天', 'TUE,THU', 35);
INSERT INTO `doctor` VALUES (96, NULL, '顾强', '主任医师', 12, '13800012113', 1, '2025-12-06 23:12:26', 'M', '儿童消化系统疾病、营养指导', '儿童消化专家，擅长儿童喂养困难和营养指导', '周一至周五 下午', '1,2,3,4,5', 30);
INSERT INTO `doctor` VALUES (97, NULL, '孟静', '副主任医师', 12, '13800012114', 1, '2025-12-06 23:12:26', 'F', '儿童生长发育、矮小症', '生长发育专家，擅长儿童身高管理和生长激素治疗', '周一/周三/周五 全天', 'MON,WED,FRI', 45);
INSERT INTO `doctor` VALUES (98, NULL, '平伟', '副主任医师', 12, '13800012115', 1, '2025-12-06 23:12:26', 'M', '儿童感染性疾病、发热待查', '儿童感染专家，擅长儿童发热性疾病的诊治', '周二/周四 上午', 'TUE,THU', 40);
INSERT INTO `doctor` VALUES (99, NULL, '黄梅', '副主任医师', 12, '13800012116', 1, '2025-12-06 23:12:26', 'F', '儿童神经系统疾病、癫痫', '小儿神经专家，擅长儿童癫痫的诊断和治疗', '周一至周五 上午', '1,2,3,4,5', 50);
INSERT INTO `doctor` VALUES (100, NULL, '和强', '副主任医师', 12, '13800012117', 1, '2025-12-06 23:12:26', 'M', '儿童急症、重症监护', '儿科ICU专家，擅长儿童急危重症的抢救', '周二/周四/周六 上午', 'TUE,THU,SAT', 35);
INSERT INTO `doctor` VALUES (101, NULL, '穆娜', '副主任医师', 12, '13800012118', 1, '2025-12-06 23:12:26', 'F', '儿童过敏性疾病、湿疹', '儿童过敏专家，擅长儿童过敏性疾病的综合管理', '周三/周五/周日 上午', 'WED,FRI,SUN', 40);
INSERT INTO `doctor` VALUES (102, NULL, '萧华', '主任医师', 13, '13800013111', 1, '2025-12-06 23:12:26', 'F', '妇科肿瘤、妇科腔镜手术', '妇产科主任，擅长妇科肿瘤的微创手术治疗', '周一/周三/周五 上午', 'MON,WED,FRI', 25);
INSERT INTO `doctor` VALUES (103, NULL, '尹芳', '主任医师', 13, '13800013112', 1, '2025-12-06 23:12:26', 'F', '高危妊娠、产科重症', '产科专家，擅长高危妊娠的管理和救治', '周二/周四 全天', 'TUE,THU', 30);
INSERT INTO `doctor` VALUES (104, NULL, '姚强', '主任医师', 13, '13800013113', 1, '2025-12-06 23:12:26', 'M', '不孕不育、辅助生殖', '生殖医学专家，擅长不孕症的诊断和治疗', '周一至周五 下午', '1,2,3,4,5', 20);
INSERT INTO `doctor` VALUES (105, NULL, '邵静', '副主任医师', 13, '13800013114', 1, '2025-12-06 23:12:26', 'F', '妇科内分泌、月经失调', '妇科内分泌专家，擅长月经紊乱的治疗', '周一/周三/周五 全天', 'MON,WED,FRI', 30);
INSERT INTO `doctor` VALUES (106, NULL, '湛伟', '副主任医师', 13, '13800013115', 1, '2025-12-06 23:12:26', 'M', '计划生育、人工流产', '计划生育专家，擅长各类计划生育手术', '周二/周四 上午', 'TUE,THU', 25);
INSERT INTO `doctor` VALUES (107, NULL, '汪梅', '副主任医师', 13, '13800013116', 1, '2025-12-06 23:12:26', 'F', '盆底功能障碍、尿失禁', '盆底康复专家，擅长女性盆底疾病的诊治', '周一至周五 上午', '1,2,3,4,5', 35);
INSERT INTO `doctor` VALUES (108, NULL, '祁强', '副主任医师', 13, '13800013117', 1, '2025-12-06 23:12:26', 'M', '宫颈疾病、HPV感染', '宫颈疾病专家，擅长宫颈癌前病变的诊治', '周二/周四/周六 上午', 'TUE,THU,SAT', 30);
INSERT INTO `doctor` VALUES (109, NULL, '毛娜', '副主任医师', 13, '13800013118', 1, '2025-12-06 23:12:26', 'F', '普通产科、正常分娩', '产科专家，擅长正常妊娠的管理和分娩指导', '周三/周五/周日 上午', 'WED,FRI,SUN', 25);

-- ----------------------------
-- Table structure for drug_info
-- ----------------------------
DROP TABLE IF EXISTS `drug_info`;
CREATE TABLE `drug_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `drug_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品商品名，如：盐酸氨溴索片',
  `generic_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通用名 / 学名',
  `english_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '英文名（可选）',
  `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `dosage_form` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `spec` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格，如 30mg*20片/盒',
  `indications` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `dosage_usage` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `adverse_reaction` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `contraindication` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `precautions` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `interactions` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `storage` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `reference` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '参考来源，如说明书版本/网站链接等',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '药品知识库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of drug_info
-- ----------------------------
INSERT INTO `drug_info` VALUES (1, '盐酸氨溴索片', '氨溴索', 'Ambroxol', '呼吸系统用药', '片剂', '30mg*20片/盒', '用于治疗痰液黏稠所致的咳嗽、慢性支气管炎等。', '口服。成人一次1片，一日3次，餐后服用。', '偶见恶心、呕吐、轻度胃部不适等。', '对本品成分过敏者禁用。', '肝肾功能不全者慎用；孕妇及哺乳期妇女在医生指导下使用。', '与其他祛痰药合用需遵医嘱。', '密封，在阴凉干燥处保存。', '某某药品说明书（2024年修订版）。', '2025-12-07 22:19:16', '2025-12-08 21:49:08');
INSERT INTO `drug_info` VALUES (2, '布地奈德气雾剂', '布地奈德', 'Budesonide Inhalation Aerosol', '呼吸系统', '气雾剂', '200μg*200揿/支', '用于支气管哮喘等呼吸道炎症性疾病的维持治疗。', '按说明书或医嘱使用，一般每日2次，每次1～2揿。', '可见口咽部刺激、咳嗽、咽干等，长期大剂量可致口腔念珠菌感染。', '活动性肺结核患者禁用。', '使用后请及时漱口以减少局部不良反应，儿童需在成人监护下使用。', '与其他糖皮质激素类药物合用时需注意全身性不良反应。', '避光，25℃以下保存。', '某某制药说明书（2022版）', '2025-12-07 22:19:16', '2025-12-07 22:19:16');
INSERT INTO `drug_info` VALUES (3, '阿莫西林胶囊', '阿莫西林', 'Amoxicillin Capsules', '抗感染', '胶囊剂', '0.5g*24粒/盒', '适用于敏感菌所致的呼吸道、泌尿道、胆道、皮肤软组织等感染。', '成人一次0.5g，一日3次，疗程一般为5～7天，或遵医嘱。', '可见恶心、呕吐、腹泻等胃肠道不适，偶见过敏反应。', '对青霉素类药物过敏者禁用。', '既往有严重药物过敏史者慎用，使用过程中如出现皮疹等应立即停药并就医。', '与别的抗生素（如四环素类、氯霉素等）同时使用可能影响疗效。', '密封，在阴凉干燥处保存。', '某某药业说明书（2021版）', '2025-12-07 22:19:16', '2025-12-07 22:19:16');
INSERT INTO `drug_info` VALUES (4, '阿奇霉素片', '阿奇霉素', 'Azithromycin Tablets', '抗生素', '片剂', '0.25g*6片/盒', '用于敏感菌引起的呼吸道感染、皮肤软组织感染等', '成人每日1次，每次0.5g，连服3天', '胃肠道反应、肝功能异常等', '对阿奇霉素过敏者禁用', '肝功能不全者慎用', '与抗酸药合用需间隔2小时', '密封，阴凉干燥处保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (5, '孟鲁司特钠片', '孟鲁司特', 'Montelukast Sodium Tablets', '呼吸系统', '片剂', '10mg*5片/盒', '用于哮喘的预防和长期治疗', '成人每晚1片，睡前服用', '头痛、恶心、腹痛等', '对本产品任何成分过敏者禁用', '不用于治疗急性哮喘发作', '与苯巴比妥合用时需监测', '避光，30℃以下保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (6, '沙丁胺醇气雾剂', '沙丁胺醇', 'Salbutamol Aerosol', '呼吸系统', '气雾剂', '100μg*200揿/支', '用于缓解支气管哮喘或慢性阻塞性肺疾病的症状', '按需使用，每次1-2揿', '心悸、震颤、头痛等', '对本品过敏者禁用', '长期使用需监测心功能', '与β受体阻滞剂合用可能降低疗效', '避光，不超过30℃保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (7, '奥美拉唑肠溶胶囊', '奥美拉唑', 'Omeprazole Enteric-coated Capsules', '消化系统', '肠溶胶囊', '20mg*14粒/盒', '用于胃溃疡、十二指肠溃疡、反流性食管炎等', '每日1次，每次20mg，晨起空腹服用', '头痛、腹泻、恶心等', '对奥美拉唑过敏者禁用', '长期使用需监测血镁水平', '与地高辛合用可能增加地高辛血药浓度', '密封，避光，25℃以下保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (8, '铝碳酸镁片', '铝碳酸镁', 'Hydrotalcite Tablets', '消化系统', '咀嚼片', '0.5g*20片/盒', '用于胃酸过多、胃炎、胃溃疡等', '每次1-2片，每日3次，餐后1-2小时咀嚼', '偶见便秘、腹泻、口干等', '严重肾功能不全者禁用', '服用后1-2小时内避免服用其他药物', '与四环素类药物合用影响吸收', '密封，干燥处保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (9, '多潘立酮片', '多潘立酮', 'Domperidone Tablets', '消化系统', '片剂', '10mg*30片/盒', '用于消化不良、腹胀、恶心呕吐等', '每次1片，每日3-4次，餐前15-30分钟服用', '偶见口干、头痛、皮疹等', '胃肠道出血、机械性肠梗阻者禁用', '心脏病患者慎用', '与抗胆碱药合用降低疗效', '密封，避光保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (10, '硝苯地平控释片', '硝苯地平', 'Nifedipine Controlled-release Tablets', '心血管系统', '控释片', '30mg*7片/盒', '用于高血压、心绞痛的治疗', '每日1次，每次30mg，整片吞服', '头痛、面部潮红、水肿等', '对硝苯地平过敏者禁用', '不可咀嚼或压碎服用', '与地高辛合用需监测地高辛血药浓度', '密封，30℃以下保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (11, '美托洛尔缓释片', '美托洛尔', 'Metoprolol Sustained-release Tablets', '心血管系统', '缓释片', '47.5mg*7片/盒', '用于高血压、心绞痛、心力衰竭等', '每日1次，每次47.5mg', '疲劳、头痛、心动过缓等', '心源性休克、病态窦房结综合征者禁用', '停药需逐渐减量', '与维拉帕米合用增加心动过缓风险', '密封，25℃以下保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (12, '阿托伐他汀钙片', '阿托伐他汀', 'Atorvastatin Calcium Tablets', '心血管系统', '片剂', '20mg*7片/盒', '用于高胆固醇血症、冠心病等', '每日1次，每次20mg，晚间服用', '肌肉疼痛、肝功能异常等', '活动性肝病患者禁用', '用药期间需监测肝功能', '与红霉素合用增加肌病风险', '密封，避光保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (13, '阿司匹林肠溶片', '阿司匹林', 'Aspirin Enteric-coated Tablets', '神经系统', '肠溶片', '100mg*30片/盒', '用于预防心肌梗死、脑卒中等', '每日1次，每次100mg', '胃肠道不适、出血倾向等', '活动性消化道溃疡者禁用', '手术前需停药', '与抗凝药合用增加出血风险', '密封，避光保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (14, '氯吡格雷片', '氯吡格雷', 'Clopidogrel Tablets', '神经系统', '片剂', '75mg*7片/盒', '用于预防动脉粥样硬化血栓事件', '每日1次，每次75mg', '出血、胃肠道反应等', '严重肝损伤、活动性出血者禁用', '手术前需停药', '与奥美拉唑合用可能降低疗效', '密封，30℃以下保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (15, '加巴喷丁胶囊', '加巴喷丁', 'Gabapentin Capsules', '神经系统', '胶囊剂', '0.3g*24粒/盒', '用于神经性疼痛、癫痫等', '初始剂量每日3次，每次0.3g', '头晕、嗜睡、疲劳等', '对加巴喷丁过敏者禁用', '肾功能不全者需调整剂量', '与抗酸药合用需间隔2小时', '密封，25℃以下保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (16, '双氯芬酸钠缓释片', '双氯芬酸', 'Diclofenac Sodium Sustained-release Tablets', '骨科', '缓释片', '75mg*10片/盒', '用于关节炎、软组织损伤等引起的疼痛和炎症', '每日1次，每次75mg', '胃肠道不适、头痛、眩晕等', '活动性消化道溃疡者禁用', '长期使用需监测肝肾功能', '与抗凝药合用增加出血风险', '密封，30℃以下保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (17, '塞来昔布胶囊', '塞来昔布', 'Celecoxib Capsules', '骨科', '胶囊剂', '0.2g*6粒/盒', '用于骨关节炎、类风湿关节炎等', '每日1-2次，每次0.2g', '胃肠道反应、头痛、皮疹等', '对磺胺过敏者禁用', '心血管疾病患者慎用', '与华法林合用需监测INR', '密封，避光保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (18, '氨基葡萄糖胶囊', '氨基葡萄糖', 'Glucosamine Capsules', '骨科', '胶囊剂', '0.75g*20粒/盒', '用于骨关节炎的辅助治疗', '每日2次，每次0.75g', '偶见胃肠道不适、头痛等', '对氨基葡萄糖过敏者禁用', '糖尿病患者需监测血糖', '与抗凝药合用可能增加出血风险', '密封，阴凉干燥处保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (19, '二甲双胍缓释片', '二甲双胍', 'Metformin Sustained-release Tablets', '内分泌系统', '缓释片', '0.5g*30片/盒', '用于2型糖尿病的治疗', '每日1次，每次0.5g，随晚餐服用', '胃肠道反应、乳酸酸中毒等', '严重肾功能不全者禁用', '造影检查前后需暂停用药', '与乙醇合用增加乳酸酸中毒风险', '密封，30℃以下保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (20, '格列美脲片', '格列美脲', 'Glimepiride Tablets', '内分泌系统', '片剂', '2mg*15片/盒', '用于2型糖尿病的治疗', '初始剂量每日1次，每次1mg', '低血糖、胃肠道反应等', '1型糖尿病、糖尿病酮症酸中毒者禁用', '需规律饮食，避免低血糖', '与β受体阻滞剂合用可能掩盖低血糖症状', '密封，避光保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (21, '左甲状腺素钠片', '左甲状腺素', 'Levothyroxine Sodium Tablets', '内分泌系统', '片剂', '50μg*100片/盒', '用于甲状腺功能减退的替代治疗', '每日1次，早餐前30分钟服用', '心悸、头痛、失眠等', '未经治疗的肾上腺皮质功能不全者禁用', '与钙剂、铁剂需间隔4小时服用', '与抗凝药合用需调整抗凝药剂量', '密封，25℃以下保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (22, '氯雷他定片', '氯雷他定', 'Loratadine Tablets', '皮肤科', '片剂', '10mg*6片/盒', '用于过敏性鼻炎、慢性荨麻疹等', '每日1次，每次10mg', '嗜睡、口干、头痛等', '对氯雷他定过敏者禁用', '肝功能不全者需调整剂量', '与酮康唑合用增加不良反应', '密封，避光保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (23, '糠酸莫米松乳膏', '莫米松', 'Mometasone Furoate Cream', '皮肤科', '乳膏剂', '10g:10mg/支', '用于湿疹、神经性皮炎、银屑病等', '每日1次，薄涂于患处', '皮肤刺激、干燥、毛囊炎等', '细菌、真菌、病毒感染部位禁用', '避免长期大面积使用', '与其他皮质类固醇合用可能增加不良反应', '密封，不超过25℃保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (24, '阿达帕林凝胶', '阿达帕林', 'Adapalene Gel', '皮肤科', '凝胶剂', '15g:15mg/支', '用于寻常痤疮的治疗', '每晚1次，洁面后薄涂于患处', '皮肤刺激、干燥、红斑等', '对阿达帕林过敏者禁用', '避免接触眼、口、鼻黏膜', '与维A酸类药物合用增加刺激性', '密封，不超过25℃保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (25, '左氧氟沙星滴眼液', '左氧氟沙星', 'Levofloxacin Eye Drops', '眼科', '滴眼剂', '5ml:15mg/支', '用于细菌性结膜炎、角膜炎等', '每日3-4次，每次1-2滴', '眼部刺激、视力模糊等', '对喹诺酮类药物过敏者禁用', '佩戴隐形眼镜时不可使用', '与其他眼药水合用需间隔10分钟', '避光，不超过25℃保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (26, '玻璃酸钠滴眼液', '玻璃酸钠', 'Sodium Hyaluronate Eye Drops', '眼科', '滴眼剂', '0.4ml*30支/盒', '用于干眼症、眼疲劳等', '每日3-4次，每次1滴', '偶见眼部刺激、瘙痒等', '对玻璃酸钠过敏者禁用', '开封后1个月内用完', '无已知明显相互作用', '室温保存，避免冻结', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (27, '溴莫尼定滴眼液', '溴莫尼定', 'Brimonidine Eye Drops', '眼科', '滴眼剂', '5ml:10mg/支', '用于开角型青光眼、高眼压症', '每日2次，每次1滴', '眼部刺激、口干、头痛等', '对溴莫尼定过敏者禁用', '佩戴软性隐形眼镜者慎用', '与中枢神经系统抑制剂合用需谨慎', '避光，2-25℃保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (28, '丙酸氟替卡松鼻喷雾剂', '氟替卡松', 'Fluticasone Propionate Nasal Spray', '耳鼻喉科', '喷雾剂', '50μg*120喷/瓶', '用于过敏性鼻炎的治疗', '每日1次，每侧鼻孔2喷', '鼻出血、鼻部刺激感等', '对氟替卡松过敏者禁用', '使用前摇匀，避免喷入眼睛', '与CYP3A4抑制剂合用需谨慎', '30℃以下保存，避免阳光直射', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (29, '盐酸羟甲唑啉喷雾剂', '羟甲唑啉', 'Oxymetazoline Hydrochloride Spray', '耳鼻喉科', '喷雾剂', '10ml:5mg/瓶', '用于急慢性鼻炎、鼻窦炎等', '每日2次，每侧鼻孔1-3喷', '鼻部干燥、烧灼感、头痛等', '萎缩性鼻炎、鼻腔干燥者禁用', '连续使用不超过7天', '与单胺氧化酶抑制剂合用需谨慎', '避光，不超过25℃保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (30, '桉柠蒎肠溶软胶囊', '桉柠蒎', 'Eucalyptol, Limonene and Pinene Enteric Soft Capsules', '耳鼻喉科', '肠溶软胶囊', '0.3g*18粒/盒', '用于急慢性鼻窦炎、支气管炎等', '每日2-3次，每次0.3g', '胃肠道不适、皮疹等', '对桉柠蒎过敏者禁用', '餐前半小时凉开水送服', '无已知明显相互作用', '密封，阴凉干燥处保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (31, '布洛芬混悬液', '布洛芬', 'Ibuprofen Suspension', '儿科', '混悬液', '100ml:2g/瓶', '用于儿童发热、疼痛等症状', '按体重计算剂量，每6-8小时一次', '胃肠道反应、皮疹等', '对布洛芬过敏者、活动性消化道溃疡者禁用', '使用前摇匀，开封后冷藏保存', '与抗凝药合用增加出血风险', '密封，不超过25℃保存，开封后冷藏', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (32, '小儿氨酚黄那敏颗粒', '复方制剂', 'Pediatric Paracetamol and Artificial Cow-bezoar and Chlorphenamine Granules', '儿科', '颗粒剂', '6g*10袋/盒', '用于儿童感冒引起的发热、头痛、流涕等', '按年龄和体重计算剂量，每日3次', '嗜睡、恶心、皮疹等', '对成分过敏者禁用，肝肾功能不全者慎用', '用药不超过3天，症状未缓解请就医', '与镇静催眠药合用增强中枢抑制作用', '密封，阴凉干燥处保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (33, '蒙脱石散', '蒙脱石', 'Montmorillonite Powder', '儿科', '散剂', '3g*10袋/盒', '用于儿童急慢性腹泻', '1岁以下每日1袋，分3次服用', '偶见便秘、大便干结', '对蒙脱石过敏者禁用', '与其他药物需间隔1-2小时服用', '可能影响其他药物吸收', '密封，干燥处保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (34, '黄体酮胶囊', '黄体酮', 'Progesterone Capsules', '妇产科', '胶囊剂', '0.1g*20粒/盒', '用于黄体功能不足、先兆流产等', '每日200-300mg，分1-2次服用', '头晕、嗜睡、乳房胀痛等', '对黄体酮过敏者、血栓性疾病患者禁用', '用药期间避免驾驶和操作机械', '与酮康唑合用可能增加不良反应', '密封，避光保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (35, '头孢克肟胶囊', '头孢克肟', 'Cefixime Capsules', '妇产科', '胶囊剂', '0.1g*6粒/盒', '用于敏感菌引起的泌尿道感染、呼吸道感染等', '每日2次，每次0.1g', '胃肠道反应、皮疹、肝功能异常等', '对头孢菌素类过敏者禁用', '青霉素过敏者慎用', '与抗酸药合用影响吸收', '密封，避光保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (36, '硝呋太尔制霉素阴道软胶囊', '硝呋太尔/制霉素', 'Nifuratel and Nystatin Vaginal Soft Capsules', '妇产科', '阴道软胶囊', '6粒/盒', '用于细菌性阴道病、外阴阴道念珠菌病等', '每晚1粒，置入阴道深处', '局部刺激、灼热感等', '对硝呋太尔或制霉素过敏者禁用', '月经期暂停使用', '无已知明显相互作用', '密封，不超过25℃保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (37, '复方氨酚烷胺片', '复方制剂', 'Compound Paracetamol and Amantadine Tablets', '全科', '片剂', '12片/盒', '用于普通感冒和流行性感冒引起的发热、头痛等', '每日2次，每次1片', '嗜睡、恶心、口干等', '对成分过敏者、严重肝肾功能不全者禁用', '用药期间避免驾驶和操作机械', '与中枢神经系统抑制剂合用增强抑制作用', '密封，避光保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (38, '枸橼酸莫沙必利片', '莫沙必利', 'Mosapride Citrate Tablets', '全科', '片剂', '5mg*24片/盒', '用于功能性消化不良、胃食管反流等', '每日3次，每次5mg，餐前服用', '腹泻、口干、头晕等', '胃肠道出血、肠梗阻者禁用', '心脏病患者慎用', '与抗胆碱药合用降低疗效', '密封，避光保存', '药品说明书(2024版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_info` VALUES (39, '对乙酰氨基酚片', '对乙酰氨基酚', 'Paracetamol Tablets', '全科', '片剂', '0.5g*10片/盒', '用于发热、头痛、关节痛等', '每次0.5g，每日不超过2g', '偶见皮疹、恶心等', '严重肝肾功能不全者禁用', '用药不超过3天，长期饮酒者慎用', '与抗凝药合用可能增强抗凝作用', '密封，避光保存', '药品说明书(2023版)', '2025-12-16 21:27:21', '2025-12-16 21:27:21');

-- ----------------------------
-- Table structure for drug_inventory
-- ----------------------------
DROP TABLE IF EXISTS `drug_inventory`;
CREATE TABLE `drug_inventory`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `drug_id` bigint UNSIGNED NOT NULL COMMENT '药品ID，对应 drug_info.id',
  `stock_qty` int NOT NULL DEFAULT 0 COMMENT '当前库存量（最小单位）',
  `unit_price` decimal(38, 2) NOT NULL,
  `batch_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `expire_date` date NULL DEFAULT NULL COMMENT '有效期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/STOPPED',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_inventory_drug`(`drug_id` ASC) USING BTREE,
  CONSTRAINT `fk_inventory_drug` FOREIGN KEY (`drug_id`) REFERENCES `drug_info` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of drug_inventory
-- ----------------------------
INSERT INTO `drug_inventory` VALUES (1, 1, 2000, 1.80, 'AMBRO-20251201', '2026-12-31', 'ACTIVE', '2025-12-09 20:34:00', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (2, 2, 1500, 65.00, 'BUD-20251101', '2026-11-30', 'ACTIVE', '2025-12-09 20:34:00', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (3, 3, 2500, 0.80, 'AMOX-20251001', '2027-10-31', 'ACTIVE', '2025-12-09 20:34:00', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (4, 4, 2000, 25.80, '阿奇霉素片-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (5, 5, 1800, 38.50, '孟鲁司特钠-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (6, 6, 1800, 42.30, '沙丁胺醇气-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (7, 7, 2200, 56.20, '奥美拉唑肠-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (8, 8, 2200, 18.90, '铝碳酸镁片-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (9, 9, 2200, 12.50, '多潘立酮片-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (10, 10, 1500, 32.40, '硝苯地平控-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (11, 11, 1500, 28.70, '美托洛尔缓-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (12, 12, 1500, 45.60, '阿托伐他汀-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (13, 13, 1000, 8.90, '阿司匹林肠-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (14, 14, 1000, 78.30, '氯吡格雷片-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (15, 15, 1000, 52.10, '加巴喷丁胶-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (16, 16, 1000, 16.80, '双氯芬酸钠-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (17, 17, 1000, 34.20, '塞来昔布胶-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (18, 18, 1000, 62.50, '氨基葡萄糖-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (19, 19, 1000, 15.30, '二甲双胍缓-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (20, 20, 1000, 22.40, '格列美脲片-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (21, 21, 1000, 28.90, '左甲状腺素-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (22, 22, 1000, 14.70, '氯雷他定片-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (23, 23, 800, 36.80, '糠酸莫米松-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (24, 24, 1000, 41.20, '阿达帕林凝-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (25, 25, 800, 24.50, '左氧氟沙星-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (26, 26, 800, 38.60, '玻璃酸钠滴-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (27, 27, 800, 52.30, '溴莫尼定滴-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (28, 28, 1000, 68.40, '丙酸氟替卡-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (29, 29, 1000, 12.80, '盐酸羟甲唑-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (30, 30, 1000, 46.20, '桉柠蒎肠溶-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (31, 31, 1200, 22.50, '布洛芬混悬-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (32, 32, 1200, 18.30, '小儿氨酚黄-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (33, 33, 1200, 14.60, '蒙脱石散-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (34, 34, 1000, 58.70, '黄体酮胶囊-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (35, 35, 1000, 32.90, '头孢克肟胶-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (36, 36, 1000, 44.80, '硝呋太尔制-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (37, 37, 1000, 10.50, '复方氨酚烷-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (38, 38, 1000, 26.40, '枸橼酸莫沙-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');
INSERT INTO `drug_inventory` VALUES (39, 39, 1000, 6.80, '对乙酰氨基-20251216-001', '2027-12-16', 'ACTIVE', '2025-12-16 21:27:21', '2025-12-16 21:27:21');

-- ----------------------------
-- Table structure for medication_guide
-- ----------------------------
DROP TABLE IF EXISTS `medication_guide`;
CREATE TABLE `medication_guide`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `patient_id` bigint NULL DEFAULT NULL,
  `dept_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `doctor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `viewed` tinyint(1) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of medication_guide
-- ----------------------------
INSERT INTO `medication_guide` VALUES (1, 2, 1, '呼吸内科', '张三', '咳嗽用药指导', '建议按照医嘱口服 XXX，每日 2 次，每次 1 片，连续服用 5 天。期间注意多喝温水，若出现发热加重或呼吸困难，请及时就诊。', 0, '2025-12-03 20:11:41');
INSERT INTO `medication_guide` VALUES (2, 2, 1, '骨科', '李四', '术后镇痛用药说明', '术后 3 天内可按需口服布洛芬，每日不超过 3 次，注意保护胃黏膜，有不适及时就诊。', 1, '2025-12-02 20:11:47');
INSERT INTO `medication_guide` VALUES (3, 1, 1, '呼吸内科', '张三', '咳嗽症状用药指导', '建议按医嘱口服复方止咳药物，每日 2 次，每次 1 片，连续 5 天。期间注意多饮温水、避免受凉与油腻饮食。若出现发热持续不退、呼吸困难等情况，请及时就诊。', 0, '2025-12-03 20:21:14');
INSERT INTO `medication_guide` VALUES (4, 1, 1, '消化内科', '李四', '胃部不适用药说明', '饭前 30 分钟口服抑酸药，每日 2 次；如有反酸明显，可在医生指导下短期加用黏膜保护剂。用药期间避免辛辣、生冷、咖啡及浓茶。', 1, '2025-12-02 20:21:14');
INSERT INTO `medication_guide` VALUES (5, 1, 1, '骨科', '王五', '术后镇痛及消炎用药指导', '手术后 3 天内可按需口服止痛药，每日不超过 3 次；术后 5~7 天规律口服消炎药，具体剂量按处方执行。若出现伤口红肿渗液或体温＞38.5℃，请立即复诊。', 0, '2025-12-01 20:21:14');
INSERT INTO `medication_guide` VALUES (6, 8, 8, '科室', '张测试', '常见病症用药指导', '尊敬的时然然：\n\n根据您的诊断【常见病症】,科室的张测试医生为您开具以下用药方案：\n\n【用药清单】\n1. 盐酸氨溴索片（30mg*20片/盒）\n   用法用量：口服。成人一次1片，一日3次，餐后服用。\n   用药频率：\n   用药天数：1天\n\n【注意事项】\n• 孕妇及哺乳期妇女在医生指导下使用\n• 肝肾功能不全者慎用\n\n【可能出现的不良反应】\n• 偶见恶心、呕吐、轻度胃部不适等\n\n【温馨提示】\n• 请妥善保管药品，放在儿童不能触及的地方\n• 服药期间注意饮食清淡，避免辛辣刺激食物\n• 请按照医嘱完成整个疗程，不要随意停药\n• 如需调整用药，请咨询医生\n\n祝您早日康复！\n科室 张测试医生\n生成时间：2025-12-16 20:53', 0, '2025-12-16 20:53:23');
INSERT INTO `medication_guide` VALUES (7, 1, 1, '科室', '张测试', '呼吸局促用药指导', '尊敬的杨节约：\n\n根据您的诊断【呼吸局促】,科室的张测试医生为您开具以下用药方案：\n\n【用药清单】\n1. 阿莫西林胶囊（0.5g*24粒/盒）\n   用法用量：成人一次0.5g，一日3次，疗程一般为5～7天，或遵医嘱。\n   用药频率：\n   用药天数：1天\n\n【注意事项】\n• 既往有严重药物过敏史者慎用，使用过程中如出现皮疹等应立即停药并就医\n\n【可能出现的不良反应】\n• 可见恶心、呕吐、腹泻等胃肠道不适，偶见过敏反应\n\n【温馨提示】\n• 请妥善保管药品，放在儿童不能触及的地方\n• 服药期间注意饮食清淡，避免辛辣刺激食物\n• 请按照医嘱完成整个疗程，不要随意停药\n• 如需调整用药，请咨询医生\n\n祝您早日康复！\n科室 张测试医生\n生成时间：2025-12-16 20:58', 0, '2025-12-16 20:58:22');

-- ----------------------------
-- Table structure for nurse
-- ----------------------------
DROP TABLE IF EXISTS `nurse`;
CREATE TABLE `nurse`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '护士ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '关联用户账号ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '所属科室',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `is_active` int NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_nurse_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_nurse_dept`(`dept_id` ASC) USING BTREE,
  INDEX `idx_nurse_active`(`is_active` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '护士信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of nurse
-- ----------------------------
INSERT INTO `nurse` VALUES (1, 3, '张护士', 'F', 1, '护士', '13800000001', 1, '2025-12-08 20:07:03', '2025-12-08 20:07:03');

-- ----------------------------
-- Table structure for patients
-- ----------------------------
DROP TABLE IF EXISTS `patients`;
CREATE TABLE `patients`  (
  `patient_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '就诊人姓名',
  `id_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '证件类型：身份证/护照/医保卡',
  `id_card` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '证件号码',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`patient_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `patients_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of patients
-- ----------------------------
INSERT INTO `patients` VALUES (1, '杨节约', '身份证', '131022200410121025', 1, '18733683998', '2025-11-21 11:50:33');
INSERT INTO `patients` VALUES (2, 'jjj', '身份证', '12345678912345678', 1, '15803265699', '2025-11-21 16:00:39');
INSERT INTO `patients` VALUES (3, '张三', '身份证', '123456789012345678', 2, '13800000001', '2025-11-21 17:16:59');
INSERT INTO `patients` VALUES (4, '酷酷酷', '身份证', '131022200410121158', 1, '18733683554', '2025-12-06 21:18:15');
INSERT INTO `patients` VALUES (5, '王五', '身份证', '123456789012345678', 2, '13800000010', '2025-12-11 11:20:11');
INSERT INTO `patients` VALUES (6, '来来来', '身份证', '131022200410125588', 6, '15822761008', '2025-12-11 15:53:52');
INSERT INTO `patients` VALUES (7, '腰冉冉', '身份证', '130522200455921664', 8, '18533092213', '2025-12-16 13:53:31');
INSERT INTO `patients` VALUES (8, '时然然', '护照', 'E12345678', 8, '18533117223', '2025-12-16 13:53:54');

-- ----------------------------
-- Table structure for pharmacist
-- ----------------------------
DROP TABLE IF EXISTS `pharmacist`;
CREATE TABLE `pharmacist`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '关联 users.user_id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `dept_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '所属科室ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_active` int NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_pharmacist_user`(`user_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_pharmacist_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_pharmacist_dept`(`dept_id` ASC) USING BTREE,
  INDEX `idx_pharm_dept`(`dept_id` ASC) USING BTREE,
  INDEX `idx_pharm_active`(`is_active` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '药师信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pharmacist
-- ----------------------------
INSERT INTO `pharmacist` VALUES (1, 4, '张三药师', '女', 1, '主管药师', '13900000001', 1, '2025-12-08 22:09:11', '2025-12-08 22:09:11');

-- ----------------------------
-- Table structure for prescription
-- ----------------------------
DROP TABLE IF EXISTS `prescription`;
CREATE TABLE `prescription`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '处方ID',
  `register_id` bigint NOT NULL COMMENT '挂号记录ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '就诊人ID（可选）',
  `doctor_id` bigint NULL DEFAULT NULL COMMENT '开方医生ID（可选）',
  `diag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '诊断结论',
  `total_amount` decimal(38, 2) NOT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开方时间',
  `dispense_time` datetime NULL DEFAULT NULL COMMENT '发药时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_prescription_register`(`register_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '处方主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prescription
-- ----------------------------
INSERT INTO `prescription` VALUES (1, 13, 1, 5, '', 0.00, 'DISPENSED', '2025-12-11 15:10:39', '2025-12-11 15:50:06');
INSERT INTO `prescription` VALUES (2, 18, 6, 5, '', 0.00, 'DISPENSED', '2025-12-11 15:58:01', '2025-12-11 15:58:18');
INSERT INTO `prescription` VALUES (3, 21, 6, 5, '', 0.00, 'SUBMITTED', '2025-12-12 10:24:32', NULL);
INSERT INTO `prescription` VALUES (4, 22, 6, 5, '', 0.00, 'SUBMITTED', '2025-12-12 15:29:05', NULL);
INSERT INTO `prescription` VALUES (5, 26, 7, 5, '你眼睛瞎吗？你耳朵聋吗？', 0.00, 'SUBMITTED', '2025-12-16 14:25:57', NULL);
INSERT INTO `prescription` VALUES (6, 28, 8, 5, '咳嗽', 148.00, 'SUBMITTED', '2025-12-16 20:15:52', NULL);
INSERT INTO `prescription` VALUES (7, 27, 7, 5, '疯啦', 136.40, 'SUBMITTED', '2025-12-16 20:44:27', NULL);
INSERT INTO `prescription` VALUES (8, 29, 8, 5, '', 9.00, 'SUBMITTED', '2025-12-16 20:53:19', NULL);
INSERT INTO `prescription` VALUES (9, 30, 1, 5, '呼吸局促', 2.40, 'SUBMITTED', '2025-12-16 20:58:19', NULL);

-- ----------------------------
-- Table structure for prescription_item
-- ----------------------------
DROP TABLE IF EXISTS `prescription_item`;
CREATE TABLE `prescription_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `prescription_id` bigint NOT NULL COMMENT '所属处方ID',
  `drug_id` bigint NOT NULL COMMENT '药品ID，对应 drug_info.id',
  `quantity` int NOT NULL COMMENT '数量（盒/瓶/支等）',
  `dosage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `frequency` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `days` int NULL DEFAULT NULL COMMENT '天数',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '本行金额',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pi_prescription`(`prescription_id` ASC) USING BTREE,
  INDEX `idx_pi_drug`(`drug_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '处方明细' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of prescription_item
-- ----------------------------
INSERT INTO `prescription_item` VALUES (1, 1, 1, 1, '口服。成人一次1片，一日3次，餐后服用。', '', 1, '', 0.00);
INSERT INTO `prescription_item` VALUES (2, 2, 1, 1, '口服。成人一次1片，一日3次，餐后服用。', '', 1, '', 0.00);
INSERT INTO `prescription_item` VALUES (3, 3, 1, 1, '口服。成人一次1片，一日3次，餐后服用。', '', 1, '', 0.00);
INSERT INTO `prescription_item` VALUES (4, 4, 1, 1, '口服。成人一次1片，一日3次，餐后服用。', '', 1, '', 0.00);
INSERT INTO `prescription_item` VALUES (7, 5, 1, 15, '口服。成人一次1片，一日3次，餐后服用。', '', 1, '', 0.00);
INSERT INTO `prescription_item` VALUES (8, 5, 3, 9, '成人一次0.5g，一日3次，疗程一般为5～7天，或遵医嘱。', '', 1, '', 0.00);
INSERT INTO `prescription_item` VALUES (11, 6, 1, 10, '口服。成人一次1片，一日3次，餐后服用。', '', 1, '', 18.00);
INSERT INTO `prescription_item` VALUES (12, 6, 2, 2, '按说明书或医嘱使用，一般每日2次，每次1～2揿。', '', 1, '', 130.00);
INSERT INTO `prescription_item` VALUES (15, 7, 3, 8, '成人一次0.5g，一日3次，疗程一般为5～7天，或遵医嘱。', '', 1, '', 6.40);
INSERT INTO `prescription_item` VALUES (16, 7, 2, 2, '按说明书或医嘱使用，一般每日2次，每次1～2揿。', '', 1, '', 130.00);
INSERT INTO `prescription_item` VALUES (18, 8, 1, 5, '口服。成人一次1片，一日3次，餐后服用。', '', 1, '', 9.00);
INSERT INTO `prescription_item` VALUES (20, 9, 3, 3, '成人一次0.5g，一日3次，疗程一般为5～7天，或遵医嘱。', '', 1, '', 2.40);

-- ----------------------------
-- Table structure for register_record
-- ----------------------------
DROP TABLE IF EXISTS `register_record`;
CREATE TABLE `register_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '挂号记录ID',
  `user_id` bigint NOT NULL COMMENT '登录用户ID（谁付钱/谁的账号）',
  `patient_id` bigint NOT NULL COMMENT '就诊人ID',
  `dept_id` bigint NOT NULL COMMENT '科室ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `doctor_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '挂号时间',
  `queue_no` int NOT NULL COMMENT '排队号（在该医生/科室当天的序号）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'WAITING' COMMENT '状态：WAITING/FINISHED/CANCELLED',
  `source` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '来源：INTELLIGENT_TRIAGE / MANUAL',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `triage_nurse_id` bigint NULL DEFAULT NULL COMMENT '分诊护士ID',
  `triage_time` datetime NULL DEFAULT NULL COMMENT '分诊时间',
  `triage_note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分诊备注',
  `queue_status` int NOT NULL,
  `queue_priority` int NOT NULL DEFAULT 0 COMMENT '队列优先级，越大越靠前（急诊/插队）',
  `last_call_time` datetime NULL DEFAULT NULL COMMENT '最近叫号时间',
  `called_times` int NOT NULL DEFAULT 0 COMMENT '已叫号次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '门诊挂号记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of register_record
-- ----------------------------
INSERT INTO `register_record` VALUES (1, 1, 1, 1, 1, NULL, NULL, '2025-11-21 16:41:36', 1, 'WAITING', 'MANUAL', '1', '2025-11-21 16:41:36', '2025-12-08 20:44:35', 5, '2025-12-08 20:44:35', '高热，疑似肺炎，优先就诊', 4, 100, '2025-12-08 20:40:09', 1);
INSERT INTO `register_record` VALUES (2, 2, 3, 101, 5001, NULL, NULL, '2025-11-21 17:18:39', 1, 'WAITING', 'MANUAL', '测试挂号', '2025-11-21 17:18:39', '2025-11-21 17:18:39', NULL, NULL, NULL, 0, 0, NULL, 0);
INSERT INTO `register_record` VALUES (3, 2, 3, 1, 1, NULL, NULL, '2025-11-21 20:51:06', 2, 'WAITING', 'MANUAL', 'Postman 测试挂号', '2025-11-21 20:51:06', '2025-11-21 20:51:06', NULL, NULL, NULL, 0, 0, NULL, 0);
INSERT INTO `register_record` VALUES (4, 1, 1, 1, 1, NULL, NULL, '2025-11-22 12:08:32', 1, 'WAITING', 'INTELLIGENT_TRIAGE', NULL, '2025-11-22 12:08:32', '2025-11-22 12:08:32', NULL, NULL, NULL, 0, 0, NULL, 0);
INSERT INTO `register_record` VALUES (5, 1, 1, 1, 2, NULL, NULL, '2025-11-22 12:36:13', 1, 'WAITING', 'INTELLIGENT_TRIAGE', NULL, '2025-11-22 12:36:13', '2025-11-22 12:36:13', NULL, NULL, NULL, 0, 0, NULL, 0);
INSERT INTO `register_record` VALUES (6, 2, 3, 101, 5001, NULL, NULL, '2025-11-22 16:05:50', 1, 'WAITING', 'MANUAL', '测试挂号', '2025-11-22 16:05:50', '2025-11-22 16:05:50', NULL, NULL, NULL, 0, 0, NULL, 0);
INSERT INTO `register_record` VALUES (7, 1, 1, 1, 1, NULL, NULL, '2025-11-22 17:16:50', 2, 'WAITING', 'INTELLIGENT_TRIAGE', NULL, '2025-11-22 17:16:50', '2025-11-22 17:16:50', NULL, NULL, NULL, 0, 0, NULL, 0);
INSERT INTO `register_record` VALUES (8, 1, 1, 1, 1, NULL, NULL, '2025-11-22 20:18:02', 3, 'WAITING', 'MANUAL_PATIENT', NULL, '2025-11-22 20:18:02', '2025-11-22 20:18:02', NULL, NULL, NULL, 0, 0, NULL, 0);
INSERT INTO `register_record` VALUES (9, 1, 1, 3, 3, NULL, NULL, '2025-12-03 19:11:15', 1, 'WAITING', 'INTELLIGENT_TRIAGE', NULL, '2025-12-03 19:11:15', '2025-12-03 19:11:15', NULL, NULL, NULL, 0, 0, NULL, 0);
INSERT INTO `register_record` VALUES (10, 1, 1, 1, 1, NULL, NULL, '2025-12-07 21:10:29', 1, 'WAITING', 'MANUAL_PATIENT', NULL, '2025-12-07 21:10:29', '2025-12-07 21:10:29', NULL, NULL, NULL, 0, 0, NULL, 0);
INSERT INTO `register_record` VALUES (11, 1, 1, 1, 1, '呼吸内科', '张三', '2025-12-08 21:04:56', 1, 'WAITING', 'MANUAL', '测试挂号', '2025-12-08 21:04:56', '2025-12-08 21:08:42', 3, '2025-12-08 21:08:32', NULL, 4, 0, '2025-12-08 21:08:32', 1);
INSERT INTO `register_record` VALUES (12, 2, 3, 101, 5001, NULL, NULL, '2025-12-11 11:20:50', 1, 'WAITING', 'MANUAL', '测试挂号', '2025-12-11 11:20:50', '2025-12-11 11:20:50', NULL, NULL, NULL, 0, 0, NULL, 0);
INSERT INTO `register_record` VALUES (13, 2, 1, 1, 5, '呼吸内科门诊', '张测试', '2025-12-11 09:00:00', 1, 'FINISHED', 'SELF', '测试挂号记录1：咳嗽 3 天', '2025-12-11 11:35:50', '2025-12-11 15:10:39', NULL, NULL, NULL, 2, 0, NULL, 0);
INSERT INTO `register_record` VALUES (14, 2, 1, 1, 5, '呼吸内科门诊', '张测试', '2025-12-11 09:10:00', 2, 'WAITING', 'SELF', '测试挂号记录2：发热 2 天', '2025-12-11 11:35:50', '2025-12-11 14:29:21', NULL, NULL, NULL, 1, 0, NULL, 0);
INSERT INTO `register_record` VALUES (15, 2, 1, 1, 5, '呼吸内科门诊', '张测试', '2025-12-11 09:20:00', 3, 'WAITING', 'SELF', '测试挂号记录3：胸闷心悸', '2025-12-11 11:35:50', '2025-12-11 15:56:52', 3, '2025-12-11 15:51:58', NULL, 1, 0, '2025-12-11 15:51:58', 1);
INSERT INTO `register_record` VALUES (16, 1, 1, 1, 5, NULL, NULL, '2025-12-11 15:51:00', 4, 'WAITING', 'MANUAL_PATIENT', NULL, '2025-12-11 15:51:00', '2025-12-11 15:51:00', NULL, NULL, NULL, 0, 0, NULL, 0);
INSERT INTO `register_record` VALUES (17, 1, 4, 1, 5, NULL, NULL, '2025-12-11 15:51:25', 5, 'WAITING', 'MANUAL_PATIENT', NULL, '2025-12-11 15:51:25', '2025-12-11 15:52:24', NULL, NULL, NULL, 1, 0, NULL, 0);
INSERT INTO `register_record` VALUES (18, 6, 6, 1, 5, NULL, NULL, '2025-12-11 15:53:56', 6, 'FINISHED', 'MANUAL_PATIENT', '', '2025-12-11 15:53:56', '2025-12-11 15:58:01', 3, '2025-12-11 15:57:18', NULL, 2, 0, '2025-12-11 15:57:18', 1);
INSERT INTO `register_record` VALUES (19, 6, 6, 1, 5, NULL, NULL, '2025-12-11 15:59:25', 7, 'WAITING', 'MANUAL_PATIENT', NULL, '2025-12-11 15:59:25', '2025-12-11 15:59:25', NULL, NULL, NULL, 0, 0, NULL, 0);
INSERT INTO `register_record` VALUES (20, 6, 6, 1, 5, NULL, NULL, '2025-12-11 16:17:47', 8, 'WAITING', 'MANUAL_PATIENT', NULL, '2025-12-11 16:17:47', '2025-12-11 16:25:13', 3, '2025-12-11 16:25:13', NULL, 1, 0, '2025-12-11 16:25:13', 1);
INSERT INTO `register_record` VALUES (21, 6, 6, 1, 5, '呼吸内科', '张测试', '2025-12-12 10:23:04', 1, 'FINISHED', 'MANUAL_PATIENT', '', '2025-12-12 10:23:04', '2025-12-12 10:24:32', 3, '2025-12-12 10:23:21', NULL, 3, 0, '2025-12-12 10:23:21', 1);
INSERT INTO `register_record` VALUES (22, 6, 6, 1, 5, '呼吸内科', '张测试', '2025-12-12 15:28:29', 2, 'FINISHED', 'MANUAL_PATIENT', '', '2025-12-12 15:28:29', '2025-12-12 15:29:05', NULL, NULL, NULL, 3, 0, NULL, 0);
INSERT INTO `register_record` VALUES (23, 6, 6, 1, 5, '呼吸内科', '张测试', '2025-12-15 21:53:25', 1, 'WAITING', 'MANUAL_PATIENT', NULL, '2025-12-15 21:53:25', '2025-12-15 23:14:16', 3, '2025-12-15 21:54:26', NULL, 2, 0, '2025-12-15 21:54:26', 1);
INSERT INTO `register_record` VALUES (24, 8, 7, 1, 1, NULL, NULL, '2025-12-16 13:54:09', 1, 'WAITING', 'MANUAL_PATIENT', NULL, '2025-12-16 13:54:09', '2025-12-16 15:11:56', NULL, NULL, NULL, 4, 0, NULL, 0);
INSERT INTO `register_record` VALUES (25, 8, 8, 1, 2, NULL, NULL, '2025-12-16 13:54:19', 1, 'WAITING', 'MANUAL_PATIENT', NULL, '2025-12-16 13:54:19', '2025-12-16 15:11:58', 3, '2025-12-16 13:55:51', NULL, 4, 0, '2025-12-16 13:55:51', 1);
INSERT INTO `register_record` VALUES (26, 8, 7, 1, 5, NULL, NULL, '2025-12-16 14:23:16', 1, 'FINISHED', 'MANUAL_PATIENT', '', '2025-12-16 14:23:16', '2025-12-16 14:30:05', 3, '2025-12-16 14:23:44', NULL, 4, 0, '2025-12-16 14:23:44', 1);
INSERT INTO `register_record` VALUES (27, 8, 7, 1, 5, NULL, NULL, '2025-12-16 15:11:31', 2, 'FINISHED', 'MANUAL_PATIENT', '想休息，想休息', '2025-12-16 15:11:31', '2025-12-16 20:44:30', 3, '2025-12-16 15:12:01', NULL, 3, 0, '2025-12-16 15:12:01', 1);
INSERT INTO `register_record` VALUES (28, 8, 8, 1, 5, NULL, NULL, '2025-12-16 20:13:44', 3, 'FINISHED', 'MANUAL_PATIENT', '', '2025-12-16 20:13:44', '2025-12-16 20:52:29', 3, '2025-12-16 20:14:07', NULL, 4, 0, '2025-12-16 20:14:07', 1);
INSERT INTO `register_record` VALUES (29, 8, 8, 1, 5, NULL, NULL, '2025-12-16 20:52:05', 4, 'FINISHED', 'MANUAL_PATIENT', '', '2025-12-16 20:52:05', '2025-12-16 20:57:14', 3, '2025-12-16 20:52:31', NULL, 4, 0, '2025-12-16 20:52:31', 1);
INSERT INTO `register_record` VALUES (30, 1, 1, 1, 5, NULL, NULL, '2025-12-16 20:56:52', 5, 'FINISHED', 'MANUAL_PATIENT', '注意休息', '2025-12-16 20:56:52', '2025-12-16 20:58:22', 3, '2025-12-16 20:57:17', NULL, 3, 0, '2025-12-16 20:57:17', 1);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `login_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号：患者/护士/医生',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密后的密码',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `status` int NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `login_name`(`login_name` ASC) USING BTREE,
  INDEX `idx_users_role`(`role` ASC) USING BTREE,
  INDEX `idx_users_status`(`status` ASC) USING BTREE,
  INDEX `idx_users_login`(`login_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '18733683998', '$2a$10$8Ho/mNeGjbUPFjyzaNfpWukdgDTjcQY./0ZjdG9Xvt6ZdZ4WCVXXS', '18733683998', 1, '2025-11-20 10:28:38', 'PATIENT');
INSERT INTO `users` VALUES (2, '13800000001', '$2a$10$GaEh.IHHfPzX5F9WolIhx.S5JBp2DS7x6mlY9w0UoM7E.wl7CPkeC', '13800000001', 1, '2025-11-21 17:10:36', 'PATIENT');
INSERT INTO `users` VALUES (3, 'N0001', '$2b$12$ssrHh0ISgR0/va2LTOJWIOWwS/tDTUBC5r3cPyruGg9PSc/7kTQfW', '13800000001', 1, '2025-12-08 20:07:03', 'NURSE');
INSERT INTO `users` VALUES (4, 'P0001', '$2b$12$plM9tDOt7g0jTH36PiezRe9a1CqJZXfNsfCdl6JtNz6DNiyB1zEo6', '13900000001', 1, '2025-12-08 22:09:02', 'PHARMACIST');
INSERT INTO `users` VALUES (5, 'D0001', '$2a$10$GaEh.IHHfPzX5F9WolIhx.S5JBp2DS7x6mlY9w0UoM7E.wl7CPkeC', '13600000001', 1, '2025-12-11 10:18:29', 'DOCTOR');
INSERT INTO `users` VALUES (6, '15822761008', '$2a$10$svCxp2yBGgwoU2i/.VEnoOYn50OFSLgOSliM2MwFOLda2XCT9DJWK', '15822761008', 1, '2025-12-11 15:53:06', 'PATIENT');
INSERT INTO `users` VALUES (7, 'admin', '$2b$10$LG74dymAymcQj3OHxQXb/u4ibZWvf3NZqeYskJTj0fghMVqKsIYQe', '13800000000', 1, '2025-12-14 22:59:33', 'ADMIN');
INSERT INTO `users` VALUES (8, '18533092213', '$2a$10$2.JSR4SHzRL1xdSV29l7g.GqTvswK6lgxNqI9yuqnXqUueEFxrwOq', '18533092213', 1, '2025-12-16 13:52:53', 'PATIENT');

SET FOREIGN_KEY_CHECKS = 1;
