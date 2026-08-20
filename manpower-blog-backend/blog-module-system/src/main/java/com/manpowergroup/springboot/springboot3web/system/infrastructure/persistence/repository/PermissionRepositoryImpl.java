package com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.Permission;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.PermissionRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.permission.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {

    private final PermissionMapper permissionMapper;

    @Override
    public List<String> selectPermissionCodesByUserId(Long userId) {
        return permissionMapper.selectPermissionCodesByUserId(userId);
    }

    @Override
    public List<String> selectRoleCodesByUserId(Long userId) {
        return permissionMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    public List<Permission> findEnabledRules() {
        return permissionMapper.selectList(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getStatus, Status.ENABLED)
                .orderByAsc(Permission::getSort)
                .orderByAsc(Permission::getId));
    }

    @Override
    public List<Permission> findAll() {
        return permissionMapper.selectList(new LambdaQueryWrapper<Permission>()
                .orderByAsc(Permission::getSort)
                .orderByAsc(Permission::getId));
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return Optional.ofNullable(permissionMapper.selectById(id));
    }

    @Override
    public List<Permission> findByIds(Collection<Long> ids) {
        return ids == null || ids.isEmpty() ? List.of() : permissionMapper.selectBatchIds(ids);
    }

    @Override
    public void save(Permission permission) {
        permissionMapper.insert(permission);
    }

    @Override
    public void update(Permission permission) {
        permissionMapper.updateById(permission);
    }

    @Override
    public void deleteById(Long id) {
        permissionMapper.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code, Long excludeId) {
        return permissionMapper.exists(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getCode, code)
                .ne(excludeId != null, Permission::getId, excludeId));
    }

    @Override
    public boolean existsByRule(HttpMethod method, String path, Long excludeId) {
        return permissionMapper.exists(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getMethod, method)
                .eq(Permission::getPath, path)
                .ne(excludeId != null, Permission::getId, excludeId));
    }

    @Override
    public boolean existsByMenuId(Long menuId) {
        return permissionMapper.exists(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getMenuId, menuId));
    }

}
