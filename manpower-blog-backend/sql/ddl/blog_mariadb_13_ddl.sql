-- --------------------------------------------------------
-- MariaDB Server 13.0+ 用 DDL
-- 実行後、../data/blog_data.sql を同じ blog_db に対して実行してください。
-- MySQL 8 版からの主な変更:
--   1. utf8mb4_0900_ai_ci を MariaDB 対応の utf8mb4_unicode_ci に変更
--   2. MySQL の関数インデックスを不可視の仮想生成列 + UNIQUE KEY に変更
--   3. MySQL 固有の DEFAULT ENCRYPTION オプションを削除
-- --------------------------------------------------------

SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT;
SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS;
SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION;
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET @OLD_TIME_ZONE = @@TIME_ZONE;
SET TIME_ZONE = '+00:00';
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';
SET @OLD_SQL_NOTES = @@SQL_NOTES;
SET SQL_NOTES = 0;

CREATE DATABASE IF NOT EXISTS `blog_db`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
USE `blog_db`;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ブログ記事テーブル';

CREATE TABLE IF NOT EXISTS `t_content_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'カテゴリ主キーID',
  `name` varchar(100) NOT NULL COMMENT 'カテゴリ名',
  `slug` varchar(100) NOT NULL COMMENT 'カテゴリ識別子',
  `sort` int NOT NULL DEFAULT '100' COMMENT '表示順',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態（0=無効、1=有効）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  `_active_unique_key` tinyint GENERATED ALWAYS AS (
    CASE WHEN `is_deleted` = 0 THEN 0 ELSE NULL END
  ) VIRTUAL INVISIBLE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_category_slug_active` (`slug`, `_active_unique_key`),
  KEY `idx_content_category_sort` (`sort`),
  KEY `idx_content_category_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='記事カテゴリテーブル';

CREATE TABLE IF NOT EXISTS `t_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会員ID',
  `member_no` varchar(32) NOT NULL COMMENT '外部公開用会員番号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態（0=無効、1=有効）',
  `last_active_at` datetime DEFAULT NULL COMMENT '最終活動日時',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_member_no` (`member_no`),
  KEY `idx_member_deleted_status_id` (`is_deleted`, `status`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会員';

CREATE TABLE IF NOT EXISTS `t_member_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'アカウントID',
  `member_id` bigint NOT NULL COMMENT '会員ID',
  `account_type` varchar(30) NOT NULL COMMENT 'LOCAL_EMAIL / LOCAL_PHONE / GOOGLE / GITHUB',
  `account_value` varchar(191) NOT NULL COMMENT 'メール、電話番号またはOAuthユーザーID',
  `password` varchar(255) DEFAULT NULL COMMENT 'パスワードハッシュ（OAuthの場合はNULL）',
  `verified` tinyint NOT NULL DEFAULT '0' COMMENT '認証済みフラグ',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態',
  `last_login_at` datetime DEFAULT NULL COMMENT '最終ログイン日時',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ',
  `_active_unique_key` tinyint GENERATED ALWAYS AS (
    CASE WHEN `is_deleted` = 0 THEN 0 ELSE NULL END
  ) VIRTUAL INVISIBLE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_member_account_active` (`account_type`, `account_value`, `_active_unique_key`),
  KEY `idx_member_account_member` (`member_id`, `is_deleted`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会員ログインアカウント';

CREATE TABLE IF NOT EXISTS `t_member_profile` (
  `member_id` bigint NOT NULL COMMENT '会員ID',
  `display_name` varchar(50) NOT NULL COMMENT '表示名',
  `handle` varchar(50) DEFAULT NULL COMMENT '公開用ユーザー名',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT 'アバターURL',
  `bio` varchar(500) DEFAULT NULL COMMENT '自己紹介',
  `website_url` varchar(500) DEFAULT NULL COMMENT 'WebサイトURL',
  `locale` varchar(10) DEFAULT NULL COMMENT '言語設定',
  `timezone` varchar(50) DEFAULT NULL COMMENT 'タイムゾーン',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `uk_member_profile_handle` (`handle`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会員プロフィール';

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
  `_active_unique_key` tinyint GENERATED ALWAYS AS (
    CASE WHEN `is_deleted` = 0 THEN 0 ELSE NULL END
  ) VIRTUAL INVISIBLE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_path_active` (`path`, `_active_unique_key`),
  KEY `idx_sys_menu_parent_id` (`parent_id`),
  KEY `idx_sys_menu_type` (`type`),
  KEY `idx_sys_menu_status` (`status`),
  KEY `idx_sys_menu_is_delete` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='システムメニュー管理テーブル';

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
  `_active_unique_key` tinyint GENERATED ALWAYS AS (
    CASE WHEN `is_deleted` = 0 THEN 0 ELSE NULL END
  ) VIRTUAL INVISIBLE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code_active` (`code`, `_active_unique_key`),
  UNIQUE KEY `uk_permission_method_path_active` (`method`, `path`, `_active_unique_key`),
  KEY `idx_permission_menu_id` (`menu_id`),
  KEY `idx_path_method` (`path`, `method`)
) ENGINE=InnoDB AUTO_INCREMENT=1045 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API権限マスタ';

CREATE TABLE IF NOT EXISTS `t_sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `code` varchar(50) NOT NULL COMMENT 'ロールコード（例：ADMIN / USER）',
  `name` varchar(100) NOT NULL COMMENT 'ロール名',
  `sort` int NOT NULL DEFAULT '0' COMMENT '表示順',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態（0=無効、1=有効）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、削除時=レコードのid）',
  `_active_unique_key` tinyint GENERATED ALWAYS AS (
    CASE WHEN `is_deleted` = 0 THEN 0 ELSE NULL END
  ) VIRTUAL INVISIBLE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`code`, `_active_unique_key`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ロールマスタ';

