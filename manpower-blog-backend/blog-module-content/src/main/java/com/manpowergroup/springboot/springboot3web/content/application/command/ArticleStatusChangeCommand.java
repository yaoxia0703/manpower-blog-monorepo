package com.manpowergroup.springboot.springboot3web.content.application.command;

import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleStatus;

/** 記事状態変更コマンド。 */
public record ArticleStatusChangeCommand(
        Long id,
        ArticleStatus status
) {
}
