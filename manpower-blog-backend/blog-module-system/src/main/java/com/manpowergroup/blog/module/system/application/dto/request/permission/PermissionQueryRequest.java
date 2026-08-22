package com.manpowergroup.blog.module.system.application.dto.request.permission;

import com.manpowergroup.blog.shared.enums.HttpMethod;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "権限一覧検索リクエスト")
public record PermissionQueryRequest(
        @Schema(description = "権限名または権限制御コードの部分一致キーワード")
        String keyword,

        @Schema(description = "所属メニューID")
        Long menuId,

        @Schema(description = "HTTPメソッド")
        HttpMethod method,

        @Schema(description = "状態（0=無効、1=有効）")
        Integer status
) {
}
