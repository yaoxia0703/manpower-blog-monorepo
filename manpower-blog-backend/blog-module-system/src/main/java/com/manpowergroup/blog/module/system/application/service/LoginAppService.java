package com.manpowergroup.blog.module.system.application.service;

import com.manpowergroup.blog.module.system.application.dto.response.auth.LoginUser;
import com.manpowergroup.blog.module.system.application.command.auth.LoginCommand;

/** ログインユースケースを提供する。 */
public interface LoginAppService {

    LoginUser login(LoginCommand command);
}
