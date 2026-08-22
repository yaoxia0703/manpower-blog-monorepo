package com.manpowergroup.blog.module.system.infrastructure.security;

import com.manpowergroup.blog.shared.util.CollectionUtils;
import com.manpowergroup.blog.framework.security.authority.ApiPermission;
import com.manpowergroup.blog.framework.security.authority.PermissionRuleProvider;
import com.manpowergroup.blog.module.system.domain.repository.PermissionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SystemPermissionRuleProvider implements PermissionRuleProvider {

    private final PermissionRepository permissionRepository;

    public SystemPermissionRuleProvider(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<ApiPermission> loadEnabledRules() {
        return CollectionUtils.safeList(permissionRepository.listEnabledRules()).stream()
                .map(permission -> new ApiPermission(
                        permission.getCode(), permission.getPath(), permission.getMethod().name()))
                .toList();
    }
}
