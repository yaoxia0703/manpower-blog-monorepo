package com.manpowergroup.springboot.springboot3web.system.application.vo.role;

import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuTreeVo;
import com.manpowergroup.springboot.springboot3web.system.application.vo.permission.PermissionVo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "ロール認可設定")
public record RoleAuthorizationVo(
        List<MenuTreeVo> menus,
        List<PermissionVo> permissions,
        List<Long> selectedMenuIds,
        List<Long> selectedPermissionIds
) {
}
