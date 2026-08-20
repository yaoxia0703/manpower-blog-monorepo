package com.manpowergroup.springboot.springboot3web.system.domain.repository;

import com.manpowergroup.springboot.springboot3web.system.domain.model.role.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** ロール永続化ポート。 */
public interface RoleRepository {

    List<Role> findAll();

    Optional<Role> findById(Long id);

    List<Role> findByIds(Collection<Long> ids);

    void save(Role role);

    void update(Role role);

    void deleteById(Long id);

    boolean existsByCode(String code, Long excludeId);
}
