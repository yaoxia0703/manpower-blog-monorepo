package com.manpowergroup.springboot.springboot3web.system.application.command.user;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;

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
