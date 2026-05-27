ALTER TABLE t_sys_menu
    ADD COLUMN path varchar(200) DEFAULT NULL COMMENT 'フロントエンドルートパス' AFTER name,
    ADD COLUMN component varchar(200) DEFAULT NULL COMMENT 'フロントエンドコンポーネントキー' AFTER path;

UPDATE t_sys_menu SET path = '/system/dashboard', component = 'system/dashboard/index' WHERE id = 2;
UPDATE t_sys_menu SET path = '/system/role', component = 'system/role/index' WHERE id = 4;
UPDATE t_sys_menu SET path = '/system/user', component = 'system/user/index' WHERE id = 6;
UPDATE t_sys_menu SET path = '/system/permission', component = 'system/permission/index' WHERE id = 7;
UPDATE t_sys_menu SET path = '/system/menu', component = 'system/menu/index' WHERE id = 9;

ALTER TABLE t_sys_menu
    DROP INDEX uk_permission_id_active,
    DROP INDEX idx_sys_menu_permission_id,
    DROP COLUMN permission_id,
    ADD UNIQUE KEY uk_menu_path_active (path, ((case when (is_deleted = 0) then 0 else NULL end)));
