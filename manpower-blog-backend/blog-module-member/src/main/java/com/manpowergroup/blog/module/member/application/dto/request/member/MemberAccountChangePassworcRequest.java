package com.manpowergroup.blog.module.member.application.dto.request.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 会員アカウントパスワード変更リクエストDTO。
 */
@Schema(description = "会員アカウントパスワード変更リクエストDTO")
public record MemberAccountChangePassworcRequest(


        @NotBlank(message = "新しいパスワードは必須です。")
        @Size(max = 255, message = "パスワードは255文字以下である必要があります。")
        @Schema(description = "新しいパスワード", example = "newPassword")
        String newPassword,

        @NotBlank(message = "現在のパスワードは必須です。")
        @Size(max = 255, message = "パスワードは255文字以下である必要があります。")
        @Schema(description = "現在のパスワード", example = "currentPassword")
        String currentPassword
) {
}
