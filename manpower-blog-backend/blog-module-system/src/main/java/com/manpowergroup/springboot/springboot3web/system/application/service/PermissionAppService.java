package com.manpowergroup.springboot.springboot3web.system.application.service;

import com.manpowergroup.springboot.springboot3web.blog.common.dto.JoinPageResult;
import com.manpowergroup.springboot.springboot3web.system.application.command.permission.PermissionCreateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.command.permission.PermissionUpdateCommand;
import com.manpowergroup.springboot.springboot3web.system.application.dto.response.permission.PermissionResponse;
import com.manpowergroup.springboot.springboot3web.system.application.query.permission.PermissionPageQuery;

import java.util.Collection;
import java.util.List;

/**
 * API権限のユースケースを提供する。
 */
public interface PermissionAppService {

    /** 指定ユーザーに紐づく権限コード一覧を取得する。 */
    List<String> listPermissionCodesByUserId(Long userId);

    /** 指定ユーザーに紐づくロールコード一覧を取得する。 */
    List<String> listRoleCodesByUserId(Long userId);

    /** 権限一覧をページ単位で取得する。 */
    JoinPageResult<PermissionResponse> page(PermissionPageQuery query);

    /** ロール割当などの選択肢用に全権限を取得する。 */
    List<PermissionResponse> list();

    /** 権限詳細を取得する。 */
    PermissionResponse findById(Long id);

    /** 権限を作成する。 */
    Long create(PermissionCreateCommand command);

    /** 権限を更新する。 */
    void update(PermissionUpdateCommand command);

    /** 権限を削除する。 */
    void delete(Long id);

    /** 指定された権限IDがすべて存在するか判定する。 */
    boolean allExist(Collection<Long> ids);
}
