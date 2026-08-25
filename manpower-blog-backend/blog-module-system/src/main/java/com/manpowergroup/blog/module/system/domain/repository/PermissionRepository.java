package com.manpowergroup.blog.module.system.domain.repository;

import com.manpowergroup.blog.shared.enums.HttpMethod;
import com.manpowergroup.blog.module.system.domain.model.permission.Permission;
import com.manpowergroup.blog.module.system.domain.model.permission.PermissionSearchCriteria;
import com.manpowergroup.blog.module.system.domain.model.permission.PermissionSearchPage;
import com.manpowergroup.blog.shared.dto.PageQuery;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository {

    /**
     * 指定ユーザーに紐づく権限コード一覧を取得する
     */
    List<String> listPermissionCodesByUserId(Long userId);

    List<String> listRoleCodesByUserId(Long userId);

    /** 有効なAPI権限ルールを表示順で取得する。 */
    List<Permission> listEnabledRules();

    /** 全権限を表示順で取得する。 */
    List<Permission> list();

    /**
     * 検索条件に一致する権限をページ単位で取得する。
     *
     * <p>ページング値は {@link PageQuery} として受け取る。
     * 個別の数値で受けると隣接する同型引数の取り違えを検出できないため、
     * 検証済みの単一の値として扱う。</p>
     */
    PermissionSearchPage page(PermissionSearchCriteria criteria, PageQuery page);

    /** IDに一致する権限を取得する。 */
    Optional<Permission> findById(Long id);

    /** 指定IDの権限を取得する。 */
    List<Permission> listByIds(Collection<Long> ids);

    /** 権限を保存する。 */
    void create(Permission permission);

    /** 権限を更新する。 */
    void update(Permission permission);

    /** 権限を論理削除する。 */
    void delete(Long id);

    /** 自身を除き権限制御コードが存在するか判定する。 */
    boolean existsByCode(String code, Long excludeId);

    /** 自身を除きHTTPメソッドとパスの組み合わせが存在するか判定する。 */
    boolean existsByRule(HttpMethod method, String path, Long excludeId);

    /** 指定メニューに権限が紐づいているか判定する。 */
    boolean existsByMenuId(Long menuId);

}
