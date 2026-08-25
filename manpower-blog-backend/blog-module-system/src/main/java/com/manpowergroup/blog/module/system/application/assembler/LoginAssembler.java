package com.manpowergroup.blog.module.system.application.assembler;

import com.manpowergroup.blog.module.system.application.command.auth.LoginCommand;
import com.manpowergroup.blog.module.system.application.dto.request.auth.LoginRequest;

/** ログイン入力をコマンドへ変換する。 */
public final class LoginAssembler {

    private LoginAssembler() {
    }

    public static LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.accountType(), request.accountValue(), request.password());
    }
}