CREATE TABLE IF NOT EXISTS `t_sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `role_id` bigint NOT NULL COMMENT 'ロールID（t_sys_role.id）',
  `menu_id` bigint NOT NULL COMMENT 'メニューID（t_sys_menu.id）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  `_active_unique_key` tinyint GENERATED ALWAYS AS (
    CASE WHEN `is_deleted` = 0 THEN 0 ELSE NULL END
  ) VIRTUAL INVISIBLE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu_active` (`role_id`, `menu_id`, `_active_unique_key`),
  KEY `idx_sys_role_menu_role_id` (`role_id`),
  KEY `idx_sys_role_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ロールメニュー関連テーブル';

CREATE TABLE IF NOT EXISTS `t_sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `role_id` bigint NOT NULL COMMENT 'ロールID（t_sys_role.id）',
  `permission_id` bigint NOT NULL COMMENT '権限ID（t_sys_permission.id）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  `_active_unique_key` tinyint GENERATED ALWAYS AS (
    CASE WHEN `is_deleted` = 0 THEN 0 ELSE NULL END
  ) VIRTUAL INVISIBLE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm_active` (`role_id`, `permission_id`, `_active_unique_key`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ロール・権限紐付け';

CREATE TABLE IF NOT EXISTS `t_sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `nick_name` varchar(50) DEFAULT NULL COMMENT 'ニックネーム',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT 'ユーザー状態（0=無効、1=有効）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ：0-未削除、1-削除済み',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='システムユーザーテーブル';

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
  `_active_unique_key` tinyint GENERATED ALWAYS AS (
    CASE WHEN `is_deleted` = 0 THEN 0 ELSE NULL END
  ) VIRTUAL INVISIBLE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_active` (`account_type`, `account_value`, `_active_unique_key`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ユーザーログインアカウント';

CREATE TABLE IF NOT EXISTS `t_sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `user_id` bigint NOT NULL COMMENT 'ユーザーID（t_sys_user.id）',
  `role_id` bigint NOT NULL COMMENT 'ロールID（t_sys_role.id）',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `updated_at` datetime NOT NULL COMMENT '更新日時',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  `_active_unique_key` tinyint GENERATED ALWAYS AS (
    CASE WHEN `is_deleted` = 0 THEN 0 ELSE NULL END
  ) VIRTUAL INVISIBLE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role_active` (`user_id`, `role_id`, `_active_unique_key`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ユーザー・ロール紐付け';

CREATE TABLE IF NOT EXISTS `t_test_bad_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ロール名',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態（0=無効、1=有効）',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  PRIMARY KEY (`id`),
  KEY `idx_bad_role_status` (`status`),
  KEY `idx_bad_role_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反例用ロールテーブル';

CREATE TABLE IF NOT EXISTS `t_test_bad_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主キーID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ユーザー名',
  `role_ids` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ロールID文字列（反例：1,2,3）',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状態（0=無効、1=有効）',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '論理削除フラグ（0=未削除、1=削除済み）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  PRIMARY KEY (`id`),
  KEY `idx_bad_user_status` (`status`),
  KEY `idx_bad_user_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反例：role_ids をカンマ区切り文字列で保持するユーザーテーブル';

SET TIME_ZONE = @OLD_TIME_ZONE;
SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT;
SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS;
SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION;
SET SQL_NOTES = @OLD_SQL_NOTES;
