package com.manpowergroup.blog.module.member.application.command.account;

/**
 * 会員アカウントパスワード変更コマンド。
 */
public record MemberAccountChangePassworcCommand(
        // 会員アカウントID
        Long accountId,

        // 新しいパスワード
        String newPassword,

        // 現在のパスワード
        String currentPassword
) {


}
