package com.manpowergroup.blog.module.system.domain.repository;

import java.util.Collection;
import java.util.List;

/** ユーザー・ロール関連の永続化ポート。 */
public interface UserRoleRepository {

    boolean existsByRoleId(Long roleId);

    List<Long> findActiveRoleIds(Long userId);

    void replaceRoles(Long userId, Collection<Long> roleIds);
}
