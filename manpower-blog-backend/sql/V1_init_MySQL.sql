/*
SQLyog Community v13.3.1 (64 bit)
MySQL - 8.0.44 : Database - blog_db
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`blog_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `blog_db`;

/*Table structure for table `t_content_article` */

DROP TABLE IF EXISTS `t_content_article`;

CREATE TABLE `t_content_article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '記事主キーID',
  `title` varchar(200) NOT NULL COMMENT '記事タイトル',
  `summary` varchar(512) DEFAULT NULL COMMENT '記事概要',
  `content` longtext NOT NULL COMMENT '記事本文内容',
  `category_id` bigint NOT NULL COMMENT 'カテゴリID（t_content_category.id）',
  `author_id` bigint NOT NULL COMMENT '投稿者ID（t_sys_user.id）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '記事状態：0-下書き、1-公開済み、2-非公開',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '論理削除フラグ：0-未削除、1-削除済み',
  PRIMARY KEY (`id`),
  KEY `idx_content_article_category` (`category_id`),
  KEY `idx_content_article_author` (`author_id`),
  KEY `idx_content_article_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ブログ記事テーブル';

/*Data for the table `t_content_article` */

/*Table structure for table `t_sys_menu` */

DROP TABLE IF EXISTS `t_sys_menu`;

CREATE TABLE `t_sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '親メニューID（0は最上位）',
  `name` varchar(100) NOT NULL COMMENT 'メニュー名称',
  `type` tinyint NOT NULL COMMENT 'メニュー種別（1=ディレクトリ 2=メニュー 3=ボタン）',
  `sort` int NOT NULL DEFAULT '0' COMMENT '表示順',
  `icon` varchar(100) DEFAULT NULL COMMENT 'アイコン',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態（0=無効 1=有効）',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除 1=削除済）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `permission_id` bigint DEFAULT NULL COMMENT '関連するPermissionのID（t_sys_permission.id）、ディレクトリはNULL可',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_id_active` (`permission_id`,((case when (`is_deleted` = 0) then 0 else NULL end))),
  KEY `idx_sys_menu_parent_id` (`parent_id`),
  KEY `idx_sys_menu_type` (`type`),
  KEY `idx_sys_menu_status` (`status`),
  KEY `idx_sys_menu_is_delete` (`is_deleted`),
  KEY `idx_sys_menu_permission_id` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='システムメニュー管理テーブル';

/*Data for the table `t_sys_menu` */

insert  into `t_sys_menu`(`id`,`parent_id`,`name`,`type`,`sort`,`icon`,`status`,`is_deleted`,`created_at`,`updated_at`,`permission_id`) values 
(1,0,'システム管理',1,1,'Setting',1,0,'2026-04-02 17:25:56','2026-05-05 23:01:48',NULL),
(2,1,'ホームページ',2,1,'HomeFilled',1,0,'2026-04-02 17:26:14','2026-05-22 13:01:41',1006),
(3,1,'役割管理',1,1,'Lock',1,0,'2026-04-02 17:25:56','2026-05-05 23:18:50',NULL),
(4,3,'役割一覧',2,1,'Key',1,0,'2026-04-02 17:25:56','2026-05-22 13:01:41',1009),
(5,1,'ユーザー管理',1,1,'User',1,0,'2026-05-05 23:01:20','2026-05-24 16:03:06',NULL),
(6,5,'ユーザー一覧',2,1,'UserFilled',1,0,'2026-05-05 23:16:53','2026-05-24 16:03:32',1008),
(7,3,'権限一覧',2,1,'Finished',1,0,'2026-05-13 23:27:20','2026-05-24 16:02:38',1016),
(8,1,'メニュー管理',1,1,'Menu',1,0,'2026-05-17 12:03:23','2026-05-24 16:02:38',NULL),
(9,8,'メニュー一覧',2,1,'List',1,0,'2026-05-17 12:04:24','2026-05-24 16:02:38',1021),
(10,0,'test',1,1,'test',1,0,'2026-05-24 00:47:53','2026-05-24 00:47:53',NULL);

/*Table structure for table `t_sys_permission` */

DROP TABLE IF EXISTS `t_sys_permission`;

