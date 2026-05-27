package com.manpowergroup.springboot.springboot3web.system.application.vo.permission;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.PermissionType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "権限詳細VO")
public record PermissionDetailVo(

        @Schema(description = "ID")
        Long id,

        @Schema(description = "親権限ID（0=ルート）")
        Long parentId,

        @Schema(description = "権限名")
        String name,

        @Schema(description = "権限制御コード")
        String code,

        @Schema(description = "権限種別（1=MENU、2=BUTTON、3=API）")
        PermissionType type,

        @Schema(description = "対象パス")
        String path,

        @Schema(description = "HTTPメソッド")
        HttpMethod method,

        @Schema(description = "表示順")
        Integer sort,

        @Schema(description = "状態（0=無効、1=有効）")
        Status status,

        @Schema(description = "作成日時")
        LocalDateTime createdAt,

        @Schema(description = "更新日時")
        LocalDateTime updatedAt

) {}