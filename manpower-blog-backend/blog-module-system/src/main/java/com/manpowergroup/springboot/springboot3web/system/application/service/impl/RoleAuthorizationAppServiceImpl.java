package com.manpowergroup.springboot.springboot3web.system.application.service.impl;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.ErrorCode;
import com.manpowergroup.springboot.springboot3web.blog.common.exception.BizException;
import com.manpowergroup.springboot.springboot3web.system.application.service.MenuAppService;
import com.manpowergroup.springboot.springboot3web.system.application.service.PermissionAppService;
import com.manpowergroup.springboot.springboot3web.system.application.service.RoleAppMenuService;
import com.manpowergroup.springboot.springboot3web.system.application.service.RoleAppService;
import com.manpowergroup.springboot.springboot3web.system.application.service.RoleAuthorizationAppService;
import com.manpowergroup.springboot.springboot3web.system.application.service.RolePermissionAppService;
import com.manpowergroup.springboot.springboot3web.system.application.vo.role.RoleAuthorizationVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleAuthorizationAppServiceImpl implements RoleAuthorizationAppService {

    private final RoleAppService roleAppService;
    private final MenuAppService menuAppService;
    private final PermissionAppService permissionAppService;
    private final RoleAppMenuService roleMenuAppService;
    private final RolePermissionAppService rolePermissionAppService;

    @Override
    public RoleAuthorizationVo getAuthorization(Long roleId) {
        roleAppService.getRoleById(roleId);
        return new RoleAuthorizationVo(
                menuAppService.getActiveMenuTree(),
                permissionAppService.getPermissionList(),
                roleMenuAppService.getMenuIdsByRoleId(roleId),
                rolePermissionAppService.getPermissionIdsByRoleId(roleId)
        );
    }

    @Override
    @Transactional
    public void saveAuthorization(Long roleId, Long[] menuIds, Long[] permissionIds) {
        roleAppService.getRoleById(roleId);

        final Set<Long> normalizedMenuIds = normalize(menuIds, "メニューID一覧が指定されていません");
        final Set<Long> normalizedPermissionIds = normalize(permissionIds, "権限ID一覧が指定されていません");

        if (!normalizedMenuIds.isEmpty()
                && menuAppService.listByIds(normalizedMenuIds).size() != normalizedMenuIds.size()) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "存在しないメニューが含まれています");
        }
        if (!normalizedPermissionIds.isEmpty()
                && permissionAppService.listByIds(normalizedPermissionIds).size() != normalizedPermissionIds.size()) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, "存在しない権限が含まれています");
        }

        roleMenuAppService.saveOrUpdate(roleId, normalizedMenuIds.toArray(Long[]::new));
        rolePermissionAppService.saveOrUpdate(roleId, normalizedPermissionIds.toArray(Long[]::new));
    }

    private Set<Long> normalize(Long[] ids, String message) {
        if (ids == null) {
            throw BizException.withDetail(ErrorCode.BAD_REQUEST, message);
        }
        final Set<Long> result = new LinkedHashSet<>();
        Arrays.stream(ids).filter(Objects::nonNull).forEach(result::add);
        return result;
    }
}
