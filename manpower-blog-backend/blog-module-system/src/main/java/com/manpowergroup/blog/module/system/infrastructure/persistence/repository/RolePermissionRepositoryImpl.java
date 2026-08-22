package com.manpowergroup.blog.module.system.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manpowergroup.blog.module.system.domain.model.role.RolePermission;
import com.manpowergroup.blog.module.system.domain.repository.RolePermissionRepository;
import com.manpowergroup.blog.module.system.infrastructure.persistence.mapper.role.RolePermissionMapper;
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
public class RolePermissionRepositoryImpl implements RolePermissionRepository {

    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public boolean existsByPermissionId(Long permissionId) {
        return rolePermissionMapper.exists(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getPermissionId, permissionId));
    }

    @Override
    public List<Long> findActivePermissionIds(Long roleId) {
        return rolePermissionMapper.selectAllByRoleIdIncludeDeleted(roleId).stream()
                .filter(row -> Byte.valueOf((byte) 0).equals(row.getIsDeleted()))
                .map(RolePermission::getPermissionId)
                .toList();
    }

    @Override
    public void replacePermissions(Long roleId, Collection<Long> permissionIds) {
        final Set<Long> targetIds = permissionIds == null
                ? Set.of()
                : permissionIds.stream().collect(Collectors.toCollection(LinkedHashSet::new));
        final List<RolePermission> existing = rolePermissionMapper.selectAllByRoleIdIncludeDeleted(roleId);
        final Map<Long, RolePermission> existingByPermissionId = existing.stream()
                .collect(Collectors.toMap(RolePermission::getPermissionId, Function.identity(), (left, right) -> left));

        final Set<Long> restoreIds = targetIds.stream()
                .filter(existingByPermissionId::containsKey)
                .filter(id -> Byte.valueOf((byte) 1).equals(existingByPermissionId.get(id).getIsDeleted()))
                .collect(Collectors.toSet());
        final Set<Long> deleteIds = existing.stream()
                .filter(row -> Byte.valueOf((byte) 0).equals(row.getIsDeleted()))
                .map(RolePermission::getPermissionId)
                .filter(id -> !targetIds.contains(id))
                .collect(Collectors.toSet());
        final LocalDateTime now = LocalDateTime.now();

        if (!restoreIds.isEmpty()) {
            rolePermissionMapper.restorePermissions(roleId, restoreIds, now);
        }
        targetIds.stream()
                .filter(id -> !existingByPermissionId.containsKey(id))
                .map(id -> RolePermission.create(roleId, id))
                .forEach(rolePermissionMapper::insert);
        if (!deleteIds.isEmpty()) {
            rolePermissionMapper.logicalDeletePermissions(roleId, deleteIds, now);
        }
    }
}
