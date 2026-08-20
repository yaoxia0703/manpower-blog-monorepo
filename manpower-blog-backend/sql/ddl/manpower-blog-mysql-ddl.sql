-- manpower-blog MySQL 8 schema
-- Execute this file first, then ../data/manpower-blog-data.sql.
-- --------------------------------------------------------
-- ホスト:                          127.0.0.1
-- サーバーのバージョン:                   8.0.44 - MySQL Community Server - GPL
-- サーバー OS:                      Win64
-- HeidiSQL バージョン:               12.20.0.7320
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- blog_db のデータベース構造をダンプしています
CREATE DATABASE IF NOT EXISTS `blog_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `blog_db`;

--  テーブル blog_db.t_content_article の構造をダンプしています
CREATE TABLE IF NOT EXISTS `t_content_article` (
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

-- エクスポートするデータが選択されていません

--  テーブル blog_db.t_sys_menu の構造をダンプしています
CREATE TABLE IF NOT EXISTS `t_sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '親メニューID（0は最上位）',
  `name` varchar(100) NOT NULL COMMENT 'メニュー名称',
  `path` varchar(200) DEFAULT NULL COMMENT 'フロントエンドルートパス',
  `component` varchar(200) DEFAULT NULL COMMENT 'フロントエンドコンポーネントキー',
  `type` tinyint NOT NULL COMMENT 'メニュー種別（1=ディレクトリ 2=メニュー 3=ボタン）',
  `sort` int NOT NULL DEFAULT '0' COMMENT '表示順',
  `icon` varchar(100) DEFAULT NULL COMMENT 'アイコン',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態（0=無効 1=有効）',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除 1=削除済）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_path_active` (`path`,((case when (`is_deleted` = 0) then 0 else NULL end))),
  KEY `idx_sys_menu_parent_id` (`parent_id`),
  KEY `idx_sys_menu_type` (`type`),
  KEY `idx_sys_menu_status` (`status`),
  KEY `idx_sys_menu_is_delete` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='システムメニュー管理テーブル';

-- エクスポートするデータが選択されていません

--  テーブル blog_db.t_sys_permission の構造をダンプしています
CREATE TABLE IF NOT EXISTS `t_sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `menu_id` bigint DEFAULT NULL COMMENT '所属メニューID（画面上の分類用、API認可には不使用）',
  `name` varchar(100) NOT NULL COMMENT '権限名',
  `code` varchar(100) NOT NULL COMMENT '権限制御コード（例：user:add / article:edit）',
  `path` varchar(200) NOT NULL COMMENT '対象APIパス',
  `method` varchar(10) NOT NULL COMMENT 'HTTPメソッド（GET/POST/PUT/DELETE/PATCH）',
  `sort` int NOT NULL DEFAULT '0' COMMENT '表示順',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態（0=無効、1=有効）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code_active` (`code`,((case when (`is_deleted` = 0) then 0 else NULL end))),
  UNIQUE KEY `uk_permission_method_path_active` (`method`,`path`,((case when (`is_deleted` = 0) then 0 else NULL end))),
  KEY `idx_permission_menu_id` (`menu_id`),
  KEY `idx_path_method` (`path`,`method`)
) ENGINE=InnoDB AUTO_INCREMENT=1042 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='API権限マスタ';

-- エクスポートするデータが選択されていません

--  テーブル blog_db.t_sys_role の構造をダンプしています
CREATE TABLE IF NOT EXISTS `t_sys_role` (
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

-- エクスポートするデータが選択されていません

--  テーブル blog_db.t_sys_role_menu の構造をダンプしています
CREATE TABLE IF NOT EXISTS `t_sys_role_menu` (
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

-- エクスポートするデータが選択されていません

--  テーブル blog_db.t_sys_role_permission の構造をダンプしています
CREATE TABLE IF NOT EXISTS `t_sys_role_permission` (
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
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ロール・権限紐付け';

-- エクスポートするデータが選択されていません

--  テーブル blog_db.t_sys_user の構造をダンプしています
CREATE TABLE IF NOT EXISTS `t_sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `nick_name` varchar(50) DEFAULT NULL COMMENT 'ニックネーム',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'ユーザー状態（0=無効、1=有効）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ：0-未削除、1-削除済み',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='システムユーザーテーブル';

-- エクスポートするデータが選択されていません

--  テーブル blog_db.t_sys_user_account の構造をダンプしています
CREATE TABLE IF NOT EXISTS `t_sys_user_account` (
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
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ユーザーログインアカウント';

-- エクスポートするデータが選択されていません

--  テーブル blog_db.t_sys_user_role の構造をダンプしています
CREATE TABLE IF NOT EXISTS `t_sys_user_role` (
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
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ユーザー・ロール紐付け';

-- エクスポートするデータが選択されていません

--  テーブル blog_db.t_test_bad_role の構造をダンプしています
CREATE TABLE IF NOT EXISTS `t_test_bad_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ロール名',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態（0=無効、1=有効）',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  PRIMARY KEY (`id`),
  KEY `idx_bad_role_status` (`status`),
  KEY `idx_bad_role_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反例用ロールテーブル';

-- エクスポートするデータが選択されていません

--  テーブル blog_db.t_test_bad_user の構造をダンプしています
CREATE TABLE IF NOT EXISTS `t_test_bad_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ユーザー名',
  `role_ids` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ロールID文字列（反例：1,2,3）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態（0=無効、1=有効）',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  PRIMARY KEY (`id`),
  KEY `idx_bad_user_status` (`status`),
  KEY `idx_bad_user_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反例：role_ids をカンマ区切り文字列で保持するユーザーテーブル';

-- エクスポートするデータが選択されていません

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
