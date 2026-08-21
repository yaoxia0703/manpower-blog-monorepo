package com.manpowergroup.springboot.springboot3web.system.application.dto.request.user;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "ユーザーステータス変更リクエスト")
public record UserChangeStatusRequest(
        @NotNull(message = "アカウントIDは必須です。")
        @Schema(description = "アカウントID", example = "1")
        Long accountId,
        @NotNull(message = "ステータスは必須です。")
        @Schema(description = "ユーザーステータス（0=無効、1=有効）", example = "1")
        Status status
) {
}
