package com.manpowergroup.blog.module.member.application.command.account;

import com.manpowergroup.blog.module.member.domain.model.member.MemberAccountType;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.enums.VerifiedStatus;

/**
 * 会員アカウント作成コマンド。
 */
public record MemberAccountCreateCommand(

        // 会員ID
        Long memberId,

        // 会員アカウントステータス
        Status status,

        // 会員アカウント種別
        MemberAccountType accountType,

        // 会員アカウント値
        String accountValue,

        // 会員アカウントパスワード
        String password,

        // 会員アカウント認証ステータス
        VerifiedStatus verified
) {
}
