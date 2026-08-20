package com.manpowergroup.springboot.springboot3web.content.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "記事情報")
public record ArticleResponse(
        @Schema(description = "記事ID") Long id,
        @Schema(description = "記事タイトル") String title,
        @Schema(description = "記事概要") String summary,
        @Schema(description = "記事本文") String content,
        @Schema(description = "作成者名") String authorName,
        @Schema(description = "カテゴリ名") String categoryName,
        @Schema(description = "作成日時")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Tokyo")
        LocalDateTime createdAt,
        @Schema(description = "更新日時")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Tokyo")
        LocalDateTime updatedAt
) {
}
