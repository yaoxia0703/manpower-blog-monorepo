package com.manpowergroup.springboot.springboot3web.system.application.dto.response.permission;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "API権限レスポンス")
public record PermissionResponse(
        @Schema(description = "ID")
        Long id,

        @Schema(description = "所属メニューID（未所属の場合はnull）")
        Long menuId,

        @Schema(description = "所属メニュー名（未所属の場合はnull）")
        String menuName,

        @Schema(description = "権限名")
        String name,

        @Schema(description = "権限制御コード")
        String code,

        @Schema(description = "対象パス")
        String path,

        @Schema(description = "HTTPメソッド")
        HttpMethod method,

        @Schema(description = "ソート順")
        Integer sort,

        @Schema(description = "権限ステータス")
        Status status,

        @Schema(description = "権限作成日時")
        LocalDateTime createdAt,

        @Schema(description = "権限更新日時")
        LocalDateTime updatedAt
) {
}
