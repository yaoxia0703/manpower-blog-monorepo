package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.PermissionAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.command.permission.PermissionCreateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.permission.PermissionUpdateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.permission.PermissionResponse;
import com.manpowergroup.springboot.springboot3web.system.application.service.PermissionAppService;
import com.manpowergroup.springboot.springboot3web.system.domain.model.menu.Menu;
import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.Permission;
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
    public List<String> selectPermissionCodesByUserId(Long userId) {
        return permissionRepository.selectPermissionCodesByUserId(userId);
    }

    @Override
    public List<String> selectRoleCodesByUserId(Long userId) {
        return permissionRepository.selectRoleCodesByUserId(userId);
    }

    @Override
    public List<PermissionResponse> getPermissionList() {
        final List<Permission> permissions = permissionRepository.findAll();
        final List<Long> menuIds = permissions.stream()
                .map(Permission::getMenuId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        final Map<Long, Menu> menuMap = menuRepository.findByIds(menuIds).stream()
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

    @Override
    public PermissionResponse getPermissionDetail(Long id) {
        final Permission permission = getRequiredPermission(id);
        final String menuName = permission.getMenuId() == null
                ? null
                : menuRepository.findById(permission.getMenuId()).map(Menu::getName).orElse(null);
        return PermissionAssembler.toResponse(permission, menuName);
    }

    @Override
    @Transactional
    public Long createPermission(PermissionCreateCommand command) {
        ensureMenuExists(command.menuId());

        final Permission permission = Permission.create(
                command.menuId(), command.name(), command.code(), command.path(),
                command.method(), command.sort(), command.status()
        );
        ensureUnique(permission);
        permissionRepository.save(permission);
        log.info("権限を作成しました。id={}, code={}", permission.getId(), permission.getCode());
        return permission.getId();
    }

    @Override
    @Transactional
    public void updatePermission(PermissionUpdateCommand command) {
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
    public void deletePermission(Long id) {
        final Permission permission = getRequiredPermission(id);
        if (rolePermissionRepository.existsByPermissionId(id)) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "ロールに割り当てられている権限は削除できません");
        }
        permissionRepository.deleteById(id);
        log.info("権限を削除しました。id={}, code={}", id, permission.getCode());
    }

    @Override
    public boolean allExist(Collection<Long> ids) {
        return ids != null && permissionRepository.findByIds(ids).size() == ids.size();
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
}
