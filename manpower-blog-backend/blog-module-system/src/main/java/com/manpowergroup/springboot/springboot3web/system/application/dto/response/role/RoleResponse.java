package com.manpowergroup.springboot.springboot3web.system.application.dto.response.role;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "ロール情報")
public record RoleResponse(
        @Schema(description = "ロールID") Long id,
        @Schema(description = "ロールコード") String code,
        @Schema(description = "ロール名") String name,
        @Schema(description = "表示順") Integer sort,
        @Schema(description = "状態") Status status,
        @Schema(description = "作成日時") LocalDateTime createdAt,
        @Schema(description = "更新日時") LocalDateTime updatedAt
) {
}
