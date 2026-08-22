package com.manpowergroup.blog.module.system.application.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "ロール認可設定保存リクエスト")
public record RoleAuthorizationSaveRequest(

        @Schema(description = "メニューID一覧")
        @NotNull(message = "メニューID一覧は必須です")
        List<Long> menuIds,

        @Schema(description = "権限ID一覧")
        @NotNull(message = "権限ID一覧は必須です")
        List<Long> permissionIds
) {
    public RoleAuthorizationSaveRequest {
        menuIds = menuIds == null ? null : List.copyOf(menuIds);
        permissionIds = permissionIds == null ? null : List.copyOf(permissionIds);
    }
}
