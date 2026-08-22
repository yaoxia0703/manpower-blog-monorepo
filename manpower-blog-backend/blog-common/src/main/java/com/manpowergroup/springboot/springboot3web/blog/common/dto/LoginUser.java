package com.manpowergroup.springboot.springboot3web.blog.common.dto;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "ログイン成功レスポンス（ユーザー情報）")
public record LoginUser(
    @Schema(description = "ユーザーID（t_sys_user.id）")
    Long userId,

    @Schema(description = "アカウントID（t_sys_user_account.id）")
    Long accountId,

    @Schema(description = "ユーザー氏名（t_sys_user.nick_name）")
    String nickName,

    @Schema(description = "アカウント種別（EMAIL / PHONE）")
    AccountType accountType,

    @Schema(description = "ログイン識別子")
    String accountValue,

    @Schema(description = "ロール一覧")
    List<String> roleNames,

    @Schema(description = "権限一覧")
    List<String> permissions
) {
    public LoginUser {
        roleNames = roleNames == null ? List.of() : List.copyOf(roleNames);
        permissions = permissions == null ? List.of() : List.copyOf(permissions);
    }
}
