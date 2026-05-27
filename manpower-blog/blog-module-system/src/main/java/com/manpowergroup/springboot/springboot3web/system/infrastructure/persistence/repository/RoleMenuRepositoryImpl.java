package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.repository;

import com.manpowergroup.springboot.springboot3web.system.domain.model.role.RoleMenu;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RoleMenuRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.role.RoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoleMenuRepositoryImpl implements RoleMenuRepository {

    private final RoleMenuMapper roleMenuMapper;

    @Override
    public List<RoleMenu> selectAllByRoleIdIncludeDeleted(Long roleId) {
        return roleMenuMapper.selectAllByRoleIdIncludeDeleted(roleId);
    }

    @Override
    public int restoreMenus(Long roleId, Collection<Long> menuIds, LocalDateTime now) {
        return roleMenuMapper.restoreMenus(roleId, menuIds, now);
    }

    @Override
    public int logicalDeleteMenus(Long roleId, Collection<Long> menuIds, LocalDateTime now) {
        return roleMenuMapper.logicalDeleteMenus(roleId, menuIds, now);
    }
}