package com.manpowergroup.blog.module.system.application.dto.request.auth;

import com.manpowergroup.blog.shared.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "ログインリクエスト")
public record LoginRequest(
    @NotNull(message = "アカウント種別は必須です")
    @Schema(description = "アカウント種別（EMAIL / PHONE）", example = "EMAIL")
    AccountType accountType,

    @NotBlank(message = "ログイン識別子は必須です")
    @Size(min = 8, max = 100, message = "ログイン識別子は8文字以上100文字以下でなければなりません")
    @Schema(description = "ログイン識別子（メールアドレス／電話番号）", example = "test@example.com")
    String accountValue,

    @NotBlank(message = "パスワードは必須です")
    @Size(min = 8, max = 16, message = "パスワードは8文字以上16文字以下でなければなりません")
    @Schema(description = "パスワード（平文）", example = "Passw0rd!")
    String password
) {
    @Override
    public String toString() {
        return "LoginRequest[accountType=" + accountType
                + ", accountValue=" + accountValue + ", password=***]";
    }
}
