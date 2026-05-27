package com.manpowergroup.springboot.springboot3web.system.application.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "ロールメニュー割り当てリクエスト")
public record RoleAssignMenuRequest(

        @Schema(description = "メニューIDリスト", example = "[1, 2, 3]")
        @NotNull(message = "メニューIDリストは必須です")
        Long[] menuIds

) {}