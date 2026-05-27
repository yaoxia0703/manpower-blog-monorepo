package com.manpowergroup.springboot.springboot3web.system.domain.repository;

import com.manpowergroup.springboot.springboot3web.system.domain.model.role.RoleMenu;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface RoleMenuRepository {

    List<RoleMenu> selectAllByRoleIdIncludeDeleted(Long roleId);

    int restoreMenus(Long roleId, Collection<Long> menuIds, LocalDateTime now);

    int logicalDeleteMenus(Long roleId, Collection<Long> menuIds, LocalDateTime now);
}