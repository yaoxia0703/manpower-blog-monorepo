package com.manpowergroup.springboot.springboot3web.system.application.command.user;

/** ユーザー削除コマンド。 */
public record UserDeleteCommand(
        // ユーザーID
        Long userId,
        // アカウントID
        Long accountId
) {
}
