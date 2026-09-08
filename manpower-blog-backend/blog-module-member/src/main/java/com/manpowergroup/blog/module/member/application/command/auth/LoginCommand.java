package com.manpowergroup.blog.module.member.application.command.auth;

import com.manpowergroup.blog.module.member.domain.model.member.MemberAccountType;

/// ログインコマンド
public record LoginCommand(
        // アカウント種別
        MemberAccountType accountType,
        // ログイン識別子
        String accountValue,
        // 平文パスワード（ログ出力禁止）
        String password
) {
    @Override
    public String toString() {
        return "LoginCommand[accountType=" + accountType + ", accountValue=" + accountValue + ", password=***]";
    }
}
