package com.manpowergroup.blog.module.system.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manpowergroup.blog.module.system.domain.model.role.RoleMenu;
import com.manpowergroup.blog.module.system.domain.repository.RoleMenuRepository;
import com.manpowergroup.blog.module.system.infrastructure.persistence.mapper.role.RoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RoleMenuRepositoryImpl implements RoleMenuRepository {

    private final RoleMenuMapper roleMenuMapper;

    @Override
    public boolean existsByMenuId(Long menuId) {
        return roleMenuMapper.exists(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getMenuId, menuId));
    }

    @Override
    public List<Long> findActiveMenuIds(Long roleId) {
        return roleMenuMapper.selectAllByRoleIdIncludeDeleted(roleId).stream()
                .filter(row -> Byte.valueOf((byte) 0).equals(row.getIsDeleted()))
                .map(RoleMenu::getMenuId)
                .toList();
    }

    @Override
    public void replaceMenus(Long roleId, Collection<Long> menuIds) {
        final Set<Long> targetIds = menuIds == null
                ? Set.of()
                : menuIds.stream().collect(Collectors.toCollection(LinkedHashSet::new));
        final List<RoleMenu> existing = roleMenuMapper.selectAllByRoleIdIncludeDeleted(roleId);
        final Map<Long, RoleMenu> existingByMenuId = existing.stream()
                .collect(Collectors.toMap(RoleMenu::getMenuId, Function.identity(), (left, right) -> left));

        final Set<Long> restoreIds = targetIds.stream()
                .filter(existingByMenuId::containsKey)
                .filter(id -> Byte.valueOf((byte) 1).equals(existingByMenuId.get(id).getIsDeleted()))
                .collect(Collectors.toSet());
        final Set<Long> deleteIds = existing.stream()
                .filter(row -> Byte.valueOf((byte) 0).equals(row.getIsDeleted()))
                .map(RoleMenu::getMenuId)
                .filter(id -> !targetIds.contains(id))
                .collect(Collectors.toSet());
        final LocalDateTime now = LocalDateTime.now();

        if (!restoreIds.isEmpty()) {
            roleMenuMapper.restoreMenus(roleId, restoreIds, now);
        }
        targetIds.stream()
                .filter(id -> !existingByMenuId.containsKey(id))
                .map(id -> RoleMenu.create(roleId, id))
                .forEach(roleMenuMapper::insert);
        if (!deleteIds.isEmpty()) {
            roleMenuMapper.logicalDeleteMenus(roleId, deleteIds, now);
        }
    }
}
