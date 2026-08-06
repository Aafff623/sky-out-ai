-- MySQL dump 10.13  Distrib 8.0.37, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: cq_take_out
-- ------------------------------------------------------
-- Server version	8.0.37

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

# 创建cq_take_out数据库
DROP DATABASE IF EXISTS cq_take_out;
CREATE DATABASE cq_take_out;
USE cq_take_out;

--
-- Table structure for table `address_book`
--

DROP TABLE IF EXISTS `address_book`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address_book`
(
    `id`            bigint                                                NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`       bigint                                                NOT NULL COMMENT '用户id',
    `consignee`     varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin          DEFAULT NULL COMMENT '收货人',
    `sex`           varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin           DEFAULT NULL COMMENT '性别',
    `phone`         varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '手机号',
    `province_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   DEFAULT NULL COMMENT '省级区划编号',
    `province_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   DEFAULT NULL COMMENT '省级名称',
    `city_code`     varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   DEFAULT NULL COMMENT '市级区划编号',
    `city_name`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   DEFAULT NULL COMMENT '市级名称',
    `district_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   DEFAULT NULL COMMENT '区级区划编号',
    `district_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci   DEFAULT NULL COMMENT '区级名称',
    `detail`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  DEFAULT NULL COMMENT '详细地址',
    `label`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  DEFAULT NULL COMMENT '标签',
    `is_default`    tinyint(1)                                            NOT NULL DEFAULT '0' COMMENT '默认 0 否 1是',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin
  ROW_FORMAT = DYNAMIC COMMENT ='地址簿';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address_book`
--

LOCK TABLES `address_book` WRITE;
/*!40000 ALTER TABLE `address_book`
    DISABLE KEYS */;
INSERT INTO `address_book`
VALUES (1, 1, '马龙', '1', '13566523654', '44', '广东省', '4401', '广州市', '440106', '天河区', '正佳广场', '其他', 1),
       (2, 2, '张三', '1', '13622426587', '44', '广东省', '4401', '广州市', '440106', '天河区', '天一庄', '家', 0),
       (3, 3, '炸串王', '1', '13622222222', '44', '广东省', '4415', '汕尾市', '441502', '城区', '香洲街道', '家', 1),
       (4, 2, '李四', '1', '13622420000', '44', '广东省', '4401', '广州市', '440106', '天河区', '阳光都会广场', '家',
        1);
/*!40000 ALTER TABLE `address_book`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category`
(
    `id`          bigint                                                NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type`        int                                                            DEFAULT NULL COMMENT '类型   1 菜品分类 2 套餐分类',
    `name`        varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '分类名称',
    `sort`        int                                                   NOT NULL DEFAULT '0' COMMENT '顺序',
    `status`      int                                                            DEFAULT NULL COMMENT '分类状态 0:禁用，1:启用',
    `create_time` datetime                                                       DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                                       DEFAULT NULL COMMENT '更新时间',
    `create_user` bigint                                                         DEFAULT NULL COMMENT '创建人',
    `update_user` bigint                                                         DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `idx_category_name` (`name`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 39
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin
  ROW_FORMAT = DYNAMIC COMMENT ='菜品分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category`
    DISABLE KEYS */;
INSERT INTO `category`
VALUES (30, 1, '饮料', 100, 1, '2024-09-23 14:52:46', '2024-09-23 14:52:47', 1, 1),
       (31, 1, '广式风味', 1, 1, '2024-09-23 14:54:02', '2025-10-16 12:08:56', 1, 1),
       (32, 1, '小吃', 5, 1, '2024-09-23 14:57:14', '2025-10-16 11:40:29', 1, 1),
       (33, 1, '粉面类', 2, 1, '2024-09-23 14:58:41', '2025-07-28 14:20:21', 1, 1),
       (34, 1, '鱼类', 2, 1, '2024-09-23 15:00:02', '2025-07-28 14:33:03', 1, 1),
       (35, 2, '热门套餐', 100, 1, '2025-07-27 21:22:00', '2025-07-27 21:22:05', 1, 1),
       (36, 1, '时令蔬菜', 100, 1, '2025-07-28 14:22:08', '2025-07-28 14:22:36', 1, 1),
       (37, 1, '主食', 100, 1, '2025-07-28 14:23:13', '2025-07-28 14:23:16', 1, 1),
       (38, 2, '风味套餐', 100, 1, '2025-09-06 22:52:32', '2025-09-06 22:52:39', 1, 1);
/*!40000 ALTER TABLE `category`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_service_session`
--

DROP TABLE IF EXISTS `customer_service_session`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_service_session`
(
    `id`           bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`      bigint                                                       NOT NULL COMMENT '用户ID',
    `session_id`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话ID',
    `service_type` tinyint                                                      NOT NULL DEFAULT '1' COMMENT '服务类型 1人工客服 2AI助手',
    `status`       tinyint                                                      NOT NULL DEFAULT '1' COMMENT '会话状态 1进行中 2已结束',
    `staff_id`     bigint                                                                DEFAULT NULL COMMENT '客服人员ID',
    `create_time`  datetime                                                              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `end_time`     datetime                                                              DEFAULT NULL COMMENT '结束时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_session_id` (`session_id`) USING BTREE COMMENT '会话ID唯一索引',
    KEY `idx_user_id` (`user_id`) USING BTREE COMMENT '用户ID索引',
    KEY `idx_staff_id` (`staff_id`) USING BTREE COMMENT '客服人员ID索引'
) ENGINE = InnoDB
  AUTO_INCREMENT = 15
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT ='客服会话表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_service_session`
--

LOCK TABLES `customer_service_session` WRITE;
/*!40000 ALTER TABLE `customer_service_session`
    DISABLE KEYS */;
INSERT INTO `customer_service_session`
VALUES (6, 1, '1abfc8bd1c904b5cbcc5883ec8c8b24e', 2, 1, NULL, '2025-07-28 00:29:19', NULL),
       (9, 1, '6f57a0c71cb44d9784e3a03d3fa551f7', 1, 2, NULL, '2025-09-04 11:46:21', '2025-10-07 14:04:37'),
       (10, 2, '81d59487a8b94320bb10ffe0b90088ef', 2, 1, NULL, '2025-09-05 19:02:21', NULL),
       (11, 2, '4542db1e70b94c20bf06866b2b4e9289', 1, 2, NULL, '2025-09-05 19:02:47', '2025-09-09 19:40:39'),
       (12, 2, 'dbe9126c45f84fb6a9875fec7fc3f43b', 1, 1, NULL, '2025-09-09 19:40:43', NULL),
       (13, 3, '1d38fa51fb84464d984136d729763b56', 1, 2, NULL, '2025-09-18 11:58:58', '2025-11-03 16:10:25'),
       (14, 3, 'aeeaa7ebfcbb4eddb218c55b0bf62db5', 2, 1, NULL, '2025-09-18 11:59:02', NULL);
/*!40000 ALTER TABLE `customer_service_session`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dish`
--

DROP TABLE IF EXISTS `dish`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dish`
(
    `id`          bigint                                                NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '菜品名称',
    `category_id` bigint                                                NOT NULL COMMENT '菜品分类id',
    `price`       decimal(10, 2)                                         DEFAULT NULL COMMENT '菜品价格',
    `image`       varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
    `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '描述信息',
    `status`      int                                                    DEFAULT '1' COMMENT '0 停售 1 起售',
    `hot_spot`    bigint                                                 DEFAULT '0' COMMENT '菜品热度',
    `create_time` datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                               DEFAULT NULL COMMENT '更新时间',
    `create_user` bigint                                                 DEFAULT NULL COMMENT '创建人',
    `update_user` bigint                                                 DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `idx_dish_name` (`name`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 108
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin
  ROW_FORMAT = DYNAMIC COMMENT ='菜品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dish`
--

LOCK TABLES `dish` WRITE;
/*!40000 ALTER TABLE `dish`
    DISABLE KEYS */;
INSERT INTO `dish`
VALUES (76, '西兰花', 36, 11.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片1-20250906-9559add3-048b-42f2-abed-fe95dd68725f.png',
        '西兰花', 1, 0, '2024-09-23 14:48:46', '2025-09-06 17:08:28', 1, 1),
       (77, '鲜肉云吞', 33, 10.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片2-20250906-f8a88394-38e0-4fad-af1f-be8d0923cc89.png',
        '', 1, 1, '2024-09-23 14:50:05', '2025-09-06 17:09:07', 1, 1),
       (78, '蜂蜜柚子茶', 30, 7.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片3-20250906-c5bd0c65-596c-4bbf-b276-72397ea5b4b5.png',
        '', 1, 0, '2024-09-23 14:51:28', '2025-09-06 17:09:24', 1, 1),
       (79, '金吉柠檬果汁', 30, 6.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片4-20250906-e5c39070-a588-4366-b7db-8132c0b6dbe2.png',
        '', 1, 1, '2024-09-23 14:52:16', '2025-09-06 17:09:37', 1, 1),
       (80, '百香果汁', 30, 6.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片5-20250906-c77199d0-de3a-47c2-b1ab-710fc12cdacf.png',
        '', 1, 1, '2024-09-23 14:52:34', '2025-09-06 17:10:30', 1, 1),
       (81, '王老吉', 30, 3.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片6-20250906-c5afee42-0d4f-41e1-a4f4-aa930c1e123b.png',
        '', 1, 0, '2024-09-23 14:53:15', '2025-09-06 17:10:51', 1, 1),
       (82, '北冰洋', 30, 4.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片7-20250906-03849c48-c331-4a32-bddf-051a4c009138.png',
        '', 1, 0, '2024-09-23 14:53:40', '2025-09-06 17:11:04', 1, 1),
       (83, '加米粉', 37, 1.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片8-20250906-12f9b457-7344-45dd-a347-72c51a37a2d1.png',
        '单点不送', 1, 0, '2024-09-23 14:54:41', '2025-09-06 17:11:16', 1, 1),
       (84, '叉烧桂林粉', 33, 8.50,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片9-20250906-1a8d8883-765c-4cc2-adc1-23e9778d5a0d.png',
        '浓郁鲜香', 1, 0, '2024-09-23 14:55:15', '2025-09-06 17:11:28', 1, 1),
       (85, '猪脚粉', 33, 12.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片10-20250906-2700c599-a8a7-493a-9a38-712c699162ca.png',
        '远近闻名，回味无穷，唇齿留香', 1, 0, '2024-09-23 14:56:01', '2025-09-06 17:11:40', 1, 1),
       (86, '桂林卤菜粉', 33, 8.50,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片11-20250906-04dcef8e-9677-4b25-b859-b06458ae972e.png',
        '五香、米粉、叉烧肉、葱、黄豆', 1, 0, '2024-09-23 14:57:00', '2025-09-06 17:11:52', 1, 1),
       (87, '原味煎蛋', 32, 3.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片12-20250906-5b377098-8acb-466a-91a6-3d9eaef17f4d.png',
        '原味煎蛋描述', 1, 0, '2024-09-23 14:57:56', '2025-09-06 17:12:03', 1, 1),
       (88, '麻辣水煮鱼', 34, 2.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片13-20250906-29b98f57-267e-4a23-8492-eb704d864269.png',
        '麻辣水煮鱼', 1, 1, '2024-09-23 14:58:18', '2025-09-06 17:12:15', 1, 1),
       (89, '金汤酸菜鱼', 34, 36.80,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片14-20250906-30c02ee8-7814-48c1-9d8f-52209f45a386.png',
        '金汤酸菜鱼', 1, 0, '2024-09-23 14:59:18', '2025-09-06 17:12:28', 1, 1),
       (90, '米饭', 37, 2.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片15-20250906-3bf6ff9d-beac-42ed-b3ae-5cca69e2f30a.png',
        '五常大米，香喷喷', 1, 2, '2024-09-23 14:59:50', '2025-09-06 17:12:39', 1, 1),
       (91, '清蒸鲈鱼', 34, 52.80,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片16-20250906-87a4c844-0dca-4cbb-932e-60f8f06ca33d.png',
        '清蒸鲈鱼', 1, 4, '2024-09-23 15:03:55', '2025-09-06 17:12:53', 1, 1),
       (92, '香辣烤鱼', 34, 88.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片17-20250906-95faccf7-7964-4b81-86c1-7af892b6a381.png',
        '香辣烤鱼，香辣爽口', 1, 5, '2024-09-23 15:04:20', '2025-09-06 17:13:04', 1, 1),
       (93, '清炒上海青', 36, 18.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片18-20250906-8b91e837-57ce-4e50-9536-7f003f13c0a9.png',
        '清炒上海青', 1, 2, '2025-07-28 14:18:41', '2025-09-06 17:13:18', 1, 1),
       (94, '广式烧鸭', 31, 28.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片19-20250906-cc6632cd-1e30-4c6e-8600-b220205b6f3a.png',
        '广式烧鸭', 1, 1, '2025-07-28 14:29:12', '2025-09-06 17:13:30', 1, 1),
       (95, '白切鸡', 31, 58.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片20-20250906-bfc446da-b256-421b-add2-ea127e6bf14e.png',
        '白切鸡', 1, 3, '2025-07-28 14:29:54', '2025-09-09 20:50:06', 1, 1),
       (97, '烧鹅', 31, 68.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片21-20250906-7fc1dc5a-457b-4bd2-b5eb-f1cf9b216c67.png',
        '烧鹅', 1, 2, '2025-07-28 14:32:06', '2025-09-09 20:49:58', 1, 1),
       (98, '红烧猪蹄', 31, 88.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片23-20250906-ea354130-135c-498e-a49e-c5311060d807.png',
        '红烧猪蹄', 1, 9, '2025-07-28 14:32:40', '2025-09-18 12:40:20', 1, 1),
       (99, '凉拌青瓜', 32, 28.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片24-20250906-fb7f1859-562a-442c-b189-c1755b8704fe.png',
        '凉拌青瓜哈哈哈哈哈哈哈哈哈哈哈哈哈', 1, 6, '2025-07-28 14:33:59', '2025-09-09 20:49:49', 1, 1),
       (100, '白灼菜心', 36, 18.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片22-20250906-7fdadff3-f28b-4084-a4d5-ad3ea8a9d559.png',
        '', 1, 11, '2025-07-28 14:40:05', '2025-09-06 21:52:21', 1, 1),
       (102, '案板上的鱼肉', 34, 68.00,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        '案板上的鱼肉，好吃口味多', 1, 18, '2025-09-06 12:40:27', '2025-09-09 20:49:17', 1, 1);
/*!40000 ALTER TABLE `dish`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dish_flavor`
--

DROP TABLE IF EXISTS `dish_flavor`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dish_flavor`
(
    `id`      bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '菜品口味id',
    `dish_id` bigint                                                       NOT NULL COMMENT '菜品id',
    `name`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '口味名称',
    `value`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '口味值',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_dish_id` (`dish_id`) USING BTREE COMMENT '菜品id索引'
) ENGINE = InnoDB
  AUTO_INCREMENT = 33
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT ='菜品口味表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dish_flavor`
--

LOCK TABLES `dish_flavor` WRITE;
/*!40000 ALTER TABLE `dish_flavor`
    DISABLE KEYS */;
INSERT INTO `dish_flavor`
VALUES (13, 100, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]'),
       (14, 100, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),
       (15, 102, '甜味', '[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]'),
       (16, 102, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),
       (17, 102, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]'),
       (18, 99, '辣度', '[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]'),
       (19, 99, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),
       (20, 97, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),
       (21, 95, '忌口', '[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]');
/*!40000 ALTER TABLE `dish_flavor`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee`
(
    `id`          bigint                                                NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '姓名',
    `username`    varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '用户名',
    `password`    varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '密码',
    `phone`       varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '手机号',
    `sex`         varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin  NOT NULL COMMENT '性别',
    `status`      int                                                   NOT NULL DEFAULT '1' COMMENT '状态 0:禁用，1:启用',
    `create_time` datetime                                                       DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                                       DEFAULT NULL COMMENT '更新时间',
    `create_user` bigint                                                         DEFAULT NULL COMMENT '创建人',
    `update_user` bigint                                                         DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `idx_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 20
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin
  ROW_FORMAT = DYNAMIC COMMENT ='员工信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee`
    DISABLE KEYS */;
INSERT INTO `employee`
VALUES (1, '管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '13599865142', '1', 1, '2025-07-28 14:38:32',
        '2025-07-28 14:38:32', 1, 1),
       (17, '店长', '店长', 'e10adc3949ba59abbe56e057f20f883e', '15326532541', '1', 1, '2025-07-28 14:38:32',
        '2025-08-30 23:13:31', 1, 1),
       (18, '李四', 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '13622420000', '1', 1, '2025-11-03 08:16:51',
        '2025-11-03 08:26:34', 1, 1),
       (19, '张三', 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '13612350000', '1', 1, '2025-11-03 08:17:32',
        '2025-11-03 08:26:37', 1, 1);
/*!40000 ALTER TABLE `employee`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_views`
--

DROP TABLE IF EXISTS `item_views`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_views`
(
    `id`         bigint      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`    bigint      NOT NULL COMMENT '用户id',
    `item_id`    bigint      NOT NULL COMMENT '菜品ID或套餐ID',
    `item_type`  varchar(10) NOT NULL COMMENT '菜品:dish, 套餐:setmeal',
    `view_count` int DEFAULT '0' COMMENT '浏览次数',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='商品项浏览表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_views`
--

LOCK TABLES `item_views` WRITE;
/*!40000 ALTER TABLE `item_views`
    DISABLE KEYS */;
INSERT INTO `item_views`
VALUES (1, 2, 2, 'setmeal', 3),
       (3, 2, 102, 'dish', 7),
       (4, 2, 100, 'dish', 4),
       (5, 2, 98, 'dish', 2),
       (6, 2, 99, 'setmeal', 1);
/*!40000 ALTER TABLE `item_views`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `knowledge_base`
--

DROP TABLE IF EXISTS `knowledge_base`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `knowledge_base`
(
    `id`           int          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `file_name`    varchar(50)  NOT NULL COMMENT '知识库名称',
    `url`          varchar(255) NOT NULL COMMENT '原始文件链接',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 38
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='知识库表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `knowledge_base`
--

LOCK TABLES `knowledge_base` WRITE;
/*!40000 ALTER TABLE `knowledge_base`
    DISABLE KEYS */;
INSERT INTO `knowledge_base`
VALUES (33, '测试2.txt',
        'http://192.168.88.130:9000/training-take-out/2025/11/08/f7afc2cc-0ad8-468c-85d9-0b5e96e832ed-测试2.txt',
        '2025-11-08 17:10:19'),
       (34, '西北大学.pdf',
        'http://192.168.88.130:9000/training-take-out/2025/11/08/002bc8fc-5feb-46e0-94c1-a0cd8fbb0170-西北大学.pdf',
        '2025-11-08 17:15:58'),
       (35, '热门专业top20.pdf',
        'http://192.168.88.130:9000/training-take-out/2025/11/08/ebc87d30-f003-448c-bb77-2af1ee65244a-热门专业top20.pdf',
        '2025-11-08 17:16:04'),
       (36, '西北农林科技大学.md',
        'http://192.168.88.130:9000/training-take-out/2025/11/08/aa63efa9-5b59-4bd1-be1c-8e4bdb222296-西北农林科技大学.md',
        '2025-11-08 17:16:16'),
       (37, '菜品详细介绍文档.md',
        'http://192.168.88.130:9000/training-take-out/2025/11/08/42363184-2309-48e5-9124-ee13952f784b-菜品详细介绍文档.md',
        '2025-11-08 17:59:32');
/*!40000 ALTER TABLE `knowledge_base`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `knowledge_base_redis`
--

DROP TABLE IF EXISTS `knowledge_base_redis`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `knowledge_base_redis`
(
    `id`                int          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `knowledge_base_id` bigint       NOT NULL COMMENT '知识库主键ID',
    `redis_id`          varchar(255) NOT NULL COMMENT '关联的Redis向量ID',
    `created_time`      datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 185
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='知识库与Redis向量ID关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `knowledge_base_redis`
--

LOCK TABLES `knowledge_base_redis` WRITE;
/*!40000 ALTER TABLE `knowledge_base_redis`
    DISABLE KEYS */;
INSERT INTO `knowledge_base_redis`
VALUES (96, 33, '01a61580-186b-4fbd-a16a-025151c45580', '2025-11-08 17:10:19'),
       (97, 34, '9944dccc-f5dc-4cb2-9eb4-c95863dc884c', '2025-11-08 17:15:58'),
       (98, 34, '9102cf25-70ba-466d-8593-4328626cf40a', '2025-11-08 17:15:58'),
       (99, 34, '0abbfeb9-fe78-4810-8b5a-adda2ccb396a', '2025-11-08 17:15:58'),
       (100, 34, 'eaacf414-d536-40ac-b061-a0cbae52e999', '2025-11-08 17:15:58'),
       (101, 34, '44f78f76-8bdb-4fe2-a5d7-01a47f0ac125', '2025-11-08 17:15:58'),
       (102, 34, '75aa4b0b-c360-4c3a-a045-e84580876210', '2025-11-08 17:15:58'),
       (103, 34, 'b75f82c1-9b4c-4471-9577-71981b1f1d56', '2025-11-08 17:15:58'),
       (104, 34, '8b53624c-2dfc-4fb5-9cbc-fd24762daaa2', '2025-11-08 17:15:58'),
       (105, 34, 'e286aad7-0486-497f-b2c0-7663f78bbddb', '2025-11-08 17:15:58'),
       (106, 34, 'be527e01-74b5-4c23-80b4-0429702d0d89', '2025-11-08 17:15:58'),
       (107, 35, 'fbbb2eac-c87a-4014-9960-cd89ffefd570', '2025-11-08 17:16:04'),
       (108, 35, '2c43b8f0-633a-45ac-bf18-a28e0e1e24d5', '2025-11-08 17:16:04'),
       (109, 35, '3dc07569-4be7-4390-9267-383e568ed3a8', '2025-11-08 17:16:04'),
       (110, 35, 'd0921f1e-0e26-4a73-8844-9fcae453357b', '2025-11-08 17:16:04'),
       (111, 35, '83899470-074d-4c76-9d08-2079d8981e6e', '2025-11-08 17:16:04'),
       (112, 36, '6f47f279-a21d-4651-ad5a-8fcabbeff87b', '2025-11-08 17:16:16'),
       (113, 36, '0d523d66-a8c8-40ef-ab6f-e9d254f5a59e', '2025-11-08 17:16:16'),
       (114, 36, 'f5bfccb5-b58e-451c-905e-70d78c2eb274', '2025-11-08 17:16:16'),
       (115, 36, 'e494f80f-f295-42fe-9012-b946d947e939', '2025-11-08 17:16:16'),
       (116, 36, '518acfab-1966-418d-89b2-f3449fb32add', '2025-11-08 17:16:16'),
       (117, 36, '31923969-7593-4f51-b1ba-c018ae8272ec', '2025-11-08 17:16:16'),
       (118, 36, '0ee4b8ba-c408-4b18-9db8-abce11147151', '2025-11-08 17:16:16'),
       (119, 36, 'f4ee0170-559e-4a3e-89da-ff2d6cc9e52f', '2025-11-08 17:16:16'),
       (120, 36, 'f1354332-23e9-4db3-b4b1-6d3c987f9b38', '2025-11-08 17:16:16'),
       (121, 36, 'd2070c57-816f-462f-bf0b-b9657a6a614f', '2025-11-08 17:16:16'),
       (122, 36, '9189c207-f1a7-4970-8f2a-46317411ad3f', '2025-11-08 17:16:16'),
       (123, 36, '7a07ff60-3458-408b-9cd3-80364de2d54f', '2025-11-08 17:16:16'),
       (124, 36, '45ef5172-6000-465b-8488-92572c4ef926', '2025-11-08 17:16:16'),
       (125, 36, 'e47e24d2-a021-487b-9ce8-805524f6135b', '2025-11-08 17:16:16'),
       (126, 36, '94213f0b-adef-4533-a26d-5bddd0af4ae8', '2025-11-08 17:16:16'),
       (127, 36, '661adaba-f294-45e1-8fa0-c92eaa6d85e8', '2025-11-08 17:16:16'),
       (128, 36, '0d1a66f9-67db-41b7-abc0-48e5751b226f', '2025-11-08 17:16:16'),
       (129, 36, '4b45d7f2-cdc9-4ffa-9f27-4d24fa77363d', '2025-11-08 17:16:16'),
       (130, 36, '58ac95d5-e9dd-4142-b05f-bde7dcf3f22a', '2025-11-08 17:16:16'),
       (131, 36, '3d1943d3-62ba-49c7-9868-b66cb2f5ddc7', '2025-11-08 17:16:16'),
       (132, 36, '83237c43-b379-47cb-9d16-24b68a91d625', '2025-11-08 17:16:16'),
       (133, 36, '7fbd17c6-8933-420e-be16-2018ad738562', '2025-11-08 17:16:16'),
       (134, 36, 'e7712cdc-14fb-4eab-926e-7b5b23397383', '2025-11-08 17:16:16'),
       (135, 36, 'c3398103-a29c-413a-a0e4-12d1a023a18c', '2025-11-08 17:16:16'),
       (136, 36, '80684b77-3ba7-4397-a67d-5e7874e466f2', '2025-11-08 17:16:16'),
       (137, 36, '3d951efa-7d90-4842-a7b1-b83ec42f144e', '2025-11-08 17:16:16'),
       (138, 36, '3ec1b270-6e21-454e-bfe8-2d25f8678fd9', '2025-11-08 17:16:16'),
       (139, 36, 'b81d3eef-1f0a-46b5-b8f7-1984863540ed', '2025-11-08 17:16:16'),
       (140, 36, 'db9f0fd0-1297-48e9-b601-da47a3a7447c', '2025-11-08 17:16:16'),
       (141, 36, '51484c71-d403-467e-9df8-10cf699c2118', '2025-11-08 17:16:16'),
       (142, 36, '1b16b156-69ec-490a-9417-907c70165404', '2025-11-08 17:16:16'),
       (143, 36, '08c9a387-5fa3-4b68-a4b1-bc166793bde9', '2025-11-08 17:16:16'),
       (144, 36, 'a78fb51e-7081-40eb-9651-fbd4f88fd6ff', '2025-11-08 17:16:16'),
       (145, 36, 'b39ef38b-f1d4-4da3-80a2-a8632c34b151', '2025-11-08 17:16:16'),
       (146, 36, '8f9c8288-004c-407e-b81e-d31aea884424', '2025-11-08 17:16:16'),
       (147, 36, '35d9e060-860e-446e-8975-01480955398b', '2025-11-08 17:16:16'),
       (148, 36, 'fff6d069-c15c-4f43-85d2-770cd4c171cf', '2025-11-08 17:16:16'),
       (149, 36, '8557f02c-1e7f-4134-81c1-8f8624086609', '2025-11-08 17:16:16'),
       (150, 36, 'a025f47f-6d1b-44fe-8b04-14934bc1611f', '2025-11-08 17:16:16'),
       (151, 36, 'f55e5b7b-6bfa-495f-8830-335ff62ee159', '2025-11-08 17:16:16'),
       (152, 36, 'ca653511-68d1-47d7-aaeb-d70cbfef95ab', '2025-11-08 17:16:16'),
       (153, 36, '5249f691-6636-4129-891e-5b59fe87decb', '2025-11-08 17:16:16'),
       (154, 36, '3289385c-052b-41f1-8b14-39de0fa94b60', '2025-11-08 17:16:16'),
       (155, 36, 'eec97b6a-93b0-4500-946b-2bb079dc3d5c', '2025-11-08 17:16:16'),
       (156, 36, '3e88de4c-d846-46e5-86e6-af52a0175299', '2025-11-08 17:16:16'),
       (157, 36, '685867d8-bfc3-4685-90b7-de7a5519338f', '2025-11-08 17:16:16'),
       (158, 36, 'bac46d67-13e3-4303-b519-6213eb3e4a9d', '2025-11-08 17:16:16'),
       (159, 36, '191235b3-fed0-4006-8f66-a93138df5eef', '2025-11-08 17:16:16'),
       (160, 36, '5a6af783-5b3c-4d86-9321-d30aca34b8f3', '2025-11-08 17:16:16'),
       (161, 36, '9f2e7ab2-265e-459c-b5e0-1c0d549c07e7', '2025-11-08 17:16:16'),
       (162, 36, 'd9fdaeb4-3759-414c-b9e6-f42466a0a9d3', '2025-11-08 17:16:16'),
       (163, 36, '6184b30b-54d5-45fa-ac00-b8f83df9d557', '2025-11-08 17:16:16'),
       (164, 37, '84e9a0b0-ccd0-40e3-b3f7-748efc73c3c9', '2025-11-08 17:59:32'),
       (165, 37, 'a3116ef9-3238-4aa6-ad70-378c5f6be54d', '2025-11-08 17:59:32'),
       (166, 37, '21afae15-96bc-4d82-94a9-e6f6147720ca', '2025-11-08 17:59:32'),
       (167, 37, '3b3ec3ab-f42e-4845-804e-fb7e8d2bc1a0', '2025-11-08 17:59:32'),
       (168, 37, '0bdd53c6-dbd4-4d6c-813f-d82f7b67283e', '2025-11-08 17:59:32'),
       (169, 37, '60e32159-eb8c-4cea-a5b9-d8f5fd49a234', '2025-11-08 17:59:32'),
       (170, 37, '0f890b43-0689-4d5d-bc18-62e9aa42d07b', '2025-11-08 17:59:32'),
       (171, 37, '3c4074dc-8303-429c-a9dd-41379dfe322c', '2025-11-08 17:59:32'),
       (172, 37, '97372709-694f-41e8-aa99-c1695c76cc60', '2025-11-08 17:59:32'),
       (173, 37, 'e4d52d9d-928d-4563-82d4-91822a561880', '2025-11-08 17:59:32'),
       (174, 37, '07adcd45-b323-4ca8-b90d-1395608cd33a', '2025-11-08 17:59:32'),
       (175, 37, '3ab17004-a5e1-4b25-9656-94f12a995462', '2025-11-08 17:59:32'),
       (176, 37, 'd58cc533-b07a-44aa-9e00-5c51dcd38ada', '2025-11-08 17:59:32'),
       (177, 37, 'dd4442c2-5dd2-4f04-b7e8-7e404fcdf2a3', '2025-11-08 17:59:32'),
       (178, 37, '0bd5e3f7-c8b2-4c5a-b74b-5edd075f93ed', '2025-11-08 17:59:32'),
       (179, 37, '59f733d1-d95c-4b12-9cc7-860ca5c16a80', '2025-11-08 17:59:32'),
       (180, 37, '39b65574-1ca7-42a9-b95b-5bbc8af7402a', '2025-11-08 17:59:32'),
       (181, 37, '64d0c566-d09b-4efb-9335-72601d5cf225', '2025-11-08 17:59:32'),
       (182, 37, '87b96ca8-e27c-4576-867c-3f6d5d55caed', '2025-11-08 17:59:32'),
       (183, 37, '1f905440-eb62-4101-8974-25cace26c107', '2025-11-08 17:59:32'),
       (184, 37, 'ca265900-e4ec-4595-a356-a29e671bc74f', '2025-11-08 17:59:32');
/*!40000 ALTER TABLE `knowledge_base_redis`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_detail`
--

DROP TABLE IF EXISTS `order_detail`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_detail`
(
    `id`         bigint         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`       varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin  DEFAULT NULL COMMENT '名字',
    `image`      varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
    `order_id`   bigint         NOT NULL COMMENT '订单id',
    `setmeal_id` bigint                                                 DEFAULT NULL COMMENT '套餐id',
    `dish_id`    bigint                                                 DEFAULT NULL COMMENT '菜品id',
    `number`     int            NOT NULL                                DEFAULT '1' COMMENT '数量',
    `amount`     decimal(10, 2) NOT NULL COMMENT '金额',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 148
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin
  ROW_FORMAT = DYNAMIC COMMENT ='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_detail`
--

LOCK TABLES `order_detail` WRITE;
/*!40000 ALTER TABLE `order_detail`
    DISABLE KEYS */;
INSERT INTO `order_detail`
VALUES (21, '爆辣鲜香螺蛳粉',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/2f5345c9f30bddbbe3b8aab752bbfd3-20240923-51fe032e-b9fe-4777-9cdd-0483202b268f.jpg',
        13, NULL, 92, 1, 8.00),
       (22, '桂林卤菜粉',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/618235d456f5614c96dd34466eb1e89-20240923-73bfde52-4430-42d3-9216-306a4985394d.jpg',
        13, NULL, 86, 1, 8.50),
       (23, '猪脚粉',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/5c61ff75547f0d6a18eb3468f3d5665-20240923-f8462113-43ac-41b9-b091-b6cf7e33e62b.jpg',
        13, NULL, 85, 1, 12.00),
       (24, '白灼菜心',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/ba880a9e-fd37-4900-b73d-f45f5e98ca23-20250728-289936ad-0b06-4be1-93be-a43332d4fc98.png',
        14, NULL, 100, 1, 18.00),
       (25, '凉拌青瓜',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/d66293dd-0507-4178-a373-7f058b91e64a-20250728-85f3b1b1-32ad-4e99-a279-a8c5fa0243a6.jpeg',
        14, NULL, 99, 1, 28.00),
       (26, '红烧猪蹄',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/db987c70-fb3b-436d-86a7-f558df1176ab-20250728-5a49df20-c726-4ca3-9a93-eaf96debe8e1.jpeg',
        14, NULL, 98, 1, 88.00),
       (27, '金汤酸菜鱼',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/7-20250728-b0d7d81d-235b-4a3c-8f54-3ea70eabf7c1.png', 14,
        NULL, 89, 1, 36.80),
       (28, '白切鸡',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/d221e5da-d45e-4849-9f96-c89903e5bd2f-20250728-7045ba39-0e5c-4f09-9397-1bf0459365fb.png',
        15, NULL, 95, 1, 58.00),
       (29, '香辣烤鱼',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/21-20250728-6d512be6-2871-46d6-a920-e6db98467145.png',
        15, NULL, 92, 1, 88.00),
       (30, '香辣烤鱼',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/21-20250728-6d512be6-2871-46d6-a920-e6db98467145.png',
        16, NULL, 92, 1, 88.00),
       (31, '清蒸鲈鱼',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/13-20250728-3f0eb7fa-8167-4200-8b62-0d1a12db70a9.png',
        16, NULL, 91, 1, 52.80),
       (32, '桂林卤菜粉',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/618235d456f5614c96dd34466eb1e89-20240923-73bfde52-4430-42d3-9216-306a4985394d.jpg',
        17, NULL, 86, 1, 8.50),
       (33, '清炒上海青',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/9-20250728-473c1260-4d6f-4742-a790-fa9fdc3232e9.png', 18,
        NULL, 93, 1, 18.00),
       (34, '白灼菜心',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/ba880a9e-fd37-4900-b73d-f45f5e98ca23-20250728-289936ad-0b06-4be1-93be-a43332d4fc98.png',
        18, NULL, 100, 1, 18.00),
       (35, '白灼菜心',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/ba880a9e-fd37-4900-b73d-f45f5e98ca23-20250728-289936ad-0b06-4be1-93be-a43332d4fc98.png',
        19, NULL, 100, 1, 18.00),
       (36, '白灼菜心',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/ba880a9e-fd37-4900-b73d-f45f5e98ca23-20250728-289936ad-0b06-4be1-93be-a43332d4fc98.png',
        20, NULL, 100, 1, 18.00),
       (37, '清炒上海青',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/9-20250728-473c1260-4d6f-4742-a790-fa9fdc3232e9.png', 21,
        NULL, 93, 1, 18.00),
       (38, '白灼菜心',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/ba880a9e-fd37-4900-b73d-f45f5e98ca23-20250728-289936ad-0b06-4be1-93be-a43332d4fc98.png',
        22, NULL, 100, 1, 18.00),
       (39, '红烧猪蹄',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/db987c70-fb3b-436d-86a7-f558df1176ab-20250728-5a49df20-c726-4ca3-9a93-eaf96debe8e1.jpeg',
        23, NULL, 98, 1, 88.00),
       (40, '红烧猪蹄',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/db987c70-fb3b-436d-86a7-f558df1176ab-20250728-5a49df20-c726-4ca3-9a93-eaf96debe8e1.jpeg',
        24, NULL, 98, 1, 88.00),
       (41, '白灼菜心',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/ba880a9e-fd37-4900-b73d-f45f5e98ca23-20250728-289936ad-0b06-4be1-93be-a43332d4fc98.png',
        25, NULL, 100, 1, 18.00),
       (42, '白灼菜心',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/ba880a9e-fd37-4900-b73d-f45f5e98ca23-20250728-289936ad-0b06-4be1-93be-a43332d4fc98.png',
        26, NULL, 100, 1, 18.00),
       (43, '白灼菜心',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/ba880a9e-fd37-4900-b73d-f45f5e98ca23-20250728-289936ad-0b06-4be1-93be-a43332d4fc98.png',
        27, NULL, 100, 1, 18.00),
       (44, '白灼菜心',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/ba880a9e-fd37-4900-b73d-f45f5e98ca23-20250728-289936ad-0b06-4be1-93be-a43332d4fc98.png',
        28, NULL, 100, 1, 18.00),
       (45, '白灼菜心',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/ba880a9e-fd37-4900-b73d-f45f5e98ca23-20250728-289936ad-0b06-4be1-93be-a43332d4fc98.png',
        29, NULL, 100, 1, 18.00),
       (46, '凉拌青瓜',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/d66293dd-0507-4178-a373-7f058b91e64a-20250728-85f3b1b1-32ad-4e99-a279-a8c5fa0243a6.jpeg',
        29, NULL, 99, 1, 28.00),
       (47, '烧鹅',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/b9ec4c2d-57bb-4942-bfbc-9e32f2eea967-20250728-71c5e331-605e-4c96-9a56-dea7b3a4ebdf.png',
        30, NULL, 97, 1, 68.00),
       (48, '香辣烤鱼',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/21-20250728-6d512be6-2871-46d6-a920-e6db98467145.png',
        31, NULL, 92, 1, 88.00),
       (49, '香辣烤鱼',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/21-20250728-6d512be6-2871-46d6-a920-e6db98467145.png',
        32, NULL, 92, 1, 88.00),
       (50, '麻辣水煮鱼',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/8-20250728-28df034e-efaf-4780-a59f-64ac07562870.png', 33,
        NULL, 88, 2, 2.00),
       (51, '烧鹅',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/b9ec4c2d-57bb-4942-bfbc-9e32f2eea967-20250728-71c5e331-605e-4c96-9a56-dea7b3a4ebdf.png',
        33, NULL, 97, 1, 68.00),
       (52, '凉拌青瓜',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/d66293dd-0507-4178-a373-7f058b91e64a-20250728-85f3b1b1-32ad-4e99-a279-a8c5fa0243a6.jpeg',
        33, NULL, 99, 1, 28.00),
       (53, '米饭',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/4-20250728-6244a932-a9b7-4ecf-80b8-13c022635599.png', 33,
        NULL, 90, 1, 2.00),
       (54, '加米粉',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/db62835af5663180f174814c4506fd0-20240923-74362762-d1b8-454c-9331-728d264f8f88.jpg',
        33, NULL, 83, 1, 1.00),
       (55, '凉拌青瓜',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/d66293dd-0507-4178-a373-7f058b91e64a-20250728-85f3b1b1-32ad-4e99-a279-a8c5fa0243a6.jpeg',
        34, NULL, 99, 1, 28.00),
       (56, '红烧猪蹄',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/db987c70-fb3b-436d-86a7-f558df1176ab-20250728-5a49df20-c726-4ca3-9a93-eaf96debe8e1.jpeg',
        34, NULL, 98, 1, 88.00),
       (57, '烧鹅',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/b9ec4c2d-57bb-4942-bfbc-9e32f2eea967-20250728-71c5e331-605e-4c96-9a56-dea7b3a4ebdf.png',
        35, NULL, 97, 1, 68.00),
       (58, '白切鸡',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/d221e5da-d45e-4849-9f96-c89903e5bd2f-20250728-7045ba39-0e5c-4f09-9397-1bf0459365fb.png',
        35, NULL, 95, 1, 58.00),
       (59, '一人餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/24-20250728-e1f4fdc4-e21e-473e-bca0-86a11791ea6d.png',
        35, NULL, NULL, 1, 28.00),
       (60, '全家福套餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/23-20250728-483f78a0-7662-4ccf-8874-7e9e5491a497.png',
        36, NULL, NULL, 1, 108.00),
       (61, '香辣烤鱼',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/21-20250728-6d512be6-2871-46d6-a920-e6db98467145.png',
        37, NULL, 92, 1, 88.00),
       (62, '一人餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/24-20250728-e1f4fdc4-e21e-473e-bca0-86a11791ea6d.png',
        38, NULL, NULL, 1, 28.00),
       (63, '一人餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/24-20250728-e1f4fdc4-e21e-473e-bca0-86a11791ea6d.png',
        39, NULL, NULL, 1, 28.00),
       (64, '一人餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/24-20250728-e1f4fdc4-e21e-473e-bca0-86a11791ea6d.png',
        40, NULL, NULL, 1, 28.00),
       (65, '一人餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/24-20250728-e1f4fdc4-e21e-473e-bca0-86a11791ea6d.png',
        41, NULL, NULL, 1, 28.00),
       (66, '一人餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/24-20250728-e1f4fdc4-e21e-473e-bca0-86a11791ea6d.png',
        42, 2, NULL, 1, 28.00),
       (67, '一人餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/24-20250728-e1f4fdc4-e21e-473e-bca0-86a11791ea6d.png',
        43, 2, NULL, 1, 28.00),
       (68, '白灼菜心',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/ba880a9e-fd37-4900-b73d-f45f5e98ca23-20250728-289936ad-0b06-4be1-93be-a43332d4fc98.png',
        44, NULL, 100, 1, 18.00),
       (69, '凉拌青瓜',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/d66293dd-0507-4178-a373-7f058b91e64a-20250728-85f3b1b1-32ad-4e99-a279-a8c5fa0243a6.jpeg',
        45, NULL, 99, 1, 28.00),
       (70, '一人餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/24-20250728-e1f4fdc4-e21e-473e-bca0-86a11791ea6d.png',
        45, 2, NULL, 1, 28.00),
       (71, '凉拌青瓜',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/d66293dd-0507-4178-a373-7f058b91e64a-20250728-85f3b1b1-32ad-4e99-a279-a8c5fa0243a6.jpeg',
        46, NULL, 99, 1, 28.00),
       (72, '一人餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/24-20250728-e1f4fdc4-e21e-473e-bca0-86a11791ea6d.png',
        46, 2, NULL, 1, 28.00),
       (73, '白灼菜心',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/ba880a9e-fd37-4900-b73d-f45f5e98ca23-20250728-289936ad-0b06-4be1-93be-a43332d4fc98.png',
        46, NULL, 100, 1, 18.00),
       (74, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        47, NULL, 102, 1, 68.00),
       (75, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        47, NULL, 102, 1, 68.00),
       (76, '一人餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/24-20250728-e1f4fdc4-e21e-473e-bca0-86a11791ea6d.png',
        48, 2, NULL, 1, 28.00),
       (77, '红烧猪蹄',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/db987c70-fb3b-436d-86a7-f558df1176ab-20250728-5a49df20-c726-4ca3-9a93-eaf96debe8e1.jpeg',
        49, NULL, 98, 1, 88.00),
       (78, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        50, NULL, 102, 1, 68.00),
       (79, '全家福套餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/23-20250728-483f78a0-7662-4ccf-8874-7e9e5491a497.png',
        50, 1, NULL, 1, 108.00),
       (80, '一人餐',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/24-20250728-e1f4fdc4-e21e-473e-bca0-86a11791ea6d.png',
        51, 2, NULL, 1, 28.00),
       (81, '红烧猪蹄',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/db987c70-fb3b-436d-86a7-f558df1176ab-20250728-5a49df20-c726-4ca3-9a93-eaf96debe8e1.jpeg',
        52, NULL, 98, 1, 88.00),
       (82, '烧鹅',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/b9ec4c2d-57bb-4942-bfbc-9e32f2eea967-20250728-71c5e331-605e-4c96-9a56-dea7b3a4ebdf.png',
        53, NULL, 97, 1, 68.00),
       (83, '红烧猪蹄',
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/db987c70-fb3b-436d-86a7-f558df1176ab-20250728-5a49df20-c726-4ca3-9a93-eaf96debe8e1.jpeg',
        54, NULL, 98, 1, 88.00),
       (84, '白灼菜心',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片22-20250906-7fdadff3-f28b-4084-a4d5-ad3ea8a9d559.png',
        55, NULL, 100, 1, 18.00),
       (85, '白切鸡',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片20-20250906-bfc446da-b256-421b-add2-ea127e6bf14e.png',
        56, NULL, 95, 1, 58.00),
       (86, '烧鹅',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片21-20250906-7fc1dc5a-457b-4bd2-b5eb-f1cf9b216c67.png',
        57, NULL, 97, 1, 68.00),
       (87, '白切鸡',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片20-20250906-bfc446da-b256-421b-add2-ea127e6bf14e.png',
        58, NULL, 95, 5, 58.00),
       (88, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        59, NULL, 102, 1, 68.00),
       (89, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        60, NULL, 102, 1, 68.00),
       (90, '清炒上海青',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片18-20250906-8b91e837-57ce-4e50-9536-7f003f13c0a9.png',
        61, NULL, 93, 1, 18.00),
       (91, '香辣烤鱼',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片17-20250906-95faccf7-7964-4b81-86c1-7af892b6a381.png',
        62, NULL, 92, 1, 88.00),
       (92, '麻辣水煮鱼',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片13-20250906-29b98f57-267e-4a23-8492-eb704d864269.png',
        62, NULL, 88, 1, 2.00),
       (93, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        62, NULL, 102, 1, 68.00),
       (94, '清炒上海青',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片18-20250906-8b91e837-57ce-4e50-9536-7f003f13c0a9.png',
        62, NULL, 93, 1, 18.00),
       (95, '凉拌青瓜',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片24-20250906-fb7f1859-562a-442c-b189-c1755b8704fe.png',
        62, NULL, 99, 1, 28.00),
       (96, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        62, NULL, 102, 1, 68.00),
       (97, '清蒸鲈鱼',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片16-20250906-87a4c844-0dca-4cbb-932e-60f8f06ca33d.png',
        62, NULL, 91, 1, 52.80),
       (98, '广式风味2',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/25-20250907-851c549c-f755-429e-9090-19d7c3970419.png',
        62, 4, NULL, 1, 108.00),
       (99, '白灼菜心',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片22-20250906-7fdadff3-f28b-4084-a4d5-ad3ea8a9d559.png',
        63, NULL, 100, 1, 18.00),
       (100, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        63, NULL, 102, 1, 68.00),
       (101, '红烧猪蹄',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片23-20250906-ea354130-135c-498e-a49e-c5311060d807.png',
        63, NULL, 98, 1, 88.00),
       (102, '广式烧鸭',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片19-20250906-cc6632cd-1e30-4c6e-8600-b220205b6f3a.png',
        64, NULL, 94, 2, 28.00),
       (103, '白切鸡',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片20-20250906-bfc446da-b256-421b-add2-ea127e6bf14e.png',
        64, NULL, 95, 1, 58.00),
       (104, '红烧猪蹄',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片23-20250906-ea354130-135c-498e-a49e-c5311060d807.png',
        64, NULL, 98, 1, 88.00),
       (105, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        64, NULL, 102, 1, 68.00),
       (106, '香辣烤鱼',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片17-20250906-95faccf7-7964-4b81-86c1-7af892b6a381.png',
        65, NULL, 92, 1, 88.00),
       (107, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        66, NULL, 102, 1, 68.00),
       (108, '白灼菜心',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片22-20250906-7fdadff3-f28b-4084-a4d5-ad3ea8a9d559.png',
        66, NULL, 100, 1, 18.00),
       (109, '凉拌青瓜',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片24-20250906-fb7f1859-562a-442c-b189-c1755b8704fe.png',
        66, NULL, 99, 1, 28.00),
       (110, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        67, NULL, 102, 1, 68.00),
       (111, '白灼菜心',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片22-20250906-7fdadff3-f28b-4084-a4d5-ad3ea8a9d559.png',
        68, NULL, 100, 1, 18.00),
       (112, '白灼菜心',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片22-20250906-7fdadff3-f28b-4084-a4d5-ad3ea8a9d559.png',
        69, NULL, 100, 1, 18.00),
       (113, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        70, NULL, 102, 1, 68.00),
       (114, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        71, NULL, 102, 1, 68.00),
       (115, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        72, NULL, 102, 1, 68.00),
       (116, '凉拌青瓜',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片24-20250906-fb7f1859-562a-442c-b189-c1755b8704fe.png',
        73, NULL, 99, 1, 28.00),
       (117, '金吉柠檬果汁',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片4-20250906-e5c39070-a588-4366-b7db-8132c0b6dbe2.png',
        74, NULL, 79, 1, 6.00),
       (118, '百香果汁',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片5-20250906-c77199d0-de3a-47c2-b1ab-710fc12cdacf.png',
        74, NULL, 80, 1, 6.00),
       (119, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        74, NULL, 102, 1, 68.00),
       (120, '红烧猪蹄',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片23-20250906-ea354130-135c-498e-a49e-c5311060d807.png',
        74, NULL, 98, 1, 88.00),
       (121, '红烧猪蹄',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片23-20250906-ea354130-135c-498e-a49e-c5311060d807.png',
        75, NULL, 98, 1, 88.00),
       (122, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        76, NULL, 102, 1, 68.00),
       (123, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        77, NULL, 102, 1, 68.00),
       (124, '白灼菜心',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片22-20250906-7fdadff3-f28b-4084-a4d5-ad3ea8a9d559.png',
        78, NULL, 100, 1, 18.00),
       (125, '凉拌青瓜',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片24-20250906-fb7f1859-562a-442c-b189-c1755b8704fe.png',
        79, NULL, 99, 1, 28.00),
       (126, '白灼菜心',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片22-20250906-7fdadff3-f28b-4084-a4d5-ad3ea8a9d559.png',
        80, NULL, 100, 1, 18.00),
       (127, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        81, NULL, 102, 1, 68.00),
       (128, '凉拌青瓜',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片24-20250906-fb7f1859-562a-442c-b189-c1755b8704fe.png',
        81, NULL, 99, 1, 28.00),
       (129, '白灼菜心',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片22-20250906-7fdadff3-f28b-4084-a4d5-ad3ea8a9d559.png',
        81, NULL, 100, 1, 18.00),
       (130, '鲜肉云吞',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片2-20250906-f8a88394-38e0-4fad-af1f-be8d0923cc89.png',
        82, NULL, 77, 1, 10.00),
       (131, '香辣烤鱼',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片17-20250906-95faccf7-7964-4b81-86c1-7af892b6a381.png',
        82, NULL, 92, 1, 88.00),
       (132, '白灼菜心',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片22-20250906-7fdadff3-f28b-4084-a4d5-ad3ea8a9d559.png',
        82, NULL, 100, 1, 18.00),
       (133, '红烧猪蹄',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片23-20250906-ea354130-135c-498e-a49e-c5311060d807.png',
        83, NULL, 98, 1, 88.00),
       (134, '米饭',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片15-20250906-3bf6ff9d-beac-42ed-b3ae-5cca69e2f30a.png',
        83, NULL, 90, 1, 2.00),
       (135, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        83, NULL, 102, 1, 68.00),
       (136, '香辣烤鱼',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片17-20250906-95faccf7-7964-4b81-86c1-7af892b6a381.png',
        84, NULL, 92, 1, 88.00),
       (137, '清蒸鲈鱼',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片16-20250906-87a4c844-0dca-4cbb-932e-60f8f06ca33d.png',
        84, NULL, 91, 1, 52.80),
       (138, '香辣烤鱼',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片17-20250906-95faccf7-7964-4b81-86c1-7af892b6a381.png',
        85, NULL, 92, 1, 88.00),
       (139, '清蒸鲈鱼',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片16-20250906-87a4c844-0dca-4cbb-932e-60f8f06ca33d.png',
        85, NULL, 91, 1, 52.80),
       (140, '清蒸鲈鱼',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片16-20250906-87a4c844-0dca-4cbb-932e-60f8f06ca33d.png',
        86, NULL, 91, 1, 52.80),
       (141, '红烧猪蹄',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片23-20250906-ea354130-135c-498e-a49e-c5311060d807.png',
        87, NULL, 98, 1, 88.00),
       (142, '白灼菜心',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片22-20250906-7fdadff3-f28b-4084-a4d5-ad3ea8a9d559.png',
        88, NULL, 100, 1, 18.00),
       (143, '案板上的鱼肉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/20-20250906-82d0950b-5e5e-4554-bde4-22a52aaa2f84.png',
        88, NULL, 102, 1, 68.00),
       (144, '米饭',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片15-20250906-3bf6ff9d-beac-42ed-b3ae-5cca69e2f30a.png',
        88, NULL, 90, 1, 2.00),
       (145, '凉拌青瓜',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片24-20250906-fb7f1859-562a-442c-b189-c1755b8704fe.png',
        89, NULL, 99, 1, 28.00),
       (146, '白灼菜心',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片22-20250906-7fdadff3-f28b-4084-a4d5-ad3ea8a9d559.png',
        89, NULL, 100, 1, 18.00),
       (147, '红烧猪蹄',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片23-20250906-ea354130-135c-498e-a49e-c5311060d807.png',
        89, NULL, 98, 1, 88.00);
/*!40000 ALTER TABLE `order_detail`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders`
(
    `id`                      bigint         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `number`                  varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin  DEFAULT NULL COMMENT '订单号',
    `status`                  int            NOT NULL                                DEFAULT '1' COMMENT '订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款',
    `user_id`                 bigint         NOT NULL COMMENT '下单用户',
    `address_book_id`         bigint                                                 DEFAULT NULL COMMENT '地址id',
    `order_time`              datetime       NOT NULL COMMENT '下单时间',
    `checkout_time`           datetime                                               DEFAULT NULL COMMENT '结账时间',
    `pay_method`              int            NOT NULL                                DEFAULT '1' COMMENT '支付方式 1微信,2支付宝',
    `pay_status`              tinyint        NOT NULL                                DEFAULT '0' COMMENT '支付状态 0未支付 1已支付 2退款',
    `amount`                  decimal(10, 2) NOT NULL COMMENT '实收金额',
    `remark`                  varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '备注',
    `phone`                   varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin  DEFAULT NULL COMMENT '手机号',
    `address`                 varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '地址',
    `user_name`               varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin  DEFAULT NULL COMMENT '用户名称',
    `consignee`               varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin  DEFAULT NULL COMMENT '收货人',
    `cancel_reason`           varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '订单取消原因',
    `rejection_reason`        varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '订单拒绝原因',
    `cancel_time`             datetime                                               DEFAULT NULL COMMENT '订单取消时间',
    `estimated_delivery_time` datetime                                               DEFAULT NULL COMMENT '预计送达时间',
    `delivery_status`         tinyint(1)                                             DEFAULT '1' COMMENT '配送状态  1立即送出  0选择具体时间',
    `delivery_time`           datetime                                               DEFAULT NULL COMMENT '送达时间',
    `pack_amount`             int                                                    DEFAULT NULL COMMENT '打包费',
    `tableware_number`        int                                                    DEFAULT NULL COMMENT '餐具数量',
    `tableware_status`        tinyint(1)     NOT NULL                                DEFAULT '1' COMMENT '餐具数量状态  1按餐量提供  0选择具体数量',
    `order_type`              int                                                    DEFAULT '1' COMMENT '订单类型 1外卖 2堂食',
    `pickup_number`           varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin  DEFAULT NULL COMMENT '取餐号（堂食专用）',
    `is_comment`              tinyint                                                DEFAULT '0' COMMENT '是否评论',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 90
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin
  ROW_FORMAT = DYNAMIC COMMENT ='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders`
    DISABLE KEYS */;
INSERT INTO `orders`
VALUES (14, '1949726541399330816', 5, 1, 1, '2025-07-28 15:00:06', '2025-07-28 15:00:12', 1, 1, 172.80, '',
        '13566523654', '正佳广场', NULL, '马龙', NULL, NULL, NULL, '2025-07-28 15:29:59', 1, '2025-07-28 15:00:38', 3,
        1, 1, 1, NULL, 0),
       (15, '1949736322893746176', 5, 1, NULL, '2025-07-28 15:38:58', '2025-07-28 15:39:16', 1, 1, 146.00, '', NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, '2025-07-28 16:08:43', NULL, '2025-07-28 15:39:37', 0, 1, 1, 2, 'X606', 0),
       (16, '1949736620030824448', 5, 1, 1, '2025-07-28 15:40:09', '2025-07-28 15:40:16', 1, 1, 142.80, '',
        '13566523654', '正佳广场', NULL, '马龙', NULL, NULL, NULL, '2025-07-28 16:09:51', 1, '2025-07-28 15:41:04', 3,
        1, 1, 1, NULL, 0),
       (17, '1961037730968375296', 5, 2, 2, '2025-08-28 20:06:44', '2025-08-28 20:06:51', 1, 1, 10.50, '',
        '13622426587', '11111', NULL, '张三', NULL, NULL, NULL, '2025-08-28 20:35:28', 1, '2025-08-28 20:07:59', 3, 1,
        1, 1, NULL, 0),
       (18, '1961075902863511552', 5, 2, NULL, '2025-08-28 22:38:25', '2025-08-28 22:38:32', 2, 1, 36.00, '', NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, '2025-08-28 23:08:12', NULL, '2025-08-29 19:13:57', 0, 0, 0, 2, 'R523', 1),
       (19, '1961077731944960000', 5, 2, NULL, '2025-08-28 22:45:41', '2025-08-28 22:46:35', 1, 1, 18.00, '', NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, '2025-08-28 23:14:44', NULL, '2025-08-29 19:12:48', 0, 0, 0, 2, 'V632', 0),
       (20, '1961078232048603136', 6, 2, 2, '2025-08-28 22:47:40', '2025-08-28 22:47:43', 1, 1, 20.00, '',
        '13622426587', '11111', NULL, '张三', '111', NULL, '2025-08-28 22:50:18', '2025-08-28 23:17:34', 1,
        '2025-08-28 22:48:21', 3, 0, 0, 1, NULL, 0),
       (21, '1961079309330419712', 6, 2, 2, '2025-08-28 22:51:57', '2025-08-28 22:52:40', 1, 2, 20.00, '',
        '13622426587', '11111', NULL, '张三', '订单超时，已自动取消！', NULL, '2025-08-28 22:56:58',
        '2025-08-28 23:21:51', 1, NULL, 3, 0, 0, 1, NULL, 0),
       (22, '1961079562846736384', 5, 2, NULL, '2025-08-28 22:52:58', '2025-08-28 22:53:11', 1, 1, 18.00, '', NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, '2025-08-28 23:22:47', NULL, '2025-08-29 19:12:46', 0, 0, 0, 2, 'W136', 0),
       (23, '1961289573334126592', 5, 2, 2, '2025-08-29 12:47:28', '2025-08-29 12:47:39', 1, 1, 90.00, '',
        '13622426587', '11111', NULL, '张三', NULL, NULL, NULL, '2025-08-29 13:17:20', 1, '2025-08-29 12:54:38', 3, 1,
        1, 1, NULL, 0),
       (24, '1961350690194460672', 5, 2, 2, '2025-08-29 16:50:19', '2025-08-29 16:50:23', 1, 1, 90.00, '',
        '13622426587', '11111', NULL, '张三', NULL, NULL, NULL, '2025-08-29 17:20:13', 1, '2025-08-29 16:50:43', 3, 1,
        1, 1, NULL, 0),
       (25, '1961351765068746752', 6, 2, 2, '2025-08-29 16:54:36', '2025-08-29 16:54:40', 1, 1, 20.00, '',
        '13622426587', '天一庄', NULL, '张三', '不想做了', '不想做了', '2025-08-29 16:54:59', '2025-08-29 17:23:51', 1,
        NULL, 3, 1, 1, 1, NULL, 0),
       (26, '1961352731893567488', 5, 2, 2, '2025-08-29 16:58:26', '2025-08-29 16:58:30', 1, 1, 20.00, '',
        '13622426587', '天一庄', NULL, '张三', NULL, NULL, NULL, '2025-08-29 17:28:22', 1, '2025-08-29 17:03:06', 3, 1,
        1, 1, NULL, 0),
       (27, '1961356147902517248', 5, 2, NULL, '2025-08-29 17:12:01', '2025-08-29 17:12:04', 1, 1, 18.00, '', NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, '2025-08-29 17:41:55', NULL, '2025-08-29 19:11:21', 0, 1, 1, 2, 'I521', 0),
       (28, '1961384935805816832', 5, 2, NULL, '2025-08-29 19:06:24', '2025-08-29 19:06:55', 1, 1, 18.00, '', NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, '2025-08-29 19:36:16', NULL, '2025-08-29 19:11:14', 0, 1, 1, 2, 'P316', 0),
       (29, '1961387272708427776', 5, 2, NULL, '2025-08-29 19:15:41', '2025-08-29 19:15:46', 1, 1, 46.00, '', NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, '2025-08-29 19:45:35', NULL, '2025-08-29 19:17:09', 0, 1, 1, 2, 'H404', 0),
       (30, '1961387878546280448', 5, 2, NULL, '2025-08-29 19:18:06', '2025-08-29 19:18:09', 1, 1, 68.00, '', NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, '2025-08-29 19:47:59', NULL, '2025-08-29 19:19:56', 0, 1, 1, 2, 'A328', 0),
       (31, '1961402223929987072', 6, 2, 2, '2025-08-29 20:15:06', '2025-08-29 20:15:11', 1, 1, 90.00, '',
        '13622426587', '天一庄', NULL, '张三', '已休息', '已休息', '2025-08-29 20:15:40', '2025-08-29 20:45:01', 1,
        NULL, 3, 1, 1, 1, NULL, 0),
       (32, '1961402726432772096', 6, 2, 2, '2025-08-29 20:17:06', '2025-08-29 20:17:09', 1, 1, 90.00, '',
        '13622426587', '天一庄', NULL, '张三', '半路撒了', NULL, '2025-08-29 20:17:37', '2025-08-29 20:47:00', 1, NULL,
        3, 0, 0, 1, NULL, 0),
       (33, '1963922130131357696', 5, 2, 2, '2025-09-05 19:08:18', '2025-09-05 19:08:23', 1, 1, 105.00, '',
        '13622426587', '天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-05 19:37:47', 1, '2025-09-05 19:09:11', 3, 1,
        1, 1, NULL, 0),
       (34, '1963973235309678592', 5, 3, 3, '2025-09-05 22:31:23', '2025-09-05 22:31:27', 1, 1, 118.00, '',
        '13622222222', '香洲街道', NULL, '炸串王', NULL, NULL, NULL, '2025-09-05 23:01:17', 1, '2025-09-05 22:39:18', 3,
        1, 1, 1, NULL, 0),
       (35, '1963976070151671808', 5, 3, 3, '2025-09-05 22:42:39', '2025-09-05 22:42:42', 1, 1, 156.00, '',
        '13622222222', '香洲街道', NULL, '炸串王', NULL, NULL, NULL, '2025-09-05 23:12:33', 1, '2025-09-05 22:43:06', 3,
        1, 1, 1, NULL, 0),
       (36, '1963978640035287040', 6, 3, 3, '2025-09-05 22:52:51', NULL, 1, 0, 110.00, '', '13622222222', '香洲街道',
        NULL, '炸串王', '等待时间长', NULL, '2025-09-18 12:43:19', '2025-09-05 23:21:44', 1, NULL, 3, 1, 1, 1, NULL, 0),
       (37, '1963980420848685056', 6, 3, 3, '2025-09-05 22:59:56', NULL, 1, 0, 90.00, '', '13622222222', '香洲街道',
        NULL, '炸串王', '11', NULL, '2025-09-05 23:00:05', '2025-09-05 23:29:30', 1, NULL, 3, 1, 1, 1, NULL, 0),
       (38, '1963980569150885888', 6, 3, 3, '2025-09-05 23:00:31', '2025-09-05 23:00:39', 1, 1, 30.00, '',
        '13622222222', '香洲街道', NULL, '炸串王', NULL, 'a ', '2025-09-05 23:08:12', '2025-09-05 23:30:12', 1, NULL, 3,
        1, 1, 1, NULL, 0),
       (39, '1963980849036791808', 6, 3, 3, '2025-09-05 23:01:38', NULL, 1, 0, 30.00, '', '13622222222', '香洲街道',
        NULL, '炸串王', '地址/电话填错了', NULL, '2025-09-05 23:01:57', '2025-09-05 23:31:33', 1, NULL, 3, 1, 1, 1,
        NULL, 0),
       (40, '1963980977080504320', 6, 3, 3, '2025-09-05 23:02:09', NULL, 1, 0, 30.00, '', '13622222222', '香洲街道',
        NULL, '炸串王', '等待时间长', NULL, '2025-09-05 23:09:21', '2025-09-05 23:32:04', 1, NULL, 3, 0, 0, 1, NULL, 0),
       (41, '1963981649800728576', 6, 3, 3, '2025-09-05 23:04:49', '2025-09-05 23:05:22', 1, 1, 30.00, '',
        '13622222222', '香洲街道', NULL, '炸串王', NULL, '11', '2025-09-05 23:08:02', '2025-09-05 23:34:43', 1, NULL, 3,
        0, 0, 1, NULL, 0),
       (42, '1963982286315720704', 6, 3, 3, '2025-09-05 23:07:21', NULL, 1, 0, 30.00, '', '13622222222', '香洲街道',
        NULL, '炸串王', '等待时间长', NULL, '2025-09-05 23:08:58', '2025-09-05 23:37:14', 1, NULL, 3, 0, 0, 1, NULL, 0),
       (43, '1963983794503553024', 5, 3, 3, '2025-09-05 23:13:20', '2025-09-05 23:13:23', 1, 1, 30.00, '',
        '13622222222', '香洲街道', NULL, '炸串王', NULL, NULL, NULL, '2025-09-05 23:43:16', 1, '2025-09-05 23:13:36', 3,
        1, 1, 1, NULL, 0),
       (44, '1963992054220591104', 5, 3, 3, '2025-09-05 23:46:10', '2025-09-05 23:46:13', 1, 1, 20.00, '',
        '13622222222', '香洲街道', NULL, '炸串王', NULL, NULL, NULL, '2025-09-06 00:16:06', 1, '2025-09-05 23:46:24', 3,
        0, 0, 1, NULL, 1),
       (45, '1963992731609075712', 5, 3, 3, '2025-09-05 23:48:51', '2025-09-05 23:48:54', 1, 1, 58.00, '',
        '13622222222', '香洲街道', NULL, '炸串王', NULL, NULL, NULL, '2025-09-06 00:18:46', 1, '2025-09-05 23:49:05', 3,
        1, 0, 1, NULL, 1),
       (46, '1964009408262443008', 5, 3, 3, '2025-09-06 00:55:07', '2025-09-06 00:55:11', 1, 1, 76.00, '',
        '13622222222', '香洲街道', NULL, '炸串王', NULL, NULL, NULL, '2025-09-06 01:25:02', 1, '2025-09-06 00:55:23', 3,
        0, 0, 1, NULL, 1),
       (47, '1964187049850441728', 5, 2, 2, '2025-09-06 12:41:00', '2025-09-06 12:41:04', 1, 1, 138.00, '',
        '13622426587', '天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-06 13:10:54', 1, '2025-09-06 12:41:16', 3, 1,
        1, 1, NULL, 0),
       (48, '1964193063714820096', 5, 2, 2, '2025-09-06 13:04:54', '2025-09-06 13:04:57', 1, 1, 30.00, '',
        '13622426587', '天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-06 13:34:48', 1, '2025-09-06 13:05:08', 3, 1,
        1, 1, NULL, 0),
       (49, '1964209229434851328', 5, 2, 2, '2025-09-06 14:09:08', '2025-09-06 14:09:12', 1, 1, 90.00, '',
        '13622426587', '天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-06 14:39:04', 1, '2025-09-06 14:09:24', 3, 0,
        0, 1, NULL, 0),
       (50, '1964228474755485696', 5, 2, 2, '2025-09-06 15:25:37', '2025-09-06 15:25:40', 1, 1, 178.00, '',
        '13622426587', '天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-06 15:55:32', 1, '2025-09-06 16:22:56', 3, 1,
        1, 1, NULL, 1),
       (51, '1964228611787591680', 5, 2, 2, '2025-09-06 15:26:09', '2025-09-06 15:26:13', 1, 1, 58.00, '',
        '13622426587', '天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-06 15:56:02', 1, '2025-09-06 16:22:57', 3, 0,
        0, 1, NULL, 0),
       (52, '1964242648789291008', 5, 2, 2, '2025-09-06 16:21:56', '2025-09-06 16:22:00', 1, 1, 90.00, '',
        '13622426587', '天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-06 16:51:52', 1, '2025-09-06 16:22:48', 3, 0,
        0, 1, NULL, 0),
       (53, '1964242723431124992', 5, 2, 2, '2025-09-06 16:22:14', '2025-09-06 16:22:17', 1, 1, 70.00, '',
        '13622426587', '天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-06 16:52:09', 1, '2025-09-06 16:23:02', 3, 0,
        0, 1, NULL, 0),
       (54, '1964249640207519744', 5, 2, 2, '2025-09-06 16:49:43', '2025-09-06 16:49:46', 1, 1, 90.00, '',
        '13622426587', '天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-06 17:19:37', 1, '2025-09-06 16:49:58', 3, 0,
        0, 1, NULL, 0),
       (55, '1964346018401226752', 5, 2, 2, '2025-09-06 23:12:41', '2025-09-06 23:12:45', 1, 1, 20.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-06 23:42:30', 1,
        '2025-09-06 23:13:23', 3, 0, 0, 1, NULL, 1),
       (56, '1964348928988155904', 5, 2, 2, '2025-09-06 23:24:15', '2025-09-06 23:24:19', 1, 1, 60.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-06 23:54:10', 1,
        '2025-09-06 23:24:37', 3, 0, 0, 1, NULL, 0),
       (57, '1965213591212789760', 5, 2, 2, '2025-09-09 08:40:07', '2025-09-09 08:40:11', 1, 1, 70.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-09 09:09:54', 1,
        '2025-09-09 08:40:39', 3, 1, 1, 1, NULL, 0),
       (58, '1965372484828139520', 5, 2, 2, '2025-09-09 19:11:30', '2025-09-09 19:11:35', 1, 1, 292.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-09 19:41:21', 1,
        '2025-09-09 19:12:15', 3, 1, 1, 1, NULL, 1),
       (59, '1965375466835677184', 5, 2, 2, '2025-09-09 19:23:21', '2025-09-09 19:24:09', 1, 1, 70.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-09 19:53:16', 1,
        '2025-09-09 19:32:33', 3, 1, 1, 1, NULL, 1),
       (60, '1965378573023973376', 5, 2, 2, '2025-09-09 19:35:41', '2025-09-09 19:35:45', 1, 1, 70.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-09 20:05:37', 1,
        '2025-09-09 19:37:34', 3, 1, 1, 1, NULL, 1),
       (61, '1965826104686153728', 6, 2, 2, '2025-09-11 01:14:01', '2025-09-11 01:14:05', 1, 1, 20.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, '11', '2025-09-11 02:32:27',
        '2025-09-11 01:43:56', 1, NULL, 3, 1, 1, 1, NULL, 0),
       (62, '1965994125346082816', 5, 2, 2, '2025-09-11 12:21:41', '2025-09-11 12:21:44', 1, 1, 434.80, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, NULL, NULL, '2025-09-11 12:51:35', 1,
        '2025-09-11 12:22:23', 3, 1, 1, 1, NULL, 1),
       (63, '1967406225800957952', 5, 3, 3, '2025-09-15 09:52:52', '2025-09-15 09:52:55', 1, 1, 176.00, '',
        '13622222222', '广东省汕尾市城区香洲街道', NULL, '炸串王', NULL, NULL, NULL, '2025-09-15 10:22:45', 1,
        '2025-09-15 09:53:34', 3, 1, 1, 1, NULL, 1),
       (64, '1968536023369650176', 5, 3, 3, '2025-09-18 12:42:16', '2025-09-18 12:42:22', 1, 1, 272.00, '',
        '13622222222', '广东省汕尾市城区香洲街道', NULL, '炸串王', NULL, NULL, NULL, '2025-09-18 13:12:10', 1,
        '2025-09-18 12:43:04', 3, 1, 1, 1, NULL, 1),
       (65, '1969764304429387776', 5, 3, 3, '2025-09-21 22:03:01', '2025-09-21 22:03:05', 1, 1, 90.00, '',
        '13622222222', '广东省汕尾市城区香洲街道', NULL, '炸串王', NULL, NULL, NULL, '2025-09-21 22:32:54', 1,
        '2025-09-21 22:03:37', 3, 1, 1, 1, NULL, 1),
       (66, '1974836374444052480', 5, 2, 2, '2025-10-05 21:57:37', '2025-10-05 21:57:41', 1, 1, 116.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, NULL, NULL, '2025-10-05 22:27:28', 1,
        '2025-10-05 21:58:07', 3, 0, 0, 1, NULL, 1),
       (67, '1975209851135070208', 6, 2, 2, '2025-10-06 22:41:41', NULL, 1, 0, 70.00, '', '13622426587',
        '广东省广州市天河区天一庄', NULL, '张三', '无', NULL, '2025-10-06 22:43:01', '2025-10-06 23:11:10', 1, NULL, 3,
        1, 1, 1, NULL, 0),
       (68, '1978472068395700224', 6, 2, 2, '2025-10-15 22:44:34', NULL, 1, 0, 20.00, '', '13622426587',
        '广东省广州市天河区天一庄', NULL, '张三', '等待时间长', NULL, '2025-10-15 22:45:19', '2025-10-15 23:14:24', 1,
        NULL, 3, 1, 1, 1, NULL, 0),
       (69, '1978473984404099072', 6, 2, 2, '2025-10-15 22:52:11', NULL, 1, 0, 20.00, '', '13622426587',
        '广东省广州市天河区天一庄', NULL, '张三', '地址/电话填错了', NULL, '2025-10-15 23:15:29', '2025-10-15 23:22:04',
        1, NULL, 3, 1, 1, 1, NULL, 0),
       (70, '1978474054549639168', 6, 2, 2, '2025-10-15 22:52:28', '2025-10-15 22:56:16', 1, 1, 70.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, '不想做了', '2025-10-16 11:39:06',
        '2025-10-15 23:22:23', 1, NULL, 3, 0, 0, 1, NULL, 0),
       (71, '1978475757030215680', 6, 2, 2, '2025-10-15 22:59:13', NULL, 1, 0, 70.00, '', '13622426587',
        '广东省广州市天河区天一庄', NULL, '张三', '订单超时，已自动取消！', NULL, '2025-10-15 23:04:13',
        '2025-10-15 23:29:08', 1, NULL, 3, 1, 1, 1, NULL, 0),
       (72, '1978476394568617984', 6, 2, 2, '2025-10-15 23:01:45', NULL, 1, 0, 70.00, '', '13622426587',
        '广东省广州市天河区天一庄', NULL, '张三', '等待时间长', NULL, '2025-10-15 23:10:46', '2025-10-15 23:31:41', 1,
        NULL, 3, 0, 0, 1, NULL, 0),
       (73, '1978480365710479360', 6, 2, 2, '2025-10-15 23:17:32', NULL, 1, 0, 30.00, '', '13622426587',
        '广东省广州市天河区天一庄', NULL, '张三', '订单超时，已自动取消！', NULL, '2025-10-15 23:22:39',
        '2025-10-15 23:47:28', 1, NULL, 3, 1, 1, 1, NULL, 0),
       (74, '1978684214127759360', 6, 2, 2, '2025-10-16 12:47:33', '2025-10-16 12:47:39', 1, 2, 170.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', '订单未支付已超时，自动取消', NULL,
        '2025-10-16 12:47:39', '2025-10-16 13:17:23', 1, NULL, 3, 1, 1, 1, NULL, 0),
       (75, '1978684536854286336', 6, 2, 2, '2025-10-16 12:48:50', '2025-10-16 12:48:54', 1, 2, 90.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', '订单未支付已超时，自动取消', NULL,
        '2025-10-16 12:48:54', '2025-10-16 13:18:42', 1, NULL, 3, 1, 1, 1, NULL, 0),
       (76, '1978685216432197632', 6, 2, 2, '2025-10-16 12:51:32', '2025-10-16 12:51:42', 1, 2, 70.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', '订单未支付已超时，自动取消', NULL,
        '2025-10-16 12:51:42', '2025-10-16 13:21:24', 1, NULL, 3, 1, 1, 1, NULL, 0),
       (77, '1978685387958259712', 6, 2, 2, '2025-10-16 12:52:13', '2025-10-16 12:52:18', 1, 2, 70.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', '订单未支付已超时，自动取消', NULL,
        '2025-10-16 12:52:18', '2025-10-16 13:22:09', 1, NULL, 3, 0, 0, 1, NULL, 0),
       (78, '1978685833095548928', 6, 2, 2, '2025-10-16 12:53:59', '2025-10-16 12:54:06', 1, 1, 20.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, '1111', '2025-10-16 12:59:25',
        '2025-10-16 13:23:55', 1, NULL, 3, 1, 1, 1, NULL, 0),
       (79, '1978686177397575680', 5, 2, 2, '2025-10-16 12:55:22', '2025-10-16 12:55:25', 1, 1, 30.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, NULL, NULL, '2025-10-16 13:25:16', 1,
        '2025-10-16 15:52:17', 3, 1, 1, 1, NULL, 0),
       (80, '1978686912839421952', 5, 2, 2, '2025-10-16 12:58:17', '2025-10-16 12:58:30', 1, 1, 20.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, NULL, NULL, '2025-10-16 13:28:09', 1,
        '2025-10-16 15:52:14', 3, 1, 1, 1, NULL, 0),
       (81, '1978689727011885056', 5, 2, 2, '2025-10-16 13:09:28', '2025-10-16 13:09:36', 1, 1, 116.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, NULL, NULL, '2025-10-16 13:39:13', 0,
        '2025-10-16 13:12:36', 3, 1, 1, 1, NULL, 1),
       (82, '1978730801977757696', 5, 2, 2, '2025-10-16 15:52:41', '2025-10-16 15:52:44', 2, 1, 118.00, '',
        '13622426587', '广东省广州市天河区天一庄', NULL, '张三', NULL, NULL, NULL, '2025-10-16 16:22:34', 1,
        '2025-10-16 15:53:04', 3, 1, 1, 1, NULL, 0),
       (83, '1985178026152955904', 3, 2, 4, '2025-11-03 10:51:39', '2025-11-03 10:51:47', 1, 1, 160.00, '',
        '13622420000', '广东省广州市天河区阳光都会广场', NULL, '李四', NULL, NULL, NULL, '2025-11-03 11:21:27', 1, NULL,
        3, 1, 1, 1, NULL, 0),
       (84, '1985178242415464448', 5, 2, 4, '2025-11-03 10:52:30', '2025-11-03 10:52:34', 1, 1, 142.80, '',
        '13622420000', '广东省广州市天河区阳光都会广场', NULL, '李四', NULL, NULL, NULL, '2025-11-03 11:22:25', 1,
        '2025-11-03 22:43:21', 3, 1, 1, 1, NULL, 0),
       (85, '1985178349525405696', 5, 2, 4, '2025-11-03 10:52:56', '2025-11-03 10:53:00', 1, 1, 142.80, '',
        '13622420000', '广东省广州市天河区阳光都会广场', NULL, '李四', NULL, NULL, NULL, '2025-11-03 11:22:51', 1,
        '2025-11-03 22:43:18', 3, 0, 0, 1, NULL, 1),
       (86, '1985179912633454592', 3, 2, 4, '2025-11-03 10:59:09', '2025-11-03 10:59:12', 1, 1, 54.80, '',
        '13622420000', '广东省广州市天河区阳光都会广场', NULL, '李四', NULL, NULL, NULL, '2025-11-03 11:29:04', 1, NULL,
        3, 1, 1, 1, NULL, 0),
       (87, '1985502418066083840', 6, 2, 4, '2025-11-04 08:20:40', NULL, 1, 0, 90.00, '', '13622420000',
        '广东省广州市天河区阳光都会广场', NULL, '李四', '订单超时，已自动取消！', NULL, '2025-11-04 08:25:41',
        '2025-11-04 08:48:35', 1, NULL, 3, 1, 1, 1, NULL, 0),
       (88, '1985503365618077696', 2, 2, 4, '2025-11-04 08:24:26', '2025-11-04 08:25:12', 1, 1, 90.00, '',
        '13622420000', '广东省广州市天河区阳光都会广场', NULL, '李四', NULL, NULL, NULL, '2025-11-04 08:53:00', 1, NULL,
        3, 1, 1, 1, NULL, 0),
       (89, '1985505527366553600', 6, 2, 4, '2025-11-04 08:33:01', '2025-11-04 08:33:28', 1, 2, 136.00, '',
        '13622420000', '广东省广州市天河区阳光都会广场', NULL, '李四', '有一个菜品不想要了', NULL,
        '2025-11-10 08:58:48', '2025-11-04 09:01:10', 1, NULL, 3, 1, 1, 1, NULL, 0);
/*!40000 ALTER TABLE `orders`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pickup_code`
--

DROP TABLE IF EXISTS `pickup_code`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pickup_code`
(
    `id`          bigint                                                NOT NULL AUTO_INCREMENT COMMENT '主键',
    `date`        date                                                  NOT NULL COMMENT '日期',
    `code`        varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '取餐码',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_date_code` (`date`, `code`) USING BTREE COMMENT '日期和取餐码的唯一索引',
    KEY `idx_date` (`date`) USING BTREE COMMENT '日期索引'
) ENGINE = InnoDB
  AUTO_INCREMENT = 15
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin
  ROW_FORMAT = DYNAMIC COMMENT ='取餐码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pickup_code`
--

LOCK TABLES `pickup_code` WRITE;
/*!40000 ALTER TABLE `pickup_code`
    DISABLE KEYS */;
INSERT INTO `pickup_code`
VALUES (4, '2025-07-27', 'N668', '2025-07-27 22:26:09'),
       (5, '2025-07-28', 'E089', '2025-07-28 03:59:53'),
       (6, '2025-07-28', 'Y841', '2025-07-28 14:13:15'),
       (7, '2025-07-28', 'X606', '2025-07-28 15:38:58'),
       (8, '2025-08-28', 'R523', '2025-08-28 22:38:24'),
       (9, '2025-08-28', 'V632', '2025-08-28 22:45:41'),
       (10, '2025-08-28', 'W136', '2025-08-28 22:52:57'),
       (11, '2025-08-29', 'I521', '2025-08-29 17:12:00'),
       (12, '2025-08-29', 'P316', '2025-08-29 19:06:24'),
       (13, '2025-08-29', 'H404', '2025-08-29 19:15:41'),
       (14, '2025-08-29', 'A328', '2025-08-29 19:18:05');
/*!40000 ALTER TABLE `pickup_code`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sensitive`
--

DROP TABLE IF EXISTS `sensitive`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sensitive`
(
    `id`           int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `sensitives`   varchar(50) DEFAULT NULL COMMENT '敏感词',
    `created_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 24
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='敏感词表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sensitive`
--

LOCK TABLES `sensitive` WRITE;
/*!40000 ALTER TABLE `sensitive`
    DISABLE KEYS */;
INSERT INTO `sensitive`
VALUES (12, '冰毒', '2025-09-15 15:22:41', '2025-09-15 15:22:41'),
       (13, '有毒', '2025-09-15 15:23:34', '2025-09-15 15:23:34'),
       (15, '6668', '2025-09-15 15:39:53', '2025-09-15 15:55:04');
/*!40000 ALTER TABLE `sensitive`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `setmeal`
--

DROP TABLE IF EXISTS `setmeal`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `setmeal`
(
    `id`          bigint                                                NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_id` bigint                                                NOT NULL COMMENT '分类id',
    `name`        varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '套餐名称',
    `price`       decimal(10, 2)                                        NOT NULL COMMENT '套餐价格',
    `status`      int                                                    DEFAULT '1' COMMENT '状态 0:停用 1:启用',
    `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '描述信息',
    `image`       varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
    `hot_spot`    bigint                                                 DEFAULT '0' COMMENT '套餐热度',
    `create_time` datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                               DEFAULT NULL COMMENT '更新时间',
    `create_user` bigint                                                 DEFAULT NULL COMMENT '创建人',
    `update_user` bigint                                                 DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `idx_setmeal_name` (`name`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin
  ROW_FORMAT = DYNAMIC COMMENT ='套餐';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `setmeal`
--

LOCK TABLES `setmeal` WRITE;
/*!40000 ALTER TABLE `setmeal`
    DISABLE KEYS */;
INSERT INTO `setmeal`
VALUES (1, 35, '全家福套餐', 108.00, 1, '1',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片25-20250906-bc4c19fd-9019-4766-bbd0-fac7de1507cb.png',
        1, '2025-07-27 21:23:05', '2025-09-06 23:23:18', 1, 1),
       (2, 35, '一人餐', 28.00, 1, NULL,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片26-20250906-24b254e2-a63f-47ff-881b-c28c78c1f330.png',
        3, '2025-07-28 14:36:03', '2025-09-06 17:16:32', 1, 1),
       (3, 38, '广式风味1', 68.00, 1, '',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/25-20250907-180d6917-5d24-412f-81fa-eb1a505fe6dd.png',
        0, '2025-09-07 12:19:13', '2025-09-07 12:19:17', 1, 1),
       (4, 38, '广式风味2', 108.00, 1, NULL,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/25-20250907-851c549c-f755-429e-9090-19d7c3970419.png',
        1, '2025-09-07 12:20:08', '2025-09-21 20:10:44', 1, 1);
/*!40000 ALTER TABLE `setmeal`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `setmeal_dish`
--

DROP TABLE IF EXISTS `setmeal_dish`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `setmeal_dish`
(
    `id`         bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `setmeal_id` bigint NOT NULL COMMENT '套餐id',
    `dish_id`    bigint NOT NULL COMMENT '菜品id',
    `name`       varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin  DEFAULT NULL COMMENT '菜品名称',
    `price`      decimal(10, 2)                                         DEFAULT NULL COMMENT '菜品价格',
    `copies`     int    NOT NULL COMMENT '份数',
    `image`      varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 24
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin
  ROW_FORMAT = DYNAMIC COMMENT ='套餐菜品关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `setmeal_dish`
--

LOCK TABLES `setmeal_dish` WRITE;
/*!40000 ALTER TABLE `setmeal_dish`
    DISABLE KEYS */;
INSERT INTO `setmeal_dish`
VALUES (12, 2, 90, '米饭', 2.00, 1,
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/4-20250728-6244a932-a9b7-4ecf-80b8-13c022635599.png'),
       (13, 2, 76, '西兰花', 11.00, 1,
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/11-20250728-0aca7138-6368-4431-a828-663ae88f6a88.png'),
       (14, 2, 94, '广式烧鸭', 28.00, 1,
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/28fcf267-5588-476b-a0e9-62b7dc391db2-20250728-f4a3f952-a3e6-4f6f-8a8e-c4db7a79bfbe.png'),
       (15, 1, 93, '清炒上海青', 18.00, 1,
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/9-20250728-473c1260-4d6f-4742-a790-fa9fdc3232e9.png'),
       (16, 1, 90, '米饭', 2.00, 1,
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/4-20250728-6244a932-a9b7-4ecf-80b8-13c022635599.png'),
       (17, 1, 99, '凉拌青瓜', 28.00, 1,
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/d66293dd-0507-4178-a373-7f058b91e64a-20250728-85f3b1b1-32ad-4e99-a279-a8c5fa0243a6.jpeg'),
       (18, 1, 92, '香辣烤鱼', 88.00, 1,
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/21-20250728-6d512be6-2871-46d6-a920-e6db98467145.png'),
       (19, 1, 95, '白切鸡', 58.00, 1,
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/d221e5da-d45e-4849-9f96-c89903e5bd2f-20250728-7045ba39-0e5c-4f09-9397-1bf0459365fb.png'),
       (20, 3, 94, '广式烧鸭', 28.00, 1,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片19-20250906-cc6632cd-1e30-4c6e-8600-b220205b6f3a.png'),
       (21, 3, 95, '白切鸡', 58.00, 1,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片20-20250906-bfc446da-b256-421b-add2-ea127e6bf14e.png'),
       (22, 4, 97, '烧鹅', 68.00, 1,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片21-20250906-7fc1dc5a-457b-4bd2-b5eb-f1cf9b216c67.png'),
       (23, 4, 98, '红烧猪蹄', 88.00, 1,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片23-20250906-ea354130-135c-498e-a49e-c5311060d807.png');
/*!40000 ALTER TABLE `setmeal_dish`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_info`
--

DROP TABLE IF EXISTS `shop_info`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_info`
(
    `id`             bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '商家信息实体id',
    `name`           varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '店家名称',
    `logo`           varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT NULL COMMENT 'logo',
    `address`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '店家地址',
    `business_hours` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci           DEFAULT NULL COMMENT '营业时间',
    `contact_phone`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci           DEFAULT NULL COMMENT '联系电话',
    `status`         int                                                           NOT NULL DEFAULT '1' COMMENT '营业状态：1营业 0休息',
    `create_time`    datetime                                                               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime                                                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = DYNAMIC COMMENT ='商家信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_info`
--

LOCK TABLES `shop_info` WRITE;
/*!40000 ALTER TABLE `shop_info`
    DISABLE KEYS */;
INSERT INTO `shop_info`
VALUES (1, '中餐厅', 'https://training-take-out.oss-cn-beijing.aliyuncs.com/logo.png', '广东省广州市天河区正佳广场',
        '08:00-22:00', '0772-1234567', 1, '2025-07-27 22:14:21', '2025-07-28 13:23:45');
/*!40000 ALTER TABLE `shop_info`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shopping_cart`
--

DROP TABLE IF EXISTS `shopping_cart`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shopping_cart`
(
    `id`          bigint         NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin  DEFAULT NULL COMMENT '商品名称',
    `image`       varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
    `user_id`     bigint         NOT NULL COMMENT '所属用户ID',
    `dish_id`     bigint                                                 DEFAULT NULL COMMENT '菜品id',
    `setmeal_id`  bigint                                                 DEFAULT NULL COMMENT '套餐id',
    `dish_flavor` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '菜品口味',
    `number`      int            NOT NULL                                DEFAULT '1' COMMENT '数量',
    `amount`      decimal(10, 2) NOT NULL COMMENT '金额',
    `create_time` datetime                                               DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 307
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin
  ROW_FORMAT = DYNAMIC COMMENT ='购物车';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shopping_cart`
--

LOCK TABLES `shopping_cart` WRITE;
/*!40000 ALTER TABLE `shopping_cart`
    DISABLE KEYS */;
INSERT INTO `shopping_cart`
VALUES (301, '蜂蜜柚子茶',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片3-20250906-c5bd0c65-596c-4bbf-b276-72397ea5b4b5.png',
        2, 78, NULL, '', 1, 7.00, '2025-11-04 10:03:52'),
       (302, '叉烧桂林粉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片9-20250906-1a8d8883-765c-4cc2-adc1-23e9778d5a0d.png',
        2, 84, NULL, '', 1, 8.50, '2025-11-04 10:03:53'),
       (303, '加米粉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片8-20250906-12f9b457-7344-45dd-a347-72c51a37a2d1.png',
        2, 83, NULL, '', 1, 1.00, '2025-11-04 10:03:54'),
       (304, '猪脚粉',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片10-20250906-2700c599-a8a7-493a-9a38-712c699162ca.png',
        2, 85, NULL, '', 1, 12.00, '2025-11-08 16:33:31'),
       (305, '原味煎蛋',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片12-20250906-5b377098-8acb-466a-91a6-3d9eaef17f4d.png',
        2, 87, NULL, '', 1, 3.00, '2025-11-08 16:33:35'),
       (306, '白切鸡',
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/图片20-20250906-bfc446da-b256-421b-add2-ea127e6bf14e.png',
        2, 95, NULL, '不要香菜', 1, 58.00, '2025-11-08 16:33:42');
/*!40000 ALTER TABLE `shopping_cart`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user`
(
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `openid`      varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin  DEFAULT NULL COMMENT '微信用户唯一标识',
    `name`        varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin  DEFAULT NULL COMMENT '姓名',
    `phone`       varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin  DEFAULT NULL COMMENT '手机号',
    `sex`         varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin   DEFAULT NULL COMMENT '性别',
    `avatar`      varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '头像',
    `create_time` datetime                                               DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = utf8mb3
  COLLATE = utf8mb3_bin
  ROW_FORMAT = DYNAMIC COMMENT ='用户信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user`
    DISABLE KEYS */;
INSERT INTO `user`
VALUES (1, 'olyK76GaEUQBOKoCILXGJ2RybKpc', 'curious', NULL, NULL,
        'https://training-take-out.oss-cn-beijing.aliyuncs.com/GZHz6JVWLrcKae52efacc8a43aeadc65d2e3c3589a8a-20250727-45081ec5-c70b-4316-a70e-4aff54cf23f5.jpeg',
        '2025-07-27 21:59:50'),
       (2, 'oWAtx7cCV7YnhR5X0qQ1xKH2zzBo', '服了', NULL, NULL,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/gkYF4pBF42a75a93e0c7cf78270f3847af98442030d1-20250906-9f48228a-6c15-42bc-a677-c8512acd8627.jpg',
        '2025-08-28 19:32:34'),
       (3, 'oWAtx7ZNaDOAnANMkS770oB8cRGk', '君无敌', NULL, NULL,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/2d77c4a928c7c2b91406fd348cebbe1.jpg',
        '2025-09-05 22:25:41'),
       (4, '123', '锦食坊小助手', NULL, NULL,
        'https://springboot-sky-itcast-take-out.oss-cn-beijing.aliyuncs.com/AI.jpg', '2025-09-15 08:10:30'),
       (5, '99999', '商家9527', NULL, NULL,
        'http://192.168.88.130:9000/training-take-out/2025/10/20/e4909f27-0a55-4b63-ba49-e5d4e3f35f3e-4.jpg',
        '2025-10-20 23:11:40');
/*!40000 ALTER TABLE `user`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2025-11-28 16:46:17
