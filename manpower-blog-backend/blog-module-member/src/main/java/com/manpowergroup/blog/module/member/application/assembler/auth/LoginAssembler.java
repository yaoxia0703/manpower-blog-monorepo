package com.manpowergroup.blog.module.member.application.assembler.auth;

import com.manpowergroup.blog.module.member.application.command.auth.LoginCommand;
import com.manpowergroup.blog.module.member.application.dto.request.auth.LoginRequest;

/** 会員ログイン入力をコマンドへ変換する。 */
public final class LoginAssembler {

    private LoginAssembler() {
    }

    public static LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.accountType(), request.accountValue(), request.password());
    }
}
