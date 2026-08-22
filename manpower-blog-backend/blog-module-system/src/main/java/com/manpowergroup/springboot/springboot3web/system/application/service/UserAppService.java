package com.manpowergroup.springboot.springboot3web.system.application.service;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.blog.common.dto.LoginUser;
import com.manpowergroup.springboot.springboot3web.system.application.command.user.UserCreateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.user.UserDeleteCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.user.UserStatusChangeCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.user.UserUpdateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.user.UserResponse;
import com.manpowergroup.springboot.springboot3web.system.application.query.user.UserDetailQuery;
import com.manpowergroup.springboot.springboot3web.system.application.query.user.UserPageQuery;

/** ユーザーのユースケースを提供する。 */
public interface UserAppService {

    LoginUser getCurrentUserContext(Long userId, Long accountId);

    JoinPageResult<UserResponse> page(UserPageQuery query);

    Long create(UserCreateCommand command);

    void update(UserUpdateCommand command);

    void delete(UserDeleteCommand command);

    void changeStatus(UserStatusChangeCommand command);

    UserResponse findById(UserDetailQuery query);
}
