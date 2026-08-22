package com.manpowergroup.springboot.springboot3web.system.application.assembler;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.LoginRequest;
import com.manpowergroup.springboot.springboot3web.system.application.command.auth.LoginCommand;

/** ログイン入力をコマンドへ変換する。 */
public final class LoginAssembler {

    private LoginAssembler() {
    }

    public static LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.accountType(), request.accountValue(), request.password());
    }
}
