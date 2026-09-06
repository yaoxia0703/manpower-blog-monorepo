package com.manpowergroup.blog.module.member.application.assembler.member;

import com.manpowergroup.blog.module.member.application.command.member.MemberCreateCommand;
import com.manpowergroup.blog.module.member.application.command.member.MemberProfileUpdateCommand;
import com.manpowergroup.blog.module.member.application.dto.member.MemberCreateRequest;
import com.manpowergroup.blog.module.member.application.dto.member.MemberProfileUpdateRequest;

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
}
