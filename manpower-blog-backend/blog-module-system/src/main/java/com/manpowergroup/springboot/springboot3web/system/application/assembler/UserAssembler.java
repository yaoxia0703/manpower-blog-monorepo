package com.manpowergroup.springboot.springboot3web.system.application.assembler;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.PageRequest;
import com.manpowergroup.springboot.springboot3web.system.application.command.user.UserCreateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.user.UserDeleteCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.user.UserStatusChangeCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.user.UserUpdateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserChangeStatusRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserQueryRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.user.UserUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.user.UserResponse;
import com.manpowergroup.springboot.springboot3web.system.application.query.user.UserDetailQuery;
import com.manpowergroup.springboot.springboot3web.system.application.query.user.UserPageQuery;
import com.manpowergroup.springboot.springboot3web.system.domain.model.user.UserProfile;

/** ユーザーの入出力変換を一元管理する。 */
public final class UserAssembler {

    private UserAssembler() {
    }

    public static UserCreateCommand toCommand(UserCreateRequest request) {
        return new UserCreateCommand(
                request.nickName(), request.roleId(), request.accountType(), request.accountValue(),
                request.password(), request.status()
        );
    }

    public static UserUpdateCommand toCommand(Long userId, UserUpdateRequest request) {
        return new UserUpdateCommand(
                userId, request.accountId(), request.nickName(), request.status(), request.roleId()
        );
    }

    public static UserStatusChangeCommand toCommand(Long userId, UserChangeStatusRequest request) {
        return new UserStatusChangeCommand(userId, request.accountId(), request.status());
    }

    public static UserDeleteCommand toDeleteCommand(Long userId, Long accountId) {
        return new UserDeleteCommand(userId, accountId);
    }

    public static UserPageQuery toQuery(PageRequest pageRequest, UserQueryRequest request) {
        return new UserPageQuery(
                pageRequest == null ? null : pageRequest.pageNum(),
                pageRequest == null ? null : pageRequest.pageSize(),
                request == null ? null : request.keyword(),
                request == null ? null : request.status()
        );
    }

    public static UserDetailQuery toDetailQuery(Long userId, Long accountId) {
        return new UserDetailQuery(userId, accountId);
    }

    public static UserResponse toResponse(UserProfile profile) {
        return new UserResponse(
                profile.userId(), profile.accountId(), profile.nickName(), profile.userStatus(),
                profile.accountType(), profile.accountValue(), profile.accountStatus(),
                profile.verifiedStatus(), profile.createdAt(), profile.roleId(), profile.roleName()
        );
    }
}
