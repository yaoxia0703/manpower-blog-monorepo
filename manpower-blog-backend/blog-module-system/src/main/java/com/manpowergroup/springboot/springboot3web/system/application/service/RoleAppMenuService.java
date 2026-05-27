package com.manpowergroup.springboot.springboot3web.system.application.service;

import com.manpowergroup.springboot.springboot3web.system.domain.model.role.RoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * ロールメニュー関連テーブル サービス実装クラス
 * </p>
 *
 * @author YAOXIA
 * @since 2026-03-01
 */
public interface RoleAppMenuService extends IService<RoleMenu> {

    /**
     * ロールに紐づく有効なメニューIDリストを取得する
     *
     * @param roleId ロールID
     * @return メニューIDリスト
     */
    List<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * ロールに紐づくメニューを保存または更新する
     *
     * @param roleId  ロールID
     * @param menuIds メニューID配列
     */
    void saveOrUpdate(Long roleId, Long[] menuIds);

}
