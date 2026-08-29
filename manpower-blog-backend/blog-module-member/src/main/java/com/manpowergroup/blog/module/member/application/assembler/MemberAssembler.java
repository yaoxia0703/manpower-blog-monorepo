package com.manpowergroup.blog.module.member.application.assembler;

import com.manpowergroup.blog.module.member.application.command.MemberCreateCommand;
import com.manpowergroup.blog.module.member.application.dto.MemberCreateRequest;

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
}
