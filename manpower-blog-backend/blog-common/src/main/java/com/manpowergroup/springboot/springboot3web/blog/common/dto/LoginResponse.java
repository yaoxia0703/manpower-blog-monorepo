package com.manpowergroup.springboot.springboot3web.blog.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "ログイン成功レスポンス（JWT）")
public record LoginResponse<T>(
    @Schema(description = "アクセストークン（Bearer）")
    String accessToken,

    @Schema(description = "ログインユーザー情報（クライアント別）")
    T user
) {
}
