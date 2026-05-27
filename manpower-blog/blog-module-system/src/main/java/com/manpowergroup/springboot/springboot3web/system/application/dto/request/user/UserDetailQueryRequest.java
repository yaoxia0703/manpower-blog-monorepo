package com.manpowergroup.springboot.springboot3web.system.application.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "ユーザー詳細情報取得リクエスト")
public record UserDetailQueryRequest(
        @NotNull(message = "ユーザーIDは必須です")
        @Schema(description = "ユーザーID", example = "1")
        Long userId,
        @Schema(description = "アカウントID", example = "1")
        Long accountId
) {
}
