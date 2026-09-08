package com.manpowergroup.blog.module.member.application.dto.request.auth;

import com.manpowergroup.blog.module.member.domain.model.member.MemberAccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "会員ログインリクエストDTO")
public record LoginRequest(

        @NotNull(message = "アカウント種別は必須です。")
        @Schema(description = "ログイン識別子（メール、電話番号または外部認証のユーザーID）", example = "")
        MemberAccountType accountType,

        @NotNull(message = "ログイン識別子は必須です。")
        @Size(min = 8, max = 191, message = "ログイン識別子は8文字以上191文字以下である必要があります。")
        @Schema(description = "ログイン識別子（メール、電話番号または外部認証のユーザーID）", example = "")
        String accountValue,

        @NotBlank(message = "パスワードは必須です")
        @Size(min = 8, max = 100, message = "パスワードは8文字以上100文字以下でなければなりません")
        @Schema(description = "パスワード（平文）", example = "Passw0rd!")
        String password
) {
}
