package com.manpowergroup.springboot.springboot3web.system.application.dto.request.permission;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.HttpMethod;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "権限の更新リクエスト")
public record PermissionUpdateRequest(

        @Schema(description = "所属メニューID（未所属の場合はnull）", example = "6")
        @Positive(message = "所属メニューIDは1以上でなければなりません")
        Long menuId,

        @Schema(description = "権限名", example = "ユーザー管理")
        @NotBlank(message = "権限名は必須です")
        @Size(max = 100, message = "権限名は100文字以内で入力してください")
        String name,

        @Schema(description = "対象APIパス", example = "/api/system/user")
        @NotBlank(message = "対象APIパスは必須です")
        @Size(max = 200, message = "対象パスは200文字以内で入力してください")
        @Pattern(regexp = "^/.*$", message = "対象パスは「/」から始まる形式で入力してください")
        String path,

        @Schema(description = "HTTPメソッド", example = "GET")
        @NotNull(message = "HTTPメソッドは必須です")
        HttpMethod method,

        @Schema(description = "表示順(小さい値ほど前に表示)", example = "1")
        @NotNull(message = "表示順は必須です")
        @Min(value = 0, message = "表示順は0以上でなければなりません")
        Integer sort,

        @Schema(description = "状態(0=無効、1=有効)", example = "1")
        @NotNull(message = "状態は必須です")
        Status status
) {

}
