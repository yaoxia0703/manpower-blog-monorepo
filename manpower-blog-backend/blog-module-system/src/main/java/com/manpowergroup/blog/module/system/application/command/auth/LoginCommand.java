package com.manpowergroup.blog.module.system.application.command.auth;

import com.manpowergroup.blog.shared.enums.AccountType;

/** ログインコマンド。 */
public record LoginCommand(
        // アカウント種別
        AccountType accountType,
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
