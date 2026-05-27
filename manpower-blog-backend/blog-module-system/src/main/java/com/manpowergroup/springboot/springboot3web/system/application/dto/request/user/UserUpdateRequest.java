package com.manpowergroup.springboot.springboot3web.system.application.dto.request.user;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "ユーザー更新リクエスト")
public record UserUpdateRequest(

        @NotNull(message = "ユーザーIDは必須です。")
        @Schema(description = "ユーザーID", example = "1")
        Long userId,

        @NotNull
        @Schema(description = "アカウントID", example = "1")
        Long accountId,
        @NotBlank(message = "ニックネームは必須です。")
        @Size(max = 50, message = "ニックネームは50文字以内で入力してください")
        @Schema(description = "ニックネーム", example = "ユーザーA")
        String nickName,

        @NotNull(message = "ステータスは必須です。")
        @Schema(description = "ユーザーステータス（0=無効、1=有効）", example = "1")
        Status status,

        @NotNull(message = "ロールIDは必須です。")
        @Schema(description = "ロールID", example = "1")
        Long roleId

) {
}