CREATE TABLE `t_sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '親権限ID（0=ルート）',
  `name` varchar(100) NOT NULL COMMENT '権限名',
  `code` varchar(100) NOT NULL COMMENT '権限制御コード（例：user:add / article:edit）',
  `type` tinyint NOT NULL COMMENT '権限種別（1=MENU, 2=BUTTON, 3=API）',
  `path` varchar(200) DEFAULT NULL COMMENT '対象パス（MENU/API 用）',
  `method` varchar(10) DEFAULT NULL COMMENT 'HTTPメソッド（API 用：GET/POST/PUT/DELETE）',
  `sort` int NOT NULL DEFAULT '0' COMMENT '表示順',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態（0=無効、1=有効）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code_method_active` (`code`,`method`,((case when (`is_deleted` = 0) then 0 else NULL end))),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_type` (`type`),
  KEY `idx_path_method` (`path`,`method`)
) ENGINE=InnoDB AUTO_INCREMENT=1033 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='権限マスタ（MENU/BUTTON/API）';

/*Data for the table `t_sys_permission` */

insert  into `t_sys_permission`(`id`,`parent_id`,`name`,`code`,`type`,`path`,`method`,`sort`,`status`,`created_at`,`updated_at`,`is_deleted`) values 
(1001,1009,'ロール一覧取得','sys:role:list',2,'/api/system/role','GET',1,1,'2026-02-28 23:41:34','2026-02-28 23:41:34',0),
(1002,1009,'ロール作成','sys:role:create',2,'/api/system/role','POST',2,1,'2026-02-28 23:41:34','2026-02-28 23:41:34',0),
(1003,1009,'ロール更新','sys:role:update',2,'/api/system/role/{id}','PUT',3,1,'2026-02-28 23:41:34','2026-02-28 23:41:34',0),
(1004,1009,'ロール削除','sys:role:delete',2,'/api/system/role/{id}','DELETE',4,1,'2026-02-28 23:41:34','2026-02-28 23:41:34',0),
(1005,1009,'ロール状態変更','sys:role:changeStatus',2,'/api/system/role/{id}/status','PATCH',5,1,'2026-02-28 23:41:34','2026-02-28 23:41:34',0),
(1006,0,'ホームページ','sys:dashboard:view',1,'/system/dashboard',NULL,1,1,'2026-04-02 17:33:23','2026-05-16 23:03:51',0),
(1007,1009,'ロール詳細情報','sys:role:detail',2,'/api/system/role/{id}','GET',6,1,'2026-05-05 14:18:08','2026-05-05 14:18:11',0),
(1008,0,'ユーザーページ','sys:user:view',1,'/system/user',NULL,1,1,'2026-05-06 00:27:34','2026-05-16 23:03:47',0),
(1009,0,'ロールページ','sys:role:view',1,'/system/role',NULL,1,1,'2026-05-06 00:43:21','2026-05-06 00:43:27',0),
(1010,1008,'ユーザー一覧','sys:user:list',2,'/api/system/user','GET',0,1,'2026-05-06 00:53:17','2026-05-06 00:53:19',0),
(1011,1008,'ユーザー作成','sys:user:create',2,'/api/system/user','POST',1,1,'2026-05-12 21:06:33','2026-05-12 21:06:35',0),
(1012,1008,'ユーザー更新','sys:user:update',2,'/api/system/user/{id}','PUT',2,1,'2026-05-12 21:07:25','2026-05-12 21:07:27',0),
(1013,1008,'ユーザー削除','sys:user:delete',2,'/api/system/user/{id}','DELETE',3,1,'2026-05-12 21:07:59','2026-05-24 14:21:12',0),
(1014,1008,'ユーザー詳細情報','sys:user:detail',2,'/api/system/user/{id}','GET',0,1,'2026-05-12 21:08:32','2026-05-12 21:08:34',0),
(1015,1008,'ユーザー状態更新','sys:user:changeStatus',2,'/api/system/user/{id}/status','PATCH',0,1,'2026-05-12 21:09:06','2026-05-12 21:09:08',0),
(1016,0,'権限ページ','sys:permission:view',1,'/system/permission',NULL,1,1,'2026-05-13 23:28:56','2026-05-16 22:59:45',0),
(1017,1016,'権限一覧','sys:permission:list',2,'/api/system/permission','GET',1,1,'2026-05-13 23:30:06','2026-05-13 23:30:08',0),
(1018,1016,'権限作成','sys:permission:create',2,'/api/system/permission','POST',2,1,'2026-05-14 16:23:25','2026-05-14 16:23:27',0),
(1019,1016,'権限更新','sys:permission:update',2,'/api/system/permission/{id}','PUT',3,1,'2026-05-16 22:44:57','2026-05-16 22:44:59',0),
(1020,1016,'権限詳細情報','sys:permission:detail',2,'/api/system/permission/{id}','GET',4,1,'2026-05-16 22:54:22','2026-05-16 22:54:24',0),
(1021,0,'メニューページ','sys:menu:view',1,'/system/menu',NULL,1,1,'2026-05-22 13:01:33','2026-05-22 13:01:33',0),
(1022,1021,'メニュー一覧','sys:menu:list',2,'/api/system/menu','GET',2,1,'2026-05-23 16:07:09','2026-05-23 16:07:10',0),
(1023,1021,'メニュー作成','sys:menu:create',2,'/api/system/menu','POST',3,1,'2026-05-23 20:23:25','2026-05-23 20:23:27',0),
(1024,1021,'メニュー更新','sys:menu:update',2,'/api/system/menu/{id}','PUT',4,1,'2026-05-23 23:14:12','2026-05-23 23:14:14',0),
(1025,1021,'メニュー詳細情報','sys:menu:detail',2,'/api/system/menu/{id}','GET',5,1,'2026-05-23 23:15:47','2026-05-23 23:15:49',0),
(1026,1009,'ロール権限取得','sys:role:assignPermission',2,'/api/system/role/{id}/permissions','GET',7,1,'2026-05-24 15:19:36','2026-05-24 15:19:36',0),
(1027,1009,'ロール権限割り当て','sys:role:assignPermission',2,'/api/system/role/{id}/permissions','PUT',8,1,'2026-05-24 15:19:36','2026-05-24 15:19:36',0),
(1031,1009,'ロールメニュー取得','sys:role:assignMenu',2,'/api/system/role/{id}/menus','GET',9,1,'2026-05-24 15:56:15','2026-05-24 15:56:15',0),
(1032,1009,'ロールメニュー割り当て','sys:role:assignMenu',2,'/api/system/role/{id}/menus','PUT',10,1,'2026-05-24 15:56:15','2026-05-24 15:56:15',0);

/*Table structure for table `t_sys_role` */

DROP TABLE IF EXISTS `t_sys_role`;

CREATE TABLE `t_sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `code` varchar(50) NOT NULL COMMENT 'ロールコード（例：ADMIN / USER）',
  `name` varchar(100) NOT NULL COMMENT 'ロール名',
  `sort` int NOT NULL DEFAULT '0' COMMENT '表示順',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態（0=無効、1=有効）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` bigint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、削除時=レコードのid）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`code`,((case when (`is_deleted` = 0) then 0 else NULL end)))
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ロールマスタ';

/*Data for the table `t_sys_role` */

insert  into `t_sys_role`(`id`,`code`,`name`,`sort`,`status`,`created_at`,`updated_at`,`is_deleted`) values 
(1,'ADMIN','管理者',1,1,'2026-02-28 23:39:49','2026-05-05 17:01:08',0),
(17,'AUDITOR','審査員',1,1,'2026-05-12 22:21:16','2026-05-12 22:21:16',0),
(19,'TEST','test',1,1,'2026-05-17 20:44:04','2026-05-17 20:44:58',1);

/*Table structure for table `t_sys_role_menu` */

DROP TABLE IF EXISTS `t_sys_role_menu`;

CREATE TABLE `t_sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `role_id` bigint NOT NULL COMMENT 'ロールID（t_sys_role.id）',
  `menu_id` bigint NOT NULL COMMENT 'メニューID（t_sys_menu.id）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu_active` (`role_id`,`menu_id`,((case when (`is_deleted` = 0) then 0 else NULL end))),
  KEY `idx_sys_role_menu_role_id` (`role_id`),
  KEY `idx_sys_role_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ロールメニュー関連テーブル';

/*Data for the table `t_sys_role_menu` */

insert  into `t_sys_role_menu`(`id`,`role_id`,`menu_id`,`created_at`,`updated_at`,`is_deleted`) values 
(1,1,1,'2026-04-02 17:28:26','2026-05-24 14:29:26',0),
(2,1,2,'2026-04-02 17:40:54','2026-05-24 14:29:26',0),
(3,1,3,'2026-04-14 09:38:52','2026-05-24 14:29:26',0),
(4,1,4,'2026-04-14 09:41:13','2026-05-24 14:29:26',0),
(5,1,5,'2026-05-05 23:38:09','2026-05-24 14:29:26',0),
(6,1,6,'2026-05-05 23:38:22','2026-05-24 14:29:26',0),
(7,1,7,'2026-05-13 23:31:31','2026-05-24 14:29:26',0),
(8,1,8,'2026-05-23 13:43:47','2026-05-24 14:29:26',0),
(9,1,9,'2026-05-23 13:43:53','2026-05-24 14:29:26',0),
(10,17,1,'2026-05-24 15:56:39','2026-05-24 16:01:21',0),
(11,17,2,'2026-05-24 15:56:39','2026-05-24 15:56:39',0),
(12,17,5,'2026-05-24 15:56:39','2026-05-24 15:56:39',0),
(13,17,6,'2026-05-24 15:56:39','2026-05-24 15:56:39',0),
(14,17,4,'2026-05-24 16:00:08','2026-05-24 16:00:08',0),
(15,17,8,'2026-05-24 16:00:08','2026-05-24 16:00:08',0),
(16,17,9,'2026-05-24 16:00:08','2026-05-24 16:00:08',0),
(17,17,3,'2026-05-24 16:01:21','2026-05-24 16:01:21',0),
(18,17,7,'2026-05-24 16:01:21','2026-05-24 16:01:21',0);

/*Table structure for table `t_sys_role_permission` */

DROP TABLE IF EXISTS `t_sys_role_permission`;

CREATE TABLE `t_sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `role_id` bigint NOT NULL COMMENT 'ロールID（t_sys_role.id）',
  `permission_id` bigint NOT NULL COMMENT '権限ID（t_sys_permission.id）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm_active` (`role_id`,`permission_id`,((case when (`is_deleted` = 0) then 0 else NULL end))),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ロール・権限紐付け';

