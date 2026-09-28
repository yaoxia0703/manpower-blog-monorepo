-- =====================================================================
-- 権限コードを命名規約 <ドメイン>:<リソース>:<動詞>[修飾語] へ移行する
--
-- 対象: 既存の blog_db（sql/data/blog_data.sql は移行後の値で更新済み）
-- 新規環境では本スクリプトは不要。
--
-- - 接頭辞 sys: を system: へ（第1段はドメイン名。略記を廃止）
-- - 規約外の動詞を「基本動詞 + 修飾語」へ改名
--
-- t_sys_role_permission は permission_id で紐付くため、割当は変わらない。
-- 権限コードは JWT に含まれず、リクエストごとに DB から読み込まれるため、
-- 発行済みトークンの再発行は不要。画面側は再ログインで /me を取り直すこと。
--
-- 再実行しても結果は変わらない（WHERE 句が移行前の値のみに一致する）。
-- =====================================================================

START TRANSACTION;

-- 1. 規約外の動詞を個別に改名する（接頭辞置換より先に行う）
UPDATE t_sys_permission SET code = 'system:menu:listEnabled'         WHERE code = 'sys:menu:activeTree';
UPDATE t_sys_permission SET code = 'system:menu:listOptions'         WHERE code = 'sys:menu:parentOptions';
UPDATE t_sys_permission SET code = 'system:role:detailAuthorization' WHERE code = 'sys:role:authorization:list';
UPDATE t_sys_permission SET code = 'system:role:updateAuthorization' WHERE code = 'sys:role:assignAuthorization';

-- 2. 残りは接頭辞のみ置換する
UPDATE t_sys_permission
   SET code = CONCAT('system:', SUBSTRING(code, CHAR_LENGTH('sys:') + 1))
 WHERE code LIKE 'sys:%';

-- 3. 確認：0 件であること
SELECT id, code FROM t_sys_permission WHERE code LIKE 'sys:%';

COMMIT;
