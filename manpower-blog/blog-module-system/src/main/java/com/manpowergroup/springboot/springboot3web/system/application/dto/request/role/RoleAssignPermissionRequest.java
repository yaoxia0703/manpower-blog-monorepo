package com.manpowergroup.springboot.springboot3web.system.application.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "ロール権限割り当てリクエスト")
public record RoleAssignPermissionRequest(

        @Schema(description = "権限IDリスト", example = "[1, 2, 3]")
        @NotNull(message = "権限IDリストは必須です")
        Long[] permissionIds

) {}