package com.manpowergroup.springboot.springboot3web.system.application.vo.menu;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.MenuType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "メニュー詳細")
public record MenuDetailVo(

        @Schema(description = "ID")
        Long id,

        @Schema(description = "親ID")
        Long parentId,

        @Schema(description = "関連するPermissionのID")
        Long permissionId,

        @Schema(description = "メニュー名")
        String name,

        @Schema(description = "メニュー種別")
        MenuType type,

        @Schema(description = "表示順")
        Integer sort,

        @Schema(description = "アイコン")
        String icon,

        @Schema(description = "状態")
        Status status


) {}