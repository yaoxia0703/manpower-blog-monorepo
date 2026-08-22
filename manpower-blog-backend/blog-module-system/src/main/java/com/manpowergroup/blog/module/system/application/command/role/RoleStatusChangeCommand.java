package com.manpowergroup.blog.module.system.application.command.role;

import com.manpowergroup.blog.shared.enums.Status;

/** ロール状態変更コマンド。 */
public record RoleStatusChangeCommand(
        // ロールID
        Long id,
        // 変更後の状態
        Status status
) {
}
