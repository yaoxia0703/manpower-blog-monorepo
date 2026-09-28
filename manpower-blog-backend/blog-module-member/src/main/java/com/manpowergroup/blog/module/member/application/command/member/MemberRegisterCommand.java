package com.manpowergroup.blog.module.member.application.command.member;

import com.manpowergroup.blog.module.member.domain.model.member.MemberAccountType;

/**
 * 会員の自己登録コマンド。
 *
 * <p>{@link MemberCreateCommand} と異なり、会員状態と認証済みフラグを持たない。
 * 自己登録の利用者は匿名であり、これらを入力として受け取ると
 * 本人確認を経ずに「認証済み」の会員を作成できてしまうため、
 * 値はユースケース側で固定する。</p>
 *
 * @param accountType  アカウント種別（自己登録可能な種別に限る）
 * @param accountValue ログイン識別子
 * @param password     パスワード（平文）
 * @param displayName  表示名
 */
public record MemberRegisterCommand(
        MemberAccountType accountType,
        String accountValue,
        String password,
        String displayName
) {
}
