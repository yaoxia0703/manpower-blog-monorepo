package com.manpowergroup.springboot.springboot3web.system.application.command.role;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;

/** ロール状態変更コマンド。 */
public record RoleStatusChangeCommand(
        // ロールID
        Long id,
        // 変更後の状態
        Status status
) {
}