/*Data for the table `t_sys_role_permission` */

insert  into `t_sys_role_permission`(`id`,`role_id`,`permission_id`,`created_at`,`updated_at`,`is_deleted`) values 
(1,1,1001,'2026-02-28 23:55:55','2026-02-28 23:55:55',0),
(2,1,1002,'2026-02-28 23:55:55','2026-02-28 23:55:55',0),
(3,1,1003,'2026-02-28 23:55:55','2026-02-28 23:55:55',0),
(4,1,1004,'2026-02-28 23:55:55','2026-02-28 23:55:55',0),
(5,1,1005,'2026-02-28 23:55:55','2026-02-28 23:55:55',0),
(6,1,1006,'2026-04-02 17:33:58','2026-04-02 17:34:01',0),
(7,1,1007,'2026-05-05 14:18:54','2026-05-05 14:18:56',0),
(8,1,1008,'2026-05-06 00:46:41','2026-05-06 00:46:43',0),
(9,1,1009,'2026-05-06 00:52:00','2026-05-06 00:52:02',0),
(10,1,1010,'2026-05-08 23:37:17','2026-05-08 23:37:19',0),
(11,1,1011,'2026-05-12 21:09:22','2026-05-12 21:09:25',0),
(12,1,1012,'2026-05-12 21:09:41','2026-05-12 21:09:43',0),
(13,1,1013,'2026-05-12 21:09:52','2026-05-12 21:09:54',0),
(14,1,1014,'2026-05-12 21:10:01','2026-05-12 21:10:03',0),
(15,1,1015,'2026-05-12 21:10:11','2026-05-12 21:10:13',0),
(16,1,1016,'2026-05-13 23:30:44','2026-05-13 23:30:46',0),
(17,1,1017,'2026-05-13 23:30:52','2026-05-13 23:30:54',0),
(18,1,1018,'2026-05-14 16:23:56','2026-05-14 16:23:58',0),
(19,1,1019,'2026-05-16 22:49:47','2026-05-16 22:49:48',0),
(20,1,1020,'2026-05-16 22:54:39','2026-05-16 22:54:41',0),
(21,1,1021,'2026-05-22 13:01:50','2026-05-22 13:01:50',0),
(22,1,1022,'2026-05-23 16:08:07','2026-05-23 16:08:09',0),
(23,1,1023,'2026-05-23 20:23:53','2026-05-23 20:23:55',0),
(24,1,1024,'2026-05-23 23:14:28','2026-05-23 23:14:29',0),
(25,1,1025,'2026-05-23 23:48:53','2026-05-23 23:48:54',0),
(26,1,1026,'2026-05-24 15:21:32','2026-05-24 15:21:34',0),
(27,1,1027,'2026-05-24 15:23:05','2026-05-24 15:23:07',0),
(29,17,1006,'2026-05-24 15:23:41','2026-05-24 15:23:41',0),
(30,17,1008,'2026-05-24 15:24:54','2026-05-24 16:00:30',1),
(31,17,1010,'2026-05-24 15:24:54','2026-05-24 15:24:54',0),
(32,17,1014,'2026-05-24 15:24:54','2026-05-24 16:00:30',1),
(33,17,1015,'2026-05-24 15:26:42','2026-05-24 16:00:30',1),
(34,17,1011,'2026-05-24 15:26:42','2026-05-24 16:00:30',1),
(35,17,1012,'2026-05-24 15:26:56','2026-05-24 16:00:30',1),
(36,1,1031,'2026-05-24 15:56:15','2026-05-24 15:56:15',0),
(37,1,1032,'2026-05-24 15:56:15','2026-05-24 15:56:15',0),
(39,17,1001,'2026-05-24 16:00:30','2026-05-24 16:00:30',0),
(40,17,1017,'2026-05-24 16:00:30','2026-05-24 16:00:30',0),
(41,17,1022,'2026-05-24 16:00:30','2026-05-24 16:00:30',0);

