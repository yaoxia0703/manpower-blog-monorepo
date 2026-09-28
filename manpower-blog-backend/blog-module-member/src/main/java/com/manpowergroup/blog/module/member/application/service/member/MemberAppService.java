package com.manpowergroup.blog.module.member.application.service.member;

import com.manpowergroup.blog.module.member.application.command.member.MemberCreateCommand;
import com.manpowergroup.blog.module.member.application.command.member.MemberProfileUpdateCommand;
import com.manpowergroup.blog.module.member.application.command.member.MemberRegisterCommand;
import com.manpowergroup.blog.shared.enums.Status;

public interface MemberAppService {

    /**
     * 会員を新規作成する
     *
     * <p>会員状態・認証済みフラグ・外部認証種別を呼び出し側が指定できるため、
     * 匿名の利用者からの入力へ直接つないではならない。自己登録には {@link #register} を使う。</p>
     *
     * @param command 会員作成コマンド
     * @return 作成された会員のID
     */
    Long create(MemberCreateCommand command);

    /**
     * 会員が自身で登録する
     *
     * <p>会員状態は有効、認証済みフラグは未認証に固定する。
     * 自己登録できないアカウント種別（外部認証）は拒否する。</p>
     *
     * @param command 自己登録コマンド
     * @return 作成された会員のID
     */
    Long register(MemberRegisterCommand command);

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

    /**
     * 会員のステータスを変更する
     *
     * @param memberId 会員ID
     * @param status   新しいステータス
     */
    void changeStatus(Long memberId, Status status);




}
