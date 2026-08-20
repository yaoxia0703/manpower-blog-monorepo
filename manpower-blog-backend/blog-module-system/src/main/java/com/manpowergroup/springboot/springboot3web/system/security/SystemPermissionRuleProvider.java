package com.manpowergroup.springboot.springboot3web.system.security;

import com.manpowergroup.springboot.springboot3web.blog.common.util.CollectionUtils;
import com.manpowergroup.springboot.springboot3web.framework.security.authority.ApiPermission;
import com.manpowergroup.springboot.springboot3web.framework.security.authority.PermissionRuleProvider;
import com.manpowergroup.springboot.springboot3web.system.application.service.PermissionAppService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SystemPermissionRuleProvider implements PermissionRuleProvider {

    private final PermissionAppService permissionAppService;

    public SystemPermissionRuleProvider(PermissionAppService permissionAppService) {
        this.permissionAppService = permissionAppService;
    }

    @Override
    public List<ApiPermission> loadEnabledRules() {
        return CollectionUtils.safeList(permissionAppService.selectEnabledApiPermissions());
    }
}
