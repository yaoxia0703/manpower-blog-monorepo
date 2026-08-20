package com.manpowergroup.springboot.springboot3web.content.domain.model;

import java.time.LocalDateTime;

/** 記事一覧表示用の読み取りモデル。 */
public record ArticleView(
        Long id,
        String title,
        String summary,
        String content,
        String authorName,
        String categoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
