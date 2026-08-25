package com.manpowergroup.blog.module.system.application.service;

import com.manpowergroup.blog.shared.api.JoinPageResult;
import com.manpowergroup.blog.module.system.application.command.permission.PermissionCreateCommand;
import com.manpowergroup.blog.module.system.application.command.permission.PermissionUpdateCommand;
import com.manpowergroup.blog.module.system.application.dto.response.permission.PermissionResponse;
import com.manpowergroup.blog.module.system.application.query.permission.PermissionPageQuery;
import com.manpowergroup.blog.module.system.domain.model.permission.UserAuthorities;

import java.util.Collection;
import java.util.List;

/** 権限のユースケースを提供する。 */
public interface PermissionAppService {

    /**
     * ユーザーの実効権限（ロール＋権限）を取得する。
     * 画面制御と API 認可の双方がこの結果から導出される。
     */
    UserAuthorities loadUserAuthorities(Long userId);

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
