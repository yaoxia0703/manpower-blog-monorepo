package com.manpowergroup.springboot.springboot3web.system.domain.repository;

import com.manpowergroup.springboot.springboot3web.blog.common.enums.Status;
import com.manpowergroup.springboot.springboot3web.framework.security.authority.ApiPermission;

import java.util.List;

public interface PermissionRepository {

    /**
     * 指定ユーザーに紐づく権限コード一覧を取得する
     */
    List<String> selectPermissionCodesByUserId(Long userId);

    List<ApiPermission> selectApiPermissionsByUserId(Long userId);

    /**
     * 全子孫IDを再帰的に取得する
     */
    List<Long> selectAllDescendantIds(Long parentId);

    /**
     * 指定IDリストのステータスを一括更新する
     */
    void updateStatusBatch(List<Long> ids, Status status);
}
