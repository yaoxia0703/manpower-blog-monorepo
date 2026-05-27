package com.manpowergroup.springboot.springboot3web.system.application.dto.request.user;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.AccountType;
import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "ユーザー作成リクエスト")
public record UserCreateRequest(

        @NotBlank(message = "ニックネームは必須です")
        @Size(max = 50, message = "ニックネームは50文字以内で入力してください")
        @Schema(description = "ニックネーム", example = "ユーザーA")
        String nickName,

        @NotNull(message = "ロールIDは必須です")
        @Schema(description = "ロールID", example = "1")
        Long roleId,

        @NotNull(message = "アカウント種別は必須です")
        @Schema(description = "アカウント種別", example = "EMAIL")
        AccountType accountType,

        @NotBlank(message = "メールアドレスは必須です")
        @Email(message = "メールアドレスの形式が正しくありません")
        @Size(max = 100, message = "メールアドレスは100文字以内で入力してください")
        @Schema(description = "アカウントログインID", example = "usera@example.com")
        String accountValue,

        @NotBlank(message = "パスワードは必須です")
        @Size(min = 8, max = 255, message = "パスワードは8文字以上で入力してください")
        @Schema(description = "アカウントパスワード", example = "password123")
        String password,

        @NotNull(message = "ステータスは必須です")
        @Schema(description = "ユーザーステータス（0=無効、1=有効）", example = "1")
        Status status

) {
}