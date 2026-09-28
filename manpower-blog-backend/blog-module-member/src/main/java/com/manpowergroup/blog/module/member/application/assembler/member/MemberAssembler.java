package com.manpowergroup.blog.module.member.application.assembler.member;

import com.manpowergroup.blog.module.member.application.command.account.MemberAccountChangePassworcCommand;
import com.manpowergroup.blog.module.member.application.command.member.MemberCreateCommand;
import com.manpowergroup.blog.module.member.application.command.member.MemberProfileUpdateCommand;
import com.manpowergroup.blog.module.member.application.command.member.MemberRegisterCommand;
import com.manpowergroup.blog.module.member.application.dto.request.member.MemberAccountChangePassworcRequest;
import com.manpowergroup.blog.module.member.application.dto.request.member.MemberCreateRequest;
import com.manpowergroup.blog.module.member.application.dto.request.member.MemberProfileUpdateRequest;
import com.manpowergroup.blog.module.member.application.dto.request.member.MemberRegisterRequest;

/**
 * 会員アセンブラ
 */
public final class MemberAssembler {

    private MemberAssembler() {
    }


    /**
     * 会員作成リクエストDTOを会員作成コマンドに変換する
     *
     * @param request 会員作成リクエストDTO
     * @return 会員作成コマンド
     */
    public static MemberCreateCommand toMemberCreateCommand(MemberCreateRequest request) {
        return new MemberCreateCommand(
                request.status(),
                request.accountType(),
                request.accountValue(),
                request.password(),
                request.verified(),
                request.displayName()
        );
    }

    /**
     * 会員自己登録リクエストDTOを自己登録コマンドに変換する
     *
     * @param request 会員自己登録リクエストDTO
     * @return 自己登録コマンド
     */
    public static MemberRegisterCommand toMemberRegisterCommand(MemberRegisterRequest request) {
        return new MemberRegisterCommand(
                request.accountType(),
                request.accountValue(),
                request.password(),
                request.displayName()
        );
    }

    /**
     * 会員プロフィール更新リクエストDTOを会員プロフィール更新コマンドに変換する
     *
     * @param request 会員プロフィール更新リクエストDTO
     * @return 会員プロフィール更新コマンド
     */
    public static MemberProfileUpdateCommand toMemberProfileUpdateCommand(MemberProfileUpdateRequest request) {
        return new MemberProfileUpdateCommand(
                request.memberId(),
                request.displayName(),
                request.handle(),
                request.avatarUrl(),
                request.bio(),
                request.websiteUrl(),
                request.locale(),
                request.timezone()
        );
    }

    /**
     * 会員アカウントパスワード変更リクエストDTOを会員アカウントパスワード変更コマンドに変換する
     *
     * @param accountId 会員アカウントID
     * @param request   会員アカウントパスワード変更リクエストDTO
     * @return 会員アカウントパスワード変更コマンド
     */
    public static MemberAccountChangePassworcCommand toMemberAccountChangePassworcCommand(Long accountId, MemberAccountChangePassworcRequest request) {
        return new MemberAccountChangePassworcCommand(
                accountId,
                request.newPassword(),
                request.currentPassword()
        );
    }
}
