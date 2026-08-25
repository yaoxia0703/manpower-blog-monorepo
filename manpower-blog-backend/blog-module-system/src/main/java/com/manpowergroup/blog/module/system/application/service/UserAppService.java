package com.manpowergroup.blog.module.system.application.service;

import com.manpowergroup.blog.shared.api.PageResult;
import com.manpowergroup.blog.shared.dto.LoginUser;
import com.manpowergroup.blog.module.system.application.command.user.UserCreateCommand;
import com.manpowergroup.blog.module.system.application.command.user.UserDeleteCommand;
import com.manpowergroup.blog.module.system.application.command.user.UserStatusChangeCommand;
import com.manpowergroup.blog.module.system.application.command.user.UserUpdateCommand;
import com.manpowergroup.blog.module.system.application.dto.response.user.UserResponse;
import com.manpowergroup.blog.module.system.application.query.user.UserDetailQuery;
import com.manpowergroup.blog.module.system.application.query.user.UserPageQuery;

/** ユーザーのユースケースを提供する。 */
public interface UserAppService {

    LoginUser getCurrentUserContext(Long userId, Long accountId);

    PageResult<UserResponse> page(UserPageQuery query);

    Long create(UserCreateCommand command);

    void update(UserUpdateCommand command);

    void delete(UserDeleteCommand command);

    void changeStatus(UserStatusChangeCommand command);

    UserResponse findById(UserDetailQuery query);
}
