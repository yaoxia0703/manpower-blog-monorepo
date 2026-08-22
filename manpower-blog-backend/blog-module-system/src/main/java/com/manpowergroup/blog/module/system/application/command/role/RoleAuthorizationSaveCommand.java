package com.manpowergroup.blog.module.system.application.command.role;

import java.util.List;

/** ロール認可設定保存コマンド。 */
public record RoleAuthorizationSaveCommand(
        // ロールID
        Long roleId,
        // 付与するメニューID
        List<Long> menuIds,
        // 付与する権限ID
        List<Long> permissionIds
) {
    public RoleAuthorizationSaveCommand {
        menuIds = menuIds == null ? null : List.copyOf(menuIds);
        permissionIds = permissionIds == null ? null : List.copyOf(permissionIds);
    }
}
