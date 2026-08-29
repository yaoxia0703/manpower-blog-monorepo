package com.manpowergroup.blog.module.member.application.command;

import com.manpowergroup.blog.module.member.domain.model.MemberAccountType;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.enums.VerifiedStatus;

/**
 * 会員作成コマンド
 */
public record MemberCreateCommand(

        Status status,

        MemberAccountType accountType,

        String accountValue,

        String password,

        VerifiedStatus verified,

        String displayName,

        String handle,

        String avatarUrl,

        String bio,

        String websiteUrl,

        String locale,

        String timezone
) {
}
