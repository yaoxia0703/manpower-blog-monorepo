package com.manpowergroup.blog.module.system.application.dto.response.auth;

import com.manpowergroup.blog.shared.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 運用者のログイン結果。
 *
 * <p>system モジュール固有の概念であり、共通モジュールへは置かない。
 * 以前は framework 層の {@code JwtTokenProvider} が本型を直接受け取っていたため
 * {@code shared} へ配置せざるを得なかったが、トークン発行契約が
 * {@code TokenSubject} へ中立化されたことで、その制約は解消された。</p>
 *
 * <p>会員のログイン結果とは共有しない。会員はロールを持たず、
 * アカウント種別の取り得る値も異なるため、共有すると
 * 存在し得ない状態が表現可能になる。</p>
 */
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
    List<String> roleNames
) {
    public LoginUser {
        roleNames = roleNames == null ? List.of() : List.copyOf(roleNames);
    }
}
