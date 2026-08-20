package com.manpowergroup.springboot.springboot3web.system.application.dto.response.menu;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.MenuType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "メニューツリー")
public record MenuTreeResponse(
        @Schema(description = "メニューID") Long id,
        @Schema(description = "親メニューID") Long parentId,
        @Schema(description = "メニュー名称") String name,
        @Schema(description = "Frontend route path") String path,
        @Schema(description = "Frontend component key") String component,
        @Schema(description = "アイコン") String icon,
        @Schema(description = "メニュー種別") MenuType type,
        @Schema(description = "表示順") Integer sort,
        @Schema(description = "状態") Status status,
        @Schema(description = "作成日時") LocalDateTime createdAt,
        @Schema(description = "更新日時") LocalDateTime updatedAt,
        @Schema(description = "子メニュー") List<MenuTreeResponse> children
) {
    public MenuTreeResponse {
        children = children == null ? List.of() : List.copyOf(children);
    }
}
