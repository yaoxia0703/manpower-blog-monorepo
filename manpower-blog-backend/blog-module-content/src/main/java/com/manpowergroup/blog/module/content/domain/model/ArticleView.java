package com.manpowergroup.blog.module.content.domain.model;

import java.time.LocalDateTime;

/** 記事一覧表示用の読み取りモデル。 */
public record ArticleView(
        Long id,
        String title,
        String summary,
        String content,
        Long categoryId,
        String categoryName,
        Long authorId,
        String authorName,
        ArticleStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
