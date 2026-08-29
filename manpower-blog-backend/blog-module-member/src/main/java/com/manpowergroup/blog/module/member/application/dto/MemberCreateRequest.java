package com.manpowergroup.blog.module.member.application.dto;

import com.manpowergroup.blog.module.member.domain.model.MemberAccountType;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.enums.VerifiedStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "会員作成リクエストDTO")
public record MemberCreateRequest(

        @NotNull(message = "会員状態は必須です。")
        @Schema(description = "会員状態", example = "ENABLED")
        Status status,

        @NotNull(message = "アカウント種別は必須です。")
        @Schema(description = "アカウント種別", example = "EMAIL")
        MemberAccountType accountType,

        @NotBlank(message = "ログイン識別子は必須です。")
        @Size(min = 8, max = 255, message = "ログイン識別子は8文字以上191文字以下である必要があります。")
        @Schema(description = "ログイン識別子（メール、電話番号または外部認証のユーザーID）", example = "122")
        String accountValue,

        @Size(max = 255, message = "パスワードは255文字以下である必要があります。")
        @Schema(description = "パスワードハッシュ（外部認証の場合は null）", example = "password")
        String password,

        @NotNull(message = "認証済みフラグは必須です。")
        @Schema(description = "認証済みフラグ", example = "VERIFIED")
        VerifiedStatus verified,

        @Size(max = 50, message = "表示名は50文字以下である必要があります。")
        @NotBlank(message = "表示名は必須です。")
        @Schema(description = "表示名", example = "John Doe")
        String displayName,

        @Size(max = 50, message = "ハンドルは50文字以下である必要があります。")
        @Schema(description = "ハンドル", example = "johndoe")
        String handle,

        @Size(max = 500, message = "アバターURLは500文字以下である必要があります。")
        @Schema(description = "アバターURL", example = "https://example.com/avatar.jpg")
        String avatarUrl,

        @Size(max = 500, message = "自己紹介は500文字以下である必要があります。")
        @Schema(description = "自己紹介", example = "Hello, I'm John Doe.")
        String bio,

        @Size(max = 500, message = "WebサイトURLは500文字以下である必要があります。")
        @Schema(description = "WebサイトURL", example = "https://example.com")
        String websiteUrl,

        @Size(max = 10, message = "言語設定は10文字以下である必要があります。")
        @Schema(description = "言語設定", example = "en-US")
        String locale,

        @Size(max = 50, message = "タイムゾーンは50文字以下である必要があります。")
        @Schema(description = "タイムゾーン", example = "America/New_York")
        String timezone
) {
}
