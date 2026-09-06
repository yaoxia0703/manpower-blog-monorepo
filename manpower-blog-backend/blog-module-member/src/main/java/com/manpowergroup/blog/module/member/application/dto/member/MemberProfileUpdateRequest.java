package com.manpowergroup.blog.module.member.application.dto.member;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

@Schema(description = "会員プロフィール更新リクエストDTO")
public record MemberProfileUpdateRequest(


        @NotNull(message = "会員IDは必須です。")
        @Schema(description = "会員ID", example = "1")
        Long memberId,

        @NotBlank(message = "表示名は必須です。")
        @Size(max = 50, message = "表示名は50文字以下である必要があります。")
        @Schema(description = "表示名", example = "John Doe")
        String displayName,

        @Size(max = 50, message = "公開用ユーザー名（ハンドル）は50文字以下である必要があります。")
        @Schema(description = "公開用ユーザー名（ハンドル）", example = "johndoe")
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
