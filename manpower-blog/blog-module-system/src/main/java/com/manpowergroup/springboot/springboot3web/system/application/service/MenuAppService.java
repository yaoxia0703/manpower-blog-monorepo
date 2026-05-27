package com.manpowergroup.springboot.springboot3web.system.application.service;

import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuCreateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuStatusUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.dto.request.menu.MenuUpdateRequest;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuDetailVo;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuOptionVo;
import com.manpowergroup.springboot.springboot3web.system.application.vo.menu.MenuTreeVo;
import com.manpowergroup.springboot.springboot3web.system.domain.model.menu.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * システムメニュー管理テーブルサービス実装クラス
 * </p>
 *
 * @author YAOXIA
 * @since 2026-03-01
 */
public interface MenuAppService extends IService<Menu> {

    /**
     * メニューをTree構造で取得する
     *
     * @return メニューのTree構造のリスト
     */
    List<MenuTreeVo> getAllMenuTree();

    /**
     * ユーザIDに基づいてメニューをTree構造で取得する
     *
     * @param userId ユーザID
     * @return メニューのTree構造のリスト
     */
    List<MenuTreeVo> selectMenusByUserId(Long userId);


    /**
     * メニューのオプションリストを取得する
     *
     * @return メニューのオプションリスト
     */
    List<MenuOptionVo> getMenuOptions();

    /**
     * メニューIDに基づいてメニューの詳細情報を取得する
     *
     * @param id メニューID
     * @return メニューの詳細情報
     */
    MenuDetailVo getMenuDetail(Long id);

    /**
     * メニューを新規作成する
     *
     * @param request メニュー情報
     * @return 作成されたメニューID
     */
    Long createMenu(MenuCreateRequest request);

    /**
     * メニュー情報を更新する
     *
     * @param id      メニューID
     * @param request 更新内容
     */
    void updateMenu(Long id, MenuUpdateRequest request);

    /**
     * メニューを削除する
     *
     * @param id メニューID
     */
    void deleteMenu(Long id);

    /**
     * メニューの状態を変更する
     *
     * @param id     メニューID
     * @param status 新しい状態
     */
    void changeMenuStatus(Long id, MenuStatusUpdateRequest status);


    /**
     * 有効なメニューツリー取得（ロールメニュー設定用）
     */
    List<MenuTreeVo> getActiveMenuTree();
}
