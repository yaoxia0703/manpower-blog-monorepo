package com.manpowergroup.springboot.springboot3web.system.application.dto.request.user;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "ユーザー一覧検索リクエスト")
public record UserQueryRequest(
        @Schema(
                description = "検索キーワード（ユーザー名またはアカウントに対する部分一致）",
                example = "ADMIN"
        )
        String keyword,

        @Schema(
                description = "状態（0=無効、1=有効）",
                example = "1"
        )
        Status status
) {
}
