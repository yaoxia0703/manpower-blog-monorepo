package com.manpowergroup.blog.module.system.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manpowergroup.blog.shared.dto.PageQuery;
import com.manpowergroup.blog.shared.enums.HttpMethod;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.module.system.domain.model.permission.Permission;
import com.manpowergroup.blog.module.system.domain.model.permission.PermissionSearchCriteria;
import com.manpowergroup.blog.module.system.domain.model.permission.PermissionSearchPage;
import com.manpowergroup.blog.module.system.domain.repository.PermissionRepository;
import com.manpowergroup.blog.module.system.infrastructure.persistence.mapper.permission.PermissionMapper;
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
    public List<String> listPermissionCodesByUserId(Long userId) {
        return permissionMapper.selectPermissionCodesByUserId(userId);
    }

    @Override
    public List<String> listRoleCodesByUserId(Long userId) {
        return permissionMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    public List<Permission> listEnabledRules() {
        return permissionMapper.selectList(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getStatus, Status.ENABLED)
                .orderByAsc(Permission::getSort)
                .orderByAsc(Permission::getId));
    }

    @Override
    public List<Permission> list() {
        return permissionMapper.selectList(new LambdaQueryWrapper<Permission>()
                .orderByAsc(Permission::getSort)
                .orderByAsc(Permission::getId));
    }

    @Override
    public PermissionSearchPage page(
            PermissionSearchCriteria criteria, PageQuery page) {
        final PermissionSearchCriteria safeCriteria = criteria == null
                ? new PermissionSearchCriteria(null, null, null, null)
                : criteria;
        final String keyword = safeCriteria.keyword() == null
                ? null
                : safeCriteria.keyword().trim();
        final boolean hasKeyword = keyword != null && !keyword.isEmpty();
        final LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<Permission>()
                .and(hasKeyword, condition -> condition
                        .like(Permission::getName, keyword)
                        .or()
                        .like(Permission::getCode, keyword))
                .eq(safeCriteria.menuId() != null, Permission::getMenuId, safeCriteria.menuId())
                .eq(safeCriteria.method() != null, Permission::getMethod, safeCriteria.method())
                .eq(safeCriteria.status() != null, Permission::getStatus, safeCriteria.status())
                .orderByAsc(Permission::getSort)
                .orderByAsc(Permission::getId);
        final Page<Permission> mybatisPage = new Page<>(page.pageNum(), page.pageSize());
        final IPage<Permission> result = permissionMapper.selectPage(mybatisPage, wrapper);
        return new PermissionSearchPage(
                result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return Optional.ofNullable(permissionMapper.selectById(id));
    }

    @Override
    public List<Permission> listByIds(Collection<Long> ids) {
        return ids == null || ids.isEmpty() ? List.of() : permissionMapper.selectBatchIds(ids);
    }

    @Override
    public void create(Permission permission) {
        permissionMapper.insert(permission);
    }

    @Override
    public void update(Permission permission) {
        permissionMapper.updateById(permission);
    }

    @Override
    public void delete(Long id) {
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
