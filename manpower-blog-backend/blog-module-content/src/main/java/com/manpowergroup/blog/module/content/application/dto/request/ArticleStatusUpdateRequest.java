package com.manpowergroup.blog.module.content.application.dto.request;

import com.manpowergroup.blog.module.content.domain.model.ArticleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "記事状態変更リクエスト")
public record ArticleStatusUpdateRequest(
        @NotNull(message = "記事状態は必須です")
        @Schema(description = "記事状態（0=下書き、1=公開、2=非公開）", example = "1")
        ArticleStatus status
) {
}
