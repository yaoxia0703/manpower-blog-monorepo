package com.manpowergroup.springboot.springboot3web.system.domain.repository;

import com.manpowergroup.springboot.springboot3web.system.domain.model.role.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** ロール永続化ポート。 */
public interface RoleRepository {

    List<Role> list();

    Optional<Role> findById(Long id);

    List<Role> listByIds(Collection<Long> ids);

    void create(Role role);

    void update(Role role);

    void delete(Long id);

    boolean existsByCode(String code, Long excludeId);
}
