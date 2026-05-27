package com.manpowergroup.springboot.springboot3web.system.application.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "ユーザー削除リクエスト")
public record UserDeleteRequest(

        @NotNull(message = "ユーザーIDは必須です。")
        @Schema(description = "ユーザーID", example = "1")
        Long userId,

        @NotNull(message = "アカウントIDは必須です。")
        @Schema(description = "アカウントID", example = "1")
        Long accountId

//        @NotNull(message = "ロールIDは必須です。")
//        @Schema(description = "ロールID", example = "1")
//        Long roleId

) {
}