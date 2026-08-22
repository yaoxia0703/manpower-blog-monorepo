package com.manpowergroup.blog.module.system.application.assembler;

import com.manpowergroup.blog.shared.dto.PageRequest;
import com.manpowergroup.blog.shared.enums.Status;
import com.manpowergroup.blog.module.system.application.command.permission.PermissionCreateCommand;
import com.manpowergroup.blog.module.system.application.command.permission.PermissionUpdateCommand;
import com.manpowergroup.blog.module.system.application.dto.request.permission.PermissionCreateRequest;
import com.manpowergroup.blog.module.system.application.dto.request.permission.PermissionQueryRequest;
import com.manpowergroup.blog.module.system.application.dto.request.permission.PermissionUpdateRequest;
import com.manpowergroup.blog.module.system.application.dto.response.permission.PermissionResponse;
import com.manpowergroup.blog.module.system.application.query.permission.PermissionPageQuery;
import com.manpowergroup.blog.module.system.domain.model.permission.Permission;


/**
 * 権限の入出力変換を一元管理する。
 */
public final class PermissionAssembler {

    private PermissionAssembler() {
    }

    /**
     * 作成リクエストをコマンドへ変換する。
     *
     * @param request 作成リクエスト
     * @return 作成コマンド
     */
    public static PermissionCreateCommand toCommand(PermissionCreateRequest request) {
        return new PermissionCreateCommand(
                request.menuId(), request.name(), request.code(), request.path(),
                request.method(), request.sort(), request.status()
        );
    }

    /**
     * 更新リクエストをコマンドへ変換する。
     *
     * @param id 権限ID
     * @param request 更新リクエスト
     * @return 更新コマンド
     */
    public static PermissionUpdateCommand toCommand(Long id, PermissionUpdateRequest request) {
        return new PermissionUpdateCommand(
                id, request.menuId(), request.name(), request.path(),
                request.method(), request.sort(), request.status()
        );
    }

    public static PermissionPageQuery toQuery(PageRequest pageRequest, PermissionQueryRequest request) {
        return new PermissionPageQuery(
                pageRequest == null ? null : pageRequest.pageNum(),
                pageRequest == null ? null : pageRequest.pageSize(),
                request == null ? null : request.keyword(),
                request == null ? null : request.menuId(),
                request == null ? null : request.method(),
                request == null || request.status() == null
                        ? null
                        : Status.fromJson(request.status())
        );
    }

    /**
     * 権限エンティティをレスポンスへ変換する。
     *
     * @param permission 権限
     * @param menuName 所属メニュー名
     * @return 権限レスポンス
     */
    public static PermissionResponse toResponse(Permission permission, String menuName) {
        return new PermissionResponse(
                permission.getId(), permission.getMenuId(), menuName,
                permission.getName(), permission.getCode(), permission.getPath(),
                permission.getMethod(), permission.getSort(), permission.getStatus(),
                permission.getCreatedAt(), permission.getUpdatedAt()
        );
    }

}
