package com.manpowergroup.blog.module.member.application.dto;

import com.manpowergroup.blog.module.member.domain.model.MemberAccountType;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.shared.enums.VerifiedStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 会員作成リクエストDTO。
 *
 * <p>登録に必要な最小限の項目のみを受け取る。アバターや自己紹介などの任意項目は
 * プロフィール更新で設定する。公開用ユーザー名（handle）は一意制約を持つため、
 * 登録に含めると他会員との重複で登録そのものが失敗し得る。
 * 認証情報の登録と表示情報の設定を分けることで、
 * 登録が失敗する条件をログイン識別子の重複のみに限定する。</p>
 */
@Schema(description = "会員作成リクエストDTO")
public record MemberCreateRequest(

        @NotNull(message = "会員状態は必須です。")
        @Schema(description = "会員状態", example = "ENABLED")
        Status status,

        @NotNull(message = "アカウント種別は必須です。")
        @Schema(description = "アカウント種別", example = "LOCAL_EMAIL")
        MemberAccountType accountType,

        @NotBlank(message = "ログイン識別子は必須です。")
        @Size(min = 8, max = 191, message = "ログイン識別子は8文字以上191文字以下である必要があります。")
        @Schema(description = "ログイン識別子（メール、電話番号または外部認証のユーザーID）",
                example = "john.doe@example.com")
        String accountValue,

        @Size(max = 255, message = "パスワードは255文字以下である必要があります。")
        @Schema(description = "パスワード（外部認証の場合は null）", example = "password")
        String password,

        @NotNull(message = "認証済みフラグは必須です。")
        @Schema(description = "認証済みフラグ", example = "VERIFIED")
        VerifiedStatus verified,

        @NotBlank(message = "表示名は必須です。")
        @Size(max = 50, message = "表示名は50文字以下である必要があります。")
        @Schema(description = "表示名", example = "John Doe")
        String displayName
) {
}
