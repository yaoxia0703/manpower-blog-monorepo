package com.manpowergroup.blog.module.system.application.assembler;

import com.manpowergroup.blog.shared.dto.PageRequest;
import com.manpowergroup.blog.module.system.application.command.user.UserCreateCommand;
import com.manpowergroup.blog.module.system.application.command.user.UserDeleteCommand;
import com.manpowergroup.blog.module.system.application.command.user.UserStatusChangeCommand;
import com.manpowergroup.blog.module.system.application.command.user.UserUpdateCommand;
import com.manpowergroup.blog.module.system.application.dto.request.user.UserChangeStatusRequest;
import com.manpowergroup.blog.module.system.application.dto.request.user.UserCreateRequest;
import com.manpowergroup.blog.module.system.application.dto.request.user.UserQueryRequest;
import com.manpowergroup.blog.module.system.application.dto.request.user.UserUpdateRequest;
import com.manpowergroup.blog.module.system.application.dto.response.user.UserResponse;
import com.manpowergroup.blog.module.system.application.query.user.UserDetailQuery;
import com.manpowergroup.blog.module.system.application.query.user.UserPageQuery;
import com.manpowergroup.blog.module.system.domain.model.user.UserView;

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

    public static UserResponse toResponse(UserView profile) {
        return new UserResponse(
                profile.userId(), profile.accountId(), profile.nickName(), profile.userStatus(),
                profile.accountType(), profile.accountValue(), profile.accountStatus(),
                profile.verifiedStatus(), profile.createdAt(), profile.roleId(), profile.roleName()
        );
    }
}
