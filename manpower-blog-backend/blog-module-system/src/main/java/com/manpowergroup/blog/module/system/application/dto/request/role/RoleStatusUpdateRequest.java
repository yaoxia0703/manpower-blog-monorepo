package com.manpowergroup.blog.module.system.application.dto.request.role;

import com.manpowergroup.blog.shared.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "ロール状態変更リクエスト")
public record RoleStatusUpdateRequest(
        @Schema(description = "状態（0=無効、1=有効）", example = "1")
        @NotNull(message = "状態は必須です")
        Status status
) {}
