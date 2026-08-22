package com.manpowergroup.blog.module.system.application.dto.response.menu;

import com.manpowergroup.blog.shared.enums.MenuType;
import com.manpowergroup.blog.shared.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "メニュー詳細")
public record MenuDetailResponse(
        @Schema(description = "ID") Long id,
        @Schema(description = "親ID") Long parentId,
        @Schema(description = "メニュー名") String name,
        @Schema(description = "フロントエンドのルートパス") String path,
        @Schema(description = "フロントエンドのコンポーネントキー") String component,
        @Schema(description = "メニュー種別") MenuType type,
        @Schema(description = "表示順") Integer sort,
        @Schema(description = "アイコン") String icon,
        @Schema(description = "状態") Status status
) {
}
