package com.manpowergroup.blog.module.system.domain.repository;

import java.util.Collection;
import java.util.List;

public interface RolePermissionRepository {

    /** 指定権限がロールに割り当てられているか判定する。 */
    boolean existsByPermissionId(Long permissionId);

    /**
     * 指定ロールに紐づく権限関連を取得する（論理削除されたデータも含む）
     *
     * @param roleId ロールID
     * @return ロール権限関連一覧
     */
    /** ロールに紐づく有効な権限IDを取得する。 */
    List<Long> findActivePermissionIds(Long roleId);

    /** ロールの権限関連を指定内容へ置き換える。 */
    void replacePermissions(Long roleId, Collection<Long> permissionIds);
}
