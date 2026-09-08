package com.manpowergroup.blog.module.member.application.service.auth;

import com.manpowergroup.blog.module.member.application.command.auth.LoginCommand;
import com.manpowergroup.blog.module.member.application.dto.response.auth.LoginMember;

public interface MemberLoginAppService {

    /**
     * 会員ログインを行う
     *
     * @param command ログインコマンド
     * @return ログインユーザー情報
     */
    LoginMember login(LoginCommand command);
}
