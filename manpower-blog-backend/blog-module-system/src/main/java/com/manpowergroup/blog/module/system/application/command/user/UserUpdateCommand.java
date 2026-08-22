package com.manpowergroup.blog.module.system.application.command.user;

import com.manpowergroup.blog.shared.enums.Status;

/** ユーザー更新コマンド。 */
public record UserUpdateCommand(
        // ユーザーID
        Long userId,
        // アカウントID
        Long accountId,
        // ニックネーム
        String nickName,
        // ユーザー・アカウント状態
        Status status,
        // 付与するロールID
        Long roleId
) {
}
