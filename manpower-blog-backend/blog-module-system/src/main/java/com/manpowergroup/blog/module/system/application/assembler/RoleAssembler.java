package com.manpowergroup.blog.module.system.application.assembler;

import com.manpowergroup.blog.module.system.application.command.role.RoleAuthorizationSaveCommand;
import com.manpowergroup.blog.module.system.application.command.role.RoleCreateCommand;
import com.manpowergroup.blog.module.system.application.command.role.RoleStatusChangeCommand;
import com.manpowergroup.blog.module.system.application.command.role.RoleUpdateCommand;
import com.manpowergroup.blog.module.system.application.dto.request.role.RoleAuthorizationSaveRequest;
import com.manpowergroup.blog.module.system.application.dto.request.role.RoleCreateRequest;
import com.manpowergroup.blog.module.system.application.dto.request.role.RoleStatusUpdateRequest;
import com.manpowergroup.blog.module.system.application.dto.request.role.RoleUpdateRequest;
import com.manpowergroup.blog.module.system.application.dto.response.role.RoleResponse;
import com.manpowergroup.blog.module.system.domain.model.role.Role;

/** ロールの入出力変換を一元管理する。 */
public final class RoleAssembler {

    private RoleAssembler() {
    }

    public static RoleCreateCommand toCommand(RoleCreateRequest request) {
        return new RoleCreateCommand(request.code(), request.name(), request.sort(), request.status());
    }

    public static RoleUpdateCommand toCommand(Long id, RoleUpdateRequest request) {
        return new RoleUpdateCommand(id, request.code(), request.name(), request.sort(), request.status());
    }

    public static RoleStatusChangeCommand toCommand(Long id, RoleStatusUpdateRequest request) {
        return new RoleStatusChangeCommand(id, request.status());
    }

    public static RoleAuthorizationSaveCommand toCommand(Long roleId, RoleAuthorizationSaveRequest request) {
        return new RoleAuthorizationSaveCommand(
                roleId,
                request.menuIds(),
                request.permissionIds()
        );
    }

    public static RoleResponse toResponse(Role role) {
        return new RoleResponse(
                role.getId(), role.getCode(), role.getName(), role.getSort(), role.getStatus(),
                role.getCreatedAt(), role.getUpdatedAt()
        );
    }
}
