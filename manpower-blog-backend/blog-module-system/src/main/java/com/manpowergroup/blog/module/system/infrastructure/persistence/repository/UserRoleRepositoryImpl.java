package com.manpowergroup.blog.module.system.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manpowergroup.blog.module.system.domain.model.user.UserRole;
import com.manpowergroup.blog.module.system.domain.repository.UserRoleRepository;
import com.manpowergroup.blog.module.system.infrastructure.persistence.mapper.user.UserRoleMapper;
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
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private final UserRoleMapper userRoleMapper;

    @Override
    public boolean existsByRoleId(Long roleId) {
        return userRoleMapper.exists(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getRoleId, roleId));
    }

    @Override
    public List<Long> findActiveRoleIds(Long userId) {
        return userRoleMapper.selectAllByUserIdIncludeDeleted(userId).stream()
                .filter(row -> Byte.valueOf((byte) 0).equals(row.getIsDeleted()))
                .map(UserRole::getRoleId)
                .toList();
    }

    @Override
    public void replaceRoles(Long userId, Collection<Long> roleIds) {
        final Set<Long> targetIds = roleIds == null
                ? Set.of()
                : roleIds.stream().collect(Collectors.toCollection(LinkedHashSet::new));
        final List<UserRole> existing = userRoleMapper.selectAllByUserIdIncludeDeleted(userId);
        final Map<Long, UserRole> existingByRoleId = existing.stream()
                .collect(Collectors.toMap(UserRole::getRoleId, Function.identity(), (left, right) -> left));

        final Set<Long> restoreIds = targetIds.stream()
                .filter(existingByRoleId::containsKey)
                .filter(id -> Byte.valueOf((byte) 1).equals(existingByRoleId.get(id).getIsDeleted()))
                .collect(Collectors.toSet());
        final Set<Long> deleteIds = existing.stream()
                .filter(row -> Byte.valueOf((byte) 0).equals(row.getIsDeleted()))
                .map(UserRole::getRoleId)
                .filter(id -> !targetIds.contains(id))
                .collect(Collectors.toSet());
        final LocalDateTime now = LocalDateTime.now();

        if (!restoreIds.isEmpty()) {
            userRoleMapper.restoreRoles(userId, restoreIds, now);
        }
        targetIds.stream()
                .filter(id -> !existingByRoleId.containsKey(id))
                .map(id -> UserRole.create(userId, id))
                .forEach(userRoleMapper::insert);
        if (!deleteIds.isEmpty()) {
            userRoleMapper.logicalDeleteRoles(userId, deleteIds, now);
        }
    }
}
