package com.manpowergroup.blog.module.system.application.command.user;

import com.manpowergroup.blog.shared.enums.AccountType;
import com.manpowergroup.blog.shared.enums.Status;

/** ユーザー作成コマンド。 */
public record UserCreateCommand(
        // ニックネーム
        String nickName,
        // ロールID
        Long roleId,
        // アカウント種別
        AccountType accountType,
        // ログイン識別子
        String accountValue,
        // 平文パスワード（ログ出力禁止）
        String password,
        // ユーザー状態
        Status status
) {
    @Override
    public String toString() {
        return "UserCreateCommand[nickName=" + nickName + ", roleId=" + roleId
                + ", accountType=" + accountType + ", accountValue=" + accountValue
                + ", password=***, status=" + status + "]";
    }
}
