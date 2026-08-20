package com.manpowergroup.springboot.springboot3web.system.domain.repository;

import java.util.Collection;
import java.util.List;

public interface RoleMenuRepository {

    /** 指定メニューがロールに割り当てられているか判定する。 */
    boolean existsByMenuId(Long menuId);

    /** ロールに紐づく有効なメニューIDを取得する。 */
    List<Long> findActiveMenuIds(Long roleId);

    /** ロールのメニュー関連を指定内容へ置き換える。 */
    void replaceMenus(Long roleId, Collection<Long> menuIds);
}
