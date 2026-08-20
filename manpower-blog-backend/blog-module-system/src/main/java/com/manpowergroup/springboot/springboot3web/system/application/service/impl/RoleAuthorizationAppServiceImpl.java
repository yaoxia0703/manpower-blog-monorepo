package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.MenuType;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.system.application.command.role.RoleAuthorizationSaveCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.role.RoleAuthorizationResponse;
import com.manpowergroup.springboot.springboot3web.system.application.service.MenuAppService;
import com.manpowergroup.springboot.springboot3web.system.application.service.PermissionAppService;
import com.manpowergroup.springboot.springboot3web.system.application.service.RoleAuthorizationAppService;
import com.manpowergroup.springboot.springboot3web.system.domain.model.menu.Menu;
import com.manpowergroup.springboot.springboot3web.system.domain.model.role.RoleAuthorization;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.MenuRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.PermissionRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RoleMenuRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RolePermissionRepository;
import com.manpowergroup.springboot.springboot3web.system.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleAuthorizationAppServiceImpl implements RoleAuthorizationAppService {

    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final MenuAppService menuAppService;
    private final PermissionAppService permissionAppService;

    @Override
    public RoleAuthorizationResponse getAuthorization(Long roleId) {
        ensureRoleExists(roleId);
        final List<Long> activeMenuIds = roleMenuRepository.findActiveMenuIds(roleId);
        final Set<Long> selectableMenuIds = menuRepository.findByIds(activeMenuIds).stream()
                .filter(menu -> menu.getType() == MenuType.MENU)
                .map(Menu::getId)
                .collect(Collectors.toSet());

        return new RoleAuthorizationResponse(
                menuAppService.getActiveMenuTree(),
                permissionAppService.getPermissionList(),
                activeMenuIds.stream().filter(selectableMenuIds::contains).toList(),
                rolePermissionRepository.findActivePermissionIds(roleId)
        );
    }

    @Override
    @Transactional
    public void saveAuthorization(RoleAuthorizationSaveCommand command) {
        ensureRoleExists(command.roleId());
        final RoleAuthorization authorization = RoleAuthorization.create(
                command.roleId(), command.menuIds(), command.permissionIds());

        if (menuRepository.findByIds(authorization.menuIds()).size() != authorization.menuIds().size()) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "存在しないメニューが含まれています");
        }
        if (permissionRepository.findByIds(authorization.permissionIds()).size()
                != authorization.permissionIds().size()) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "存在しない権限が含まれています");
        }

        roleMenuRepository.replaceMenus(authorization.roleId(), authorization.menuIds());
        rolePermissionRepository.replacePermissions(authorization.roleId(), authorization.permissionIds());
    }

    private void ensureRoleExists(Long roleId) {
        if (roleId == null || roleRepository.findById(roleId).isEmpty()) {
            throw BizException.withDetail(ErrorCode.NOT_FOUND, "ロールが存在しません。id=" + roleId);
        }
    }
}
