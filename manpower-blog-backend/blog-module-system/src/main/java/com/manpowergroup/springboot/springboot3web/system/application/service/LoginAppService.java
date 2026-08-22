package com.manpowergroup.springboot.springboot3web.system.application.service;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.LoginUser;
import com.manpowergroup.springboot.springboot3web.system.application.command.auth.LoginCommand;

/** ログインユースケースを提供する。 */
public interface LoginAppService {

    LoginUser login(LoginCommand command);
}
