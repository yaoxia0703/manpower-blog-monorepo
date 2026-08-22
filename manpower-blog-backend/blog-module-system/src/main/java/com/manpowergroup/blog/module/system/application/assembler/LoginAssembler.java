package com.manpowergroup.blog.module.system.application.assembler;

import com.manpowergroup.blog.shared.dto.LoginRequest;
import com.manpowergroup.blog.module.system.application.command.auth.LoginCommand;

/** ログイン入力をコマンドへ変換する。 */
public final class LoginAssembler {

    private LoginAssembler() {
    }

    public static LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.accountType(), request.accountValue(), request.password());
    }
}
