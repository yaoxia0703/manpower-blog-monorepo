package com.manpowergroup.blog.module.content.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "公開記事一覧検索リクエスト")
public record PublishedArticleQueryRequest(
        @Schema(description = "ページ番号", example = "1") Long pageNum,
        @Schema(description = "1ページあたりの件数", example = "20") Long pageSize,
        @Schema(description = "記事タイトル") String title,
        @Schema(description = "カテゴリID") Long categoryId
) {
}
