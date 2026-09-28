package com.manpowergroup.blog.module.member.application.dto.request.member;

import com.manpowergroup.blog.module.member.domain.model.member.MemberAccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 会員の自己登録リクエストDTO。
 *
 * <p>会員状態と認証済みフラグは受け取らない。登録者は匿名であり、
 * これらを入力に含めると本人確認を経ずに「認証済み」の会員を作成できるため、
 * 値はユースケース側で固定する。</p>
 *
 * <p>アカウント種別は自己登録可能な種別（ローカル認証）のみ受け付ける。
 * 判定はドメインの {@link MemberAccountType#isSelfRegistrable()} が行う。</p>
 */
@Schema(description = "会員自己登録リクエストDTO")
public record MemberRegisterRequest(

        @NotNull(message = "アカウント種別は必須です。")
        @Schema(description = "アカウント種別（LOCAL_EMAIL / LOCAL_PHONE）", example = "LOCAL_EMAIL")
        MemberAccountType accountType,

        @NotBlank(message = "ログイン識別子は必須です。")
        @Size(min = 8, max = 191, message = "ログイン識別子は8文字以上191文字以下である必要があります。")
        @Schema(description = "ログイン識別子（メールアドレスまたは電話番号）", example = "john.doe@example.com")
        String accountValue,

        @NotBlank(message = "パスワードは必須です。")
        @Size(min = 8, max = 100, message = "パスワードは8文字以上100文字以下でなければなりません。")
        @Schema(description = "パスワード（平文）", example = "Passw0rd!")
        String password,

        @NotBlank(message = "表示名は必須です。")
        @Size(max = 50, message = "表示名は50文字以下である必要があります。")
        @Schema(description = "表示名", example = "John Doe")
        String displayName
) {
}
