package com.manpowergroup.blog.module.content.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.manpowergroup.blog.module.content.domain.model.ArticleStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "記事情報")
public record ArticleResponse(
        @Schema(description = "記事ID") Long id,
        @Schema(description = "記事タイトル") String title,
        @Schema(description = "記事概要") String summary,
        @Schema(description = "記事本文") String content,
        @Schema(description = "カテゴリID") Long categoryId,
        @Schema(description = "カテゴリ名") String categoryName,
        @Schema(description = "作成者ID") Long authorId,
        @Schema(description = "作成者名") String authorName,
        @Schema(description = "記事状態") ArticleStatus status,
        @Schema(description = "作成日時")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Tokyo")
        LocalDateTime createdAt,
        @Schema(description = "更新日時")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Tokyo")
        LocalDateTime updatedAt
) {
}
