package com.manpowergroup.blog.module.member.application.service.member;

import com.manpowergroup.blog.module.member.application.command.account.MemberAccountChangePassworcCommand;
import com.manpowergroup.blog.module.member.application.command.account.MemberAccountCreateCommand;
import com.manpowergroup.blog.shared.enums.Status;

public interface MemberAccountAppService {

    /**
     * アカウントIDで会員のステータスを変更する
     *
     * @param accountId アカウントID
     * @param status    新しいステータス
     */
    void changeStatusByAccountId(Long accountId, Status status);

    /**
     * 会員アカウントを作成する
     * 会員アカウントは会員に紐づくため、会員IDが必要です。
     *
     * @param command 会員アカウント作成コマンド
     * @return 作成された会員アカウントのID
     */
    Long createAccount(MemberAccountCreateCommand command);

    /**
     * 会員アカウントを削除する
     * 会員アカウントを削除するのみで、会員自体は削除されません。
     *
     * @param accountId 削除する会員アカウントのID
     */
    void deleteAccount(Long accountId);


    /**
     * 会員アカウントのパスワードを変更する
     *
     * @param command 会員アカウントパスワード変更コマンド
     */
    void changePassword(MemberAccountChangePassworcCommand command);

}
