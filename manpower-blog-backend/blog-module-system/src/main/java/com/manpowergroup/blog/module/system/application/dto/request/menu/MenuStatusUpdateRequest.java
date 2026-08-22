package com.manpowergroup.blog.module.system.application.dto.request.menu;

import com.manpowergroup.blog.shared.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "メニュー状態変更リクエスト")
public record MenuStatusUpdateRequest(

        @Schema(
                description = "状態（0=無効、1=有効）",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "状態は必須です")
        Status status

) {}
