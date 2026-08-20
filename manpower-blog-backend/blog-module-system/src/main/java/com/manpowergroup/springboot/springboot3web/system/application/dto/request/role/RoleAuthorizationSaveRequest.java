package com.manpowergroup.springboot.springboot3web.system.application.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "ロール認可設定保存リクエスト")
public record RoleAuthorizationSaveRequest(

        @Schema(description = "メニューID一覧")
        @NotNull(message = "メニューID一覧は必須です")
        Long[] menuIds,

        @Schema(description = "権限ID一覧")
        @NotNull(message = "権限ID一覧は必須です")
        Long[] permissionIds
) {
}
