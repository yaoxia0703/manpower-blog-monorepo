package com.manpowergroup.blog.module.content.application.dto.request;

import com.manpowergroup.blog.module.content.domain.model.ArticleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "記事更新リクエスト")
public record ArticleUpdateRequest(
        @NotBlank(message = "記事タイトルは必須です")
        @Size(max = 200, message = "記事タイトルは200文字以内で入力してください")
        @Schema(description = "記事タイトル")
        String title,

        @Size(max = 512, message = "記事概要は512文字以内で入力してください")
        @Schema(description = "記事概要")
        String summary,

        @NotBlank(message = "記事本文は必須です")
        @Schema(description = "記事本文")
        String content,

        @NotNull(message = "カテゴリIDは必須です")
        @Positive(message = "カテゴリIDは1以上でなければなりません")
        @Schema(description = "カテゴリID")
        Long categoryId,

        @NotNull(message = "記事状態は必須です")
        @Schema(description = "記事状態（0=下書き、1=公開、2=非公開）")
        ArticleStatus status
) {
}
