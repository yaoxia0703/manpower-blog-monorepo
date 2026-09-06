package com.manpowergroup.blog.module.member.application.command.member;

/**
 * 会員プロフィール更新コマンド
 */
public record MemberProfileUpdateCommand(
        Long memberId,

        String displayName,

        String handle,

        String avatarUrl,

        String bio,

        String websiteUrl,

        String locale,

        String timezone
) {
}
