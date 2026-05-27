package com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "メニューの更新リクエスト（名称・表示順・アイコン・状態のみ変更可）")
public record MenuUpdateRequest(

        @Schema(description = "メニュー名", example = "ユーザー管理")
        @NotBlank(message = "メニュー名は必須です")
        @Size(max = 100, message = "メニュー名は100文字以内で入力してください")
        String name,

        @Schema(description = "Frontend route path", example = "/system/user")
        @Size(max = 200, message = "path must be 200 characters or less")
        String path,

        @Schema(description = "Frontend component key", example = "system/user/index")
        @Size(max = 200, message = "component must be 200 characters or less")
        String component,

        @Schema(description = "表示順（小さい値ほど前に表示）", example = "1")
        @NotNull(message = "表示順は必須です")
        @Min(value = 0, message = "表示順は0以上でなければなりません")
        Integer sort,

        @Schema(description = "アイコン（例：el-icon-user）", example = "el-icon-user")
        @Size(max = 100, message = "アイコンは100文字以内で入力してください")
        String icon,

        @Schema(description = "状態（0=無効、1=有効）", example = "1")
        @NotNull(message = "状態は必須です")
        Status status

) {}