/*Table structure for table `t_sys_user` */

DROP TABLE IF EXISTS `t_sys_user`;

CREATE TABLE `t_sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `nick_name` varchar(50) DEFAULT NULL COMMENT 'ニックネーム',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'ユーザー状態（0=無効、1=有効）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ：0-未削除、1-削除済み',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='システムユーザーテーブル';

/*Data for the table `t_sys_user` */

insert  into `t_sys_user`(`id`,`nick_name`,`status`,`created_at`,`updated_at`,`is_deleted`) values 
(1,'管理者テストアカウント',1,'2026-02-15 10:55:32','2026-05-12 22:09:22',0),
(2,'審査員アカウント',1,'2026-05-12 22:23:53','2026-05-13 00:00:57',0);

/*Table structure for table `t_sys_user_account` */

DROP TABLE IF EXISTS `t_sys_user_account`;

CREATE TABLE `t_sys_user_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `user_id` bigint NOT NULL COMMENT 'ユーザーID（t_sys_user.id）',
  `account_type` varchar(20) NOT NULL COMMENT 'アカウント種別（EMAIL / PHONE）',
  `account_value` varchar(100) NOT NULL COMMENT 'ログイン識別子（メールアドレス／電話番号）',
  `password` varchar(255) NOT NULL COMMENT 'ログインパスワード（ハッシュ化）',
  `verified` tinyint NOT NULL DEFAULT '0' COMMENT '認証済みフラグ（0=未認証、1=認証済み）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'アカウント状態（0=無効、1=有効）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_active` (`account_type`,`account_value`,((case when (`is_deleted` = 0) then 0 else NULL end))),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ユーザーログインアカウント';

/*Data for the table `t_sys_user_account` */

insert  into `t_sys_user_account`(`id`,`user_id`,`account_type`,`account_value`,`password`,`verified`,`status`,`created_at`,`updated_at`,`is_deleted`) values 
(1,1,'EMAIL','admin@gmail.com','$2a$10$ms8IDvU5aKqH/6TZq/erHOH3W6dWzIDNPPZ2p9WFgaRW7n1FdLt3W',1,1,'2026-02-15 10:56:53','2026-05-12 22:09:22',0),
(2,2,'EMAIL','auditor@gmail.com','$2a$10$wi4ttBQJDISiXmHwpEGmMOtkQ6D6.9rxlk/caEG0jdIZZt4g2D7/.',1,1,'2026-05-12 22:23:53','2026-05-13 00:00:57',0);

/*Table structure for table `t_sys_user_role` */

DROP TABLE IF EXISTS `t_sys_user_role`;

CREATE TABLE `t_sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `user_id` bigint NOT NULL COMMENT 'ユーザーID（t_sys_user.id）',
  `role_id` bigint NOT NULL COMMENT 'ロールID（t_sys_role.id）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role_active` (`user_id`,`role_id`,((case when (`is_deleted` = 0) then 0 else NULL end))),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ユーザー・ロール紐付け';

/*Data for the table `t_sys_user_role` */

insert  into `t_sys_user_role`(`id`,`user_id`,`role_id`,`created_at`,`updated_at`,`is_deleted`) values 
(1,1,1,'2026-02-28 23:41:24','2026-02-28 23:41:24',0),
(2,2,17,'2026-05-12 22:23:53','2026-05-12 22:23:53',0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
