package com.manpowergroup.blog.module.member.application.command.member;

import com.manpowergroup.blog.module.member.domain.model.member.MemberAccountType;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.enums.VerifiedStatus;

/**
 * 会員作成コマンド。
 *
 * <p>会員番号は集約が自ら採番するため、ここには含めない。</p>
 */
public record MemberCreateCommand(

        Status status,

        MemberAccountType accountType,

        String accountValue,

        String password,

        VerifiedStatus verified,

        String displayName
) {
}
