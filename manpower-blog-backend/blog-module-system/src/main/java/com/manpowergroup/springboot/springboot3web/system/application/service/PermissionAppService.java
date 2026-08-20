package com.manpowergroup.springboot.springboot3web.system.application.service;

import com.manpowergroup.springboot.springboot3web.system.application.dto.request.permission.PermissionCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.permission.PermissionUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.vo.permission.PermissionVo;
import com.manpowergroup.springboot.springboot3web.system.domain.model.permission.Permission;
import com.baomidou.mybatisplus.extension.service.IService;
import com.manpowergroup.springboot.springboot3web.framework.security.authority.ApiPermission;

import java.util.List;

/**
 * <p>
 * API権限マスタサービス。
 * </p>
 *
 * @author YAOXIA
 * @since 2025-12-18
 */
public interface PermissionAppService extends IService<Permission> {

    /**
     * ユーザーIDに紐づく権限コード一覧を取得する
     *
     * @param userId ユーザーID
     * @return 権限コード一覧
     */
    List<String> selectPermissionCodesByUserId(Long userId);

    List<String> selectRoleCodesByUserId(Long userId);

    List<ApiPermission> selectEnabledApiPermissions();


    List<PermissionVo> getPermissionList();

    /**
     * 権限IDにより権限情報を取得する
     *
     * @param id 権限ID
     * @return 権限情報
     */
    PermissionVo getPermissionDetail(Long id);

    /**
     * 権限を新規作成する
     *
     * @param request 権限作成リクエスト
     * @return 作成された権限ID
     */
    Long createPermission(PermissionCreateRequest request);

    /**
     * 権限情報を更新する
     *
     * @param id      権限ID
     * @param request 更新内容
     */
    void updatePermission(Long id, PermissionUpdateRequest request);

    /**
     * 権限を削除する
     *
     * @param id 権限ID
     */
    void deletePermission(Long id);

}
