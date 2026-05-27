package com.manpowergroup.springboot.springboot3web.system.application.vo.permission;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "権限オプションVO")
public record PermissionOptionVo(
        @Schema(description = "ID")
        Long id,

        @Schema(description = "権限名")
        String name

) {
}
