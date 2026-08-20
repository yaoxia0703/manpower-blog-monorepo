package com.manpowergroup.springboot.springboot3web.system.domain.repository;

import com.manpowergroup.springboot.springboot3web.framework.security.authority.ApiPermission;

import java.util.List;

public interface PermissionRepository {

    /**
     * 指定ユーザーに紐づく権限コード一覧を取得する
     */
    List<String> selectPermissionCodesByUserId(Long userId);

    List<String> selectRoleCodesByUserId(Long userId);

    List<ApiPermission> selectEnabledApiPermissions();

}
