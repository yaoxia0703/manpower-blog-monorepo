package com.manpowergroup.springboot.springboot3web.system.application.command.user;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;

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
