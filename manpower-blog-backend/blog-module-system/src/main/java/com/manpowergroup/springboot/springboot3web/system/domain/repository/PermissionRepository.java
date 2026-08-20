package com.manpowergroup.springboot.springboot3web.system.domain.repository;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;
import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.Permission;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository {

    /**
     * 指定ユーザーに紐づく権限コード一覧を取得する
     */
    List<String> selectPermissionCodesByUserId(Long userId);

    List<String> selectRoleCodesByUserId(Long userId);

    /** 有効なAPI権限ルールを表示順で取得する。 */
    List<Permission> findEnabledRules();

    /** 全権限を表示順で取得する。 */
    List<Permission> findAll();

    /** IDに一致する権限を取得する。 */
    Optional<Permission> findById(Long id);

    /** 指定IDの権限を取得する。 */
    List<Permission> findByIds(Collection<Long> ids);

    /** 権限を保存する。 */
    void save(Permission permission);

    /** 権限を更新する。 */
    void update(Permission permission);

    /** 権限を論理削除する。 */
    void deleteById(Long id);

    /** 自身を除き権限制御コードが存在するか判定する。 */
    boolean existsByCode(String code, Long excludeId);

    /** 自身を除きHTTPメソッドとパスの組み合わせが存在するか判定する。 */
    boolean existsByRule(HttpMethod method, String path, Long excludeId);

    /** 指定メニューに権限が紐づいているか判定する。 */
    boolean existsByMenuId(Long menuId);

}
