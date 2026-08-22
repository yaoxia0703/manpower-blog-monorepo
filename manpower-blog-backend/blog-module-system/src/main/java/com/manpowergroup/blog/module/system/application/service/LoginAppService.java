package com.manpowergroup.blog.module.system.application.service;

import com.manpowergroup.blog.shared.dto.LoginUser;
import com.manpowergroup.blog.module.system.application.command.auth.LoginCommand;

/** ログインユースケースを提供する。 */
public interface LoginAppService {

    LoginUser login(LoginCommand command);
}
