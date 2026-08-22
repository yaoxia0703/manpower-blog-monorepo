package com.manpowergroup.blog.module.system.application.dto.response.menu;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "メニュー選択肢")
public record MenuOptionResponse(
        @Schema(description = "ID") Long id,
        @Schema(description = "メニュー名") String name
) {
}
