package com.manpowergroup.blog.module.content.application.command;

import com.manpowergroup.blog.module.content.domain.model.ArticleStatus;

/** 記事状態変更コマンド。 */
public record ArticleStatusChangeCommand(
        Long id,
        ArticleStatus status
) {
}
