package com.manpowergroup.springboot.springboot3web.content.application.dto.request;

import com.manpowergroup.springboot.springboot3web.content.domain.model.ArticleStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "記事一覧検索リクエスト")
public record ArticleQueryRequest(
        @Schema(description = "ページ番号", example = "1") Long pageNum,
        @Schema(description = "1ページあたりの件数", example = "20") Long pageSize,
        @Schema(description = "記事タイトル") String title,
        @Schema(description = "記事状態（0=下書き、1=公開、2=非公開）") ArticleStatus status,
        @Schema(description = "カテゴリID") Long categoryId
) {
}
