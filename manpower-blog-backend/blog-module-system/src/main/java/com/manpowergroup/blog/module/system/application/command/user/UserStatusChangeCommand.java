package com.manpowergroup.blog.module.system.application.command.user;

import com.manpowergroup.blog.shared.enums.Status;

/** ユーザー状態変更コマンド。 */
public record UserStatusChangeCommand(
        // ユーザーID
        Long userId,
        // アカウントID
        Long accountId,
        // 変更後の状態
        Status status
) {
}
