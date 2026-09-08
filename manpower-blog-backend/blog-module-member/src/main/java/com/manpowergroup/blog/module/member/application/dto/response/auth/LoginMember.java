package com.manpowergroup.blog.module.member.application.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 会員のログイン結果。
 *
 * <p>プロフィールの全項目は返さない。ログインに必要なのは本人の識別と
 * 画面表示の最小限であり、プロフィールへ項目を追加するたびに
 * ログインの契約が変わる状態を避けるため。
 * 詳細は認証後に会員自身のエンドポイントから取得する。</p>
 *
 * <p>ロールを持たない。会員は権限体系を持たず、会員面の認可は
 * 認証済みか否かのみで判定する。空のロール一覧を返すと
 * 権限体系が存在するかのような誤解を生むため、項目自体を設けない。</p>
 */
@Schema(description = "ログイン成功レスポンス（会員情報）")
public record LoginMember(
        @Schema(description = "会員ID")
        Long memberId,

        @Schema(description = "アカウントID")
        Long accountId,

        @Schema(description = "表示名")
        String displayName,

        @Schema(description = "公開用ユーザー名")
        String handle,

        @Schema(description = "アバターURL")
        String avatarUrl
) {
}
