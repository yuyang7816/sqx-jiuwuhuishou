/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80409
 Source Host           : localhost:3306
 Source Schema         : jiuwuhuishou

 Target Server Type    : MySQL
 Target Server Version : 80409
 File Encoding         : 65001

 Date: 09/11/2026 15:10:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '地址id',
  `consignee` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '收货人',
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建时间',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '详细地址',
  `mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '电话',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户id',
  `is_default` int(0) NULL DEFAULT NULL COMMENT '是否默认地址',
  `provinces` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '省市区',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 144 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '地址' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------

-- ----------------------------
-- Table structure for adverti
-- ----------------------------
DROP TABLE IF EXISTS `adverti`;
CREATE TABLE `adverti`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '广告id',
  `create_at` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建时间',
  `img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图片路径',
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '页面路由',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '广告' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of adverti
-- ----------------------------
INSERT INTO `adverti` VALUES (1, '2023-10-25 13:32:46', 'https://ysx.xianmxkj.com/file/uploadPath/2023/10/25/27c0c47503d0b557138ec79888683d19.png', '/pages/index/mian');

-- ----------------------------
-- Table structure for app
-- ----------------------------
DROP TABLE IF EXISTS `app`;
CREATE TABLE `app`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `android_wgt_url` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `create_at` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `des` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `ios_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `ios_wgt_url` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `method` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `version` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `wgt_url` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'app升级' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app
-- ----------------------------
INSERT INTO `app` VALUES (5, 'https://www.pgyer.com/hb2v', '2020-07-20 18:35:00', '升级', NULL, '', 'false', '1.1', 'https://www.pgyer.com/hb2v');
INSERT INTO `app` VALUES (6, '6', '2023-07-18 10:19:35', '6', '6', '6', 'false', '6', '6rrr66');

-- ----------------------------
-- Table structure for appliance_category
-- ----------------------------
DROP TABLE IF EXISTS `appliance_category`;
CREATE TABLE `appliance_category`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `display_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '价格卡片展示标题',
  `sort` int(0) NOT NULL DEFAULT 0,
  `state` tinyint(0) NOT NULL DEFAULT 1,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '家电品类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appliance_category
-- ----------------------------
INSERT INTO `appliance_category` VALUES (1, '空调', 'star', '挂机空调回收', 1, 1, '2026-07-06 14:56:18', '2026-07-06 14:56:18');
INSERT INTO `appliance_category` VALUES (2, '冰箱', 'home', '十字对开门冰箱', 2, 1, '2026-07-06 14:56:18', '2026-07-06 14:56:18');
INSERT INTO `appliance_category` VALUES (3, '洗衣机', 'grid', '滚筒洗衣机回收', 3, 1, '2026-07-06 14:56:18', '2026-07-06 14:56:18');
INSERT INTO `appliance_category` VALUES (4, '电视', 'photo', '液晶电视回收', 4, 1, '2026-07-06 14:56:18', '2026-07-06 14:56:18');

-- ----------------------------
-- Table structure for appliance_hero_flow
-- ----------------------------
DROP TABLE IF EXISTS `appliance_hero_flow`;
CREATE TABLE `appliance_hero_flow`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '步骤名称，如 线上预约',
  `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '图标图片地址',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1上架 2下架',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_appliance_hero_flow_state_sort`(`state`, `sort`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '家电回收-首页流程步骤' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appliance_hero_flow
-- ----------------------------
INSERT INTO `appliance_hero_flow` VALUES (1, '线上预约', 'edit-pen', 1, 1, '2026-07-15 15:43:25', '2026-07-15 15:43:25');
INSERT INTO `appliance_hero_flow` VALUES (2, '电话沟通', 'phone', 2, 1, '2026-07-15 15:43:25', '2026-07-15 15:43:25');
INSERT INTO `appliance_hero_flow` VALUES (3, '上门回收', 'car', 3, 1, '2026-07-15 15:43:25', '2026-07-15 15:43:25');
INSERT INTO `appliance_hero_flow` VALUES (4, '立即收钱', 'rmb-circle', 4, 1, '2026-07-15 15:43:25', '2026-07-15 15:43:25');

-- ----------------------------
-- Table structure for appliance_model
-- ----------------------------
DROP TABLE IF EXISTS `appliance_model`;
CREATE TABLE `appliance_model`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(0) NOT NULL COMMENT '品类ID',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '型号名称',
  `spec_json` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '规格参数JSON',
  `base_price` decimal(10, 2) NOT NULL COMMENT '基础估价',
  `state` tinyint(0) NOT NULL DEFAULT 1,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_appliance_model_category`(`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '家电型号' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appliance_model
-- ----------------------------
INSERT INTO `appliance_model` VALUES (28, 1, '云佳 1.5匹', '{\"brand\":\"格力\",\"type\":\"挂机\",\"capacity\":\"1.5匹\"}', 680.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (29, 1, '云锦 2匹', '{\"brand\":\"格力\",\"type\":\"柜机\",\"capacity\":\"2匹\"}', 920.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (30, 1, '天丽 大1匹', '{\"brand\":\"格力\",\"type\":\"挂机\",\"capacity\":\"1匹\"}', 560.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (31, 1, '风尊 1.5匹', '{\"brand\":\"美的\",\"type\":\"挂机\",\"capacity\":\"1.5匹\"}', 650.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (32, 1, '酷省电 大1匹', '{\"brand\":\"美的\",\"type\":\"挂机\",\"capacity\":\"1匹\"}', 540.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (33, 1, '静悦 1.5匹', '{\"brand\":\"海尔\",\"type\":\"挂机\",\"capacity\":\"1.5匹\"}', 620.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (34, 1, '京裕 大1匹', '{\"brand\":\"奥克斯\",\"type\":\"挂机\",\"capacity\":\"1匹\"}', 480.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (35, 2, 'BCD-501WGHFD14S8U1', '{\"brand\":\"海尔\",\"doorType\":\"十字对开门\",\"capacity\":\"400L以上\"}', 980.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (36, 2, 'BCD-535WGHSSEDS9', '{\"brand\":\"海尔\",\"doorType\":\"十字对开门\",\"capacity\":\"400L以上\"}', 860.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (37, 2, 'BCD-478WSPZM(E)', '{\"brand\":\"美的\",\"doorType\":\"三门\",\"capacity\":\"300-400L\"}', 760.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (38, 2, 'BCD-407WSPZM(E)', '{\"brand\":\"美的\",\"doorType\":\"双门\",\"capacity\":\"300-400L\"}', 680.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (39, 2, 'BCD-509WD18MP', '{\"brand\":\"容声\",\"doorType\":\"十字对开门\",\"capacity\":\"400L以上\"}', 820.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (40, 2, 'KA92NV02TI', '{\"brand\":\"西门子\",\"doorType\":\"多门法式\",\"capacity\":\"400L以上\"}', 1180.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (41, 3, 'TG100V86WMDY5', '{\"brand\":\"小天鹅\",\"type\":\"滚筒\",\"capacity\":\"8-10kg\"}', 620.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (42, 3, 'TD100V618T', '{\"brand\":\"小天鹅\",\"type\":\"洗烘一体\",\"capacity\":\"8-10kg\"}', 880.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (43, 3, 'EG100MATE3S', '{\"brand\":\"海尔\",\"type\":\"滚筒\",\"capacity\":\"8-10kg\"}', 580.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (44, 3, 'MG100VT55DY', '{\"brand\":\"美的\",\"type\":\"滚筒\",\"capacity\":\"6-8kg\"}', 520.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (45, 3, 'XQG100-EGALW', '{\"brand\":\"松下\",\"type\":\"滚筒\",\"capacity\":\"8-10kg\"}', 760.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (46, 4, 'EA55 2024款', '{\"brand\":\"小米\",\"screenType\":\"液晶 LED\",\"size\":\"43-55寸\"}', 520.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (47, 4, 'S65 Mini LED', '{\"brand\":\"小米\",\"screenType\":\"Mini LED\",\"size\":\"55-65寸\"}', 760.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (48, 4, '55E5N Pro', '{\"brand\":\"海信\",\"screenType\":\"液晶 LED\",\"size\":\"43-55寸\"}', 580.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (49, 4, '55Q10K', '{\"brand\":\"TCL\",\"screenType\":\"Mini LED\",\"size\":\"43-55寸\"}', 620.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_model` VALUES (50, 4, 'XR-55X90L', '{\"brand\":\"索尼\",\"screenType\":\"OLED\",\"size\":\"43-55寸\"}', 980.00, 1, '2026-07-06 16:04:08', '2026-07-06 16:04:08');

-- ----------------------------
-- Table structure for appliance_pricing_rule
-- ----------------------------
DROP TABLE IF EXISTS `appliance_pricing_rule`;
CREATE TABLE `appliance_pricing_rule`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `model_id` bigint(0) NOT NULL,
  `rule_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规则键',
  `rule_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '规则值',
  `adjust_rate` decimal(6, 4) NOT NULL COMMENT '调整比例',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_appliance_pricing_model`(`model_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 86 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '家电估价规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appliance_pricing_rule
-- ----------------------------
INSERT INTO `appliance_pricing_rule` VALUES (1, 28, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (2, 29, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (3, 30, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (4, 31, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (5, 32, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (6, 33, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (7, 34, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (8, 35, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (9, 36, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (10, 37, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (11, 38, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (12, 39, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (13, 40, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (14, 41, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (15, 42, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (16, 43, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (17, 44, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (18, 45, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (19, 46, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (20, 47, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (21, 48, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (22, 49, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (23, 50, 'condition', 'good', 0.0000, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (32, 28, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (33, 29, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (34, 30, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (35, 31, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (36, 32, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (37, 33, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (38, 34, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (39, 35, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (40, 36, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (41, 37, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (42, 38, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (43, 39, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (44, 40, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (45, 41, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (46, 42, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (47, 43, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (48, 44, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (49, 45, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (50, 46, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (51, 47, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (52, 48, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (53, 49, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (54, 50, 'condition', 'fair', -0.0800, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (63, 28, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (64, 29, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (65, 30, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (66, 31, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (67, 32, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (68, 33, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (69, 34, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (70, 35, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (71, 36, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (72, 37, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (73, 38, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (74, 39, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (75, 40, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (76, 41, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (77, 42, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (78, 43, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (79, 44, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (80, 45, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (81, 46, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (82, 47, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (83, 48, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (84, 49, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');
INSERT INTO `appliance_pricing_rule` VALUES (85, 50, 'condition', 'poor', -0.1500, '2026-07-06 16:04:08', '2026-07-06 16:04:08');

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '名称',
  `image_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '图片地址',
  `classify` int(0) NULL DEFAULT NULL COMMENT '分类1banner图 2金刚区分类 9境外banner',
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '跳转地址 ',
  `sort` int(0) NULL DEFAULT NULL COMMENT '顺序',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '描述',
  `state` int(0) NULL DEFAULT NULL COMMENT '状态1正常 2隐藏',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'banner' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, '轮播图1', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/0c3ffd66-652e-47b3-ba21-fb708768f232.png', 1, '/packageRecycle/clothes/booking', 1, '旧衣回收跳转', 1, '2025-07-18 15:38:36', '2026-07-21 14:19:57');
INSERT INTO `banner` VALUES (2, '淑婷呀', 'https://xianyu.xianmaxiong.com/file/uploadPath/2025/08/14/5d1e3d3c1abe6194a14b8db3cb0f891c.png', 9, '1', 1, '123', 1, '2025-08-14 18:03:07', NULL);
INSERT INTO `banner` VALUES (3, '首页轮播图', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/30/6f8fb97f-4923-4b1d-af26-ecb432dcebf4.png', 1, '/packageRecycle/clothes/booking', 2, '1', 1, '2025-11-10 14:40:38', '2026-07-30 19:48:38');
INSERT INTO `banner` VALUES (15, '轮播图', 'https://tcxx.xianmaxiong.com/file/uploadPath/2025/11/17/f511b8ae1fe0893dfd8aa4cc610bbbe5.png', 3, NULL, NULL, NULL, 2, '2025-11-17 13:19:13', '2025-11-26 15:24:09');
INSERT INTO `banner` VALUES (16, '1111', 'https://tu.mianyazuju.com/2026/01/06/1ed7855f-f9e1-4f14-ae4d-d1963db09855.png', 7, NULL, NULL, NULL, 1, '2026-01-06 17:15:59', '2026-01-06 17:27:14');
INSERT INTO `banner` VALUES (17, '每日签到', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/0d917751-9cc9-4b18-b289-e67ccc305080.png', 2, '/pages/welfare/welfare', 1, '连续签到领好礼|+5', 1, '2026-07-06 14:33:48', '2026-07-21 14:24:50');
INSERT INTO `banner` VALUES (18, '环保红包', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/bb840ae3-e303-4a1d-98aa-234a134113cb.png', 2, '/packageCommon/user/invite', 2, '下单最高返20元', 1, '2026-07-06 14:33:48', '2026-07-21 14:25:21');
INSERT INTO `banner` VALUES (19, '品牌合作', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/52ee1428-5402-4526-885d-c8bcec962eb1.png', 2, '/packageCommon/user/brand-cooperation', 3, '联名环保周边兑换', 1, '2026-07-06 14:33:48', '2026-07-21 14:25:28');
INSERT INTO `banner` VALUES (20, '家电维修', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/3b7aebc2-d4ec-4824-8284-53bcea23128a.png', 2, 'https://www.baidu.com', 4, '以修代换更环保', 1, '2026-07-06 14:33:48', '2026-07-21 14:42:24');
INSERT INTO `banner` VALUES (21, '家电回收', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/08d19b8a-72cd-43db-9148-5a2a4c730b09.png', 5, '/packageRecycle/appliance/home', 1, 'home|is-red', 1, '2026-07-15 16:23:54', '2026-07-21 14:21:54');
INSERT INTO `banner` VALUES (22, '数码回收', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/6f19847f-3e38-4ade-82c1-3b1b6474d509.png', 5, '/packageRecycle/digital/models?scene=digital', 2, 'grid|is-rose', 1, '2026-07-15 16:23:54', '2026-07-21 14:22:09');
INSERT INTO `banner` VALUES (23, '旧衣回收', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/6377aeef-77c1-4f7b-8ba4-081a6e7a64f8.png', 5, '/packageRecycle/clothes/booking', 3, 'gift|is-pink', 1, '2026-07-15 16:23:54', '2026-07-21 14:22:26');
INSERT INTO `banner` VALUES (24, '奢侈品', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/517dcf7c-65c5-47e5-8041-5d70a9e692d1.png', 5, '/packageRecycle/digital/models?scene=luxury', 4, 'coupon|is-rose', 1, '2026-07-15 16:23:54', '2026-07-21 14:22:35');
INSERT INTO `banner` VALUES (25, '书籍', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/fde2d644-a5b5-432a-abf0-384835d01902.png', 5, '/packageRecycle/clothes/booking?scene=book', 5, 'file-text|is-red', 1, '2026-07-15 16:23:54', '2026-07-21 14:22:44');
INSERT INTO `banner` VALUES (26, '邀请背景图', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/15/b4e5affe-6bcf-41ab-9d2d-c6d5d25284a0.png', 4, NULL, 1, NULL, 1, '2026-07-15 17:26:05', NULL);
INSERT INTO `banner` VALUES (27, '王** 刚刚回收旧衣获得 15.5 元', NULL, 6, '', 1, NULL, 1, '2026-07-15 19:16:56', '2026-07-15 19:16:56');
INSERT INTO `banner` VALUES (28, '张** 刚刚回收纸箱获得 8.2 元', NULL, 6, '', 2, NULL, 1, '2026-07-15 19:16:56', '2026-07-15 19:16:56');
INSERT INTO `banner` VALUES (29, '李** 刚刚回收旧家电获得 126.0 元', NULL, 6, '', 3, NULL, 1, '2026-07-15 19:16:56', '2026-07-15 19:16:56');
INSERT INTO `banner` VALUES (30, '陈** 刚刚回收数码获得 358.0 元', NULL, 6, '', 4, NULL, 1, '2026-07-15 19:16:56', '2026-07-15 19:16:56');
INSERT INTO `banner` VALUES (31, '赵** 刚刚回收旧衣获得 100.5 元', NULL, 6, NULL, 1, NULL, 1, '2026-07-15 19:18:51', NULL);

-- ----------------------------
-- Table structure for banner_tour
-- ----------------------------
DROP TABLE IF EXISTS `banner_tour`;
CREATE TABLE `banner_tour`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `banner_id` bigint(0) NOT NULL COMMENT 'banner id',
  `tour_id` bigint(0) NULL DEFAULT NULL COMMENT '旅游ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner_tour
-- ----------------------------
INSERT INTO `banner_tour` VALUES (1, 5, 26);
INSERT INTO `banner_tour` VALUES (2, 5, 27);
INSERT INTO `banner_tour` VALUES (5, 5, 41);
INSERT INTO `banner_tour` VALUES (6, 4, 26);
INSERT INTO `banner_tour` VALUES (7, 4, 27);

-- ----------------------------
-- Table structure for book_category
-- ----------------------------
DROP TABLE IF EXISTS `book_category`;
CREATE TABLE `book_category`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1上架 2下架',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '书籍回收分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_category
-- ----------------------------
INSERT INTO `book_category` VALUES (1, '文学小说', 'file-text', 1, 1, '2026-07-07 13:56:58', '2026-07-07 13:56:58');
INSERT INTO `book_category` VALUES (2, '教材教辅', 'order', 2, 1, '2026-07-07 13:56:58', '2026-07-07 13:56:58');
INSERT INTO `book_category` VALUES (3, '儿童绘本', 'gift', 3, 1, '2026-07-07 13:56:58', '2026-07-07 13:56:58');
INSERT INTO `book_category` VALUES (4, '杂志期刊', 'bookmark', 4, 1, '2026-07-07 13:56:58', '2026-07-07 13:56:58');

-- ----------------------------
-- Table structure for book_weight_option
-- ----------------------------
DROP TABLE IF EXISTS `book_weight_option`;
CREATE TABLE `book_weight_option`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '重量区间标题，如 5-15kg',
  `option_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '说明文案',
  `estimate_amount` decimal(10, 2) NOT NULL COMMENT '预估金额',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1上架 2下架',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_book_weight_option_state_sort`(`state`, `sort`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '书籍预约-预估重量选项' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of book_weight_option
-- ----------------------------
INSERT INTO `book_weight_option` VALUES (1, '5-15kg', '日常整理', 5.50, 1, 1, '2026-07-15 15:05:12', '2026-07-15 15:27:34');
INSERT INTO `book_weight_option` VALUES (2, '15-30kg', '搬家清理', 12.00, 2, 1, '2026-07-15 15:05:12', '2026-07-15 15:05:12');
INSERT INTO `book_weight_option` VALUES (3, '>30kg', '大批量回收', 22.00, 3, 1, '2026-07-15 15:05:12', '2026-07-15 15:05:12');
INSERT INTO `book_weight_option` VALUES (4, 'E2E_重量_mrvnpw9l', 'E2E_auto', 9.90, 1, 1, '2026-07-22 13:43:46', '2026-07-22 13:43:46');
INSERT INTO `book_weight_option` VALUES (5, 'E2E_重量_mrvnv58x', 'E2E_auto', 9.90, 1, 1, '2026-07-22 13:47:51', '2026-07-22 13:47:50');
INSERT INTO `book_weight_option` VALUES (6, 'E2E_重量_mrvnx1py', 'E2E_auto', 9.90, 1, 1, '2026-07-22 13:49:20', '2026-07-22 13:49:19');
INSERT INTO `book_weight_option` VALUES (7, 'E2E_重量_mrvo0auj', 'E2E_auto', 9.90, 1, 1, '2026-07-22 13:51:52', '2026-07-22 13:51:51');
INSERT INTO `book_weight_option` VALUES (8, 'E2E_重量_mrvo5otf', 'E2E_auto', 9.90, 1, 1, '2026-07-22 13:56:03', '2026-07-22 13:56:02');
INSERT INTO `book_weight_option` VALUES (9, 'E2E_重量_mrvobuln', 'E2E_auto', 9.90, 1, 1, '2026-07-22 14:00:51', '2026-07-22 14:00:50');
INSERT INTO `book_weight_option` VALUES (10, 'E2E_重量_mrvr01uc', 'E2E_auto', 9.90, 1, 1, '2026-07-22 15:15:39', '2026-07-22 15:15:38');
INSERT INTO `book_weight_option` VALUES (11, 'E2E_重量_mrvr46of', 'E2E_auto', 9.90, 1, 1, '2026-07-22 15:18:52', '2026-07-22 15:18:51');

-- ----------------------------
-- Table structure for brand_cooperation
-- ----------------------------
DROP TABLE IF EXISTS `brand_cooperation`;
CREATE TABLE `brand_cooperation`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '提交用户ID',
  `brand_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '品牌名称',
  `brand_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '品牌图片',
  `contact_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系人',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系电话',
  `province` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '省',
  `city` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '市',
  `district` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '区',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '详细地址',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1待跟进 2已联系 3已合作 4已拒绝',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_brand_cooperation_user_id`(`user_id`) USING BTREE,
  INDEX `idx_brand_cooperation_status`(`status`) USING BTREE,
  INDEX `idx_brand_cooperation_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '品牌合作意向' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of brand_cooperation
-- ----------------------------
INSERT INTO `brand_cooperation` VALUES (1, 44, '蜜雪', '', '小咩', '15200000000', '北京市', '北京市', '东城区', '灵境胡同', 1, NULL, '2026-07-10 15:57:24', NULL);
INSERT INTO `brand_cooperation` VALUES (2, 47, '书亦烧仙草', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/08/18/e06efa49-65ae-4884-be73-10df34ec1c1b.png', '张先生', '18329680001', '陕西省', '西安市', '雁塔区', '朗臣大厦', 1, NULL, '2026-08-18 10:16:51', NULL);

-- ----------------------------
-- Table structure for breach_policy
-- ----------------------------
DROP TABLE IF EXISTS `breach_policy`;
CREATE TABLE `breach_policy`  (
  `id` bigint(0) NOT NULL COMMENT 'id',
  `days` int(0) NOT NULL COMMENT '天数',
  `times` time(0) NOT NULL COMMENT '时间',
  `rule` tinyint(1) NULL DEFAULT NULL COMMENT '1=之前 2=之后',
  `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '违约金(0.1=10%)',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '1=用户 2=商家',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '违约规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of breach_policy
-- ----------------------------
INSERT INTO `breach_policy` VALUES (1, 7, '00:00:00', 1, 0.30, 1, '2025-12-18 16:13:15', NULL);
INSERT INTO `breach_policy` VALUES (2, 4, '00:00:00', 1, 0.60, 1, '2025-12-18 16:14:04', NULL);
INSERT INTO `breach_policy` VALUES (3, 1, '00:00:00', 1, 0.80, 1, '2025-12-18 16:14:26', NULL);
INSERT INTO `breach_policy` VALUES (4, 1, '00:00:00', 2, 1.00, 1, '2025-12-18 16:14:47', NULL);
INSERT INTO `breach_policy` VALUES (5, 7, '00:00:00', 1, 0.10, 2, '2025-12-18 16:15:30', '2026-01-06 16:47:44');
INSERT INTO `breach_policy` VALUES (6, 4, '00:00:00', 1, 0.20, 2, '2025-12-18 16:15:46', '2026-01-06 16:47:36');
INSERT INTO `breach_policy` VALUES (7, 1, '00:00:00', 1, 0.30, 2, '2025-12-18 16:16:03', '2026-01-06 16:47:29');
INSERT INTO `breach_policy` VALUES (8, 1, '00:00:00', 2, 0.40, 2, '2025-12-18 16:16:25', '2026-01-06 16:47:21');

-- ----------------------------
-- Table structure for cancel_account
-- ----------------------------
DROP TABLE IF EXISTS `cancel_account`;
CREATE TABLE `cancel_account`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标题',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '内容',
  `status` int(0) NOT NULL COMMENT '状态: 1申请中 2已注销 3撤销申请',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户注销申请' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cancel_account
-- ----------------------------

-- ----------------------------
-- Table structure for cash_out
-- ----------------------------
DROP TABLE IF EXISTS `cash_out`;
CREATE TABLE `cash_out`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '申请提现id',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户id',
  `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '提现金额',
  `out_flag` int(0) NULL DEFAULT NULL COMMENT '是否转账',
  `out_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '转账时间',
  `order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '订单编号',
  `state` int(0) NULL DEFAULT NULL COMMENT '状态 0待转账 1成功 -1退款',
  `refund` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '退款原因',
  `classify` int(0) NULL DEFAULT NULL COMMENT '提现方式 1支付宝 2微信',
  `wx_img` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '微信图片',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '申请时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '提现' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cash_out
-- ----------------------------

-- ----------------------------
-- Table structure for cash_out_account
-- ----------------------------
DROP TABLE IF EXISTS `cash_out_account`;
CREATE TABLE `cash_out_account`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '账户类型 1支付宝 2微信',
  `account_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '账户账号',
  `account_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '账户名称',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态 1启用 2禁用',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '提现账号' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cash_out_account
-- ----------------------------

-- ----------------------------
-- Table structure for chat_content
-- ----------------------------
DROP TABLE IF EXISTS `chat_content`;
CREATE TABLE `chat_content`  (
  `chat_content_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '聊天内容id',
  `chat_conversation_id` int(0) NULL DEFAULT NULL COMMENT '聊天会话id',
  `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '内容',
  `message_type` int(0) NULL DEFAULT NULL COMMENT '类型 1文字 2图片 3语音',
  `width` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图片宽度',
  `height` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图片高度',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '发送用户id',
  `status` int(0) NULL DEFAULT NULL COMMENT '0未读 1已读',
  `create_time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发送时间',
  `voice_length` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`chat_content_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '聊天内容' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_content
-- ----------------------------

-- ----------------------------
-- Table structure for chat_conversation
-- ----------------------------
DROP TABLE IF EXISTS `chat_conversation`;
CREATE TABLE `chat_conversation`  (
  `chat_conversation_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '聊天会话id',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '发送用户id',
  `focused_user_id` int(0) NULL DEFAULT NULL COMMENT '接受用户id',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '状态',
  `create_time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `is_send_msg` int(0) NULL DEFAULT NULL,
  `is_wx_msg` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`chat_conversation_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '聊天会话' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_conversation
-- ----------------------------

-- ----------------------------
-- Table structure for chats
-- ----------------------------
DROP TABLE IF EXISTS `chats`;
CREATE TABLE `chats`  (
  `chat_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '会话id',
  `create_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建时间',
  `user_count` int(0) NULL DEFAULT 0 COMMENT '用户未读条数',
  `user_count2` int(0) NULL DEFAULT 0 COMMENT '好友未读条数',
  `user_head` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户头像',
  `user_head2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '好友头像',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户id',
  `user_id2` bigint(0) NULL DEFAULT NULL COMMENT '好友id',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户昵称',
  `user_name2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '好友昵称',
  `store_count` int(0) NULL DEFAULT 0 COMMENT '后台未读条数',
  `store_head` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '后台头像',
  `store_id` bigint(0) NULL DEFAULT NULL COMMENT '总后台id(总后台传0',
  `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商户昵称',
  PRIMARY KEY (`chat_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '聊天会话' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chats
-- ----------------------------

-- ----------------------------
-- Table structure for chats_content
-- ----------------------------
DROP TABLE IF EXISTS `chats_content`;
CREATE TABLE `chats_content`  (
  `chat_content_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '会话内容id',
  `chat_id` bigint(0) NULL DEFAULT NULL COMMENT '会话Id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '聊天内容',
  `create_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建时间',
  `send_type` bigint(0) NULL DEFAULT NULL COMMENT '消息来源（1用户消息 2好友消息）',
  `status` int(0) NULL DEFAULT 1 COMMENT '是否已读(1未读 2已读)',
  `type` int(0) NULL DEFAULT NULL COMMENT '类型（1文字 2图片 3链接）',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户id',
  `user_id2` bigint(0) NULL DEFAULT NULL COMMENT '好友id',
  `store_id` bigint(0) NULL DEFAULT NULL COMMENT '后台id（总后台传0）',
  PRIMARY KEY (`chat_content_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '聊天会话内容' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chats_content
-- ----------------------------

-- ----------------------------
-- Table structure for clothes_category
-- ----------------------------
DROP TABLE IF EXISTS `clothes_category`;
CREATE TABLE `clothes_category`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1上架 2下架',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '旧衣回收分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of clothes_category
-- ----------------------------
INSERT INTO `clothes_category` VALUES (1, '上衣/裤子', 'integral', 1, 1, '2026-07-07 11:40:06', '2026-07-07 11:40:06');
INSERT INTO `clothes_category` VALUES (2, '床单/毛毯', 'home', 2, 1, '2026-07-07 11:40:06', '2026-07-07 11:40:06');
INSERT INTO `clothes_category` VALUES (3, '鞋帽/包包', 'bag', 3, 1, '2026-07-07 11:40:06', '2026-07-07 11:40:06');
INSERT INTO `clothes_category` VALUES (4, '儿童旧衣', 'gift', 4, 1, '2026-07-07 11:40:06', '2026-07-07 11:40:06');

-- ----------------------------
-- Table structure for common_info
-- ----------------------------
DROP TABLE IF EXISTS `common_info`;
CREATE TABLE `common_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '配置文件id',
  `create_at` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `max` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `min` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '配置文件名称',
  `type` int(0) NULL DEFAULT NULL COMMENT '类型',
  `value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '值',
  `condition_from` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '分类',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 895 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of common_info
-- ----------------------------
INSERT INTO `common_info` VALUES (1, '2023-07-18 09:40:54', NULL, '客服二维码', 1, '', 'xitong');
INSERT INTO `common_info` VALUES (2, '2020-02-21 21:17:17', NULL, '公众号二维码', 2, '', 'xitong');
INSERT INTO `common_info` VALUES (6, '2020-07-09 17:59:45', NULL, '微信公众号APPID	', 5, '', 'weixin');
INSERT INTO `common_info` VALUES (7, '2021-03-15 21:16:30', NULL, '短信签名', 6, '', 'duanxin');
INSERT INTO `common_info` VALUES (16, '2020-02-25 20:43:38', NULL, '微信公众号秘钥	', 21, '', 'weixin');
INSERT INTO `common_info` VALUES (17, '2020-02-25 20:43:59', NULL, '公众号Token', 16, '', 'weixin');
INSERT INTO `common_info` VALUES (18, '2020-02-25 20:44:15', NULL, '公众号EncodingAESKey	', 17, '', 'weixin');
INSERT INTO `common_info` VALUES (20, '2023-07-17 15:26:13', NULL, '后台管理平台域名配置	', 20, '', 'xitong');
INSERT INTO `common_info` VALUES (21, '2023-07-07 13:32:18', NULL, 'h5服务域名配置	', 19, '', 'xitong');
INSERT INTO `common_info` VALUES (22, '2020-07-27 15:17:00', NULL, '短信服务商（1 腾讯云  2阿里云  3短信宝）', 79, '', 'duanxin');
INSERT INTO `common_info` VALUES (23, '2026-03-26 17:52:50', NULL, '后台服务名称 后台管理', 12, '', 'xitong');
INSERT INTO `common_info` VALUES (27, '2020-03-09 20:46:10', NULL, '通用APP下载地址	', 25, '', 'xitong');
INSERT INTO `common_info` VALUES (33, '2021-03-15 21:19:27', NULL, '腾讯云短信clientId	', 31, '', 'duanxin');
INSERT INTO `common_info` VALUES (34, '2020-03-28 00:47:00', NULL, '腾讯云短信clientSecret	', 32, '', 'duanxin');
INSERT INTO `common_info` VALUES (44, '2022-03-10 20:38:21', NULL, '客服微信号', 44, '', 'xitong');
INSERT INTO `common_info` VALUES (47, '2022-01-22 20:52:42', NULL, '微信小程序APPID', 45, '', 'weixin');
INSERT INTO `common_info` VALUES (48, '2022-01-22 20:52:48', NULL, '微信小程序秘钥', 46, '', 'weixin');
INSERT INTO `common_info` VALUES (51, '2020-03-29 00:21:00', NULL, '分享安卓下载地址', 49, '', 'xitong');
INSERT INTO `common_info` VALUES (52, '2020-03-29 00:21:00', NULL, '分享苹果下载地址', 50, '', 'xitong');
INSERT INTO `common_info` VALUES (58, '2020-03-29 00:21:00', NULL, '开启微信登录', 53, '', 'kaiguan');
INSERT INTO `common_info` VALUES (69, '2020-06-04 16:34:00', NULL, 'APP消息推送PushAppKey', 60, '', 'push');
INSERT INTO `common_info` VALUES (70, '2020-06-04 16:34:00', NULL, 'APP消息推送PushAppId', 61, '', 'push');
INSERT INTO `common_info` VALUES (71, '2020-06-04 16:34:00', NULL, 'APP消息推送PushMasterSecret', 62, '', 'push');
INSERT INTO `common_info` VALUES (72, '2020-06-04 16:34:00', NULL, '企业支付宝APPID', 63, '', 'zhifubao');
INSERT INTO `common_info` VALUES (73, '2020-06-04 16:34:00', NULL, '企业支付宝公钥', 64, '', 'zhifubaos');
INSERT INTO `common_info` VALUES (74, '2020-06-04 16:34:00', NULL, '企业支付宝商户秘钥', 65, '', 'zhifubao');
INSERT INTO `common_info` VALUES (77, '2021-03-15 21:22:30', NULL, '文件上传阿里云Endpoint', 68, '', 'oss');
INSERT INTO `common_info` VALUES (78, '2021-03-15 21:22:38', NULL, '文件上传阿里云账号accessKeyId', 69, '', 'oss');
INSERT INTO `common_info` VALUES (79, '2021-03-15 21:22:46', NULL, '文件上传阿里云账号accessKeySecret', 70, '', 'oss');
INSERT INTO `common_info` VALUES (80, '2021-03-15 21:22:53', NULL, '文件上传阿里云Bucket名称', 71, '', 'oss');
INSERT INTO `common_info` VALUES (81, '2021-03-15 21:23:00', NULL, '文件上传阿里云Bucket域名', 72, '', 'oss');
INSERT INTO `common_info` VALUES (83, '2020-07-27 15:17:00', NULL, '微信APPappId', 74, '', 'weixin');
INSERT INTO `common_info` VALUES (84, '2020-07-27 15:17:00', NULL, '微信商户key', 75, '', 'weixins');
INSERT INTO `common_info` VALUES (85, '2020-07-27 15:17:00', NULL, '微信商户号mchId', 76, '', 'weixin');
INSERT INTO `common_info` VALUES (96, '2020-07-27 15:17:00', NULL, '阿里云登陆或注册模板code（开启阿里云短信必须配置）', 80, '', 'duanxin');
INSERT INTO `common_info` VALUES (97, '2020-07-27 15:17:00', NULL, '阿里云找回密码模板code（开启阿里云短信必须配置）', 81, '', 'duanxin');
INSERT INTO `common_info` VALUES (98, '2020-07-27 15:17:00', NULL, '阿里云绑定手机号模板code（开启阿里云短信必须配置）', 82, ' ', 'duanxin');
INSERT INTO `common_info` VALUES (99, '2020-07-27 15:17:00', NULL, '阿里云短信accessKeyId', 83, '', 'duanxin');
INSERT INTO `common_info` VALUES (100, '2020-07-27 15:17:00', NULL, '阿里云短信accessSecret', 84, '', 'duanxin');
INSERT INTO `common_info` VALUES (102, '2022-04-21 19:40:07', NULL, '转账方式 1支付宝证书  2支付宝秘钥 3手动', 98, '', 'zhifubao');
INSERT INTO `common_info` VALUES (108, '2023-07-14 16:40:21', NULL, '公众号是否自动登录', 108, '', 'kaiguans');
INSERT INTO `common_info` VALUES (118, '2020-07-27 15:17:00', NULL, '邀请推广内容', 101, '', 'xitongs');
INSERT INTO `common_info` VALUES (120, '2020-07-27 15:17:00', NULL, '邀请微信分享内容', 103, '', 'xitongs');
INSERT INTO `common_info` VALUES (130, '2020-11-04 10:31:40', NULL, '提现最低额度', 112, '', 'fuwufei');
INSERT INTO `common_info` VALUES (134, '2023-07-07 15:28:39', NULL, '海报邀请语', 116, '', 'xitong');
INSERT INTO `common_info` VALUES (154, '2023-07-18 10:03:34', NULL, '是否开启APP微信分享', 136, '', 'kaiguan');
INSERT INTO `common_info` VALUES (159, '2021-11-02 14:53:27', NULL, 'H5推广是否分享APP下载页面', 141, '', 'kaiguan');
INSERT INTO `common_info` VALUES (170, '2022-03-17 18:03:01', NULL, '提现手续费（用户提现时候的手续费，可以作为平台服务费使用，设置0.3就是百分之30，举例提现100服务费30，到账70）', 152, '', 'fuwufei');
INSERT INTO `common_info` VALUES (171, '2021-09-06 16:19:51', NULL, '最高提现金额', 153, '', 'fuwufei');
INSERT INTO `common_info` VALUES (182, '2022-03-20 15:06:28', NULL, '短信宝用户名', 164, '', 'duanxin');
INSERT INTO `common_info` VALUES (183, '2022-03-20 15:06:40', NULL, '短信宝密码', 165, '', 'duanxin');
INSERT INTO `common_info` VALUES (200, '2021-07-19 11:32:12', NULL, '帮助中心', 175, '', 'xieyi');
INSERT INTO `common_info` VALUES (201, '2022-04-18 14:05:03', NULL, '隐私政策', 176, '', 'xieyi');
INSERT INTO `common_info` VALUES (202, '2021-08-14 19:09:54', NULL, '注册协议', 177, '', 'xieyi');
INSERT INTO `common_info` VALUES (203, '2021-08-14 19:09:54', NULL, '关于我们', 187, '', 'xieyi');
INSERT INTO `common_info` VALUES (204, '2023-07-18 09:36:04', NULL, '登录是否获取手机号', 188, '', 'kaiguan');
INSERT INTO `common_info` VALUES (217, '2020-07-27 15:17:00', NULL, '腾讯地图key', 217, '', 'xitong');
INSERT INTO `common_info` VALUES (234, '2020-11-04 10:31:40', NULL, '上传方式 1阿里云oss  2本地', 234, '', 'oss');
INSERT INTO `common_info` VALUES (236, '2020-11-04 10:31:40', NULL, '退款微信通知', 236, '', 'weixin');
INSERT INTO `common_info` VALUES (240, '2020-11-04 10:31:40', NULL, '微信消息通知模版', 240, '', 'weixin');
INSERT INTO `common_info` VALUES (241, '2020-11-04 10:31:40', NULL, '超时几分钟短信通知', 241, '', 'xitong');
INSERT INTO `common_info` VALUES (242, '2020-11-04 10:31:40', NULL, '几条未读短信通知', 242, '', 'xitong');
INSERT INTO `common_info` VALUES (243, '2020-11-04 10:31:40', NULL, '是否开启微信提现', 243, '', 'kaiguan');
INSERT INTO `common_info` VALUES (244, '2021-12-22 10:11:36', NULL, '微信提现方式 1自动 2手动', 244, '', 'xitong');
INSERT INTO `common_info` VALUES (245, '2020-11-04 10:31:40', NULL, '是否开启支付宝提现', 245, '', 'kaiguan');
INSERT INTO `common_info` VALUES (246, '2020-10-28 16:57:07', NULL, '微信证书地址', 201, '', 'weixin');
INSERT INTO `common_info` VALUES (257, '2020-11-04 10:31:40', NULL, '身份验证SecretID', 257, '', 'xitong');
INSERT INTO `common_info` VALUES (258, '2020-11-04 10:31:40', NULL, '身份验证SecretKey', 258, '', 'xitong');
INSERT INTO `common_info` VALUES (259, '2020-11-04 10:31:40', NULL, '身份验证实例ID', 259, '', 'xitong');
INSERT INTO `common_info` VALUES (262, '2022-05-26 21:31:52', NULL, '小程序是否上架', 262, '', 'kaiguan');
INSERT INTO `common_info` VALUES (276, '2023-07-13 16:32:33', NULL, '推广文案', 276, '', 'xitong');
INSERT INTO `common_info` VALUES (289, '2026-01-07 16:52:59', NULL, '推广图片', 289, '', 'image');
INSERT INTO `common_info` VALUES (290, '2023-07-10 10:32:30', NULL, '关键词过滤', 290, '', 'xitong');
INSERT INTO `common_info` VALUES (291, '2023-07-10 10:36:08', NULL, '聊天警告文字提醒', 291, '', 'xitong');
INSERT INTO `common_info` VALUES (292, '2023-07-10 17:31:30', NULL, '审核状态通知', 292, '', 'weixin');
INSERT INTO `common_info` VALUES (293, '2023-07-10 17:31:30', NULL, '用户注销协议', 293, '', 'xieyi');
INSERT INTO `common_info` VALUES (294, '2023-07-11 13:32:12', NULL, 'vip到期提醒', 294, '', 'weixin');
INSERT INTO `common_info` VALUES (295, '2023-07-11 13:32:12', NULL, 'vip到期提前几天提醒', 295, '', 'weixin');
INSERT INTO `common_info` VALUES (303, '2023-07-12 09:42:00', NULL, '是否开启分佣提成', 303, '', 'kaiguan');
INSERT INTO `common_info` VALUES (304, '2023-07-12 09:42:00', NULL, '佣金比例', 304, '', 'fuwufei');
INSERT INTO `common_info` VALUES (306, '2023-07-13 17:50:00', NULL, '新人赠送vip天数(0表示不赠送)', 306, '', 'xitong');
INSERT INTO `common_info` VALUES (309, '2023-07-17 15:23:54', NULL, '用户小程序名称配置', 309, '', 'xitong');
INSERT INTO `common_info` VALUES (414, '2023-07-26 16:27:15', NULL, '地图方式 1腾讯 2天地图', 414, '', 'xitong');
INSERT INTO `common_info` VALUES (415, '2023-07-26 16:27:15', NULL, '天地图key', 415, '', 'xitong');
INSERT INTO `common_info` VALUES (417, '2024-07-03 09:09:54', NULL, '会员充值协议', 417, '', 'xieyi');
INSERT INTO `common_info` VALUES (418, '2024-07-03 10:31:54', NULL, '使用服务规则', 418, '', 'xieyi');
INSERT INTO `common_info` VALUES (855, '2025-07-08 17:35:13', NULL, '短信平台 1腾讯云 2阿里云 3短信宝', 423, '', 'xitong');
INSERT INTO `common_info` VALUES (856, '2025-12-25 19:37:18', NULL, '积分兑换比例 100表示100积分兑换1元', 424, '', 'xitong');
INSERT INTO `common_info` VALUES (857, '2025-07-22 16:24:48', NULL, '是否开启审核', 850, '', 'xitong');
INSERT INTO `common_info` VALUES (858, '2025-08-07 09:50:11', NULL, '企业微信客服链接', 320, '', 'xitong');
INSERT INTO `common_info` VALUES (859, '2025-08-07 09:50:11', NULL, '企业微信客服appid', 321, '', 'xitong');
INSERT INTO `common_info` VALUES (860, '2025-11-18 16:47:32', NULL, '客服类型(1:电话，2:企业微信，3:客服二维码)', 322, '', 'xitong');
INSERT INTO `common_info` VALUES (861, '2025-08-07 09:50:11', NULL, '客服电话', 323, '', 'xitong');
INSERT INTO `common_info` VALUES (862, '2025-08-05 14:40:12', NULL, '支付宝appid', 425, '', 'pay');
INSERT INTO `common_info` VALUES (863, '2025-08-05 14:40:12', NULL, '支付宝公钥', 426, '', 'pay');
INSERT INTO `common_info` VALUES (864, '2025-08-05 14:40:12', NULL, '支付宝商户秘钥', 427, '', 'pay');
INSERT INTO `common_info` VALUES (865, '2025-08-05 14:40:12', NULL, '支付宝应用公钥证书路径', 428, NULL, 'pay');
INSERT INTO `common_info` VALUES (866, '2025-08-05 14:40:12', NULL, '支付宝公钥证书路径', 429, NULL, 'pay');
INSERT INTO `common_info` VALUES (867, '2025-08-05 14:40:12', NULL, '支付宝根证书路径', 430, NULL, 'pay');
INSERT INTO `common_info` VALUES (868, '2025-08-05 14:40:12', NULL, '支付宝支付模式 1: 普通公钥模式 2: 证书模式', 431, '1', 'pay');
INSERT INTO `common_info` VALUES (869, '2025-08-05 14:40:12', NULL, '支付宝是否开启沙箱模式 1: 是 2: 否', 432, '2', 'pay');
INSERT INTO `common_info` VALUES (870, '2026-01-07 15:58:53', NULL, '旅游服务协议', 433, '', 'xieyi');
INSERT INTO `common_info` VALUES (871, '2026-01-07 15:59:15', NULL, '货费用说明及报名须知', 434, '', 'xieyi');
INSERT INTO `common_info` VALUES (872, '2026-01-07 15:59:25', NULL, '中国公民国内旅游文明行为公约', 435, '', 'xieyi');
INSERT INTO `common_info` VALUES (873, '2025-08-18 11:00:18', NULL, '添加客服微信', 436, '', 'xitong');
INSERT INTO `common_info` VALUES (874, '2025-09-04 13:55:51', NULL, '微信登录是否获取手机号', 437, '', 'xitong');
INSERT INTO `common_info` VALUES (875, '2025-11-17 15:23:23', NULL, '官方邀请码', 438, '', 'xitong');
INSERT INTO `common_info` VALUES (876, '2025-11-26 13:46:23', NULL, '策划师微信二维码', 439, '', 'xitong');
INSERT INTO `common_info` VALUES (877, '2025-11-26 14:12:15', NULL, '个推appid', 440, '', 'xitong');
INSERT INTO `common_info` VALUES (878, '2025-11-26 14:12:46', NULL, '个推appkey', 441, '', 'xitong');
INSERT INTO `common_info` VALUES (879, '2025-11-26 14:13:14', NULL, '个推mastersecret', 442, '', 'xitong');
INSERT INTO `common_info` VALUES (881, '2025-12-12 18:07:55', NULL, '本地上传文件夹', 740, '', 'oss');
INSERT INTO `common_info` VALUES (882, '2025-12-23 15:59:02', NULL, '订单超时时间（单位: 分钟）', 445, '', 'xitong');
INSERT INTO `common_info` VALUES (883, '2026-01-08 16:47:41', NULL, '保证金', 446, '', 'xitong');
INSERT INTO `common_info` VALUES (884, '2025-12-19 13:25:43', NULL, '保证金缴纳规则', 447, '', 'xitong');
INSERT INTO `common_info` VALUES (885, '2025-12-19 13:26:06', NULL, '保证金退款规则', 448, '', 'xitong');
INSERT INTO `common_info` VALUES (886, '2025-12-19 13:49:28', NULL, '用户取消规则说明', 449, '', 'xitong');
INSERT INTO `common_info` VALUES (887, '2025-12-19 13:49:39', NULL, '企业取消规则说明', 450, '', 'xitong');
INSERT INTO `common_info` VALUES (888, '2025-12-29 14:26:44', NULL, '用户端是否展示企业入驻', 451, '', 'kaiguan');
INSERT INTO `common_info` VALUES (889, '2025-12-23 16:25:54', NULL, '结算延迟天数', 452, '', 'xitong');
INSERT INTO `common_info` VALUES (890, '2025-12-24 19:23:39', NULL, '用户注销协议', 453, '', 'xieyi');
INSERT INTO `common_info` VALUES (891, '2025-12-24 19:23:55', NULL, '会员开通协议', 454, '', 'xieyi');
INSERT INTO `common_info` VALUES (892, '2026-01-06 14:13:14', NULL, '微信公众号appid', 455, NULL, 'pay');
INSERT INTO `common_info` VALUES (893, '2026-01-06 14:13:31', NULL, '微信APP appid', 456, '', 'pay');
INSERT INTO `common_info` VALUES (894, '2026-01-13 18:03:23', NULL, '微信支付p12证书路径', 457, '', 'pay');

-- ----------------------------
-- Table structure for customer_service_message
-- ----------------------------
DROP TABLE IF EXISTS `customer_service_message`;
CREATE TABLE `customer_service_message`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id` bigint(0) NOT NULL COMMENT '会话ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `sender_type` tinyint(0) NOT NULL COMMENT '1用户 2客服',
  `message_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'text' COMMENT 'text/image',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_customer_service_message_session_id`(`session_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客服消息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer_service_message
-- ----------------------------

-- ----------------------------
-- Table structure for customer_service_quick_question
-- ----------------------------
DROP TABLE IF EXISTS `customer_service_quick_question`;
CREATE TABLE `customer_service_quick_question`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `question` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '问题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '自动回复内容',
  `show_welcome` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1展示在欢迎区 0仅自动回复',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_cs_quick_question`(`question`) USING BTREE,
  INDEX `idx_cs_quick_question_sort`(`sort`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客服-快捷问题与自动回复' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer_service_quick_question
-- ----------------------------
INSERT INTO `customer_service_quick_question` VALUES (1, '如何预约上门回收？', '亲，旧家电回收非常方便！\n1. 在首页点击「上门回收」\n2. 选择对应品类并上传照片\n3. 提交订单后，回收员通常会在2小时内联系您确定具体时间。', 1, 1, '2026-07-21 10:39:51', '2026-07-21 10:39:51');
INSERT INTO `customer_service_quick_question` VALUES (2, '碳积分如何兑换礼品？', '请进入福利中心查看当前积分与可兑换商品，达标后可一键兑换。', 1, 2, '2026-07-21 10:39:51', '2026-07-21 10:39:51');
INSERT INTO `customer_service_quick_question` VALUES (3, '回收品类及价格标准', '平台支持旧衣、家电、数码、书籍等回收，价格以质检后实际成交结果为准。', 1, 3, '2026-07-21 10:39:51', '2026-07-21 10:39:51');
INSERT INTO `customer_service_quick_question` VALUES (4, '价格查询', '您可在各品类估价页查看参考价格，最终以质检后确认结果为准。', 0, 4, '2026-07-21 10:39:51', '2026-07-21 10:39:51');
INSERT INTO `customer_service_quick_question` VALUES (5, '订单进度', '请前往「我的订单」查看当前状态，包含待上门、取件中、结算确认、打款中等节点。', 0, 5, '2026-07-21 10:39:51', '2026-07-21 10:39:51');
INSERT INTO `customer_service_quick_question` VALUES (6, '积分兑换', '请进入福利中心查看当前积分与可兑换商品。', 0, 6, '2026-07-21 10:39:51', '2026-07-21 10:39:51');

-- ----------------------------
-- Table structure for customer_service_session
-- ----------------------------
DROP TABLE IF EXISTS `customer_service_session`;
CREATE TABLE `customer_service_session`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1进行中 2已关闭',
  `last_message_time` datetime(0) NULL DEFAULT NULL COMMENT '最近消息时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_customer_service_session_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客服会话' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer_service_session
-- ----------------------------
INSERT INTO `customer_service_session` VALUES (1, 44, 2, '2026-09-22 17:00:28', '2026-07-10 10:57:31', '2026-09-22 17:00:28');
INSERT INTO `customer_service_session` VALUES (2, 45, 1, '2026-08-07 16:38:40', '2026-07-14 16:03:54', '2026-08-07 16:38:40');
INSERT INTO `customer_service_session` VALUES (3, 47, 1, '2026-09-28 13:43:48', '2026-09-28 13:43:40', '2026-09-28 13:43:48');

-- ----------------------------
-- Table structure for customer_service_shortcut
-- ----------------------------
DROP TABLE IF EXISTS `customer_service_shortcut`;
CREATE TABLE `customer_service_shortcut`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '快捷提示词',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_cs_shortcut_content`(`content`) USING BTREE,
  INDEX `idx_cs_shortcut_sort`(`sort`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '客服-输入框上方快捷提示词' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer_service_shortcut
-- ----------------------------
INSERT INTO `customer_service_shortcut` VALUES (1, '价格查询', 1, '2026-07-21 10:39:51', '2026-07-21 10:39:51');
INSERT INTO `customer_service_shortcut` VALUES (2, '订单进度', 2, '2026-07-21 10:39:52', '2026-07-21 10:39:52');
INSERT INTO `customer_service_shortcut` VALUES (3, '积分兑换', 3, '2026-07-21 10:39:52', '2026-07-21 10:39:52');
INSERT INTO `customer_service_shortcut` VALUES (4, '转人工', 4, '2026-07-21 10:39:52', '2026-07-21 10:39:52');

-- ----------------------------
-- Table structure for digital_brand
-- ----------------------------
DROP TABLE IF EXISTS `digital_brand`;
CREATE TABLE `digital_brand`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `logo` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sort` int(0) NOT NULL DEFAULT 0,
  `state` tinyint(0) NOT NULL DEFAULT 1,
  `scene` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'digital' COMMENT '场景：digital/luxury',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数码品牌' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of digital_brand
-- ----------------------------
INSERT INTO `digital_brand` VALUES (1, 'Apple', NULL, 1, 1, 'digital', '2026-07-06 10:35:22', '2026-07-06 10:35:22');
INSERT INTO `digital_brand` VALUES (2, '华为', NULL, 2, 1, 'digital', '2026-07-06 10:35:22', '2026-07-14 11:17:18');
INSERT INTO `digital_brand` VALUES (3, '小米', NULL, 3, 1, 'digital', '2026-07-06 10:35:22', '2026-07-14 11:17:29');
INSERT INTO `digital_brand` VALUES (4, 'LV', NULL, 1, 1, 'luxury', '2026-07-14 11:15:37', '2026-07-14 11:15:37');
INSERT INTO `digital_brand` VALUES (5, 'CHANEL', NULL, 2, 1, 'luxury', '2026-07-14 11:15:37', '2026-07-14 11:15:37');
INSERT INTO `digital_brand` VALUES (6, 'GUCCI', NULL, 3, 1, 'luxury', '2026-07-14 11:15:38', '2026-07-14 11:15:38');

-- ----------------------------
-- Table structure for digital_model
-- ----------------------------
DROP TABLE IF EXISTS `digital_model`;
CREATE TABLE `digital_model`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `brand_id` bigint(0) NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `spec_json` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `base_price` decimal(10, 2) NOT NULL,
  `state` tinyint(0) NOT NULL DEFAULT 1,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_digital_model_brand`(`brand_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数码型号' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of digital_model
-- ----------------------------
INSERT INTO `digital_model` VALUES (1, 1, 'iPhone 15 Pro', '{\"storage\":\"256G\"}', 6120.00, 1, '2026-07-06 10:35:22', '2026-07-06 10:35:22');
INSERT INTO `digital_model` VALUES (2, 1, 'iPhone 14', '{\"storage\":\"128G\"}', 4200.00, 1, '2026-07-06 10:35:22', '2026-07-06 10:35:22');
INSERT INTO `digital_model` VALUES (3, 2, 'Mate 60 Pro', '{\"storage\":\"512G\"}', 4800.00, 1, '2026-07-06 10:35:22', '2026-07-06 10:35:22');
INSERT INTO `digital_model` VALUES (4, 2, 'huawei P60', NULL, 5000.00, 1, '2026-07-14 11:17:51', NULL);

-- ----------------------------
-- Table structure for digital_pricing_rule
-- ----------------------------
DROP TABLE IF EXISTS `digital_pricing_rule`;
CREATE TABLE `digital_pricing_rule`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `model_id` bigint(0) NOT NULL,
  `rule_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `rule_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `adjust_rate` decimal(6, 4) NOT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_digital_pricing_model`(`model_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数码估价规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of digital_pricing_rule
-- ----------------------------
INSERT INTO `digital_pricing_rule` VALUES (1, 1, 'condition', 'good', 0.0000, '2026-07-06 10:35:22', '2026-07-06 10:35:22');
INSERT INTO `digital_pricing_rule` VALUES (2, 1, 'condition', 'fair', -0.0800, '2026-07-06 10:35:22', '2026-07-06 10:35:22');
INSERT INTO `digital_pricing_rule` VALUES (3, 1, 'condition', 'poor', -0.1500, '2026-07-06 10:35:22', '2026-07-06 10:35:22');
INSERT INTO `digital_pricing_rule` VALUES (4, 4, '内存', '128G,256G,512G,1T', 1.0000, '2026-07-14 11:19:31', NULL);

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `opinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '意见',
  `contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系方式',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '意见反馈' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feedback
-- ----------------------------

-- ----------------------------
-- Table structure for help_article
-- ----------------------------
DROP TABLE IF EXISTS `help_article`;
CREATE TABLE `help_article`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `category_id` bigint(0) NULL DEFAULT NULL COMMENT '分类id',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '内容',
  `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '帮助中心文章' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of help_article
-- ----------------------------
INSERT INTO `help_article` VALUES (6, 1, '如何注册登录？', '<p>请查看文章详情话费卡数控刀具分即可大师傅</p>', 1, '2026-01-05 14:58:45', '2026-01-06 17:36:00');
INSERT INTO `help_article` VALUES (7, 2, 'asdf111111', '<p>333333333阿斯蒂芬阿德沙发上地方阿斯蒂芬</p>', 1, '2026-01-06 13:18:22', '2026-07-15 17:39:20');
INSERT INTO `help_article` VALUES (8, NULL, '11111111', 'kkk', 0, '2026-01-06 17:20:40', NULL);
INSERT INTO `help_article` VALUES (9, 2, '如何开通会员？', '<ol><li>ADHD卡上科技大厦；</li><li>是的科技科技圣诞快乐；</li><li>快乐就是抵抗力反对势力开始；对方的身份</li></ol>', 2, '2026-01-06 17:36:46', '2026-01-06 17:41:08');
INSERT INTO `help_article` VALUES (101, 101, '如何预约旧衣、家电和数码回收？', '<p>介绍从首页进入品类、填写地址、选择时间到提交订单的完整流程。</p><p>1. 在首页选择旧衣回收、家电回收或数码回收，进入对应业务页面。</p><p>2. 按页面提示选择地址、预约时间、服务区域或机型信息，确认回收规则后提交订单。</p><p>3. 提交成功后可在订单页面查看履约进度，并在需要时进行改约、异常反馈和价格确认。</p>', 1, '2026-07-01 00:00:00', '2026-07-15 17:32:56');
INSERT INTO `help_article` VALUES (102, 102, '订单取消、改约和异常反馈怎么操作？', '<p>说明订单页内取消订单、重新预约上门时间以及提交异常工单的入口。</p><p>1. 进入订单页，点击“取消订单”，选择原因并补充说明后提交。</p><p>2. 在订单详情或成功页点击“改约”，重新选择日期和时间段后提交即可。</p><p>3. 若出现回收员迟到、联系不上、价格争议等问题，可进入异常反馈页面提交工单。</p>', 2, '2026-07-01 00:00:00', '2026-07-01 00:00:00');
INSERT INTO `help_article` VALUES (103, 103, '回收价格、质检结算和打款规则说明', '<p>说明预估价与成交价差异、质检确认流程以及打款到账时效。</p><p>1. 前端展示的价格多为预估回收价，实际成交价会根据实物情况、称重或质检结果确认。</p><p>2. 当实际成交价与预估价差异较大时，系统会生成结算确认单，用户确认后再成交。</p><p>3. 确认成交后订单进入打款流程，用户可在打款进度页查看状态与到账信息。</p>', 3, '2026-07-01 00:00:00', '2026-07-01 00:00:00');
INSERT INTO `help_article` VALUES (104, 104, '邀请好友、福利任务和提现常见问题', '<p>整理邀请分佣、签到奖励、任务领取和申请提现的常见说明。</p><p>1. 可在推荐收益页查看邀请规则、邀请记录、排行榜和佣金明细。</p><p>2. 福利中心支持签到、邀请好友、发布回收等任务，完成后可获得对应奖励。</p><p>3. 当前前端演示中最低提现金额为 10 元，提交申请后会进入审核与打款流程。</p>', 4, '2026-07-01 00:00:00', '2026-07-01 00:00:00');
INSERT INTO `help_article` VALUES (105, 105, '账号登录、修改密码和资料设置问题', '<p>介绍登录注册、忘记密码、修改密码以及编辑个人资料的方法。</p><p>1. 用户可通过手机号登录，忘记密码时可通过验证码重置。</p><p>2. 进入设置页后点击修改密码，填写当前密码、新密码和确认密码后提交即可。</p><p>3. 在个人资料页可修改头像、昵称、手机号、性别等基础信息。</p>', 5, '2026-07-01 00:00:00', '2026-07-01 00:00:00');

-- ----------------------------
-- Table structure for help_category
-- ----------------------------
DROP TABLE IF EXISTS `help_category`;
CREATE TABLE `help_category`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '名称',
  `client_type` int(0) NULL DEFAULT NULL COMMENT '客户端类型 1: 用户端',
  `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '帮助中心分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of help_category
-- ----------------------------
INSERT INTO `help_category` VALUES (1, '登录问题1', 1, 1, '2025-09-30 14:05:12', '2025-10-14 16:43:53');
INSERT INTO `help_category` VALUES (2, '会员问题1', 1, 2, '2025-09-30 14:05:27', '2025-10-14 16:44:03');
INSERT INTO `help_category` VALUES (101, '下单指南', 1, 1, '2026-07-01 00:00:00', '2026-07-01 00:00:00');
INSERT INTO `help_category` VALUES (102, '订单问题', 1, 2, '2026-07-01 00:00:00', '2026-07-01 00:00:00');
INSERT INTO `help_category` VALUES (103, '价格结算', 1, 3, '2026-07-01 00:00:00', '2026-07-01 00:00:00');
INSERT INTO `help_category` VALUES (104, '福利收益', 1, 4, '2026-07-01 00:00:00', '2026-07-01 00:00:00');
INSERT INTO `help_category` VALUES (105, '账号安全', 1, 5, '2026-07-01 00:00:00', '2026-07-01 00:00:00');

-- ----------------------------
-- Table structure for help_question
-- ----------------------------
DROP TABLE IF EXISTS `help_question`;
CREATE TABLE `help_question`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '问题ID',
  `category_id` bigint(0) NOT NULL COMMENT '分类ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `sort` int(0) NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态 1启用',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `del_flag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_help_question_category`(`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帮助问题' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of help_question
-- ----------------------------

-- ----------------------------
-- Table structure for invite
-- ----------------------------
DROP TABLE IF EXISTS `invite`;
CREATE TABLE `invite`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '邀请者id',
  `invitee_user_id` int(0) NULL DEFAULT NULL COMMENT '被邀请者id',
  `money` decimal(10, 2) NULL DEFAULT NULL COMMENT '收益',
  `create_time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建时间',
  `money_type` int(0) NULL DEFAULT NULL COMMENT '1会员2陪玩',
  `state` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '邀请信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of invite
-- ----------------------------

-- ----------------------------
-- Table structure for invite_money
-- ----------------------------
DROP TABLE IF EXISTS `invite_money`;
CREATE TABLE `invite_money`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '收益钱包id',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户id',
  `money_sum` decimal(10, 2) NULL DEFAULT NULL COMMENT '总获取收益',
  `money` decimal(10, 2) NULL DEFAULT NULL COMMENT '当前金额',
  `cash_out` decimal(10, 2) NULL DEFAULT NULL COMMENT '累计提现',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '收益钱包' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of invite_money
-- ----------------------------

-- ----------------------------
-- Table structure for message_info
-- ----------------------------
DROP TABLE IF EXISTS `message_info`;
CREATE TABLE `message_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '内容',
  `create_at` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建时间',
  `image` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图片',
  `is_see` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `send_state` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `send_time` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `state` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '1为公告2位用户反馈 3为投诉服务 4为投诉店铺 5为用户消息 6客服消息',
  `title` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标题',
  `url` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '地址',
  `type` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `platform` int(0) NULL DEFAULT NULL COMMENT '来源',
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户id',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名',
  `audit_content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `status` int(0) NULL DEFAULT NULL,
  `by_user_id` int(0) NULL DEFAULT NULL,
  `platform_id` int(0) NULL DEFAULT NULL,
  `shop_id` int(0) NULL DEFAULT NULL COMMENT '店铺id',
  `data_id` int(0) NULL DEFAULT NULL COMMENT '服务id',
  `phone` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系方式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '消息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_info
-- ----------------------------

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '消息内容',
  `image_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '图片URL，逗号分隔',
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '跳转链接',
  `type` tinyint(0) NOT NULL COMMENT '消息类型: 1=系统公告, 2=用户消息',
  `user_id` bigint(0) UNSIGNED NOT NULL COMMENT '接收用户ID',
  `data_id` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '关联数据ID（如服务ID、订单ID等）',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读: 0=未读, 1=已读',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id_is_read`(`user_id`, `is_read`) USING BTREE COMMENT '用户消息状态查询索引',
  INDEX `idx_type_create_time`(`type`, `create_time`) USING BTREE COMMENT '按类型和时间查询索引',
  INDEX `idx_user_id_create_time`(`user_id`, `create_time`) USING BTREE COMMENT '用户消息时间线索引'
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '消息信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------

-- ----------------------------
-- Table structure for order_dispute
-- ----------------------------
DROP TABLE IF EXISTS `order_dispute`;
CREATE TABLE `order_dispute`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '争议ID',
  `order_id` bigint(0) NOT NULL COMMENT '订单ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `reason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '争议原因',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '争议说明',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/RESOLVED/CANCELLED',
  `resolve_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处理备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_dispute_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '价格争议' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_dispute
-- ----------------------------

-- ----------------------------
-- Table structure for order_exception
-- ----------------------------
DROP TABLE IF EXISTS `order_exception`;
CREATE TABLE `order_exception`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NOT NULL,
  `exception_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `expect_action` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_exception_order`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单异常反馈' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_exception
-- ----------------------------

-- ----------------------------
-- Table structure for order_payout
-- ----------------------------
DROP TABLE IF EXISTS `order_payout`;
CREATE TABLE `order_payout`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '打款单ID',
  `payout_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '打款单号',
  `order_id` bigint(0) NOT NULL COMMENT '订单ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `amount` decimal(12, 2) NOT NULL COMMENT '打款金额',
  `pay_channel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'WECHAT' COMMENT '打款渠道',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/PROCESSING/SUCCESS/FAILED',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `success_time` datetime(0) NULL DEFAULT NULL COMMENT '到账时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_payout_order_id`(`order_id`) USING BTREE,
  INDEX `idx_order_payout_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单打款单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_payout
-- ----------------------------

-- ----------------------------
-- Table structure for order_payout_log
-- ----------------------------
DROP TABLE IF EXISTS `order_payout_log`;
CREATE TABLE `order_payout_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `payout_id` bigint(0) NOT NULL COMMENT '打款单ID',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志内容',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_payout_log_payout_id`(`payout_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '打款日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_payout_log
-- ----------------------------

-- ----------------------------
-- Table structure for order_reschedule
-- ----------------------------
DROP TABLE IF EXISTS `order_reschedule`;
CREATE TABLE `order_reschedule`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NOT NULL,
  `reason` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `new_appointment_time` datetime(0) NULL DEFAULT NULL,
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_reschedule_order`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单改约记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_reschedule
-- ----------------------------

-- ----------------------------
-- Table structure for order_review
-- ----------------------------
DROP TABLE IF EXISTS `order_review`;
CREATE TABLE `order_review`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint(0) NOT NULL COMMENT '订单ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `score` int(0) NOT NULL DEFAULT 5 COMMENT '评分1-5',
  `complaint_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '投诉类型',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评价内容',
  `admin_reply` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客服回复',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_review_order_id`(`order_id`) USING BTREE,
  INDEX `idx_order_review_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单评价' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_review
-- ----------------------------

-- ----------------------------
-- Table structure for order_settlement
-- ----------------------------
DROP TABLE IF EXISTS `order_settlement`;
CREATE TABLE `order_settlement`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '结算单ID',
  `settlement_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '结算单号',
  `order_id` bigint(0) NOT NULL COMMENT '订单ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `estimate_amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '预估金额',
  `actual_amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '实际金额',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/CONFIRMED/DISPUTED',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_settlement_order_id`(`order_id`) USING BTREE,
  INDEX `idx_order_settlement_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单结算单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_settlement
-- ----------------------------

-- ----------------------------
-- Table structure for pay_classify
-- ----------------------------
DROP TABLE IF EXISTS `pay_classify`;
CREATE TABLE `pay_classify`  (
  `pay_classify_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '充值分类id',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '售价',
  `give_money` decimal(10, 2) NULL DEFAULT NULL COMMENT '赠送数量',
  `money` decimal(10, 2) NULL DEFAULT NULL COMMENT '水晶数量',
  `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `create_time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`pay_classify_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '充值分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pay_classify
-- ----------------------------

-- ----------------------------
-- Table structure for pay_details
-- ----------------------------
DROP TABLE IF EXISTS `pay_details`;
CREATE TABLE `pay_details`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '充值id',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `platform` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '支付平台 1微信 2支付宝 3paypal 4stripe 5余额',
  `trade_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '支付类型 应用：APP 网页：WEB 小程序：JSAPI',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '订单编号',
  `trade_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '第三方平台支付流水号',
  `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额',
  `business_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '业务类型',
  `state` tinyint(0) NULL DEFAULT NULL COMMENT '0待支付 1支付成功 2失败',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `extend` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '扩展字段',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 459 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户支付详情' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pay_details
-- ----------------------------

-- ----------------------------
-- Table structure for pickup_task
-- ----------------------------
DROP TABLE IF EXISTS `pickup_task`;
CREATE TABLE `pickup_task`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `task_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务编号',
  `order_id` bigint(0) NULL DEFAULT NULL COMMENT '关联订单ID',
  `staff_id` bigint(0) NOT NULL COMMENT '员工用户ID',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT '任务状态: PENDING/RUNNING/DONE/CANCELLED',
  `appointment_time` datetime(0) NULL DEFAULT NULL COMMENT '预约时间',
  `contact_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '取件地址',
  `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回收品类',
  `item_summary` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物品摘要',
  `finished_at` datetime(0) NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pickup_task_staff_id`(`staff_id`) USING BTREE,
  INDEX `idx_pickup_task_status`(`status`) USING BTREE,
  INDEX `idx_pickup_task_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '取件任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pickup_task
-- ----------------------------

-- ----------------------------
-- Table structure for pickup_task_log
-- ----------------------------
DROP TABLE IF EXISTS `pickup_task_log`;
CREATE TABLE `pickup_task_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `task_id` bigint(0) NOT NULL COMMENT '任务ID',
  `action_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '动作: ASSIGN/START/FINISH',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志内容',
  `operator_id` bigint(0) NULL DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pickup_task_log_task_id`(`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 119 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '取件任务日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pickup_task_log
-- ----------------------------

-- ----------------------------
-- Table structure for product_brand
-- ----------------------------
DROP TABLE IF EXISTS `product_brand`;
CREATE TABLE `product_brand`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` bigint(0) NOT NULL COMMENT '商品分类ID',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '品牌名称',
  `image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1启用 2禁用',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_brand_category`(`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品品牌' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_brand
-- ----------------------------
INSERT INTO `product_brand` VALUES (1, 1, '海尔', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/13/fb7e9216-05a1-45ac-bf80-9bf858a77c1c.png', 0, 1, '2026-07-13 17:06:30', NULL);
INSERT INTO `product_brand` VALUES (2, 1, '美的', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/13/06a2d19e-f51f-4eb8-8fe0-b1503b6a83a3.jpeg', 1, 1, '2026-07-13 17:06:45', NULL);
INSERT INTO `product_brand` VALUES (3, 6, '卡地亚', NULL, 0, 1, '2026-07-13 17:17:41', NULL);
INSERT INTO `product_brand` VALUES (4, 6, '香奈儿', NULL, 0, 1, '2026-07-13 17:17:49', NULL);
INSERT INTO `product_brand` VALUES (5, 6, '宝格丽', NULL, 0, 1, '2026-07-13 17:17:59', NULL);
INSERT INTO `product_brand` VALUES (6, 6, '爱马仕', NULL, 0, 1, '2026-07-13 17:18:04', NULL);
INSERT INTO `product_brand` VALUES (7, 5, '香奈儿', NULL, 0, 1, '2026-07-13 17:18:46', NULL);
INSERT INTO `product_brand` VALUES (8, 5, 'LV', NULL, 0, 1, '2026-07-13 17:18:51', NULL);
INSERT INTO `product_brand` VALUES (9, 5, '古驰', NULL, 0, 1, '2026-07-13 17:19:04', NULL);
INSERT INTO `product_brand` VALUES (10, 4, '百达翡丽', NULL, 0, 1, '2026-07-13 17:19:28', NULL);
INSERT INTO `product_brand` VALUES (11, 4, '江诗丹顿', NULL, 0, 1, '2026-07-13 17:19:38', NULL);
INSERT INTO `product_brand` VALUES (12, 4, '劳力士', NULL, 0, 1, '2026-07-13 17:19:45', NULL);
INSERT INTO `product_brand` VALUES (13, 3, '苹果', NULL, 0, 1, '2026-07-13 17:34:45', '2026-07-13 17:43:50');
INSERT INTO `product_brand` VALUES (14, 3, '小米', NULL, 0, 1, '2026-07-13 17:34:54', NULL);
INSERT INTO `product_brand` VALUES (15, 3, '华为', NULL, 0, 1, '2026-07-13 17:35:03', '2026-07-13 17:43:43');
INSERT INTO `product_brand` VALUES (16, 9, '格力', NULL, 0, 1, '2026-07-14 13:33:20', '2026-07-14 13:33:20');
INSERT INTO `product_brand` VALUES (17, 9, '美的', NULL, 0, 1, '2026-07-14 13:33:22', '2026-07-14 13:33:22');
INSERT INTO `product_brand` VALUES (18, 9, '海尔', NULL, 0, 1, '2026-07-14 13:33:27', '2026-07-14 13:33:27');
INSERT INTO `product_brand` VALUES (19, 9, '奥克斯', NULL, 0, 1, '2026-07-14 13:33:28', '2026-07-14 13:33:28');
INSERT INTO `product_brand` VALUES (20, 1, '容声', NULL, 0, 1, '2026-07-14 13:33:31', '2026-07-14 13:33:31');
INSERT INTO `product_brand` VALUES (21, 1, '西门子', NULL, 0, 1, '2026-07-14 13:33:32', '2026-07-14 13:33:32');
INSERT INTO `product_brand` VALUES (22, 10, '小天鹅', NULL, 0, 1, '2026-07-14 13:33:32', '2026-07-14 13:33:32');
INSERT INTO `product_brand` VALUES (23, 10, '海尔', NULL, 0, 1, '2026-07-14 13:33:33', '2026-07-14 13:33:33');
INSERT INTO `product_brand` VALUES (24, 10, '美的', NULL, 0, 1, '2026-07-14 13:33:33', '2026-07-14 13:33:33');
INSERT INTO `product_brand` VALUES (25, 10, '松下', NULL, 0, 1, '2026-07-14 13:33:33', '2026-07-14 13:33:33');
INSERT INTO `product_brand` VALUES (26, 11, '小米', NULL, 0, 1, '2026-07-14 13:33:34', '2026-07-14 13:33:34');
INSERT INTO `product_brand` VALUES (27, 11, '海信', NULL, 0, 1, '2026-07-14 13:33:34', '2026-07-14 13:33:34');
INSERT INTO `product_brand` VALUES (28, 11, 'TCL', NULL, 0, 1, '2026-07-14 13:33:34', '2026-07-14 13:33:34');
INSERT INTO `product_brand` VALUES (29, 11, '索尼', NULL, 0, 1, '2026-07-14 13:33:35', '2026-07-14 13:33:35');
INSERT INTO `product_brand` VALUES (30, 12, 'Apple', NULL, 1, 1, '2026-07-14 13:33:36', '2026-07-14 13:33:36');
INSERT INTO `product_brand` VALUES (31, 12, '华为', NULL, 2, 1, '2026-07-14 13:33:37', '2026-07-14 13:33:37');
INSERT INTO `product_brand` VALUES (32, 12, '小米', NULL, 3, 1, '2026-07-14 13:33:39', '2026-07-14 13:33:39');
INSERT INTO `product_brand` VALUES (33, 13, 'LV', NULL, 1, 1, '2026-07-14 13:33:40', '2026-07-14 13:33:40');
INSERT INTO `product_brand` VALUES (34, 13, 'CHANEL', NULL, 2, 1, '2026-07-14 13:33:40', '2026-07-14 13:33:40');
INSERT INTO `product_brand` VALUES (35, 13, 'GUCCI', NULL, 3, 1, '2026-07-14 13:33:40', '2026-07-14 13:33:40');
INSERT INTO `product_brand` VALUES (37, 15, '奥克斯', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/08/12/ef935ab3-5007-4ad3-914e-0ec0939741ae.png', 1, 1, '2026-08-12 19:41:50', NULL);
INSERT INTO `product_brand` VALUES (38, 15, '格力', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/08/12/d2ac340d-73ae-4bd9-99c3-9fa55ca6b96f.png', 2, 1, '2026-08-12 19:42:02', NULL);
INSERT INTO `product_brand` VALUES (39, 15, '美的', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/08/12/ad46d103-5cb4-4b88-8578-175263e2a64f.png', 3, 1, '2026-08-12 19:42:14', NULL);

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `recycle_category_id` bigint(0) NOT NULL COMMENT '绑定的回收品类ID',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1启用 2禁用',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_category_recycle`(`recycle_category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_category
-- ----------------------------
INSERT INTO `product_category` VALUES (1, 2, '冰箱', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/13/3263992d-4982-4edd-9995-0dc651bf63df.png', 1, 1, '2026-07-13 17:06:03', NULL);
INSERT INTO `product_category` VALUES (2, 3, '电脑', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/13/aa7499f7-6455-4dc0-9100-d52a81f23c4d.png', 1, 1, '2026-07-13 17:13:51', NULL);
INSERT INTO `product_category` VALUES (3, 3, '手机', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/13/6bc366e3-b080-4bd0-a2e6-fd5123bf41f8.png', 0, 1, '2026-07-13 17:14:21', NULL);
INSERT INTO `product_category` VALUES (4, 4, '手表', NULL, 0, 1, '2026-07-13 17:14:45', NULL);
INSERT INTO `product_category` VALUES (5, 4, '包包', NULL, 0, 1, '2026-07-13 17:15:02', NULL);
INSERT INTO `product_category` VALUES (6, 4, '珠宝', NULL, 0, 1, '2026-07-13 17:15:23', NULL);
INSERT INTO `product_category` VALUES (7, 1, '旧衣', NULL, 0, 1, '2026-07-13 17:15:43', NULL);
INSERT INTO `product_category` VALUES (8, 5, '旧书', NULL, 0, 1, '2026-07-13 17:15:57', NULL);
INSERT INTO `product_category` VALUES (9, 2, '空调', 'star', 1, 1, '2026-07-14 13:33:20', '2026-07-14 13:33:20');
INSERT INTO `product_category` VALUES (10, 2, '洗衣机', 'grid', 3, 1, '2026-07-14 13:33:20', '2026-07-14 13:33:20');
INSERT INTO `product_category` VALUES (11, 2, '电视', 'photo', 4, 1, '2026-07-14 13:33:20', '2026-07-14 13:33:20');
INSERT INTO `product_category` VALUES (12, 3, '数码', NULL, 0, 1, '2026-07-14 13:33:36', '2026-07-14 13:33:36');
INSERT INTO `product_category` VALUES (13, 4, '奢侈品', NULL, 0, 1, '2026-07-14 13:33:39', '2026-07-14 13:33:39');
INSERT INTO `product_category` VALUES (15, 2, '冰柜', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/08/12/9012e54f-bf6e-4847-9a31-e134e213ce29.png', 5, 1, '2026-08-12 19:41:31', NULL);

-- ----------------------------
-- Table structure for product_model
-- ----------------------------
DROP TABLE IF EXISTS `product_model`;
CREATE TABLE `product_model`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `brand_id` bigint(0) NOT NULL COMMENT '品牌ID',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '型号名称',
  `image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `base_price` decimal(12, 2) NOT NULL COMMENT '基础估价',
  `min_price` decimal(12, 2) NOT NULL COMMENT '最低估值',
  `max_price` decimal(12, 2) NOT NULL COMMENT '最高估值',
  `spec_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '规格参数JSON',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1启用 2禁用',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_model_brand`(`brand_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品型号' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_model
-- ----------------------------
INSERT INTO `product_model` VALUES (1, 1, '其他型号', NULL, 1000.00, 1000.00, 1000.00, NULL, 0, 1, '2026-07-14 13:33:19', '2026-07-28 18:04:46');
INSERT INTO `product_model` VALUES (2, 9, '默认型号', NULL, 0.00, 0.00, 0.00, NULL, 0, 1, '2026-07-14 13:33:19', '2026-07-14 13:33:19');
INSERT INTO `product_model` VALUES (3, 10, '默认型号', NULL, 0.00, 0.00, 0.00, NULL, 0, 1, '2026-07-14 13:33:19', '2026-07-14 13:33:19');
INSERT INTO `product_model` VALUES (4, 15, '默认型号', NULL, 1250.00, 1000.00, 1500.00, NULL, 0, 1, '2026-07-14 13:33:19', '2026-09-24 11:43:04');
INSERT INTO `product_model` VALUES (8, 16, '云佳 1.5匹', NULL, 680.00, 680.00, 680.00, '{\"brand\":\"格力\",\"type\":\"挂机\",\"capacity\":\"1.5匹\"}', 0, 1, '2026-07-14 13:33:20', '2026-07-14 13:33:20');
INSERT INTO `product_model` VALUES (9, 16, '云锦 2匹', NULL, 920.00, 920.00, 920.00, '{\"brand\":\"格力\",\"type\":\"柜机\",\"capacity\":\"2匹\"}', 0, 1, '2026-07-14 13:33:21', '2026-07-14 13:33:21');
INSERT INTO `product_model` VALUES (10, 16, '天丽 大1匹', NULL, 560.00, 560.00, 560.00, '{\"brand\":\"格力\",\"type\":\"挂机\",\"capacity\":\"1匹\"}', 0, 1, '2026-07-14 13:33:21', '2026-07-14 13:33:21');
INSERT INTO `product_model` VALUES (11, 17, '风尊 1.5匹', NULL, 650.00, 650.00, 650.00, '{\"brand\":\"美的\",\"type\":\"挂机\",\"capacity\":\"1.5匹\"}', 0, 1, '2026-07-14 13:33:22', '2026-07-14 13:33:22');
INSERT INTO `product_model` VALUES (12, 17, '酷省电 大1匹', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/09/24/4da409a4-26a8-4c3b-962d-e20c6511616a.png', 350.00, 200.00, 500.00, '{\"brand\":\"美的\",\"type\":\"挂机\",\"capacity\":\"1匹\"}', 0, 1, '2026-07-14 13:33:27', '2026-09-24 15:56:15');
INSERT INTO `product_model` VALUES (13, 18, '静悦 1.5匹', NULL, 620.00, 620.00, 620.00, '{\"brand\":\"海尔\",\"type\":\"挂机\",\"capacity\":\"1.5匹\"}', 0, 1, '2026-07-14 13:33:27', '2026-07-14 13:33:27');
INSERT INTO `product_model` VALUES (14, 19, '京裕 大1匹', NULL, 340.00, 200.00, 480.00, '{\"brand\":\"奥克斯\",\"type\":\"挂机\",\"capacity\":\"1匹\"}', 0, 1, '2026-07-14 13:33:28', '2026-09-24 11:19:04');
INSERT INTO `product_model` VALUES (15, 1, 'BCD-501双系统双净化', NULL, 980.00, 980.00, 980.00, '{\"brand\":\"海尔\",\"doorType\":\"十字对开门\",\"capacity\":\"400L以上\"}', 0, 1, '2026-07-14 13:33:29', '2026-07-28 18:11:08');
INSERT INTO `product_model` VALUES (16, 1, 'BCD-535全空间保鲜+EPP超净', NULL, 860.00, 860.00, 860.00, '{\"brand\":\"海尔\",\"doorType\":\"十字对开门\",\"capacity\":\"400L以上\"}', 0, 1, '2026-07-14 13:33:29', '2026-07-28 18:04:09');
INSERT INTO `product_model` VALUES (17, 2, 'BCD-478WSPZM(E)', NULL, 760.00, 760.00, 760.00, '{\"brand\":\"美的\",\"doorType\":\"三门\",\"capacity\":\"300-400L\"}', 0, 1, '2026-07-14 13:33:30', '2026-07-14 13:33:30');
INSERT INTO `product_model` VALUES (18, 2, 'BCD-407WSPZM(E)', NULL, 680.00, 680.00, 680.00, '{\"brand\":\"美的\",\"doorType\":\"双门\",\"capacity\":\"300-400L\"}', 0, 1, '2026-07-14 13:33:30', '2026-07-14 13:33:30');
INSERT INTO `product_model` VALUES (19, 20, '515方糖冰箱', NULL, 820.00, 820.00, 820.00, '{\"brand\":\"容声\",\"doorType\":\"十字对开门\",\"capacity\":\"400L以上\"}', 0, 1, '2026-07-14 13:33:31', '2026-07-28 18:02:52');
INSERT INTO `product_model` VALUES (20, 21, '279L 两门冰箱‌', NULL, 1180.00, 1180.00, 1180.00, '{\"brand\":\"西门子\",\"doorType\":\"多门法式\",\"capacity\":\"400L以上\"}', 0, 1, '2026-07-14 13:33:32', '2026-07-28 18:01:39');
INSERT INTO `product_model` VALUES (21, 22, 'TG100V86WMDY5', NULL, 620.00, 620.00, 620.00, '{\"brand\":\"小天鹅\",\"type\":\"滚筒\",\"capacity\":\"8-10kg\"}', 0, 1, '2026-07-14 13:33:32', '2026-07-14 13:33:32');
INSERT INTO `product_model` VALUES (22, 22, 'TD100V618T', NULL, 880.00, 880.00, 880.00, '{\"brand\":\"小天鹅\",\"type\":\"洗烘一体\",\"capacity\":\"8-10kg\"}', 0, 1, '2026-07-14 13:33:33', '2026-07-14 13:33:33');
INSERT INTO `product_model` VALUES (23, 23, 'EG100MATE3S', NULL, 580.00, 580.00, 580.00, '{\"brand\":\"海尔\",\"type\":\"滚筒\",\"capacity\":\"8-10kg\"}', 0, 1, '2026-07-14 13:33:33', '2026-07-14 13:33:33');
INSERT INTO `product_model` VALUES (24, 24, 'MG100VT55DY', NULL, 520.00, 520.00, 520.00, '{\"brand\":\"美的\",\"type\":\"滚筒\",\"capacity\":\"6-8kg\"}', 0, 1, '2026-07-14 13:33:33', '2026-07-14 13:33:33');
INSERT INTO `product_model` VALUES (25, 25, 'XQG100-EGALW', NULL, 760.00, 760.00, 760.00, '{\"brand\":\"松下\",\"type\":\"滚筒\",\"capacity\":\"8-10kg\"}', 0, 1, '2026-07-14 13:33:33', '2026-07-14 13:33:33');
INSERT INTO `product_model` VALUES (26, 26, 'EA55 2024款', NULL, 520.00, 520.00, 520.00, '{\"brand\":\"小米\",\"screenType\":\"液晶 LED\",\"size\":\"43-55寸\"}', 0, 1, '2026-07-14 13:33:34', '2026-07-14 13:33:34');
INSERT INTO `product_model` VALUES (27, 26, 'S65 Mini LED', NULL, 760.00, 760.00, 760.00, '{\"brand\":\"小米\",\"screenType\":\"Mini LED\",\"size\":\"55-65寸\"}', 0, 1, '2026-07-14 13:33:34', '2026-07-14 13:33:34');
INSERT INTO `product_model` VALUES (28, 27, '55E5N Pro', NULL, 580.00, 580.00, 580.00, '{\"brand\":\"海信\",\"screenType\":\"液晶 LED\",\"size\":\"43-55寸\"}', 0, 1, '2026-07-14 13:33:34', '2026-07-14 13:33:34');
INSERT INTO `product_model` VALUES (29, 28, '55Q10K', NULL, 620.00, 620.00, 620.00, '{\"brand\":\"TCL\",\"screenType\":\"Mini LED\",\"size\":\"43-55寸\"}', 0, 1, '2026-07-14 13:33:35', '2026-07-14 13:33:35');
INSERT INTO `product_model` VALUES (30, 29, 'XR-55X90L', NULL, 980.00, 980.00, 980.00, '{\"brand\":\"索尼\",\"screenType\":\"OLED\",\"size\":\"43-55寸\"}', 0, 1, '2026-07-14 13:33:35', '2026-07-14 13:33:35');
INSERT INTO `product_model` VALUES (31, 30, 'iPhone 15 Pro', NULL, 6560.00, 6120.00, 7000.00, '{\"storage\":\"256G\"}', 0, 1, '2026-07-14 13:33:37', '2026-09-24 11:37:22');
INSERT INTO `product_model` VALUES (32, 30, 'iPhone 14', NULL, 4200.00, 4200.00, 4200.00, '{\"storage\":\"128G\"}', 0, 1, '2026-07-14 13:33:37', '2026-07-14 13:33:37');
INSERT INTO `product_model` VALUES (33, 31, 'Mate 60 Pro', NULL, 4800.00, 4800.00, 4800.00, '{\"storage\":\"512G\"}', 0, 1, '2026-07-14 13:33:37', '2026-07-14 13:33:37');
INSERT INTO `product_model` VALUES (34, 31, 'huawei P60', NULL, 5000.00, 5000.00, 5000.00, NULL, 0, 1, '2026-07-14 13:33:38', '2026-07-14 13:33:38');
INSERT INTO `product_model` VALUES (36, 37, 'A01', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/08/12/b530fc9d-1c0d-4737-9204-cc218304de72.png', 100.00, 100.00, 100.00, NULL, 1, 1, '2026-08-12 19:42:30', NULL);

-- ----------------------------
-- Table structure for product_spec
-- ----------------------------
DROP TABLE IF EXISTS `product_spec`;
CREATE TABLE `product_spec`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `model_id` bigint(0) NOT NULL COMMENT '型号ID',
  `attribute_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规格名称',
  `attributes_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '规格属性值JSON',
  `multi_select` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否多选 0否1是',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_spec_model`(`model_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品规格' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_spec
-- ----------------------------
INSERT INTO `product_spec` VALUES (1, 1, '型号', '[{\"attName\":\"静悦1.5匹\",\"attPrice\":\"200\",\"id\":\"1\",\"img\":\"\",\"specId\":\"1\"},{\"attName\":\"静悦2.0匹\",\"attPrice\":\"250\",\"id\":\"2\",\"img\":\"\",\"specId\":\"1\"}]', 0, 1, '2026-07-13 17:08:10', '2026-07-14 15:22:03');
INSERT INTO `product_spec` VALUES (2, 1, '空调类型', '[{\"attName\":\"挂机\",\"attPrice\":\"100\",\"id\":\"1\",\"img\":\"\",\"specId\":\"2\"},{\"attName\":\"柜机\",\"attPrice\":\"150\",\"id\":\"2\",\"img\":\"\",\"specId\":\"2\"},{\"attName\":\"中央空调机\",\"attPrice\":\"200\",\"id\":\"3\",\"img\":\"\",\"specId\":\"2\"}]', 0, 2, '2026-07-13 17:09:15', '2026-07-14 15:21:58');
INSERT INTO `product_spec` VALUES (3, 1, '损伤情况', '[{\"attName\":\"无明显故障\",\"attPrice\":\"500\",\"id\":\"1\",\"img\":\"\",\"specId\":\"3\"},{\"attName\":\"轻微磕碰\",\"attPrice\":\"450\",\"id\":\"2\",\"img\":\"\",\"specId\":\"3\"},{\"attName\":\"制冷效果一般\",\"attPrice\":\"400\",\"id\":\"3\",\"img\":\"\",\"specId\":\"3\"},{\"attName\":\"无法制冷\",\"attPrice\":\"200\",\"id\":\"4\",\"img\":\"\",\"specId\":\"3\"}]', 0, 3, '2026-07-13 17:10:45', '2026-07-14 15:21:53');
INSERT INTO `product_spec` VALUES (4, 1, '新旧程度', '[{\"attName\":\"99新\",\"attPrice\":\"200\",\"id\":\"1\",\"img\":\"\",\"specId\":\"4\"},{\"attName\":\"95新\",\"attPrice\":\"180\",\"id\":\"2\",\"img\":\"\",\"specId\":\"4\"},{\"attName\":\"9成新\",\"attPrice\":\"150\",\"id\":\"3\",\"img\":\"\",\"specId\":\"4\"},{\"attName\":\"85新\",\"attPrice\":\"130\",\"id\":\"4\",\"img\":\"\",\"specId\":\"4\"}]', 0, 4, '2026-07-13 17:11:46', '2026-07-14 15:21:45');
INSERT INTO `product_spec` VALUES (5, 1, '使用年限', '[{\"attName\":\"1年内\",\"attPrice\":\"200\",\"id\":\"1\",\"img\":\"\",\"specId\":\"5\"},{\"attName\":\"1-3年\",\"attPrice\":\"150\",\"id\":\"2\",\"img\":\"\",\"specId\":\"5\"},{\"attName\":\"3-5年\",\"attPrice\":\"100\",\"id\":\"3\",\"img\":\"\",\"specId\":\"5\"},{\"attName\":\"5年以上\",\"attPrice\":\"50\",\"id\":\"4\",\"img\":\"\",\"specId\":\"5\"}]', 0, 5, '2026-07-13 17:12:50', '2026-07-14 15:21:38');
INSERT INTO `product_spec` VALUES (6, 3, '系列', '[{\"attName\":\"运动优雅\",\"attPrice\":\"500\",\"id\":\"1\",\"img\":\"\",\"specId\":\"6\"},{\"attName\":\"超级复杂功能计时\",\"attPrice\":\"800\",\"id\":\"2\",\"img\":\"\",\"specId\":\"6\"},{\"attName\":\"古典表\",\"attPrice\":\"1000\",\"id\":\"3\",\"img\":\"\",\"specId\":\"6\"}]', 0, 0, '2026-07-13 17:22:58', '2026-07-13 17:22:58');
INSERT INTO `product_spec` VALUES (7, 3, '染色', '[{\"attName\":\"黑色系\",\"attPrice\":\"300\",\"id\":\"1\",\"img\":\"\",\"specId\":\"7\"},{\"attName\":\"蓝色系\",\"attPrice\":\"300\",\"id\":\"2\",\"img\":\"\",\"specId\":\"7\"},{\"attName\":\"银色系\",\"attPrice\":\"350\",\"id\":\"3\",\"img\":\"\",\"specId\":\"7\"},{\"attName\":\"棕色系\",\"attPrice\":\"500\",\"id\":\"4\",\"img\":\"\",\"specId\":\"7\"}]', 0, 0, '2026-07-13 17:24:19', '2026-07-13 17:24:19');
INSERT INTO `product_spec` VALUES (8, 3, '机芯类型', '[{\"attName\":\"自动机械\",\"attPrice\":\"500\",\"id\":\"1\",\"img\":\"\",\"specId\":\"8\"},{\"attName\":\"石英\",\"attPrice\":\"600\",\"id\":\"2\",\"img\":\"\",\"specId\":\"8\"},{\"attName\":\"机械\",\"attPrice\":\"700\",\"id\":\"3\",\"img\":\"\",\"specId\":\"8\"}]', 0, 0, '2026-07-13 17:25:00', '2026-07-13 17:25:01');
INSERT INTO `product_spec` VALUES (9, 3, '表镜', '[{\"attName\":\"35-40mm\",\"attPrice\":\"500\",\"id\":\"1\",\"img\":\"\",\"specId\":\"9\"},{\"attName\":\"40mm以上\",\"attPrice\":\"550\",\"id\":\"2\",\"img\":\"\",\"specId\":\"9\"},{\"attName\":\"30-35mm\",\"attPrice\":\"450\",\"id\":\"3\",\"img\":\"\",\"specId\":\"9\"}]', 0, 0, '2026-07-13 17:25:57', '2026-07-13 17:25:57');
INSERT INTO `product_spec` VALUES (10, 2, '系列', '[{\"attName\":\"铂金包\",\"attPrice\":\"500\",\"id\":\"1\",\"img\":\"\",\"specId\":\"10\"},{\"attName\":\"凯莉包\",\"attPrice\":\"600\",\"id\":\"2\",\"img\":\"\",\"specId\":\"10\"},{\"attName\":\"康康包\",\"attPrice\":\"700\",\"id\":\"3\",\"img\":\"\",\"specId\":\"10\"},{\"attName\":\"林迪包\",\"attPrice\":\"800\",\"id\":\"4\",\"img\":\"\",\"specId\":\"10\"}]', 0, 0, '2026-07-13 17:27:31', '2026-07-13 17:27:31');
INSERT INTO `product_spec` VALUES (11, 2, '尺寸(cm)', '[{\"attName\":\"25*17*7\",\"attPrice\":\"500\",\"id\":\"1\",\"img\":\"\",\"specId\":\"11\"},{\"attName\":\"28*22*10.5\",\"attPrice\":\"600\",\"id\":\"2\",\"img\":\"\",\"specId\":\"11\"},{\"attName\":\"32*24*12\",\"attPrice\":\"700\",\"id\":\"3\",\"img\":\"\",\"specId\":\"11\"},{\"attName\":\"20*11.5*1.5\",\"attPrice\":\"300\",\"id\":\"4\",\"img\":\"\",\"specId\":\"11\"}]', 0, 0, '2026-07-13 17:29:13', '2026-07-13 17:29:13');
INSERT INTO `product_spec` VALUES (12, 2, '年份', '[{\"attName\":\"T刻-O刻2015-2011年\",\"attPrice\":\"1000\",\"id\":\"1\",\"img\":\"\",\"specId\":\"12\"},{\"attName\":\"N刻-E刻2010-2001年\",\"attPrice\":\"2000\",\"id\":\"2\",\"img\":\"\",\"specId\":\"12\"},{\"attName\":\"D刻-U刻2000-1991年\",\"attPrice\":\"3000\",\"id\":\"3\",\"img\":\"\",\"specId\":\"12\"},{\"attName\":\"T刻1990年以前\",\"attPrice\":\"4000\",\"id\":\"4\",\"img\":\"\",\"specId\":\"12\"}]', 0, 0, '2026-07-13 17:32:13', '2026-07-13 17:32:13');
INSERT INTO `product_spec` VALUES (13, 2, '包袋配件', '[{\"attName\":\"肩带、子代齐全\",\"attPrice\":\"500\",\"id\":\"1\",\"img\":\"\",\"specId\":\"13\"},{\"attName\":\"肩带丢失\",\"attPrice\":\"300\",\"id\":\"2\",\"img\":\"\",\"specId\":\"13\"},{\"attName\":\"子代丢失\",\"attPrice\":\"400\",\"id\":\"3\",\"img\":\"\",\"specId\":\"13\"},{\"attName\":\"无肩带、子带\",\"attPrice\":\"100\",\"id\":\"4\",\"img\":\"\",\"specId\":\"13\"}]', 0, 0, '2026-07-13 17:34:13', '2026-07-13 17:34:13');
INSERT INTO `product_spec` VALUES (14, 4, '内存', '[{\"attName\":\"128G\",\"attPrice\":\"300\",\"id\":\"1\",\"img\":\"\",\"specId\":\"14\"},{\"attName\":\"256G\",\"attPrice\":\"400\",\"id\":\"2\",\"img\":\"\",\"specId\":\"14\"},{\"attName\":\"1T\",\"attPrice\":\"500\",\"id\":\"3\",\"img\":\"\",\"specId\":\"14\"}]', 0, 0, '2026-07-13 17:36:08', '2026-07-13 17:36:32');
INSERT INTO `product_spec` VALUES (15, 4, '机型', '[{\"attName\":\"华为P60\",\"attPrice\":\"2000\",\"id\":\"1\",\"img\":\"\",\"specId\":\"15\"},{\"attName\":\"华为P620 Pro\",\"attPrice\":\"2500\",\"id\":\"2\",\"img\":\"\",\"specId\":\"15\"},{\"attName\":\"华为P80\",\"attPrice\":\"3000\",\"id\":\"3\",\"img\":\"\",\"specId\":\"15\"},{\"attName\":\"华为P80 pro\",\"attPrice\":\"3500\",\"id\":\"4\",\"img\":\"\",\"specId\":\"15\"},{\"attName\":\"华为三折叠\",\"attPrice\":\"5000\",\"id\":\"5\",\"img\":\"\",\"specId\":\"15\"}]', 0, 0, '2026-07-13 17:38:50', '2026-07-13 17:40:11');
INSERT INTO `product_spec` VALUES (16, 4, '新旧程度', '[{\"attName\":\"全新\",\"attPrice\":\"2000\",\"id\":\"1\",\"img\":\"\",\"specId\":\"16\"},{\"attName\":\"99新\",\"attPrice\":\"1800\",\"id\":\"2\",\"img\":\"\",\"specId\":\"16\"},{\"attName\":\"95新\",\"attPrice\":\"1500\",\"id\":\"3\",\"img\":\"\",\"specId\":\"16\"},{\"attName\":\"8成新\",\"attPrice\":\"1000\",\"id\":\"4\",\"img\":\"\",\"specId\":\"16\"}]', 0, 0, '2026-07-13 17:41:13', '2026-07-13 17:41:14');
INSERT INTO `product_spec` VALUES (17, 4, '损伤情况', '[{\"attName\":\"全新\",\"attPrice\":\"1000\",\"id\":\"1\",\"img\":\"\",\"specId\":\"17\"},{\"attName\":\"无明细问题\",\"attPrice\":\"900\",\"id\":\"2\",\"img\":\"\",\"specId\":\"17\"},{\"attName\":\"轻微磕碰\",\"attPrice\":\"800\",\"id\":\"3\",\"img\":\"\",\"specId\":\"17\"},{\"attName\":\"无法启动\",\"attPrice\":\"100\",\"id\":\"4\",\"img\":\"\",\"specId\":\"17\"}]', 0, 0, '2026-07-13 17:42:55', '2026-07-13 17:42:55');
INSERT INTO `product_spec` VALUES (18, 1, '门型', '[{\"attName\":\"双门\",\"attPrice\":\"100\",\"id\":\"1\",\"img\":\"\",\"specId\":\"18\"},{\"attName\":\"三门\",\"attPrice\":\"150\",\"id\":\"2\",\"img\":\"\",\"specId\":\"18\"},{\"attName\":\"十字对开门\",\"attPrice\":\"200\",\"id\":\"3\",\"img\":\"\",\"specId\":\"18\"},{\"attName\":\"多门法式\",\"attPrice\":\"250\",\"id\":\"4\",\"img\":\"\",\"specId\":\"18\"}]', 0, 0, '2026-07-14 13:41:18', '2026-07-14 15:22:07');
INSERT INTO `product_spec` VALUES (19, 36, '压缩机', '[{\"attName\":\"完好100%\",\"attPrice\":\"50\",\"id\":\"1\",\"img\":\"\",\"specId\":\"19\"},{\"attName\":\"基本完好90%\",\"attPrice\":\"20\",\"id\":\"2\",\"img\":\"\",\"specId\":\"19\"},{\"attName\":\"无法使用\",\"attPrice\":\"10\",\"id\":\"3\",\"img\":\"\",\"specId\":\"19\"}]', 0, 1, '2026-08-12 19:43:40', '2026-08-12 19:43:40');
INSERT INTO `product_spec` VALUES (20, 36, '使用年限', '[{\"attName\":\"一年内\",\"attPrice\":\"100\",\"id\":\"1\",\"img\":\"\",\"specId\":\"20\"},{\"attName\":\"5年内\",\"attPrice\":\"50\",\"id\":\"2\",\"img\":\"\",\"specId\":\"20\"},{\"attName\":\"10年内\",\"attPrice\":\"10\",\"id\":\"3\",\"img\":\"\",\"specId\":\"20\"}]', 0, 1, '2026-08-12 19:44:27', '2026-08-12 19:44:28');
INSERT INTO `product_spec` VALUES (21, 36, '门型', '[{\"attName\":\"双门\",\"attPrice\":\"50\",\"id\":\"1\",\"img\":\"\",\"specId\":\"21\"},{\"attName\":\"三门\",\"attPrice\":\"10\",\"id\":\"2\",\"img\":\"\",\"specId\":\"21\"}]', 1, 3, '2026-08-12 19:45:18', '2026-08-12 19:45:50');
INSERT INTO `product_spec` VALUES (23, 12, '空调类型', '[{\"attName\":\"挂机\",\"attPrice\":\"100\",\"id\":\"1\",\"img\":\"\",\"specId\":\"23\"},{\"attName\":\"柜机\",\"attPrice\":\"200\",\"id\":\"2\",\"img\":\"\",\"specId\":\"23\"}]', 0, 0, '2026-09-24 10:01:24', '2026-09-24 15:56:37');

-- ----------------------------
-- Table structure for recycle_appointment_slot
-- ----------------------------
DROP TABLE IF EXISTS `recycle_appointment_slot`;
CREATE TABLE `recycle_appointment_slot`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `start_time` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开始时间，如 10:00',
  `end_time` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '结束时间，如 12:00',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1上架 2下架',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_recycle_appointment_slot_state_sort`(`state`, `sort`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '回收上门预约时间段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recycle_appointment_slot
-- ----------------------------
INSERT INTO `recycle_appointment_slot` VALUES (1, '10:00', '11:00', 2, 1, '2026-07-15 16:12:19', '2026-07-15 16:20:00');
INSERT INTO `recycle_appointment_slot` VALUES (2, '14:00', '16:00', 3, 1, '2026-07-15 16:12:19', '2026-07-15 16:19:55');
INSERT INTO `recycle_appointment_slot` VALUES (3, '18:00', '20:00', 4, 1, '2026-07-15 16:12:19', '2026-07-15 16:19:51');
INSERT INTO `recycle_appointment_slot` VALUES (4, '09:00', '10:00', 1, 1, '2026-07-15 16:19:43', '2026-07-15 16:19:42');

-- ----------------------------
-- Table structure for recycle_category
-- ----------------------------
DROP TABLE IF EXISTS `recycle_category`;
CREATE TABLE `recycle_category`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码 clothes/appliance/digital',
  `icon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1上架 2下架',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_recycle_category_code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '回收分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recycle_category
-- ----------------------------
INSERT INTO `recycle_category` VALUES (1, '旧衣回收', 'clothes', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/5c0a66c9-e6e3-4d98-a801-e2964c8e41ec.png', 3, 1, '旧衣、鞋帽、家纺等可回收物品', '2026-07-06 10:35:22', '2026-07-21 15:22:45');
INSERT INTO `recycle_category` VALUES (2, '家电回收', 'appliance', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/4512dfc2-a312-48b8-bd62-c72dccab761d.png', 1, 1, '冰箱、洗衣机、空调等家电', '2026-07-06 10:35:22', '2026-07-21 15:20:26');
INSERT INTO `recycle_category` VALUES (3, '数码回收', 'digital', 'grid', 2, 1, '手机、平板、笔记本等数码产品', '2026-07-06 10:35:22', '2026-07-06 14:32:45');
INSERT INTO `recycle_category` VALUES (4, '奢侈品', 'luxury', 'coupon', 4, 1, '奢侈品回收', '2026-07-06 14:32:45', '2026-07-06 14:32:45');
INSERT INTO `recycle_category` VALUES (5, '书籍', 'book', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/9ae46442-d865-412c-9171-d00e42c76987.png', 5, 1, '书籍回收', '2026-07-06 14:32:45', '2026-07-21 15:23:20');

-- ----------------------------
-- Table structure for recycle_order
-- ----------------------------
DROP TABLE IF EXISTS `recycle_order`;
CREATE TABLE `recycle_order`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `user_id` bigint(0) NOT NULL,
  `order_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'clothes/appliance/digital',
  `fulfill_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'visit' COMMENT '履约方式 visit上门/mail邮寄',
  `category_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '品类描述',
  `appointment_time` datetime(0) NULL DEFAULT NULL COMMENT '预约时间',
  `address_id` bigint(0) NULL DEFAULT NULL COMMENT '地址ID',
  `contact_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `contact_mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `address_text` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '完整地址快照',
  `mail_address_text` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮寄地址快照（仓库收件地址）',
  `express_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '快递单号（邮寄订单）',
  `estimate_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '预估金额',
  `estimate_min_amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '预估金额下限（家电）',
  `estimate_max_amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '预估金额上限（家电）',
  `actual_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际金额',
  `actual_amount_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '实际金额修改说明',
  `estimate_amount_confirmed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '预估/预付金额是否已确认',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单状态',
  `status_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态描述',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_recycle_order_no`(`order_no`) USING BTREE,
  INDEX `idx_recycle_order_user`(`user_id`) USING BTREE,
  INDEX `idx_recycle_order_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 82 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '回收订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recycle_order
-- ----------------------------
INSERT INTO `recycle_order` VALUES (1, 'RE202607061647216376', 44, 'appliance', 'visit', '电视 / 55E5N Pro', '2026-07-06 10:00:00', 2, '咩2', '15300000004', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 533.60, 533.60, 533.60, NULL, NULL, 0, 'WAIT_ASSIGN', '待分配', '液晶 LED · 43-55寸 · 正常老化 · 无明显故障 · 购入 1-3年', '2026-07-06 16:47:22', NULL);
INSERT INTO `recycle_order` VALUES (2, 'RE202607061700554274', 44, 'appliance', 'visit', '空调 / 云佳 1.5匹', '2026-07-06 10:00:00', 2, '咩2', '15300000004', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 680.00, 680.00, 680.00, NULL, NULL, 0, 'WAIT_ASSIGN', '待分配', '挂机 · 1.5匹 · 良好 · 无明显故障 · 购入 1-3年', '2026-07-06 17:00:55', NULL);
INSERT INTO `recycle_order` VALUES (3, 'RE202607061756569537', 44, 'appliance', 'visit', '电视 / EA55 2024款', '2026-07-07 10:00:00', 2, '咩2', '15300000004', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 1160.00, 1160.00, 1160.00, NULL, NULL, 0, 'WAIT_ASSIGN', '待分配', 'OLED · 43寸以下 · 九成新 · 无明显故障 · 购入 1年内', '2026-07-06 17:56:57', NULL);
INSERT INTO `recycle_order` VALUES (4, 'RE202607070935583480', 44, 'digital', 'visit', 'Apple / iPhone 15 Pro', '2026-07-07 10:00:00', 2, '咩2', '15300000004', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 13180.00, NULL, NULL, NULL, NULL, 0, 'WAIT_ASSIGN', '待分配', '需要隐私清除服务', '2026-07-07 09:35:58', NULL);
INSERT INTO `recycle_order` VALUES (5, 'RE202607071006273658', 42, 'appliance', 'visit', '空调 / 云佳 1.5匹', '2026-07-07 14:00:00', 3, '152', '18292841998', '天津市 天津市 和平区 出', NULL, NULL, 1120.00, 1120.00, 1120.00, NULL, NULL, 0, 'WAIT_ASSIGN', '待分配', '挂机 · 1.5匹 · 良好 · 无明显故障 · 购入 1-3年', '2026-07-07 10:06:27', NULL);
INSERT INTO `recycle_order` VALUES (6, 'RE202607071006411354', 42, 'appliance', 'visit', '空调 / 云佳 1.5匹', '2026-07-07 14:00:00', 3, '152', '18292841998', '天津市 天津市 和平区 出', NULL, NULL, 1120.00, 1120.00, 1120.00, NULL, NULL, 0, 'WAIT_ASSIGN', '待分配', '挂机 · 1.5匹 · 良好 · 无明显故障 · 购入 1-3年', '2026-07-07 10:06:42', NULL);
INSERT INTO `recycle_order` VALUES (7, 'RE202607071035429925', 44, 'digital', 'visit', 'Huawei / Mate 60 Pro', '2026-07-07 10:00:00', 2, '咩2', '15300000004', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 4910.00, NULL, NULL, NULL, NULL, 0, 'WAIT_ASSIGN', '待分配', '需要隐私清除服务', '2026-07-07 10:35:42', NULL);
INSERT INTO `recycle_order` VALUES (8, 'RE202607071352205547', 44, 'clothes', 'visit', '旧衣回收', '2026-07-07 13:00:00', 1, '咩1', '15200000000', '北京市 北京市 东城区 天安门广场', NULL, NULL, 17.00, NULL, NULL, 17.00, NULL, 1, 'COMPLETED', '已完成', NULL, '2026-07-07 13:52:21', '2026-07-13 16:03:27');
INSERT INTO `recycle_order` VALUES (9, 'RE202607071352349277', 44, 'clothes', 'visit', '旧衣回收', '2026-07-11 10:00:00', 1, '咩1', '15200000000', '北京市 北京市 东城区 天安门广场', NULL, NULL, 18.50, NULL, NULL, NULL, NULL, 0, 'WAIT_ASSIGN', '待分配 · 已改约至 7月11日 10:00（地址有变）', NULL, '2026-07-07 13:52:35', NULL);
INSERT INTO `recycle_order` VALUES (10, 'RE202607071353059632', 44, 'clothes', 'visit', '旧衣回收', '2026-07-11 10:00:00', 1, '咩1', '15200000000', '北京市 北京市 东城区 天安门广场', NULL, NULL, 18.50, NULL, NULL, NULL, NULL, 0, 'CANCELLED', '已提交改约：时间冲突', NULL, '2026-07-07 13:53:06', '2026-07-07 14:12:56');
INSERT INTO `recycle_order` VALUES (11, 'RE202607071413309158', 44, 'clothes', 'visit', '书籍回收', '2026-07-07 15:00:00', 1, '咩1', '15200000000', '北京市 北京市 东城区 天安门广场', NULL, NULL, 5.50, NULL, NULL, NULL, NULL, 0, 'CANCELLED', '已取消', NULL, '2026-07-07 14:13:30', '2026-07-13 15:31:57');
INSERT INTO `recycle_order` VALUES (12, 'RE202607141042564062', 45, 'appliance', 'visit', '空调 / 风尊 1.5匹', '2026-07-14 14:00:00', 5, '小黑盒', '15533248231', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 1090.00, 1090.00, 1090.00, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', '挂机 · 1.5匹 · 良好 · 无明显故障 · 购入 1-3年', '2026-07-14 10:42:56', '2026-07-14 10:42:57');
INSERT INTO `recycle_order` VALUES (13, 'RE202607141522534677', 45, 'appliance', 'visit', '冰箱 / 默认型号', '2026-07-14 18:00:00', 5, '小黑盒', '15533248231', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 930.00, 930.00, 930.00, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', '三门 · 200L以下 · 良好 · 无明显损伤 · 购入 3-5年', '2026-07-14 15:22:54', '2026-07-14 15:22:56');
INSERT INTO `recycle_order` VALUES (14, 'RE202607141544195063', 45, 'digital', 'visit', '华为 / 默认型号', '2026-07-14 10:00:00', 5, '小黑盒', '15533248231', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 5000.00, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', '需要隐私清除服务', '2026-07-14 15:44:19', '2026-07-14 15:44:20');
INSERT INTO `recycle_order` VALUES (15, 'RE202607141701475146', 45, 'digital', 'visit', '华为 / 默认型号', '2026-07-14 18:00:00', 5, '小黑盒', '15533248231', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 5200.00, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', '需要隐私清除服务', '2026-07-14 17:01:48', '2026-07-14 17:01:48');
INSERT INTO `recycle_order` VALUES (16, 'RE202607151106595080', 44, 'appliance', 'mail', '冰箱 / 默认型号', '2026-07-15 14:00:00', 1, '咩1', '15200000000', '北京市 北京市 东城区 天安门广场', '上海市浦东新区张江高科技园区纳贤路365号 旧物回收仓储中心 收件人：平台仓储部 电话：400-800-6688', NULL, 520.00, 520.00, 520.00, NULL, NULL, 0, 'IN_PROGRESS', '待顺丰寄件取件', '挂机 · 1匹 · 良好 · 无明显故障 · 购入 1-3年', '2026-07-15 11:07:00', NULL);
INSERT INTO `recycle_order` VALUES (17, 'RE202607161546152555', 46, 'appliance', 'visit', '冰箱 / 默认型号', '2026-07-17 14:00:00', 6, 'SMOKE_用户', '13800000001', '上海市 上海市 浦东新区 SMOKE_默认地址', NULL, NULL, 226.00, 226.00, 226.00, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-07-16 15:46:16', '2026-07-16 15:46:17');
INSERT INTO `recycle_order` VALUES (18, 'RE202607161546174742', 46, 'clothes', 'visit', '旧衣回收', '2026-07-17 14:00:00', 6, 'SMOKE_用户', '13800000001', '上海市 上海市 浦东新区 SMOKE_默认地址', NULL, NULL, 18.00, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-07-16 15:46:17', '2026-07-16 15:46:18');
INSERT INTO `recycle_order` VALUES (19, 'RE202607161546184229', 46, 'digital', 'visit', '古驰 / 默认型号', '2026-07-17 14:00:00', 6, 'SMOKE_用户', '13800000001', '上海市 上海市 浦东新区 SMOKE_默认地址', NULL, NULL, 6120.00, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-07-16 15:46:19', '2026-07-16 15:46:20');
INSERT INTO `recycle_order` VALUES (20, 'RE202607161547202261', 46, 'appliance', 'visit', '空调 / 京裕 大1匹', '2026-07-17 14:00:00', 7, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', NULL, NULL, 520.00, 520.00, 520.00, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-07-16 15:47:21', '2026-07-16 15:47:21');
INSERT INTO `recycle_order` VALUES (21, 'RE202607161547218801', 46, 'clothes', 'visit', '旧衣回收', '2026-07-17 14:00:00', 7, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', NULL, NULL, 18.50, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-07-16 15:47:22', '2026-07-16 15:47:23');
INSERT INTO `recycle_order` VALUES (22, 'RE202607161547232964', 46, 'digital', 'visit', '华为 / 默认型号', '2026-07-17 14:00:00', 7, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', NULL, NULL, 1500.00, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-07-16 15:47:24', '2026-07-16 15:47:24');
INSERT INTO `recycle_order` VALUES (23, 'RE202607161547461750', 46, 'appliance', 'visit', '空调 / 京裕 大1匹', '2026-07-17 14:00:00', 8, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', NULL, NULL, 520.00, 520.00, 520.00, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-07-16 15:47:46', '2026-07-16 15:47:47');
INSERT INTO `recycle_order` VALUES (24, 'RE202607161547471699', 46, 'clothes', 'visit', '旧衣回收', '2026-07-17 14:00:00', 8, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', NULL, NULL, 18.50, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-07-16 15:47:48', '2026-07-16 15:47:49');
INSERT INTO `recycle_order` VALUES (25, 'RE202607161547499889', 46, 'digital', 'visit', '华为 / 默认型号', '2026-07-17 14:00:00', 8, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', NULL, NULL, 1500.00, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-07-16 15:47:50', '2026-07-16 15:47:51');
INSERT INTO `recycle_order` VALUES (26, 'RE202607211133161511', 44, 'digital', 'visit', '华为 / 默认型号', '2026-07-21 14:00:00', 1, '咩1', '15200000000', '北京市 北京市 东城区 天安门广场', NULL, NULL, 6700.00, NULL, NULL, NULL, NULL, 0, 'CANCELLED', '已取消', '需要隐私清除服务', '2026-07-21 11:33:17', '2026-09-23 19:47:06');
INSERT INTO `recycle_order` VALUES (27, 'RE202607211135136617', 44, 'digital', 'mail', '华为 / 默认型号', '2026-07-21 14:00:00', 4, '咩咩', '15200000002', '天津市 天津市 河东区 天津站', '上海市浦东新区张江高科技园区纳贤路365号 旧物回收仓储中心 收件人：平台仓储部 电话：400-800-6688', 'SF0217121758386', 6698.00, NULL, NULL, 6698.00, NULL, 1, 'COMPLETED', '已完成', '需要隐私清除服务', '2026-07-21 11:35:13', '2026-07-22 10:32:40');
INSERT INTO `recycle_order` VALUES (28, 'RE202607211138176426', 44, 'digital', 'visit', '华为 / 默认型号', '2026-07-21 14:00:00', 1, '咩1', '15200000000', '北京市 北京市 东城区 天安门广场', NULL, NULL, 6700.00, NULL, NULL, NULL, NULL, 0, 'DISPUTE_PENDING', '用户发起价格争议，等待平台处理', '需要隐私清除服务', '2026-07-21 11:38:18', '2026-07-21 16:16:48');
INSERT INTO `recycle_order` VALUES (32, 'RE202607212113291979', 44, 'appliance', 'mail', 'dddd / eee', '2026-07-22 09:00:00', 1, '咩1', '15200000000', '北京市 北京市 东城区 天安门广场', '上海市浦东新区张江高科技园区纳贤路365号 旧物回收仓储中心 收件人：平台仓储部 电话：400-800-6688', NULL, 1040.00, 1040.00, 1040.00, NULL, NULL, 0, 'CANCELLED', '已取消', '双门 · 200L以下 · 良好 · 无明显损伤 · 购入 1-3年', '2026-07-21 21:13:29', '2026-07-21 21:15:23');
INSERT INTO `recycle_order` VALUES (33, 'RE202607221049536304', 44, 'luxury', 'mail', '百达翡丽 / 默认型号', '2026-07-23 14:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', NULL, 2450.00, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '待顺丰寄件取件', '需要隐私清除服务', '2026-07-22 10:49:54', NULL);
INSERT INTO `recycle_order` VALUES (34, 'RE202607221110392447', 44, 'luxury', 'mail', '百达翡丽 / 默认型号', '2026-07-22 14:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', NULL, 2450.00, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '待顺丰寄件取件', '需要隐私清除服务', '2026-07-22 11:10:39', NULL);
INSERT INTO `recycle_order` VALUES (35, 'RE202607221111337033', 44, 'book', 'visit', '书籍回收', '2026-07-22 14:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', NULL, NULL, 6.00, NULL, NULL, 7.00, NULL, 1, 'COMPLETED', '已完成', NULL, '2026-07-22 11:11:34', '2026-07-30 20:57:45');
INSERT INTO `recycle_order` VALUES (36, 'RE202607221326211315', 46, 'appliance', 'visit', 'dddd / eee', '2026-07-23 14:00:00', 9, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', NULL, NULL, 1040.00, 1040.00, 1040.00, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-07-22 13:26:21', '2026-07-22 13:26:22');
INSERT INTO `recycle_order` VALUES (37, 'RE202607221326227820', 46, 'clothes', 'visit', '旧衣回收', '2026-07-23 14:00:00', 9, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', NULL, NULL, 18.50, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-07-22 13:26:23', '2026-07-22 13:26:23');
INSERT INTO `recycle_order` VALUES (38, 'RE202607221326241760', 46, 'digital', 'mail', '华为 / 默认型号', '2026-07-23 14:00:00', 9, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', NULL, 1500.00, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '待顺丰寄件取件', NULL, '2026-07-22 13:26:24', NULL);
INSERT INTO `recycle_order` VALUES (39, 'RE202607221647023038', 46, 'appliance', 'visit', 'dddd / eee', '2026-07-23 14:00:00', 10, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', NULL, NULL, 1040.00, 1040.00, 1040.00, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-07-22 16:47:03', '2026-07-22 16:47:04');
INSERT INTO `recycle_order` VALUES (40, 'RE202607221647041117', 46, 'clothes', 'visit', '旧衣回收', '2026-07-23 14:00:00', 10, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', NULL, NULL, 18.50, NULL, NULL, NULL, NULL, 0, 'CANCELLED', '已取消', NULL, '2026-07-22 16:47:04', '2026-09-23 20:01:03');
INSERT INTO `recycle_order` VALUES (41, 'RE202607221647067492', 46, 'digital', 'mail', '华为 / 默认型号', '2026-07-23 14:00:00', 10, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', NULL, 1500.00, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '待顺丰寄件取件', NULL, '2026-07-22 16:47:07', NULL);
INSERT INTO `recycle_order` VALUES (42, 'RE202607221647116245', 46, 'digital', 'mail', '华为 / 默认型号', '2026-07-23 14:00:00', 11, 'SMOKE_联系人', '13900001234', '上海市 上海市 浦东新区 SMOKE_联调路1号', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', NULL, 1500.00, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '待顺丰寄件取件', NULL, '2026-07-22 16:47:12', NULL);
INSERT INTO `recycle_order` VALUES (43, 'RE202607231034437957', 44, 'appliance', 'mail', 'dddd / eee', '2026-07-23 14:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', NULL, 1040.00, 1040.00, 1040.00, NULL, NULL, 0, 'IN_PROGRESS', '待顺丰寄件取件', '双门 · 200L以下 · 良好 · 无明显损伤 · 购入 1-3年', '2026-07-23 10:34:44', NULL);
INSERT INTO `recycle_order` VALUES (44, 'RE202607281732355772', 44, 'appliance', 'mail', '空调 / 京裕 大1匹', '2026-07-28 18:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', NULL, 520.00, 520.00, 520.00, NULL, NULL, 0, 'IN_PROGRESS', '待顺丰寄件取件', '挂机 · 1匹 · 良好 · 无明显故障 · 购入 1-3年', '2026-07-28 17:32:36', NULL);
INSERT INTO `recycle_order` VALUES (45, 'RE202607312211567824', 45, 'appliance', 'mail', '空调 / 京裕 大1匹', '2026-08-01 09:00:00', 5, '小黑盒', '15533248231', '北京市 北京市 东城区 灵境胡同', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', NULL, 520.00, 520.00, 520.00, NULL, NULL, 0, 'IN_PROGRESS', '待顺丰寄件取件', '挂机 · 1匹 · 良好 · 无明显故障 · 购入 1-3年', '2026-07-31 22:11:56', NULL);
INSERT INTO `recycle_order` VALUES (46, 'RE202608071638153439', 45, 'clothes', 'visit', '旧衣回收', '2026-08-07 18:00:00', 5, '小黑盒', '15533248231', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 18.50, NULL, NULL, NULL, NULL, 0, 'CANCELLED', '已取消', NULL, '2026-08-07 16:38:16', '2026-09-23 20:00:04');
INSERT INTO `recycle_order` VALUES (47, 'RE202608121959324999', 47, 'appliance', 'visit', '冰柜 / A01', '2026-08-13 09:00:00', 12, '黄一', '18329680001', '陕西省 西安市 雁塔区 朗臣大厦1808', NULL, NULL, 260.00, 260.00, 260.00, NULL, NULL, 0, 'CANCELLED', '已取消', '三门 · 200L以下 · 良好 · 无明显损伤 · 购入 1-3年', '2026-08-12 19:59:33', '2026-08-12 19:59:33');
INSERT INTO `recycle_order` VALUES (48, 'RE202609171005535593', 45, 'clothes', 'visit', '旧衣回收', '2026-09-17 14:00:00', 5, '小黑盒', '15533248231', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 36.00, NULL, NULL, 36.00, NULL, 1, 'PAYOUT_PROCESSING', '打款中', NULL, '2026-09-17 10:05:54', '2026-09-17 10:11:08');
INSERT INTO `recycle_order` VALUES (49, 'RE202609171041002089', 44, 'clothes', 'visit', '旧衣回收', '2026-09-17 14:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', NULL, NULL, 18.50, NULL, NULL, 16.00, '实际重量：4.5kg', 0, 'CANCELLED', '已取消', NULL, '2026-09-17 10:41:01', '2026-09-23 11:27:41');
INSERT INTO `recycle_order` VALUES (50, 'RE202609171425484575', 44, 'clothes', 'visit', '旧衣回收', '2026-09-17 18:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', NULL, NULL, 36.00, NULL, NULL, 60.00, NULL, 0, 'DISPUTE_PENDING', '用户发起价格争议，等待平台处理', NULL, '2026-09-17 14:25:49', '2026-09-24 16:56:40');
INSERT INTO `recycle_order` VALUES (51, 'RE202609221520071329', 45, 'appliance', 'visit', '空调 / 京裕 大1匹', '2026-09-22 18:00:00', 5, '小黑盒', '15533248231', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 520.00, 520.00, 520.00, NULL, NULL, 0, 'WAIT_SETTLEMENT_CONFIRM', '已完成上门回收，请确认最终成交价', '挂机 · 1匹 · 良好 · 无明显故障 · 购入 1-3年', '2026-09-22 15:20:08', '2026-09-22 15:21:24');
INSERT INTO `recycle_order` VALUES (52, 'RE202609221522293288', 44, 'clothes', 'visit', '旧衣回收', '2026-09-22 18:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', NULL, NULL, 58.00, NULL, NULL, NULL, NULL, 0, 'DISPUTE_PENDING', '用户发起价格争议，等待平台处理', NULL, '2026-09-22 15:22:29', '2026-09-22 17:12:24');
INSERT INTO `recycle_order` VALUES (53, 'RE202609221659171463', 44, 'clothes', 'visit', '旧衣回收', '2026-09-22 18:00:00', 2, '咩2', '15300000004', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 18.50, NULL, NULL, 20.00, NULL, 1, 'COMPLETED', '已完成', NULL, '2026-09-22 16:59:17', '2026-09-24 17:18:09');
INSERT INTO `recycle_order` VALUES (54, 'RE202609221709206494', 44, 'clothes', 'visit', '旧衣回收', '2026-09-22 18:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', NULL, NULL, 18.50, NULL, NULL, 18.50, NULL, 1, 'COMPLETED', '已完成', NULL, '2026-09-22 17:09:20', '2026-09-22 17:11:59');
INSERT INTO `recycle_order` VALUES (55, 'RE202609231432314268', 44, 'clothes', 'visit', '旧衣回收', '2026-09-24 10:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', NULL, NULL, 18.50, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '进行中 · 已改约至 9月24日 10:00（时间冲突）', NULL, '2026-09-23 14:32:32', '2026-09-23 14:32:32');
INSERT INTO `recycle_order` VALUES (56, 'RE202609231542041078', 47, 'appliance', 'visit', '空调 / 云佳 1.5匹', '2026-09-23 18:00:00', 12, '黄一', '18329680001', '陕西省 西安市 雁塔区 朗臣大厦1808', NULL, NULL, 720.00, 720.00, 720.00, 500.00, '成色不新，只值500', 1, 'COMPLETED', '已完成', NULL, '2026-09-23 15:42:04', '2026-09-23 16:28:34');
INSERT INTO `recycle_order` VALUES (57, 'RE202609231632417446', 47, 'clothes', 'visit', '旧衣回收', '2026-09-23 18:00:00', 12, '黄一', '18329680001', '陕西省 西安市 雁塔区 朗臣大厦1808', NULL, NULL, 18.50, NULL, NULL, 20.00, '方法大多数烦都烦死', 0, 'IN_PROGRESS', '进行中 · 已改约至 9月23日 18:00（时间冲突）', NULL, '2026-09-23 16:32:41', '2026-09-23 16:58:56');
INSERT INTO `recycle_order` VALUES (58, 'RE202609231914327364', 44, 'clothes', 'visit', '旧衣回收', '2026-09-24 09:00:00', 4, '咩咩', '15533248239', '天津市 天津市 河东区 天津站', NULL, NULL, 18.50, NULL, NULL, 16.00, '实际重量是：4kg', 1, 'COMPLETED', '已完成', NULL, '2026-09-23 19:14:33', '2026-09-24 16:11:21');
INSERT INTO `recycle_order` VALUES (59, 'RE202609241328245400', 44, 'appliance', 'visit', '空调 / 京裕 大1匹', '2026-09-24 14:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', NULL, NULL, 340.00, 200.00, 480.00, 450.00, '有点划痕', 1, 'COMPLETED', '已完成', NULL, '2026-09-24 13:28:25', '2026-09-24 16:41:15');
INSERT INTO `recycle_order` VALUES (60, 'RE202609241329583577', 44, 'appliance', 'visit', '空调 / 京裕 大1匹', '2026-09-24 14:00:00', 4, '咩咩', '15533248239', '天津市 天津市 河东区 天津站', NULL, NULL, 340.00, 200.00, 480.00, NULL, NULL, 0, 'CANCELLED', '已取消', NULL, '2026-09-24 13:29:58', '2026-09-24 19:21:33');
INSERT INTO `recycle_order` VALUES (61, 'RE202609241602509738', 47, 'appliance', 'visit', '空调 / 酷省电 大1匹', '2026-09-24 18:00:00', 12, '黄一', '18329680001', '陕西省 西安市 雁塔区 朗臣大厦1808', NULL, NULL, 350.00, 200.00, 500.00, 100.00, '晚上呜呜呜呜', 1, 'COMPLETED', '已完成', '柜机', '2026-09-24 16:02:50', '2026-09-24 16:05:33');
INSERT INTO `recycle_order` VALUES (62, 'RE202609241725272337', 44, 'appliance', 'visit', '空调 / 京裕 大1匹', '2026-09-24 18:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', NULL, NULL, 340.00, 200.00, 480.00, 300.00, NULL, 1, 'COMPLETED', '已完成', NULL, '2026-09-24 17:25:28', '2026-09-24 17:34:09');
INSERT INTO `recycle_order` VALUES (63, 'RE202609241751354007', 44, 'appliance', 'visit', '空调 / 京裕 大1匹', '2026-09-24 18:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', NULL, NULL, 340.00, 200.00, 480.00, 300.00, '11111111111111111111', 1, 'COMPLETED', '已完成', NULL, '2026-09-24 17:51:36', '2026-09-24 17:52:31');
INSERT INTO `recycle_order` VALUES (64, 'RE202609241755545799', 47, 'appliance', 'visit', '空调 / 酷省电 大1匹', '2026-09-24 18:00:00', 12, '黄一', '18329680001', '陕西省 西安市 雁塔区 朗臣大厦1808', NULL, NULL, 450.00, 300.00, 600.00, 500.00, '呜呜呜呜', 1, 'COMPLETED', '已完成', '挂机', '2026-09-24 17:55:54', '2026-09-24 17:58:24');
INSERT INTO `recycle_order` VALUES (65, 'RE202609241800079276', 47, 'appliance', 'mail', '空调 / 酷省电 大1匹', '2026-09-24 18:00:00', 12, '黄一', '18329680001', '陕西省 西安市 雁塔区 朗臣大厦1808', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', NULL, 550.00, 400.00, 700.00, NULL, NULL, 0, 'IN_PROGRESS', '待顺丰寄件取件', '柜机', '2026-09-24 18:00:07', NULL);
INSERT INTO `recycle_order` VALUES (66, 'RE202609241807455541', 44, 'digital', 'mail', '华为 / 默认型号', '2026-09-25 09:00:00', 1, '咩1', '15533248239', '北京市 北京市 东城区 天安门广场', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', 'SF5158965048147', 6450.00, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '待收货', '需要隐私清除服务', '2026-09-24 18:07:46', '2026-09-24 18:31:50');
INSERT INTO `recycle_order` VALUES (67, 'RE202609241853587801', 44, 'appliance', 'mail', '空调 / 京裕 大1匹', '2026-09-25 09:00:00', 4, '咩咩', '18740331034', '天津市 天津市 河东区 天津站', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', 'SF5158965048147', 340.00, 200.00, 480.00, 340.00, NULL, 1, 'COMPLETED', '已完成', NULL, '2026-09-24 18:53:58', '2026-09-24 19:04:06');
INSERT INTO `recycle_order` VALUES (68, 'RE202609241904542189', 44, 'appliance', 'mail', '空调 / 京裕 大1匹', '2026-09-25 09:00:00', 4, '咩咩', '18740331034', '天津市 天津市 河东区 天津站', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', 'SF5158965048147', 340.00, 200.00, 480.00, 340.00, NULL, 0, 'CANCELLED', '已取消', NULL, '2026-09-24 19:04:55', '2026-09-24 19:25:02');
INSERT INTO `recycle_order` VALUES (69, 'RE202609281002427779', 47, 'appliance', 'mail', '空调 / 酷省电 大1匹', '2026-09-28 10:00:00', 12, '黄一', '18329680001', '陕西省 西安市 雁塔区 朗臣大厦1808', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', '00001', 550.00, 400.00, 700.00, 550.00, NULL, 1, 'COMPLETED', '已完成', '柜机', '2026-09-28 10:02:43', '2026-09-28 10:35:51');
INSERT INTO `recycle_order` VALUES (70, 'RE202609281334222613', 47, 'appliance', 'mail', '空调 / 酷省电 大1匹', '2026-09-28 14:00:00', 12, '黄一', '18329680001', '陕西省 西安市 雁塔区 朗臣大厦1808', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', '002', 450.00, 300.00, 600.00, 450.00, NULL, 1, 'COMPLETED', '已完成', '挂机', '2026-09-28 13:34:23', '2026-09-28 13:42:02');
INSERT INTO `recycle_order` VALUES (71, 'RE202609281345066843', 47, 'appliance', 'visit', '空调 / 酷省电 大1匹', '2026-09-29 09:00:00', 12, '黄一', '18329680001', '陕西省 西安市 雁塔区 朗臣大厦1808', NULL, NULL, 450.00, 300.00, 600.00, 500.00, NULL, 0, 'CANCELLED', '已取消', '挂机', '2026-09-28 13:45:06', '2026-09-28 13:48:49');
INSERT INTO `recycle_order` VALUES (72, 'RE202609281351046004', 47, 'appliance', 'mail', '空调 / 风尊 1.5匹', '2026-09-28 14:00:00', 12, '黄一', '18329680001', '陕西省 西安市 雁塔区 朗臣大厦1808', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', NULL, 650.00, 650.00, 650.00, NULL, NULL, 0, 'CANCELLED', '已取消', NULL, '2026-09-28 13:51:05', '2026-09-28 13:52:16');
INSERT INTO `recycle_order` VALUES (73, 'RE202609281353045297', 47, 'appliance', 'mail', '空调 / 酷省电 大1匹', '2026-09-28 14:00:00', 12, '黄一', '18329680001', '陕西省 西安市 雁塔区 朗臣大厦1808', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', NULL, 550.00, 400.00, 700.00, NULL, NULL, 0, 'IN_PROGRESS', '待顺丰寄件取件', '柜机', '2026-09-28 13:53:04', NULL);
INSERT INTO `recycle_order` VALUES (74, 'RE202609281359509266', 48, 'appliance', 'mail', '空调 / 酷省电 大1匹', '2026-09-28 14:00:00', 13, '下级', '18329680002', '北京市 北京市 东城区 呼家楼接单', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', '0003', 550.00, 400.00, 700.00, 550.00, NULL, 1, 'PAYOUT_PROCESSING', '打款中', '柜机', '2026-09-28 13:59:51', '2026-09-28 14:00:44');
INSERT INTO `recycle_order` VALUES (75, 'RE202609291742239048', 44, 'appliance', 'visit', '空调 / 京裕 大1匹', '2026-09-29 18:00:00', 4, '咩咩', '18740331034', '天津市 天津市 河东区 天津站', NULL, NULL, 340.00, 200.00, 480.00, 255.00, NULL, 1, 'PAYOUT_PROCESSING', '打款中', NULL, '2026-09-29 17:42:24', '2026-09-29 17:51:52');
INSERT INTO `recycle_order` VALUES (76, 'RE202609291759025784', 50, 'appliance', 'visit', '空调 / 京裕 大1匹', '2026-09-29 18:00:00', 14, '啊啊', '15533248232', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 340.00, 200.00, 480.00, 300.00, NULL, 1, 'COMPLETED', '已完成', NULL, '2026-09-29 17:59:02', '2026-09-29 19:06:36');
INSERT INTO `recycle_order` VALUES (77, 'RE202609292006368881', 44, 'appliance', 'visit', '空调 / 静悦 1.5匹', '2026-09-30 09:00:00', 4, '咩咩', '18740331034', '天津市 天津市 河东区 天津站', NULL, NULL, 620.00, 620.00, 620.00, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-09-29 20:06:37', '2026-09-29 20:06:37');
INSERT INTO `recycle_order` VALUES (78, 'RE202609292037243828', 44, 'appliance', 'mail', '空调 / 京裕 大1匹', '2026-09-30 09:00:00', 4, '咩咩', '18740331034', '天津市 天津市 河东区 天津站', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', 'SF5158965048147', 340.00, 200.00, 480.00, 333.00, NULL, 0, 'WAIT_SETTLEMENT_CONFIRM', '待确认', NULL, '2026-09-29 20:37:24', '2026-09-29 20:53:47');
INSERT INTO `recycle_order` VALUES (79, 'RE202609292054184240', 44, 'appliance', 'mail', '空调 / 京裕 大1匹', '2026-09-30 09:00:00', 4, '咩咩', '18740331034', '天津市 天津市 河东区 天津站', '陕西省西安市雁塔区高新四路13号 收件人：平台仓储部 电话：400-800-6688', '12344', 340.00, 200.00, 480.00, 355.00, NULL, 0, 'WAIT_SETTLEMENT_CONFIRM', '待确认', NULL, '2026-09-29 20:54:19', '2026-09-29 20:58:07');
INSERT INTO `recycle_order` VALUES (80, 'RE202610191306112587', 45, 'clothes', 'visit', '旧衣回收', '2026-10-19 14:00:00', 5, '小黑盒', '15533248231', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 18.50, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-10-19 13:06:11', '2026-10-19 13:06:12');
INSERT INTO `recycle_order` VALUES (81, 'RE202610191307472289', 45, 'clothes', 'visit', '旧衣回收', '2026-10-19 14:00:00', 5, '小黑盒', '15533248231', '北京市 北京市 东城区 灵境胡同', NULL, NULL, 18.50, NULL, NULL, NULL, NULL, 0, 'IN_PROGRESS', '员工正在上门取件', NULL, '2026-10-19 13:07:48', '2026-10-19 13:07:48');

-- ----------------------------
-- Table structure for recycle_order_item
-- ----------------------------
DROP TABLE IF EXISTS `recycle_order_item`;
CREATE TABLE `recycle_order_item`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NOT NULL,
  `item_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '明细类型',
  `item_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `item_value` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '明细值/规格',
  `quantity` decimal(10, 2) NULL DEFAULT NULL COMMENT '数量/重量',
  `unit` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '单位',
  `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_recycle_order_item_order`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 82 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '回收订单明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recycle_order_item
-- ----------------------------
INSERT INTO `recycle_order_item` VALUES (1, 1, 'model', '55E5N Pro', '48', 1.00, '台', 533.60, '2026-07-06 16:47:22', NULL);
INSERT INTO `recycle_order_item` VALUES (2, 2, 'model', '云佳 1.5匹', '28', 1.00, '台', 680.00, '2026-07-06 17:00:56', NULL);
INSERT INTO `recycle_order_item` VALUES (3, 3, 'model', 'EA55 2024款', '46', 1.00, '台', 1160.00, '2026-07-06 17:56:57', NULL);
INSERT INTO `recycle_order_item` VALUES (4, 4, 'model', 'iPhone 15 Pro', '1', 1.00, '台', 13180.00, '2026-07-07 09:35:59', NULL);
INSERT INTO `recycle_order_item` VALUES (5, 5, 'model', '云佳 1.5匹', '28', 1.00, '台', 1120.00, '2026-07-07 10:06:27', NULL);
INSERT INTO `recycle_order_item` VALUES (6, 6, 'model', '云佳 1.5匹', '28', 1.00, '台', 1120.00, '2026-07-07 10:06:42', NULL);
INSERT INTO `recycle_order_item` VALUES (7, 7, 'model', 'Mate 60 Pro', '3', 1.00, '台', 4910.00, '2026-07-07 10:35:42', NULL);
INSERT INTO `recycle_order_item` VALUES (8, 8, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-07-07 13:52:21', NULL);
INSERT INTO `recycle_order_item` VALUES (9, 9, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-07-07 13:52:35', NULL);
INSERT INTO `recycle_order_item` VALUES (10, 10, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-07-07 13:53:06', NULL);
INSERT INTO `recycle_order_item` VALUES (11, 11, 'weight', '预估重量', '5-15kg', NULL, 'kg', 5.50, '2026-07-07 14:13:30', NULL);
INSERT INTO `recycle_order_item` VALUES (12, 12, 'model', '风尊 1.5匹', '31', 1.00, '台', 1090.00, '2026-07-14 10:42:56', NULL);
INSERT INTO `recycle_order_item` VALUES (13, 13, 'model', '默认型号', '1', 1.00, '台', 930.00, '2026-07-14 15:22:54', NULL);
INSERT INTO `recycle_order_item` VALUES (14, 14, 'model', '默认型号', '4', 1.00, '台', 5000.00, '2026-07-14 15:44:20', NULL);
INSERT INTO `recycle_order_item` VALUES (15, 15, 'model', '默认型号', '4', 1.00, '台', 5200.00, '2026-07-14 17:01:48', NULL);
INSERT INTO `recycle_order_item` VALUES (16, 16, 'model', '默认型号', '1', 1.00, '台', 520.00, '2026-07-15 11:07:00', NULL);
INSERT INTO `recycle_order_item` VALUES (17, 17, 'model', '默认型号', '1', 1.00, '台', 226.00, '2026-07-16 15:46:16', NULL);
INSERT INTO `recycle_order_item` VALUES (18, 18, 'weight', '预估重量', '约18kg', 18.00, 'kg', 18.00, '2026-07-16 15:46:17', NULL);
INSERT INTO `recycle_order_item` VALUES (19, 19, 'model', '默认型号', '2', 1.00, '台', 6120.00, '2026-07-16 15:46:19', NULL);
INSERT INTO `recycle_order_item` VALUES (20, 20, 'model', '京裕 大1匹', '14', 1.00, '台', 520.00, '2026-07-16 15:47:21', NULL);
INSERT INTO `recycle_order_item` VALUES (21, 21, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-07-16 15:47:22', NULL);
INSERT INTO `recycle_order_item` VALUES (22, 22, 'model', '默认型号', '4', 1.00, '台', 1500.00, '2026-07-16 15:47:24', NULL);
INSERT INTO `recycle_order_item` VALUES (23, 23, 'model', '京裕 大1匹', '14', 1.00, '台', 520.00, '2026-07-16 15:47:47', NULL);
INSERT INTO `recycle_order_item` VALUES (24, 24, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-07-16 15:47:48', NULL);
INSERT INTO `recycle_order_item` VALUES (25, 25, 'model', '默认型号', '4', 1.00, '台', 1500.00, '2026-07-16 15:47:50', NULL);
INSERT INTO `recycle_order_item` VALUES (26, 26, 'model', '默认型号', '4', 1.00, '台', 6700.00, '2026-07-21 11:33:17', NULL);
INSERT INTO `recycle_order_item` VALUES (27, 27, 'model', '默认型号', '4', 1.00, '台', 6700.00, '2026-07-21 11:35:13', NULL);
INSERT INTO `recycle_order_item` VALUES (28, 28, 'model', '默认型号', '4', 1.00, '台', 6700.00, '2026-07-21 11:38:18', NULL);
INSERT INTO `recycle_order_item` VALUES (32, 32, 'model', 'eee', '35', 1.00, '台', 1040.00, '2026-07-21 21:13:30', NULL);
INSERT INTO `recycle_order_item` VALUES (33, 33, 'model', '默认型号', '3', 1.00, '台', 2450.00, '2026-07-22 10:49:54', NULL);
INSERT INTO `recycle_order_item` VALUES (34, 34, 'model', '默认型号', '3', 1.00, '台', 2450.00, '2026-07-22 11:10:39', NULL);
INSERT INTO `recycle_order_item` VALUES (35, 35, 'weight', '预估重量', '5-15kg', NULL, 'kg', 5.50, '2026-07-22 11:11:34', NULL);
INSERT INTO `recycle_order_item` VALUES (36, 36, 'model', 'eee', '35', 1.00, '台', 1040.00, '2026-07-22 13:26:21', NULL);
INSERT INTO `recycle_order_item` VALUES (37, 37, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-07-22 13:26:23', NULL);
INSERT INTO `recycle_order_item` VALUES (38, 38, 'model', '默认型号', '4', 1.00, '台', 1500.00, '2026-07-22 13:26:24', NULL);
INSERT INTO `recycle_order_item` VALUES (39, 39, 'model', 'eee', '35', 1.00, '台', 1040.00, '2026-07-22 16:47:03', NULL);
INSERT INTO `recycle_order_item` VALUES (40, 40, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-07-22 16:47:04', NULL);
INSERT INTO `recycle_order_item` VALUES (41, 41, 'model', '默认型号', '4', 1.00, '台', 1500.00, '2026-07-22 16:47:07', NULL);
INSERT INTO `recycle_order_item` VALUES (42, 42, 'model', '默认型号', '4', 1.00, '台', 1500.00, '2026-07-22 16:47:12', NULL);
INSERT INTO `recycle_order_item` VALUES (43, 43, 'model', 'eee', '35', 1.00, '台', 1040.00, '2026-07-23 10:34:44', NULL);
INSERT INTO `recycle_order_item` VALUES (44, 44, 'model', '京裕 大1匹', '14', 1.00, '台', 520.00, '2026-07-28 17:32:36', NULL);
INSERT INTO `recycle_order_item` VALUES (45, 45, 'model', '京裕 大1匹', '14', 1.00, '台', 520.00, '2026-07-31 22:11:56', NULL);
INSERT INTO `recycle_order_item` VALUES (46, 46, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-08-07 16:38:16', NULL);
INSERT INTO `recycle_order_item` VALUES (47, 47, 'model', 'A01', '36', 1.00, '台', 260.00, '2026-08-12 19:59:33', NULL);
INSERT INTO `recycle_order_item` VALUES (48, 48, 'weight', '预估重量', '20-50kg', NULL, 'kg', 36.00, '2026-09-17 10:05:54', NULL);
INSERT INTO `recycle_order_item` VALUES (49, 49, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-09-17 10:41:01', NULL);
INSERT INTO `recycle_order_item` VALUES (50, 50, 'weight', '预估重量', '20-50kg', NULL, 'kg', 36.00, '2026-09-17 14:25:49', NULL);
INSERT INTO `recycle_order_item` VALUES (51, 51, 'model', '京裕 大1匹', '14', 1.00, '台', 520.00, '2026-09-22 15:20:08', NULL);
INSERT INTO `recycle_order_item` VALUES (52, 52, 'weight', '预估重量', '>50kg', 50.00, 'kg', 58.00, '2026-09-22 15:22:29', NULL);
INSERT INTO `recycle_order_item` VALUES (53, 53, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-09-22 16:59:18', NULL);
INSERT INTO `recycle_order_item` VALUES (54, 54, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-09-22 17:09:20', NULL);
INSERT INTO `recycle_order_item` VALUES (55, 55, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-09-23 14:32:32', NULL);
INSERT INTO `recycle_order_item` VALUES (56, 56, 'model', '云佳 1.5匹', '8', 1.00, '台', 720.00, '2026-09-23 15:42:04', NULL);
INSERT INTO `recycle_order_item` VALUES (57, 57, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-09-23 16:32:42', NULL);
INSERT INTO `recycle_order_item` VALUES (58, 58, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-09-23 19:14:33', NULL);
INSERT INTO `recycle_order_item` VALUES (59, 59, 'model', '京裕 大1匹', '14', 1.00, '台', 340.00, '2026-09-24 13:28:25', NULL);
INSERT INTO `recycle_order_item` VALUES (60, 60, 'model', '京裕 大1匹', '14', 1.00, '台', 340.00, '2026-09-24 13:29:59', NULL);
INSERT INTO `recycle_order_item` VALUES (61, 61, 'model', '酷省电 大1匹', '12', 1.00, '台', 350.00, '2026-09-24 16:02:50', NULL);
INSERT INTO `recycle_order_item` VALUES (62, 62, 'model', '京裕 大1匹', '14', 1.00, '台', 340.00, '2026-09-24 17:25:28', NULL);
INSERT INTO `recycle_order_item` VALUES (63, 63, 'model', '京裕 大1匹', '14', 1.00, '台', 340.00, '2026-09-24 17:51:36', NULL);
INSERT INTO `recycle_order_item` VALUES (64, 64, 'model', '酷省电 大1匹', '12', 1.00, '台', 450.00, '2026-09-24 17:55:54', NULL);
INSERT INTO `recycle_order_item` VALUES (65, 65, 'model', '酷省电 大1匹', '12', 1.00, '台', 550.00, '2026-09-24 18:00:08', NULL);
INSERT INTO `recycle_order_item` VALUES (66, 66, 'model', '默认型号', '4', 1.00, '台', 6450.00, '2026-09-24 18:07:46', NULL);
INSERT INTO `recycle_order_item` VALUES (67, 67, 'model', '京裕 大1匹', '14', 1.00, '台', 340.00, '2026-09-24 18:53:58', NULL);
INSERT INTO `recycle_order_item` VALUES (68, 68, 'model', '京裕 大1匹', '14', 1.00, '台', 340.00, '2026-09-24 19:04:55', NULL);
INSERT INTO `recycle_order_item` VALUES (69, 69, 'model', '酷省电 大1匹', '12', 1.00, '台', 550.00, '2026-09-28 10:02:43', NULL);
INSERT INTO `recycle_order_item` VALUES (70, 70, 'model', '酷省电 大1匹', '12', 1.00, '台', 450.00, '2026-09-28 13:34:23', NULL);
INSERT INTO `recycle_order_item` VALUES (71, 71, 'model', '酷省电 大1匹', '12', 1.00, '台', 450.00, '2026-09-28 13:45:06', NULL);
INSERT INTO `recycle_order_item` VALUES (72, 72, 'model', '风尊 1.5匹', '11', 1.00, '台', 650.00, '2026-09-28 13:51:05', NULL);
INSERT INTO `recycle_order_item` VALUES (73, 73, 'model', '酷省电 大1匹', '12', 1.00, '台', 550.00, '2026-09-28 13:53:04', NULL);
INSERT INTO `recycle_order_item` VALUES (74, 74, 'model', '酷省电 大1匹', '12', 1.00, '台', 550.00, '2026-09-28 13:59:51', NULL);
INSERT INTO `recycle_order_item` VALUES (75, 75, 'model', '京裕 大1匹', '14', 1.00, '台', 340.00, '2026-09-29 17:42:24', NULL);
INSERT INTO `recycle_order_item` VALUES (76, 76, 'model', '京裕 大1匹', '14', 1.00, '台', 340.00, '2026-09-29 17:59:02', NULL);
INSERT INTO `recycle_order_item` VALUES (77, 77, 'model', '静悦 1.5匹', '13', 1.00, '台', 620.00, '2026-09-29 20:06:37', NULL);
INSERT INTO `recycle_order_item` VALUES (78, 78, 'model', '京裕 大1匹', '14', 1.00, '台', 340.00, '2026-09-29 20:37:24', NULL);
INSERT INTO `recycle_order_item` VALUES (79, 79, 'model', '京裕 大1匹', '14', 1.00, '台', 340.00, '2026-09-29 20:54:19', NULL);
INSERT INTO `recycle_order_item` VALUES (80, 80, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-10-19 13:06:12', NULL);
INSERT INTO `recycle_order_item` VALUES (81, 81, 'weight', '预估重量', '5-20kg', NULL, 'kg', 18.50, '2026-10-19 13:07:48', NULL);

-- ----------------------------
-- Table structure for recycle_order_log
-- ----------------------------
DROP TABLE IF EXISTS `recycle_order_log`;
CREATE TABLE `recycle_order_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NOT NULL,
  `action_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '动作类型',
  `from_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `to_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `operator_id` bigint(0) NULL DEFAULT NULL,
  `operator_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'USER' COMMENT 'USER/STAFF/SYSTEM',
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_recycle_order_log_order`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 323 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '回收订单日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recycle_order_log
-- ----------------------------

-- ----------------------------
-- Table structure for recycle_rule
-- ----------------------------
DROP TABLE IF EXISTS `recycle_rule`;
CREATE TABLE `recycle_rule`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '规则内容',
  `rule_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'general' COMMENT '规则类型',
  `sort` int(0) NOT NULL DEFAULT 0,
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1启用 2禁用',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '回收规则' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recycle_rule
-- ----------------------------
INSERT INTO `recycle_rule` VALUES (1, '回收范围', '支持旧衣、家电、数码三大类物品上门回收或预约寄件。', 'general', 1, 1, '2026-07-06 10:35:22', '2026-07-06 10:35:22');
INSERT INTO `recycle_rule` VALUES (2, '履约说明', '提交订单后平台将在预约时段内安排回收员上门取件。', 'fulfillment', 2, 1, '2026-07-06 10:35:22', '2026-07-06 10:35:22');
INSERT INTO `recycle_rule` VALUES (3, '质检与价格', '实物到达后平台将进行质检，实际成交价可能与预估价存在差异，需用户确认。', 'pricing', 3, 1, '2026-07-06 10:35:22', '2026-07-06 10:35:22');
INSERT INTO `recycle_rule` VALUES (9, '环保拆解', '正规拆解基地，防止重金属污染，资源循环利用。', 'appliance_science', 1, 1, '2026-07-06 14:56:18', '2026-07-06 14:56:18');
INSERT INTO `recycle_rule` VALUES (10, '隐私清除', '针对智能家电数据彻底清除，确保个人隐私不泄露。', 'appliance_science', 2, 1, '2026-07-06 14:56:18', '2026-07-06 14:56:18');
INSERT INTO `recycle_rule` VALUES (11, '坏了的电器也回收吗？', '支持大部分损坏家电回收，具体价格会根据品牌、机型和损坏程度评估。', 'appliance_faq', 1, 1, '2026-07-06 14:56:18', '2026-07-06 14:56:18');
INSERT INTO `recycle_rule` VALUES (12, '回收需要支付上门费吗？', '常规服务范围内免费上门，超远距离或特殊拆机可能收取服务费。', 'appliance_faq', 2, 1, '2026-07-06 14:56:18', '2026-07-06 14:56:18');
INSERT INTO `recycle_rule` VALUES (13, '什么时候能拿到回收款？', '现场确认后可当场结算，部分机型会在检测完成后打款。', 'appliance_faq', 3, 1, '2026-07-06 14:56:18', '2026-07-06 14:56:18');
INSERT INTO `recycle_rule` VALUES (14, '大型家电', '{\"icon\":\"home\",\"top\":\"¥80起\",\"items\":[{\"name\":\"空调（挂机/柜机）\",\"price\":\"¥120~450\"},{\"name\":\"冰箱（单门/多门）\",\"price\":\"¥80~260\"},{\"name\":\"洗衣机（滚筒/波轮）\",\"price\":\"¥60~180\"}]}', 'appliance_more_pricing', 1, 1, '2026-07-06 15:20:37', '2026-07-06 15:20:37');
INSERT INTO `recycle_rule` VALUES (15, '厨房家电', '{\"icon\":\"grid\",\"top\":\"¥20起\",\"items\":[{\"name\":\"微波炉/烤箱\",\"price\":\"¥20~50\"},{\"name\":\"油烟机（集成/侧吸）\",\"price\":\"¥30~80\"},{\"name\":\"洗碗机/消毒柜\",\"price\":\"¥50~120\"}]}', 'appliance_more_pricing', 2, 1, '2026-07-06 15:20:37', '2026-07-06 15:20:37');
INSERT INTO `recycle_rule` VALUES (16, '超远距离费', '{\"icon\":\"map\",\"imageUrl\":\"https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/f15bb33d-a9e8-4399-8ba8-d9a0e4c27bd4.png\",\"theme\":\"gold\",\"desc\":\"服务半径超过 10 公里后开始计费。\",\"lines\":[\"10-15 公里加收 ¥30\",\"15-25 公里加收 ¥60\",\"超过 25 公里以上：需与回收员电话协商\"],\"tags\":[]}', 'appliance_more_service', 1, 1, '2026-07-06 15:20:37', '2026-07-21 16:34:43');
INSERT INTO `recycle_rule` VALUES (17, '特殊拆机服务费', '{\"icon\":\"tools\",\"imageUrl\":\"https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/2e2d04eb-23bd-4c92-bbf6-640cc8dc1af9.png\",\"theme\":\"pink\",\"desc\":\"针对高难度或高空作业收取的专业人工费。\",\"lines\":[\"\"],\"tags\":[{\"name\":\"高空空调拆除\",\"price\":\"¥100 / 台\"},{\"name\":\"嵌入式厨电拆卸\",\"price\":\"¥30 / 台\"}]}', 'appliance_more_service', 2, 1, '2026-07-06 15:20:37', '2026-07-21 16:34:25');
INSERT INTO `recycle_rule` VALUES (18, '什么样的家电不符合回收标准？', '严重污染、涉危品、来源不明或已拆解损毁严重的设备不予回收。', 'appliance_more_tip', 1, 1, '2026-07-06 15:20:37', '2026-07-06 15:20:37');
INSERT INTO `recycle_rule` VALUES (19, '关于数据隐私处理', '含存储介质的智能设备将按标准流程进行数据清除或物理拆解处理。', 'appliance_more_tip', 2, 1, '2026-07-06 15:20:37', '2026-07-06 15:20:37');

-- ----------------------------
-- Table structure for referral_commission
-- ----------------------------
DROP TABLE IF EXISTS `referral_commission`;
CREATE TABLE `referral_commission`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `order_id` bigint(0) NOT NULL COMMENT '订单id',
  `rate` decimal(10, 2) NOT NULL COMMENT '佣金比例',
  `amount` decimal(10, 2) NOT NULL COMMENT '佣金金额',
  `type` int(0) NULL DEFAULT NULL COMMENT '类型 0: 提现 1: 订单佣金 2: 充值佣金',
  `direction` tinyint(0) NOT NULL DEFAULT 1 COMMENT '资金流向: 1=收入, 2=支出',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '交易标题',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '交易描述',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '佣金信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of referral_commission
-- ----------------------------
INSERT INTO `referral_commission` VALUES (5, 20, 87, 0.10, 168.00, 1, 1, NULL, NULL, '2025-12-02 11:08:52', NULL);
INSERT INTO `referral_commission` VALUES (6, 15, 89, 0.10, 714.00, 1, 1, NULL, NULL, '2025-12-18 17:51:46', NULL);
INSERT INTO `referral_commission` VALUES (7, 15, 92, 0.10, 98.00, 1, 1, NULL, NULL, '2025-12-19 18:50:30', NULL);
INSERT INTO `referral_commission` VALUES (8, 44, 76, 0.10, 30.00, 1, 1, '邀请奖励', '好友完成订单：RE202609291759025784', '2026-09-29 19:06:37', NULL);
INSERT INTO `referral_commission` VALUES (9, 44, 0, 0.00, 10.00, 0, 2, '提现到钱包', '提现到钱包: 10.00', '2026-09-29 19:49:33', NULL);
INSERT INTO `referral_commission` VALUES (10, 44, 0, 0.00, 5.00, 0, 2, '提现到钱包', '提现到钱包: 5.00', '2026-09-29 19:54:42', NULL);
INSERT INTO `referral_commission` VALUES (11, 44, 0, 0.00, 5.00, 0, 2, '提现到钱包', '提现到钱包: 5.00', '2026-09-29 19:54:56', NULL);

-- ----------------------------
-- Table structure for referral_user_commission
-- ----------------------------
DROP TABLE IF EXISTS `referral_user_commission`;
CREATE TABLE `referral_user_commission`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `commission` decimal(10, 2) NOT NULL COMMENT '佣金',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户佣金' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of referral_user_commission
-- ----------------------------
INSERT INTO `referral_user_commission` VALUES (4, 15, 0.00);
INSERT INTO `referral_user_commission` VALUES (5, 18, 0.00);
INSERT INTO `referral_user_commission` VALUES (6, 22, 0.00);
INSERT INTO `referral_user_commission` VALUES (7, 23, 0.00);
INSERT INTO `referral_user_commission` VALUES (8, 24, 0.00);
INSERT INTO `referral_user_commission` VALUES (9, 25, 0.00);
INSERT INTO `referral_user_commission` VALUES (10, 26, 0.00);
INSERT INTO `referral_user_commission` VALUES (11, 31, 0.00);
INSERT INTO `referral_user_commission` VALUES (12, 32, 0.00);
INSERT INTO `referral_user_commission` VALUES (13, 33, 7999.00);
INSERT INTO `referral_user_commission` VALUES (14, 34, 0.00);
INSERT INTO `referral_user_commission` VALUES (15, 35, 0.00);
INSERT INTO `referral_user_commission` VALUES (16, 36, 0.00);
INSERT INTO `referral_user_commission` VALUES (17, 37, 8721.00);
INSERT INTO `referral_user_commission` VALUES (18, 38, 0.00);
INSERT INTO `referral_user_commission` VALUES (19, 39, 0.00);
INSERT INTO `referral_user_commission` VALUES (20, 40, 0.00);
INSERT INTO `referral_user_commission` VALUES (21, 41, 0.00);
INSERT INTO `referral_user_commission` VALUES (22, 42, 0.00);
INSERT INTO `referral_user_commission` VALUES (23, 43, 0.00);
INSERT INTO `referral_user_commission` VALUES (24, 44, 10.00);
INSERT INTO `referral_user_commission` VALUES (25, 45, 0.00);
INSERT INTO `referral_user_commission` VALUES (26, 46, 0.00);
INSERT INTO `referral_user_commission` VALUES (27, 47, 0.00);
INSERT INTO `referral_user_commission` VALUES (28, 48, 0.00);
INSERT INTO `referral_user_commission` VALUES (29, 49, 0.00);
INSERT INTO `referral_user_commission` VALUES (30, 50, 0.00);

-- ----------------------------
-- Table structure for search
-- ----------------------------
DROP TABLE IF EXISTS `search`;
CREATE TABLE `search`  (
  `search_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '搜索id',
  `search_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '搜索名称',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户id',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `type` int(0) NULL DEFAULT NULL COMMENT '1服务 2店铺',
  PRIMARY KEY (`search_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '搜索' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of search
-- ----------------------------

-- ----------------------------
-- Table structure for service_area
-- ----------------------------
DROP TABLE IF EXISTS `service_area`;
CREATE TABLE `service_area`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `district` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1启用 2禁用',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_service_area_city`(`province`, `city`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '服务区域' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_area
-- ----------------------------

-- ----------------------------
-- Table structure for sign_in_config
-- ----------------------------
DROP TABLE IF EXISTS `sign_in_config`;
CREATE TABLE `sign_in_config`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `days` int(0) NULL DEFAULT NULL COMMENT '签到天数',
  `reward` int(0) NULL DEFAULT NULL COMMENT '签到奖励数',
  `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '签到配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sign_in_config
-- ----------------------------
INSERT INTO `sign_in_config` VALUES (1, 1, 1, 1, '2025-07-17 15:15:45', NULL);
INSERT INTO `sign_in_config` VALUES (2, 2, 2, 2, '2025-07-17 15:15:53', NULL);
INSERT INTO `sign_in_config` VALUES (3, 3, 3, 3, '2025-07-17 15:16:01', NULL);
INSERT INTO `sign_in_config` VALUES (4, 4, 4, 4, '2025-07-17 15:16:08', NULL);
INSERT INTO `sign_in_config` VALUES (5, 5, 5, 5, '2025-07-17 15:16:17', NULL);
INSERT INTO `sign_in_config` VALUES (6, 6, 6, 6, '2025-07-17 15:16:25', NULL);
INSERT INTO `sign_in_config` VALUES (7, 7, 7, 7, '2025-07-17 15:16:32', NULL);

-- ----------------------------
-- Table structure for sign_in_record
-- ----------------------------
DROP TABLE IF EXISTS `sign_in_record`;
CREATE TABLE `sign_in_record`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户id',
  `sign_time` date NULL DEFAULT NULL COMMENT '签到日期',
  `consecutive_days` int(0) NOT NULL COMMENT '用户连续签到天数',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 115 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '签到记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sign_in_record
-- ----------------------------
INSERT INTO `sign_in_record` VALUES (89, 22, '2025-11-21', 1, '2025-11-25 14:41:05');
INSERT INTO `sign_in_record` VALUES (90, 22, '2025-11-23', 2, '2025-11-25 14:42:46');
INSERT INTO `sign_in_record` VALUES (91, 22, '2025-11-25', 1, '2025-11-25 14:43:43');
INSERT INTO `sign_in_record` VALUES (92, 22, '2025-11-26', 2, '2025-11-26 10:08:20');
INSERT INTO `sign_in_record` VALUES (93, 20, '2025-12-01', 1, '2025-12-01 16:08:45');
INSERT INTO `sign_in_record` VALUES (94, 22, '2025-12-18', 1, '2025-12-18 11:20:09');
INSERT INTO `sign_in_record` VALUES (95, 32, '2025-12-19', 1, '2025-12-19 10:43:30');
INSERT INTO `sign_in_record` VALUES (96, 32, '2025-12-22', 1, '2025-12-22 10:50:09');
INSERT INTO `sign_in_record` VALUES (97, 32, '2025-12-24', 1, '2025-12-24 10:53:17');
INSERT INTO `sign_in_record` VALUES (98, 35, '2025-12-25', 1, '2025-12-25 09:30:35');
INSERT INTO `sign_in_record` VALUES (99, 37, '2025-12-26', 1, '2025-12-26 19:24:09');
INSERT INTO `sign_in_record` VALUES (100, 39, '2026-01-04', 1, '2026-01-05 14:47:36');
INSERT INTO `sign_in_record` VALUES (101, 22, '2026-01-05', 1, '2026-01-05 15:36:13');
INSERT INTO `sign_in_record` VALUES (103, 36, '2026-01-06', 1, '2026-01-06 10:10:49');
INSERT INTO `sign_in_record` VALUES (104, 32, '2026-01-06', 1, '2026-01-06 10:56:29');
INSERT INTO `sign_in_record` VALUES (105, 22, '2026-01-06', 2, '2026-01-06 11:23:37');
INSERT INTO `sign_in_record` VALUES (106, 34, '2026-01-06', 1, '2026-01-06 13:13:17');
INSERT INTO `sign_in_record` VALUES (107, 24, '2026-01-06', 1, '2026-01-06 13:23:35');
INSERT INTO `sign_in_record` VALUES (109, 39, '2026-01-06', 1, '2026-01-06 17:43:00');
INSERT INTO `sign_in_record` VALUES (110, 42, '2026-01-07', 1, '2026-01-07 14:38:48');
INSERT INTO `sign_in_record` VALUES (111, 32, '2026-01-07', 2, '2026-01-07 20:28:33');
INSERT INTO `sign_in_record` VALUES (112, 32, '2026-01-08', 3, '2026-01-08 09:26:35');
INSERT INTO `sign_in_record` VALUES (113, 39, '2026-01-08', 1, '2026-01-08 17:39:23');
INSERT INTO `sign_in_record` VALUES (114, 39, '2026-01-09', 2, '2026-01-09 09:42:07');

-- ----------------------------
-- Table structure for sms_template
-- ----------------------------
DROP TABLE IF EXISTS `sms_template`;
CREATE TABLE `sms_template`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务标识 register/resetPassword 等',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板内容，%s 依次为站点名、验证码',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sms_template_biz_code`(`biz_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '短信模板' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_template
-- ----------------------------
INSERT INTO `sms_template` VALUES (1, 'register', '【%s】您的注册验证码为%s，10分钟内有效。', '2026-07-06 13:19:44', '2026-07-06 13:19:44');
INSERT INTO `sms_template` VALUES (2, 'resetPassword', '【%s】您的重置密码验证码为%s，10分钟内有效。', '2026-07-06 13:19:44', '2026-07-06 13:19:44');

-- ----------------------------
-- Table structure for sys_captcha
-- ----------------------------
DROP TABLE IF EXISTS `sys_captcha`;
CREATE TABLE `sys_captcha`  (
  `uuid` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'uuid',
  `code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '验证码',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统验证码' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_captcha
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `param_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'key',
  `param_value` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '配置内容',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态   0：隐藏   1：显示',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `param_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '类型',
  `param_type_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '类型描述',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `param_key`(`param_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 150 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统配置信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'file_storage_type', NULL, 1, '上传文件存储平台 local:本地 s3:s3云存储 ali: 阿里OSS', 'file', '文件配置');
INSERT INTO `sys_config` VALUES (2, 'file_local_upload_path', '', 1, '本地上传路径', 'file', '文件配置');
INSERT INTO `sys_config` VALUES (3, 'sms_platform', '', 1, '短信平台 1:腾讯云 2: 阿里云 3: 短信宝', 'sms', '短信配置');
INSERT INTO `sys_config` VALUES (4, 'sms_smsbao_username', '', 1, '短信宝用户名', 'sms', '短信配置');
INSERT INTO `sys_config` VALUES (5, 'sms_smsbao_password', NULL, 1, '短信宝密码', 'sms', '短信配置');
INSERT INTO `sys_config` VALUES (6, 'sms_ali_access_key', NULL, 1, '阿里云短信accessKeyId', 'sms', '短信配置');
INSERT INTO `sys_config` VALUES (7, 'sms_ali_access_secret', NULL, 1, '阿里云短信accessSecret', 'sms', '短信配置');
INSERT INTO `sys_config` VALUES (8, 'sys_server_name', NULL, 1, '后台服务名称', 'sys', '系统配置');
INSERT INTO `sys_config` VALUES (9, 'sms_tencent_client_id', NULL, 1, '腾讯云短信clientId', 'sms', '短信配置');
INSERT INTO `sys_config` VALUES (10, 'sms_tencent_client_secret', NULL, 1, '腾讯云短信clientSecret', 'sms', '短信配置');
INSERT INTO `sys_config` VALUES (11, 'sys_h5_domain', NULL, 1, 'h5服务域名配置', 'sys', '系统配置');
INSERT INTO `sys_config` VALUES (12, 'sys_admin_domain', NULL, 1, '后台管理平台域名配置', 'sys', '系统配置');
INSERT INTO `sys_config` VALUES (18, 'pay_ali_appid', NULL, 1, '支付宝appid', 'pay', '支付配置');
INSERT INTO `sys_config` VALUES (19, 'pay_ali_public_key', NULL, 1, '支付宝公钥', 'pay', '支付配置');
INSERT INTO `sys_config` VALUES (20, 'pay_ali_private_key', NULL, 1, '支付宝商户秘钥', 'pay', '支付配置');
INSERT INTO `sys_config` VALUES (28, 'sys_wechat_app_appid', NULL, 1, '微信APPappId', 'sys', '系统配置');
INSERT INTO `sys_config` VALUES (29, 'pay_wechat_mch_key', NULL, 1, '微信商户key', 'pay', '支付配置');
INSERT INTO `sys_config` VALUES (30, 'pay_wechat_mch_id', NULL, 1, '微信商户号mchId', 'pay', '支付配置');
INSERT INTO `sys_config` VALUES (31, 'sys_wechat_h5_appid', NULL, 1, '微信公众号appId', 'sys', '系统配置');
INSERT INTO `sys_config` VALUES (32, 'sys_wechat_jsapi_appid', NULL, 1, '微信小程序appId', 'sys', '系统配置');
INSERT INTO `sys_config` VALUES (33, 'mail_host', NULL, 1, '邮箱服务器地址', 'mail', '邮件配置');
INSERT INTO `sys_config` VALUES (34, 'mail_send_mail', NULL, 1, '发送信息邮箱', 'mail', '邮件配置');
INSERT INTO `sys_config` VALUES (35, 'mail_port', NULL, 1, '邮箱端口号', 'mail', '邮件配置');
INSERT INTO `sys_config` VALUES (36, 'mail_auth_code', NULL, 1, '邮箱授权码', 'mail', '邮件配置');
INSERT INTO `sys_config` VALUES (39, 'file_bt_endpoint', '', 1, '宝塔上传地址', 'file', '文件配置');
INSERT INTO `sys_config` VALUES (41, 'sys_domain', NULL, 1, '域名', 'sys', '系统配置');
INSERT INTO `sys_config` VALUES (46, 'pay_wx_h5_app_id', NULL, 1, '微信支付h5 appid', 'pay', '支付配置');
INSERT INTO `sys_config` VALUES (47, 'pay_wx_jsapi_app_id', NULL, 1, '微信支付jsapi appid', 'pay', '支付配置');
INSERT INTO `sys_config` VALUES (50, 'file_endpoint', NULL, 1, '第三方文件存储endpoint', 'file', '文件配置');
INSERT INTO `sys_config` VALUES (51, 'file_bucket', NULL, 1, '第三方文件存储bucket', 'file', '文件配置');
INSERT INTO `sys_config` VALUES (52, 'file_ali_oss_accessKeyId', NULL, 1, '阿里oss accessKeyId', 'file', '文件配置');
INSERT INTO `sys_config` VALUES (53, 'file_ali_oss_secretAccessKey', NULL, 1, '阿里oss secretAccessKey', 'file', '文件配置');
INSERT INTO `sys_config` VALUES (54, 'pay_ali_app_cert_path', NULL, 1, '支付宝应用公钥证书路径', 'pay', '支付配置');
INSERT INTO `sys_config` VALUES (59, 'pay_ali_alipay_public_cert_path', NULL, 1, '支付宝公钥证书路径', 'pay', '支付配置');
INSERT INTO `sys_config` VALUES (60, 'pay_ali_root_cert_path', NULL, 1, '支付宝根证书路径', 'pay', '支付配置');
INSERT INTO `sys_config` VALUES (61, 'pay_ali_mode', NULL, 1, '支付宝支付模式 1: 普通公钥模式 2: 证书模式', 'pay', '支付配置');
INSERT INTO `sys_config` VALUES (62, 'pay_ali_sandbox', '', 1, '支付宝是否开启沙箱模式 1: 是 2: 否', 'pay', '支付配置');
INSERT INTO `sys_config` VALUES (63, 'map_type', NULL, 1, '地图类型 1: 腾讯地图 2：天地图 3：谷歌地图', 'map', '地图配置');
INSERT INTO `sys_config` VALUES (64, 'google_map_key', '', 1, '谷歌地图key', 'map', '地图配置');
INSERT INTO `sys_config` VALUES (67, 'sys_wx_key_path', '', 1, '微信证书路径', 'map', '地图配置');
INSERT INTO `sys_config` VALUES (68, 'sys_wechat_jsapi_secret', '', 1, '微信小程序秘钥', 'map', '地图配置');
INSERT INTO `sys_config` VALUES (69, 'sys_wechat_h5_secret', '', 1, '微信公众号秘钥', 'map', '地图配置');
INSERT INTO `sys_config` VALUES (71, 'sms_provider', '', 1, NULL, 'default', '其他配置');
INSERT INTO `sys_config` VALUES (72, 'appliance_hero_badge', '', 1, '家电回收顶部标签', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (73, 'appliance_hero_title_line1', '', 1, '家电回收顶部标题1', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (74, 'appliance_hero_title_line2', '', 1, '家电回收顶部标题2', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (80, 'app_push_remind_enable', '', 1, '首页推送提醒开关', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (81, 'app_push_remind_text', '', 1, '首页推送提醒文案', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (82, 'home_invite_title', '', 1, '首页邀请横幅标题', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (83, 'home_invite_desc', '', 1, '首页邀请横幅描述', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (84, 'home_invite_btn_text', '', 1, '首页邀请横幅按钮', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (85, 'home_invite_url', '', 1, '首页邀请横幅跳转', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (86, 'appliance_more_hero_title', '', 1, '家电规则详情页顶部标题', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (87, 'appliance_more_hero_desc', '', 1, '家电规则详情页顶部描述', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (88, 'appliance_more_hero_icon', '', 1, '家电规则详情页顶部图标', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (89, 'appliance_more_price_note', '', 1, '家电规则详情页价格说明', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (90, 'appliance_more_carbon_title', '', 1, '家电规则详情页环保卡片标题', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (91, 'appliance_more_carbon_desc', '', 1, '家电规则详情页环保卡片描述', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (92, 'appliance_more_booking_category', '', 1, '家电规则详情页默认预约品类', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (100, 'book_unit_price', '', 1, '书籍回收单价', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (101, 'book_unit_price_suffix', '', 1, '书籍回收单价后缀', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (102, 'book_price_desc', '', 1, '书籍回收单价说明', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (103, 'book_service_range_desc', '', 1, '书籍预约-服务范围说明', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (104, 'book_service_range_tag', '', 1, '书籍预约-服务范围标签', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (105, 'book_order_notice', '', 1, '书籍预约-下单须知', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (106, 'book_science_title', '', 1, '书籍预约-去向科普标题', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (107, 'book_science_desc', '', 1, '书籍预约-去向科普说明', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (108, 'book_agreement_text', '', 1, '书籍预约-协议勾选文案', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (110, 'order_settlement_confirmed_desc', '', 1, '结算确认页-已确认预估金额说明', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (111, 'welfare_sign_total_days', '', 1, '连续签到总天数', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (112, 'welfare_sign_daily_coin', '', 1, '每日签到奖励金币', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (113, 'welfare_withdraw_min_amount', '', 1, '福利中心最低提现金额', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (114, 'service_chat_welcome_title', '', 1, '客服页-欢迎标题', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (115, 'service_chat_welcome_desc', '', 1, '客服页-欢迎说明', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (118, 'service_chat_transfer_hint', '', 1, '客服页-转人工提示文案', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (120, 'user_about_us', '', 1, '关于我们', 'agreement', '协议配置');
INSERT INTO `sys_config` VALUES (121, 'biz_referral_commission_rate', '', 1, '订单完成后给邀请人的佣金比例，0.1 表示 10%', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (122, 'biz_referral_commission_coin_rate', '', 1, '推荐佣金折算福利金币比例，1 表示 1 元佣金 = 1 金币', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (123, 'file_cdn_domain', '', 1, '文件访问 CDN 域名，如 https://bucket.oss-cn-beijing.aliyuncs.com；留空则自动按 bucket+endpoint 拼接', 'file', '文件配置');
INSERT INTO `sys_config` VALUES (124, 'appliance_mail_address', '', 1, '家电顺丰邮寄-仓库收件地址', NULL, NULL);
INSERT INTO `sys_config` VALUES (126, 'recycle_appointment_slots', '', 1, '上门预约展示天数（仅配置天数，时段由系统默认）', NULL, NULL);
INSERT INTO `sys_config` VALUES (127, 'sys_withdraw_min', '', 1, '钱包提现-最低提现金额', NULL, NULL);
INSERT INTO `sys_config` VALUES (128, 'sys_withdraw_max', '', 1, '钱包提现-最高提现金额', NULL, NULL);
INSERT INTO `sys_config` VALUES (129, 'sys_withdraw_rate', '', 1, '钱包提现-手续费率', NULL, NULL);
INSERT INTO `sys_config` VALUES (130, 'sys_withdraw_amount_options', '', 1, '钱包提现-快捷金额配置', NULL, NULL);
INSERT INTO `sys_config` VALUES (131, 'user_privacy_agreement', '', 1, '隐私政策', 'agreement', '协议配置');
INSERT INTO `sys_config` VALUES (132, 'user_service_agreement', '', 1, '用户服务协议', 'agreement', '协议配置');
INSERT INTO `sys_config` VALUES (133, 'platform_mail_receiver', '', 1, '平台收件人', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (134, 'platform_mail_receiver_phone', '', 1, '平台收件人电话', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (135, 'platform_mail_receiver_address', '', 1, '平台收件地址', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (136, 'referral_rules_json', '', 1, '分佣规则页展示内容', 'agreement', '协议配置');
INSERT INTO `sys_config` VALUES (137, 'sf_butler_base_url', '', 1, '顺丰管家API域名（测试/生产）', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (138, 'sf_butler_app_id', '', 1, '顺丰管家 sendAppId', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (139, 'sf_butler_sk', '', 1, '顺丰管家签名密钥 SK', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (140, 'sf_butler_company_id', '', 1, '顺丰管家 companyId（同 sendAppId 数值）', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (141, 'sf_butler_custid', '', 1, '顺丰月结卡号（测试环境统一 9999999999）', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (142, 'sf_butler_pay_method', '', 1, '运费付款方式：0寄付月结 1寄付现结 2收方付 3第三方付', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (143, 'sf_butler_express_type', '', 1, '快件产品类别（与顺丰约定，默认 2=标快）', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (144, 'sf_butler_route_uri', '', 1, '顺丰路由查询 URI', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (145, 'platform_mail_receiver_province', '', 1, '平台收件省', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (146, 'platform_mail_receiver_city', '', 1, '平台收件市', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (147, 'platform_mail_receiver_county', '', 1, '平台收件区/县', 'default', '其他配置');
INSERT INTO `sys_config` VALUES (148, 'welfare_coin_wallet_rate', '', 1, '环保积分兑换钱包余额比例，1 表示 1 积分 = 1 元', 'welfare', '福利中心');
INSERT INTO `sys_config` VALUES (149, 'aliyun_logistics_appcode', '', 1, '阿里云物流查询 AppCode（市场商品 wuliu.market.alicloudapi.com）', 'default', '其他配置');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '字典名称',
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '字典类型',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '字典码',
  `value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '字典值',
  `order_num` int(0) NULL DEFAULT 0 COMMENT '排序',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `del_flag` int(0) NULL DEFAULT 0 COMMENT '删除标记  -1：已删除  0：正常',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `type`(`type`, `code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '数据字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1, '首页位置1', 'index_position', 'first', '1', 0, NULL, 0);
INSERT INTO `sys_dict` VALUES (2, '首页位置2', 'index_position', 'second', '2', 0, NULL, 0);
INSERT INTO `sys_dict` VALUES (3, '首页位置3', 'index_position', 'third', '3', 0, NULL, 0);
INSERT INTO `sys_dict` VALUES (4, '首页位置4', 'index_position', 'fourth', '4', 0, NULL, 0);
INSERT INTO `sys_dict` VALUES (5, '首页位置5', 'index_position', 'fifth', '5', 0, NULL, 0);

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户操作',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '请求方法',
  `params` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '请求参数',
  `time` bigint(0) NOT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'IP地址',
  `create_date` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 454 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES (1, 'nyy', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":72,\"parentId\":0,\"name\":\"资料类型\",\"url\":\"locality\",\"perms\":\"\",\"type\":1,\"icon\":\"order\",\"orderNum\":5}]', 8, '192.168.0.110', '2024-06-11 15:40:00');
INSERT INTO `sys_log` VALUES (2, 'nyy', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,168,169,170,72,73,74,75,76,77,78,92,93,149,165,166,167,171,172,111,112,113,114,115,127,128,174,175,176,177,178,116,117,118,119,120,144,145,146,147,148,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666]}]', 286, '192.168.0.110', '2024-06-11 16:18:45');
INSERT INTO `sys_log` VALUES (3, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":184,\"parentId\":0,\"name\":\"题录类型\",\"url\":\"collectionList\",\"perms\":\"\",\"type\":1,\"icon\":\"fenleilist\",\"orderNum\":5}]', 17, '192.168.0.110', '2024-06-11 16:29:21');
INSERT INTO `sys_log` VALUES (4, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":185,\"parentId\":184,\"name\":\"查看\",\"url\":\"\",\"perms\":\"collectionList:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 7, '192.168.0.110', '2024-06-11 16:29:39');
INSERT INTO `sys_log` VALUES (5, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":186,\"parentId\":184,\"name\":\"添加\",\"url\":\"\",\"perms\":\"collectionList:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.110', '2024-06-11 16:29:51');
INSERT INTO `sys_log` VALUES (6, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":187,\"parentId\":184,\"name\":\"修改\",\"url\":\"\",\"perms\":\"collectionList:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.110', '2024-06-11 16:30:07');
INSERT INTO `sys_log` VALUES (7, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":188,\"parentId\":184,\"name\":\"删除\",\"url\":\"\",\"perms\":\"collectionList:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 32, '192.168.0.110', '2024-06-11 16:31:08');
INSERT INTO `sys_log` VALUES (8, 'nyy', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":184,\"parentId\":0,\"name\":\"题库类型\",\"url\":\"collectionList\",\"type\":1,\"icon\":\"fenleilist\",\"orderNum\":5}]', 14, '192.168.0.110', '2024-06-11 16:31:22');
INSERT INTO `sys_log` VALUES (9, 'nyy', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,168,169,170,72,73,74,75,76,184,185,186,187,188,77,78,92,93,149,165,166,167,171,172,111,112,113,114,115,127,128,174,175,176,177,178,116,117,118,119,120,144,145,146,147,148,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666]}]', 282, '192.168.0.110', '2024-06-11 16:32:20');
INSERT INTO `sys_log` VALUES (10, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":189,\"parentId\":0,\"name\":\"题目类型\",\"url\":\"questionType\",\"perms\":\"\",\"type\":1,\"icon\":\"fenleilist\",\"orderNum\":5}]', 9, '192.168.0.107', '2024-06-18 15:24:39');
INSERT INTO `sys_log` VALUES (11, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":190,\"parentId\":189,\"name\":\"查看\",\"url\":\"\",\"perms\":\"questionType:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.107', '2024-06-18 15:24:54');
INSERT INTO `sys_log` VALUES (12, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":191,\"parentId\":189,\"name\":\"添加\",\"url\":\"\",\"perms\":\"questionType:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.107', '2024-06-18 15:25:06');
INSERT INTO `sys_log` VALUES (13, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":192,\"parentId\":189,\"name\":\"修改\",\"url\":\"\",\"perms\":\"questionType:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.107', '2024-06-18 15:25:21');
INSERT INTO `sys_log` VALUES (14, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":193,\"parentId\":189,\"name\":\"删除\",\"url\":\"\",\"perms\":\"questionType:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 7, '192.168.0.107', '2024-06-18 15:25:32');
INSERT INTO `sys_log` VALUES (15, 'nyy', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,168,169,170,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,77,78,92,93,149,165,166,167,171,172,111,112,113,114,115,127,128,174,175,176,177,178,116,117,118,119,120,144,145,146,147,148,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666]}]', 721, '192.168.0.107', '2024-06-18 15:25:58');
INSERT INTO `sys_log` VALUES (16, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":194,\"parentId\":0,\"name\":\"题目管理\",\"url\":\"casting\",\"perms\":\"\",\"type\":1,\"icon\":\"tianjia\",\"orderNum\":5}]', 7, '192.168.0.107', '2024-06-18 16:47:13');
INSERT INTO `sys_log` VALUES (17, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":195,\"parentId\":194,\"name\":\"查看\",\"url\":\"\",\"perms\":\"casting:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.107', '2024-06-18 16:47:27');
INSERT INTO `sys_log` VALUES (18, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":196,\"parentId\":194,\"name\":\"添加\",\"url\":\"\",\"perms\":\"casting:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.107', '2024-06-18 16:47:40');
INSERT INTO `sys_log` VALUES (19, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":197,\"parentId\":194,\"name\":\"修改\",\"url\":\"\",\"perms\":\"casting:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 18, '192.168.0.107', '2024-06-18 16:47:57');
INSERT INTO `sys_log` VALUES (20, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":198,\"parentId\":194,\"name\":\"删除\",\"url\":\"\",\"perms\":\"casting:telete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 8, '192.168.0.107', '2024-06-18 16:48:13');
INSERT INTO `sys_log` VALUES (21, 'nyy', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,168,169,170,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,77,78,92,93,149,165,166,167,171,172,111,112,113,114,115,127,128,174,175,176,177,178,116,117,118,119,120,144,145,146,147,148,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666]}]', 201, '192.168.0.107', '2024-06-18 16:48:37');
INSERT INTO `sys_log` VALUES (22, 'nyy', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":77,\"parentId\":0,\"name\":\"考试科目\",\"url\":\"autonym\",\"perms\":\"\",\"type\":1,\"icon\":\"renwu\",\"orderNum\":6}]', 9, '192.168.0.106', '2024-06-19 10:17:15');
INSERT INTO `sys_log` VALUES (23, 'nyy', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":57,\"parentId\":0,\"name\":\"系统配置\",\"url\":\"allocationList\",\"perms\":\"\",\"type\":1,\"icon\":\"menu\",\"orderNum\":19}]', 6, '192.168.0.106', '2024-06-19 15:51:28');
INSERT INTO `sys_log` VALUES (24, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":199,\"parentId\":0,\"name\":\"门票配置\",\"url\":\"taskConfig\",\"perms\":\"\",\"type\":1,\"icon\":\"shezhi\",\"orderNum\":3}]', 36, '192.168.0.106', '2024-06-19 15:52:11');
INSERT INTO `sys_log` VALUES (25, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":200,\"parentId\":199,\"name\":\"查看\",\"url\":\"\",\"perms\":\"taskConfig:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 7, '192.168.0.106', '2024-06-19 15:52:29');
INSERT INTO `sys_log` VALUES (26, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":201,\"parentId\":199,\"name\":\"添加\",\"url\":\"\",\"perms\":\"taskConfig:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 7, '192.168.0.106', '2024-06-19 15:52:44');
INSERT INTO `sys_log` VALUES (27, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":202,\"parentId\":199,\"name\":\"修改\",\"url\":\"\",\"perms\":\"taskConfig:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.106', '2024-06-19 15:52:58');
INSERT INTO `sys_log` VALUES (28, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":203,\"parentId\":199,\"name\":\"删除\",\"url\":\"\",\"perms\":\"taskConfig:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 7, '192.168.0.106', '2024-06-19 15:53:10');
INSERT INTO `sys_log` VALUES (29, 'nyy', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,199,200,201,202,203,168,169,170,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,77,78,92,93,149,165,166,167,171,172,111,112,113,114,115,127,128,174,175,176,177,178,116,117,118,119,120,144,145,146,147,148,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666]}]', 331, '192.168.0.106', '2024-06-19 15:53:33');
INSERT INTO `sys_log` VALUES (30, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":204,\"parentId\":0,\"name\":\"商品管理\",\"url\":\"\",\"perms\":\"\",\"type\":0,\"icon\":\"\",\"orderNum\":0}]', 19, '192.168.0.107', '2024-06-20 16:56:15');
INSERT INTO `sys_log` VALUES (31, 'nyy', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":204,\"parentId\":0,\"name\":\"商品管理\",\"type\":0,\"icon\":\"shangpin\",\"orderNum\":3}]', 14, '192.168.0.107', '2024-06-20 16:56:31');
INSERT INTO `sys_log` VALUES (32, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":205,\"parentId\":204,\"name\":\"商品分类\",\"url\":\"classifyAdmin\",\"type\":1,\"icon\":\"fenleilist\",\"orderNum\":0}]', 7, '192.168.0.107', '2024-06-20 16:56:56');
INSERT INTO `sys_log` VALUES (33, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":206,\"parentId\":205,\"name\":\"查看\",\"perms\":\"classifyAdmin:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.107', '2024-06-20 16:57:10');
INSERT INTO `sys_log` VALUES (34, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":207,\"parentId\":205,\"name\":\"添加\",\"perms\":\"classifyAdmin:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 7, '192.168.0.107', '2024-06-20 16:57:22');
INSERT INTO `sys_log` VALUES (35, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":208,\"parentId\":205,\"name\":\"修改\",\"perms\":\"classifyAdmin:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.107', '2024-06-20 16:57:34');
INSERT INTO `sys_log` VALUES (36, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":209,\"parentId\":205,\"name\":\"删除\",\"perms\":\"classifyAdmin:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.107', '2024-06-20 16:57:45');
INSERT INTO `sys_log` VALUES (37, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":210,\"parentId\":204,\"name\":\"商品列表\",\"url\":\"shopAdmin\",\"type\":1,\"icon\":\"peizhilb\",\"orderNum\":0}]', 8, '192.168.0.107', '2024-06-20 16:58:11');
INSERT INTO `sys_log` VALUES (38, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":211,\"parentId\":210,\"name\":\"查看\",\"perms\":\"shopAdmin:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 7, '192.168.0.107', '2024-06-20 16:58:51');
INSERT INTO `sys_log` VALUES (39, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":212,\"parentId\":210,\"name\":\"添加\",\"perms\":\"shopAdmin:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.107', '2024-06-20 16:59:02');
INSERT INTO `sys_log` VALUES (40, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":213,\"parentId\":210,\"name\":\"修改\",\"perms\":\"shopAdmin:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 5, '192.168.0.107', '2024-06-20 16:59:17');
INSERT INTO `sys_log` VALUES (41, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":214,\"parentId\":210,\"name\":\"删除\",\"perms\":\"shopAdmin:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.107', '2024-06-20 16:59:29');
INSERT INTO `sys_log` VALUES (42, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":215,\"parentId\":204,\"name\":\"评论管理\",\"url\":\"comment\",\"perms\":\"\",\"type\":1,\"icon\":\"pinglun\",\"orderNum\":0}]', 10, '192.168.0.107', '2024-06-20 17:01:44');
INSERT INTO `sys_log` VALUES (43, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":216,\"parentId\":215,\"name\":\"查看\",\"url\":\"\",\"perms\":\"comment:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 7, '192.168.0.107', '2024-06-20 17:01:58');
INSERT INTO `sys_log` VALUES (44, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":217,\"parentId\":215,\"name\":\"添加\",\"url\":\"\",\"perms\":\"comment:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.107', '2024-06-20 17:02:10');
INSERT INTO `sys_log` VALUES (45, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":218,\"parentId\":215,\"name\":\"修改\",\"url\":\"\",\"perms\":\"comment:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.107', '2024-06-20 17:02:23');
INSERT INTO `sys_log` VALUES (46, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":219,\"parentId\":215,\"name\":\"删除\",\"url\":\"\",\"perms\":\"comment:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 9, '192.168.0.107', '2024-06-20 17:02:35');
INSERT INTO `sys_log` VALUES (47, 'nyy', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,77,78,92,93,149,165,166,167,171,172,111,112,113,114,115,127,128,174,175,176,177,178,116,117,118,119,120,144,145,146,147,148,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666]}]', 355, '192.168.0.107', '2024-06-20 17:04:27');
INSERT INTO `sys_log` VALUES (48, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":220,\"parentId\":0,\"name\":\"课节管理\",\"url\":\"classSection\",\"perms\":\"\",\"type\":1,\"icon\":\"peizhi\",\"orderNum\":5}]', 8, '192.168.0.105', '2024-06-24 14:20:35');
INSERT INTO `sys_log` VALUES (49, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":221,\"parentId\":220,\"name\":\"查看\",\"url\":\"\",\"perms\":\"classSection:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 7, '192.168.0.105', '2024-06-24 14:20:50');
INSERT INTO `sys_log` VALUES (50, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":222,\"parentId\":220,\"name\":\"添加\",\"url\":\"\",\"perms\":\"classSection:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.105', '2024-06-24 14:21:07');
INSERT INTO `sys_log` VALUES (51, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":223,\"parentId\":220,\"name\":\"修改\",\"url\":\"\",\"perms\":\"classSection:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.105', '2024-06-24 14:21:24');
INSERT INTO `sys_log` VALUES (52, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":224,\"parentId\":220,\"name\":\"删除\",\"url\":\"\",\"perms\":\"classSection:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.105', '2024-06-24 14:21:36');
INSERT INTO `sys_log` VALUES (53, 'nyy', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,220,221,222,223,224,77,78,92,93,149,165,166,167,171,172,111,112,113,114,115,127,128,174,175,176,177,178,116,117,118,119,120,144,145,146,147,148,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666]}]', 385, '192.168.0.105', '2024-06-24 14:22:04');
INSERT INTO `sys_log` VALUES (54, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":225,\"parentId\":0,\"name\":\"其他配置\",\"url\":\"\",\"perms\":\"\",\"type\":0,\"icon\":\"shangpin\",\"orderNum\":6}]', 5, '192.168.0.105', '2024-06-24 16:45:22');
INSERT INTO `sys_log` VALUES (55, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":226,\"parentId\":225,\"name\":\"签到配置\",\"url\":\"taskConfig\",\"perms\":\"\",\"type\":1,\"icon\":\"config\",\"orderNum\":0}]', 6, '192.168.0.105', '2024-06-24 16:45:57');
INSERT INTO `sys_log` VALUES (56, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":227,\"parentId\":226,\"name\":\"查看\",\"url\":\"\",\"perms\":\"taskConfig:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 9, '192.168.0.105', '2024-06-24 16:46:11');
INSERT INTO `sys_log` VALUES (57, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":228,\"parentId\":226,\"name\":\"添加\",\"url\":\"\",\"perms\":\"taskConfig:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 5, '192.168.0.105', '2024-06-24 16:46:26');
INSERT INTO `sys_log` VALUES (58, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":229,\"parentId\":226,\"name\":\"修改\",\"url\":\"\",\"perms\":\"taskConfig:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.105', '2024-06-24 16:46:44');
INSERT INTO `sys_log` VALUES (59, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":230,\"parentId\":226,\"name\":\"删除\",\"url\":\"\",\"perms\":\"taskConfig:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 7, '192.168.0.105', '2024-06-24 16:46:56');
INSERT INTO `sys_log` VALUES (60, 'nyy', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":199,\"parentId\":225,\"name\":\"门票配置\",\"url\":\"taskConfig\",\"type\":1,\"icon\":\"shezhi\",\"orderNum\":0}]', 8, '192.168.0.105', '2024-06-24 16:47:30');
INSERT INTO `sys_log` VALUES (61, 'nyy', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":171,\"parentId\":225,\"name\":\"聊天记录\",\"url\":\"chatRecord\",\"type\":1,\"icon\":\"pinglun\",\"orderNum\":0}]', 6, '192.168.0.105', '2024-06-24 16:47:48');
INSERT INTO `sys_log` VALUES (62, 'nyy', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":111,\"parentId\":204,\"name\":\"公告管理\",\"url\":\"dynamic\",\"perms\":\"\",\"type\":1,\"icon\":\"leibie\",\"orderNum\":0}]', 6, '192.168.0.105', '2024-06-24 16:48:03');
INSERT INTO `sys_log` VALUES (63, 'nyy', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,204,111,112,113,114,115,127,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,220,221,222,223,224,77,78,92,93,149,165,166,167,225,171,172,199,200,201,202,203,226,227,228,229,230,128,174,175,176,177,178,116,117,118,119,120,144,145,146,147,148,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666]}]', 267, '192.168.0.105', '2024-06-24 16:48:38');
INSERT INTO `sys_log` VALUES (64, 'nyy', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":144,\"parentId\":225,\"name\":\"帮助中心\",\"url\":\"materialsList\",\"perms\":\"\",\"type\":1,\"icon\":\"renwu\",\"orderNum\":8}]', 5, '192.168.0.105', '2024-06-24 16:49:01');
INSERT INTO `sys_log` VALUES (65, 'nyy', '删除菜单', 'com.sqx.modules.sys.controller.SysMenuController.delete()', '[203]', 42, '192.168.0.105', '2024-06-24 16:50:50');
INSERT INTO `sys_log` VALUES (66, 'nyy', '删除菜单', 'com.sqx.modules.sys.controller.SysMenuController.delete()', '[202]', 9, '192.168.0.105', '2024-06-24 16:51:01');
INSERT INTO `sys_log` VALUES (67, 'nyy', '删除菜单', 'com.sqx.modules.sys.controller.SysMenuController.delete()', '[201]', 11, '192.168.0.105', '2024-06-24 16:51:13');
INSERT INTO `sys_log` VALUES (68, 'nyy', '删除菜单', 'com.sqx.modules.sys.controller.SysMenuController.delete()', '[200]', 10, '192.168.0.105', '2024-06-24 16:51:24');
INSERT INTO `sys_log` VALUES (69, 'nyy', '删除菜单', 'com.sqx.modules.sys.controller.SysMenuController.delete()', '[199]', 10, '192.168.0.105', '2024-06-24 16:51:38');
INSERT INTO `sys_log` VALUES (70, 'nyy', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":198,\"parentId\":194,\"name\":\"删除\",\"perms\":\"casting:delete\",\"type\":2,\"orderNum\":0}]', 10, '192.168.0.108', '2024-06-26 13:18:21');
INSERT INTO `sys_log` VALUES (71, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":231,\"parentId\":128,\"name\":\"优惠券管理\",\"url\":\"couponYhq\",\"perms\":\"\",\"type\":1,\"icon\":\"caiwu\",\"orderNum\":0}]', 20, '192.168.0.105', '2024-06-27 13:47:46');
INSERT INTO `sys_log` VALUES (72, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":232,\"parentId\":231,\"name\":\"查看\",\"url\":\"\",\"perms\":\"couponYhq:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.105', '2024-06-27 13:48:04');
INSERT INTO `sys_log` VALUES (73, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":233,\"parentId\":231,\"name\":\"添加\",\"url\":\"\",\"perms\":\"couponYhq:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.105', '2024-06-27 13:48:14');
INSERT INTO `sys_log` VALUES (74, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":234,\"parentId\":231,\"name\":\"修改\",\"url\":\"\",\"perms\":\"couponYhq:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.105', '2024-06-27 13:48:32');
INSERT INTO `sys_log` VALUES (75, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":235,\"parentId\":231,\"name\":\"删除\",\"url\":\"\",\"perms\":\"couponYhq:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 7, '192.168.0.105', '2024-06-27 13:48:43');
INSERT INTO `sys_log` VALUES (76, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":236,\"parentId\":128,\"name\":\"优惠券领取记录\",\"url\":\"couponuser\",\"perms\":\"\",\"type\":1,\"icon\":\"order\",\"orderNum\":4}]', 6, '192.168.0.105', '2024-06-27 13:49:17');
INSERT INTO `sys_log` VALUES (77, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":237,\"parentId\":236,\"name\":\"查看\",\"url\":\"\",\"perms\":\"couponuser:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.105', '2024-06-27 13:49:36');
INSERT INTO `sys_log` VALUES (78, 'nyy', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":116,\"parentId\":128,\"name\":\"会员配置\",\"url\":\"coupon\",\"perms\":\"\",\"type\":1,\"icon\":\"caiwu\",\"orderNum\":0}]', 11, '192.168.0.105', '2024-06-27 13:49:55');
INSERT INTO `sys_log` VALUES (79, 'nyy', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,204,111,112,113,114,115,127,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,220,221,222,223,224,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666]}]', 383, '192.168.0.105', '2024-06-27 13:50:14');
INSERT INTO `sys_log` VALUES (80, 'nyy', '保存菜单', 'com.sqx.modules.sys.controller.SysMenuController.save()', '[{\"menuId\":238,\"parentId\":231,\"name\":\"赠送\",\"url\":\"\",\"perms\":\"couponYhq:zengsong\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 24, '192.168.0.105', '2024-06-27 14:59:20');
INSERT INTO `sys_log` VALUES (81, 'nyy', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,204,111,112,113,114,115,127,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,220,221,222,223,224,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666]}]', 350, '192.168.0.105', '2024-06-27 14:59:39');
INSERT INTO `sys_log` VALUES (82, 'zww', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":10,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,204,111,112,113,114,115,127,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,220,221,222,223,224,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666]}]', 336, '192.168.0.126', '2024-07-02 11:05:07');
INSERT INTO `sys_log` VALUES (83, 'zww', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":4,\"roleName\":\"普通用户\",\"remark\":\"普通用户\",\"createUserId\":10,\"menuIdList\":[33,60,90,91,105,161,35,36,37,38,39,173,42,162,163,164,51,154,155,158,157,112,168,169,170,73,74,75,77,78,92,93,149,165,166,167,171,172,144,145,146,147,148,117,118,119,58,15,19,23,-666666,32,34,41,50,204,111,72,225,128,116,57,126,2,3,4]}]', 137, '192.168.0.126', '2024-07-02 11:05:20');
INSERT INTO `sys_log` VALUES (84, 'zww', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":10,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,220,221,222,223,224,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666,204]}]', 303, '192.168.0.126', '2024-07-02 15:58:37');
INSERT INTO `sys_log` VALUES (85, 'zww', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":4,\"roleName\":\"普通用户\",\"remark\":\"普通用户\",\"createUserId\":10,\"menuIdList\":[33,60,90,91,105,161,35,36,37,38,39,173,42,162,163,164,51,154,155,158,157,168,169,170,73,74,75,77,78,92,93,149,165,166,167,171,172,144,145,146,147,148,117,118,119,58,15,19,23,-666666,32,34,41,50,72,225,128,116,57,126,2,3,4]}]', 106, '192.168.0.126', '2024-07-02 15:58:50');
INSERT INTO `sys_log` VALUES (86, 'nyy', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":77,\"parentId\":0,\"name\":\"模块大赛\",\"url\":\"autonym\",\"perms\":\"\",\"type\":1,\"icon\":\"renwu\",\"orderNum\":6}]', 7, '192.168.0.110', '2024-07-10 16:14:16');
INSERT INTO `sys_log` VALUES (87, 'zww', '修改菜单', 'com.sqx.modules.sys.controller.SysMenuController.update()', '[{\"menuId\":77,\"parentId\":0,\"name\":\"模考大赛\",\"url\":\"autonym\",\"perms\":\"\",\"type\":1,\"icon\":\"renwu\",\"orderNum\":6}]', 8, '192.168.0.114', '2024-07-11 16:26:30');
INSERT INTO `sys_log` VALUES (88, 'nyy', '修改角色', 'com.sqx.modules.sys.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,162,163,164,50,51,52,53,54,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,220,221,222,223,224,77,78,92,93,149,165,166,167,226,227,228,229,230,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666,204,225]}]', 408, '192.168.0.115', '2024-07-15 11:07:14');
INSERT INTO `sys_log` VALUES (89, 'admin', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":2,\"parentId\":126,\"name\":\"管理员列表\",\"url\":\"user\",\"type\":1,\"icon\":\"svg:zhanghao.svg\",\"orderNum\":10}]', 14, '192.168.0.136', '2025-09-03 14:42:06');
INSERT INTO `sys_log` VALUES (90, 'admin', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":3,\"parentId\":126,\"name\":\"角色管理\",\"url\":\"role\",\"type\":1,\"icon\":\"svg:tag.svg\",\"orderNum\":11}]', 5, '192.168.0.136', '2025-09-03 14:42:21');
INSERT INTO `sys_log` VALUES (91, 'admin', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":4,\"parentId\":126,\"name\":\"菜单管理\",\"url\":\"menu\",\"type\":1,\"icon\":\"svg:Directory-tree.svg\",\"orderNum\":12}]', 12, '192.168.0.136', '2025-09-03 14:42:41');
INSERT INTO `sys_log` VALUES (92, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":150,\"parentId\":0,\"name\":\"领队申请\",\"url\":\"authentication\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:flag.svg\",\"orderNum\":8}]', 11, '192.168.0.136', '2025-09-03 14:46:31');
INSERT INTO `sys_log` VALUES (93, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":57,\"parentId\":0,\"name\":\"系统配置\",\"url\":\"allocationList\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:cog-fill.svg\",\"orderNum\":19}]', 11, '192.168.0.136', '2025-09-03 14:46:44');
INSERT INTO `sys_log` VALUES (94, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":126,\"parentId\":0,\"name\":\"系统管理\",\"url\":\"\",\"perms\":\"\",\"type\":0,\"icon\":\"svg:cat-code.svg\",\"orderNum\":20}]', 12, '192.168.0.136', '2025-09-03 14:46:52');
INSERT INTO `sys_log` VALUES (95, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":204,\"parentId\":0,\"name\":\"自营商城\",\"type\":0,\"icon\":\"svg:all-fill.svg\",\"orderNum\":3}]', 6, '192.168.0.136', '2025-09-03 14:55:19');
INSERT INTO `sys_log` VALUES (96, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":72,\"parentId\":0,\"name\":\"团队定制管理\",\"url\":\"locality\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:database-plus.svg\",\"orderNum\":5}]', 12, '192.168.0.136', '2025-09-03 14:56:12');
INSERT INTO `sys_log` VALUES (97, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":50,\"parentId\":0,\"name\":\"首页装修\",\"url\":\"bannerList\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:Navbar-full.svg\",\"orderNum\":2}]', 4, '192.168.0.136', '2025-09-03 14:56:33');
INSERT INTO `sys_log` VALUES (98, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":184,\"parentId\":0,\"name\":\"签到配置\",\"url\":\"collectionList\",\"type\":1,\"icon\":\"svg:cat-code.svg\",\"orderNum\":5}]', 10, '192.168.0.136', '2025-09-03 15:08:06');
INSERT INTO `sys_log` VALUES (99, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":189,\"parentId\":0,\"name\":\"任务配置\",\"url\":\"questionType\",\"type\":1,\"icon\":\"svg:collection-fill.svg\",\"orderNum\":5}]', 5, '192.168.0.136', '2025-09-03 15:08:24');
INSERT INTO `sys_log` VALUES (100, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":239,\"parentId\":0,\"name\":\"目的地管理\",\"type\":0,\"icon\":\"svg:map-fill.svg\",\"orderNum\":4}]', 13, '192.168.0.136', '2025-09-03 15:10:05');
INSERT INTO `sys_log` VALUES (101, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":77,\"parentId\":0,\"name\":\"动态管理\",\"url\":\"autonym\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:chart-scatter-plot.svg\",\"orderNum\":6}]', 12, '192.168.0.136', '2025-09-03 15:12:19');
INSERT INTO `sys_log` VALUES (102, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":220,\"parentId\":0,\"name\":\"报名列表\",\"url\":\"classSection\",\"type\":1,\"icon\":\"svg:laptop-check.svg\",\"orderNum\":5}]', 11, '192.168.0.136', '2025-09-03 15:13:56');
INSERT INTO `sys_log` VALUES (103, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":194,\"parentId\":0,\"name\":\"出境游配置\",\"url\":\"casting\",\"type\":1,\"icon\":\"svg:database-plus.svg\",\"orderNum\":5}]', 5, '192.168.0.136', '2025-09-03 15:16:06');
INSERT INTO `sys_log` VALUES (104, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":128,\"parentId\":0,\"name\":\"会员管理\",\"url\":\"\",\"perms\":\"\",\"type\":0,\"icon\":\"svg:nail.svg\",\"orderNum\":7}]', 12, '192.168.0.136', '2025-09-03 15:16:23');
INSERT INTO `sys_log` VALUES (105, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":225,\"parentId\":0,\"name\":\"其他配置\",\"type\":0,\"icon\":\"svg:chart-relation.svg\",\"orderNum\":6}]', 12, '192.168.0.136', '2025-09-03 15:16:40');
INSERT INTO `sys_log` VALUES (106, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":168,\"parentId\":0,\"name\":\"订单中心\",\"url\":\"exchangeList\",\"type\":1,\"icon\":\"svg:gallery-view.svg\",\"orderNum\":4}]', 13, '192.168.0.136', '2025-09-03 15:16:59');
INSERT INTO `sys_log` VALUES (107, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":154,\"parentId\":0,\"name\":\"旅游商品管理\",\"url\":\"shopManage\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:paper-plane.svg\",\"orderNum\":3}]', 6, '192.168.0.136', '2025-09-03 15:20:04');
INSERT INTO `sys_log` VALUES (108, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":41,\"parentId\":0,\"name\":\"消息中心\",\"url\":\"message\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:commentdots-fill.svg\",\"orderNum\":1}]', 5, '192.168.0.136', '2025-09-03 15:20:15');
INSERT INTO `sys_log` VALUES (109, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":162,\"parentId\":0,\"name\":\"注销信息\",\"url\":\"messageZx\",\"type\":1,\"icon\":\"svg:commentlines-fill.svg\",\"orderNum\":1}]', 12, '192.168.0.136', '2025-09-03 15:20:23');
INSERT INTO `sys_log` VALUES (110, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":34,\"parentId\":0,\"name\":\"财务中心\",\"url\":\"financeList\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:picside-fill.svg\",\"orderNum\":1}]', 12, '192.168.0.136', '2025-09-03 15:20:42');
INSERT INTO `sys_log` VALUES (111, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":32,\"parentId\":0,\"name\":\"用户中心\",\"url\":\"userList\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:user-group-fill.svg\",\"orderNum\":1}]', 11, '192.168.0.136', '2025-09-03 15:20:51');
INSERT INTO `sys_log` VALUES (112, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":33,\"parentId\":0,\"name\":\"数据中心\",\"url\":\"home\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:all-fill.svg\",\"orderNum\":1}]', 12, '192.168.0.136', '2025-09-03 15:21:02');
INSERT INTO `sys_log` VALUES (113, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":33,\"parentId\":33,\"name\":\"查看\",\"url\":\"home\",\"perms\":\"home:list\",\"type\":2,\"icon\":\"svg:all-fill.svg\",\"orderNum\":1}]', 13, '192.168.0.136', '2025-09-03 15:21:54');
INSERT INTO `sys_log` VALUES (114, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":33,\"parentId\":0,\"name\":\"数据中心\",\"url\":\"home\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:all-fill.svg\",\"orderNum\":1}]', 10, '192.168.0.136', '2025-09-03 15:22:33');
INSERT INTO `sys_log` VALUES (115, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":240,\"parentId\":33,\"name\":\"查看\",\"perms\":\"home:list\",\"type\":2}]', 11, '192.168.0.136', '2025-09-03 15:22:54');
INSERT INTO `sys_log` VALUES (116, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":162,\"parentId\":0,\"name\":\"注销信息\",\"url\":\"messageZx\",\"type\":1,\"icon\":\"svg:commentlines-fill.svg\",\"orderNum\":2}]', 7, '192.168.0.136', '2025-09-03 15:23:04');
INSERT INTO `sys_log` VALUES (117, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":41,\"parentId\":0,\"name\":\"消息中心\",\"url\":\"message\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:commentdots-fill.svg\",\"orderNum\":2}]', 11, '192.168.0.136', '2025-09-03 15:23:09');
INSERT INTO `sys_log` VALUES (118, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":34,\"parentId\":0,\"name\":\"财务中心\",\"url\":\"financeList\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:picside-fill.svg\",\"orderNum\":2}]', 11, '192.168.0.136', '2025-09-03 15:23:15');
INSERT INTO `sys_log` VALUES (119, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":32,\"parentId\":0,\"name\":\"用户中心\",\"url\":\"userList\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:user-group-fill.svg\",\"orderNum\":2}]', 6, '192.168.0.136', '2025-09-03 15:23:20');
INSERT INTO `sys_log` VALUES (120, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":33,\"parentId\":0,\"name\":\"数据中心\",\"url\":\"home\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:all-fill.svg\",\"orderNum\":1}]', 1, '192.168.0.136', '2025-09-03 15:23:28');
INSERT INTO `sys_log` VALUES (121, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":33,\"parentId\":0,\"name\":\"数据中心\",\"url\":\"home\",\"perms\":\" \",\"type\":1,\"icon\":\"svg:all-fill.svg\",\"orderNum\":1}]', 4, '192.168.0.136', '2025-09-03 15:23:37');
INSERT INTO `sys_log` VALUES (122, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,155,158,157,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,220,221,222,223,224,77,78,92,93,149,165,166,167,226,227,228,229,230,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,204,225]}]', 236, '192.168.0.136', '2025-09-03 15:24:22');
INSERT INTO `sys_log` VALUES (123, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":204,\"parentId\":0,\"name\":\"自营商城\",\"type\":0,\"icon\":\"svg:chart-line.svg\",\"orderNum\":3}]', 7, '192.168.0.136', '2025-09-03 15:25:08');
INSERT INTO `sys_log` VALUES (124, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":226,\"parentId\":225,\"name\":\"积分领取记录\",\"url\":\"taskConfig\",\"type\":1,\"icon\":\"config\",\"orderNum\":1}]', 7, '192.168.0.136', '2025-09-03 20:15:29');
INSERT INTO `sys_log` VALUES (125, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":239,\"parentId\":0,\"name\":\"目的地管理\",\"url\":\"destination\",\"type\":1,\"icon\":\"svg:map-fill.svg\",\"orderNum\":4}]', 7, '192.168.0.136', '2025-09-04 13:31:33');
INSERT INTO `sys_log` VALUES (126, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":241,\"parentId\":239,\"name\":\"查看\",\"perms\":\"destination:list\",\"type\":2}]', 8, '192.168.0.136', '2025-09-04 13:31:53');
INSERT INTO `sys_log` VALUES (127, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":242,\"parentId\":239,\"name\":\"添加\",\"perms\":\"destination:add\",\"type\":2}]', 9, '192.168.0.136', '2025-09-04 13:32:11');
INSERT INTO `sys_log` VALUES (128, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":243,\"parentId\":239,\"name\":\"修改\",\"perms\":\"destination:update\",\"type\":2}]', 4, '192.168.0.136', '2025-09-04 13:32:28');
INSERT INTO `sys_log` VALUES (129, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":244,\"parentId\":239,\"name\":\"删除\",\"perms\":\"destination:delete\",\"type\":2}]', 11, '192.168.0.136', '2025-09-04 13:32:42');
INSERT INTO `sys_log` VALUES (130, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,155,158,157,204,111,112,113,114,115,127,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,239,241,242,243,244,72,73,74,75,76,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,220,221,222,223,224,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null]}]', 339, '192.168.0.136', '2025-09-04 13:32:57');
INSERT INTO `sys_log` VALUES (131, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":245,\"parentId\":0,\"name\":\"次数配置\",\"url\":\"frequency\",\"type\":1,\"icon\":\"svg:aligncenter-fill.svg\",\"orderNum\":5}]', 7, '192.168.0.136', '2025-09-04 14:18:31');
INSERT INTO `sys_log` VALUES (132, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":246,\"parentId\":245,\"name\":\"查看\",\"perms\":\"frequency:list\",\"type\":2}]', 11, '192.168.0.136', '2025-09-04 14:18:49');
INSERT INTO `sys_log` VALUES (133, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":247,\"parentId\":245,\"name\":\"添加\",\"perms\":\"frequency:add\",\"type\":2}]', 5, '192.168.0.136', '2025-09-04 14:19:07');
INSERT INTO `sys_log` VALUES (134, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":248,\"parentId\":245,\"name\":\"修改\",\"perms\":\"frequency:update\",\"type\":2}]', 12, '192.168.0.136', '2025-09-04 14:19:24');
INSERT INTO `sys_log` VALUES (135, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":249,\"parentId\":245,\"name\":\"删除\",\"perms\":\"frequency:delete\",\"type\":2}]', 12, '192.168.0.136', '2025-09-04 14:19:42');
INSERT INTO `sys_log` VALUES (136, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":250,\"parentId\":0,\"name\":\"配置管理\",\"type\":0,\"icon\":\"svg:collection-fill.svg\",\"orderNum\":5}]', 4, '192.168.0.136', '2025-09-04 14:20:28');
INSERT INTO `sys_log` VALUES (137, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":184,\"parentId\":250,\"name\":\"签到配置\",\"url\":\"collectionList\",\"type\":1,\"icon\":\"svg:cat-code.svg\",\"orderNum\":1}]', 16, '192.168.0.136', '2025-09-04 14:20:58');
INSERT INTO `sys_log` VALUES (138, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":189,\"parentId\":250,\"name\":\"任务配置\",\"url\":\"questionType\",\"type\":1,\"icon\":\"svg:collection-fill.svg\",\"orderNum\":2}]', 6, '192.168.0.136', '2025-09-04 14:21:13');
INSERT INTO `sys_log` VALUES (139, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":245,\"parentId\":250,\"name\":\"次数配置\",\"url\":\"frequency\",\"type\":1,\"icon\":\"svg:aligncenter-fill.svg\",\"orderNum\":3}]', 11, '192.168.0.136', '2025-09-04 14:21:25');
INSERT INTO `sys_log` VALUES (140, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":194,\"parentId\":250,\"name\":\"出境游配置\",\"url\":\"casting\",\"type\":1,\"icon\":\"svg:database-plus.svg\",\"orderNum\":4}]', 4, '192.168.0.136', '2025-09-04 14:21:55');
INSERT INTO `sys_log` VALUES (141, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,155,158,157,204,111,112,113,114,115,127,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,239,241,242,243,244,72,73,74,75,76,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,194,195,196,197,198,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null]}]', 247, '192.168.0.136', '2025-09-04 14:22:13');
INSERT INTO `sys_log` VALUES (142, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":251,\"parentId\":225,\"name\":\"签到记录\",\"url\":\"checkIn\",\"type\":1,\"icon\":\"svg:commentlines-fill.svg\",\"orderNum\":1}]', 15, '192.168.0.136', '2025-09-04 15:43:49');
INSERT INTO `sys_log` VALUES (143, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":252,\"parentId\":251,\"name\":\"查看\",\"perms\":\"checkIn:list\",\"type\":2}]', 5, '192.168.0.136', '2025-09-04 15:44:04');
INSERT INTO `sys_log` VALUES (144, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,155,158,157,204,111,112,113,114,115,127,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,239,241,242,243,244,72,73,74,75,76,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,194,195,196,197,198,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null]}]', 84, '192.168.0.136', '2025-09-04 15:44:19');
INSERT INTO `sys_log` VALUES (145, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":77,\"parentId\":0,\"name\":\"动态管理\",\"url\":\"shopManage\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:chart-scatter-plot.svg\",\"orderNum\":6}]', 6, '192.168.0.136', '2025-09-04 15:45:00');
INSERT INTO `sys_log` VALUES (146, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":78,\"parentId\":77,\"name\":\"列表\",\"url\":\"\",\"perms\":\"shopManage:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 12, '192.168.0.136', '2025-09-04 15:45:13');
INSERT INTO `sys_log` VALUES (147, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":92,\"parentId\":77,\"name\":\"通过\",\"url\":\"\",\"perms\":\"shopManage:tongguo\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 12, '192.168.0.136', '2025-09-04 15:45:20');
INSERT INTO `sys_log` VALUES (148, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":93,\"parentId\":77,\"name\":\"拒绝\",\"url\":\"\",\"perms\":\"shopManage:jujue\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 11, '192.168.0.136', '2025-09-04 15:45:25');
INSERT INTO `sys_log` VALUES (149, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":149,\"parentId\":77,\"name\":\"删除\",\"url\":\"\",\"perms\":\"shopManage:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 4, '192.168.0.136', '2025-09-04 15:45:31');
INSERT INTO `sys_log` VALUES (150, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":165,\"parentId\":77,\"name\":\"添加\",\"perms\":\"shopManage:add\",\"type\":2,\"orderNum\":0}]', 38, '192.168.0.136', '2025-09-04 15:49:22');
INSERT INTO `sys_log` VALUES (151, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":166,\"parentId\":77,\"name\":\"修改\",\"perms\":\"shopManage:update\",\"type\":2,\"orderNum\":0}]', 7, '192.168.0.136', '2025-09-04 15:49:29');
INSERT INTO `sys_log` VALUES (152, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":167,\"parentId\":77,\"name\":\"封禁\",\"perms\":\"shopManage:fengjin\",\"type\":2,\"orderNum\":0}]', 7, '192.168.0.136', '2025-09-04 15:49:34');
INSERT INTO `sys_log` VALUES (153, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":253,\"parentId\":225,\"name\":\"城市管理\",\"url\":\"citys\",\"type\":1,\"icon\":\"svg:paper-plane.svg\",\"orderNum\":2}]', 14, '192.168.0.136', '2025-09-04 16:05:18');
INSERT INTO `sys_log` VALUES (154, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":254,\"parentId\":253,\"name\":\"查看\",\"perms\":\"citys:list\",\"type\":2}]', 11, '192.168.0.136', '2025-09-04 16:05:32');
INSERT INTO `sys_log` VALUES (155, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":255,\"parentId\":253,\"name\":\"添加\",\"perms\":\"citys:add\",\"type\":2}]', 5, '192.168.0.136', '2025-09-04 16:05:51');
INSERT INTO `sys_log` VALUES (156, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":256,\"parentId\":253,\"name\":\"修改\",\"perms\":\"citys:update\",\"type\":2}]', 11, '192.168.0.136', '2025-09-04 16:06:10');
INSERT INTO `sys_log` VALUES (157, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":257,\"parentId\":253,\"name\":\"删除\",\"perms\":\"citys:delete\",\"type\":2}]', 5, '192.168.0.136', '2025-09-04 16:06:27');
INSERT INTO `sys_log` VALUES (158, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,155,158,157,204,111,112,113,114,115,127,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,239,241,242,243,244,72,73,74,75,76,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,194,195,196,197,198,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,254,255,256,257,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null]}]', 246, '192.168.0.136', '2025-09-04 16:20:22');
INSERT INTO `sys_log` VALUES (159, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":258,\"parentId\":225,\"name\":\"国家管理\",\"url\":\"country\",\"type\":1,\"icon\":\"svg:all.svg\",\"orderNum\":6}]', 11, '192.168.0.136', '2025-09-08 10:33:05');
INSERT INTO `sys_log` VALUES (160, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":259,\"parentId\":258,\"name\":\"查看\",\"perms\":\"country:list\",\"type\":2}]', 11, '192.168.0.136', '2025-09-08 10:33:20');
INSERT INTO `sys_log` VALUES (161, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":260,\"parentId\":258,\"name\":\"添加\",\"perms\":\"country:add\",\"type\":2}]', 9, '192.168.0.136', '2025-09-08 10:33:35');
INSERT INTO `sys_log` VALUES (162, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":261,\"parentId\":258,\"name\":\"修改\",\"perms\":\"country:update\",\"type\":2}]', 5, '192.168.0.136', '2025-09-08 10:33:51');
INSERT INTO `sys_log` VALUES (163, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":262,\"parentId\":258,\"name\":\"删除\",\"perms\":\"country:delete\",\"type\":2}]', 13, '192.168.0.136', '2025-09-08 10:34:06');
INSERT INTO `sys_log` VALUES (164, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,155,158,157,204,111,112,113,114,115,127,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,239,241,242,243,244,72,73,74,75,76,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,194,195,196,197,198,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,254,255,256,257,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null]}]', 123, '192.168.0.136', '2025-09-08 10:34:27');
INSERT INTO `sys_log` VALUES (165, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":154,\"parentId\":0,\"name\":\"旅游商品管理\",\"url\":\"travelProducts\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:paper-plane.svg\",\"orderNum\":3}]', 14, '192.168.0.136', '2025-09-08 13:27:45');
INSERT INTO `sys_log` VALUES (166, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":155,\"parentId\":154,\"name\":\"修改店铺信息\",\"url\":\"\",\"perms\":\"travelProducts:updata\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 12, '192.168.0.136', '2025-09-08 13:27:57');
INSERT INTO `sys_log` VALUES (167, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":155,\"parentId\":154,\"name\":\"修改\",\"url\":\"\",\"perms\":\"travelProducts:updata\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 6, '192.168.0.136', '2025-09-08 13:28:13');
INSERT INTO `sys_log` VALUES (168, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[157]', 20, '192.168.0.136', '2025-09-08 13:28:22');
INSERT INTO `sys_log` VALUES (169, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[158]', 18, '192.168.0.136', '2025-09-08 13:28:25');
INSERT INTO `sys_log` VALUES (170, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":263,\"parentId\":154,\"name\":\"查看\",\"perms\":\"travelProducts:list\",\"type\":2}]', 14, '192.168.0.136', '2025-09-08 13:28:43');
INSERT INTO `sys_log` VALUES (171, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":264,\"parentId\":154,\"name\":\"添加\",\"perms\":\"travelProducts:add\",\"type\":2}]', 10, '192.168.0.136', '2025-09-08 13:29:01');
INSERT INTO `sys_log` VALUES (172, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":265,\"parentId\":154,\"name\":\"删除\",\"perms\":\"travelProducts:delete\",\"type\":2}]', 12, '192.168.0.136', '2025-09-08 13:29:17');
INSERT INTO `sys_log` VALUES (173, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,168,169,170,239,241,242,243,244,72,73,74,75,76,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,194,195,196,197,198,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,254,255,256,257,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null]}]', 84, '192.168.0.136', '2025-09-08 13:48:10');
INSERT INTO `sys_log` VALUES (174, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":210,\"parentId\":204,\"name\":\"商品列表\",\"url\":\"shopAdmin\",\"type\":1,\"icon\":\"peizhilb\",\"orderNum\":1}]', 9, '192.168.0.136', '2025-09-09 13:58:51');
INSERT INTO `sys_log` VALUES (175, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":205,\"parentId\":204,\"name\":\"商品分类\",\"url\":\"classifyAdmin\",\"type\":1,\"icon\":\"fenleilist\",\"orderNum\":2}]', 5, '192.168.0.136', '2025-09-09 13:58:59');
INSERT INTO `sys_log` VALUES (176, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":266,\"parentId\":204,\"name\":\"商品规格\",\"url\":\"specification\",\"type\":1,\"icon\":\"svg:all.svg\",\"orderNum\":3}]', 7, '192.168.0.136', '2025-09-09 14:00:16');
INSERT INTO `sys_log` VALUES (177, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":267,\"parentId\":266,\"name\":\"查看\",\"perms\":\"specification:list\",\"type\":2}]', 6, '192.168.0.136', '2025-09-09 14:00:31');
INSERT INTO `sys_log` VALUES (178, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":268,\"parentId\":266,\"name\":\"添加\",\"perms\":\"specification:add\",\"type\":2}]', 12, '192.168.0.136', '2025-09-09 14:01:55');
INSERT INTO `sys_log` VALUES (179, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":269,\"parentId\":266,\"name\":\"修改\",\"perms\":\"specification:update\",\"type\":2}]', 11, '192.168.0.136', '2025-09-09 14:02:09');
INSERT INTO `sys_log` VALUES (180, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":270,\"parentId\":266,\"name\":\"删除\",\"perms\":\"specification:delete\",\"type\":2}]', 12, '192.168.0.136', '2025-09-09 14:02:22');
INSERT INTO `sys_log` VALUES (181, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":271,\"parentId\":204,\"name\":\"商城首页装修\",\"url\":\"shopConfig\",\"type\":1,\"icon\":\"svg:chart-relation.svg\",\"orderNum\":4}]', 13, '192.168.0.136', '2025-09-09 14:03:01');
INSERT INTO `sys_log` VALUES (182, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":272,\"parentId\":271,\"name\":\"查看\",\"perms\":\"shopConfig:list\",\"type\":2}]', 12, '192.168.0.136', '2025-09-09 14:03:15');
INSERT INTO `sys_log` VALUES (183, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":273,\"parentId\":271,\"name\":\"添加\",\"perms\":\"shopConfig:add\",\"type\":2}]', 11, '192.168.0.136', '2025-09-09 14:03:28');
INSERT INTO `sys_log` VALUES (184, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":274,\"parentId\":271,\"name\":\"修改\",\"perms\":\"shopConfig:update\",\"type\":2}]', 12, '192.168.0.136', '2025-09-09 14:03:41');
INSERT INTO `sys_log` VALUES (185, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":275,\"parentId\":271,\"name\":\"删除\",\"perms\":\"shopConfig:delete\",\"type\":2}]', 12, '192.168.0.136', '2025-09-09 14:03:55');
INSERT INTO `sys_log` VALUES (186, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":276,\"parentId\":204,\"name\":\"自营订单\",\"url\":\"orderAdmin\",\"type\":1,\"icon\":\"svg:picside-fill.svg\",\"orderNum\":5}]', 12, '192.168.0.136', '2025-09-09 14:04:55');
INSERT INTO `sys_log` VALUES (187, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":277,\"parentId\":276,\"name\":\"查看\",\"perms\":\"orderAdmin:list\",\"type\":2}]', 6, '192.168.0.136', '2025-09-09 14:05:33');
INSERT INTO `sys_log` VALUES (188, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":278,\"parentId\":276,\"name\":\"退款\",\"perms\":\"orderAdmin:refund\",\"type\":2}]', 11, '192.168.0.136', '2025-09-09 14:05:52');
INSERT INTO `sys_log` VALUES (189, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":279,\"parentId\":276,\"name\":\"发货\",\"perms\":\"orderAdmin:deliver\",\"type\":2}]', 12, '192.168.0.136', '2025-09-09 14:06:07');
INSERT INTO `sys_log` VALUES (190, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":280,\"parentId\":204,\"name\":\"待处理订单\",\"url\":\"disposeOrder\",\"type\":1,\"icon\":\"svg:database-plus.svg\",\"orderNum\":6}]', 4, '192.168.0.136', '2025-09-09 15:18:38');
INSERT INTO `sys_log` VALUES (191, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":281,\"parentId\":280,\"name\":\"查看\",\"perms\":\"disposeOrder:list\",\"type\":2}]', 5, '192.168.0.136', '2025-09-09 15:18:51');
INSERT INTO `sys_log` VALUES (192, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,215,216,217,218,219,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,194,195,196,197,198,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,254,255,256,257,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null]}]', 317, '192.168.0.136', '2025-09-09 15:19:10');
INSERT INTO `sys_log` VALUES (193, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,215,216,217,218,219,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,194,195,196,197,198,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,254,255,256,257,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,204]}]', 536, '192.168.0.136', '2025-09-10 20:43:13');
INSERT INTO `sys_log` VALUES (194, 'nyy', '保存用户', 'com.sqx.system.controller.SysUserController.save()', '[{\"userId\":14,\"username\":\"test\",\"password\":\"7f173fbd8017da225f096dab7888fc3fc08bf79072ca8cac066b3239553d070f\",\"salt\":\"vMFEOj5b8vKXfmpNDtFM\",\"email\":\"test@123.com\",\"mobile\":\"12312341234\",\"status\":1,\"roleIdList\":[1],\"createUserId\":2,\"createTime\":\"Sep 23, 2025 10:47:19 AM\"}]', 228, '192.168.0.123', '2025-09-23 10:47:20');
INSERT INTO `sys_log` VALUES (195, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,215,216,217,218,219,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,194,195,196,197,198,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,254,255,256,257,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null]}]', 262, '192.168.0.136', '2025-09-30 09:21:52');
INSERT INTO `sys_log` VALUES (196, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":168,\"parentId\":0,\"name\":\"订单中心\",\"url\":\"mission\",\"type\":1,\"icon\":\"svg:gallery-view.svg\",\"orderNum\":4}]', 13, '192.168.0.136', '2025-09-30 09:54:17');
INSERT INTO `sys_log` VALUES (197, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":169,\"parentId\":168,\"name\":\"查看\",\"perms\":\"mission:list\",\"type\":2,\"orderNum\":0}]', 4, '192.168.0.136', '2025-09-30 09:54:26');
INSERT INTO `sys_log` VALUES (198, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":170,\"parentId\":168,\"name\":\"退款\",\"perms\":\"mission:tuikuan\",\"type\":2,\"orderNum\":0}]', 4, '192.168.0.136', '2025-09-30 09:54:31');
INSERT INTO `sys_log` VALUES (199, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":215,\"parentId\":0,\"name\":\"评论管理\",\"url\":\"commentList\",\"type\":1,\"icon\":\"pinglun\",\"orderNum\":5}]', 11, '192.168.0.136', '2025-09-30 10:09:36');
INSERT INTO `sys_log` VALUES (200, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":216,\"parentId\":215,\"name\":\"查看\",\"perms\":\"commentList:list\",\"type\":2,\"orderNum\":0}]', 6, '192.168.0.136', '2025-09-30 10:09:50');
INSERT INTO `sys_log` VALUES (201, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":219,\"parentId\":215,\"name\":\"删除\",\"perms\":\"commentList:delete\",\"type\":2,\"orderNum\":0}]', 11, '192.168.0.136', '2025-09-30 10:09:55');
INSERT INTO `sys_log` VALUES (202, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":215,\"parentId\":0,\"name\":\"评论管理\",\"url\":\"commentList\",\"type\":1,\"icon\":\"svg:laptop-check.svg\",\"orderNum\":5}]', 11, '192.168.0.136', '2025-09-30 10:10:34');
INSERT INTO `sys_log` VALUES (203, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,254,255,256,257,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250]}]', 71, '192.168.0.136', '2025-09-30 10:20:23');
INSERT INTO `sys_log` VALUES (204, 'nyy', '保存用户', 'com.sqx.system.controller.SysUserController.save()', '[{\"userId\":15,\"username\":\"hst\",\"password\":\"ead2fcc143b5f0dec242abb2587cb0ff13d9c947494a514b3fef6c700bd5de25\",\"salt\":\"IXh5Yc5r50eowGsKgJQE\",\"email\":\"hstg@163.com\",\"mobile\":\"18899999999\",\"status\":1,\"roleIdList\":[1],\"createUserId\":2,\"createTime\":\"Nov 10, 2025 2:27:24 PM\"}]', 316, '192.168.0.131', '2025-11-10 14:27:24');
INSERT INTO `sys_log` VALUES (205, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":105,\"parentId\":32,\"name\":\"修改钱包\",\"url\":\"\",\"perms\":\"userList:updateMoney\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 8, '192.168.0.136', '2025-11-18 13:16:27');
INSERT INTO `sys_log` VALUES (206, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,194,195,196,197,198,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,254,255,256,257,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,126]}]', 96, '192.168.0.136', '2025-11-18 13:29:59');
INSERT INTO `sys_log` VALUES (207, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,254,255,256,257,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250,126]}]', 72, '192.168.0.136', '2025-11-18 13:45:33');
INSERT INTO `sys_log` VALUES (208, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":4,\"roleName\":\"普通用户\",\"remark\":\"普通用户\",\"createUserId\":2,\"menuIdList\":[33,240,60,90,91,105,161,35,36,37,38,39,173,41,42,43,69,70,71,51,162,163,164,154,263,264,265,155,168,169,170,73,74,75,77,78,92,93,149,165,166,167,171,172,144,145,146,147,148,117,118,119,58,15,19,23,null,32,34,50,72,225,128,116,57,126,2,3,4]}]', 32, '192.168.0.136', '2025-11-18 13:52:59');
INSERT INTO `sys_log` VALUES (209, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":4,\"roleName\":\"普通用户\",\"remark\":\"普通用户\",\"createUserId\":2,\"menuIdList\":[33,240,60,35,42,50,51,52,53,54,162,163,164,154,263,264,265,155,168,169,170,72,73,74,75,76,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,254,255,256,257,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,32,34,41]}]', 34, '192.168.0.136', '2025-11-18 13:53:27');
INSERT INTO `sys_log` VALUES (210, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,194,195,196,197,198,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,254,255,256,257,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null]}]', 176, '192.168.0.136', '2025-11-26 15:29:31');
INSERT INTO `sys_log` VALUES (211, 'nyy', '保存用户', 'com.sqx.system.controller.SysUserController.save()', '[{\"userId\":16,\"username\":\"cm\",\"password\":\"5d573fdbd872427d64dfcc6b36cb66d361e25e01c0b7c18b9e7a573d2bbd2401\",\"salt\":\"CmVYXSOCVuv3g5ul2Pxu\",\"email\":\"123@123.com\",\"mobile\":\"12312341234\",\"status\":1,\"roleIdList\":[1],\"createUserId\":2,\"createTime\":\"Nov 26, 2025 4:14:09 PM\"}]', 134, '192.168.0.124', '2025-11-26 16:14:09');
INSERT INTO `sys_log` VALUES (212, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,194,195,196,197,198,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,254,255,256,257,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null]}]', 80, '192.168.0.136', '2025-12-08 15:37:13');
INSERT INTO `sys_log` VALUES (213, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250]}]', 18296, '127.0.0.1', '2025-12-16 13:21:11');
INSERT INTO `sys_log` VALUES (214, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250]}]', 20016, '127.0.0.1', '2025-12-16 13:21:28');
INSERT INTO `sys_log` VALUES (215, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250]}]', 18483, '127.0.0.1', '2025-12-16 13:25:48');
INSERT INTO `sys_log` VALUES (216, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250]}]', 16657, '127.0.0.1', '2025-12-16 13:27:06');
INSERT INTO `sys_log` VALUES (217, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250]}]', 16117, '192.168.0.136', '2025-12-16 13:44:09');
INSERT INTO `sys_log` VALUES (218, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250]}]', 14528, '192.168.0.136', '2025-12-16 13:45:03');
INSERT INTO `sys_log` VALUES (219, 'cm', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":4,\"roleName\":\"普通用户\",\"remark\":\"普通用户\",\"createUserId\":16,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,168,169,170,241,72,73,74,75,76,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,239]}]', 9090, '192.168.0.124', '2025-12-16 13:48:33');
INSERT INTO `sys_log` VALUES (220, 'cm', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":4,\"roleName\":\"普通用户\",\"remark\":\"普通用户\",\"createUserId\":16,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,204]}]', 11989, '192.168.0.124', '2025-12-16 13:50:06');
INSERT INTO `sys_log` VALUES (221, 'cm', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":4,\"roleName\":\"普通用户\",\"remark\":\"普通用户\",\"createUserId\":16,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,151,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,150]}]', 14502, '192.168.0.124', '2025-12-16 13:52:13');
INSERT INTO `sys_log` VALUES (222, 'cm', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":4,\"roleName\":\"普通用户\",\"remark\":\"普通用户\",\"createUserId\":16,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,216,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,215]}]', 6374, '192.168.0.124', '2025-12-16 13:58:23');
INSERT INTO `sys_log` VALUES (223, 'cm', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":4,\"roleName\":\"普通用户\",\"remark\":\"普通用户\",\"createUserId\":16,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,246,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250,245]}]', 7989, '192.168.0.124', '2025-12-16 13:59:06');
INSERT INTO `sys_log` VALUES (224, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250]}]', 8003, '192.168.0.136', '2025-12-16 14:00:01');
INSERT INTO `sys_log` VALUES (225, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250]}]', 7555, '192.168.0.136', '2025-12-16 14:00:51');
INSERT INTO `sys_log` VALUES (226, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,179,180,181,182,183,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250]}]', 6579, '192.168.0.136', '2025-12-16 14:03:22');
INSERT INTO `sys_log` VALUES (227, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,250,126]}]', 8106, '192.168.0.136', '2025-12-16 14:23:09');
INSERT INTO `sys_log` VALUES (228, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[195]', 185, '192.168.0.136', '2025-12-16 14:32:44');
INSERT INTO `sys_log` VALUES (229, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[196]', 214, '192.168.0.136', '2025-12-16 14:32:49');
INSERT INTO `sys_log` VALUES (230, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[197]', 166, '192.168.0.136', '2025-12-16 14:32:53');
INSERT INTO `sys_log` VALUES (231, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[198]', 167, '192.168.0.136', '2025-12-16 14:32:56');
INSERT INTO `sys_log` VALUES (232, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[194]', 169, '192.168.0.136', '2025-12-16 14:33:00');
INSERT INTO `sys_log` VALUES (233, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[180]', 171, '192.168.0.136', '2025-12-16 14:33:07');
INSERT INTO `sys_log` VALUES (234, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[181]', 168, '192.168.0.136', '2025-12-16 14:33:11');
INSERT INTO `sys_log` VALUES (235, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[182]', 166, '192.168.0.136', '2025-12-16 14:33:15');
INSERT INTO `sys_log` VALUES (236, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[183]', 171, '192.168.0.136', '2025-12-16 14:33:19');
INSERT INTO `sys_log` VALUES (237, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[179]', 168, '192.168.0.136', '2025-12-16 14:33:23');
INSERT INTO `sys_log` VALUES (238, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":4,\"roleName\":\"普通用户\",\"remark\":\"普通用户\",\"createUserId\":2,\"menuIdList\":[33,240,60,61,90,91,105,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,32]}]', 6214, '192.168.0.136', '2025-12-16 14:33:51');
INSERT INTO `sys_log` VALUES (239, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":4,\"roleName\":\"普通用户\",\"remark\":\"普通用户\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,111,112,113,114,115,127,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,225,171,172,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null]}]', 6163, '192.168.0.136', '2025-12-16 14:34:15');
INSERT INTO `sys_log` VALUES (240, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[112]', 414, '127.0.0.1', '2025-12-18 13:20:47');
INSERT INTO `sys_log` VALUES (241, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[113]', 164, '127.0.0.1', '2025-12-18 13:20:51');
INSERT INTO `sys_log` VALUES (242, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[114]', 403, '127.0.0.1', '2025-12-18 13:20:55');
INSERT INTO `sys_log` VALUES (243, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[115]', 165, '127.0.0.1', '2025-12-18 13:20:58');
INSERT INTO `sys_log` VALUES (244, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[210]', 33, '127.0.0.1', '2025-12-18 13:21:03');
INSERT INTO `sys_log` VALUES (245, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[127]', 641, '127.0.0.1', '2025-12-18 13:21:09');
INSERT INTO `sys_log` VALUES (246, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[111]', 166, '127.0.0.1', '2025-12-18 13:21:14');
INSERT INTO `sys_log` VALUES (247, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":210,\"parentId\":204,\"name\":\"商品列表\",\"url\":\"shopAdmin\",\"type\":1,\"icon\":\"svg:database-plus.svg\",\"orderNum\":1}]', 99, '127.0.0.1', '2025-12-18 13:25:52');
INSERT INTO `sys_log` VALUES (248, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":205,\"parentId\":204,\"name\":\"商品分类\",\"url\":\"classifyAdmin\",\"type\":1,\"icon\":\"svg:alignright-fill.svg\",\"orderNum\":2}]', 98, '127.0.0.1', '2025-12-18 13:26:01');
INSERT INTO `sys_log` VALUES (249, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":226,\"parentId\":225,\"name\":\"积分领取记录\",\"url\":\"taskConfig\",\"type\":1,\"icon\":\"svg:borderverticle-fill.svg\",\"orderNum\":1}]', 109, '127.0.0.1', '2025-12-18 13:26:29');
INSERT INTO `sys_log` VALUES (250, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":144,\"parentId\":225,\"name\":\"帮助中心\",\"url\":\"materialsList\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:paper-plane.svg\",\"orderNum\":8}]', 99, '127.0.0.1', '2025-12-18 13:26:42');
INSERT INTO `sys_log` VALUES (251, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":116,\"parentId\":128,\"name\":\"会员配置\",\"url\":\"coupon\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:database.svg\",\"orderNum\":1}]', 207, '127.0.0.1', '2025-12-18 13:27:00');
INSERT INTO `sys_log` VALUES (252, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":231,\"parentId\":128,\"name\":\"优惠券管理\",\"url\":\"couponYhq\",\"type\":1,\"icon\":\"svg:all.svg\",\"orderNum\":1}]', 98, '127.0.0.1', '2025-12-18 13:27:10');
INSERT INTO `sys_log` VALUES (253, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":174,\"parentId\":128,\"name\":\"会员特权\",\"url\":\"vipPrivilege\",\"type\":1,\"icon\":\"svg:laptop.svg\",\"orderNum\":1}]', 99, '127.0.0.1', '2025-12-18 13:27:20');
INSERT INTO `sys_log` VALUES (254, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":236,\"parentId\":128,\"name\":\"优惠券领取记录\",\"url\":\"couponuser\",\"type\":1,\"icon\":\"svg:chart-bar.svg\",\"orderNum\":4}]', 100, '127.0.0.1', '2025-12-18 13:27:38');
INSERT INTO `sys_log` VALUES (255, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,null,225]}]', 6341, '127.0.0.1', '2025-12-18 13:28:28');
INSERT INTO `sys_log` VALUES (256, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,77,78,92,93,149,165,166,167,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666,225]}]', 8084, '127.0.0.1', '2025-12-18 13:51:31');
INSERT INTO `sys_log` VALUES (257, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":282,\"parentId\":250,\"name\":\"违约配置\",\"url\":\"defaultType\",\"type\":1,\"icon\":\"svg:chart-relation.svg\",\"orderNum\":4}]', 110, '192.168.0.136', '2025-12-19 13:48:03');
INSERT INTO `sys_log` VALUES (258, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":283,\"parentId\":282,\"name\":\"查看\",\"perms\":\"defaultType:list\",\"type\":2}]', 103, '192.168.0.136', '2025-12-19 13:48:20');
INSERT INTO `sys_log` VALUES (259, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":284,\"parentId\":282,\"name\":\"添加\",\"perms\":\"defaultType:add\",\"type\":2}]', 105, '192.168.0.136', '2025-12-19 13:48:37');
INSERT INTO `sys_log` VALUES (260, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":285,\"parentId\":282,\"name\":\"修改\",\"perms\":\"defaultType:update\",\"type\":2}]', 108, '192.168.0.136', '2025-12-19 13:48:52');
INSERT INTO `sys_log` VALUES (261, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":286,\"parentId\":282,\"name\":\"删除\",\"perms\":\"defaultType:delete\",\"type\":2}]', 104, '192.168.0.136', '2025-12-19 13:49:07');
INSERT INTO `sys_log` VALUES (262, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,250,184,185,186,187,188,189,190,191,192,193,245,246,247,248,249,282,283,284,285,286,77,78,92,93,149,165,166,167,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666,225]}]', 6398, '192.168.0.136', '2025-12-19 13:49:34');
INSERT INTO `sys_log` VALUES (263, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,263,264,265,155,204,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,282,283,284,285,286,77,78,92,93,149,165,166,167,226,227,228,229,230,251,252,253,256,257,254,255,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,150,151,152,159,160,57,58,59,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666,250,225]}]', 6565, '0:0:0:0:0:0:0:1', '2025-12-22 17:19:35');
INSERT INTO `sys_log` VALUES (264, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":150,\"parentId\":0,\"name\":\"企业申请\",\"url\":\"authentication\",\"perms\":\"\",\"type\":1,\"icon\":\"svg:flag.svg\",\"orderNum\":8}]', 75, '192.168.0.136', '2025-12-24 19:46:32');
INSERT INTO `sys_log` VALUES (265, 'zww', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":10,\"menuIdList\":[33,240,32,60,61,90,91,105,161,34,35,36,37,38,39,40,173,41,42,43,69,70,71,50,51,52,53,54,162,163,164,154,155,263,264,265,204,210,211,212,213,214,205,206,207,208,209,266,267,268,269,270,271,272,273,274,275,276,277,278,279,280,281,168,169,170,239,241,242,243,244,72,73,74,75,76,215,216,217,218,219,220,221,222,223,224,184,185,186,187,188,189,190,191,192,193,282,283,284,285,286,77,78,92,93,149,165,166,167,226,227,228,229,230,251,252,253,254,255,256,257,258,259,260,261,262,144,145,146,147,148,128,116,117,118,119,120,174,175,176,177,178,231,232,233,234,235,238,236,237,287,288,150,151,152,159,160,57,58,59,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,-666666,250,225]}]', 1080, '192.168.0.116', '2026-06-05 17:28:38');
INSERT INTO `sys_log` VALUES (266, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[150]', 35, '192.168.0.136', '2026-07-13 09:28:25');
INSERT INTO `sys_log` VALUES (267, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[151]', 175, '192.168.0.136', '2026-07-13 09:28:31');
INSERT INTO `sys_log` VALUES (268, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[152]', 179, '192.168.0.136', '2026-07-13 09:28:37');
INSERT INTO `sys_log` VALUES (269, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[159]', 177, '192.168.0.136', '2026-07-13 09:28:40');
INSERT INTO `sys_log` VALUES (270, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[160]', 274, '192.168.0.136', '2026-07-13 09:28:45');
INSERT INTO `sys_log` VALUES (271, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[150]', 208, '192.168.0.136', '2026-07-13 09:28:48');
INSERT INTO `sys_log` VALUES (272, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[155]', 181, '192.168.0.136', '2026-07-13 09:29:01');
INSERT INTO `sys_log` VALUES (273, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[263]', 174, '192.168.0.136', '2026-07-13 09:29:04');
INSERT INTO `sys_log` VALUES (274, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[264]', 203, '192.168.0.136', '2026-07-13 09:29:06');
INSERT INTO `sys_log` VALUES (275, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[265]', 187, '192.168.0.136', '2026-07-13 09:29:08');
INSERT INTO `sys_log` VALUES (276, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[154]', 197, '192.168.0.136', '2026-07-13 09:29:12');
INSERT INTO `sys_log` VALUES (277, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[211]', 213, '192.168.0.136', '2026-07-13 09:29:48');
INSERT INTO `sys_log` VALUES (278, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[212]', 191, '192.168.0.136', '2026-07-13 09:29:51');
INSERT INTO `sys_log` VALUES (279, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[213]', 187, '192.168.0.136', '2026-07-13 09:29:54');
INSERT INTO `sys_log` VALUES (280, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[214]', 418, '192.168.0.136', '2026-07-13 09:29:56');
INSERT INTO `sys_log` VALUES (281, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[281]', 178, '192.168.0.136', '2026-07-13 09:30:04');
INSERT INTO `sys_log` VALUES (282, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[280]', 180, '192.168.0.136', '2026-07-13 09:30:07');
INSERT INTO `sys_log` VALUES (283, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[277]', 187, '192.168.0.136', '2026-07-13 09:30:11');
INSERT INTO `sys_log` VALUES (284, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[278]', 167, '192.168.0.136', '2026-07-13 09:30:13');
INSERT INTO `sys_log` VALUES (285, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[279]', 177, '192.168.0.136', '2026-07-13 09:30:15');
INSERT INTO `sys_log` VALUES (286, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[272]', 189, '192.168.0.136', '2026-07-13 09:30:21');
INSERT INTO `sys_log` VALUES (287, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[273]', 166, '192.168.0.136', '2026-07-13 09:30:23');
INSERT INTO `sys_log` VALUES (288, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[274]', 204, '192.168.0.136', '2026-07-13 09:30:25');
INSERT INTO `sys_log` VALUES (289, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[275]', 173, '192.168.0.136', '2026-07-13 09:30:27');
INSERT INTO `sys_log` VALUES (290, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[267]', 170, '192.168.0.136', '2026-07-13 09:30:33');
INSERT INTO `sys_log` VALUES (291, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[268]', 172, '192.168.0.136', '2026-07-13 09:30:35');
INSERT INTO `sys_log` VALUES (292, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[269]', 183, '192.168.0.136', '2026-07-13 09:30:38');
INSERT INTO `sys_log` VALUES (293, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[270]', 180, '192.168.0.136', '2026-07-13 09:30:40');
INSERT INTO `sys_log` VALUES (294, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[206]', 168, '192.168.0.136', '2026-07-13 09:30:45');
INSERT INTO `sys_log` VALUES (295, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[207]', 167, '192.168.0.136', '2026-07-13 09:30:47');
INSERT INTO `sys_log` VALUES (296, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[208]', 194, '192.168.0.136', '2026-07-13 09:30:50');
INSERT INTO `sys_log` VALUES (297, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[209]', 414, '192.168.0.136', '2026-07-13 09:30:52');
INSERT INTO `sys_log` VALUES (298, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[210]', 179, '192.168.0.136', '2026-07-13 09:30:56');
INSERT INTO `sys_log` VALUES (299, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[205]', 167, '192.168.0.136', '2026-07-13 09:30:58');
INSERT INTO `sys_log` VALUES (300, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[266]', 188, '192.168.0.136', '2026-07-13 09:31:00');
INSERT INTO `sys_log` VALUES (301, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[271]', 178, '192.168.0.136', '2026-07-13 09:31:03');
INSERT INTO `sys_log` VALUES (302, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[276]', 184, '192.168.0.136', '2026-07-13 09:31:05');
INSERT INTO `sys_log` VALUES (303, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[204]', 187, '192.168.0.136', '2026-07-13 09:31:10');
INSERT INTO `sys_log` VALUES (304, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[241]', 174, '192.168.0.136', '2026-07-13 09:31:22');
INSERT INTO `sys_log` VALUES (305, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[242]', 166, '192.168.0.136', '2026-07-13 09:31:24');
INSERT INTO `sys_log` VALUES (306, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[243]', 178, '192.168.0.136', '2026-07-13 09:31:26');
INSERT INTO `sys_log` VALUES (307, 'zww', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[244]', 220, '192.168.0.136', '2026-07-13 09:31:28');
INSERT INTO `sys_log` VALUES (308, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":311,\"parentId\":310,\"name\":\"用户中心\",\"url\":\"userList\",\"perms\":\"\",\"type\":1,\"icon\":\"user\",\"orderNum\":1}]', 116, '192.168.0.136', '2026-07-13 11:31:07');
INSERT INTO `sys_log` VALUES (309, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":375,\"parentId\":372,\"name\":\"查看\",\"url\":\"\",\"perms\":\"allocationList:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 122, '192.168.0.136', '2026-07-13 15:06:12');
INSERT INTO `sys_log` VALUES (310, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":376,\"parentId\":372,\"name\":\"修改\",\"url\":\"\",\"perms\":\"allocationList:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 114, '192.168.0.136', '2026-07-13 15:06:28');
INSERT INTO `sys_log` VALUES (311, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,310,311,320,321,322,323,324,325,326,330,331,332,333,340,341,342,343,344,345,346,350,351,352,353,354,355,356,357,358,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,372,375,376,373,374,-666666]}]', 4806, '192.168.0.136', '2026-07-13 15:06:50');
INSERT INTO `sys_log` VALUES (312, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,310,311,320,321,322,323,324,325,326,330,331,332,333,340,341,342,343,344,345,346,350,351,352,353,354,355,356,357,358,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,372,375,376,377,378,373,374,-666666]}]', 12108, '192.168.0.136', '2026-07-13 15:21:30');
INSERT INTO `sys_log` VALUES (313, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,310,311,320,321,322,323,324,325,326,330,331,332,333,340,341,342,343,344,345,346,350,351,352,353,354,355,356,357,358,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,372,375,376,377,378,373,374,-666666]}]', 6697, '192.168.0.136', '2026-07-13 15:21:48');
INSERT INTO `sys_log` VALUES (314, 'nyy', '修改配置', 'com.sqx.system.controller.SysConfigController.update()', '[{\"id\":72,\"paramKey\":\"appliance_hero_badge\",\"paramValue\":\"高价回收 · 绿色环保111\",\"status\":\"1\",\"remark\":\"家电回收顶部标签\",\"paramType\":\"default\",\"paramTypeDesc\":\"其他配置\"}]', 285, '192.168.0.136', '2026-07-13 15:22:10');
INSERT INTO `sys_log` VALUES (315, 'nyy', '修改配置', 'com.sqx.system.controller.SysConfigController.update()', '[{\"id\":72,\"paramKey\":\"appliance_hero_badge\",\"paramValue\":\"高价回收 · 绿色环保\",\"status\":\"1\",\"remark\":\"家电回收顶部标签\",\"paramType\":\"default\",\"paramTypeDesc\":\"其他配置\"}]', 208, '192.168.0.136', '2026-07-13 15:22:15');
INSERT INTO `sys_log` VALUES (316, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,310,311,312,320,321,327,328,322,323,324,325,326,330,331,332,334,333,380,340,341,342,343,344,345,346,350,351,352,353,355,356,362,360,361,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,372,375,376,377,378,379,373,374,-666666]}]', 6490, '192.168.0.136', '2026-07-16 09:44:47');
INSERT INTO `sys_log` VALUES (317, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":381,\"parentId\":300,\"name\":\"查看\",\"url\":\"\",\"perms\":\"home:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 148, '192.168.0.136', '2026-07-16 09:46:06');
INSERT INTO `sys_log` VALUES (318, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":382,\"parentId\":311,\"name\":\"查看\",\"url\":\"\",\"perms\":\"userList:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 139, '192.168.0.136', '2026-07-16 16:02:30');
INSERT INTO `sys_log` VALUES (319, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":383,\"parentId\":311,\"name\":\"修改信息\",\"url\":\"\",\"perms\":\"userList:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 141, '192.168.0.136', '2026-07-16 16:02:47');
INSERT INTO `sys_log` VALUES (320, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":384,\"parentId\":311,\"name\":\"修改状态\",\"url\":\"\",\"perms\":\"userList:updateStatus\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 138, '192.168.0.136', '2026-07-16 16:03:47');
INSERT INTO `sys_log` VALUES (321, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":385,\"parentId\":311,\"name\":\"修改金额\",\"url\":\"\",\"perms\":\"userList:updateJe\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 170, '192.168.0.136', '2026-07-16 16:05:59');
INSERT INTO `sys_log` VALUES (322, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":386,\"parentId\":311,\"name\":\"修改积分\",\"url\":\"\",\"perms\":\"userList:updateJf\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 150, '192.168.0.136', '2026-07-16 16:06:26');
INSERT INTO `sys_log` VALUES (323, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":387,\"parentId\":312,\"name\":\"查看\",\"url\":\"\",\"perms\":\"userAddress:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 146, '192.168.0.136', '2026-07-16 16:07:06');
INSERT INTO `sys_log` VALUES (324, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":388,\"parentId\":321,\"name\":\"查看\",\"url\":\"\",\"perms\":\"orderList:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 142, '192.168.0.136', '2026-07-16 16:09:18');
INSERT INTO `sys_log` VALUES (325, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":389,\"parentId\":321,\"name\":\"取消订单\",\"url\":\"\",\"perms\":\"orderList:cancel\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 147, '192.168.0.136', '2026-07-16 16:09:50');
INSERT INTO `sys_log` VALUES (326, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":390,\"parentId\":321,\"name\":\"分配员工\",\"url\":\"\",\"perms\":\"orderList:allocation\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 147, '192.168.0.136', '2026-07-16 16:11:12');
INSERT INTO `sys_log` VALUES (327, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":391,\"parentId\":327,\"name\":\"查看\",\"url\":\"\",\"perms\":\"orderReschedule:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 150, '192.168.0.136', '2026-07-16 16:12:14');
INSERT INTO `sys_log` VALUES (328, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":392,\"parentId\":328,\"name\":\"查看\",\"url\":\"\",\"perms\":\"orderException:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 164, '192.168.0.136', '2026-07-16 16:12:32');
INSERT INTO `sys_log` VALUES (329, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":393,\"parentId\":322,\"name\":\"查看\",\"url\":\"\",\"perms\":\"pickupTast:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 150, '192.168.0.136', '2026-07-16 16:15:01');
INSERT INTO `sys_log` VALUES (330, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":394,\"parentId\":322,\"name\":\"分配员工\",\"url\":\"\",\"perms\":\"pickupTask:allication\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 303, '192.168.0.136', '2026-07-16 16:15:48');
INSERT INTO `sys_log` VALUES (331, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":395,\"parentId\":322,\"name\":\"完成\",\"url\":\"\",\"perms\":\"pickupTask:complete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 190, '192.168.0.136', '2026-07-16 16:16:41');
INSERT INTO `sys_log` VALUES (332, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":396,\"parentId\":323,\"name\":\"查看\",\"url\":\"\",\"perms\":\"settlement:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 224, '192.168.0.136', '2026-07-16 16:18:49');
INSERT INTO `sys_log` VALUES (333, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":397,\"parentId\":324,\"name\":\"查看\",\"url\":\"\",\"perms\":\"payout:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 152, '192.168.0.136', '2026-07-16 16:19:12');
INSERT INTO `sys_log` VALUES (334, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":398,\"parentId\":325,\"name\":\"查看\",\"url\":\"\",\"perms\":\"dispute:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 163, '192.168.0.136', '2026-07-16 16:19:33');
INSERT INTO `sys_log` VALUES (335, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":399,\"parentId\":326,\"name\":\"查看\",\"url\":\"\",\"perms\":\"review:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 232, '192.168.0.136', '2026-07-16 16:19:51');
INSERT INTO `sys_log` VALUES (336, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":400,\"parentId\":331,\"name\":\"查看\",\"url\":\"\",\"perms\":\"recycleCategory:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 152, '192.168.0.136', '2026-07-16 16:20:18');
INSERT INTO `sys_log` VALUES (337, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":401,\"parentId\":331,\"name\":\"添加\",\"url\":\"\",\"perms\":\"recycleCategory:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 151, '192.168.0.136', '2026-07-16 16:20:38');
INSERT INTO `sys_log` VALUES (338, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":402,\"parentId\":331,\"name\":\"修改\",\"url\":\"\",\"perms\":\"recycleCategory:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 171, '192.168.0.136', '2026-07-16 16:20:57');
INSERT INTO `sys_log` VALUES (339, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":403,\"parentId\":331,\"name\":\"删除\",\"url\":\"\",\"perms\":\"recycleCategory:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 167, '192.168.0.136', '2026-07-16 16:21:10');
INSERT INTO `sys_log` VALUES (340, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":404,\"parentId\":332,\"name\":\"查看\",\"url\":\"\",\"perms\":\"serviceArea:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 155, '192.168.0.136', '2026-07-16 16:21:40');
INSERT INTO `sys_log` VALUES (341, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":405,\"parentId\":332,\"name\":\"添加\",\"url\":\"\",\"perms\":\"serviceArea:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 161, '192.168.0.136', '2026-07-16 16:21:55');
INSERT INTO `sys_log` VALUES (342, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":406,\"parentId\":332,\"name\":\"修改\",\"url\":\"\",\"perms\":\"serviceArea:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 164, '192.168.0.136', '2026-07-16 16:22:13');
INSERT INTO `sys_log` VALUES (343, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":407,\"parentId\":332,\"name\":\"删除\",\"url\":\"\",\"perms\":\"serviceArea:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 165, '192.168.0.136', '2026-07-16 16:22:26');
INSERT INTO `sys_log` VALUES (344, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":408,\"parentId\":334,\"name\":\"查看\",\"url\":\"\",\"perms\":\"productCategory:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 150, '192.168.0.136', '2026-07-16 16:22:49');
INSERT INTO `sys_log` VALUES (345, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":409,\"parentId\":334,\"name\":\"添加\",\"url\":\"\",\"perms\":\"productCategory:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 143, '192.168.0.136', '2026-07-16 16:23:13');
INSERT INTO `sys_log` VALUES (346, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":410,\"parentId\":334,\"name\":\"修改\",\"url\":\"\",\"perms\":\"productCategory:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 137, '192.168.0.136', '2026-07-16 16:23:27');
INSERT INTO `sys_log` VALUES (347, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":411,\"parentId\":334,\"name\":\"删除\",\"url\":\"\",\"perms\":\"productCategory:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 132, '192.168.0.136', '2026-07-16 16:23:40');
INSERT INTO `sys_log` VALUES (348, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":412,\"parentId\":333,\"name\":\"查看\",\"url\":\"\",\"perms\":\"recycleRule:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 149, '192.168.0.136', '2026-07-16 16:25:45');
INSERT INTO `sys_log` VALUES (349, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":413,\"parentId\":333,\"name\":\"添加\",\"url\":\"\",\"perms\":\"recycleRule:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 147, '192.168.0.136', '2026-07-16 16:25:59');
INSERT INTO `sys_log` VALUES (350, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":414,\"parentId\":333,\"name\":\"修改\",\"url\":\"\",\"perms\":\"recycleRule:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 162, '192.168.0.136', '2026-07-16 16:26:17');
INSERT INTO `sys_log` VALUES (351, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":415,\"parentId\":333,\"name\":\"删除\",\"url\":\"\",\"perms\":\"recycleRule:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 144, '192.168.0.136', '2026-07-16 16:26:33');
INSERT INTO `sys_log` VALUES (352, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":416,\"parentId\":380,\"name\":\"查看\",\"url\":\"\",\"perms\":\"bookWeightOption:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 170, '192.168.0.136', '2026-07-16 16:27:29');
INSERT INTO `sys_log` VALUES (353, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":417,\"parentId\":380,\"name\":\"添加\",\"url\":\"\",\"perms\":\"bookWeightOption:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 155, '192.168.0.136', '2026-07-16 16:27:43');
INSERT INTO `sys_log` VALUES (354, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":418,\"parentId\":380,\"name\":\"修改\",\"url\":\"\",\"perms\":\"bookWeightOption:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 159, '192.168.0.136', '2026-07-16 16:27:59');
INSERT INTO `sys_log` VALUES (355, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":300,\"parentId\":0,\"name\":\"数据中心\",\"url\":\"home\",\"perms\":\"admin:datacenter:view\",\"type\":1,\"icon\":\"shouye\",\"orderNum\":1}]', 120, '192.168.0.136', '2026-07-16 16:41:02');
INSERT INTO `sys_log` VALUES (356, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":310,\"parentId\":0,\"name\":\"用户管理\",\"url\":\"\",\"perms\":\"\",\"type\":0,\"icon\":\"yonghul\",\"orderNum\":2}]', 104, '192.168.0.136', '2026-07-16 16:41:25');
INSERT INTO `sys_log` VALUES (357, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":311,\"parentId\":310,\"name\":\"用户中心\",\"url\":\"userList\",\"perms\":\"\",\"type\":1,\"icon\":\"yonghul\",\"orderNum\":1}]', 163, '192.168.0.136', '2026-07-16 16:41:40');
INSERT INTO `sys_log` VALUES (358, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":312,\"parentId\":310,\"name\":\"用户地址\",\"url\":\"userAddress\",\"perms\":\"\",\"type\":1,\"icon\":\"dangdifill\",\"orderNum\":2}]', 159, '192.168.0.136', '2026-07-16 16:41:48');
INSERT INTO `sys_log` VALUES (359, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":321,\"parentId\":0,\"name\":\"回收订单\",\"url\":\"orderList\",\"perms\":\"\",\"type\":1,\"icon\":\"log\",\"orderNum\":3}]', 119, '192.168.0.136', '2026-07-16 16:42:42');
INSERT INTO `sys_log` VALUES (360, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":322,\"parentId\":0,\"name\":\"取件任务\",\"url\":\"pickupTask\",\"perms\":\"\",\"type\":1,\"icon\":\"renwu\",\"orderNum\":4}]', 107, '192.168.0.136', '2026-07-16 16:43:09');
INSERT INTO `sys_log` VALUES (361, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":330,\"parentId\":0,\"name\":\"回收配置\",\"url\":\"\",\"perms\":\"\",\"type\":0,\"icon\":\"shezhi\",\"orderNum\":4}]', 104, '192.168.0.136', '2026-07-16 16:44:33');
INSERT INTO `sys_log` VALUES (362, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":331,\"parentId\":330,\"name\":\"回收品类\",\"url\":\"recycleCategory\",\"perms\":\"\",\"type\":1,\"icon\":\"renwu1\",\"orderNum\":1}]', 153, '192.168.0.136', '2026-07-16 16:44:52');
INSERT INTO `sys_log` VALUES (363, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":332,\"parentId\":330,\"name\":\"服务区域\",\"url\":\"serviceArea\",\"perms\":\"\",\"type\":1,\"icon\":\"pingtai\",\"orderNum\":2}]', 169, '192.168.0.136', '2026-07-16 16:45:06');
INSERT INTO `sys_log` VALUES (364, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":334,\"parentId\":330,\"name\":\"商品分类\",\"url\":\"productCategory\",\"perms\":\"\",\"type\":1,\"icon\":\"fenleilist\",\"orderNum\":2}]', 164, '192.168.0.136', '2026-07-16 16:45:12');
INSERT INTO `sys_log` VALUES (365, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":333,\"parentId\":330,\"name\":\"回收规则\",\"url\":\"recycleRule\",\"perms\":\"\",\"type\":1,\"icon\":\"peizhi\",\"orderNum\":3}]', 188, '192.168.0.136', '2026-07-16 16:45:25');
INSERT INTO `sys_log` VALUES (366, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":340,\"parentId\":0,\"name\":\"财务中心\",\"url\":\"\",\"perms\":\"\",\"type\":0,\"icon\":\"caiwu\",\"orderNum\":5}]', 102, '192.168.0.136', '2026-07-16 16:45:39');
INSERT INTO `sys_log` VALUES (367, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":341,\"parentId\":340,\"name\":\"用户钱包\",\"url\":\"userWallet\",\"perms\":\"\",\"type\":1,\"icon\":\"caiwu\",\"orderNum\":1}]', 153, '192.168.0.136', '2026-07-16 16:45:51');
INSERT INTO `sys_log` VALUES (368, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":342,\"parentId\":340,\"name\":\"钱包流水\",\"url\":\"walletTransaction\",\"perms\":\"\",\"type\":1,\"icon\":\"jifen\",\"orderNum\":2}]', 172, '192.168.0.136', '2026-07-16 16:46:05');
INSERT INTO `sys_log` VALUES (369, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":343,\"parentId\":0,\"name\":\"提现记录\",\"url\":\"withdrawRecord\",\"perms\":\"\",\"type\":1,\"icon\":\"jifen\",\"orderNum\":2}]', 104, '192.168.0.136', '2026-07-16 16:47:00');
INSERT INTO `sys_log` VALUES (370, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":343,\"parentId\":0,\"name\":\"提现管理\",\"url\":\"withdrawRecord\",\"perms\":\"\",\"type\":1,\"icon\":\"jifen\",\"orderNum\":3}]', 93, '192.168.0.136', '2026-07-16 16:47:27');
INSERT INTO `sys_log` VALUES (371, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":344,\"parentId\":340,\"name\":\"提现账户\",\"url\":\"withdrawalAccount\",\"perms\":\"\",\"type\":1,\"icon\":\"order\",\"orderNum\":4}]', 179, '192.168.0.136', '2026-07-16 16:47:41');
INSERT INTO `sys_log` VALUES (372, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":345,\"parentId\":340,\"name\":\"分销佣金\",\"url\":\"referralCommission\",\"perms\":\"\",\"type\":1,\"icon\":\"jifen\",\"orderNum\":5}]', 154, '192.168.0.136', '2026-07-16 16:47:50');
INSERT INTO `sys_log` VALUES (373, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":346,\"parentId\":340,\"name\":\"用户佣金汇总\",\"url\":\"referralUserCommission\",\"perms\":\"\",\"type\":1,\"icon\":\"log\",\"orderNum\":6}]', 153, '192.168.0.136', '2026-07-16 16:47:58');
INSERT INTO `sys_log` VALUES (374, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":351,\"parentId\":0,\"name\":\"首页装修\",\"url\":\"bannerList\",\"perms\":\"\",\"type\":1,\"icon\":\"peizhilb\",\"orderNum\":5}]', 104, '192.168.0.136', '2026-07-16 16:48:40');
INSERT INTO `sys_log` VALUES (375, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":352,\"parentId\":0,\"name\":\"消息管理\",\"url\":\"message\",\"perms\":\"\",\"type\":1,\"icon\":\"pinglun\",\"orderNum\":6}]', 359, '192.168.0.136', '2026-07-16 16:49:04');
INSERT INTO `sys_log` VALUES (376, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":361,\"parentId\":0,\"name\":\"在线客服\",\"url\":\"serviceChat\",\"perms\":\"\",\"type\":1,\"icon\":\"tixing\",\"orderNum\":15}]', 119, '192.168.0.136', '2026-07-16 17:26:25');
INSERT INTO `sys_log` VALUES (377, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[360]', 240, '192.168.0.136', '2026-07-16 17:26:30');
INSERT INTO `sys_log` VALUES (378, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":362,\"parentId\":350,\"name\":\"用户福利\",\"url\":\"userWelfare\",\"perms\":\"\",\"type\":1,\"icon\":\"fenleilist\",\"orderNum\":6}]', 146, '192.168.0.136', '2026-07-16 17:26:49');
INSERT INTO `sys_log` VALUES (379, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":356,\"parentId\":350,\"name\":\"福利任务\",\"url\":\"welfareTask\",\"perms\":\"\",\"type\":1,\"icon\":\"renwu\",\"orderNum\":5}]', 139, '192.168.0.136', '2026-07-16 17:26:59');
INSERT INTO `sys_log` VALUES (380, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":356,\"parentId\":350,\"name\":\"福利任务\",\"url\":\"welfareTask\",\"perms\":\"\",\"type\":1,\"icon\":\"renwu\",\"orderNum\":5}]', 342, '192.168.0.136', '2026-07-16 17:27:15');
INSERT INTO `sys_log` VALUES (381, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":355,\"parentId\":350,\"name\":\"品牌合作\",\"url\":\"brandCooperation\",\"perms\":\"\",\"type\":1,\"icon\":\"peizhilb\",\"orderNum\":4}]', 137, '192.168.0.136', '2026-07-16 17:27:28');
INSERT INTO `sys_log` VALUES (382, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":353,\"parentId\":350,\"name\":\"帮助中心\",\"url\":\"helpCategory\",\"perms\":\"\",\"type\":1,\"icon\":\"renwu1\",\"orderNum\":3}]', 143, '192.168.0.136', '2026-07-16 17:27:42');
INSERT INTO `sys_log` VALUES (383, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":350,\"parentId\":0,\"name\":\"运营内容\",\"url\":\"\",\"perms\":\"\",\"type\":0,\"icon\":\"fenleilist\",\"orderNum\":6}]', 115, '192.168.0.136', '2026-07-16 17:27:54');
INSERT INTO `sys_log` VALUES (384, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":2,\"parentId\":126,\"name\":\"管理员列表\",\"url\":\"user\",\"perms\":\"\",\"type\":1,\"icon\":\"admin\",\"orderNum\":10}]', 150, '192.168.0.136', '2026-07-16 17:28:48');
INSERT INTO `sys_log` VALUES (385, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":3,\"parentId\":126,\"name\":\"角色管理\",\"url\":\"role\",\"perms\":\"\",\"type\":1,\"icon\":\"yonghu\",\"orderNum\":11}]', 141, '192.168.0.136', '2026-07-16 17:29:08');
INSERT INTO `sys_log` VALUES (386, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":4,\"parentId\":126,\"name\":\"菜单管理\",\"url\":\"menu\",\"perms\":\"\",\"type\":1,\"icon\":\"zhedie\",\"orderNum\":12}]', 148, '192.168.0.136', '2026-07-16 17:29:25');
INSERT INTO `sys_log` VALUES (387, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":370,\"parentId\":126,\"name\":\"字典管理\",\"url\":\"dict\",\"perms\":\"sys:dict:list\",\"type\":1,\"icon\":\"peizhi\",\"orderNum\":13}]', 797, '192.168.0.136', '2026-07-16 17:29:41');
INSERT INTO `sys_log` VALUES (388, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":371,\"parentId\":126,\"name\":\"权限管理\",\"url\":\"permission\",\"perms\":\"sys:menu:list\",\"type\":1,\"icon\":\"shuju\",\"orderNum\":14}]', 139, '192.168.0.136', '2026-07-16 17:29:55');
INSERT INTO `sys_log` VALUES (389, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":372,\"parentId\":0,\"name\":\"系统配置\",\"url\":\"allocationList\",\"perms\":\"sys:config:list\",\"type\":1,\"icon\":\"config\",\"orderNum\":19}]', 97, '192.168.0.136', '2026-07-16 17:30:08');
INSERT INTO `sys_log` VALUES (390, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":373,\"parentId\":126,\"name\":\"短信模板\",\"url\":\"smsTemplate\",\"perms\":\"\",\"type\":1,\"icon\":\"xinxi\",\"orderNum\":17}]', 144, '192.168.0.136', '2026-07-16 17:30:31');
INSERT INTO `sys_log` VALUES (391, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":419,\"parentId\":343,\"name\":\"查看\",\"url\":\"\",\"perms\":\"withdrawRecord:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 175, '192.168.0.136', '2026-07-16 17:31:04');
INSERT INTO `sys_log` VALUES (392, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":420,\"parentId\":343,\"name\":\"审核\",\"url\":\"\",\"perms\":\"withdrawRecord:shenhe\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 144, '192.168.0.136', '2026-07-16 17:31:28');
INSERT INTO `sys_log` VALUES (393, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":421,\"parentId\":341,\"name\":\"查看\",\"url\":\"\",\"perms\":\"userWallet:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 159, '192.168.0.136', '2026-07-16 17:33:50');
INSERT INTO `sys_log` VALUES (394, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":422,\"parentId\":341,\"name\":\"修改余额\",\"url\":\"\",\"perms\":\"userWallet:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 136, '192.168.0.136', '2026-07-16 17:34:19');
INSERT INTO `sys_log` VALUES (395, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":423,\"parentId\":342,\"name\":\"查看\",\"url\":\"\",\"perms\":\"walletTransaction:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 137, '192.168.0.136', '2026-07-16 17:35:17');
INSERT INTO `sys_log` VALUES (396, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":424,\"parentId\":344,\"name\":\"查看\",\"url\":\"\",\"perms\":\"withdrawalAccount:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 145, '192.168.0.136', '2026-07-16 17:35:39');
INSERT INTO `sys_log` VALUES (397, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":425,\"parentId\":345,\"name\":\"查看\",\"url\":\"\",\"perms\":\"referralCommission:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 139, '192.168.0.136', '2026-07-16 17:35:55');
INSERT INTO `sys_log` VALUES (398, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":426,\"parentId\":346,\"name\":\"查看\",\"url\":\"\",\"perms\":\"referralUserCommission:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 137, '192.168.0.136', '2026-07-16 17:36:13');
INSERT INTO `sys_log` VALUES (399, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":427,\"parentId\":351,\"name\":\"查看\",\"url\":\"\",\"perms\":\"bannerList:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 150, '192.168.0.136', '2026-07-16 17:36:29');
INSERT INTO `sys_log` VALUES (400, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":428,\"parentId\":351,\"name\":\"添加\",\"url\":\"\",\"perms\":\"bannerList:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 137, '192.168.0.136', '2026-07-16 17:36:45');
INSERT INTO `sys_log` VALUES (401, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":429,\"parentId\":351,\"name\":\"修改\",\"url\":\"\",\"perms\":\"bannerList:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 168, '192.168.0.136', '2026-07-16 17:37:01');
INSERT INTO `sys_log` VALUES (402, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":430,\"parentId\":351,\"name\":\"删除\",\"url\":\"\",\"perms\":\"bannerList:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 137, '192.168.0.136', '2026-07-16 17:37:14');
INSERT INTO `sys_log` VALUES (403, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":431,\"parentId\":353,\"name\":\"查看\",\"url\":\"\",\"perms\":\"helpCategory:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 152, '192.168.0.136', '2026-07-16 17:37:37');
INSERT INTO `sys_log` VALUES (404, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":432,\"parentId\":353,\"name\":\"添加\",\"url\":\"\",\"perms\":\"helpCategory:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 147, '192.168.0.136', '2026-07-16 17:37:50');
INSERT INTO `sys_log` VALUES (405, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":433,\"parentId\":353,\"name\":\"修改\",\"url\":\"\",\"perms\":\"helpCategory:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 140, '192.168.0.136', '2026-07-16 17:38:05');
INSERT INTO `sys_log` VALUES (406, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":434,\"parentId\":353,\"name\":\"删除\",\"url\":\"\",\"perms\":\"helpCategory:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 154, '192.168.0.136', '2026-07-16 17:38:20');
INSERT INTO `sys_log` VALUES (407, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":435,\"parentId\":355,\"name\":\"查看\",\"url\":\"\",\"perms\":\"brandCooperation:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 158, '192.168.0.136', '2026-07-16 17:39:48');
INSERT INTO `sys_log` VALUES (408, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":436,\"parentId\":355,\"name\":\"跟进\",\"url\":\"\",\"perms\":\"brandCooperation:shenhe\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 144, '192.168.0.136', '2026-07-16 17:40:09');
INSERT INTO `sys_log` VALUES (409, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":437,\"parentId\":356,\"name\":\"查看\",\"url\":\"\",\"perms\":\"welfareTask:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 140, '192.168.0.136', '2026-07-16 17:40:34');
INSERT INTO `sys_log` VALUES (410, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":438,\"parentId\":356,\"name\":\"添加\",\"url\":\"\",\"perms\":\"welfareTask:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 138, '192.168.0.136', '2026-07-16 17:41:47');
INSERT INTO `sys_log` VALUES (411, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":439,\"parentId\":356,\"name\":\"修改\",\"url\":\"\",\"perms\":\"welfareTask:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 135, '192.168.0.136', '2026-07-16 17:42:05');
INSERT INTO `sys_log` VALUES (412, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":440,\"parentId\":356,\"name\":\"删除\",\"url\":\"\",\"perms\":\"welfareTask:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 137, '192.168.0.136', '2026-07-16 17:42:19');
INSERT INTO `sys_log` VALUES (413, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":441,\"parentId\":362,\"name\":\"查看\",\"url\":\"\",\"perms\":\"userWelfare:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 157, '192.168.0.136', '2026-07-16 17:42:39');
INSERT INTO `sys_log` VALUES (414, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":442,\"parentId\":352,\"name\":\"查看\",\"url\":\"\",\"perms\":\"message:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 148, '192.168.0.136', '2026-07-16 17:43:09');
INSERT INTO `sys_log` VALUES (415, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":443,\"parentId\":361,\"name\":\"查看\",\"url\":\"\",\"perms\":\"serviceChat:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 137, '192.168.0.136', '2026-07-16 17:44:24');
INSERT INTO `sys_log` VALUES (416, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":444,\"parentId\":361,\"name\":\"发送消息\",\"url\":\"\",\"perms\":\"serviceChat:fasong\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 452, '192.168.0.136', '2026-07-16 17:45:02');
INSERT INTO `sys_log` VALUES (417, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,381,310,311,382,383,384,385,386,312,387,320,327,391,328,392,323,396,324,397,325,398,326,399,321,388,389,390,343,419,420,322,393,394,395,330,331,400,401,402,403,332,404,405,406,407,334,408,409,410,411,333,412,413,414,415,380,416,417,418,340,341,421,422,342,423,344,424,345,425,346,426,351,427,428,429,430,350,353,431,432,433,434,355,435,436,356,437,438,439,440,362,441,352,442,361,443,444,372,375,376,377,378,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,379,373,374,-666666]}]', 13679, '192.168.0.136', '2026-07-16 17:47:19');
INSERT INTO `sys_log` VALUES (418, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,381,310,311,382,383,384,385,386,312,387,320,327,391,328,392,323,396,324,397,325,398,326,399,321,388,389,390,343,419,420,322,393,394,395,330,331,400,401,402,403,332,404,405,406,407,334,408,409,410,411,333,412,413,414,415,380,416,417,418,340,341,421,422,342,423,344,424,345,425,346,426,351,427,428,429,430,350,353,431,432,433,434,355,435,436,356,437,438,439,440,362,441,352,442,361,443,444,372,375,376,377,378,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,379,373,374,-666666]}]', 12797, '192.168.0.136', '2026-07-16 17:47:47');
INSERT INTO `sys_log` VALUES (419, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,381,310,311,382,383,384,385,386,312,387,320,327,391,328,392,323,396,324,397,325,398,326,399,321,388,389,390,343,419,420,322,393,394,395,330,331,400,401,402,403,332,404,405,406,407,334,408,409,410,411,333,412,413,414,415,380,416,417,418,340,341,421,422,342,423,344,424,345,425,346,426,351,427,428,429,430,350,353,431,432,433,434,355,435,436,356,437,438,439,440,362,441,352,442,361,443,444,372,375,376,377,378,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,379,373,374,-666666]}]', 12739, '192.168.0.136', '2026-07-16 17:49:33');
INSERT INTO `sys_log` VALUES (420, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,381,310,311,382,383,384,385,386,312,387,320,327,391,328,392,323,396,324,397,325,398,326,399,321,388,389,390,343,419,420,322,393,394,395,330,331,400,401,402,403,332,404,405,406,407,334,408,409,410,411,333,412,413,414,415,380,416,417,418,340,341,421,422,342,423,344,424,345,425,346,426,351,427,428,429,430,350,353,431,432,433,434,355,435,436,356,437,438,439,440,362,441,352,442,361,443,444,372,375,376,377,378,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,379,373,374,-666666]}]', 12667, '192.168.0.136', '2026-07-16 17:50:31');
INSERT INTO `sys_log` VALUES (421, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,381,310,311,382,383,384,385,386,312,387,320,327,391,328,392,323,396,324,397,325,398,326,399,321,388,389,390,343,419,420,322,393,394,395,330,331,400,401,402,403,332,404,405,406,407,334,408,409,410,411,333,412,413,414,415,380,416,417,418,340,341,421,422,342,423,344,424,345,425,346,426,351,427,428,429,430,350,353,431,432,433,434,355,435,436,356,437,438,439,440,362,441,352,442,361,443,444,372,375,376,377,378,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,379,373,374,-666666]}]', 13111, '192.168.0.136', '2026-07-16 17:51:50');
INSERT INTO `sys_log` VALUES (422, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,381,310,311,382,383,384,385,386,312,387,320,327,391,328,392,323,396,324,397,325,398,326,399,321,388,389,390,343,419,420,322,393,394,395,330,331,400,401,402,403,332,404,405,406,407,334,408,409,410,411,333,412,413,414,415,380,416,417,418,340,341,421,422,342,423,344,424,345,425,346,426,351,427,428,429,430,350,353,431,432,433,434,355,435,436,356,437,438,439,440,362,441,352,442,361,443,444,372,375,376,377,378,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,379,373,374]}]', 470, '192.168.0.136', '2026-07-16 17:58:14');
INSERT INTO `sys_log` VALUES (423, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,381,382,383,384,386,312,387,320,327,391,328,392,323,396,324,397,325,398,326,399,388,390,343,419,420,322,393,394,395,331,400,401,402,403,404,405,406,334,408,409,410,411,333,412,413,414,415,380,416,417,418,340,341,421,422,342,423,344,424,345,425,346,426,351,427,428,429,430,350,353,431,432,433,434,355,435,436,356,437,438,439,440,362,441,352,442,361,443,444,372,375,376,377,378,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,379,373,374,310,311,321,330,332]}]', 388, '192.168.0.136', '2026-07-16 18:06:19');
INSERT INTO `sys_log` VALUES (424, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,381,382,312,387,320,327,391,328,392,323,396,324,397,325,398,326,399,321,388,389,390,343,419,420,322,393,394,395,330,331,400,401,402,403,332,404,405,406,407,334,408,409,410,411,333,412,413,414,415,380,416,417,418,340,341,421,422,342,423,344,424,345,425,346,426,351,427,428,429,430,350,353,431,432,433,434,355,435,436,356,437,438,439,440,362,441,352,442,361,443,444,372,375,376,377,378,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,379,373,374,310,311]}]', 839, '192.168.0.136', '2026-07-16 18:07:06');
INSERT INTO `sys_log` VALUES (425, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,381,382,383,384,385,386,312,387,320,327,391,328,392,323,396,324,397,325,398,326,399,321,388,389,390,343,419,420,322,393,394,395,330,331,400,401,402,403,332,404,405,406,407,334,408,409,410,411,333,412,413,414,415,380,416,417,418,340,341,421,422,342,423,344,424,345,425,346,426,351,427,428,429,430,350,353,431,432,433,434,355,435,436,356,437,438,439,440,362,441,352,442,361,443,444,372,375,376,377,378,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,379,373,374,310,311]}]', 378, '192.168.0.136', '2026-07-16 18:32:45');
INSERT INTO `sys_log` VALUES (426, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,381,310,311,382,383,384,385,386,445,446,312,387,320,327,391,328,392,323,396,324,397,325,398,326,399,321,388,389,390,343,419,420,322,393,394,395,330,331,400,401,402,403,332,404,405,406,407,334,408,409,410,411,333,412,413,414,415,380,416,417,418,340,341,421,422,342,423,344,424,345,425,346,426,351,427,428,429,430,350,353,431,432,433,434,355,435,436,356,437,438,439,440,362,441,352,442,361,443,444,372,375,376,377,378,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,379,373,374]}]', 284, '192.168.0.136', '2026-07-16 18:33:06');
INSERT INTO `sys_log` VALUES (427, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[399]', 314, '192.168.0.136', '2026-07-17 09:50:49');
INSERT INTO `sys_log` VALUES (428, 'nyy', '删除菜单', 'com.sqx.system.controller.SysMenuController.delete()', '[326]', 288, '192.168.0.136', '2026-07-17 09:50:54');
INSERT INTO `sys_log` VALUES (429, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":447,\"parentId\":363,\"name\":\"查看\",\"url\":\"\",\"perms\":\"serviceConfig:list\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 152, '192.168.0.136', '2026-07-21 10:44:18');
INSERT INTO `sys_log` VALUES (430, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":448,\"parentId\":363,\"name\":\"添加\",\"url\":\"\",\"perms\":\"serviceConfig:add\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 146, '192.168.0.136', '2026-07-21 10:44:36');
INSERT INTO `sys_log` VALUES (431, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":449,\"parentId\":363,\"name\":\"修改\",\"url\":\"\",\"perms\":\"serviceConfig:update\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 145, '192.168.0.136', '2026-07-21 10:44:52');
INSERT INTO `sys_log` VALUES (432, 'nyy', '保存菜单', 'com.sqx.system.controller.SysMenuController.save()', '[{\"menuId\":450,\"parentId\":363,\"name\":\"删除\",\"url\":\"\",\"perms\":\"serviceConfig:delete\",\"type\":2,\"icon\":\"\",\"orderNum\":0}]', 154, '192.168.0.136', '2026-07-21 10:45:07');
INSERT INTO `sys_log` VALUES (433, 'nyy', '修改菜单', 'com.sqx.system.controller.SysMenuController.update()', '[{\"menuId\":363,\"parentId\":0,\"name\":\"客服配置\",\"url\":\"serviceConfig\",\"perms\":\"\",\"type\":1,\"icon\":\"config\",\"orderNum\":14}]', 135, '192.168.0.136', '2026-07-21 10:45:44');
INSERT INTO `sys_log` VALUES (434, 'nyy', '修改角色', 'com.sqx.system.controller.SysRoleController.update()', '[{\"roleId\":1,\"roleName\":\"超级管理员\",\"remark\":\"super\",\"createUserId\":2,\"menuIdList\":[300,381,310,311,382,383,384,385,386,445,446,312,387,320,327,391,328,392,323,396,324,397,325,398,321,388,389,390,343,419,420,322,393,394,395,330,331,400,401,402,403,332,404,405,406,407,334,408,409,410,411,333,412,413,414,415,380,416,417,418,340,341,421,422,342,423,344,424,345,425,346,426,351,427,428,429,430,350,353,431,432,433,434,355,435,436,356,437,438,439,440,362,441,352,442,363,447,448,449,450,361,443,444,372,375,376,377,378,126,2,15,16,17,18,3,19,20,21,22,4,23,24,25,26,370,371,379,373,374]}]', 402, '192.168.0.136', '2026-07-21 10:46:04');
INSERT INTO `sys_log` VALUES (435, 'zww', '修改配置', 'com.sqx.system.controller.SysConfigController.update()', '[{\"id\":145,\"paramKey\":\"platform_mail_receiver_province\",\"paramValue\":\"陕西省\",\"status\":\"1\",\"remark\":\"平台收件省\",\"paramType\":\"default\",\"paramTypeDesc\":\"其他配置\"}]', 387, '192.168.0.136', '2026-07-22 10:46:37');
INSERT INTO `sys_log` VALUES (436, 'zww', '刷新缓存', 'com.sqx.system.controller.SysConfigController.refresh()', '[]', 173, '192.168.0.136', '2026-07-22 10:46:37');
INSERT INTO `sys_log` VALUES (437, 'zww', '修改配置', 'com.sqx.system.controller.SysConfigController.update()', '[{\"id\":146,\"paramKey\":\"platform_mail_receiver_city\",\"paramValue\":\"西安市\",\"status\":\"1\",\"remark\":\"平台收件市\",\"paramType\":\"default\",\"paramTypeDesc\":\"其他配置\"}]', 428, '192.168.0.136', '2026-07-22 10:46:46');
INSERT INTO `sys_log` VALUES (438, 'zww', '刷新缓存', 'com.sqx.system.controller.SysConfigController.refresh()', '[]', 198, '192.168.0.136', '2026-07-22 10:46:47');
INSERT INTO `sys_log` VALUES (439, 'zww', '修改配置', 'com.sqx.system.controller.SysConfigController.update()', '[{\"id\":147,\"paramKey\":\"platform_mail_receiver_county\",\"paramValue\":\"雁塔区\",\"status\":\"1\",\"remark\":\"平台收件区/县\",\"paramType\":\"default\",\"paramTypeDesc\":\"其他配置\"}]', 649, '192.168.0.136', '2026-07-22 10:47:02');
INSERT INTO `sys_log` VALUES (440, 'zww', '刷新缓存', 'com.sqx.system.controller.SysConfigController.refresh()', '[]', 377, '192.168.0.136', '2026-07-22 10:47:03');
INSERT INTO `sys_log` VALUES (441, 'zww', '修改配置', 'com.sqx.system.controller.SysConfigController.update()', '[{\"id\":135,\"paramKey\":\"platform_mail_receiver_address\",\"paramValue\":\"陕西省西安市雁塔区高新四路13号\",\"status\":\"1\",\"remark\":\"平台收件地址\",\"paramType\":\"default\",\"paramTypeDesc\":\"其他配置\"}]', 382, '192.168.0.136', '2026-07-22 10:47:41');
INSERT INTO `sys_log` VALUES (442, 'zww', '刷新缓存', 'com.sqx.system.controller.SysConfigController.refresh()', '[]', 337, '192.168.0.136', '2026-07-22 10:47:42');
INSERT INTO `sys_log` VALUES (443, 'nyy', '刷新缓存', 'com.sqx.system.controller.SysConfigController.refresh()', '[]', 122, '127.0.0.1', '2026-08-17 17:15:08');
INSERT INTO `sys_log` VALUES (444, 'nyy', '修改配置', 'com.sqx.system.controller.SysConfigController.update()', '[{\"id\":148,\"paramKey\":\"welfare_coin_wallet_rate\",\"paramValue\":\"0.9\",\"status\":\"1\",\"remark\":\"环保积分兑换钱包余额比例，1 表示 1 积分 \\u003d 1 元\",\"paramType\":\"welfare\",\"paramTypeDesc\":\"福利中心\"}]', 320, '192.168.0.128', '2026-09-23 15:07:53');
INSERT INTO `sys_log` VALUES (445, 'nyy', '刷新缓存', 'com.sqx.system.controller.SysConfigController.refresh()', '[]', 146, '192.168.0.128', '2026-09-23 15:07:53');
INSERT INTO `sys_log` VALUES (446, 'nyy', '修改配置', 'com.sqx.system.controller.SysConfigController.update()', '[{\"id\":149,\"paramKey\":\"aliyun_logistics_appcode\",\"paramValue\":\"38e5cf82f85244439bad57ae8756e9c1\",\"status\":\"1\",\"remark\":\"阿里云物流查询 AppCode（市场商品 wuliu.market.alicloudapi.com）\",\"paramType\":\"default\",\"paramTypeDesc\":\"其他配置\"}]', 323, '192.168.0.128', '2026-09-24 18:51:33');
INSERT INTO `sys_log` VALUES (447, 'nyy', '刷新缓存', 'com.sqx.system.controller.SysConfigController.refresh()', '[]', 177, '192.168.0.128', '2026-09-24 18:51:33');
INSERT INTO `sys_log` VALUES (448, 'nyy', '修改配置', 'com.sqx.system.controller.SysConfigController.update()', '[{\"id\":71,\"paramKey\":\"sms_provider\",\"paramValue\":\"2\",\"status\":\"1\",\"remark\":\"\",\"paramType\":\"default\",\"paramTypeDesc\":\"其他配置\"}]', 240, '192.168.0.128', '2026-09-29 19:09:43');
INSERT INTO `sys_log` VALUES (449, 'nyy', '刷新缓存', 'com.sqx.system.controller.SysConfigController.refresh()', '[]', 96, '192.168.0.128', '2026-09-29 19:09:44');
INSERT INTO `sys_log` VALUES (450, 'nyy', '修改配置', 'com.sqx.system.controller.SysConfigController.update()', '[{\"id\":71,\"paramKey\":\"sms_provider\",\"paramValue\":\"3\",\"status\":\"1\",\"remark\":\"\",\"paramType\":\"default\",\"paramTypeDesc\":\"其他配置\"}]', 241, '192.168.0.128', '2026-09-29 19:09:55');
INSERT INTO `sys_log` VALUES (451, 'nyy', '刷新缓存', 'com.sqx.system.controller.SysConfigController.refresh()', '[]', 88, '192.168.0.128', '2026-09-29 19:09:55');
INSERT INTO `sys_log` VALUES (452, 'nyy', '修改配置', 'com.sqx.system.controller.SysConfigController.update()', '[{\"id\":132,\"paramKey\":\"user_service_agreement\",\"paramValue\":\"{\\\"title\\\":\\\"用户服务协议\\\",\\\"html\\\":\\\"\\u003cp\\u003e尊敬的用户您好：在您使用本服务之前，请您认真阅读本用户协议，更好的了解我们所提供的服务以及您享有的权利义务。您开始使用时，即表示您已经了解并确认接受了本文件中的全部条款，包括我们对本服务条款随时做的任何修改。\\u003c/p\\u003e\\u003cp\\u003e一、协议的效力\\u003c/p\\u003e\\u003cp\\u003e本协议内容包括协议正文及所有已经发布或将来可能发布的各类规则。所有规则为本协议不可分割的组成部分，与协议正文具有同等法律效力。您承诺接受并遵守本协议的约定。如果您不同意本协议的约定，您应立即停止使用本平台服务。\\u003c/p\\u003e\\u003cp\\u003e二、用户行为规范\\u003c/p\\u003e\\u003cp\\u003e用户同意将不会利用本服务进行任何违法或不正当的活动，包括但不限于下列行为∶\\u003c/p\\u003e\\u003cp\\u003e发布或以其它方式传送含有下列内容之一的信息：\\u003c/p\\u003e\\u003cp\\u003e反对宪法所确定的基本原则的；\\u003c/p\\u003e\\u003cp\\u003e危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；\\u003c/p\\u003e\\u003cp\\u003e损害国家荣誉和利益的；\\u003c/p\\u003e\\u003cp\\u003e煽动民族仇恨、民族歧视、破坏民族团结的；\\u003c/p\\u003e\\u003cp\\u003e破坏国家宗教政策，宣扬邪教和封建迷信的；\\u003c/p\\u003e\\u003cp\\u003e散布谣言，扰乱社会秩序，破坏社会稳定的；\\u003c/p\\u003e\\u003cp\\u003e散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；\\u003c/p\\u003e\\u003cp\\u003e侮辱或者诽谤他人，侵害他人合法权利的；\\u003c/p\\u003e\\u003cp\\u003e含有虚假、诈骗、有害、胁迫、侵害他人隐私、骚扰、侵害、中伤、粗俗、猥亵、或其它道德上令人反感的内容；\\u003c/p\\u003e\\u003cp\\u003e含有当地法律、法规、规章、条例以及任何具有法律效力之规范所限制或禁止的其它内容的；\\u003c/p\\u003e\\u003cp\\u003e含有不适合在本平台展示的内容；\\u003c/p\\u003e\\u003cp\\u003e以任何方式危害他人的合法权益；\\u003c/p\\u003e\\u003cp\\u003e冒充其他任何人或机构，或以虚伪不实的方式陈述或谎称与任何人或机构有关；\\u003c/p\\u003e\\u003cp\\u003e将依据任何法律或合约或法定关系（例如由于雇佣关系和依据保密合约所得知或揭露之内部资料、专属及机密资料）知悉但无权传送之任何内容加以发布、发送电子邮件或以其它方式传送；\\u003c/p\\u003e\\u003cp\\u003e将侵害他人著作权、专利权、商标权、商业秘密、或其它专属权利（以下简称“专属权利”）之内容加以发布或以其它方式传送；\\u003c/p\\u003e\\u003cp\\u003e将任何广告信函、促销资料、“垃圾邮件”、““滥发信件”、“连锁信件”、“直销”或其它任何形式的劝诱资料加以发布、发送或以其它方式传送；\\u003c/p\\u003e\\u003cp\\u003e将设计目的在于干扰、破坏或限制任何计算机软件、硬件或通讯设备功能之计算机病毒（包括但不限于木马程序（trojan horses）、蠕虫（worms）、定时炸弹、删除蝇（cancelbots）（以下简称“病毒”）或其它计算机代码、档案和程序之任何资料，加以发布、发送或以其它方式传送；\\u003c/p\\u003e\\u003cp\\u003e干扰或破坏本服务或与本服务相连线之服务器和网络，或违反任何关于本服务连线网络之规定、程序、政策或规范；\\u003c/p\\u003e\\u003cp\\u003e跟踪、人肉搜索或以其它方式骚扰他人；\\u003c/p\\u003e\\u003cp\\u003e故意或非故意地违反任何适用的当地、国家法律，以及任何具有法律效力的规则；\\u003c/p\\u003e\\u003cp\\u003e未经合法授权而截获、篡改、收集、储存或删除他人个人信息、站内邮件或其它数据资料，或将获知的此类资料用于任何非法或不正当目的。\\u003c/p\\u003e\\u003cp\\u003e三、知识产权\\u003c/p\\u003e\\u003cp\\u003e本平台所有设计图样以及其他图样、产品及服务名称。任何人不得使用、复制或用作其他用途。未经我们许可，任何单位和个人不得私自复制、传播、展示、镜像、上载、下载、使用，或者从事任何其他侵犯我们知识产权的行为。否则，我们将追究相关法律责任。\\u003c/p\\u003e\\u003cp\\u003e我们鼓励用户充分利用平台自由地张贴和共享自己的信息，但这些内容必须位于公共领域内，或者用户拥有这些内容的使用权。同时，用户对于其创作并在本平台上发布的合法内容依法享有著作权及其相关权利。\\u003c/p\\u003e\\u003cp\\u003e四、免责声明\\u003c/p\\u003e\\u003cp\\u003e互联网是一个开放平台，用户将照片等个人资料上传到互联网上，有可能会被其他组织或个人复制、转载、擅改或做其它非法用途，用户必须充分意识此类风险的存在。用户明确同意其使用本服务所存在的风险将完全由其自己承担；因其使用本服务而产生的一切后果也由其自己承担，我们对用户不承担任何责任。\\u003c/p\\u003e\\u003cp\\u003e对于用户上传的照片、资料、证件等，已采用相关措施并已尽合理努力进行审核，但不保证其内容的正确性、合法性或可靠性，相关责任由上传上述内容的会员负责。\\u003c/p\\u003e\\u003cp\\u003e尽管已采取相应的技术保障措施 ，但用户仍有可能收到各类的广告信或其他不以招聘/应聘为目的邮件或其它方式传送的任何内容，本平台不承担责任。\\u003c/p\\u003e\\u003cp\\u003e对于各种广告信息、链接、资讯等，不保证其内容的正确性、合法性或可靠性，相关责任由广告商承担；用户通过本服务与广告商进行任何形式的通讯或商业往来，或参与促销活动，包含相关商品或服务之付款及交付，以及达成的其它任何相关条款、条件、保证或声明，完全为用户与广告商之间之行为，与本平台无关。用户因前述任何交易或前述广告商而遭受的任何性质的损失或损害，本平台不承担任何责任。\\u003c/p\\u003e\\u003cp\\u003e本平台不保证其提供的服务一定能满足用户的要求和期望，也不保证服务不会中断，对服务的及时性、安全性、准确性也都不作保证。对于因不可抗力或无法控制的原因造成的网络服务中断或其他缺陷，不承担任何责任。我们不对用户所发布信息的删除或储存失败承担责任。我们有权判断用户的行为是否符合本网站使用协议条款之规定，如果我们认为用户违背了协议条款的规定，我们有终止向其提供服务的权利。\\u003c/p\\u003e\\u003cp\\u003e本平台保留变更、中断或终止部分网络服务的权利。保留根据实际情况随时调整平台提供的服务种类、形式的权利。本平台不承担因业务调整给用户造成的损失。本平台仅提供相关服务，除此之外与本服务有关的设备（如电脑、调制解调器及其他与接入互联网有关的装置）及所需的费用（如为接入互联网而支付的电话费及上网费）均应由用户自行负担。\\u003c/p\\u003e\\\"}\",\"status\":\"1\",\"remark\":\"用户服务协议\",\"paramType\":\"agreement\",\"paramTypeDesc\":\"协议配置\"}]', 288, '192.168.0.128', '2026-09-29 21:30:39');
INSERT INTO `sys_log` VALUES (453, 'nyy', '刷新缓存', 'com.sqx.system.controller.SysConfigController.refresh()', '[]', 424, '192.168.0.128', '2026-09-29 21:30:39');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(0) NULL DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` int(0) NULL DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '菜单图标',
  `order_num` int(0) NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 451 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '菜单管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (2, 126, '管理员列表', 'user', NULL, 1, 'admin', 10);
INSERT INTO `sys_menu` VALUES (3, 126, '角色管理', 'role', NULL, 1, 'yonghu', 11);
INSERT INTO `sys_menu` VALUES (4, 126, '菜单管理', 'menu', NULL, 1, 'zhedie', 12);
INSERT INTO `sys_menu` VALUES (15, 2, '查看', NULL, 'sys:user:list,sys:user:info', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (16, 2, '新增', NULL, 'sys:user:save,sys:role:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (17, 2, '修改', NULL, 'sys:user:update,sys:role:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (18, 2, '删除', NULL, 'sys:user:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (19, 3, '查看', NULL, 'sys:role:list,sys:role:info', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (20, 3, '新增', NULL, 'sys:role:save,sys:menu:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (21, 3, '修改', NULL, 'sys:role:update,sys:menu:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (22, 3, '删除', NULL, 'sys:role:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (23, 4, '查看', NULL, 'sys:menu:list,sys:menu:info', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (24, 4, '新增', NULL, 'sys:menu:save,sys:menu:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (25, 4, '修改', NULL, 'sys:menu:update,sys:menu:select', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (26, 4, '删除', NULL, 'sys:menu:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (126, 0, '系统管理', '', '', 0, 'svg:cat-code.svg', 99);
INSERT INTO `sys_menu` VALUES (300, 0, '数据中心', 'home', 'admin:datacenter:view', 1, 'shouye', 1);
INSERT INTO `sys_menu` VALUES (310, 0, '用户管理', '', '', 0, 'yonghul', 2);
INSERT INTO `sys_menu` VALUES (311, 310, '用户中心', 'userList', '', 1, 'yonghul', 1);
INSERT INTO `sys_menu` VALUES (312, 310, '用户地址', 'userAddress', '', 1, 'dangdifill', 2);
INSERT INTO `sys_menu` VALUES (320, 0, '订单中心', '', '', 0, 'order', 3);
INSERT INTO `sys_menu` VALUES (321, 0, '回收订单', 'orderList', '', 1, 'log', 3);
INSERT INTO `sys_menu` VALUES (322, 0, '取件任务', 'pickupTask', '', 1, 'renwu', 4);
INSERT INTO `sys_menu` VALUES (323, 320, '结算管理', 'settlement', '', 1, 'money', 5);
INSERT INTO `sys_menu` VALUES (324, 320, '打款管理', 'payout', '', 1, 'money', 6);
INSERT INTO `sys_menu` VALUES (325, 320, '争议工单', 'dispute', '', 1, 'warning', 7);
INSERT INTO `sys_menu` VALUES (327, 320, '改约记录', 'orderReschedule', '', 1, 'date', 2);
INSERT INTO `sys_menu` VALUES (328, 320, '异常工单', 'orderException', '', 1, 'warning', 3);
INSERT INTO `sys_menu` VALUES (330, 0, '回收配置', '', '', 0, 'shezhi', 4);
INSERT INTO `sys_menu` VALUES (331, 330, '回收品类', 'recycleCategory', '', 1, 'renwu1', 1);
INSERT INTO `sys_menu` VALUES (332, 330, '服务区域', 'serviceArea', '', 1, 'pingtai', 2);
INSERT INTO `sys_menu` VALUES (333, 330, '回收规则', 'recycleRule', '', 1, 'peizhi', 3);
INSERT INTO `sys_menu` VALUES (334, 330, '商品分类', 'productCategory', '', 1, 'fenleilist', 2);
INSERT INTO `sys_menu` VALUES (340, 0, '财务中心', '', '', 0, 'caiwu', 5);
INSERT INTO `sys_menu` VALUES (341, 340, '用户钱包', 'userWallet', '', 1, 'caiwu', 1);
INSERT INTO `sys_menu` VALUES (342, 340, '钱包流水', 'walletTransaction', '', 1, 'jifen', 2);
INSERT INTO `sys_menu` VALUES (343, 0, '提现管理', 'withdrawRecord', '', 1, 'jifen', 3);
INSERT INTO `sys_menu` VALUES (344, 340, '提现账户', 'withdrawalAccount', '', 1, 'order', 4);
INSERT INTO `sys_menu` VALUES (345, 340, '分销佣金', 'referralCommission', '', 1, 'jifen', 5);
INSERT INTO `sys_menu` VALUES (346, 340, '用户佣金汇总', 'referralUserCommission', '', 1, 'log', 6);
INSERT INTO `sys_menu` VALUES (350, 0, '运营内容', '', '', 0, 'fenleilist', 6);
INSERT INTO `sys_menu` VALUES (351, 0, '首页装修', 'bannerList', '', 1, 'peizhilb', 5);
INSERT INTO `sys_menu` VALUES (352, 0, '消息管理', 'message', '', 1, 'pinglun', 6);
INSERT INTO `sys_menu` VALUES (353, 350, '帮助中心', 'helpCategory', '', 1, 'renwu1', 3);
INSERT INTO `sys_menu` VALUES (355, 350, '品牌合作', 'brandCooperation', '', 1, 'peizhilb', 4);
INSERT INTO `sys_menu` VALUES (356, 350, '福利任务', 'welfareTask', '', 1, 'renwu', 5);
INSERT INTO `sys_menu` VALUES (361, 0, '在线客服', 'serviceChat', '', 1, 'tixing', 15);
INSERT INTO `sys_menu` VALUES (362, 350, '用户福利', 'userWelfare', '', 1, 'fenleilist', 6);
INSERT INTO `sys_menu` VALUES (363, 0, '客服配置', 'serviceConfig', '', 1, 'config', 14);
INSERT INTO `sys_menu` VALUES (370, 126, '字典管理', 'dict', 'sys:dict:list', 1, 'peizhi', 13);
INSERT INTO `sys_menu` VALUES (371, 126, '权限管理', 'permission', 'sys:menu:list', 1, 'shuju', 14);
INSERT INTO `sys_menu` VALUES (372, 0, '系统配置', 'allocationList', 'sys:config:list', 1, 'config', 19);
INSERT INTO `sys_menu` VALUES (373, 126, '短信模板', 'smsTemplate', '', 1, 'xinxi', 17);
INSERT INTO `sys_menu` VALUES (374, 126, '操作日志', 'log', 'sys:log:list', 1, 'log', 18);
INSERT INTO `sys_menu` VALUES (375, 372, '配置详情', NULL, 'sys:config:info', 2, NULL, 1);
INSERT INTO `sys_menu` VALUES (376, 372, '新增配置', NULL, 'sys:config:save', 2, NULL, 2);
INSERT INTO `sys_menu` VALUES (377, 372, '修改配置', '', 'sys:config:update', 2, '', 3);
INSERT INTO `sys_menu` VALUES (378, 372, '删除配置', '', 'sys:config:delete', 2, '', 4);
INSERT INTO `sys_menu` VALUES (379, 126, '配置分组', 'configGroups', 'sys:config:list', 1, 'config', 16);
INSERT INTO `sys_menu` VALUES (380, 330, '回收配置', 'bookWeightOption', '', 1, 'config', 6);
INSERT INTO `sys_menu` VALUES (381, 300, '查看', NULL, 'home:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (382, 311, '查看', NULL, 'userList:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (383, 311, '修改信息', NULL, 'userList:update', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (384, 311, '修改状态', NULL, 'userList:updateStatus', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (385, 311, '修改金额', NULL, 'userList:updateJe', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (386, 311, '修改积分', NULL, 'userList:updateJf', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (387, 312, '查看', NULL, 'userAddress:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (388, 321, '查看', NULL, 'orderList:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (389, 321, '取消订单', NULL, 'orderList:cancel', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (390, 321, '分配员工', NULL, 'orderList:allocation', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (391, 327, '查看', NULL, 'orderReschedule:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (392, 328, '查看', NULL, 'orderException:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (393, 322, '查看', NULL, 'pickupTast:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (394, 322, '分配员工', NULL, 'pickupTask:allication', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (395, 322, '完成', NULL, 'pickupTask:complete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (396, 323, '查看', NULL, 'settlement:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (397, 324, '查看', NULL, 'payout:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (398, 325, '查看', NULL, 'dispute:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (400, 331, '查看', NULL, 'recycleCategory:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (401, 331, '添加', NULL, 'recycleCategory:add', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (402, 331, '修改', NULL, 'recycleCategory:update', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (403, 331, '删除', NULL, 'recycleCategory:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (404, 332, '查看', NULL, 'serviceArea:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (405, 332, '添加', NULL, 'serviceArea:add', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (406, 332, '修改', NULL, 'serviceArea:update', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (407, 332, '删除', NULL, 'serviceArea:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (408, 334, '查看', NULL, 'productCategory:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (409, 334, '添加', NULL, 'productCategory:add', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (410, 334, '修改', NULL, 'productCategory:update', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (411, 334, '删除', NULL, 'productCategory:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (412, 333, '查看', NULL, 'recycleRule:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (413, 333, '添加', NULL, 'recycleRule:add', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (414, 333, '修改', NULL, 'recycleRule:update', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (415, 333, '删除', NULL, 'recycleRule:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (416, 380, '查看', NULL, 'bookWeightOption:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (417, 380, '添加', NULL, 'bookWeightOption:add', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (418, 380, '修改', NULL, 'bookWeightOption:update', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (419, 343, '查看', NULL, 'withdrawRecord:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (420, 343, '审核', NULL, 'withdrawRecord:shenhe', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (421, 341, '查看', NULL, 'userWallet:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (422, 341, '修改余额', NULL, 'userWallet:update', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (423, 342, '查看', NULL, 'walletTransaction:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (424, 344, '查看', NULL, 'withdrawalAccount:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (425, 345, '查看', NULL, 'referralCommission:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (426, 346, '查看', NULL, 'referralUserCommission:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (427, 351, '查看', NULL, 'bannerList:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (428, 351, '添加', NULL, 'bannerList:add', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (429, 351, '修改', NULL, 'bannerList:update', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (430, 351, '删除', NULL, 'bannerList:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (431, 353, '查看', NULL, 'helpCategory:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (432, 353, '添加', NULL, 'helpCategory:add', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (433, 353, '修改', NULL, 'helpCategory:update', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (434, 353, '删除', NULL, 'helpCategory:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (435, 355, '查看', NULL, 'brandCooperation:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (436, 355, '跟进', NULL, 'brandCooperation:shenhe', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (437, 356, '查看', NULL, 'welfareTask:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (438, 356, '添加', NULL, 'welfareTask:add', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (439, 356, '修改', NULL, 'welfareTask:update', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (440, 356, '删除', NULL, 'welfareTask:delete', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (441, 362, '查看', NULL, 'userWelfare:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (442, 352, '查看', NULL, 'message:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (443, 361, '查看', NULL, 'serviceChat:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (444, 361, '发送消息', NULL, 'serviceChat:fasong', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (445, 311, '删除用户', '', 'userList:delete', 2, '', 6);
INSERT INTO `sys_menu` VALUES (446, 311, '修改分销收益', '', 'userList:updateFxsy', 2, '', 7);
INSERT INTO `sys_menu` VALUES (447, 363, '查看', NULL, 'serviceConfig:list', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (448, 363, '添加', NULL, 'serviceConfig:add', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (449, 363, '修改', NULL, 'serviceConfig:update', 2, NULL, 0);
INSERT INTO `sys_menu` VALUES (450, 363, '删除', NULL, 'serviceConfig:delete', 2, NULL, 0);

-- ----------------------------
-- Table structure for sys_oss
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss`;
CREATE TABLE `sys_oss`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'URL地址',
  `create_date` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '文件上传' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oss
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
  `create_user_id` bigint(0) NULL DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'super', 2, '2021-06-04 17:38:28');
INSERT INTO `sys_role` VALUES (4, '普通用户', '普通用户', 2, '2021-09-10 15:33:47');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(0) NULL DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint(0) NULL DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16016 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '角色与菜单对应关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (13252, 4, 126);
INSERT INTO `sys_role_menu` VALUES (13253, 4, 2);
INSERT INTO `sys_role_menu` VALUES (13254, 4, 15);
INSERT INTO `sys_role_menu` VALUES (13255, 4, 16);
INSERT INTO `sys_role_menu` VALUES (13256, 4, 17);
INSERT INTO `sys_role_menu` VALUES (13257, 4, 18);
INSERT INTO `sys_role_menu` VALUES (13258, 4, 3);
INSERT INTO `sys_role_menu` VALUES (13259, 4, 19);
INSERT INTO `sys_role_menu` VALUES (13260, 4, 20);
INSERT INTO `sys_role_menu` VALUES (13261, 4, 21);
INSERT INTO `sys_role_menu` VALUES (13262, 4, 22);
INSERT INTO `sys_role_menu` VALUES (13263, 4, 4);
INSERT INTO `sys_role_menu` VALUES (13264, 4, 23);
INSERT INTO `sys_role_menu` VALUES (13265, 4, 24);
INSERT INTO `sys_role_menu` VALUES (13266, 4, 25);
INSERT INTO `sys_role_menu` VALUES (13267, 4, 26);
INSERT INTO `sys_role_menu` VALUES (13268, 4, NULL);
INSERT INTO `sys_role_menu` VALUES (14579, 4, 379);
INSERT INTO `sys_role_menu` VALUES (15887, 1, 300);
INSERT INTO `sys_role_menu` VALUES (15888, 1, 381);
INSERT INTO `sys_role_menu` VALUES (15889, 1, 310);
INSERT INTO `sys_role_menu` VALUES (15890, 1, 311);
INSERT INTO `sys_role_menu` VALUES (15891, 1, 382);
INSERT INTO `sys_role_menu` VALUES (15892, 1, 383);
INSERT INTO `sys_role_menu` VALUES (15893, 1, 384);
INSERT INTO `sys_role_menu` VALUES (15894, 1, 385);
INSERT INTO `sys_role_menu` VALUES (15895, 1, 386);
INSERT INTO `sys_role_menu` VALUES (15896, 1, 445);
INSERT INTO `sys_role_menu` VALUES (15897, 1, 446);
INSERT INTO `sys_role_menu` VALUES (15898, 1, 312);
INSERT INTO `sys_role_menu` VALUES (15899, 1, 387);
INSERT INTO `sys_role_menu` VALUES (15900, 1, 320);
INSERT INTO `sys_role_menu` VALUES (15901, 1, 327);
INSERT INTO `sys_role_menu` VALUES (15902, 1, 391);
INSERT INTO `sys_role_menu` VALUES (15903, 1, 328);
INSERT INTO `sys_role_menu` VALUES (15904, 1, 392);
INSERT INTO `sys_role_menu` VALUES (15905, 1, 323);
INSERT INTO `sys_role_menu` VALUES (15906, 1, 396);
INSERT INTO `sys_role_menu` VALUES (15907, 1, 324);
INSERT INTO `sys_role_menu` VALUES (15908, 1, 397);
INSERT INTO `sys_role_menu` VALUES (15909, 1, 325);
INSERT INTO `sys_role_menu` VALUES (15910, 1, 398);
INSERT INTO `sys_role_menu` VALUES (15911, 1, 321);
INSERT INTO `sys_role_menu` VALUES (15912, 1, 388);
INSERT INTO `sys_role_menu` VALUES (15913, 1, 389);
INSERT INTO `sys_role_menu` VALUES (15914, 1, 390);
INSERT INTO `sys_role_menu` VALUES (15915, 1, 343);
INSERT INTO `sys_role_menu` VALUES (15916, 1, 419);
INSERT INTO `sys_role_menu` VALUES (15917, 1, 420);
INSERT INTO `sys_role_menu` VALUES (15918, 1, 322);
INSERT INTO `sys_role_menu` VALUES (15919, 1, 393);
INSERT INTO `sys_role_menu` VALUES (15920, 1, 394);
INSERT INTO `sys_role_menu` VALUES (15921, 1, 395);
INSERT INTO `sys_role_menu` VALUES (15922, 1, 330);
INSERT INTO `sys_role_menu` VALUES (15923, 1, 331);
INSERT INTO `sys_role_menu` VALUES (15924, 1, 400);
INSERT INTO `sys_role_menu` VALUES (15925, 1, 401);
INSERT INTO `sys_role_menu` VALUES (15926, 1, 402);
INSERT INTO `sys_role_menu` VALUES (15927, 1, 403);
INSERT INTO `sys_role_menu` VALUES (15928, 1, 332);
INSERT INTO `sys_role_menu` VALUES (15929, 1, 404);
INSERT INTO `sys_role_menu` VALUES (15930, 1, 405);
INSERT INTO `sys_role_menu` VALUES (15931, 1, 406);
INSERT INTO `sys_role_menu` VALUES (15932, 1, 407);
INSERT INTO `sys_role_menu` VALUES (15933, 1, 334);
INSERT INTO `sys_role_menu` VALUES (15934, 1, 408);
INSERT INTO `sys_role_menu` VALUES (15935, 1, 409);
INSERT INTO `sys_role_menu` VALUES (15936, 1, 410);
INSERT INTO `sys_role_menu` VALUES (15937, 1, 411);
INSERT INTO `sys_role_menu` VALUES (15938, 1, 333);
INSERT INTO `sys_role_menu` VALUES (15939, 1, 412);
INSERT INTO `sys_role_menu` VALUES (15940, 1, 413);
INSERT INTO `sys_role_menu` VALUES (15941, 1, 414);
INSERT INTO `sys_role_menu` VALUES (15942, 1, 415);
INSERT INTO `sys_role_menu` VALUES (15943, 1, 380);
INSERT INTO `sys_role_menu` VALUES (15944, 1, 416);
INSERT INTO `sys_role_menu` VALUES (15945, 1, 417);
INSERT INTO `sys_role_menu` VALUES (15946, 1, 418);
INSERT INTO `sys_role_menu` VALUES (15947, 1, 340);
INSERT INTO `sys_role_menu` VALUES (15948, 1, 341);
INSERT INTO `sys_role_menu` VALUES (15949, 1, 421);
INSERT INTO `sys_role_menu` VALUES (15950, 1, 422);
INSERT INTO `sys_role_menu` VALUES (15951, 1, 342);
INSERT INTO `sys_role_menu` VALUES (15952, 1, 423);
INSERT INTO `sys_role_menu` VALUES (15953, 1, 344);
INSERT INTO `sys_role_menu` VALUES (15954, 1, 424);
INSERT INTO `sys_role_menu` VALUES (15955, 1, 345);
INSERT INTO `sys_role_menu` VALUES (15956, 1, 425);
INSERT INTO `sys_role_menu` VALUES (15957, 1, 346);
INSERT INTO `sys_role_menu` VALUES (15958, 1, 426);
INSERT INTO `sys_role_menu` VALUES (15959, 1, 351);
INSERT INTO `sys_role_menu` VALUES (15960, 1, 427);
INSERT INTO `sys_role_menu` VALUES (15961, 1, 428);
INSERT INTO `sys_role_menu` VALUES (15962, 1, 429);
INSERT INTO `sys_role_menu` VALUES (15963, 1, 430);
INSERT INTO `sys_role_menu` VALUES (15964, 1, 350);
INSERT INTO `sys_role_menu` VALUES (15965, 1, 353);
INSERT INTO `sys_role_menu` VALUES (15966, 1, 431);
INSERT INTO `sys_role_menu` VALUES (15967, 1, 432);
INSERT INTO `sys_role_menu` VALUES (15968, 1, 433);
INSERT INTO `sys_role_menu` VALUES (15969, 1, 434);
INSERT INTO `sys_role_menu` VALUES (15970, 1, 355);
INSERT INTO `sys_role_menu` VALUES (15971, 1, 435);
INSERT INTO `sys_role_menu` VALUES (15972, 1, 436);
INSERT INTO `sys_role_menu` VALUES (15973, 1, 356);
INSERT INTO `sys_role_menu` VALUES (15974, 1, 437);
INSERT INTO `sys_role_menu` VALUES (15975, 1, 438);
INSERT INTO `sys_role_menu` VALUES (15976, 1, 439);
INSERT INTO `sys_role_menu` VALUES (15977, 1, 440);
INSERT INTO `sys_role_menu` VALUES (15978, 1, 362);
INSERT INTO `sys_role_menu` VALUES (15979, 1, 441);
INSERT INTO `sys_role_menu` VALUES (15980, 1, 352);
INSERT INTO `sys_role_menu` VALUES (15981, 1, 442);
INSERT INTO `sys_role_menu` VALUES (15982, 1, 363);
INSERT INTO `sys_role_menu` VALUES (15983, 1, 447);
INSERT INTO `sys_role_menu` VALUES (15984, 1, 448);
INSERT INTO `sys_role_menu` VALUES (15985, 1, 449);
INSERT INTO `sys_role_menu` VALUES (15986, 1, 450);
INSERT INTO `sys_role_menu` VALUES (15987, 1, 361);
INSERT INTO `sys_role_menu` VALUES (15988, 1, 443);
INSERT INTO `sys_role_menu` VALUES (15989, 1, 444);
INSERT INTO `sys_role_menu` VALUES (15990, 1, 372);
INSERT INTO `sys_role_menu` VALUES (15991, 1, 375);
INSERT INTO `sys_role_menu` VALUES (15992, 1, 376);
INSERT INTO `sys_role_menu` VALUES (15993, 1, 377);
INSERT INTO `sys_role_menu` VALUES (15994, 1, 378);
INSERT INTO `sys_role_menu` VALUES (15995, 1, 126);
INSERT INTO `sys_role_menu` VALUES (15996, 1, 2);
INSERT INTO `sys_role_menu` VALUES (15997, 1, 15);
INSERT INTO `sys_role_menu` VALUES (15998, 1, 16);
INSERT INTO `sys_role_menu` VALUES (15999, 1, 17);
INSERT INTO `sys_role_menu` VALUES (16000, 1, 18);
INSERT INTO `sys_role_menu` VALUES (16001, 1, 3);
INSERT INTO `sys_role_menu` VALUES (16002, 1, 19);
INSERT INTO `sys_role_menu` VALUES (16003, 1, 20);
INSERT INTO `sys_role_menu` VALUES (16004, 1, 21);
INSERT INTO `sys_role_menu` VALUES (16005, 1, 22);
INSERT INTO `sys_role_menu` VALUES (16006, 1, 4);
INSERT INTO `sys_role_menu` VALUES (16007, 1, 23);
INSERT INTO `sys_role_menu` VALUES (16008, 1, 24);
INSERT INTO `sys_role_menu` VALUES (16009, 1, 25);
INSERT INTO `sys_role_menu` VALUES (16010, 1, 26);
INSERT INTO `sys_role_menu` VALUES (16011, 1, 370);
INSERT INTO `sys_role_menu` VALUES (16012, 1, 371);
INSERT INTO `sys_role_menu` VALUES (16013, 1, 379);
INSERT INTO `sys_role_menu` VALUES (16014, 1, 373);
INSERT INTO `sys_role_menu` VALUES (16015, 1, 374);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '密码',
  `salt` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '盐',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint(0) NULL DEFAULT NULL COMMENT '状态  0：禁用   1：正常',
  `create_user_id` bigint(0) NULL DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `inviter_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '1cc7f0f4e29524bfbfb2a299dde135e6ebad047b2c0ef70eceb7fbc198293927', 'YKJpTRPDurwTYVsIhd0P', 'root', '13612345678', 1, 1, '2016-11-11 11:11:11', NULL);

-- ----------------------------
-- Table structure for sys_user_address
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_address`;
CREATE TABLE `sys_user_address`  (
  `address_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户id',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户姓名',
  `user_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户地址',
  `lng` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '经度',
  `lat` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '纬度',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '省',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '市',
  `county` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '区/县',
  `address_details` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '详细地址',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `is_default` int(0) NULL DEFAULT NULL COMMENT '默认地址 0：非默认   1：默认',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`address_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 680 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户地址' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_address
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint(0) NULL DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户与角色对应关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (2, 4, 1);
INSERT INTO `sys_user_role` VALUES (3, 5, 1);
INSERT INTO `sys_user_role` VALUES (5, 7, 1);
INSERT INTO `sys_user_role` VALUES (7, 9, 1);
INSERT INTO `sys_user_role` VALUES (8, 8, 1);
INSERT INTO `sys_user_role` VALUES (10, 11, 1);
INSERT INTO `sys_user_role` VALUES (20, 10, 1);
INSERT INTO `sys_user_role` VALUES (22, 2, 1);
INSERT INTO `sys_user_role` VALUES (28, 6, 1);
INSERT INTO `sys_user_role` VALUES (29, 13, 1);
INSERT INTO `sys_user_role` VALUES (30, 12, 4);
INSERT INTO `sys_user_role` VALUES (31, 14, 1);
INSERT INTO `sys_user_role` VALUES (32, 15, 1);
INSERT INTO `sys_user_role` VALUES (33, 16, 1);

-- ----------------------------
-- Table structure for sys_user_token
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_token`;
CREATE TABLE `sys_user_token`  (
  `user_id` bigint(0) NOT NULL,
  `token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'token',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `token`(`token`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统用户Token' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_token
-- ----------------------------

-- ----------------------------
-- Table structure for tb_coupon
-- ----------------------------
DROP TABLE IF EXISTS `tb_coupon`;
CREATE TABLE `tb_coupon`  (
  `coupon_id` int(0) NOT NULL AUTO_INCREMENT,
  `coupon_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '优惠券名称',
  `coupon_picture` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '优惠券图片',
  `valid_days` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '有效期天数',
  `min_money` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠券可使用订单最低金额',
  `money` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠券抵扣金额',
  `delete_flag` int(0) NULL DEFAULT 0 COMMENT '是否删除 0未删除 -1已删除',
  `is_enable` int(0) NULL DEFAULT NULL COMMENT '是否启用 0未启用 1已启用',
  `coupon_type` int(0) NULL DEFAULT NULL COMMENT '优惠券类型 0全部可用 1活动优惠券',
  `max_receive` int(0) NULL DEFAULT NULL COMMENT '最多领取或购买数量(0为不限制数量)',
  `coupon_num` int(0) NULL DEFAULT NULL COMMENT '优惠券数量',
  PRIMARY KEY (`coupon_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '优惠券' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_coupon
-- ----------------------------
INSERT INTO `tb_coupon` VALUES (18, '新用户注册送优惠劵', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/20240701/66a7a2011c0a4428811c3da6c40ebce0.png', '1', 1.01, 1.00, 0, 1, NULL, 1, 93);
INSERT INTO `tb_coupon` VALUES (19, '新人注册优惠', 'https://tu.mianyazuju.com/2025/12/25/9f64055f-ea69-4a2c-b19a-7a1d1f7e1f44.png', '7', 100.00, 10.00, 0, 1, 1, 1, 94);
INSERT INTO `tb_coupon` VALUES (21, '扣完的优惠券', 'https://tu.mianyazuju.com/2026/01/08/17c7dacd-e37d-40c4-b6fc-2b5c627db4b5.jpg', '1', 1.01, 1.00, 0, 1, NULL, 0, 0);
INSERT INTO `tb_coupon` VALUES (22, '说说说', 'https://tu.mianyazuju.com/2026/01/08/82d25be5-80e7-4daa-8c59-015a38666bd3.jpg', '1', 1.02, 1.02, 0, 1, NULL, 0, 0);

-- ----------------------------
-- Table structure for tb_coupon_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_coupon_user`;
CREATE TABLE `tb_coupon_user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户id',
  `coupon_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '优惠券名称',
  `coupon_picture` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '优惠券图片',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '优惠券领取时间',
  `employ_time` datetime(0) NULL DEFAULT NULL COMMENT '优惠券使用时间',
  `expiration_time` datetime(0) NULL DEFAULT NULL COMMENT '优惠券过期时间',
  `min_money` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠券可使用订单最低金额',
  `money` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠券金额',
  `status` int(0) NULL DEFAULT NULL COMMENT '优惠券状态 0正常  1已使用  2已失效',
  `valid_days` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '有效天数',
  `coupon_id` int(0) NULL DEFAULT NULL COMMENT '绑定的优惠券id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 162 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户优惠券' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_coupon_user
-- ----------------------------

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `user_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '头像',
  `sex` int(0) NULL DEFAULT NULL COMMENT '性别 1男 2女',
  `age` int(0) NULL DEFAULT NULL COMMENT '年龄',
  `ma_open_id` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '微信小程序openId',
  `mp_open_id` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '微信公众号openId',
  `points` int(0) NULL DEFAULT 0 COMMENT '用户积分',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '密码',
  `salt` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '加密盐',
  `apple_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '苹果id',
  `sys_phone` int(0) NULL DEFAULT NULL COMMENT '手机类型 1安卓 2ios',
  `status` int(0) NULL DEFAULT 1 COMMENT '状态 1正常 2禁用',
  `platform` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '来源 APP 小程序  公众号',
  `cid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app推送id',
  `is_send_msg` int(0) NULL DEFAULT NULL COMMENT '是否推送消息',
  `wx_img` varchar(600) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '微信提现图片',
  `user_type` int(0) NULL DEFAULT 0 COMMENT '用户类型 0未认证 1普通用户 2企业用户',
  `is_vip` int(0) NULL DEFAULT 0 COMMENT '是否是会员(0不是 1是)',
  `message_notify_enabled` tinyint(0) NOT NULL DEFAULT 1 COMMENT '是否接受消息通知 1开启 0关闭',
  `vip_end_time` datetime(0) NULL DEFAULT NULL COMMENT 'vip到期时间',
  `invitation_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邀请码',
  `commission_rate` decimal(10, 2) NULL DEFAULT NULL COMMENT '佣金比例',
  `fei_rate` decimal(10, 2) NULL DEFAULT NULL COMMENT '非直属佣金',
  `publish_num` int(0) NULL DEFAULT 0 COMMENT '团游发布次数',
  `photo` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '背景图',
  `signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '个人签名',
  `inviter_user_id` bigint(0) NULL DEFAULT NULL COMMENT '邀请人用户id',
  `superior_invitation_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '上级邀请码',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `deposit` decimal(10, 2) NULL DEFAULT NULL COMMENT '保证金',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user
-- ----------------------------

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `contact_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系人',
  `contact_mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系电话',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '市',
  `district` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区',
  `street` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '街道',
  `detail_address` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '详细地址',
  `tag` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签：家/公司等',
  `is_default` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否默认 0否 1是',
  `del_flag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '逻辑删除 0正常 -1删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_address_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户地址' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_address
-- ----------------------------

-- ----------------------------
-- Table structure for user_certification
-- ----------------------------
DROP TABLE IF EXISTS `user_certification`;
CREATE TABLE `user_certification`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '用户实名认证id',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户id',
  `user_type` int(0) NULL DEFAULT NULL COMMENT '认证类型  1普通用户 2企业用户',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '真实姓名|法人姓名',
  `identity_card_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '证件号码|法人身份证',
  `enterprise_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '企业名称',
  `enterprise_phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '企业联系电话',
  `business_license` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '营业执照',
  `business_license_img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '营业执照图片',
  `status` int(0) NULL DEFAULT NULL COMMENT '0待审核 1已通过 2未通过',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '审核原因',
  `identity_card_front` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '身份证正面图片',
  `identity_card_verso` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '身份证反面图片',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `enterprise_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '个人详细地址|企业地址',
  `is_submit` int(0) NULL DEFAULT 0 COMMENT '是否允许提交0是 1否',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '实名认证' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_certification
-- ----------------------------

-- ----------------------------
-- Table structure for user_device
-- ----------------------------
DROP TABLE IF EXISTS `user_device`;
CREATE TABLE `user_device`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `device_id` bigint(0) NOT NULL COMMENT '设备ID',
  `custom_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '自定义名称',
  `is_default` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否默认 1是 0否',
  `bind_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '绑定时间',
  `unbind_time` datetime(0) NULL DEFAULT NULL COMMENT '解绑时间',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '状态 1已绑定 0已解绑',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `del_flag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_device_user`(`user_id`) USING BTREE,
  INDEX `idx_user_device_default`(`user_id`, `is_default`, `status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户设备绑定' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_device
-- ----------------------------

-- ----------------------------
-- Table structure for user_feedback
-- ----------------------------
DROP TABLE IF EXISTS `user_feedback`;
CREATE TABLE `user_feedback`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '反馈类型',
  `contact` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '联系方式',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '反馈内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '图片JSON数组',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'PENDING' COMMENT '状态',
  `reply_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '回复内容',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `del_flag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_feedback_user`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户反馈' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_feedback
-- ----------------------------

-- ----------------------------
-- Table structure for user_member
-- ----------------------------
DROP TABLE IF EXISTS `user_member`;
CREATE TABLE `user_member`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '成员ID',
  `user_id` bigint(0) NOT NULL COMMENT '所属用户ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '成员姓名',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像',
  `gender` tinyint(0) NULL DEFAULT 0 COMMENT '性别',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `height` decimal(5, 2) NULL DEFAULT NULL COMMENT '身高',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重',
  `relation` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '关系',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `del_flag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_member_user`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户成员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_member
-- ----------------------------

-- ----------------------------
-- Table structure for user_money
-- ----------------------------
DROP TABLE IF EXISTS `user_money`;
CREATE TABLE `user_money`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '钱包id',
  `money` decimal(10, 2) NULL DEFAULT NULL COMMENT '钱包金额',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户钱包' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_money
-- ----------------------------
INSERT INTO `user_money` VALUES (12, 10000000.00, 15);
INSERT INTO `user_money` VALUES (13, 9999999.00, 16);
INSERT INTO `user_money` VALUES (14, 9999999.00, 17);
INSERT INTO `user_money` VALUES (15, 0.00, 18);
INSERT INTO `user_money` VALUES (16, 9999999.00, 19);
INSERT INTO `user_money` VALUES (17, 9987089.00, 20);
INSERT INTO `user_money` VALUES (18, 9999999.00, 21);
INSERT INTO `user_money` VALUES (19, 10261569.20, 22);
INSERT INTO `user_money` VALUES (20, 9999997.36, 23);
INSERT INTO `user_money` VALUES (21, 0.00, 24);
INSERT INTO `user_money` VALUES (22, 0.00, 25);
INSERT INTO `user_money` VALUES (23, 0.00, 26);
INSERT INTO `user_money` VALUES (24, 0.00, 27);
INSERT INTO `user_money` VALUES (25, 0.00, 28);
INSERT INTO `user_money` VALUES (26, 0.00, 29);
INSERT INTO `user_money` VALUES (28, 17140.00, 31);
INSERT INTO `user_money` VALUES (29, 1300.00, 32);
INSERT INTO `user_money` VALUES (30, 100.00, 33);
INSERT INTO `user_money` VALUES (31, 98039.00, 34);
INSERT INTO `user_money` VALUES (32, 500.00, 35);
INSERT INTO `user_money` VALUES (34, 356.02, 37);
INSERT INTO `user_money` VALUES (35, 0.00, 38);
INSERT INTO `user_money` VALUES (36, 999.94, 39);
INSERT INTO `user_money` VALUES (37, 0.00, 40);
INSERT INTO `user_money` VALUES (38, 0.00, 41);
INSERT INTO `user_money` VALUES (39, 0.00, 42);
INSERT INTO `user_money` VALUES (40, 0.00, 43);

-- ----------------------------
-- Table structure for user_money_details
-- ----------------------------
DROP TABLE IF EXISTS `user_money_details`;
CREATE TABLE `user_money_details`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '钱包详情id',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户id',
  `by_user_id` int(0) NULL DEFAULT NULL COMMENT '邀请用户id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标题',
  `classify` int(0) NULL DEFAULT NULL COMMENT '用途 1购买会员 2支付订单',
  `type` int(0) NULL DEFAULT NULL COMMENT '类型 1增加 2减少',
  `state` int(0) NULL DEFAULT 1 COMMENT '状态 1待支付 2已到账 3取消',
  `money` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '内容',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `user_level` int(0) NULL DEFAULT NULL COMMENT '1一级 2二级',
  `relation_id` int(0) NULL DEFAULT NULL COMMENT '关联id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1779 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '钱包明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_money_details
-- ----------------------------
INSERT INTO `user_money_details` VALUES (1660, 33, NULL, '退保证金', NULL, 1, 2, 100.00, '退保证金: 100', '2025-12-24 20:27:05', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1661, 32, NULL, '系统增加金额', NULL, 1, 2, 1.00, '系统增加金额: 1', '2025-12-24 20:42:21', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1662, 32, NULL, '系统减少金额', NULL, 2, 2, 1.00, '系统减少金额: 1', '2025-12-24 20:42:33', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1663, 32, NULL, '订单结算', NULL, 1, 2, 180.00, '订单【202512231128284018】结算, 金额【180.00】', NULL, NULL, NULL);
INSERT INTO `user_money_details` VALUES (1664, 32, NULL, '用户提现', 3, 2, 1, 100.00, '用户发起提现，金额: 100', NULL, NULL, NULL);
INSERT INTO `user_money_details` VALUES (1665, 32, NULL, '用户提现', 3, 2, 1, 200.00, '用户发起提现，金额: 200', NULL, NULL, NULL);
INSERT INTO `user_money_details` VALUES (1666, 35, NULL, '系统增加金额', NULL, 1, 2, 180.00, '系统增加金额: 180', '2025-12-25 19:59:57', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1667, 32, NULL, '用户提现', 3, 2, 1, 10.00, '用户发起提现，金额: 10', NULL, NULL, NULL);
INSERT INTO `user_money_details` VALUES (1668, 37, NULL, '系统增加金额', NULL, 1, 2, 1000.00, '系统增加金额: 1000', '2025-12-26 17:41:14', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1669, 37, NULL, '系统增加金额', NULL, 1, 2, 1000.00, '系统增加金额: 1000', '2025-12-26 17:50:50', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1670, 37, NULL, '购买团游', 2, 2, 2, 1200.00, '购买团游花费【1200.00】元', '2025-12-26 17:51:06', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1671, 32, NULL, '订单结算', NULL, 1, 2, 1200.00, '订单【202512261748471762】结算, 金额【1200.00】', '2025-12-26 18:05:01', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1672, 32, NULL, '用户提现', 3, 2, 1, 100.00, '用户发起提现，金额: 100', '2025-12-26 18:06:07', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1673, 32, NULL, '用户提现', 3, 2, 1, 200.00, '用户发起提现，金额: 200', '2025-12-26 18:06:15', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1674, 37, NULL, '用户提现', 3, 2, 1, 20.00, '用户发起提现，金额: 20', '2025-12-26 18:10:31', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1675, 37, NULL, '用户提现', 3, 2, 1, 30.00, '用户发起提现，金额: 30', '2025-12-26 18:12:26', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1676, 37, NULL, '用户提现', 3, 2, 1, 10.00, '用户发起提现，金额: 10', '2025-12-26 18:17:08', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1677, 37, NULL, '用户提现', 3, 2, 1, 20.00, '用户发起提现，金额: 20', '2025-12-26 18:17:14', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1678, 37, NULL, '用户提现', 3, 2, 1, 10.00, '用户发起提现，金额: 10', '2025-12-26 18:27:03', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1679, 37, NULL, '用户提现', 3, 2, 1, 20.00, '用户发起提现，金额: 20', '2025-12-26 18:27:08', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1680, 37, NULL, '用户提现', 3, 2, 1, 50.00, '用户发起提现，金额: 50', '2025-12-26 18:48:00', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1681, 37, NULL, '用户提现', 3, 2, 1, 80.00, '用户发起提现，金额: 80', '2025-12-26 18:48:05', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1682, 37, NULL, '用户提现', 3, 2, 1, 30.00, '用户发起提现，金额: 30', '2025-12-26 18:59:17', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1683, 37, NULL, '用户提现', 3, 2, 1, 40.00, '用户发起提现，金额: 40', '2025-12-26 18:59:22', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1684, 37, NULL, '用户提现', 3, 2, 1, 12.00, '用户发起提现，金额: 12', '2025-12-26 19:01:39', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1685, 37, NULL, '用户提现', 3, 2, 1, 20.00, '用户发起提现，金额: 20', '2025-12-26 19:02:18', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1686, 37, NULL, '用户提现', 3, 2, 1, 20.00, '用户发起提现，金额: 20', '2025-12-26 19:03:54', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1687, 37, NULL, '用户提现', 3, 2, 1, 10.00, '用户发起提现，金额: 10', '2025-12-26 19:08:06', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1688, 37, NULL, '系统增加金额', NULL, 1, 2, 500.00, '系统增加金额: 500', '2025-12-26 19:28:21', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1689, 32, NULL, '购买团游', 2, 2, 2, 600.00, '购买团游花费【600.00】元', '2025-12-26 19:53:35', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1690, 32, NULL, '购买团游', 2, 2, 2, 590.00, '购买团游花费【590.00】元', '2025-12-26 19:55:35', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1691, 22, NULL, '购买团游', 2, 2, 2, 96.92, '购买团游花费【96.92】元', '2025-12-26 20:12:07', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1692, 37, NULL, '购买团游', 2, 2, 2, 585.99, '购买团游花费【585.99】元', '2025-12-26 20:31:39', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1693, 37, NULL, '系统增加金额', NULL, 1, 2, 600.00, '系统增加金额: 600', '2025-12-26 20:40:18', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1694, 37, NULL, '购买团游', 2, 2, 2, 585.99, '购买团游花费【585.99】元', '2025-12-26 20:40:30', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1695, 32, NULL, '订单结算', NULL, 1, 2, 360.00, '订单【202512241657307323】结算, 金额【360.00】', '2025-12-27 00:00:02', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1696, 32, NULL, '订单结算', NULL, 1, 2, 585.99, '订单【202512262039501375】结算, 金额【585.99】', '2025-12-27 20:45:01', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1697, 22, NULL, '购买团游', 2, 2, 2, 100.00, '购买团游花费【100.00】元', '2025-12-29 16:47:27', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1698, 22, NULL, '订单结算', NULL, 1, 2, 96.92, '订单【202512262012067245】结算, 金额【96.92】', '2025-12-30 16:45:01', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1699, 22, NULL, '订单结算', NULL, 1, 2, 100.00, '订单【202512291647262816】结算, 金额【100.00】', '2025-12-30 16:50:00', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1700, 32, NULL, '订单结算', NULL, 1, 2, 600.00, '订单【202512261953342557】结算, 金额【600.00】', '2025-12-31 23:20:01', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1701, 32, NULL, '订单结算', NULL, 1, 2, 590.00, '订单【202512261955186817】结算, 金额【590.00】', '2025-12-31 23:20:01', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1702, 32, NULL, '订单结算', NULL, 1, 2, 585.99, '订单【202512262031388554】结算, 金额【585.99】', '2025-12-31 23:20:01', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1703, 22, NULL, '订单结算', NULL, 1, 2, 2380.00, '订单【202512241611176775】结算, 金额【2380.00】', '2026-01-04 00:10:03', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1704, 39, NULL, '系统增加金额', NULL, 1, 2, 1000.00, '系统增加金额: 1000', '2026-01-06 10:17:40', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1705, 39, NULL, '退保证金', NULL, 1, 2, 100.00, '退保证金: 100', '2026-01-06 10:19:16', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1706, 39, NULL, '系统减少金额', NULL, 2, 2, 1100.00, '系统减少金额: 1100', '2026-01-06 10:19:45', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1707, 39, NULL, '系统增加金额', NULL, 1, 2, 1000.00, '系统增加金额: 1000', '2026-01-06 14:09:07', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1708, 39, NULL, '购买团游', 2, 2, 2, 989.97, '购买团游花费【989.97】元', '2026-01-06 14:09:22', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1709, 39, NULL, '退款', 3, 1, 1, 989.97, '退款: 989.97', '2026-01-06 16:33:49', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1710, 39, NULL, '违约赔付', NULL, 2, 2, 691.98, '违约赔付, 金额: [691.9760], 订单号: [202601061408452797]', '2026-01-06 16:33:50', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1711, 39, NULL, '商家违约赔付', NULL, 1, 2, 791.98, '商家违约赔付, 金额: [791.9760], 订单号: [202601061408452797]', '2026-01-06 16:33:50', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1712, 39, NULL, '购买团游', 2, 2, 2, 500.00, '购买团游花费【500.00】元', '2026-01-06 16:36:15', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1713, 39, NULL, '退款', 3, 1, 1, 100.00, '退款: 100.0000', '2026-01-06 16:36:45', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1714, 39, NULL, '购买团游', 2, 2, 2, 500.00, '购买团游花费【500.00】元', '2026-01-06 16:38:36', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1715, 32, NULL, '系统减少金额', NULL, 2, 2, 2201.98, '系统减少金额: 2201.98', '2026-01-06 17:44:56', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1716, 39, NULL, '退款', 3, 1, 1, 100.00, '退款: 100.00', '2026-01-07 10:17:07', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1717, 32, NULL, '购买团游', 2, 2, 2, 500.00, '购买团游花费【500.00】元', '2026-01-07 10:32:27', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1718, 32, NULL, '退款', 3, 1, 1, 500.00, '退款: 500.00', '2026-01-07 10:43:42', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1719, 32, NULL, '商家违约赔付', NULL, 1, 2, 150.00, '商家违约赔付, 金额: [150.00], 订单号: [202601071032255248]', '2026-01-07 10:43:42', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1720, 32, NULL, '购买团游', 2, 2, 2, 500.00, '购买团游花费【500.00】元', '2026-01-07 10:45:56', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1721, 32, NULL, '退款', 3, 1, 1, 500.00, '退款: 500.00', '2026-01-07 10:46:12', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1722, 39, NULL, '违约赔付', NULL, 2, 2, 100.00, '违约赔付, 金额: [100.00], 订单号: [202601071045555767]', '2026-01-07 10:46:12', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1723, 32, NULL, '商家违约赔付', NULL, 1, 2, 150.00, '商家违约赔付, 金额: [150.00], 订单号: [202601071045555767]', '2026-01-07 10:46:13', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1724, 39, NULL, '系统增加金额', NULL, 1, 2, 100.00, '系统增加金额: 100', '2026-01-07 10:59:31', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1725, 32, NULL, '购买团游', 2, 2, 2, 500.00, '购买团游花费【500.00】元', '2026-01-07 11:00:04', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1726, 32, NULL, '退款', 3, 1, 1, 500.00, '退款: 500.00', '2026-01-07 11:00:15', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1727, 39, NULL, '违约赔付', NULL, 2, 2, 100.00, '违约赔付, 金额: [100.00], 订单号: [202601071100011849]', '2026-01-07 11:00:15', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1728, 32, NULL, '商家违约赔付', NULL, 1, 2, 150.00, '商家违约赔付, 金额: [150.00], 订单号: [202601071100011849]', '2026-01-07 11:00:16', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1729, 39, NULL, '系统减少金额', NULL, 2, 2, 50.00, '系统减少金额: 50', '2026-01-07 11:01:06', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1730, 39, NULL, '系统减少金额', NULL, 2, 2, 120.00, '系统减少金额: 120', '2026-01-07 11:01:17', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1731, 32, NULL, '购买团游', 2, 2, 2, 500.00, '购买团游花费【500.00】元', '2026-01-07 11:01:57', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1732, 32, NULL, '退款', 3, 1, 1, 500.00, '退款: 500.00', '2026-01-07 11:02:23', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1733, 39, NULL, '违约赔付', NULL, 2, 2, 30.00, '违约赔付, 金额: [30.00], 订单号: [202601071101553192]', '2026-01-07 11:02:24', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1734, 32, NULL, '商家违约赔付', NULL, 1, 2, 150.00, '商家违约赔付, 金额: [150.00], 订单号: [202601071101553192]', '2026-01-07 11:02:24', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1735, 32, NULL, '购买团游', 2, 2, 2, 500.00, '购买团游花费【500.00】元', '2026-01-07 11:22:10', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1736, 32, NULL, '购买团游', 2, 2, 2, 500.00, '购买团游花费【500.00】元', '2026-01-07 19:35:04', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1737, 32, NULL, '购买团游', 2, 2, 2, 500.00, '购买团游花费【500.00】元', '2026-01-08 09:27:33', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1738, 32, NULL, '退款', 3, 1, 1, 100.00, '退款: 100.00', '2026-01-08 09:53:49', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1739, 32, NULL, '系统增加金额', NULL, 1, 2, 300.00, '系统增加金额: 300', '2026-01-08 10:02:22', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1740, 32, NULL, '购买团游', 2, 2, 2, 500.00, '购买团游花费【500.00】元', '2026-01-08 10:02:40', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1741, 32, NULL, '退款', 3, 1, 1, 500.00, '退款: 500.00', '2026-01-08 10:05:14', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1742, 32, NULL, '商家违约赔付', NULL, 1, 2, 150.00, '商家违约赔付, 金额: [150.00], 订单号: [202601080957493313]', '2026-01-08 10:05:15', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1743, 32, NULL, '退款', 3, 1, 1, 500.00, '退款: 500.00', '2026-01-08 10:14:07', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1744, 32, NULL, '商家违约赔付', NULL, 1, 2, 150.00, '商家违约赔付, 金额: [150.00], 订单号: [202601071935025846]', '2026-01-08 10:14:09', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1745, 39, NULL, '订单结算', NULL, 1, 2, 500.00, '订单【202601071122091275】结算, 金额【500.00】', '2026-01-08 11:30:00', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1746, 39, NULL, '订单结算', NULL, 1, 2, 500.00, '订单【202601071122091275】结算, 金额【500.00】', '2026-01-08 11:30:01', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1747, 22, NULL, '购买团游', 2, 2, 2, 0.01, '购买团游花费【0.01】元', '2026-01-08 14:11:20', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1748, 22, NULL, '购买团游', 2, 2, 2, 0.01, '购买团游花费【0.01】元', '2026-01-08 14:12:07', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1749, 22, NULL, '购买团游', 2, 2, 2, 0.01, '购买团游花费【0.01】元', '2026-01-08 14:16:10', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1750, 22, NULL, '购买团游', 2, 2, 2, 2.00, '购买团游花费【2.00】元', '2026-01-08 15:24:40', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1751, 22, NULL, '充值余额', 2, 1, 2, 0.01, '充值余额【0.01】元', '2026-01-08 15:32:25', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1752, 22, NULL, '购买团游', 2, 2, 2, 0.01, '购买团游花费【0.01】元', '2026-01-08 16:11:48', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1753, 39, NULL, '充值余额', 2, 1, 2, 0.01, '充值余额【0.01】元', '2026-01-08 16:41:48', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1754, 22, NULL, '充值余额', 2, 1, 2, 0.01, '充值余额【0.01】元', '2026-01-08 16:57:45', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1755, 39, NULL, '购买团游', 2, 2, 2, 0.01, '购买团游花费【0.01】元', '2026-01-08 17:23:22', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1756, 39, NULL, '购买团游', 2, 2, 2, 0.01, '购买团游花费【0.01】元', '2026-01-08 17:24:40', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1757, 39, NULL, '商家违约赔付', NULL, 1, 2, 0.00, '商家违约赔付, 金额: [0.01], 订单号: [202601081724334371]', '2026-01-08 17:25:00', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1758, 39, NULL, '购买团游', 2, 2, 2, 0.05, '购买团游花费【0.05】元', '2026-01-08 17:30:19', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1759, 39, NULL, '商家违约赔付', NULL, 1, 2, 0.02, '商家违约赔付, 金额: [0.02], 订单号: [202601081730098349]', '2026-01-08 17:35:21', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1760, 39, NULL, '商家违约赔付', NULL, 1, 2, 0.00, '商家违约赔付, 金额: [0.01], 订单号: [202601081723091585]', '2026-01-08 17:39:39', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1761, 39, NULL, '购买团游', 2, 2, 2, 0.01, '购买团游花费【0.01】元', '2026-01-09 09:52:35', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1762, 39, NULL, '购买团游', 2, 2, 2, 0.01, '购买团游花费【0.01】元', '2026-01-09 09:53:43', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1763, 39, NULL, '商家违约赔付', NULL, 1, 2, 0.00, '商家违约赔付, 金额: [0.01], 订单号: [202601090953356916]', '2026-01-09 10:28:44', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1764, 39, NULL, '商家违约赔付', NULL, 1, 2, 0.00, '商家违约赔付, 金额: [0.01], 订单号: [202601090952271629]', '2026-01-09 10:28:50', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1765, 39, NULL, '商家违约赔付', NULL, 1, 2, 0.00, '商家违约赔付, 金额: [0.01], 订单号: [202601091029324701]', '2026-01-09 10:30:04', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1766, 22, NULL, '购买团游', 2, 2, 2, 1.02, '购买团游花费【1.02】元', '2026-01-09 16:59:36', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1767, 22, NULL, '退款', 3, 1, 1, 1.02, '退款: 1.02', '2026-01-09 17:01:22', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1768, 22, NULL, '商家违约赔付', NULL, 1, 2, 0.10, '商家违约赔付, 金额: [0.11], 订单号: [202601091659356447]', '2026-01-09 17:01:22', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1769, 22, NULL, '商家违约赔付', NULL, 1, 2, 0.00, '商家违约赔付, 金额: [0.01], 订单号: [202601081611245251]', '2026-01-09 17:01:45', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1770, 22, NULL, '商家违约赔付', NULL, 1, 2, 0.00, '商家违约赔付, 金额: [0.01], 订单号: [202601081411111054]', '2026-01-09 17:02:29', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1771, 23, NULL, '购买团游', 2, 2, 2, 1.02, '购买团游花费【1.02】元', '2026-01-09 17:03:51', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1772, 23, NULL, '购买团游', 2, 2, 2, 1.02, '购买团游花费【1.02】元', '2026-01-09 17:03:52', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1773, 23, NULL, '退款', 3, 1, 1, 0.71, '退款: 0.71', '2026-01-09 17:05:13', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1774, 23, NULL, '退款', 3, 1, 1, 0.71, '退款: 0.71', '2026-01-09 17:06:11', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1775, 23, NULL, '购买团游', 2, 2, 2, 1.02, '购买团游花费【1.02】元', '2026-01-09 17:12:22', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1776, 22, NULL, '订单结算', NULL, 1, 2, 0.01, '订单【202601081337544147】结算, 金额【0.01】', '2026-01-17 21:00:01', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1777, 22, NULL, '订单结算', NULL, 1, 2, 0.01, '订单【202601081401521365】结算, 金额【0.01】', '2026-01-17 21:00:02', NULL, NULL);
INSERT INTO `user_money_details` VALUES (1778, 22, NULL, '订单结算', NULL, 1, 2, 1.02, '订单【202601091712201895】结算, 金额【1.02】', '2026-01-17 21:00:02', NULL, NULL);

-- ----------------------------
-- Table structure for user_points_detail
-- ----------------------------
DROP TABLE IF EXISTS `user_points_detail`;
CREATE TABLE `user_points_detail`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标题',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '内容',
  `points` int(0) NULL DEFAULT NULL COMMENT '积分',
  `type` tinyint(1) NOT NULL COMMENT '类型 1: 增加 2: 减少',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 97 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '积分明细' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_points_detail
-- ----------------------------
INSERT INTO `user_points_detail` VALUES (1, 16, '完成任务', '完成任务[发布动态]获得积分[200]', 200, 1, '2025-07-23 17:20:04', NULL);
INSERT INTO `user_points_detail` VALUES (2, 16, '完成任务', '完成任务[发布动态]获得积分[200]', 200, 1, '2025-07-24 09:11:12', NULL);
INSERT INTO `user_points_detail` VALUES (3, 16, '完成任务', '完成任务[分享动态]获得积分[100]', 100, 1, '2025-08-18 13:50:44', NULL);
INSERT INTO `user_points_detail` VALUES (4, 20, '签到', '签到获得: 1积分', 1, 1, '2025-11-17 17:14:23', NULL);
INSERT INTO `user_points_detail` VALUES (5, 22, '签到', '签到获得: 1积分', 1, 1, '2025-11-18 17:02:07', NULL);
INSERT INTO `user_points_detail` VALUES (6, 22, '签到', '签到获得: 1积分', 1, 1, '2025-11-25 14:41:05', NULL);
INSERT INTO `user_points_detail` VALUES (7, 22, '签到', '签到获得: 2积分', 2, 1, '2025-11-25 14:42:46', NULL);
INSERT INTO `user_points_detail` VALUES (8, 22, '签到', '签到获得: 1积分', 1, 1, '2025-11-25 14:43:43', NULL);
INSERT INTO `user_points_detail` VALUES (9, 22, '签到', '签到获得: 2积分', 2, 1, '2025-11-26 10:08:20', NULL);
INSERT INTO `user_points_detail` VALUES (10, 20, '签到', '签到获得: 1积分', 1, 1, '2025-12-01 16:08:45', NULL);
INSERT INTO `user_points_detail` VALUES (11, 20, '完成任务', '完成任务[分享团游]获得积分[100]', 100, 1, '2025-12-01 16:46:42', NULL);
INSERT INTO `user_points_detail` VALUES (12, 22, '完成任务', '完成任务[分享动态]获得积分[100]', 100, 1, '2025-12-01 17:39:09', NULL);
INSERT INTO `user_points_detail` VALUES (13, 22, '完成任务', '完成任务[分享动态]获得积分[100]', 100, 1, '2025-12-02 10:09:36', NULL);
INSERT INTO `user_points_detail` VALUES (14, 20, '完成任务', '完成任务[分享团游]获得积分[100]', 100, 1, '2025-12-02 10:33:16', NULL);
INSERT INTO `user_points_detail` VALUES (15, 22, '签到', '签到获得: 1积分', 1, 1, '2025-12-18 11:20:09', NULL);
INSERT INTO `user_points_detail` VALUES (16, 32, '签到', '签到获得: 1积分', 1, 1, '2025-12-19 10:43:30', NULL);
INSERT INTO `user_points_detail` VALUES (17, 32, '签到', '签到获得: 1积分', 1, 1, '2025-12-22 10:50:09', NULL);
INSERT INTO `user_points_detail` VALUES (18, 32, '签到', '签到获得: 1积分', 1, 1, '2025-12-24 10:53:18', NULL);
INSERT INTO `user_points_detail` VALUES (19, 35, '签到', '签到获得: 1积分', 1, 1, '2025-12-25 09:30:35', NULL);
INSERT INTO `user_points_detail` VALUES (20, 35, '完成任务', '完成任务[发布动态]获得积分[200]', 200, 1, '2025-12-25 09:32:23', NULL);
INSERT INTO `user_points_detail` VALUES (21, 37, '签到', '签到获得: 1积分', 1, 1, '2025-12-26 19:24:09', NULL);
INSERT INTO `user_points_detail` VALUES (22, 37, '完成任务', '完成任务[发布动态]获得积分[200]', 200, 1, '2025-12-26 19:26:55', NULL);
INSERT INTO `user_points_detail` VALUES (23, 37, '完成任务', '完成任务[报名参加团游]获得积分[100]', 100, 1, '2025-12-26 19:36:37', NULL);
INSERT INTO `user_points_detail` VALUES (24, 37, '完成任务', '完成任务[分享动态]获得积分[100]', 100, 1, '2025-12-26 19:46:14', NULL);
INSERT INTO `user_points_detail` VALUES (25, 22, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[208]', 208, 2, '2025-12-26 20:12:06', NULL);
INSERT INTO `user_points_detail` VALUES (26, 37, '支付订单', '支付报名团游[拉萨朝圣地2日纯游玩]扣除积分[401]', 401, 2, '2025-12-26 20:30:26', NULL);
INSERT INTO `user_points_detail` VALUES (27, 37, '取消订单', '取消订单回退积分[401]', 401, 1, '2025-12-26 20:31:03', NULL);
INSERT INTO `user_points_detail` VALUES (28, 37, '支付订单', '支付报名团游[拉萨朝圣地2日纯游玩]扣除积分[401]', 401, 2, '2025-12-26 20:36:44', NULL);
INSERT INTO `user_points_detail` VALUES (29, 37, '取消订单', '取消订单回退积分[401]', 401, 1, '2025-12-26 20:37:17', NULL);
INSERT INTO `user_points_detail` VALUES (30, 37, '支付订单', '支付报名团游[拉萨朝圣地2日纯游玩]扣除积分[401]', 401, 2, '2025-12-26 20:39:51', NULL);
INSERT INTO `user_points_detail` VALUES (31, 39, '签到', '签到获得: 1积分', 1, 1, '2026-01-05 14:47:36', NULL);
INSERT INTO `user_points_detail` VALUES (32, 22, '签到', '签到获得: 1积分', 1, 1, '2026-01-05 15:36:13', NULL);
INSERT INTO `user_points_detail` VALUES (34, 36, '签到', '签到获得: 1积分', 1, 1, '2026-01-06 10:10:49', NULL);
INSERT INTO `user_points_detail` VALUES (35, 32, '签到', '签到获得: 1积分', 1, 1, '2026-01-06 10:56:30', NULL);
INSERT INTO `user_points_detail` VALUES (36, 22, '签到', '签到获得: 2积分', 2, 1, '2026-01-06 11:23:38', NULL);
INSERT INTO `user_points_detail` VALUES (37, 39, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[3]', 3, 2, '2026-01-06 11:37:46', NULL);
INSERT INTO `user_points_detail` VALUES (38, 39, '取消订单', '取消订单回退积分[3]', 3, 1, '2026-01-06 11:45:01', NULL);
INSERT INTO `user_points_detail` VALUES (39, 34, '签到', '签到获得: 1积分', 1, 1, '2026-01-06 13:13:17', NULL);
INSERT INTO `user_points_detail` VALUES (40, 24, '签到', '签到获得: 1积分', 1, 1, '2026-01-06 13:23:35', NULL);
INSERT INTO `user_points_detail` VALUES (41, 22, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[3]', 3, 2, '2026-01-06 14:05:52', NULL);
INSERT INTO `user_points_detail` VALUES (42, 22, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[3]', 3, 2, '2026-01-06 14:06:12', NULL);
INSERT INTO `user_points_detail` VALUES (43, 39, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[3]', 3, 2, '2026-01-06 14:08:46', NULL);
INSERT INTO `user_points_detail` VALUES (44, 22, '取消订单', '取消订单回退积分[3]', 3, 1, '2026-01-06 14:10:01', NULL);
INSERT INTO `user_points_detail` VALUES (45, 22, '取消订单', '取消订单回退积分[3]', 3, 1, '2026-01-06 14:10:01', NULL);
INSERT INTO `user_points_detail` VALUES (46, 39, '取消订单', '取消订单回退积分[3]', 3, 1, '2026-01-06 16:33:49', NULL);
INSERT INTO `user_points_detail` VALUES (47, 39, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[3]', 3, 2, '2026-01-06 16:36:14', NULL);
INSERT INTO `user_points_detail` VALUES (48, 39, '签到', '签到获得: 2积分', 2, 1, '2026-01-06 17:32:34', NULL);
INSERT INTO `user_points_detail` VALUES (49, 39, '签到', '签到获得: 1积分', 1, 1, '2026-01-06 17:43:01', NULL);
INSERT INTO `user_points_detail` VALUES (50, 32, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[4]', 4, 2, '2026-01-07 10:32:26', NULL);
INSERT INTO `user_points_detail` VALUES (51, 32, '取消订单', '取消订单回退积分[4]', 4, 1, '2026-01-07 10:43:41', NULL);
INSERT INTO `user_points_detail` VALUES (52, 32, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[4]', 4, 2, '2026-01-07 10:45:55', NULL);
INSERT INTO `user_points_detail` VALUES (53, 32, '取消订单', '取消订单回退积分[4]', 4, 1, '2026-01-07 10:46:11', NULL);
INSERT INTO `user_points_detail` VALUES (54, 32, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[4]', 4, 2, '2026-01-07 11:00:02', NULL);
INSERT INTO `user_points_detail` VALUES (55, 32, '取消订单', '取消订单回退积分[4]', 4, 1, '2026-01-07 11:00:15', NULL);
INSERT INTO `user_points_detail` VALUES (56, 32, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[4]', 4, 2, '2026-01-07 11:01:56', NULL);
INSERT INTO `user_points_detail` VALUES (57, 32, '取消订单', '取消订单回退积分[4]', 4, 1, '2026-01-07 11:02:23', NULL);
INSERT INTO `user_points_detail` VALUES (58, 32, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[4]', 4, 2, '2026-01-07 11:22:09', NULL);
INSERT INTO `user_points_detail` VALUES (59, 42, '签到', '签到获得: 1积分', 1, 1, '2026-01-07 14:38:48', NULL);
INSERT INTO `user_points_detail` VALUES (60, 32, '签到', '签到获得: 2积分', 2, 1, '2026-01-07 20:28:33', NULL);
INSERT INTO `user_points_detail` VALUES (61, 32, '签到', '签到获得: 3积分', 3, 1, '2026-01-08 09:26:35', NULL);
INSERT INTO `user_points_detail` VALUES (62, 32, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[5]', 5, 2, '2026-01-08 09:27:32', NULL);
INSERT INTO `user_points_detail` VALUES (63, 39, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[3]', 3, 2, '2026-01-08 10:27:48', NULL);
INSERT INTO `user_points_detail` VALUES (64, 39, '取消订单', '取消订单回退积分[3]', 3, 1, '2026-01-08 10:35:01', NULL);
INSERT INTO `user_points_detail` VALUES (65, 39, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[3]', 3, 2, '2026-01-08 10:40:48', NULL);
INSERT INTO `user_points_detail` VALUES (66, 39, '取消订单', '取消订单回退积分[3]', 3, 1, '2026-01-08 10:45:01', NULL);
INSERT INTO `user_points_detail` VALUES (67, 22, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[3]', 3, 2, '2026-01-08 10:52:08', NULL);
INSERT INTO `user_points_detail` VALUES (68, 22, '取消订单', '取消订单回退积分[3]', 3, 1, '2026-01-08 11:00:01', NULL);
INSERT INTO `user_points_detail` VALUES (69, 22, '取消订单', '取消订单回退积分[3]', 3, 1, '2026-01-08 11:00:01', NULL);
INSERT INTO `user_points_detail` VALUES (70, 22, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[6]', 6, 2, '2026-01-08 11:01:30', NULL);
INSERT INTO `user_points_detail` VALUES (71, 22, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[6]', 6, 2, '2026-01-08 11:01:32', NULL);
INSERT INTO `user_points_detail` VALUES (72, 22, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[6]', 6, 2, '2026-01-08 11:01:40', NULL);
INSERT INTO `user_points_detail` VALUES (73, 22, '取消订单', '取消订单回退积分[6]', 6, 1, '2026-01-08 11:05:01', NULL);
INSERT INTO `user_points_detail` VALUES (74, 22, '取消订单', '取消订单回退积分[6]', 6, 1, '2026-01-08 11:05:01', NULL);
INSERT INTO `user_points_detail` VALUES (75, 22, '取消订单', '取消订单回退积分[6]', 6, 1, '2026-01-08 11:05:01', NULL);
INSERT INTO `user_points_detail` VALUES (76, 22, '取消订单', '取消订单回退积分[6]', 6, 1, '2026-01-08 11:05:02', NULL);
INSERT INTO `user_points_detail` VALUES (77, 22, '取消订单', '取消订单回退积分[6]', 6, 1, '2026-01-08 11:05:02', NULL);
INSERT INTO `user_points_detail` VALUES (78, 22, '取消订单', '取消订单回退积分[6]', 6, 1, '2026-01-08 11:05:03', NULL);
INSERT INTO `user_points_detail` VALUES (79, 22, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[24]', 24, 2, '2026-01-08 13:16:14', NULL);
INSERT INTO `user_points_detail` VALUES (80, 22, '取消订单', '取消订单回退积分[24]', 24, 1, '2026-01-08 13:20:01', NULL);
INSERT INTO `user_points_detail` VALUES (81, 22, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[24]', 24, 2, '2026-01-08 13:20:58', NULL);
INSERT INTO `user_points_detail` VALUES (82, 22, '取消订单', '取消订单回退积分[24]', 24, 1, '2026-01-08 13:25:01', NULL);
INSERT INTO `user_points_detail` VALUES (83, 22, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[24]', 24, 2, '2026-01-08 13:37:54', NULL);
INSERT INTO `user_points_detail` VALUES (84, 22, '取消订单', '取消订单回退积分[24]', 24, 1, '2026-01-08 13:38:14', NULL);
INSERT INTO `user_points_detail` VALUES (85, 39, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[3]', 3, 2, '2026-01-08 17:23:10', NULL);
INSERT INTO `user_points_detail` VALUES (86, 39, '签到', '签到获得: 1积分', 1, 1, '2026-01-08 17:39:24', NULL);
INSERT INTO `user_points_detail` VALUES (87, 39, '取消订单', '取消订单回退积分[3]', 3, 1, '2026-01-08 17:39:37', NULL);
INSERT INTO `user_points_detail` VALUES (88, 39, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[4]', 4, 2, '2026-01-08 17:43:32', NULL);
INSERT INTO `user_points_detail` VALUES (89, 39, '取消订单', '取消订单回退积分[4]', 4, 1, '2026-01-08 17:50:01', NULL);
INSERT INTO `user_points_detail` VALUES (90, 39, '支付订单', '支付报名团游[「一眼千年·长安三日」3天2晚陕西精华纯玩团 — 兵马俑+华清宫+大雁塔+城墙+回民街]扣除积分[4]', 4, 2, '2026-01-08 18:02:36', NULL);
INSERT INTO `user_points_detail` VALUES (91, 39, '取消订单', '取消订单回退积分[4]', 4, 1, '2026-01-08 18:03:14', NULL);
INSERT INTO `user_points_detail` VALUES (92, 39, '签到', '签到获得: 2积分', 2, 1, '2026-01-09 09:42:07', NULL);
INSERT INTO `user_points_detail` VALUES (93, 39, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[6]', 6, 2, '2026-01-09 09:52:28', NULL);
INSERT INTO `user_points_detail` VALUES (94, 39, '取消订单', '取消订单回退积分[6]', 6, 1, '2026-01-09 10:28:48', NULL);
INSERT INTO `user_points_detail` VALUES (95, 39, '支付订单', '支付报名团游[新疆天山天池 + 吐鲁番 2 日纯玩跟团游]扣除积分[6]', 6, 2, '2026-01-09 10:29:32', NULL);
INSERT INTO `user_points_detail` VALUES (96, 39, '取消订单', '取消订单回退积分[6]', 6, 1, '2026-01-09 10:30:02', NULL);

-- ----------------------------
-- Table structure for user_sign_record
-- ----------------------------
DROP TABLE IF EXISTS `user_sign_record`;
CREATE TABLE `user_sign_record`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `sign_date` date NOT NULL COMMENT '签到日期',
  `coin_reward` int(0) NOT NULL DEFAULT 0 COMMENT '签到奖励金币',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_sign_record_user_date`(`user_id`, `sign_date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户签到记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_sign_record
-- ----------------------------
INSERT INTO `user_sign_record` VALUES (1, 44, '2026-07-09', 5, '2026-07-09 13:57:18');
INSERT INTO `user_sign_record` VALUES (2, 44, '2026-07-10', 5, '2026-07-10 09:16:39');
INSERT INTO `user_sign_record` VALUES (3, 45, '2026-07-14', 5, '2026-07-14 14:07:45');
INSERT INTO `user_sign_record` VALUES (4, 44, '2026-07-15', 5, '2026-07-15 13:48:25');
INSERT INTO `user_sign_record` VALUES (5, 44, '2026-07-16', 5, '2026-07-16 09:24:57');
INSERT INTO `user_sign_record` VALUES (6, 46, '2026-07-16', 5, '2026-07-16 15:46:22');
INSERT INTO `user_sign_record` VALUES (7, 44, '2026-07-17', 5, '2026-07-17 09:37:39');
INSERT INTO `user_sign_record` VALUES (8, 44, '2026-07-21', 5, '2026-07-21 14:05:48');
INSERT INTO `user_sign_record` VALUES (9, 46, '2026-07-22', 5, '2026-07-22 13:26:27');
INSERT INTO `user_sign_record` VALUES (10, 44, '2026-07-22', 5, '2026-07-22 13:44:01');
INSERT INTO `user_sign_record` VALUES (11, 44, '2026-07-23', 5, '2026-07-23 10:35:56');
INSERT INTO `user_sign_record` VALUES (12, 44, '2026-07-27', 5, '2026-07-27 14:51:29');
INSERT INTO `user_sign_record` VALUES (13, 45, '2026-07-30', 5, '2026-07-30 18:46:51');
INSERT INTO `user_sign_record` VALUES (14, 47, '2026-08-12', 5, '2026-08-12 19:39:19');
INSERT INTO `user_sign_record` VALUES (15, 47, '2026-08-13', 5, '2026-08-13 09:27:33');
INSERT INTO `user_sign_record` VALUES (16, 47, '2026-08-18', 5, '2026-08-18 10:14:30');
INSERT INTO `user_sign_record` VALUES (17, 47, '2026-09-28', 5, '2026-09-28 10:36:49');
INSERT INTO `user_sign_record` VALUES (18, 44, '2026-09-29', 5, '2026-09-29 19:15:20');

-- ----------------------------
-- Table structure for user_wallet
-- ----------------------------
DROP TABLE IF EXISTS `user_wallet`;
CREATE TABLE `user_wallet`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '余额',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_wallet_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户钱包' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_wallet
-- ----------------------------
INSERT INTO `user_wallet` VALUES (1, 44, 790.00, '2026-07-06 13:38:56', '2026-09-29 19:54:55');
INSERT INTO `user_wallet` VALUES (2, 45, 10.00, '2026-07-10 14:34:07', '2026-07-30 18:47:11');
INSERT INTO `user_wallet` VALUES (3, 46, 0.00, '2026-07-16 15:46:12', NULL);
INSERT INTO `user_wallet` VALUES (4, 37, 15.00, '2026-07-22 14:03:05', '2026-07-22 14:03:04');
INSERT INTO `user_wallet` VALUES (5, 47, 1419.00, '2026-08-12 19:37:59', '2026-09-28 13:42:01');
INSERT INTO `user_wallet` VALUES (6, 48, 0.00, '2026-08-13 09:36:07', NULL);
INSERT INTO `user_wallet` VALUES (7, 49, 0.00, '2026-09-23 15:00:15', NULL);
INSERT INTO `user_wallet` VALUES (8, 50, 300.00, '2026-09-29 17:57:28', '2026-09-29 19:06:35');

-- ----------------------------
-- Table structure for user_wallet_transaction
-- ----------------------------
DROP TABLE IF EXISTS `user_wallet_transaction`;
CREATE TABLE `user_wallet_transaction`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '交易记录ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `transaction_type` tinyint(0) NULL DEFAULT NULL COMMENT '交易类型: 1=充值, 2=消费, 3=退款, 5=系统调整, 6=退保证金',
  `amount` decimal(10, 2) NOT NULL COMMENT '交易金额（正数）',
  `direction` tinyint(0) NULL DEFAULT NULL COMMENT '资金流向: 1=收入, 2=支出',
  `biz_type` tinyint(0) NULL DEFAULT NULL COMMENT '业务类型: 1=会员购买, 2=订单支付, 3=活动奖励, 5=退款, 6=退保证金',
  `biz_id` bigint(0) NULL DEFAULT NULL COMMENT '关联业务ID',
  `status` tinyint(0) NOT NULL DEFAULT 2 COMMENT '状态: 1=待处理, 2=已完成, 3=已取消, 4=失败',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易标题',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易描述',
  `extra_data` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '扩展信息',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_wallet_transaction_user_id`(`user_id`) USING BTREE,
  INDEX `idx_user_wallet_transaction_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户钱包交易记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_wallet_transaction
-- ----------------------------
INSERT INTO `user_wallet_transaction` VALUES (1, 44, 5, 100.00, 1, NULL, NULL, 2, '管理员增加余额', '管理员增加余额: 100', NULL, '2026-07-13 11:03:24', NULL);
INSERT INTO `user_wallet_transaction` VALUES (2, 44, 4, 10.00, 2, 4, 20, 2, '发起提现', '发起提现: 10', NULL, '2026-07-15 14:37:28', NULL);
INSERT INTO `user_wallet_transaction` VALUES (3, 44, 4, 50.00, 2, 4, 21, 2, '发起提现', '发起提现: 50', NULL, '2026-07-16 18:34:24', NULL);
INSERT INTO `user_wallet_transaction` VALUES (4, 44, 4, 50.00, 1, 4, 21, 2, '提现退回', '提现被拒绝退回: 50.00', NULL, '2026-07-22 13:53:30', NULL);
INSERT INTO `user_wallet_transaction` VALUES (5, 44, 4, 10.00, 1, 4, 20, 2, '提现退回', '提现被拒绝退回: 10.00', NULL, '2026-07-22 13:57:38', NULL);
INSERT INTO `user_wallet_transaction` VALUES (6, 37, 4, 15.00, 1, 4, 18, 2, '提现退回', '提现被拒绝退回: 15.00', NULL, '2026-07-22 14:03:05', NULL);
INSERT INTO `user_wallet_transaction` VALUES (7, 44, 4, 20.00, 2, 4, 22, 2, '发起提现', '发起提现: 20', NULL, '2026-07-30 18:08:15', NULL);
INSERT INTO `user_wallet_transaction` VALUES (8, 44, 4, 20.00, 1, 4, 22, 2, '提现退回', '提现被拒绝退回: 20.00', NULL, '2026-07-30 18:08:43', NULL);
INSERT INTO `user_wallet_transaction` VALUES (9, 44, 5, 10.00, 1, 3, NULL, 2, '环保积分提现到账', '环保积分提现到余额: 10', NULL, '2026-09-23 14:22:35', NULL);
INSERT INTO `user_wallet_transaction` VALUES (10, 44, 5, 300.00, 1, 3, 63, 2, '回收订单打款到账', '回收订单 RE202609241751354007 打款到钱包: ￥300.00', NULL, '2026-09-24 17:52:30', NULL);
INSERT INTO `user_wallet_transaction` VALUES (11, 47, 5, 500.00, 1, 3, 64, 2, '回收订单打款到账', '回收订单 RE202609241755545799 打款到钱包: ￥500.00', NULL, '2026-09-24 17:58:24', NULL);
INSERT INTO `user_wallet_transaction` VALUES (12, 44, 5, 340.00, 1, 3, 67, 2, '回收订单打款到账', '回收订单 RE202609241853587801 打款到钱包: ￥340.00', NULL, '2026-09-24 19:04:06', NULL);
INSERT INTO `user_wallet_transaction` VALUES (13, 47, 5, 550.00, 1, 3, 69, 2, '回收订单打款到账', '回收订单 RE202609281002427779 打款到钱包: ￥550.00', NULL, '2026-09-28 10:35:51', NULL);
INSERT INTO `user_wallet_transaction` VALUES (14, 47, 5, 9.00, 1, 3, NULL, 2, '环保积分提现到账', '环保积分提现到余额: 10积分×0.9=￥9.00', NULL, '2026-09-28 10:37:23', NULL);
INSERT INTO `user_wallet_transaction` VALUES (15, 47, 4, 100.00, 2, 4, 23, 2, '发起提现', '发起提现: 100', NULL, '2026-09-28 10:38:10', NULL);
INSERT INTO `user_wallet_transaction` VALUES (16, 47, 5, 450.00, 1, 3, 70, 2, '回收订单打款到账', '回收订单 RE202609281334222613 打款到钱包: ￥450.00', NULL, '2026-09-28 13:42:02', NULL);
INSERT INTO `user_wallet_transaction` VALUES (27, 50, 5, 300.00, 1, 3, 76, 2, '回收订单打款到账', '回收订单 RE202609291759025784 打款到钱包: ￥300.00', NULL, '2026-09-29 19:06:36', NULL);
INSERT INTO `user_wallet_transaction` VALUES (28, 44, 5, 10.00, 1, 3, NULL, 2, '佣金提现到账', '佣金提现到钱包: 10.00', NULL, '2026-09-29 19:49:33', NULL);
INSERT INTO `user_wallet_transaction` VALUES (29, 44, 5, 5.00, 1, 3, NULL, 2, '佣金提现到账', '佣金提现到钱包: 5.00', NULL, '2026-09-29 19:54:43', NULL);
INSERT INTO `user_wallet_transaction` VALUES (30, 44, 5, 5.00, 1, 3, NULL, 2, '佣金提现到账', '佣金提现到钱包: 5.00', NULL, '2026-09-29 19:54:56', NULL);

-- ----------------------------
-- Table structure for user_welfare
-- ----------------------------
DROP TABLE IF EXISTS `user_welfare`;
CREATE TABLE `user_welfare`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `coin_balance` int(0) NOT NULL DEFAULT 0 COMMENT '金币余额',
  `continuous_sign_days` int(0) NOT NULL DEFAULT 0 COMMENT '连续签到天数',
  `continuous_sign_enabled` tinyint(0) NOT NULL DEFAULT 1 COMMENT '是否开启连续签到福利 1开启 0关闭',
  `last_sign_date` date NULL DEFAULT NULL COMMENT '最近签到日期',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_welfare_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户福利账户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_welfare
-- ----------------------------
INSERT INTO `user_welfare` VALUES (2, 44, 30, 1, 1, '2026-09-29', '2026-07-09 13:57:07', '2026-07-09 13:57:17');
INSERT INTO `user_welfare` VALUES (3, 45, 20, 1, 1, '2026-07-30', '2026-07-10 14:34:12', '2026-07-14 14:07:44');
INSERT INTO `user_welfare` VALUES (4, 42, 0, 0, 0, NULL, '2026-07-14 16:41:22', NULL);
INSERT INTO `user_welfare` VALUES (5, 46, 10, 1, 1, '2026-07-22', '2026-07-16 15:46:22', '2026-07-16 15:46:22');
INSERT INTO `user_welfare` VALUES (6, 32, 0, 0, 1, NULL, '2026-08-12 19:35:00', NULL);
INSERT INTO `user_welfare` VALUES (7, 47, 10, 1, 1, '2026-09-28', '2026-08-12 19:38:03', '2026-08-12 19:39:19');
INSERT INTO `user_welfare` VALUES (8, 48, 0, 0, 0, NULL, '2026-08-13 09:39:58', NULL);
INSERT INTO `user_welfare` VALUES (9, 43, 0, 0, 0, NULL, '2026-08-17 09:27:48', NULL);
INSERT INTO `user_welfare` VALUES (10, 49, 0, 0, 0, NULL, '2026-09-23 15:00:26', NULL);
INSERT INTO `user_welfare` VALUES (11, 50, 0, 0, 1, NULL, '2026-09-29 17:57:31', NULL);

-- ----------------------------
-- Table structure for verify_code
-- ----------------------------
DROP TABLE IF EXISTS `verify_code`;
CREATE TABLE `verify_code`  (
  `id` bigint(0) NOT NULL COMMENT 'id',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '手机号',
  `code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '验证码',
  `status` tinyint(1) NOT NULL COMMENT '状态 1: 未使用 2: 已使用',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '验证码' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of verify_code
-- ----------------------------
INSERT INTO `verify_code` VALUES (2087503811911360514, '18329680001', '888888', 2, '2026-08-12 19:47:47', '2026-08-12 19:37:47');
INSERT INTO `verify_code` VALUES (2087714354492350466, '18329680002', '888888', 2, '2026-08-13 09:44:25', '2026-08-13 09:34:25');
INSERT INTO `verify_code` VALUES (2087714756168261634, '18329680002', '888888', 2, '2026-08-13 09:46:00', '2026-08-13 09:36:00');
INSERT INTO `verify_code` VALUES (2102654108273405953, '15533248230', '888888', 2, '2026-09-23 15:09:39', '2026-09-23 14:59:39');
INSERT INTO `verify_code` VALUES (2102654235667001346, '15533248231', '888888', 2, '2026-09-23 15:10:10', '2026-09-23 15:00:10');
INSERT INTO `verify_code` VALUES (2104873082461507586, '15533248231', '888888', 2, '2026-09-29 18:07:04', '2026-09-29 17:57:04');
INSERT INTO `verify_code` VALUES (2104873159196299265, '15533248232', '888888', 2, '2026-09-29 18:07:22', '2026-09-29 17:57:22');

-- ----------------------------
-- Table structure for vip_plan
-- ----------------------------
DROP TABLE IF EXISTS `vip_plan`;
CREATE TABLE `vip_plan`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会员名称',
  `month` int(0) NULL DEFAULT NULL COMMENT '月数',
  `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '会员价格',
  `original_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '会员套餐\r\n' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vip_plan
-- ----------------------------

-- ----------------------------
-- Table structure for vip_rights
-- ----------------------------
DROP TABLE IF EXISTS `vip_rights`;
CREATE TABLE `vip_rights`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '图标',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '名称',
  `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '会员特权' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vip_rights
-- ----------------------------

-- ----------------------------
-- Table structure for welfare_task
-- ----------------------------
DROP TABLE IF EXISTS `welfare_task`;
CREATE TABLE `welfare_task`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务标题',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务描述',
  `icon` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标图片地址',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '跳转地址',
  `btn_text` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '领任务' COMMENT '按钮文案',
  `sort` int(0) NOT NULL DEFAULT 0 COMMENT '排序',
  `state` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1启用 2禁用',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '福利任务配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of welfare_task
-- ----------------------------
INSERT INTO `welfare_task` VALUES (1, '邀请好友', '最高得 50 元 / 人', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/9eab5a00-0585-477f-b45b-ba8303435016.png', '/packageCommon/user/invite', '领任务', 1, 1, '2026-07-09 13:55:15', '2026-07-21 14:39:18');
INSERT INTO `welfare_task` VALUES (2, '发布回收', '晒单赢实物好礼', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/07/21/d5bb884c-186f-49c8-96ac-479be48b4640.png', '/pages/index/index', '领任务', 2, 1, '2026-07-09 13:55:15', '2026-07-21 14:39:34');

-- ----------------------------
-- Table structure for withdraw_record
-- ----------------------------
DROP TABLE IF EXISTS `withdraw_record`;
CREATE TABLE `withdraw_record`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额',
  `actual_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际到账金额',
  `service_fee` decimal(10, 2) NULL DEFAULT NULL COMMENT '手续费',
  `status` int(0) NULL DEFAULT NULL COMMENT '状态 1发起提现 2提现成功 3拒绝提现',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '拒绝理由',
  `account_type` int(0) NULL DEFAULT NULL COMMENT '提现账户 1微信 2支付宝',
  `withdraw_time` datetime(0) NULL DEFAULT NULL COMMENT '转账时间',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '提现记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of withdraw_record
-- ----------------------------
INSERT INTO `withdraw_record` VALUES (1, 18, 100.00, 99.00, 1.00, 3, '123123', 1, '2025-10-17 14:57:19', '2025-10-17 14:57:23', '2025-12-26 16:06:03');
INSERT INTO `withdraw_record` VALUES (2, 18, 200.00, 198.00, 2.00, 2, '1', 1, '2025-10-17 14:57:57', '2025-10-17 14:58:00', NULL);
INSERT INTO `withdraw_record` VALUES (3, 33, 1000.00, 900.00, 100.00, 1, NULL, NULL, NULL, '2025-12-26 18:37:26', NULL);
INSERT INTO `withdraw_record` VALUES (4, 33, 1000.00, 900.00, 100.00, 3, '123123', NULL, NULL, '2025-12-26 18:49:39', '2025-12-26 18:49:55');
INSERT INTO `withdraw_record` VALUES (5, 37, 10.00, 9.00, 1.00, 1, NULL, NULL, NULL, '2025-12-26 18:50:01', NULL);
INSERT INTO `withdraw_record` VALUES (6, 37, 1000.00, 900.00, 100.00, 3, '123', NULL, NULL, '2025-12-26 18:51:27', '2025-12-26 18:54:18');
INSERT INTO `withdraw_record` VALUES (7, 37, 50.00, 45.00, 5.00, 3, '123132', NULL, NULL, '2025-12-26 18:54:16', '2025-12-26 18:54:35');
INSERT INTO `withdraw_record` VALUES (8, 37, 50.00, 45.00, 5.00, 1, NULL, NULL, NULL, '2025-12-26 19:00:25', NULL);
INSERT INTO `withdraw_record` VALUES (9, 37, 10.00, 9.00, 1.00, 3, '123123', NULL, NULL, '2025-12-26 19:01:47', '2025-12-26 19:24:10');
INSERT INTO `withdraw_record` VALUES (10, 37, 20.00, 18.00, 2.00, 3, '呜呜呜呜', NULL, NULL, '2025-12-26 19:02:32', '2025-12-26 19:08:59');
INSERT INTO `withdraw_record` VALUES (11, 37, 15.00, 13.50, 1.50, 3, '支付宝聚聚', NULL, NULL, '2025-12-26 19:05:34', '2025-12-26 19:07:38');
INSERT INTO `withdraw_record` VALUES (12, 37, 18.00, 16.20, 1.80, 3, '微信拒绝', NULL, NULL, '2025-12-26 19:05:42', '2025-12-26 19:08:01');
INSERT INTO `withdraw_record` VALUES (13, 37, 20.00, 18.00, 2.00, 3, '123123213', NULL, NULL, '2025-12-26 19:08:49', '2025-12-26 19:20:55');
INSERT INTO `withdraw_record` VALUES (14, 37, 15.00, 13.50, 1.50, 3, '123123', 2, NULL, '2025-12-26 19:10:26', '2025-12-26 19:20:18');
INSERT INTO `withdraw_record` VALUES (15, 37, 20.00, 18.00, 2.00, 3, '123123', 2, NULL, '2025-12-26 19:10:27', '2025-12-26 19:18:01');
INSERT INTO `withdraw_record` VALUES (18, 37, 15.00, 13.50, 1.50, 3, 'E2E_withdraw_reject_smoke', 1, NULL, '2025-12-26 19:47:45', '2026-07-22 14:03:05');
INSERT INTO `withdraw_record` VALUES (19, 37, 20.00, 18.00, 2.00, 3, '123123', 2, NULL, '2025-12-26 19:47:51', '2025-12-26 20:45:46');
INSERT INTO `withdraw_record` VALUES (20, 44, 10.00, 10.00, 0.00, 3, 'E2E_withdraw_reject_smoke', 2, NULL, '2026-07-15 14:37:28', '2026-07-22 13:57:37');
INSERT INTO `withdraw_record` VALUES (21, 44, 50.00, 50.00, 0.00, 3, 'E2E_withdraw_reject_smoke', 2, NULL, '2026-07-16 18:34:24', '2026-07-22 13:53:29');
INSERT INTO `withdraw_record` VALUES (22, 44, 20.00, 20.00, 0.00, 3, '测试拒绝理由······', 2, NULL, '2026-07-30 18:08:15', '2026-07-30 18:08:43');
INSERT INTO `withdraw_record` VALUES (23, 47, 100.00, 100.00, 0.00, 1, NULL, 1, NULL, '2026-09-28 10:38:10', NULL);

-- ----------------------------
-- Table structure for withdrawal_account
-- ----------------------------
DROP TABLE IF EXISTS `withdrawal_account`;
CREATE TABLE `withdrawal_account`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `account_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '账户名称',
  `account_number` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '账户账号',
  `type` tinyint(0) NOT NULL COMMENT '账户类型: 1 微信 2 支付宝',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '提现账号' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of withdrawal_account
-- ----------------------------
INSERT INTO `withdrawal_account` VALUES (1, 18, '微信', 'https://xianyu.xianmaxiong.com/file/uploadPath/2025/09/25/bfe6b83a42f9bb61ec2b1f0e92394d55.png', 2, '2025-09-25 11:35:53', '2025-10-17 14:58:35');
INSERT INTO `withdrawal_account` VALUES (2, 18, '十五', '18892020001', 1, '2025-09-25 11:36:05', '2025-10-17 14:58:36');
INSERT INTO `withdrawal_account` VALUES (3, 44, '咩', '15200000000', 1, '2026-07-09 18:27:17', '2026-07-09 18:27:16');
INSERT INTO `withdrawal_account` VALUES (4, 47, '微信收款码', 'https://shegnqx.oss-cn-beijing.aliyuncs.com/2026/09/28/e12b30a1-0d69-438b-9691-23d45c5f8c8a.png', 2, '2026-09-28 10:38:01', '2026-09-28 10:38:01');

SET FOREIGN_KEY_CHECKS = 1;
