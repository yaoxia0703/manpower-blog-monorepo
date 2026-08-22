package com.manpowergroup.blog.module.content.application.command;

import com.manpowergroup.blog.module.content.domain.model.ArticleStatus;

/** 記事更新コマンド。 */
public record ArticleUpdateCommand(
        // 記事ID
        Long id,
        // 記事タイトル
        String title,
        // 記事概要
        String summary,
        // 記事本文
        String content,
        // カテゴリID
        Long categoryId,
        // 記事状態
        ArticleStatus status
) {
}
