package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.PermissionAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.command.permission.PermissionCreateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.permission.PermissionUpdateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.permission.PermissionResponse;
import com.manpowergroup.springboot.springboot3web.system.application.query.permission.PermissionPageQuery;
import com.manpowergroup.springboot.springboot3web.system.application.service.PermissionAppService;
import com.manpowergroup.springboot.springboot3web.system.domain.model.menu.Menu;
import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.Permission;
import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.PermissionSearchCriteria;
import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.UserAuthorities;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.MenuRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.PermissionRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * API権限ユースケースの実装。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionAppServiceImpl implements PermissionAppService {

    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;
    private final RolePermissionRepository rolePermissionRepository;


    @Override
    public UserAuthorities loadUserAuthorities(Long userId) {
        return UserAuthorities.of(
                permissionRepository.listRoleCodesByUserId(userId),
                permissionRepository.listPermissionCodesByUserId(userId));
    }

    @Override
    public JoinPageResult<PermissionResponse> page(PermissionPageQuery query) {
        final var page = permissionRepository.page(
                new PermissionSearchCriteria(
                        query.keyword(), query.menuId(), query.method(), query.status()),
                query.pageNum(), query.pageSize());
        final List<PermissionResponse> records = toResponses(page.records());
        return JoinPageResult.of(records, page.total(), page.pageNum(), page.pageSize());
    }

    @Override
    public List<PermissionResponse> list() {
        return toResponses(permissionRepository.list());
    }

    @Override
    public PermissionResponse findById(Long id) {
        final Permission permission = getRequiredPermission(id);
        final String menuName = permission.getMenuId() == null
                ? null
                : menuRepository.findById(permission.getMenuId()).map(Menu::getName).orElse(null);
        return PermissionAssembler.toResponse(permission, menuName);
    }

    @Override
    @Transactional
    public Long create(PermissionCreateCommand command) {
        ensureMenuExists(command.menuId());

        final Permission permission = Permission.create(
                command.menuId(), command.name(), command.code(), command.path(),
                command.method(), command.sort(), command.status()
        );
        ensureUnique(permission);
        permissionRepository.create(permission);
        log.info("権限を作成しました。id={}, code={}", permission.getId(), permission.getCode());
        return permission.getId();
    }

    @Override
    @Transactional
    public void update(PermissionUpdateCommand command) {
        final Permission permission = getRequiredPermission(command.id());
        ensureMenuExists(command.menuId());
        permission.updateRule(
                command.menuId(), command.name(), command.path(), command.method(),
                command.sort(), command.status()
        );
        ensureUnique(permission);
        permissionRepository.update(permission);
        log.info("権限を更新しました。id={}", permission.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        final Permission permission = getRequiredPermission(id);
        if (rolePermissionRepository.existsByPermissionId(id)) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ロールに割り当てられている権限は削除できません");
        }
        permissionRepository.delete(id);
        log.info("権限を削除しました。id={}, code={}", id, permission.getCode());
    }

    @Override
    public boolean allExist(Collection<Long> ids) {
        return ids != null && permissionRepository.listByIds(ids).size() == ids.size();
    }

    private Permission getRequiredPermission(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> BizException.withDetail(ErrorCode.NOT_FOUND, "権限が存在しません。id=" + id));
    }

    private void ensureMenuExists(Long menuId) {
        if (menuId != null && menuRepository.findById(menuId).isEmpty()) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "所属メニューが存在しません。menuId=" + menuId);
        }
    }

    private void ensureUnique(Permission permission) {
        if (permissionRepository.existsByCode(permission.getCode(), permission.getId())) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "権限制御コードは既に存在しています");
        }
        if (permissionRepository.existsByRule(
                permission.getMethod(), permission.getPath(), permission.getId())) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "同じHTTPメソッドとAPIパスの権限は既に存在しています");
        }
    }

    private List<PermissionResponse> toResponses(List<Permission> permissions) {
        final List<Long> menuIds = permissions.stream()
                .map(Permission::getMenuId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        final Map<Long, Menu> menuMap = menuRepository.listByIds(menuIds).stream()
                .collect(Collectors.toMap(Menu::getId, Function.identity()));
        return permissions.stream()
                .map(permission -> PermissionAssembler.toResponse(
                        permission,
                        menuMap.containsKey(permission.getMenuId())
                                ? menuMap.get(permission.getMenuId()).getName()
                                : null
                ))
                .toList();
    }
}
