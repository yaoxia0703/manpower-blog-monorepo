package com.manpowergroup.springboot.springboot3web.system.application.service;

import com.manpowergroup.springboot.springboot3web.system.application.command.permission.PermissionCreateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.permission.PermissionUpdateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.permission.PermissionResponse;

import java.util.Collection;
import java.util.List;

/**
 * API権限のユースケースを提供する。
 */
public interface PermissionAppService {

    /** 指定ユーザーに紐づく権限コード一覧を取得する。 */
    List<String> selectPermissionCodesByUserId(Long userId);

    /** 指定ユーザーに紐づくロールコード一覧を取得する。 */
    List<String> selectRoleCodesByUserId(Long userId);

    /** 権限一覧を取得する。 */
    List<PermissionResponse> getPermissionList();

    /** 権限詳細を取得する。 */
    PermissionResponse getPermissionDetail(Long id);

    /** 権限を作成する。 */
    Long createPermission(PermissionCreateCommand command);

    /** 権限を更新する。 */
    void updatePermission(PermissionUpdateCommand command);

    /** 権限を削除する。 */
    void deletePermission(Long id);

    /** 指定された権限IDがすべて存在するか判定する。 */
    boolean allExist(Collection<Long> ids);
}
