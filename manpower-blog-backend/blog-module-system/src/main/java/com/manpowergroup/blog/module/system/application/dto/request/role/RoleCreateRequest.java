package com.manpowergroup.blog.module.system.application.dto.request.role;

import com.manpowergroup.blog.shared.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "ロール作成リクエスト")
public record RoleCreateRequest(
        @Schema(description = "ロールコード（英数字・一意）", example = "ADMIN")
        @NotBlank(message = "ロールコードは必須です")
        @Size(max = 50, message = "ロールコードは50文字以内で入力してください")
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "ロールコードは英数字とアンダースコアのみ使用できます")
        String code,

        @Schema(description = "ロール名", example = "管理者")
        @NotBlank(message = "ロール名は必須です")
        @Size(max = 100, message = "ロール名は100文字以内で入力してください")
        String name,

        @Schema(description = "表示順", example = "1")
        @NotNull(message = "表示順は必須です")
        @Min(value = 0, message = "表示順は0以上で入力してください")
        Integer sort,

        @Schema(description = "状態（0=無効、1=有効）", example = "1")
        @NotNull(message = "状態は必須です")
        Status status
) {
}
