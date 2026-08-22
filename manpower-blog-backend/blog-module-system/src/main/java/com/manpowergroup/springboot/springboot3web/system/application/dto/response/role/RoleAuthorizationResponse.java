package com.manpowergroup.springboot.springboot3web.system.application.dto.response.role;

import com.manpowergroup.springboot.springboot3web.system.application.dto.response.menu.MenuTreeResponse;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.permission.PermissionResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "ロール認可設定")
public record RoleAuthorizationResponse(
        List<MenuTreeResponse> menus,
        List<PermissionResponse> permissions,
        List<Long> selectedMenuIds,
        List<Long> selectedPermissionIds
) {
}
