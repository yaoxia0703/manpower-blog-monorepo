package com.manpowergroup.blog.module.member.application.service.member;

import com.manpowergroup.blog.module.member.application.command.member.MemberCreateCommand;
import com.manpowergroup.blog.module.member.application.command.member.MemberProfileUpdateCommand;

public interface MemberAppService {

    /**
     * 会員を新規作成する
     *
     * @param command 会員作成コマンド
     * @return 作成された会員のID
     */
    Long create(MemberCreateCommand command);

    /**
     * 会員プロフィールを更新する
     *
     * @param command 会員プロフィール更新コマンド
     */
    void updateProfile(MemberProfileUpdateCommand command);

    /**
     * 会員を削除する
     *
     * @param memberId 会員ID
     */
    void delete(Long memberId);

    void changeStatus();

}
