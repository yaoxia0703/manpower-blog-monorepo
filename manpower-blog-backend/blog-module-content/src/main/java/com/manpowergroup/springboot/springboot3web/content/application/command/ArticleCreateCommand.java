package com.manpowergroup.springboot.springboot3web.content.application.command;

import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleStatus;

/** 記事作成コマンド。 */
public record ArticleCreateCommand(
        // 記事タイトル
        String title,
        // 記事概要
        String summary,
        // 記事本文
        String content,
        // カテゴリID
        Long categoryId,
        // 作成者ID
        Long authorId,
        // 記事状態
        ArticleStatus status
) {
}
