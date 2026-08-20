package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.framework.security.authority.ApiPermission;
import com.manpowergroup.springboot.springboot3web.system.application.assembler.PermissionAssembler;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.permission.PermissionCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.permission.PermissionUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.service.PermissionAppService;
import com.manpowergroup.springboot.springboot3web.system.application.vo.permission.PermissionVo;
import com.manpowergroup.springboot.springboot3web.system.domain.model.menu.Menu;
import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.Permission;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.PermissionRepository;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.menu.MenuMapper;
import com.manpowergroup.springboot.springboot3web.system.infrastructure.persistence.mapper.permission.PermissionMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * API権限マスタサービス実装。
 *
 * @author YAOXIA
 * @since 2025-12-18
 */
@Service
@AllArgsConstructor
@Slf4j
public class PermissionAppServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionAppService {

    private final PermissionRepository permissionRepository;
    private final MenuMapper menuMapper;

    @Override
    public List<String> selectPermissionCodesByUserId(Long userId) {
        return permissionRepository.selectPermissionCodesByUserId(userId);
    }

    @Override
    public List<String> selectRoleCodesByUserId(Long userId) {
        return permissionRepository.selectRoleCodesByUserId(userId);
    }

    @Override
    public List<ApiPermission> selectEnabledApiPermissions() {
        return permissionRepository.selectEnabledApiPermissions();
    }

    @Override
    public List<PermissionVo> getPermissionList() {
        final var permissions = baseMapper.selectList(
                        new LambdaQueryWrapper<Permission>()
                                .orderByAsc(Permission::getSort)
                                .orderByAsc(Permission::getId)
                );
        final var menuIds = permissions.stream()
                .map(Permission::getMenuId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        final Map<Long, Menu> menus = menuIds.isEmpty()
                ? Map.of()
                : menuMapper.selectBatchIds(menuIds).stream()
                        .collect(Collectors.toMap(Menu::getId, Function.identity()));

        return permissions.stream().map(permission -> {
            final var vo = PermissionAssembler.toVo(permission);
            final var menu = menus.get(permission.getMenuId());
            vo.setMenuName(menu == null ? null : menu.getName());
            return vo;
        }).toList();
    }

    @Override
    public PermissionVo getPermissionDetail(Long id) {
        final var permission = baseMapper.selectById(id);
        if (permission == null) {
            throw BizException.withDetail(ErrorCode.NOT_FOUND, "権限が存在しません。id=" + id);
        }
        final var vo = PermissionAssembler.toVo(permission);
        if (permission.getMenuId() != null) {
            final var menu = menuMapper.selectById(permission.getMenuId());
            vo.setMenuName(menu == null ? null : menu.getName());
        }
        return vo;
    }

    @Override
    @Transactional
    public Long createPermission(PermissionCreateRequest request) {
        log.info("[PermissionAppService#createPermission] start: request={}", request);

        final var entity = PermissionAssembler.toCreateEntity(request);
        ensureMenuExists(entity.getMenuId());
        checkDuplicateCode(entity);
        checkDuplicateRule(entity);
        baseMapper.insert(entity);

        log.info("[PermissionAppService#createPermission] success: id={}", entity.getId());
        return entity.getId();
    }

    @Override
    @Transactional
    public void updatePermission(Long id, PermissionUpdateRequest request) {
        log.info("[PermissionAppService#updatePermission] start: id={}, request={}", id, request);

        final var existing = ensurePermissionExists(id);

        PermissionAssembler.toUpdateEntity(request, existing);

        ensureMenuExists(existing.getMenuId());
        checkDuplicateCode(existing);
        checkDuplicateRule(existing);
        baseMapper.updateById(existing);

        log.info("[PermissionAppService#updatePermission] success: id={}", id);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        final var existing = ensurePermissionExists(id);
        baseMapper.deleteById(id);
        log.info("[PermissionAppService#deletePermission] success: id={}, code={}, name={}",
                existing.getId(), existing.getCode(), existing.getName());
    }

    /**
     * 権限制御コードの重複チェック
     */
    private void checkDuplicateCode(Permission entity) {
        final boolean exists = lambdaQuery()
                .eq(Permission::getCode, entity.getCode())
                .ne(entity.getId() != null, Permission::getId, entity.getId())
                .exists();

        if (exists) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "権限制御コードは既に存在しています");
        }
    }

    /**
     * 同一 HTTP method + path の有効な権限ルール重複を禁止する。
     */
    private void checkDuplicateRule(Permission entity) {
        final boolean exists = lambdaQuery()
                .eq(Permission::getMethod, entity.getMethod())
                .eq(Permission::getPath, entity.getPath())
                .ne(entity.getId() != null, Permission::getId, entity.getId())
                .exists();

        if (exists) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "同じHTTPメソッドとAPIパスの権限は既に存在しています");
        }
    }

    /**
     * 権限存在チェック（存在しない場合は例外をスロー）
     */
    private Permission ensurePermissionExists(Long id) {
        final var permission = baseMapper.selectById(id);
        if (permission == null) {
            log.warn("[PermissionAppService#ensurePermissionExists] not found: id={}", id);
            throw BizException.withDetail(ErrorCode.NOT_FOUND, "権限が存在しません。id=" + id);
        }
        return permission;
    }

    /**
     * menuId は分類用のため未所属（null）を許可する。
     */
    private void ensureMenuExists(Long menuId) {
        if (menuId != null && menuMapper.selectById(menuId) == null) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "所属メニューが存在しません。menuId=" + menuId);
        }
    }
}
