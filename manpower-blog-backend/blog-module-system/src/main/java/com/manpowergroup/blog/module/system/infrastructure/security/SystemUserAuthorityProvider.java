package com.manpowergroup.blog.module.system.infrastructure.security;

import com.manpowergroup.blog.framework.security.authority.UserAuthorityProvider;
import com.manpowergroup.blog.module.system.application.service.PermissionAppService;
import com.manpowergroup.blog.module.system.domain.model.permission.UserAuthorities;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * framework 側の {@link UserAuthorityProvider} の system 実装。
 *
 * <p>ロール・権限の実効値算出は {@link UserAuthorities} に一元化しており、
 * 本クラスは framework が要求する形式へ受け渡すだけのアダプタに徹する。
 * これにより /me API の画面制御用権限と、API 認可用 Authority が
 * 必ず同一のルールから導出される。</p>
 */
@Component
public class SystemUserAuthorityProvider implements UserAuthorityProvider {

    private final PermissionAppService permissionAppService;

    public SystemUserAuthorityProvider(PermissionAppService permissionAppService) {
        this.permissionAppService = permissionAppService;
    }

    @Override
    public List<String> loadPermissionCodes(Long userId) {
        return List.copyOf(
                permissionAppService.loadUserAuthorities(userId).effectivePermissionCodes());
    }

    @Override
    public List<String> loadAuthorityCodes(Long userId) {
        return permissionAppService.loadUserAuthorities(userId).toGrantedAuthorities();
    }
}
