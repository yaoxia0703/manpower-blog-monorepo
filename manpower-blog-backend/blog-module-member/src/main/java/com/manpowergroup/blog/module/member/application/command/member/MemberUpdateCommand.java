package com.manpowergroup.blog.module.member.application.command.member;

/**
 * 会員更新コマンド
 */
public record MemberUpdateCommand(
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
